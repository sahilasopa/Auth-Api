package com.sahilasopa.chat.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.util.HashMap;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private String message;
    private int statusCode;

    public JSONObject getResponse() {
        final HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("message", message);
        hashMap.put("status", String.valueOf(statusCode));
        return new JSONObject(hashMap);
    }
}
