package com.example.libraryapi.book;

import com.example.libraryapi.book.model.Book;
import com.example.libraryapi.book.model.command.BorrowBookCommand;
import com.example.libraryapi.book.model.dto.BookDto;
import com.example.libraryapi.customer.CustomerRepository;
import com.example.libraryapi.customer.model.Customer;
import com.example.libraryapi.loan.LoanRepository;
import com.example.libraryapi.loan.model.Loan;
import com.example.libraryapi.loan.model.dto.LoanDto;
import com.example.libraryapi.mapper.GeneralMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.text.MessageFormat;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final GeneralMapper generalMapper;
    private final CustomerRepository customerRepository;
    private final LoanRepository loanRepository;


    public BookDto save(Book book) {
        Book saved = bookRepository.save(book);//tutaj zwracałeś uwagę, co jak sie wywali, zastanawiałem się czy chodzi
//        rollback czy obsługę błędów. Miałem handler,który jak się okazało nie działał. Naprawiłem(nie bło adnotacji,
//        że hadnler jest advicem) rollback dla błędów które mogą sie pojawić to wyatarczy chyba z Transactionala,
//        którego finalnie wywaliłem, bo po zmienieniu tylko na zapis miałem wrażenie, że nie jest potrzebny.
        return generalMapper.mapBookToDto(saved);
    }

    @Transactional
    public BookDto lockBook(Long bookId) {
        Book toBeUpdated = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat
                        .format("Book related to id {0} has not been found", bookId)));
        toBeUpdated.setBlocked(true);
        return generalMapper.mapBookToDto(bookRepository.save(toBeUpdated));
    }

    @Transactional
    public LoanDto borrowBook(Long customerId, Long bookId, BorrowBookCommand bookCommand) {
        boolean blocked = true;
        Book toBeUpdated = bookRepository.findBookByIdAndBlockedNot(bookId, blocked)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat
                        .format("Book related to id ={0} has not been found or is blocked ", bookId)));
        Customer customer = customerRepository.findWithLockById(customerId)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat
                        .format("Customer related to id ={0} has not been found ", customerId)));
        Loan loan = loanHandler(bookId, bookCommand);
        loan.setBook(toBeUpdated);
        loan.setCustomer(customer);
        loan.setBorrowedSince(bookCommand.getBorrowedSince());
        loan.setBorrowedTo(bookCommand.getBorrowedTo());
        customer.getBookList().add(toBeUpdated);
        customer.getLoans().add(loan);
        bookRepository.save(toBeUpdated);
        return generalMapper.mapLoanToDto(loanRepository.save(loan));
    }

    public BookDto returnBook(Long loanId) {
        Loan loanToReturn = loanRepository.findById(loanId)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat
                        .format("Loan related to id ={0} has not been found ", loanId)));
        Long bookId = loanToReturn.getBook().getId();
        Long customerId = loanToReturn.getCustomer().getId();
        Book toBeUpdated = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat
                        .format("Book related to id ={0} has not been found ", bookId)));
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat
                        .format("Customer related to id ={0} has not been found ", customerId)));
        customer.getBookList().remove(toBeUpdated);
        customer.getLoans().remove(loanToReturn);
        toBeUpdated.getBookLoans().remove(loanToReturn);
        toBeUpdated.getBookLoans().remove(loanToReturn);
        customerRepository.save(customer);
        loanRepository.delete(loanToReturn);
        return generalMapper.mapBookToDto(bookRepository.save(toBeUpdated));
    }

    public Page<BookDto> findAll(Pageable pageable) {
        Page<Book> bookPage = bookRepository.findAll(pageable);
        return bookPage.map(generalMapper::mapBookToDto);

    }

    public Loan loanHandler(Long bookId, BorrowBookCommand command) {
        Loan loan;
        if (loanRepository.existsByBookIdAndBorrowedSinceLessThanEqualAndBorrowedToGreaterThanEqual(bookId,
                command.getBorrowedSince(),
                command.getBorrowedTo())) {
            throw new RuntimeException("Book is not available in requested period of time");
        }
        loan = loanRepository.findLoanByBookId(bookId)
                .orElse(new Loan());
        return loan;
    }

}
