package com.sahilasopa.chat.auth;

import lombok.Data;

@Data
public class AuthenticationRequest {
    private String username;
    private String password;
}
