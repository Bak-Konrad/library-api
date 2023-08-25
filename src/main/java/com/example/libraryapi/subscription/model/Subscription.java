package com.example.libraryapi.subscription.model;

import com.example.libraryapi.customer.model.Customer;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String bookCategory;
    @ManyToMany(mappedBy = "subscriptions")
    private Set<Customer> subscribers;
}
