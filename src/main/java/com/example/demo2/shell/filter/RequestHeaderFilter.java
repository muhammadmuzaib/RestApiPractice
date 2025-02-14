package com.example.demo2.shell.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Component
public class RequestHeaderFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String uri = httpRequest.getRequestURI();
        if (uri.startsWith("/swagger-ui") || uri.startsWith("/v3/api-docs")) {
            chain.doFilter(request, response);
            return;
        }

        // Accept
        String acceptHeader = httpRequest.getHeader(HttpHeaders.ACCEPT);
        if (acceptHeader == null || !acceptHeader.equalsIgnoreCase("application/json")) {
            sendError(httpResponse, HttpStatus.NOT_ACCEPTABLE, "Accept header must be application/json");
            return;
        }

        // Content-Type
        String method = httpRequest.getMethod();
        if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method) || "PATCH".equalsIgnoreCase(method)) {
            String contentType = httpRequest.getHeader(HttpHeaders.CONTENT_TYPE);
            if (contentType == null || !contentType.equalsIgnoreCase("application/json")) {
                sendError(httpResponse, HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Content-Type must be application/json");
                return;
            }
        }

        // corelation id
        String correlationId = httpRequest.getHeader("correlationId");
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString();
        }

        httpRequest.setAttribute("correlationId", correlationId);

        //date
        httpResponse.setHeader(HttpHeaders.DATE, Instant.now().toString());

        httpResponse.setHeader("correlationId", correlationId);

        chain.doFilter(request, response);
    }

    private void sendError(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.getWriter().write(message);
        response.getWriter().flush();
    }
}