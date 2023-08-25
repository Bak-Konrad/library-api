package com.example.libraryapi.client;

import com.example.libraryapi.email.model.MessageModel;
import feign.RequestLine;
import org.springframework.web.bind.annotation.RequestBody;

public interface MailApiClient {

    @RequestLine(value = "POST /mail")
    void sendEmail(@RequestBody MessageModel messageModel);
}
