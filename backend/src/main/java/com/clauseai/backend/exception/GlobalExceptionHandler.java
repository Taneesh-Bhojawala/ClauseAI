package com.clauseai.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler
{
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> gobalExceptionHandler(Exception ex, WebRequest request)
    {
        Map<String,Object> body = new HashMap<>();
        body.put("timestamp", new Date());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> handleMaxSizeException(MaxUploadSizeExceededException ex)
    {
        Map<String,Object> body = new HashMap<>();
        body.put("timestamp", new Date());
        body.put("status", HttpStatus.EXPECTATION_FAILED.value());
        body.put("error", "File Too Large!");
        body.put("message", "Please upload a file smaller than 10MB");

        return new ResponseEntity<>(body, HttpStatus.EXPECTATION_FAILED);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex)
    {
        Map<String,Object> body = new HashMap<>();
        body.put("timestamp", new Date());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}