package com.example.libraryapi.controller;

import com.example.libraryapi.book.BookService;
import com.example.libraryapi.book.model.command.BorrowBookCommand;
import com.example.libraryapi.book.model.dto.BookDto;
import com.example.libraryapi.loan.LoanService;
import com.example.libraryapi.loan.model.dto.LoanDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookService bookService;

    @MockBean
    private LoanService loanService;

    @BeforeEach
    public void setup() {
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    public void testBorrowBook() throws Exception {
        // Given
        Long customerId = 1L;
        Long bookId = 2L;
        BorrowBookCommand bookCommand = new BorrowBookCommand();

        LoanDto loanDto = LoanDto.builder().build();

        when(bookService.borrowBook(customerId, bookId, bookCommand)).thenReturn(loanDto);

        // When
        ResultActions result = mockMvc.perform(patch("/api/v1/library/loans/customers/{customerId}/books/{bookId}/borrow",
                customerId, bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookCommand)));

        // Then
        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER", "EMPLOYEE"})
    public void testReturnBook() throws Exception {
        // Given
        Long loanId = 1L;
        BookDto bookDto = BookDto.builder().build();

        when(bookService.returnBook(loanId)).thenReturn(bookDto);

        // When
        ResultActions result = mockMvc.perform(patch("/api/v1/library/loans/{loanId}/return", loanId)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser
    public void testBorrowBookWithoutRoles() throws Exception {
        // Given
        Long customerId = 1L;
        Long bookId = 2L;
        BorrowBookCommand bookCommand = new BorrowBookCommand();

        LoanDto loanDto = LoanDto.builder().build();

        when(bookService.borrowBook(customerId, bookId, bookCommand)).thenReturn(loanDto);

        // When
        ResultActions result = mockMvc.perform(patch("/api/v1/library/loans/customers/{customerId}/books/{bookId}/borrow", customerId, bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookCommand)));

        // Then
        result.andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    public void testReturnBookWithoutRoles() throws Exception {
        // Given
        Long loanId = 1L;
        BookDto bookDto = BookDto.builder().build();

        when(bookService.returnBook(loanId)).thenReturn(bookDto);

        // When
        ResultActions result = mockMvc.perform(patch("/api/v1/library/loans/{loanId}/return", loanId)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        result.andExpect(status().isForbidden());
    }

}
