package com.example.libraryapi.service;

import com.example.libraryapi.client.MailApiClient;
import com.example.libraryapi.email.EmailService;
import com.example.libraryapi.email.model.MessageModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private MailApiClient mailApiClient;

    @InjectMocks
    private EmailService emailService;

    @Test
    public void testSendData() {
        // Given
        String bookCategory = "Mystery";
        Set<String> subscribersMails = new HashSet<>();
        subscribersMails.add("subscriber1@example.com");
        subscribersMails.add("subscriber2@example.com");

        MessageModel expectedMessageModel = MessageModel.builder()
                .bookCategory(bookCategory)
                .emailList(subscribersMails)
                .build();

        // When
        emailService.sendData(bookCategory, subscribersMails);

        // Then
        verify(mailApiClient, times(1)).sendEmail(expectedMessageModel);
    }
}
