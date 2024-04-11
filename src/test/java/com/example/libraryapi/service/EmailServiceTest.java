package com.example.libraryapi.service;

import com.example.libraryapi.client.MailApiClient;
import com.example.libraryapi.email.EmailService;
import com.example.libraryapi.email.model.MessageModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private MailApiClient mailApiClient;

    @InjectMocks
    private EmailService emailService;


    @Test
    public void testSendData() {
        // Given
        MessageModel message1 = MessageModel.builder()
                .email("test1@example.com")
                .message("Test message 1")
                .build();

        MessageModel message2 = MessageModel.builder()
                .email("test2@example.com")
                .message("Test message 2")
                .build();

        List<MessageModel> messageModels = List.of(message1, message2);

        // When
        emailService.sendData(messageModels);

        // Then
        verify(mailApiClient, Mockito.times(1)).sendEmail(messageModels);
    }


}
