package com.example.libraryapi.book.model.command;

import lombok.Data;

@Data
public class CreateBookCommand {
    private String authorFirstName;
    private String authorLastName;
    private String title;
    private String category;
}
