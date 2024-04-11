package com.example.libraryapi.book.model.command;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class CreateBookCommand {
    @NotBlank
    private String authorFirstName;
    @NotBlank
    private String authorLastName;
    @NotBlank
    private String title;
    @NotBlank
    private String category;

    private LocalDateTime addingDateTime;
}
