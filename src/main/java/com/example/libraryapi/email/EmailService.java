package com.example.libraryapi.email;

import com.example.libraryapi.client.MailApiClient;
import com.example.libraryapi.email.model.MessageModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class EmailService {
    private final MailApiClient mailApiClient;

    public void sendData(List<MessageModel> data) {
        mailApiClient.sendEmail(data);

    }
}
