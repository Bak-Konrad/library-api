package com.example.libraryapi.exception;

import lombok.Getter;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ValidationExceptionDto extends ExceptionDto {
    private final List<ViolationInfo> violations = new ArrayList<>();

    public ValidationExceptionDto() {
        super("Validation failure");
    }

    public void addViolation(String field, String message) {
        violations.add(new ViolationInfo(field, message));
    }

    @Value
    public static class ViolationInfo {
        String field;
        String message;
    }
}
