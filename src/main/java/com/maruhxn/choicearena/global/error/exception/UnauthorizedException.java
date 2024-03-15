package com.maruhxn.choicearena.global.error.exception;

import com.maruhxn.choicearena.global.error.ErrorCode;
import org.springframework.http.HttpStatus;

public class UnauthorizedException extends GlobalException {
    public UnauthorizedException(ErrorCode code) {
        super(code, HttpStatus.UNAUTHORIZED);
    }

    public UnauthorizedException(ErrorCode code, String message) {
        super(code, HttpStatus.UNAUTHORIZED, message);
    }
}
