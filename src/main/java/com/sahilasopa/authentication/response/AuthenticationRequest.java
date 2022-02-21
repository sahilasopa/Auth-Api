package com.sahilasopa.authentication.response;

import lombok.Data;

@Data
public class AuthenticationRequest {
    private String username;
    private String password;
}
