package com.example.libraryapi.book.model.command;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BorrowBookCommand {
    private LocalDate borrowedSince;
    private LocalDate borrowedTo;
}
