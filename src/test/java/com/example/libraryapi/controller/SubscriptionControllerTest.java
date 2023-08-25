package com.example.libraryapi.controller;


import com.example.libraryapi.subscription.SubscriptionService;
import com.example.libraryapi.subscription.model.command.CreateSubscriptionCommand;
import com.example.libraryapi.subscription.model.dto.SubscriptionDto;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SubscriptionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SubscriptionService subscriptionService;

    @Test
    @WithMockUser("EMPLOYEE")
    public void testAddSubscriptionWithEmployeeRole() throws Exception {
        CreateSubscriptionCommand subscriptionCommand = new CreateSubscriptionCommand();
        subscriptionCommand.setBookCategory("Mystery");

        SubscriptionDto subscriptionDto = SubscriptionDto.builder().bookCategory("a").build();

        when(subscriptionService.save(any())).thenReturn(subscriptionDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/library/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subscriptionCommand)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookCategory").value(subscriptionDto.getBookCategory()));
    }

}
