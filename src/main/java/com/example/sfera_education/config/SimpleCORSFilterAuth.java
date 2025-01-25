package com.example.sfera_education.config;


import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SimpleCORSFilterAuth extends OncePerRequestFilter {

    private static final String errorObject =
            "{" +
                    "\"message\": \"Internal Server Error\"," +
                    "\"status\": 500, " +
                    "\"timestamp\": \"" + LocalDateTime.now(ZoneId.of("Asia/Tashkent")) + "\"" +
                    "}";


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "authorization, Content-Type, X-XSRF-TOKEN");

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            try {
                if (!response.isCommitted()) {
                    filterChain.doFilter(request, response);
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("application/json");
                response.getWriter().write(errorObject);
                response.getWriter().flush();
            }
        }
    }
}

