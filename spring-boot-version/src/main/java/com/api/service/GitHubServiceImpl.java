package com.api.service;


import com.api.dto.BranchDto;
import com.api.dto.RepositoryDto;
import com.api.dto.RepositoryInfoResponseDto;
import com.api.exception.UserNotFoundException;
import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
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

    // Construct
    public GitHubServiceImpl(@Value("${github.api.token}") String GITHUB_API_TOKEN) {
        this.webClient = WebClient.builder()
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + GITHUB_API_TOKEN)
                .baseUrl("https://api.github.com")
                .build();
    }

    public Uni<List<RepositoryInfoResponseDto>> listNonForkReposByUsername(String username) {

        List<RepositoryDto> repositoryDtoList =  webClient.get()
                .uri(LIST_REPOS_BY_USERNAME_PATH, username)
                .retrieve()
                .bodyToFlux(RepositoryDto.class)
                .filter(repo -> !repo.isFork())
                .collectList()
                .onErrorResume(e -> Mono.error(new UserNotFoundException(username)))
                .block();

        if(repositoryDtoList == null) {return Uni.createFrom().item(new ArrayList<>());}

        List<RepositoryInfoResponseDto> response = repositoryDtoList.stream()
                .map(
                        repositoryDto -> RepositoryInfoResponseDto.builder()
                                .reposName(repositoryDto.getName())
                                .ownerLogin(repositoryDto.getOwner().getLogin())
                                .branches(listReposBranches(username, repositoryDto.getName()))
                                .build())
                .toList();

        return Uni.createFrom().item(response);
    }

    public List<BranchDto> listReposBranches(String owner, String repos) {
        return webClient.get()
                .uri(LIST_BRANCHES_BY_REPOS_PATH, owner, repos)
                .retrieve()
                .bodyToFlux(BranchDto.class)
                .collectList()
                .block();
    }
}