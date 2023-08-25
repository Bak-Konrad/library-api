package com.example.libraryapi.subscription;

import com.example.libraryapi.customer.CustomerRepository;
import com.example.libraryapi.customer.model.Customer;
import com.example.libraryapi.email.EmailService;
import com.example.libraryapi.mapper.GeneralMapper;
import com.example.libraryapi.subscription.model.Subscription;
import com.example.libraryapi.subscription.model.dto.SubscriptionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final CustomerRepository customerRepository;
    private final GeneralMapper generalMapper;
    private final EmailService emailService;


    public SubscriptionDto registerSubscription(Long customerId, Subscription toBeSaved) {
        String bookCategory = toBeSaved.getBookCategory();
        Customer customerToBeUpdated = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat
                        .format("Customer related to id ={0} has not been found ", customerId)));
        Subscription toBeUpdated = subscriptionRepository.findSubscriptionByBookCategory(bookCategory)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat
                        .format("Subscription named {0} has not been found ", bookCategory)));
        customerToBeUpdated.getSubscriptions().add(toBeUpdated);
        toBeUpdated.getSubscribers().add(customerToBeUpdated);
        Subscription saved = subscriptionRepository.save(toBeUpdated);
//        System.out.println(saved.getSubscribers());
        return generalMapper.mapSubscriptionToDto(saved);
    }

    public void sendNotification(String bookCategory) {
        Subscription subscription = subscriptionRepository.findSubscriptionByBookCategory(bookCategory)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat
                        .format("Subscription named '{0}' has not been found ", bookCategory)));
        Set<Customer> subscribers = subscription.getSubscribers();
        Set<String> emails = subscribers.stream()
                .map(Customer::getEmailAddress)
                .collect(Collectors.toSet());
        emailService.sendData(bookCategory, emails);
    }

    public void verifySubscription(String category) {
        if (subscriptionRepository.findSubscriptionByBookCategory(category).isEmpty()) {
            Subscription subscription = Subscription.builder()
                    .bookCategory(category)
                    .subscribers(new HashSet<>())
                    .build();
            subscriptionRepository.save(subscription);
        }
    }

    public SubscriptionDto save(Subscription toBeSaved) {
        if (subscriptionRepository.findSubscriptionByBookCategory(toBeSaved.getBookCategory()).isPresent()) {
            throw new RuntimeException("Subscription exist");
        }
        Subscription saved = subscriptionRepository.save(toBeSaved);
        return generalMapper.mapSubscriptionToDto(saved);
    }
}
