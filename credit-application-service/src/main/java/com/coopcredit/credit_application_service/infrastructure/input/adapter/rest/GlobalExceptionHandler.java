package com.coopcredit.credit_application_service.infrastructure.input.adapter.rest;

import com.coopcredit.credit_application_service.domain.exception.DomainException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ProblemDetail> handleDomainException(DomainException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setType(URI.create("https://example.com/problem/business-error"));
        problem.setTitle("Business Rule Violation");
        problem.setDetail(ex.getMessage()); // Spanish message expected in DomainException
        problem.setInstance(URI.create(request.getRequestURI()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        String detail = ex.getBindingResult().getAllErrors()
                .stream()
                .map(err -> {
                    if (err instanceof FieldError fe) {
                        return fe.getField() + ": " + fe.getDefaultMessage();
                    }
                    return err.getDefaultMessage();
                })
                .collect(Collectors.joining("; "));

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setType(URI.create("https://example.com/problem/validation"));
        problem.setTitle("Validation Failed");
        problem.setDetail(detail); // in Spanish because validation messages are Spanish
        problem.setInstance(URI.create(request.getRequestURI()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneric(Exception ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setType(URI.create("https://example.com/problem/internal"));
        problem.setTitle("Internal Server Error");
        ex.printStackTrace(); // DEBUG: Print stack trace to stderr
        problem.setDetail("Error interno en el servidor: " + ex.getMessage()); // OPTIONAL: Include message in response

        problem.setInstance(URI.create(request.getRequestURI()));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }
}
