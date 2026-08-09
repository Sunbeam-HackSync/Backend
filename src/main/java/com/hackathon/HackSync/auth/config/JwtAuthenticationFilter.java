package com.hackathon.HackSync.auth.config;

import com.hackathon.HackSync.auth.service.JWTService;
import com.hackathon.HackSync.auth.service.UserService;
import com.hackathon.HackSync.utils.exception.UserBannedException;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final HandlerExceptionResolver handlerExceptionResolver;
    private final JWTService jwtService;
    private final UserService userService;

    public JwtAuthenticationFilter(HandlerExceptionResolver handlerExceptionResolver, JWTService jwtService, UserService userService) {
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!authHeader.startsWith("Bearer ")) {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid Authorization header");
            return;
        }

        try {

            final String jwt = authHeader.substring(7).trim();

            if (jwt.isBlank()) {
                response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Invalid JWT token");
                return;
            }

            final String userEmail = jwtService.extractUserEmail(jwt);

            Authentication authentication = SecurityContextHolder
                    .getContext()
                    .getAuthentication();

            if (userEmail != null && authentication == null) {

                UserDetails userDetails = userService.loadUserByUsername(userEmail);

                if (!userDetails.isAccountNonLocked()) {
                    throw new UserBannedException("Your account has been banned. You have been logged out.");
                }

                if (!jwtService.isTokenValid(jwt, userDetails)) {

                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired JWT token");

                    return;
                }

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authToken);
            }

            filterChain.doFilter(request, response);

        } catch (Exception e) {

            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }
}