package com.example.sfera_education.security;

public class SecurityConstants {
    public static final String[] WHITE_LIST = {
            "/auth/login",
            "/auth/register",
            "/file/**",
            "/category/list",
            "notification/contact",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/swagger-config",
            "/swagger-resources/**",
            "/webjars/**",
    };
}
