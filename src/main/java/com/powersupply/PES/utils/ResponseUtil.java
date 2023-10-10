package com.powersupply.PES.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResponseUtil {
    // 성공 메시지 출력
    public static ResponseEntity<?> successResponse(String msg) {
        return createStatusResponse(msg, HttpStatus.OK);
    }

    // 최종 성공 메시지 출력
    private static ResponseEntity<?> createStatusResponse(String message, HttpStatus status) {
        Map<String, String> body = new HashMap<>();
        body.put("message", message);

        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }
}
