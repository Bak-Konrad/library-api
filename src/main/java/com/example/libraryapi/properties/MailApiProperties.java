package com.example.libraryapi.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "library.api")
@Getter
@Setter
public class MailApiProperties {
    private String baseUrl;
}
