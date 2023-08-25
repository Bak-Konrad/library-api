package com.example.libraryapi.customer.model;

import com.example.libraryapi.book.model.Book;
import lombok.*;
import com.example.libraryapi.subscription.model.Subscription;

import javax.persistence.*;
import java.util.Set;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String emailAddress;
    @OneToMany(mappedBy = "customer")
    private Set<Book> bookList;
    @ManyToMany
    @JoinTable(name = "customer_subscriptions",
            joinColumns = @JoinColumn(name = "customer_Id"),
            inverseJoinColumns = @JoinColumn(name = "subscription_Id"))
    private Set<Subscription> subscriptions;


}
