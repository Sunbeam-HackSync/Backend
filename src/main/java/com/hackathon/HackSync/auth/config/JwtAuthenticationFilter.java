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

    public JwtAuthenticationFilter(
            HandlerExceptionResolver handlerExceptionResolver,
            JWTService jwtService,
            UserService userService) {
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

        /*
         * No Authorization header:
         *
         * Treat the request as anonymous and continue.
         *
         * This is required for public endpoints such as:
         *
         * POST /chat
         */
        if (authHeader == null) {
            filterChain.doFilter(request, response);
            return;
        }

        /*
         * Authorization header exists but is malformed.
         *
         * Example:
         *
         * Authorization: Basic abc123
         */
        if (!authHeader.startsWith("Bearer ")) {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid Authorization header");
            return;
        }

        try {

            /*
             * Extract JWT from:
             *
             * Authorization: Bearer <JWT>
             */
            final String jwt = authHeader.substring(7).trim();

            /*
             * Reject:
             *
             * Authorization: Bearer
             */
            if (jwt.isBlank()) {
                response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Invalid JWT token");
                return;
            }

            /*
             * Extract the user's email from JWT subject.
             */
            final String userEmail = jwtService.extractUserEmail(jwt);

            Authentication authentication = SecurityContextHolder
                    .getContext()
                    .getAuthentication();

            /*
             * Authenticate only when there is no existing
             * authentication in the SecurityContext.
             */
            if (userEmail != null && authentication == null) {

                UserDetails userDetails = userService.loadUserByUsername(userEmail);

                /*
                 * Reject banned users.
                 */
                if (!userDetails.isAccountNonLocked()) {
                    throw new UserBannedException(
                            "Your account has been banned. You have been logged out.");
                }

                /*
                 * JWT exists, so it MUST be valid.
                 *
                 * If invalid/expired, stop the request here.
                 *
                 * This is especially important because
                 * POST /chat is permitAll().
                 */
                if (!jwtService.isTokenValid(jwt, userDetails)) {

                    response.sendError(
                            HttpServletResponse.SC_UNAUTHORIZED,
                            "Invalid or expired JWT token");

                    return;
                }

                /*
                 * JWT is valid.
                 *
                 * Create the authenticated Spring Security principal.
                 */
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());

                /*
                 * Store authenticated user in SecurityContext.
                 */
                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authToken);
            }

            /*
             * JWT was valid and user was authenticated.
             *
             * Continue to the requested endpoint.
             */
            filterChain.doFilter(request, response);

        } catch (Exception e) {

            /*
             * Delegate JWT parsing, user lookup, and other
             * authentication exceptions to the existing
             * HandlerExceptionResolver.
             */
            handlerExceptionResolver.resolveException(
                    request,
                    response,
                    null,
                    e);
        }
    }
}