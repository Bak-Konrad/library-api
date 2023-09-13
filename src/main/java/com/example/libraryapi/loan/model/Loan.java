package com.example.libraryapi.loan.model;

import com.example.libraryapi.book.model.Book;
import com.example.libraryapi.customer.model.Customer;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private LocalDate borrowedSince;
    private LocalDate borrowedTo;
    @ManyToOne
    private Book book;
    @ManyToOne
    private Customer customer;
}
