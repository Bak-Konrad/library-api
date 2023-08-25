package com.example.libraryapi.controller;

import com.example.libraryapi.book.BookService;
import com.example.libraryapi.book.model.command.BorrowBookCommand;
import com.example.libraryapi.book.model.dto.BookDto;
import com.example.libraryapi.customer.CustomerService;
import com.example.libraryapi.customer.model.Customer;
import com.example.libraryapi.customer.model.command.CreateCustomerCommand;
import com.example.libraryapi.customer.model.dto.CustomerDto;
import com.example.libraryapi.subscription.SubscriptionService;
import com.example.libraryapi.subscription.model.Subscription;
import com.example.libraryapi.subscription.model.dto.SubscriptionDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CustomerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private BookService bookService;

    @MockBean
    private SubscriptionService subscriptionService;
    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @WithMockUser(roles = "EMPLOYEE")
    public void testAddCustomerWithEmployeeRole() throws Exception {
        CreateCustomerCommand customerCommand = new CreateCustomerCommand();
        customerCommand.setEmailAddress("john@example.com");
        customerCommand.setLastName("DDDD");
        customerCommand.setFirstName("Zenon");
        CustomerDto savedCustomerDto = CustomerDto.builder()
                .id(1L)
                .firstName("Zenon")
                .emailAddress("john@example.com")
                .build();

        when(customerService.save(any(Customer.class))).thenReturn(savedCustomerDto);

        mockMvc.perform(MockMvcRequestBuilders.post(("/api/v1/library/customers/public"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerCommand)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(savedCustomerDto.getId()))
                .andExpect(jsonPath("$.firstName").value(savedCustomerDto.getFirstName()));

        verify(customerService, times(1)).save(any(Customer.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testAddCustomerWithUserRole() throws Exception {
        CreateCustomerCommand customerCommand = new CreateCustomerCommand();
        customerCommand.setEmailAddress("john@example.com");
        customerCommand.setLastName("DDDD");
        customerCommand.setFirstName("Zenon");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/library/customers/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerCommand)))
                .andExpect(status().isCreated());

    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    public void testBorrowBookWithCustomerRole() throws Exception {
        Long customerId = 1L;
        Long bookId = 1L;
        BorrowBookCommand bookCommand = new BorrowBookCommand();
        BookDto borrowedBookDto = BookDto.builder()
                .blocked(true)
                .build();

        when(bookService.borrowBook(customerId, bookId, bookCommand)).thenReturn(borrowedBookDto);

        mockMvc.perform(patch("/api/v1/library/customers/{customerId}/books/{bookId}/borrow", customerId, bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookCommand)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(borrowedBookDto.getId()))
                .andExpect(jsonPath("$.title").value(borrowedBookDto.getTitle()))
                .andExpect(jsonPath("$.category").value(borrowedBookDto.getCategory()))
                .andExpect(jsonPath("$.blocked").value(borrowedBookDto.isBlocked()));

        verify(bookService, times(1)).borrowBook(customerId, bookId, bookCommand);
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE") // Non-CUSTOMER role
    public void testBorrowBookWithNonCustomerRole() throws Exception {
        Long customerId = 1L;
        Long bookId = 1L;
        BorrowBookCommand bookCommand = new BorrowBookCommand();

        mockMvc.perform(patch("/api/v1/library/customers/{customerId}/books/{bookId}/borrow", customerId, bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookCommand)))
                .andExpect(status().isForbidden());

        verify(bookService, never()).borrowBook(customerId, bookId, bookCommand);
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    public void testReturnBookWithCustomerRole() throws Exception {
        Long customerId = 1L;
        Long bookId = 1L;
        BookDto returnedBookDto = BookDto.builder().build();

        when(bookService.returnBook(customerId, bookId)).thenReturn(returnedBookDto);
        when(bookService.isBookRentedByCustomer(customerId, bookId)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/library/customers/{customerId}/books/{bookId}/return", customerId, bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(returnedBookDto.getId()))
                .andExpect(jsonPath("$.title").value(returnedBookDto.getTitle()))
                .andExpect(jsonPath("$.category").value(returnedBookDto.getCategory()))
                .andExpect(jsonPath("$.blocked").value(returnedBookDto.isBlocked()));

        verify(bookService, times(1)).returnBook(customerId, bookId);
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    public void testReturnBookWithNonCustomerRole() throws Exception {
        Long customerId = 1L;
        Long bookId = 1L;
        when(bookService.isBookRentedByCustomer(customerId, bookId)).thenReturn(true);

        mockMvc.perform(patch("/api/v1/library/customers/{customerId}/books/{bookId}/return", customerId, bookId))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    public void testRegisterSubscriptionWithCustomerRole() throws Exception {
        Long customerId = 1L;
        Subscription subscription = Subscription.builder().build();
        SubscriptionDto savedSubscriptionDto = SubscriptionDto.builder().build();

        when(subscriptionService.registerSubscription(customerId, subscription)).thenReturn(savedSubscriptionDto);

        mockMvc.perform(patch("/api/v1/library/customers/{customerId}/subscribe", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subscription)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    public void testRegisterSubscriptionWithNonCustomerRole() throws Exception {
        Long customerId = 1L;
        Subscription subscription = Subscription.builder().build();

        mockMvc.perform(patch("/api/v1/library/customers/{customerId}/subscribe", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subscription)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    public void testGetAllCustomersWithEmployeeRole() throws Exception {
        int page = 0;
        CustomerDto c1 = CustomerDto.builder().id(1L).build();
        CustomerDto c2 = CustomerDto.builder().id(2L).build();
        List<CustomerDto> customerDtos = List.of(c1, c2);
        Page<CustomerDto> customerPage = new PageImpl<>(customerDtos);

        when(customerService.findAll(any(Pageable.class))).thenReturn(customerPage);

        mockMvc.perform(get("/api/v1/library/customers")
                        .param("page", String.valueOf(page))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(customerDtos.get(0).getId()))
                .andExpect(jsonPath("$.content[1].id").value(customerDtos.get(1).getId()));


        verify(customerService, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER") // Non-EMPLOYEE role
    public void testGetAllCustomersWithNonEmployeeRole() throws Exception {
        mockMvc.perform(get("/api/v1/library/customers")
                        .param("page", String.valueOf(0))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        verify(customerService, never()).findAll(any(Pageable.class));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    public void testGetAllBooksForCustomerWithCustomerRole() throws Exception {
        Long customerId = 1L;
        BookDto d1 = BookDto.builder().id(2L).title("a").build();
        List<BookDto> bookDtos = List.of(d1);

        when(customerService.getAllBooksForCustomer(customerId)).thenReturn(bookDtos);

        mockMvc.perform(get("/api/v1/library/customers/{customerId}/books", customerId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookDtos.get(0).getId()))
                .andExpect(jsonPath("$[0].title").value(bookDtos.get(0).getTitle()));


        verify(customerService, times(1)).getAllBooksForCustomer(customerId);
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    public void testGetAllBooksForCustomerWithEmployeeRole() throws Exception {
        Long customerId = 1L;

        mockMvc.perform(get("/api/v1/library/customers/{customerId}/books", customerId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(customerService, times(1)).getAllBooksForCustomer(customerId);
    }

    @Test
    public void testGetAllBooksForCustomerWithAnotherRole() throws Exception {
        Long customerId = 1L;

        mockMvc.perform(get("/api/v1/library/customers/{customerId}/books", customerId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(customerService, never()).getAllBooksForCustomer(customerId);
    }


}


