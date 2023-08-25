package com.example.libraryapi.service;


import com.example.libraryapi.customer.CustomerRepository;
import com.example.libraryapi.customer.model.Customer;
import com.example.libraryapi.email.EmailService;
import com.example.libraryapi.mapper.GeneralMapper;
import com.example.libraryapi.subscription.SubscriptionRepository;
import com.example.libraryapi.subscription.SubscriptionService;
import com.example.libraryapi.subscription.model.Subscription;
import com.example.libraryapi.subscription.model.dto.SubscriptionDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {
    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private GeneralMapper generalMapper;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private SubscriptionService subscriptionService;


    @Test
    public void testRegisterSubscription_resultsInSubscriptionPassedToRepo() {
        // Given
        Long customerId = 1L;
        String bookCategory = "Mystery";
        Subscription toBeSaved = Subscription.builder()
                .bookCategory(bookCategory)
                .subscribers(new HashSet<>())
                .build();

        Customer customerToBeUpdated = new Customer();
        customerToBeUpdated.setSubscriptions(new HashSet<>());
        Subscription existingSubscription = new Subscription();
        existingSubscription.setBookCategory(bookCategory);
        existingSubscription.setSubscribers(new HashSet<>());
        customerToBeUpdated.getSubscriptions().add(existingSubscription);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customerToBeUpdated));
        when(subscriptionRepository.findSubscriptionByBookCategory(bookCategory)).thenReturn(Optional.of(existingSubscription));
        when(subscriptionRepository.save(existingSubscription)).thenReturn(existingSubscription);
        when(generalMapper.mapSubscriptionToDto(existingSubscription)).thenReturn(SubscriptionDto.builder().build());

        // When
        SubscriptionDto savedSubscriptionDto = subscriptionService.registerSubscription(customerId, toBeSaved);

        // Then
        assertNotNull(savedSubscriptionDto);
        verify(generalMapper, times(1)).mapSubscriptionToDto(existingSubscription);

    }

    @Test
    public void testRegisterSubscription_resultsInCustomerEntityNotFoundException() {
        // Given
        Long customerId = 1L;
        Subscription toBeSaved = new Subscription();

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // Then
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> subscriptionService.registerSubscription(customerId, toBeSaved))
                .withMessage(MessageFormat.format("Customer related to id ={0} has not been found ", customerId));
    }

    @Test
    public void testRegisterSubscription_resultsInSubscriptionEntityNotFoundException() {
        // Given
        Long customerId = 1L;
        String bookCategory = "Mystery";
        Subscription toBeSaved = new Subscription();
        toBeSaved.setBookCategory(bookCategory);

        Customer customerToBeUpdated = new Customer();

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customerToBeUpdated));
        when(subscriptionRepository.findSubscriptionByBookCategory(bookCategory)).thenReturn(Optional.empty());

        // Then
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> subscriptionService.registerSubscription(customerId, toBeSaved))
                .withMessage(MessageFormat.format("Subscription named {0} has not been found ", bookCategory));
    }

    @Test
    public void testSendNotification_resultsInDataSendToEmailService() {
        // Given
        String bookCategory = "Mystery";
        Subscription subscription = new Subscription();
        subscription.setSubscribers(new HashSet<>());
        subscription.setBookCategory(bookCategory);

        Customer subscriber1 = new Customer();
        subscriber1.setEmailAddress("subscriber1@example.com");
        Customer subscriber2 = new Customer();
        subscriber2.setEmailAddress("subscriber2@example.com");
        subscription.getSubscribers().add(subscriber1);
        subscription.getSubscribers().add(subscriber2);

        Set<String> emails = new HashSet<>();
        emails.add(subscriber1.getEmailAddress());
        emails.add(subscriber2.getEmailAddress());

        when(subscriptionRepository.findSubscriptionByBookCategory(bookCategory)).thenReturn(Optional.of(subscription));

        // When
        subscriptionService.sendNotification(bookCategory);

        // Then
        verify(emailService, times(1)).sendData(eq(bookCategory), eq(emails));
    }

    @Test
    public void testSendNotificationSubscriptionNotFound() {
        // Given
        String bookCategory = "Mystery";

        when(subscriptionRepository.findSubscriptionByBookCategory(bookCategory)).thenReturn(Optional.empty());

        // Then
        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> subscriptionService.sendNotification(bookCategory))
                .withMessage(MessageFormat.format("Subscription named '{0}' has not been found ", bookCategory));
    }

    @Test
    public void testVerifySubscription_resultsInCreatingNewSubscription() {
        // Given
        String bookCategory = "Fantasy";

        when(subscriptionRepository.findSubscriptionByBookCategory(bookCategory)).thenReturn(Optional.empty());

        // When
        subscriptionService.verifySubscription(bookCategory);

        // Then
        verify(subscriptionRepository, times(1)).save(any(Subscription.class));
    }

    @Test
    public void testSaveSubscription_resultsInSubscriptionBeingSaved() {
        // Given
        Subscription toBeSaved = new Subscription();
        Subscription savedSubscription = new Subscription();

        when(subscriptionRepository.save(toBeSaved)).thenReturn(savedSubscription);
        when(generalMapper.mapSubscriptionToDto(savedSubscription)).thenReturn(SubscriptionDto.builder().build());

        // When
        SubscriptionDto savedSubscriptionDto = subscriptionService.save(toBeSaved);

        // Then
        assertNotNull(savedSubscriptionDto);
        verify(generalMapper, times(1)).mapSubscriptionToDto(savedSubscription);
        verify(subscriptionRepository, times(1)).save(any(Subscription.class));
    }


}