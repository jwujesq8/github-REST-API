package com.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RepositoryDto {

    private String name;

    @JsonProperty("owner")
    private OwnerDto owner;

    private boolean fork;

}
