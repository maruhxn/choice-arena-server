package com.maruhxn.choicearena.global.error.exception;

import com.maruhxn.choicearena.global.error.ErrorCode;
import org.springframework.http.HttpStatus;

public class ForbiddenException extends GlobalException {
    public ForbiddenException(ErrorCode code) {
        super(code, HttpStatus.FORBIDDEN);
    }

    public ForbiddenException(ErrorCode code, String message) {
        super(code, HttpStatus.FORBIDDEN, message);
    }
}
