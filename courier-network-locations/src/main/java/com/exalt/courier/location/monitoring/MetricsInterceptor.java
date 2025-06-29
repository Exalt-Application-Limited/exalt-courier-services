package com.exalt.courier.location.monitoring;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * HTTP request interceptor that tracks metrics for auto-scaling.
 * Captures request start and end times to calculate request durations
 * and maintain accurate request rate metrics.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class MetricsInterceptor implements HandlerInterceptor {

    private final AutoScalingService autoScalingService;
    
    private static final String REQUEST_START_TIME = "requestStartTime";
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Track request start
        long startTime = autoScalingService.requestStarted();
        request.setAttribute(REQUEST_START_TIME, startTime);
        return true;
    }
    
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        // Nothing to do here
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // Track request completion
        Long startTime = (Long) request.getAttribute(REQUEST_START_TIME);
        if (startTime != null) {
            autoScalingService.requestCompleted(startTime);
            
            // Log slow requests for performance monitoring
            long duration = System.currentTimeMillis() - startTime;
            if (duration > 500) { // Log requests taking longer than 500ms
                log.warn("Slow request detected: {} {} took {}ms", 
                        request.getMethod(), request.getRequestURI(), duration);
            }
        }
    }
}
