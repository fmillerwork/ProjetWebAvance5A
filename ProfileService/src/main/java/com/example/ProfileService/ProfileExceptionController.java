package com.example.ProfileService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class ProfileExceptionController {

    @ResponseBody
    @ExceptionHandler(ProfileNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String profileNotFoundHandler(ProfileNotFoundException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(EmailInUseException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    String EmailInUseHandler(EmailInUseException ex) {return ex.getMessage();}


}
