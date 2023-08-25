package com.example.libraryapi.book.model.command;

import lombok.Data;

@Data
public class LockBookCommand {
    private boolean blocked;
}
