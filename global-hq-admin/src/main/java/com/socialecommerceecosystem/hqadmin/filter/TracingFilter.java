package com.gogidix.courier.hqadmin.filter;

import com.socialecommerceecosystem.hqadmin.service.TracingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter that extracts tenant ID from request headers and adds it to the tracing context
 */
@Component
@Order(1)
public class TracingFilter extends OncePerRequestFilter {

    private static final String TENANT_ID_HEADER = "X-Tenant-ID";
    
    private final TracingService tracingService;
    
    @Autowired
    public TracingFilter(TracingService tracingService) {
        this.tracingService = tracingService;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // Extract tenant ID from header
        String tenantId = request.getHeader(TENANT_ID_HEADER);
        
        // If tenant ID is present, add it to the tracing context
        if (tenantId != null && !tenantId.isEmpty()) {
            tracingService.setTenantId(tenantId);
            
            // Add tenant ID as a tag to the current span
            tracingService.addTag("tenant.id", tenantId);
        }
        
        // Add request method and URI as tags
        tracingService.addTag("http.method", request.getMethod());
        tracingService.addTag("http.uri", request.getRequestURI());
        
        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }
}