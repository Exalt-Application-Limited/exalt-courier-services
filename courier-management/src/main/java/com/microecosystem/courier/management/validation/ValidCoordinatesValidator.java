package com.exalt.courier.management.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;

/**
 * Validator implementation for ValidCoordinates annotation.
 * Validates that latitude and longitude values are within valid ranges.
 */
public class ValidCoordinatesValidator implements ConstraintValidator<ValidCoordinates, Object> {
    
    private String latitudeField;
    private String longitudeField;
    
    @Override
    public void initialize(ValidCoordinates annotation) {
        this.latitudeField = annotation.latitudeField();
        this.longitudeField = annotation.longitudeField();
    }
    
    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        if (object == null) {
            return true; // Let @NotNull handle null validation
        }
        
        try {
            Double latitude = getFieldValue(object, latitudeField);
            Double longitude = getFieldValue(object, longitudeField);
            
            // If both are null, consider valid (optional coordinates)
            if (latitude == null && longitude == null) {
                return true;
            }
            
            // If only one is null, invalid
            if (latitude == null || longitude == null) {
                return false;
            }
            
            // Validate latitude range: -90 to 90
            if (latitude < -90.0 || latitude > 90.0) {
                return false;
            }
            
            // Validate longitude range: -180 to 180
            if (longitude < -180.0 || longitude > 180.0) {
                return false;
            }
            
            return true;
            
        } catch (Exception e) {
            return false;
        }
    }
    
    private Double getFieldValue(Object object, String fieldName) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (Double) field.get(object);
    }
}