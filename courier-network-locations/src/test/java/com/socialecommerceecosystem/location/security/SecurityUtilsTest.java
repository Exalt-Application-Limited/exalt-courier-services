package com.gogidix.courier.location.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SecurityUtilsTest {

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private SecurityUtils securityUtils;

    @BeforeEach
    void setUp() {
        securityUtils = new SecurityUtils();
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getCurrentUsername_whenAuthenticated_shouldReturnUsername() {
        // Arrange
        Jwt jwt = createJwt(Map.of("sub", "testuser"));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(jwt);

        // Act
        Optional<String> username = securityUtils.getCurrentUsername();

        // Assert
        assertTrue(username.isPresent());
        assertEquals("testuser", username.get());
    }

    @Test
    void getCurrentUsername_whenNotAuthenticated_shouldReturnEmpty() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        // Act
        Optional<String> username = securityUtils.getCurrentUsername();

        // Assert
        assertFalse(username.isPresent());
    }

    @Test
    void getCurrentUsername_whenNoAuthentication_shouldReturnEmpty() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);

        // Act
        Optional<String> username = securityUtils.getCurrentUsername();

        // Assert
        assertFalse(username.isPresent());
    }

    @Test
    void getCurrentUserId_whenAuthenticated_shouldReturnUserId() {
        // Arrange
        Jwt jwt = createJwt(Map.of("user_id", "123"));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(jwt);

        // Act
        Optional<String> userId = securityUtils.getCurrentUserId();

        // Assert
        assertTrue(userId.isPresent());
        assertEquals("123", userId.get());
    }

    @Test
    void getCurrentUserRoles_whenAuthenticated_shouldReturnRoles() {
        // Arrange
        Jwt jwt = createJwt(Map.of("roles", List.of("USER", "ADMIN")));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(jwt);

        // Act
        List<String> roles = securityUtils.getCurrentUserRoles();

        // Assert
        assertEquals(2, roles.size());
        assertTrue(roles.contains("USER"));
        assertTrue(roles.contains("ADMIN"));
    }

    @Test
    void hasRole_whenUserHasRole_shouldReturnTrue() {
        // Arrange
        Jwt jwt = createJwt(Map.of("roles", List.of("USER", "ADMIN")));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(jwt);

        // Act
        boolean hasRole = securityUtils.hasRole("ADMIN");

        // Assert
        assertTrue(hasRole);
    }

    @Test
    void hasRole_whenUserDoesNotHaveRole_shouldReturnFalse() {
        // Arrange
        Jwt jwt = createJwt(Map.of("roles", List.of("USER")));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(jwt);

        // Act
        boolean hasRole = securityUtils.hasRole("ADMIN");

        // Assert
        assertFalse(hasRole);
    }

    @Test
    void isAdmin_whenUserIsAdmin_shouldReturnTrue() {
        // Arrange
        Jwt jwt = createJwt(Map.of("roles", List.of("USER", "ADMIN")));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(jwt);

        // Act
        boolean isAdmin = securityUtils.isAdmin();

        // Assert
        assertTrue(isAdmin);
    }

    @Test
    void isResourceOwner_whenUserOwnsResource_shouldReturnTrue() {
        // Arrange
        Jwt jwt = createJwt(Map.of("user_id", "123"));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(jwt);

        // Act
        boolean isResourceOwner = securityUtils.isResourceOwner("123");

        // Assert
        assertTrue(isResourceOwner);
    }

    @Test
    void isResourceOwner_whenUserDoesNotOwnResource_shouldReturnFalse() {
        // Arrange
        Jwt jwt = createJwt(Map.of("user_id", "123"));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(jwt);

        // Act
        boolean isResourceOwner = securityUtils.isResourceOwner("456");

        // Assert
        assertFalse(isResourceOwner);
    }

    /**
     * Helper method to create a JWT token with specified claims.
     */
    private Jwt createJwt(Map<String, Object> claims) {
        return new Jwt(
                "token",
                Instant.now(),
                Instant.now().plusSeconds(300),
                Map.of("alg", "RS256", "typ", "JWT"),
                claims
        );
    }
}
