package com.proyecto.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenValidationResponse {

    private Boolean valid;
    private Long userId;
    private String username;
    private String email;
    private Set<String> roles;
}
