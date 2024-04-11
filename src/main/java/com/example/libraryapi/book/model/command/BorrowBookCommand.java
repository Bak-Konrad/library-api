package com.example.libraryapi.book.model.command;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
public class BorrowBookCommand {
    @NotBlank
    private LocalDate borrowedSince;
    @NotBlank
    private LocalDate borrowedTo;
}
