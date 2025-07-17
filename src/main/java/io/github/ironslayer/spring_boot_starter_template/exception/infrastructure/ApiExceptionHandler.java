package io.github.ironslayer.spring_boot_starter_template.exception.infrastructure;


import io.github.ironslayer.spring_boot_starter_template.exception.domain.BadRequestException;
import io.github.ironslayer.spring_boot_starter_template.exception.domain.ErrorMessage;
import io.github.ironslayer.spring_boot_starter_template.exception.domain.InvalidTokenException;
import io.github.ironslayer.spring_boot_starter_template.user.domain.exception.UserNotFoundException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.security.SignatureException;


@RestControllerAdvice
public class ApiExceptionHandler {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({
            UserNotFoundException.class,
            TypeNotPresentException.class,
            EntityNotFoundException.class,
    })
    @ResponseBody
    public ErrorMessage notFound(HttpServletRequest request, Exception exception) {
        return new ErrorMessage(exception, request.getRequestURI());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            BadRequestException.class,
            InvalidTokenException.class,
            HttpRequestMethodNotSupportedException.class,
            MethodArgumentNotValidException.class,
            MissingRequestHeaderException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class,
    })
    @ResponseBody
    public ErrorMessage badRequest(HttpServletRequest request, Exception exception) {//, MethodArgumentNotValidException ex

        return new ErrorMessage(exception, request.getRequestURI());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({
            ExpiredJwtException.class,
            MalformedJwtException.class,
            SignatureException.class
    })
    @ResponseBody
    public ErrorMessage unauthorized(HttpServletRequest request, Exception exception) {
        return new ErrorMessage(exception, request.getRequestURI());
    }
}
