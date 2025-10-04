package com.shivam.cloudlet_api.exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.shivam.cloudlet_api.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @Value("${app.debug:false}")
    private boolean debugMode;

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(
            CustomException ex, WebRequest request) {

        ErrorResponse.ErrorResponseBuilder responseBuilder = ErrorResponse.builder()
                .status(ex.getStatus().value())
                .error(ex.getStatus().getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .timestamp(LocalDateTime.now());

        if (ex.getRootCause() != null) {
            responseBuilder.rootCause(ex.getRootCause().getMessage());

            if (debugMode) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ex.getRootCause().printStackTrace(pw);
                responseBuilder.stackTrace(sw.toString());
            }
        }

        return ResponseEntity
                .status(ex.getStatus())
                .body(responseBuilder.build());
    }

    // If you prefer Response format instead of ErrorResponse, use this:
    /*
     * @ExceptionHandler(CustomException.class)
     * public ResponseEntity<Response> handleCustomException(CustomException ex) {
     * return ResponseEntity
     * .status(ex.getStatus())
     * .body(new Response(ex.getStatus(), ex.getMessage(), null));
     * }
     */
}
