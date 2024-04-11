package com.example.libraryapi.book.model;

import com.example.libraryapi.customer.CustomerService;
import com.example.libraryapi.customer.model.Customer;
import lombok.AllArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.file.AccessDeniedException;
import java.util.Arrays;

@Aspect
@Component
@AllArgsConstructor
public class BookAspect {
    private final CustomerService customerService;


    @Before("@annotation(org.springframework.web.bind.annotation.GetMapping) && " +
            "execution(* com.example.libraryapi.customer.CustomerController.getAllBooksForCustomer(..)) && " +
            "@annotation(secured)")
    public void checkBookAccess(JoinPoint joinPoint, Secured secured) throws AccessDeniedException {
        Long customerId = (Long) joinPoint.getArgs()[0];
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!isAccessAllowed(authentication, customerId, secured.value())) {
            throw new AccessDeniedException("Access denied to the list of books.");
        }
    }

    private boolean isAccessAllowed(Authentication authentication, Long customerId, String[] roles) {
        if (Arrays.asList(roles).contains("ROLE_EMPLOYEE")) {
            return true;
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Customer customer = customerService.getCustomerById(customerId);

        return userDetails.getUsername().equals(customer.getId().toString());
    }
}








