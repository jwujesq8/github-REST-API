package com.api.service;

import com.api.dto.BranchDto;
import com.api.dto.RepositoryInfoResponseDto;
import io.smallrye.mutiny.Uni;

import java.util.List;

public interface GitHubService {

    Uni<List<RepositoryInfoResponseDto>> listNonForkReposByUsername(String username);
    List<BranchDto> listReposBranches(String owner, String repos);
}
