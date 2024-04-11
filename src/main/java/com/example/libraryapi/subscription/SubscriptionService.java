package com.example.libraryapi.subscription;

import com.example.libraryapi.customer.CustomerRepository;
import com.example.libraryapi.customer.model.Customer;
import com.example.libraryapi.mapper.GeneralMapper;
import com.example.libraryapi.subscription.model.Subscription;
import com.example.libraryapi.subscription.model.dto.SubscriptionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.text.MessageFormat;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final CustomerRepository customerRepository;
    private final GeneralMapper generalMapper;

    @Transactional
    public SubscriptionDto registerSubscription(Long customerId, Subscription toBeSaved) {

        String bookCategory = toBeSaved.getBookCategory();

        Customer customerToBeUpdated = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat
                        .format("Customer related to id ={0} has not been found ", customerId)));

        Subscription toBeUpdated = subscriptionRepository.findSubscriptionByBookCategory(bookCategory)
                .orElseThrow(() -> new EntityNotFoundException(MessageFormat
                        .format("Subscription named {0} has not been found ", bookCategory)));

        toBeUpdated.getSubscribers().add(customerToBeUpdated);
        customerToBeUpdated.getSubscriptions().add(toBeUpdated);
        Subscription saved = subscriptionRepository.save(toBeUpdated);

        return generalMapper.mapSubscriptionToDto(saved);
    }

    public SubscriptionDto save(Subscription toBeSaved) {
        Subscription saved = subscriptionRepository.save(toBeSaved);

        return generalMapper.mapSubscriptionToDto(saved);
    }
}
