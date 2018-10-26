package com.mogo.exception;

import com.mogo.exception.model.BuildPackNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {BuildPackNotFoundException.class})
    protected ResponseEntity<Object> handleResourceNotFound(RuntimeException ex, WebRequest request) {
        String error = "The resource is not found";
        return buildResponseEntity(new ApiError(HttpStatus.NOT_FOUND,
                error,
                ex.getMessage(),
                request.getDescription(true),
                new Date()));
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}
