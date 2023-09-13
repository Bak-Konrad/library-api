package com.example.libraryapi.loan.model.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
public class LoanDto {
    private Long Id;
    private LocalDate borrowedSince;
    private LocalDate borrowedTo;

    private Long bookId;

    private Long customerId;
}
