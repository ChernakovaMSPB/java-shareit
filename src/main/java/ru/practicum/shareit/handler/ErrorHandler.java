package ru.practicum.shareit.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.practicum.shareit.exception.BagRequestException;

@RestControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(BagRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorMessage handleNotValidException(BagRequestException e) {
        return new ErrorMessage(e.getMessage());
    }
}
