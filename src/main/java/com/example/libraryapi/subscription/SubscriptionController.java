package com.example.libraryapi.subscription;

import com.example.libraryapi.mapper.GeneralMapper;
import com.example.libraryapi.subscription.model.Subscription;
import com.example.libraryapi.subscription.model.command.CreateSubscriptionCommand;
import com.example.libraryapi.subscription.model.dto.SubscriptionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/library/subscriptions")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    private final GeneralMapper generalMapper;

    @PostMapping
    private ResponseEntity<SubscriptionDto> addSubscription(@RequestBody @Valid CreateSubscriptionCommand subscriptionCommand) {
        Subscription toBeSaved = generalMapper.mapSubscriptionFromCommand(subscriptionCommand);
        return new ResponseEntity<>(subscriptionService.save(toBeSaved), HttpStatus.CREATED);
    }
}
