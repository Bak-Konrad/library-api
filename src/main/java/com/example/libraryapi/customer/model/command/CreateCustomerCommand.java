package com.example.libraryapi.customer.model.command;

import lombok.Data;

@Data
public class CreateCustomerCommand {
    private String firstName;
    private String lastName;
    private String emailAddress;
}
