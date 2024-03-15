package com.maruhxn.choicearena.global.error.exception;

import com.maruhxn.choicearena.global.error.ErrorCode;
import org.springframework.http.HttpStatus;

public abstract class GlobalException extends RuntimeException {
    private final ErrorCode code;
    private final HttpStatus httpStatus;

    protected GlobalException(ErrorCode code, HttpStatus httpStatus) {
        super(code.getMessage());
        this.code = code;
        this.httpStatus = httpStatus;
    }

    protected GlobalException(ErrorCode code, HttpStatus httpStatus, String message) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }
}
