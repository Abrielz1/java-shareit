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
    public ErrorResponse handlerValidationException(final ValidationException exception) {
        log.warn("409 {}", exception.getMessage());
        return new ErrorResponse("Validation error 409", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlerNotFoundException(final ObjectNotFoundException exception) {
        log.warn("404 {}", exception.getMessage());
        return new ErrorResponse("Object not found 404", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlerBadRequest(final BadRequestException exception) {
        log.warn("400 {}", exception.getMessage(), exception);
        return new ErrorResponse("Object not available 400 ", exception.getMessage());
    }

    @ExceptionHandler(UnsupportedStateException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handlerUnsupportedState(final UnsupportedStateException exception) {
        log.warn("500 {}", exception.getMessage(), exception);
        return new ErrorResponse(exception.getMessage(), exception.getMessage());
    }
}
