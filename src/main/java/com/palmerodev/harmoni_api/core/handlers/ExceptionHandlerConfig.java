package com.palmerodev.harmoni_api.core.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.palmerodev.harmoni_api.core.exceptions.PrimaryException;
import com.palmerodev.harmoni_api.core.exceptions.UserAlreadyExistException;
import com.palmerodev.harmoni_api.core.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
public class ExceptionHandlerConfig {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getMessage(ex));
    }

    private static String getMessage(PrimaryException ex) {
        var className = ex.getStackTrace()[0].getClassName();
        var methodName = "Timestamp : " + ex.getStackTrace()[0].getMethodName();
        var data = "AdditionalData : " + List.of(System.currentTimeMillis() + "", ex.getJsonAdditionalData());
        try {
            return className + "::" + methodName + " -> " + ex.getMessage() + " --- " + new ObjectMapper().writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<String> handleUserAlreadyExist(UserAlreadyExistException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(getMessage(ex));
    }

}
