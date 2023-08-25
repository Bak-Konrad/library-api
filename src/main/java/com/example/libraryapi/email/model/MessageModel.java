package com.example.libraryapi.email.model;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class MessageModel {
    private String bookCategory;
    private Set<String> emailList;


}
