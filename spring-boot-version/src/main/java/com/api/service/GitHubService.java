package com.api.service;

import com.api.dto.BranchDto;
import com.api.dto.RepositoryInfoResponseDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface GitHubService {

    Mono<List<RepositoryInfoResponseDto>> listNonForkReposByUsername(String username);
    Mono<List<BranchDto>> listReposBranches(String owner, String repos);
}
