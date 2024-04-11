package com.example.libraryapi.subscription.model.command;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreateSubscriptionCommand {
    @NotBlank
    private String bookCategory;
}
