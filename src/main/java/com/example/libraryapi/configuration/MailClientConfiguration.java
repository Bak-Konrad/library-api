package com.example.libraryapi.configuration;

import com.example.libraryapi.client.MailApiClient;
import com.example.libraryapi.properties.MailApiProperties;
import feign.Feign;
import feign.Logger;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MailClientConfiguration {
    @Bean
    public MailApiClient mailApiClient(MailApiProperties properties) {
        return Feign.builder()
                .client(new OkHttpClient())
                .encoder(new GsonEncoder())
                .decoder(new GsonDecoder())
                .logger(new Slf4jLogger(MailApiClient.class))
                .logLevel(Logger.Level.FULL)
                .requestInterceptor(template -> template.header("Content-Type", "application/json"))
                .target(MailApiClient.class, properties.getBaseUrl());
    }
}
