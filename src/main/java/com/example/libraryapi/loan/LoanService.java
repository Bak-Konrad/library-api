package com.example.libraryapi.loan;

import com.example.libraryapi.book.model.dto.BookDto;
import com.example.libraryapi.loan.model.Loan;
import com.example.libraryapi.mapper.GeneralMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {
    private final LoanRepository loanRepository;
    private final GeneralMapper generalMapper;

    public List<BookDto> getAllBooksForCustomer(Long customerId) {
        List<Loan> bookList = loanRepository.findAllByCustomerId(customerId);
        return bookList
                .stream()
                .map(Loan::getBook)
                .map(generalMapper::mapBookToDto)
                .toList();

    }
}
