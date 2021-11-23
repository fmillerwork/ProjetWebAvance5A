package com.example.AuthService.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class AuthExceptionController {

    @ResponseBody
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String userNotFoundHandler(UserNotFoundException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    String userExistsHandler(UserAlreadyExistsException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(TokenNotValidException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    String tokenNotValidHandler(TokenNotValidException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(PasswordIncorrectException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    String passwordIncorrectHandler(PasswordIncorrectException ex) {
        return ex.getMessage();
    }
}
