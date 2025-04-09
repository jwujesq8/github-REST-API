package com.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${github.api.token}")
    private String GITHUB_API_TOKEN;

    @Bean
    public WebClient webClient(){
        return WebClient.builder()
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + GITHUB_API_TOKEN)
                .baseUrl("https://api.github.com")
                .build();
    }
}
