package com.gogidix.courier.filter;

import com.gogidix.courier.service.TracingService;
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
 * Filter that extracts branch ID from request headers and adds it to the tracing context
 */
@Component
@Order(1)
public class TracingFilter extends OncePerRequestFilter {

    private static final String BRANCH_ID_HEADER = "X-Branch-ID";
    
    private final TracingService tracingService;
    
    @Autowired
    public TracingFilter(TracingService tracingService) {
        this.tracingService = tracingService;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // Extract branch ID from header
        String branchId = request.getHeader(BRANCH_ID_HEADER);
        
        // If branch ID is present, add it to the tracing context
        if (branchId != null && !branchId.isEmpty()) {
            tracingService.setBranchId(branchId);
            
            // Add branch ID as a tag to the current span
            tracingService.addTag("branch.id", branchId);
        }
        
        // Add request method and URI as tags
        tracingService.addTag("http.method", request.getMethod());
        tracingService.addTag("http.uri", request.getRequestURI());
        
        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }
}