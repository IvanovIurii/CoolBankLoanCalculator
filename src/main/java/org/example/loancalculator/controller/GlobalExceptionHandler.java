package org.example.loancalculator.controller;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.Optional;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ValidationErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> {
                    String errorMessage = error.getDefaultMessage();
                    Optional<String> fieldName = getFieldNameFromError(error);
                    return fieldName.isPresent()
                            ? String.format("%s: %s", fieldName.get(), errorMessage)
                            : errorMessage;
                })
                .toList();

        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                "Validation failed",
                errors,
                HttpStatus.BAD_REQUEST
        );
        return ResponseEntity.badRequest().body(errorResponse);
    }

    private Optional<String> getFieldNameFromError(ObjectError error) {
        Object[] arguments = error.getArguments();
        if (arguments != null && arguments.length > 0) {
            var argument = (DefaultMessageSourceResolvable) arguments[0];
            return Optional.ofNullable(argument.getDefaultMessage());
        }
        return Optional.empty();
    }

    private record ValidationErrorResponse(String title, List<String> validationErrors, HttpStatus status) {
    }
}
