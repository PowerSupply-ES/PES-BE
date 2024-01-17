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

    public static ResponseEntity<?> noContentResponse(String msg) {
        return createStatusResponse(msg, HttpStatus.NO_CONTENT);
    }

    // 최종 성공 메시지 출력
    private static ResponseEntity<?> createStatusResponse(String message, HttpStatus status) {
//        Map<String, String> body = new HashMap<>();
//        body.put("message", message);
//
//        return ResponseEntity.status(status)
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(body);

        // msg가 빈 문자열인 경우, 본문 없이 상태 코드만 반환
        if (message.isEmpty()) {
            return ResponseEntity.status(status).build();
        } else {
            Map<String, String> body = new HashMap<>();
            body.put("message", message);

            return ResponseEntity.status(status)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body);
        }
    }

    public static ResponseEntity<?> createResponse(String msg) {
        return createStatusResponse(msg, HttpStatus.CREATED);
    }
}
