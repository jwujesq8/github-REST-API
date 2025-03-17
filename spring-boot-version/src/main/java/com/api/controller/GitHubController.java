package com.api.controller;

import com.api.dto.RepositoryInfoResponseDto;
import com.api.service.GitHubService;
import io.smallrye.mutiny.Uni;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/github")
@RequiredArgsConstructor
public class GitHubController {

    private final GitHubService gitHubService;

    @GetMapping("/{username}/repos")
    public Uni<List<RepositoryInfoResponseDto>> getRepositories(@PathVariable String username) {
        return gitHubService.listNonForkReposByUsername(username);
    }
}
