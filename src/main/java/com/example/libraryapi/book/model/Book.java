package com.example.libraryapi.book.model;

import com.example.libraryapi.loan.model.Loan;
import com.example.libraryapi.subscription.model.Subscription;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

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
    private boolean blocked = false;
    private LocalDateTime addingDateTime;
    @ManyToMany
    private Set<Subscription> subscriptions;
    @OneToMany(mappedBy = "book")
    private Set<Loan> bookLoans;
}
