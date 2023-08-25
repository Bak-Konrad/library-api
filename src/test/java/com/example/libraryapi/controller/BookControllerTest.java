package com.example.libraryapi.controller;

import com.example.libraryapi.book.BookService;
import com.example.libraryapi.book.model.command.CreateBookCommand;
import com.example.libraryapi.book.model.dto.BookDto;
import com.example.libraryapi.mapper.GeneralMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GeneralMapper generalMapper;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void testAddBook() throws Exception {
        CreateBookCommand bookCommand = new CreateBookCommand();
        bookCommand.setCategory("AA");
        bookCommand.setTitle("BB");
        bookCommand.setAuthorFirstName("F");
        bookCommand.setAuthorLastName("L");


        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/library/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookCommand)))
                .andExpect(status().isCreated());

    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    public void testLockBookWithEmployeeRole() throws Exception {
        Long bookId = 1L;
        BookDto lockedBookDto = BookDto.builder()
                .id(bookId)
                .blocked(true)
                .category("A")
                .title("B")
                .build();

        when(bookService.lockBook(bookId)).thenReturn(lockedBookDto);

        mockMvc.perform(patch("/api/v1/library/books/{bookId}/lock", bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(lockedBookDto.getId()))
                .andExpect(jsonPath("$.title").value(lockedBookDto.getTitle()))
                .andExpect(jsonPath("$.category").value(lockedBookDto.getCategory()))
                .andExpect(jsonPath("$.blocked").value(lockedBookDto.isBlocked()));

        verify(bookService, times(1)).lockBook(bookId);
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testLockBookWithUserRole() throws Exception {
        Long bookId = 1L;

        mockMvc.perform(patch("/api/v1/library/books/{bookId}/lock", bookId))
                .andExpect(status().isForbidden());

        verify(bookService, never()).lockBook(anyLong());
    }

    @Test
    public void testGetAllBooks() throws Exception {
        int page = 0;
        int pageSize = 10;
        Pageable pageable = PageRequest.of(page, pageSize);

        List<BookDto> bookList = new ArrayList<>();
        bookList.add(BookDto.builder()
                .title("s")
                .category("sa")
                .authorLastName("a")
                .build());

        Page<BookDto> bookPage = new PageImpl<>(bookList, pageable, bookList.size());

        when(bookService.findAll(pageable)).thenReturn(bookPage);

        mockMvc.perform(get("/api/v1/library/books/public")
                        .param("page", String.valueOf(page)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value(bookList.get(0).getTitle()))
                .andExpect(jsonPath("$.content[0].category").value(bookList.get(0).getCategory()));
        verify(bookService, times(1)).findAll(pageable);
    }


}
