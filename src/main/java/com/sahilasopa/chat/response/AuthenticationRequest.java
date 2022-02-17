package com.sahilasopa.chat.response;

import lombok.Data;

@Data
public class AuthenticationRequest {
    private String username;
    private String password;
}
