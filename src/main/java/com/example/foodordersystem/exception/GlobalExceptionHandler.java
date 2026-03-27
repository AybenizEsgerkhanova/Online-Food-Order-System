package com.example.foodordersystem.exception;

import com.example.foodordersystem.model.dto.response.ErrorResponse;
import com.example.foodordersystem.util.MessageUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex, WebRequest request) {
        log.error("User not found: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .errorCode("USER_NOT_FOUND")
                .status(HttpStatus.NOT_FOUND.value())
                .path(getRequestPath(request))
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MenuItemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMenuItemNotFound(MenuItemNotFoundException ex, WebRequest request) {
        log.error("Menu item not found: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .errorCode("MENU_ITEM_NOT_FOUND")
                .status(HttpStatus.NOT_FOUND.value())
                .path(getRequestPath(request))
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFound(OrderNotFoundException ex, WebRequest request) {
        log.error("Order not found: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .errorCode("ORDER_NOT_FOUND")
                .status(HttpStatus.NOT_FOUND.value())
                .path(getRequestPath(request))
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidOrderStatusException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOrderStatus(InvalidOrderStatusException ex, WebRequest request) {
        log.error("Invalid order status: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .errorCode("INVALID_ORDER_STATUS")
                .status(HttpStatus.BAD_REQUEST.value())
                .path(getRequestPath(request))
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, WebRequest request) {
        log.error("Access denied: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(MessageUtil.get("error.access.denied"))
                .errorCode("ACCESS_DENIED")
                .status(HttpStatus.FORBIDDEN.value())
                .path(getRequestPath(request))
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex, WebRequest request) {
        log.error("Bad credentials: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(MessageUtil.get("error.bad.credentials"))
                .errorCode("INVALID_CREDENTIALS")
                .status(HttpStatus.UNAUTHORIZED.value())
                .path(getRequestPath(request))
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handleExpiredJwt(ExpiredJwtException ex, WebRequest request) {
        log.error("JWT token expired: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(MessageUtil.get("error.token.expired"))
                .errorCode("TOKEN_EXPIRED")
                .status(HttpStatus.UNAUTHORIZED.value())
                .path(getRequestPath(request))
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ErrorResponse> handleMalformedJwt(MalformedJwtException ex, WebRequest request) {
        log.error("Malformed JWT token: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(MessageUtil.get("error.token.malformed"))
                .errorCode("INVALID_TOKEN")
                .status(HttpStatus.UNAUTHORIZED.value())
                .path(getRequestPath(request))
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        log.error("Validation error: {}", ex.getMessage());
        List<ErrorResponse.ValidationError> validationErrors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.add(new ErrorResponse.ValidationError(fieldName, errorMessage));
        });
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(MessageUtil.get("error.validation"))
                .errorCode("VALIDATION_ERROR")
                .status(HttpStatus.BAD_REQUEST.value())
                .path(getRequestPath(request))
                .timestamp(LocalDateTime.now())
                .validationErrors(validationErrors)
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, WebRequest request) {
        log.error("HTTP message not readable: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(MessageUtil.get("error.invalid.json"))
                .errorCode("INVALID_JSON")
                .status(HttpStatus.BAD_REQUEST.value())
                .path(getRequestPath(request))
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
        log.error("Type mismatch: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(MessageUtil.get("error.type.mismatch", ex.getName(), ex.getValue()))
                .errorCode("TYPE_MISMATCH")
                .status(HttpStatus.BAD_REQUEST.value())
                .path(getRequestPath(request))
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLocking(ObjectOptimisticLockingFailureException ex, WebRequest request) {
        log.warn("Optimistic locking conflict on {}: {}", ex.getPersistentClassName(), ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("The resource was modified by another request. Please retry.")
                .errorCode("CONCURRENT_MODIFICATION")
                .status(HttpStatus.CONFLICT.value())
                .path(getRequestPath(request))
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex, WebRequest request) {
        log.error("Runtime exception: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(MessageUtil.get("error.internal"))
                .errorCode("INTERNAL_ERROR")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .path(getRequestPath(request))
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(MessageUtil.get("error.unexpected"))
                .errorCode("UNEXPECTED_ERROR")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .path(getRequestPath(request))
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String getRequestPath(WebRequest request) {
        return request.getDescription(false).substring(4);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(
            DataIntegrityViolationException ex, WebRequest request) {
        log.error("Data integrity violation: {}", ex.getMessage());

        String message = "Məlumat artıq mövcuddur";

        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("username")) {
                message = "Bu istifadəçi adı artıq mövcuddur";
            } else if (ex.getMessage().contains("email")) {
                message = "Bu email artıq mövcuddur";
            }
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(message)
                .errorCode("DUPLICATE_ENTRY")
                .status(HttpStatus.CONFLICT.value())
                .path(getRequestPath(request))
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }
}
