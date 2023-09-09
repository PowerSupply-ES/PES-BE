package com.powersupply.PES.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class ExceptionManger {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<String> appExceptionHandler(AppException e, HttpServletResponse response) {
        ErrorCode errorCode = e.getErrorCode();
        String message = e.getMessage();

        response.setStatus(errorCode.getHttpStatus().value()); // 상태 코드 설정
        response.setHeader("Message", message);  // 커스텀 헤더 설정

        return new ResponseEntity<>(message, errorCode.getHttpStatus());
    }
}
