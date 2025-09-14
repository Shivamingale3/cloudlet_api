package com.shivam.store_api.filters;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // Wrap request/response to read content multiple times
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        long start = System.currentTimeMillis();
        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } catch (Exception ex) {
            log.error("Error during {} {} : {}", request.getMethod(), request.getRequestURI(), ex.getMessage(), ex);
            throw ex;
        } finally {
            long duration = System.currentTimeMillis() - start;

            // Log request
            String reqBody = getRequestBody(wrappedRequest);
            log.info("Incoming {} {}{} | body={} | from={} ",
                    request.getMethod(),
                    request.getRequestURI(),
                    request.getRemoteAddr());

            // Log response
            String respBody = getResponseBody(wrappedResponse);
            log.info("Completed {} {} -> {} ({}ms) | body={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    duration,
                    respBody);

            wrappedResponse.copyBodyToResponse(); // important!
        }
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        byte[] buf = request.getContentAsByteArray();
        if (buf.length == 0)
            return "";
        return new String(buf, 0, buf.length, StandardCharsets.UTF_8);
    }

    private String getResponseBody(ContentCachingResponseWrapper response) {
        byte[] buf = response.getContentAsByteArray();
        if (buf.length == 0)
            return "";
        return new String(buf, 0, buf.length, StandardCharsets.UTF_8);
    }
}
