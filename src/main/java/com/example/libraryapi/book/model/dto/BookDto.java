package com.example.libraryapi.book.model.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookDto {
    private Long id;
    private String authorFirstName;
    private String authorLastName;
    private String title;
    private String category;
    private boolean blocked;

}
