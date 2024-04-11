package com.example.libraryapi.service;

import com.example.libraryapi.book.BookRepository;
import com.example.libraryapi.book.model.Book;
import com.example.libraryapi.customer.CustomerRepository;
import com.example.libraryapi.customer.model.Customer;
import com.example.libraryapi.email.EmailService;
import com.example.libraryapi.email.NotificationSubscriptionSchedulerService;
import com.example.libraryapi.email.model.MessageModel;
import com.example.libraryapi.subscription.model.Subscription;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class NotificationSubscriptionSchedulerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private NotificationSubscriptionSchedulerService notificationService;


    @Test
    public void testHandleCustomerData() {
        //given
        Subscription subscription = new Subscription();
        subscription.setBookCategory("Akcja");
        List<Customer> testCustomers = List.of(
                Customer.builder().emailAddress("test0@example.com")
                        .subscriptions(Set.of(subscription))
                        .build());

        List<Book> testBooks = List.of(
                Book.builder().category("Akcja").build());

        when(bookRepository.findAllByAddingDateTimeAfterAndCategoryIn(Mockito.any(), Mockito.any()))
                .thenReturn(testBooks);

        //when
        List<MessageModel> messageModels = notificationService.handleCustomerData(testCustomers);

        //then
        assertNotNull(messageModels);
        assertFalse(messageModels.isEmpty());
    }

    @Test
    public void testPrepareCustomersForBatch() {
        // given
        int page = 0;
        int batchSize = 10;

        List<Customer> expectedCustomers = new ArrayList<>(Collections.nCopies(batchSize, new Customer()));
        for (Customer customer : expectedCustomers) {
            Subscription subscription = new Subscription();
            subscription.setBookCategory("Akcja");
            customer.setSubscriptions(Collections.singleton(subscription));
        }

        Page<Customer> mockPage = new PageImpl<>(expectedCustomers);

        when(customerRepository.getAllBySubscriptionsIsNotEmpty(Mockito.any(Pageable.class)))
                .thenReturn(mockPage);

        // When
        List<Customer> result = notificationService.prepareCustomersForBatch(page, batchSize);

        // then
        Mockito.verify(customerRepository).getAllBySubscriptionsIsNotEmpty(Mockito.any(Pageable.class));
        assertEquals(expectedCustomers.size(), result.size());
    }

    @Test
    public void testMessageBuilder_multipleCategories() {
        // given
        List<String> categories = Arrays.asList("Mystery", "Thriller");
        String expected = "New books in categories Mystery, Thriller has been added";

        // when
        String result = notificationService.messageBuilder(categories);

        // then
        assertEquals(expected, result);

    }

    @Test
    public void testMessageBuilder_singleCategory() {
        // given
        List<String> categories = Collections.singletonList("Mystery");
        String expected = "New book in category Mystery has been added";

        // when
        String result = notificationService.messageBuilder(categories);

        // then
        assertEquals(expected, result);
    }

    @Test
    public void testSendNotification() {
        // Given
        int batchSize = 100;
        int page = 0;
        boolean hasNextBatch = true;

        List<Customer> customersBatch1 = new ArrayList<>();
        customersBatch1.add(Customer.builder()
                .subscriptions(Set.of(Subscription.builder().build()))
                .emailAddress("test1@example.com")
                .build());
        List<MessageModel> messageTempBatch1 = Collections.singletonList(MessageModel.builder().build());
        when(customerRepository.getAllBySubscriptionsIsNotEmpty(any()))
                .thenReturn(new PageImpl<>(customersBatch1));

        // When
        while (hasNextBatch) {
            List<Customer> customers = notificationService.prepareCustomersForBatch(page, batchSize);
            List<MessageModel> messageTemp = new ArrayList<>();

            if (!customers.isEmpty()) {
                messageTemp = notificationService.handleCustomerData(customers);
            }

            notificationService.sendNotification();

            // Then
            verify(emailService, times(1)).sendData(messageTemp);

            page++;
            if (customers.size() < batchSize) {
                hasNextBatch = false;
            }
        }
        verify(emailService, times(1)).sendData(anyList());
    }
}





