package com.jy.rock.controller;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.text.MessageFormat;
import java.util.List;

/**
 * @author hzhou
 */
@Slf4j
@ControllerAdvice
public class ExceptionHandler extends ResponseEntityExceptionHandler {

    @NotNull
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NotNull MethodArgumentNotValidException ex,
                                                                  @NotNull HttpHeaders headers,
                                                                  @NotNull HttpStatus status,
                                                                  @NotNull WebRequest request) {
        log.error("", ex);
        BindingResult bindingResult = ex.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        StringBuilder sb = new StringBuilder(fieldErrors.size());
        sb.append("validation error.");
        for (FieldError fieldError : fieldErrors) {
            String field = fieldError.getField();
            String message = fieldError.getDefaultMessage();
            sb.append(MessageFormat.format("[{0}]: [{1}]; ", field, message));
        }
        return new ResponseEntity<>(sb.toString(), headers, status);
    }
}
