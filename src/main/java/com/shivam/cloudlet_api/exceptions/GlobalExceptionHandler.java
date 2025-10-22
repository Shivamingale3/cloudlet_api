package com.shivam.cloudlet_api.exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.shivam.cloudlet_api.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @Value("${app.debug:false}")
        private boolean debugMode;

        // Custom exception
        @ExceptionHandler(CustomException.class)
        public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex, WebRequest request) {
                return buildErrorResponse(ex.getStatus(), ex.getMessage(), ex.getRootCause(), request);
        }

        // 404 errors for Spring Boot 3.x
        @ExceptionHandler(NoResourceFoundException.class)
        public ResponseEntity<ErrorResponse> handleNoResourceFound(NoResourceFoundException ex, WebRequest request) {
                String message = "The requested resource was not found: " + ex.getResourcePath();
                return buildErrorResponse(HttpStatus.NOT_FOUND, message, ex, request);
        }

        // 404 errors for Spring Boot 2.x
        @ExceptionHandler(NoHandlerFoundException.class)
        public ResponseEntity<ErrorResponse> handleNoHandlerFound(NoHandlerFoundException ex, WebRequest request) {
                String message = "No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL();
                return buildErrorResponse(HttpStatus.NOT_FOUND, message, ex, request);
        }

        // Handle invalid method arguments
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex,
                        WebRequest request) {
                String message = ex.getBindingResult().getFieldErrors()
                                .stream()
                                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                                .reduce("", (s1, s2) -> s1.isEmpty() ? s2 : s1 + "; " + s2);
                return buildErrorResponse(HttpStatus.BAD_REQUEST, message, ex, request);
        }

        // Handle database-related exceptions
        @ExceptionHandler({ DataIntegrityViolationException.class, DataAccessException.class })
        public ResponseEntity<ErrorResponse> handleDatabaseExceptions(Exception ex, WebRequest request) {
                String message = "Database error occurred";
                Throwable cause = ex.getCause();

                // Handle PostgreSQL specific exceptions
                if (cause instanceof PSQLException) {
                        PSQLException psqlEx = (PSQLException) cause;
                        message = parsePSQLException(psqlEx);
                } else if (ex instanceof DataIntegrityViolationException) {
                        message = "Data integrity violation: " + ex.getMessage();
                }

                return buildErrorResponse(HttpStatus.CONFLICT, message, ex, request);
        }

        // Illegal argument or illegal state
        @ExceptionHandler({ IllegalArgumentException.class, IllegalStateException.class })
        public ResponseEntity<ErrorResponse> handleIllegalArgs(Exception ex, WebRequest request) {
                return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), ex, request);
        }

        // Catch-all for any unhandled exceptions
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {
                return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", ex,
                                request);
        }

        // Common method to build ErrorResponse
        private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message, Throwable ex,
                        WebRequest request) {
                ErrorResponse.ErrorResponseBuilder builder = ErrorResponse.builder()
                                .status(status.value())
                                .error(status.getReasonPhrase())
                                .message(message)
                                .path(request.getDescription(false).replace("uri=", ""))
                                .timestamp(LocalDateTime.now());

                if (debugMode && ex != null) {
                        builder.rootCause(ex.getMessage());
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        ex.printStackTrace(pw);
                        builder.stackTrace(sw.toString());
                }

                return ResponseEntity.status(status).body(builder.build());
        }

        // Helper method to extract meaningful messages from PSQLException
        private String parsePSQLException(PSQLException ex) {
                String msg = ex.getServerErrorMessage() != null ? ex.getServerErrorMessage().getMessage()
                                : ex.getMessage();
                // Optionally, extract constraint name or column
                if (msg != null && msg.contains("violates")) {
                        return msg;
                }
                return "PostgreSQL error: " + msg;
        }
}
