package com.powersupply.PES.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    INVALID_INPUT(HttpStatus.UNAUTHORIZED,""),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND,""),
    NOT_FOUND(HttpStatus.NOT_FOUND,""),
    FORBIDDEN(HttpStatus.FORBIDDEN,""),
    BAD_REQUEST(HttpStatus.BAD_REQUEST,"");

    private HttpStatus httpStatus;
    private String message;
}
