package com.exalt.courier.shared.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

/**
 * Validator for the ValidCoordinates annotation.
 */
public class ValidCoordinatesValidator implements ConstraintValidator<ValidCoordinates, Object> {

    @Override
    public void initialize(ValidCoordinates constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Let @NotNull handle null validation
        }

        try {
            Field latField = findField(value.getClass(), "latitude");
            Field lngField = findField(value.getClass(), "longitude");

            if (latField == null || lngField == null) {
                return false;
            }

            latField.setAccessible(true);
            lngField.setAccessible(true);

            Double latitude = (Double) latField.get(value);
            Double longitude = (Double) lngField.get(value);

            if (latitude == null || longitude == null) {
                return false;
            }

            boolean validLat = latitude >= -90.0 && latitude <= 90.0;
            boolean validLng = longitude >= -180.0 && longitude <= 180.0;

            return validLat && validLng;

        } catch (Exception e) {
            return false;
        }
    }

    private Field findField(Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null) {
                return findField(superClass, fieldName);
            }
            return null;
        }
    }
}