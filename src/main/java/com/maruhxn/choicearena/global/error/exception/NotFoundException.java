package com.maruhxn.choicearena.global.error.exception;

import com.maruhxn.choicearena.global.error.ErrorCode;
import org.springframework.http.HttpStatus;

public class NotFoundException extends GlobalException {
    public NotFoundException(ErrorCode code) {
        super(code, HttpStatus.NOT_FOUND);
    }

    public NotFoundException(ErrorCode code, String message) {
        super(code, HttpStatus.NOT_FOUND, message);
    }
}
