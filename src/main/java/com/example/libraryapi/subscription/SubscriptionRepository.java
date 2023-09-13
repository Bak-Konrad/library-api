package com.example.libraryapi.subscription;

import com.example.libraryapi.subscription.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findSubscriptionByBookCategory(String bookCategory);

    boolean existsByBookCategory(String bookCategory);
}
