package com.maruhxn.choicearena.global.error.handler;

import com.maruhxn.choicearena.global.common.dto.response.ErrorResponse;
import com.maruhxn.choicearena.global.error.ErrorCode;
import com.maruhxn.choicearena.global.error.exception.GlobalException;
import com.maruhxn.choicearena.global.error.exception.InternalServerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.NoHandlerFoundException;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[핸들러] - GlobalExceptionHandler")
class GlobalExceptionHandlerTest {
    private GlobalExceptionHandler sut;

    @BeforeEach
    void setUp() {
        sut = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("404 에러")
    void handle404() {
        // Given
        NoHandlerFoundException e = new NoHandlerFoundException(null, null, null);

        // When
        ResponseEntity<Object> response = sut.handle404(e);

        // Then
        assertThat(response)
                .hasFieldOrPropertyWithValue("body", ErrorResponse.of(ErrorCode.NOT_FOUND_RESOURCE))
                .hasFieldOrPropertyWithValue("headers", HttpHeaders.EMPTY)
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("데이터베이스 유니크 제약조건 에러")
    void dataIntegrityViolation() {
        // Given
        DataIntegrityViolationException e = new DataIntegrityViolationException("DataIntegrityViolationException");

        // When
        ResponseEntity<Object> response = sut.dataIntegrityViolation(e);

        // Then
        assertThat(response)
                .hasFieldOrPropertyWithValue("body", ErrorResponse.of(ErrorCode.EXISTING_RESOURCE))
                .hasFieldOrPropertyWithValue("headers", HttpHeaders.EMPTY)
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    void validationFail() {
        // Given
        MethodArgumentNotValidException e = new MethodArgumentNotValidException(null, new BeanPropertyBindingResult(null, null));

        // When
        ResponseEntity<Object> response = sut.validationFail(e);
        // Then
        assertThat(response)
                .hasFieldOrPropertyWithValue("body", ErrorResponse.validationError(e.getBindingResult()))
                .hasFieldOrPropertyWithValue("headers", HttpHeaders.EMPTY)
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.BAD_REQUEST);
    }

    @DisplayName("비즈니스 에러")
    @Test
    void globalException() {
        // Given
        GlobalException e = new InternalServerException(ErrorCode.INTERNAL_ERROR);

        // When
        ResponseEntity<Object> response = sut.globalException(e);

        // Then
        assertThat(response)
                .hasFieldOrPropertyWithValue("body", ErrorResponse.of(ErrorCode.INTERNAL_ERROR))
                .hasFieldOrPropertyWithValue("headers", HttpHeaders.EMPTY)
                .hasFieldOrPropertyWithValue("statusCode", e.getHttpStatus());
    }

    @Test
    @DisplayName("예상치 못한 에러")
    void exception() {
        // Given
        RuntimeException e = new RuntimeException("RuntimeException");

        // When
        ResponseEntity<Object> response = sut.exception(e);

        // Then
        assertThat(response)
                .hasFieldOrPropertyWithValue("body", ErrorResponse.of(ErrorCode.INTERNAL_ERROR, e.getMessage()))
                .hasFieldOrPropertyWithValue("headers", HttpHeaders.EMPTY)
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}