package com.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebClientConfig {

    private String GITHUB_API_TOKEN;

    @Bean
    public WebClient webClient(@Value("${github.api.token.path}") String githubApiTokenPath)
            throws IOException {
        Path a = Paths.get(githubApiTokenPath).toAbsolutePath().normalize();
        if (Files.exists(a)) {
            GITHUB_API_TOKEN = new String(Files.readAllBytes(a));
        } else {
            throw new IOException("githubToken file not found: " + githubApiTokenPath);
        }

        return WebClient.builder()
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + GITHUB_API_TOKEN)
                .baseUrl("https://api.github.com")
                .build();
    }
}
