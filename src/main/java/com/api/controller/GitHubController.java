package com.api.controller;

import com.api.dto.RepositoryInfoResponseDto;
import com.api.service.GitHubService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/github")
@RequiredArgsConstructor
public class GitHubController {

    private final GitHubService gitHubService;

    @GetMapping("/{username}/repos")
    public Mono<List<RepositoryInfoResponseDto>> getRepositories(@PathVariable String username) {
        return gitHubService.listNonForkReposByUsername(username);
    }
}
