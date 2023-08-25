package com.example.libraryapi.customer.model.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CustomerDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String emailAddress;

}
