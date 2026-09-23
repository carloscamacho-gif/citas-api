package com.fcv.citas.shared.web;

import com.fcv.citas.auth.domain.exception.DocumentAlreadyUsedException;
import com.fcv.citas.auth.domain.exception.EmailAlreadyUsedException;
import com.fcv.citas.auth.domain.exception.InvalidCredentialsException;
import com.fcv.citas.auth.domain.exception.InvalidRefreshTokenException;
import com.fcv.citas.auth.domain.exception.InvalidInsurancePlanException;
import com.fcv.citas.catalog.domain.exception.InvalidSpecialtyDurationException;
import com.fcv.citas.catalog.domain.exception.SpecialtyCodeAlreadyUsedException;
import com.fcv.citas.catalog.domain.exception.SpecialtyNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/** Traduce excepciones de dominio/validación a respuestas HTTP consistentes (RF-20). */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyUsedException.class)
    public ResponseEntity<ApiError> handleEmailAlreadyUsed(EmailAlreadyUsedException ex) {
        return build(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(DocumentAlreadyUsedException.class)
    public ResponseEntity<ApiError> handleDocumentAlreadyUsed(DocumentAlreadyUsedException ex) {
        return build(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiError> handleInvalidCredentials(InvalidCredentialsException ex) {
        return build(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(InvalidInsurancePlanException.class)
    public ResponseEntity<ApiError> handleInvalidInsurancePlan(InvalidInsurancePlanException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ApiError> handleInvalidRefreshToken(InvalidRefreshTokenException ex) {
        return build(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(SpecialtyCodeAlreadyUsedException.class)
    public ResponseEntity<ApiError> handleSpecialtyCodeAlreadyUsed(SpecialtyCodeAlreadyUsedException ex) {
        return build(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(SpecialtyNotFoundException.class)
    public ResponseEntity<ApiError> handleSpecialtyNotFound(SpecialtyNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(InvalidSpecialtyDurationException.class)
    public ResponseEntity<ApiError> handleInvalidSpecialtyDuration(InvalidSpecialtyDurationException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> "%s: %s".formatted(error.getField(), error.getDefaultMessage()))
                .toList();
        ApiError body = ApiError.of(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "La solicitud contiene datos inválidos", details);
        return ResponseEntity.badRequest().body(body);
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(ApiError.of(status.value(), status.getReasonPhrase(), message));
    }
}
