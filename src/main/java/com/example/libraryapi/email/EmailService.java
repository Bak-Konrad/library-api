package com.example.libraryapi.email;

import com.example.libraryapi.client.MailApiClient;
import com.example.libraryapi.email.model.MessageModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final MailApiClient mailApiClient;

    public void sendData(String bookCategory, Set<String> subscribersMails) {
        MessageModel data = MessageModel.builder()
                .bookCategory(bookCategory)
                .emailList(subscribersMails)
                .build();
        mailApiClient.sendEmail(data);
    }
}
