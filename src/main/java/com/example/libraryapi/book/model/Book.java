package com.example.libraryapi.book.model;

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
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String authorFirstName;
    private String authorLastName;
    private String title;
    private String category;
    private LocalDate borrowedSince;
    private LocalDate borrowedTo;
    private boolean blocked = false;
    @ManyToOne
    private Customer customer;
}
