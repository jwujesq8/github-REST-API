package com.api.service;


import com.api.dto.BranchDto;
import com.api.dto.RepositoryDto;
import com.api.dto.RepositoryInfoResponseDto;
import com.api.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class GitHubServiceImpl implements GitHubService {

    @Value("${LIST_REPOS_BY_USERNAME_PATH}")
    private String LIST_REPOS_BY_USERNAME_PATH;

    @Value("${LIST_BRANCHES_BY_REPOS_PATH}")
    private String LIST_BRANCHES_BY_REPOS_PATH;

    private final WebClient webClient;

    public GitHubServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<List<RepositoryInfoResponseDto>> listNonForkReposByUsername(String username) {
        return webClient.get()
                .uri(LIST_REPOS_BY_USERNAME_PATH, username)
                .retrieve()
                .bodyToFlux(RepositoryDto.class)
                .filter(repo -> !repo.isFork())
                .collectList()
                .flatMapMany(Flux::fromIterable)
                .flatMap(repo -> listReposBranches(username, repo.getName())
                        .map(branches -> RepositoryInfoResponseDto.builder()
                                .reposName(repo.getName())
                                .ownerLogin(repo.getOwner().getLogin())
                                .branches(branches)
                                .build()))
                .collectList()
                .onErrorResume(e -> {
                    log.error("Error fetching repositories for user {}: {}", username, e.getMessage());
                    return Mono.error(new UserNotFoundException(username));
                });
    }

    public Mono<List<BranchDto>> listReposBranches(String owner, String repos) {
        return webClient.get()
                .uri(LIST_BRANCHES_BY_REPOS_PATH, owner, repos)
                .retrieve()
                .bodyToFlux(BranchDto.class)
                .collectList();
    }
}
