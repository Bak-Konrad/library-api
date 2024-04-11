package com.example.libraryapi.email;

import com.example.libraryapi.book.BookRepository;
import com.example.libraryapi.book.model.Book;
import com.example.libraryapi.customer.CustomerRepository;
import com.example.libraryapi.customer.model.Customer;
import com.example.libraryapi.email.model.MessageModel;
import com.example.libraryapi.subscription.model.Subscription;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationSubscriptionSchedulerService {
    private final CustomerRepository customerRepository;
    private final BookRepository bookRepository;
    private final EmailService emailService;

    @Scheduled(cron = "0 0 20 * * *")
    public void sendNotification() {
        int batchSize = 100;
        int page = 0;
        boolean hasNextBatch = true;
        List<MessageModel> messageTemp = new ArrayList<>();
        while (hasNextBatch) {
            List<Customer> customers = prepareCustomersForBatch(page, batchSize);

            if (!customers.isEmpty()) {
                messageTemp = handleCustomerData(customers);
            }
            emailService.sendData(messageTemp);
            page++;
            if (customers.size() < batchSize) {
                hasNextBatch = false;
            }
        }
    }

    public List<MessageModel> handleCustomerData(List<Customer> customers) {
        List<MessageModel> messageModels = new ArrayList<>();

        for (Customer customer : customers) {
            List<Book> customerBooks = handleCategoriesFromRepo(customer);
            if (!customerBooks.isEmpty()) {
                String messageContent = messageBuilder(customerBooks.stream()
                        .map(Book::getCategory)
                        .distinct()
                        .toList());
                MessageModel messageModel = MessageModel.builder()
                        .email(customer.getEmailAddress())
                        .message(messageContent)
                        .build();
                messageModels.add(messageModel);
            }
        }

        return messageModels;
    }

    public List<Book> handleCategoriesFromRepo(Customer customer) {
        LocalDateTime timeStamp = LocalDateTime.now().minusHours(24);

        List<String> customerSubscriptionsTitles = customer.getSubscriptions().stream()
                .map(Subscription::getBookCategory)
                .toList();
        return bookRepository.findAllByAddingDateTimeAfterAndCategoryIn(timeStamp, customerSubscriptionsTitles);
    }


    public List<Customer> prepareCustomersForBatch(int page, int batchSize) {
        Pageable pageable = PageRequest.of(page, batchSize);
        Page<Customer> subscriberPage = customerRepository.getAllBySubscriptionsIsNotEmpty(pageable);

        return subscriberPage.getContent();
    }

    public String messageBuilder(List<String> categories) {
        int categoryCount = categories.size();

        String categoriesString = String.join(", ", categories);
        String categoryWord = categoryCount == 1 ? "category" : "categories";
        String pluralSuffix = categoryCount > 1 ? "s" : "";
        return String.format("New book%s in %s %s has been added", pluralSuffix, categoryWord, categoriesString);

    }

}
