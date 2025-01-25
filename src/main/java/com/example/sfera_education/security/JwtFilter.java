package com.example.sfera_education.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.ErrorMessage;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final UserDetailsService userDetailsService;
    public String sessionToken;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");

        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            sessionToken = token;
            try {

                String phoneNumberFromToken = jwtProvider.getPhoneNumberFromToken(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(phoneNumberFromToken);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null,
                        userDetails.getAuthorities()
                );
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } catch (ExpiredJwtException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                ErrorMessage errorMessage = new ErrorMessage("Jwt expired");
                logger.error(errorMessage.getMessage() + "  " + errorMessage.getId());
                new ObjectMapper().writeValue(response.getWriter(), errorMessage);
                return;
            } catch (SignatureException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                ErrorMessage errorMessage = new ErrorMessage("Jwt invalid");
                logger.error(errorMessage.getMessage() + "  " + errorMessage.getId());
                new ObjectMapper().writeValue(response.getWriter(), errorMessage);
                return;
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                ErrorMessage errorMessage = new ErrorMessage(e.getMessage());
                logger.error(errorMessage.getMessage() + "  " + errorMessage.getId());
                new ObjectMapper().writeValue(response.getWriter(), errorMessage);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
