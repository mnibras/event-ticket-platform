package com.devtiro.tickets.filter;

import com.devtiro.tickets.domain.User;
import com.devtiro.tickets.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Filter to provision a user in the database if they do not exist.
 * This filter checks the JWT token for the user's Keycloak ID and creates a new User entity if it does not already exist.
 */
@Component
@RequiredArgsConstructor
public class UserProvisioningFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Get the authentication object from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof Jwt jwt) {
            // Extract the user ID from the JWT subject
            UUID keycloakId = UUID.fromString(jwt.getSubject());
            if (!userRepository.existsById(keycloakId)) {
                User user = new User();
                user.setId(keycloakId);
                user.setName(jwt.getClaimAsString("preferred_username"));
                user.setEmail(jwt.getClaimAsString("email"));
                userRepository.save(user);
            }
        }
        filterChain.doFilter(request, response);
    }
}
