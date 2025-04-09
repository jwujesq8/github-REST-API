package com.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BranchDto {
    private String name;
    @JsonProperty("commit")
    private CommitDto lastCommit;
}
