package ru.practicum.shareit.exeption;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handlerValidationException(final ValidationException e) {
        log.warn("409 {}", e.getMessage());
        return new ErrorResponse("Validation error 409", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlerNotFoundException(final ObjectNotFoundException e) {
        log.warn("404 {}", e.getMessage());
        return new ErrorResponse("Object not found 404", e.getMessage());
    }
}
