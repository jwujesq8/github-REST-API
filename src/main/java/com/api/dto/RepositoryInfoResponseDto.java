package com.api.dto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RepositoryInfoResponseDto {
    private String reposName;
    private String ownerLogin;
    private List<BranchDto> branches;
}
