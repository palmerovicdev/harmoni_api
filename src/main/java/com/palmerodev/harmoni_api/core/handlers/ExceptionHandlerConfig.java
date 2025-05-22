package com.palmerodev.harmoni_api.core.handlers;

import com.palmerodev.harmoni_api.core.exceptions.UserAlreadyExistException;
import com.palmerodev.harmoni_api.core.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerConfig {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getMessage(ex));
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<String> handleUserAlreadyExist(UserAlreadyExistException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(getMessage(ex));
    }

    private static String getMessage(RuntimeException ex) {
        var className = ex.getStackTrace()[0].getClassName();
        var methodName = ex.getStackTrace()[0].getMethodName();
        return className + "::" + methodName + " -> " + ex.getMessage();
    }

}
