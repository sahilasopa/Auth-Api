package com.sahilasopa.chat.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.util.HashMap;

@Data
@AllArgsConstructor
public class Response {
    private String message;

    public String getResponse() {
        final HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("message", message);
        return String.valueOf(new JSONObject(hashMap));
    }

}
