package com.example.libraryapi.subscription.model.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Builder
@Getter
public class SubscriptionDto {
    private String bookCategory;
    private Set<Long> customersId;
}
