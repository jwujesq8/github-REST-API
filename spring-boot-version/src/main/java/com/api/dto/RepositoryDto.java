package com.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class RepositoryDto {

    private String name;

    @JsonProperty("owner")
    private OwnerDto owner;

    private boolean fork;

}
