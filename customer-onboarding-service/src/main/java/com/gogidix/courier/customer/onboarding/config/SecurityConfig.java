package com.gogidix.courier.customer.onboarding.config;

import com.gogidix.ecosystem.shared.security.AuthEntryPointJwt;
import com.gogidix.ecosystem.shared.security.AuthTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Security configuration for the Customer Onboarding Service.
 * 
 * Extends shared security configuration with customer onboarding specific endpoints.
 * This configuration leverages the shared security library for JWT authentication,
 * CORS handling, and common security patterns.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;
    
    /**
     * JWT authentication filter bean leveraging shared security.
     * 
     * @return AuthTokenFilter instance from shared-security
     */
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }
    
    /**
     * Password encoder bean with BCrypt strength 12 (shared standard).
     * 
     * @return BCryptPasswordEncoder with strength 12
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
    
    /**
     * Authentication provider bean using shared patterns.
     * 
     * @return DaoAuthenticationProvider
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    
    /**
     * Authentication manager bean.
     * 
     * @param authConfig Authentication configuration
     * @return AuthenticationManager
     * @throws Exception if configuration fails
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    
    /**
     * CORS configuration source extending shared configuration with customer onboarding specific origins.
     * 
     * @return CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allow customer portal and admin origins
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost:3000",  // Customer portal dev
            "http://localhost:3001",  // Admin dashboard dev
            "http://localhost:3002",  // Mobile app dev proxy
            "https://*.exaltcourier.com",  // Production customer portal
            "https://*.exalt.app",     // Admin dashboards
            "https://*.exalt-dev.app"  // Development environments
        ));
        
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));
        
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", 
            "Content-Type", 
            "X-Requested-With",
            "Accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers",
            "X-Customer-Portal",
            "X-Application-Version"
        ));
        
        configuration.setExposedHeaders(Arrays.asList(
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials",
            "X-Total-Count",
            "X-Page-Size"
        ));
        
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
    
    /**
     * Security filter chain configuration for customer onboarding service.
     * 
     * @param http HttpSecurity
     * @return SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> 
                auth
                    // Public endpoints for customer registration and onboarding
                    .requestMatchers("/api/public/**").permitAll()
                    .requestMatchers("/api/customer/register").permitAll()
                    .requestMatchers("/api/customer/verify-email").permitAll()
                    .requestMatchers("/api/customer/forgot-password").permitAll()
                    .requestMatchers("/api/customer/reset-password").permitAll()
                    .requestMatchers("/api/customer/check-availability").permitAll()
                    
                    // Authentication endpoints
                    .requestMatchers("/api/auth/**").permitAll()
                    
                    // Standard public endpoints from shared configuration
                    .requestMatchers("/actuator/health").permitAll()
                    .requestMatchers("/actuator/info").permitAll()
                    .requestMatchers("/swagger-ui/**").permitAll()
                    .requestMatchers("/v3/api-docs/**").permitAll()
                    .requestMatchers("/swagger-resources/**").permitAll()
                    .requestMatchers("/webjars/**").permitAll()
                    .requestMatchers("/favicon.ico").permitAll()
                    .requestMatchers("/error").permitAll()
                    
                    // Customer-specific endpoints - require authentication
                    .requestMatchers("/api/customer/onboarding/**").hasAnyRole("CUSTOMER", "ADMIN", "SUPPORT_AGENT")
                    .requestMatchers("/api/customer/profile/**").hasAnyRole("CUSTOMER", "ADMIN")
                    .requestMatchers("/api/customer/kyc/**").hasAnyRole("CUSTOMER", "ADMIN", "SUPPORT_AGENT")
                    
                    // Admin endpoints
                    .requestMatchers("/api/admin/**").hasAnyRole("ADMIN", "SUPPORT_AGENT")
                    .requestMatchers("/actuator/**").hasRole("ADMIN")
                    
                    // All other requests require authentication
                    .anyRequest().authenticated()
            );
        
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    /**
     * Gets customer onboarding specific public endpoints.
     * 
     * @return Array of customer onboarding public endpoint patterns
     */
    public static String[] getCustomerOnboardingPublicEndpoints() {
        return new String[] {
            "/api/customer/register",
            "/api/customer/verify-email",
            "/api/customer/forgot-password",
            "/api/customer/reset-password",
            "/api/customer/check-availability"
        };
    }
    
    /**
     * Gets customer-authenticated endpoints.
     * 
     * @return Array of customer endpoint patterns
     */
    public static String[] getCustomerAuthenticatedEndpoints() {
        return new String[] {
            "/api/customer/onboarding/**",
            "/api/customer/profile/**",
            "/api/customer/kyc/**"
        };
    }
}