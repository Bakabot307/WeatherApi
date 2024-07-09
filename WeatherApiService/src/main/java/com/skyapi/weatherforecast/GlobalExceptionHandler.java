package com.skyapi.weatherforecast;

import com.skyapi.weatherforecast.location.LocationNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(BadRequestException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorDTO BadRequestException(HttpServletRequest request, Exception ex) {
    ErrorDTO error = new ErrorDTO();

    error.setTimeStamp(new Date());
    error.setStatus(HttpStatus.BAD_REQUEST.value());
    error.addError(ex.getMessage());
    error.setPath(request.getServletPath());
    LOGGER.error(ex.getMessage(), ex);
    return error;
  }

  @ExceptionHandler({LocationNotFoundException.class,GeoLocationException.class})
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ResponseBody
  public ErrorDTO handleLocationNotFoundException(HttpServletRequest request, Exception ex) {
    ErrorDTO error = new ErrorDTO();

    error.setTimeStamp(new Date());
    error.setStatus(HttpStatus.NOT_FOUND.value());
    error.addError(ex.getMessage());
    error.setPath(request.getServletPath());
    LOGGER.error(ex.getMessage(), ex);
    return error;
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public ErrorDTO handleGenericException(HttpServletRequest request, Exception ex) {
    ErrorDTO error = new ErrorDTO();

    error.setTimeStamp(new Date());
    error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
    error.addError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    error.setPath(request.getServletPath());
    LOGGER.error(ex.getMessage(), ex);
    return error;
  }

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorDTO handleConstraintViolationException(HttpServletRequest request, Exception ex) {
    ErrorDTO error = new ErrorDTO();

    ConstraintViolationException cve = (ConstraintViolationException) ex;

    error.setTimeStamp(new Date());
    error.setStatus(HttpStatus.BAD_REQUEST.value());
    error.setPath(request.getServletPath());

    cve.getConstraintViolations().forEach(violation -> {
      error.addError(violation.getPropertyPath() + " : " + violation.getMessage());
    });

    LOGGER.error(ex.getMessage(), ex);
    return error;
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status,
      WebRequest request) {
    ErrorDTO error = new ErrorDTO();

    error.setTimeStamp(new Date());
    error.setStatus(HttpStatus.BAD_REQUEST.value());
    error.setPath(((ServletWebRequest) request).getRequest().getServletPath());

    List<FieldError> fieldErrorList = ex.getBindingResult().getFieldErrors();

    fieldErrorList.forEach(field -> {
      error.addError(field.getDefaultMessage());
    });
    LOGGER.error(ex.getMessage(), ex);
    return new ResponseEntity<>(error, headers, status);
  }
}
