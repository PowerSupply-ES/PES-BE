package com.powersupply.PES.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {
    public static ResponseEntity<String> successResponse(String msg) {
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"message\" : \"" + msg + "\"}");
    }
}
