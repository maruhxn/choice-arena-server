package com.maruhxn.choicearena.global.error.exception;

import com.maruhxn.choicearena.global.error.ErrorCode;
import org.springframework.http.HttpStatus;

public class BadRequestException extends GlobalException {
    public BadRequestException(ErrorCode code) {
        super(code, HttpStatus.BAD_REQUEST);
    }

    public BadRequestException(ErrorCode code, String message) {
        super(code, HttpStatus.BAD_REQUEST, message);
    }
}
