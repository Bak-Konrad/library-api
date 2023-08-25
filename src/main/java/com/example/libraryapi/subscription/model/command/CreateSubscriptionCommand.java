package com.example.libraryapi.subscription.model.command;

import lombok.Data;

@Data
public class CreateSubscriptionCommand {
    private String bookCategory;
}
