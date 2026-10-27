package com.scaler.productcatalogservice.security;

import com.scaler.productcatalogservice.clients.AuthClient;
import com.scaler.productcatalogservice.dtos.AuthUserDto;
import com.scaler.productcatalogservice.exceptions.AuthServiceUnavailableException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
public class TokenCheckFilter extends OncePerRequestFilter {

    private final AuthClient authClient;
    private final HandlerExceptionResolver exceptionResolver;

    public TokenCheckFilter(AuthClient authClient,
                            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver) {
        this.authClient = authClient;
        this.exceptionResolver = exceptionResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (request.getMethod().equals("GET")) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        if (header == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        AuthUserDto user;
        try {
            user = authClient.validateToken(header);
        } catch (HttpClientErrorException.Unauthorized e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        } catch (RestClientException e) {
            // hand the problem to our @RestControllerAdvice, so the caller gets a clear 503
            exceptionResolver.resolveException(request, response, null, new AuthServiceUnavailableException(e));
            return;
        }

        if (request.getMethod().equals("DELETE") && !"ADMIN".equals(user.getRole())) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return;
        }

        filterChain.doFilter(request, response);
    }
}
