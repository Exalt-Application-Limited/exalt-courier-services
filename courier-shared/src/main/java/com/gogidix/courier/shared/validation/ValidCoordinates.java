package com.gogidix.courier.shared.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Validation annotation for geographic coordinates.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidCoordinatesValidator.class)
@Documented
public @interface ValidCoordinates {
    String message() default "Invalid coordinates: latitude must be between -90 and 90, longitude must be between -180 and 180";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}