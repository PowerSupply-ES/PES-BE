package com.powersupply.PES.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    USERNAME_DUPLICATED(HttpStatus.CONFLICT, ""),
    INVALID_INPUT(HttpStatus.UNAUTHORIZED,"");

    private HttpStatus httpStatus;
    private String message;
}
