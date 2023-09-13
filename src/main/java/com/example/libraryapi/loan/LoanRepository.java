package com.example.libraryapi.loan;

import com.example.libraryapi.loan.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    Optional<Loan> findLoanByBookId(Long bookId);

    List<Loan> findAllByCustomerId(Long customerId);

    boolean existsByBookIdAndBorrowedSinceLessThanEqualAndBorrowedToGreaterThanEqual(Long bookId, LocalDate borrowedSince,
                                                                                     LocalDate borrowedTo);
}

