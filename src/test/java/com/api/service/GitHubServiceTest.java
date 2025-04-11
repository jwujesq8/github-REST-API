package com.api.service;

import com.api.dto.*;
import com.api.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class GitHubServiceTest {
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpecNonFork;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpecBranches;
    @Mock
    private WebClient.ResponseSpec responseSpecNonFork;
    @Mock
    private WebClient.ResponseSpec responseSpecBranches;
    @Mock
    private WebClient webClient;
    @InjectMocks
    private GitHubServiceImpl gitHubService;
    private String username;
    private String usernameNotFound;
    private String login;
    private CommitDto commitDto;
    private RepositoryDto repoDto;
    private OwnerDto ownerDto;
    private BranchDto branchDto;
    @Value("${LIST_REPOS_BY_USERNAME_PATH}")
    private String LIST_REPOS_BY_USERNAME_PATH;

    @Value("${LIST_BRANCHES_BY_REPOS_PATH}")
    private String LIST_BRANCHES_BY_REPOS_PATH;

    @BeforeEach
    void setUp(){
        username = "user";
        usernameNotFound = "userNotFound";
        login = "user@gmail.com";
        commitDto = CommitDto.builder()
                .sha("commmitSha")
                .build();
        ownerDto = OwnerDto.builder()
                .login(login)
                .build();
        repoDto = RepositoryDto.builder()
                .name("repoName")
                .fork(false)
                .owner(ownerDto)
                .build();
        branchDto = BranchDto.builder()
                .name("branchName")
                .lastCommit(commitDto)
                .build();

    }

    @Nested
    class testListNonForkReposByUsername{
        @Test
        void testListNonForkReposByUsername_success() {
            when(webClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(eq(LIST_REPOS_BY_USERNAME_PATH), eq("user")))
                    .thenReturn(requestHeadersSpecNonFork);
            when(requestHeadersSpecNonFork.retrieve()).thenReturn(responseSpecNonFork);
            when(responseSpecNonFork.bodyToFlux(RepositoryDto.class)).thenReturn(Flux.just(repoDto));
            when(requestHeadersUriSpec.uri(any(), eq("user"), eq("repoName")))
                    .thenReturn(requestHeadersSpecBranches);
            when(requestHeadersSpecBranches.retrieve()).thenReturn(responseSpecBranches);
            when(responseSpecBranches.bodyToFlux(BranchDto.class)).thenReturn(Flux.just(branchDto));

            Mono<List<RepositoryInfoResponseDto>> result = gitHubService.listNonForkReposByUsername(username);
            List<RepositoryInfoResponseDto> repos = result.block();

            assertNotNull(repos);
            assertEquals(1, repos.size());
            assertEquals("repoName", repos.get(0).getReposName());
            assertEquals(1, repos.get(0).getBranches().size());
            assertEquals("branchName", repos.get(0).getBranches().get(0).getName());
        }

        @Test
        void testListNonForkReposByUsername_userNotFound() {
            when(webClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(eq(LIST_REPOS_BY_USERNAME_PATH), eq(usernameNotFound)))
                    .thenReturn(requestHeadersSpecNonFork);
            when(requestHeadersSpecNonFork.retrieve()).thenReturn(responseSpecNonFork);
            when(responseSpecNonFork.bodyToFlux(RepositoryDto.class))
                    .thenReturn(Flux.error(new UserNotFoundException(usernameNotFound)));

            Mono<List<RepositoryInfoResponseDto>> result = gitHubService.listNonForkReposByUsername(usernameNotFound);

            assertThrows(UserNotFoundException.class, result::block);
        }
    }

    @Test
    void testListReposBranches_success() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(eq(LIST_BRANCHES_BY_REPOS_PATH), eq(username), eq(repoDto.getName())))
                .thenReturn(requestHeadersSpecBranches);
        when(requestHeadersSpecBranches.retrieve()).thenReturn(responseSpecBranches);
        when(responseSpecBranches.bodyToFlux(BranchDto.class)).thenReturn(Flux.just(branchDto));

        Mono<List<BranchDto>> result = gitHubService
                .listReposBranches("user", "repoName");
        List<BranchDto> branches = result.block();

        assertNotNull(branches);
        assertEquals(1, branches.size());
        assertEquals("branchName", branches.get(0).getName());
    }
}
