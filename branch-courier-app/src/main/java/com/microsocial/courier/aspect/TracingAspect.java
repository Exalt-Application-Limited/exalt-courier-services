package com.gogidix.courier.courier.aspect;

import brave.Span;
import brave.Tracer;
import com.microsocial.courier.annotation.Traced;
import com.microsocial.courier.service.TracingService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * Aspect for tracing service methods
 */
@Aspect
@Component
public class TracingAspect {

    private final TracingService tracingService;
    private final Tracer tracer;
    
    @Autowired
    public TracingAspect(TracingService tracingService, Tracer tracer) {
        this.tracingService = tracingService;
        this.tracer = tracer;
    }
    
    /**
     * Creates a span for each service method invocation
     */
    @Around("execution(* com.microsocial.courier.service..*.*(..)) && !execution(* com.microsocial.courier.service.TracingService.*(..))")
    public Object traceServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        // Get method signature
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        
        // Create a name for the span based on the class and method name
        String className = method.getDeclaringClass().getSimpleName();
        String methodName = method.getName();
        String spanName = className + "." + methodName;
        
        // Start a new span
        Span span = tracingService.startSpan(spanName);
        
        try (Tracer.SpanInScope spanInScope = tracer.withSpanInScope(span)) {
            // Add parameter information as tags
            Object[] args = joinPoint.getArgs();
            for (int i = 0; i < args.length; i++) {
                if (args[i] != null) {
                    String argValue = args[i].toString();
                    // Truncate very long values
                    if (argValue.length() > 100) {
                        argValue = argValue.substring(0, 97) + "...";
                    }
                    tracingService.addTag("arg" + i, argValue);
                }
            }
            
            // Execute the method
            return joinPoint.proceed();
        } catch (Throwable t) {
            // Record the error in the span
            tracingService.recordError(t);
            throw t;
        } finally {
            // Finish the span
            span.finish();
        }
    }
    
    /**
     * Creates a span for methods annotated with @Traced
     */
    @Around("@annotation(com.microsocial.courier.annotation.Traced)")
    public Object traceAnnotatedMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        // Get method signature and annotation
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Traced traced = method.getAnnotation(Traced.class);
        
        // Determine span name from annotation or default to className.methodName
        String spanName;
        if (StringUtils.hasText(traced.value())) {
            spanName = traced.value();
        } else {
            String className = method.getDeclaringClass().getSimpleName();
            String methodName = method.getName();
            spanName = className + "." + methodName;
        }
        
        // Start a new span
        Span span = tracingService.startSpan(spanName);
        
        try (Tracer.SpanInScope spanInScope = tracer.withSpanInScope(span)) {
            // Add parameter information as tags if enabled
            if (traced.includeParams()) {
                Object[] args = joinPoint.getArgs();
                String[] paramNames = signature.getParameterNames();
                
                for (int i = 0; i < args.length; i++) {
                    if (args[i] != null) {
                        String paramName = (paramNames != null && i < paramNames.length) ? paramNames[i] : "arg" + i;
                        String argValue = args[i].toString();
                        
                        // Truncate very long values
                        if (argValue.length() > 100) {
                            argValue = argValue.substring(0, 97) + "...";
                        }
                        
                        tracingService.addTag(paramName, argValue);
                    }
                }
            }
            
            // Execute the method
            return joinPoint.proceed();
        } catch (Throwable t) {
            // Record the error in the span
            tracingService.recordError(t);
            throw t;
        } finally {
            // Finish the span
            span.finish();
        }
    }
}