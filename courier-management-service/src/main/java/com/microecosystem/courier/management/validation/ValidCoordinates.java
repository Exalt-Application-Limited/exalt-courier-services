package com.gogidix.courierservices.management.$1;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validation annotation for geographic coordinates.
 * Ensures that latitude and longitude values are within valid ranges.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidCoordinatesValidator.class)
@Documented
public @interface ValidCoordinates {
    
    String message() default "Invalid coordinates: latitude must be between -90 and 90, longitude must be between -180 and 180";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
    /**
     * The field name for latitude
     */
    String latitudeField() default "latitude";
    
    /**
     * The field name for longitude
     */
    String longitudeField() default "longitude";
}