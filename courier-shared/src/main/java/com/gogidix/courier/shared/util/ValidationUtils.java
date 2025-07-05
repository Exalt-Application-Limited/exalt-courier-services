package com.gogidix.courier.shared.util;

import com.gogidix.courier.shared.exception.ValidationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Utility class for common validation operations used across courier services.
 * Provides methods to validate objects, strings, collections, etc.
 */
public final class ValidationUtils {

    // Regular expression for validating email addresses
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
    
    // Regular expression for validating phone numbers (basic example)
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9]{10,15}$");

    // Private constructor to prevent instantiation
    private ValidationUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Validates that the specified object is not null.
     *
     * @param object the object to validate
     * @param message the exception message if validation fails
     * @throws ValidationException if the object is null
     */
    public static void validateNotNull(Object object, String message) {
        if (object == null) {
            throw new ValidationException(message);
        }
    }

    /**
     * Validates that the specified string is not null or empty.
     *
     * @param string the string to validate
     * @param message the exception message if validation fails
     * @throws ValidationException if the string is null or empty
     */
    public static void validateNotEmpty(String string, String message) {
        if (string == null || string.trim().isEmpty()) {
            throw new ValidationException(message);
        }
    }

    /**
     * Validates that the specified collection is not null or empty.
     *
     * @param collection the collection to validate
     * @param message the exception message if validation fails
     * @throws ValidationException if the collection is null or empty
     */
    public static void validateNotEmpty(Collection<?> collection, String message) {
        if (collection == null || collection.isEmpty()) {
            throw new ValidationException(message);
        }
    }

    /**
     * Validates that the specified map is not null or empty.
     *
     * @param map the map to validate
     * @param message the exception message if validation fails
     * @throws ValidationException if the map is null or empty
     */
    public static void validateNotEmpty(Map<?, ?> map, String message) {
        if (map == null || map.isEmpty()) {
            throw new ValidationException(message);
        }
    }

    /**
     * Validates that the specified value is greater than zero.
     *
     * @param value the value to validate
     * @param message the exception message if validation fails
     * @throws ValidationException if the value is not greater than zero
     */
    public static void validatePositive(Number value, String message) {
        validateNotNull(value, message);
        
        if (value instanceof Integer && (Integer) value <= 0) {
            throw new ValidationException(message);
        } else if (value instanceof Long && (Long) value <= 0) {
            throw new ValidationException(message);
        } else if (value instanceof Double && (Double) value <= 0) {
            throw new ValidationException(message);
        } else if (value instanceof Float && (Float) value <= 0) {
            throw new ValidationException(message);
        } else if (value instanceof BigDecimal && ((BigDecimal) value).compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException(message);
        }
    }

    /**
     * Validates that the specified value is greater than or equal to zero.
     *
     * @param value the value to validate
     * @param message the exception message if validation fails
     * @throws ValidationException if the value is not greater than or equal to zero
     */
    public static void validateNonNegative(Number value, String message) {
        validateNotNull(value, message);
        
        if (value instanceof Integer && (Integer) value < 0) {
            throw new ValidationException(message);
        } else if (value instanceof Long && (Long) value < 0) {
            throw new ValidationException(message);
        } else if (value instanceof Double && (Double) value < 0) {
            throw new ValidationException(message);
        } else if (value instanceof Float && (Float) value < 0) {
            throw new ValidationException(message);
        } else if (value instanceof BigDecimal && ((BigDecimal) value).compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException(message);
        }
    }

    /**
     * Validates that the specified string is a valid email address.
     *
     * @param email the email address to validate
     * @param message the exception message if validation fails
     * @throws ValidationException if the email address is not valid
     */
    public static void validateEmail(String email, String message) {
        validateNotEmpty(email, message);
        
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException(message);
        }
    }

    /**
     * Validates that the specified string is a valid phone number.
     *
     * @param phoneNumber the phone number to validate
     * @param message the exception message if validation fails
     * @throws ValidationException if the phone number is not valid
     */
    public static void validatePhoneNumber(String phoneNumber, String message) {
        validateNotEmpty(phoneNumber, message);
        
        if (!PHONE_PATTERN.matcher(phoneNumber).matches()) {
            throw new ValidationException(message);
        }
    }

    /**
     * Validates that the specified date is not in the future.
     *
     * @param date the date to validate
     * @param message the exception message if validation fails
     * @throws ValidationException if the date is in the future
     */
    public static void validatePastOrPresent(LocalDate date, String message) {
        validateNotNull(date, message);
        
        if (date.isAfter(LocalDate.now())) {
            throw new ValidationException(message);
        }
    }

    /**
     * Validates that the specified date is not in the future.
     *
     * @param dateTime the date/time to validate
     * @param message the exception message if validation fails
     * @throws ValidationException if the date/time is in the future
     */
    public static void validatePastOrPresent(LocalDateTime dateTime, String message) {
        validateNotNull(dateTime, message);
        
        if (dateTime.isAfter(LocalDateTime.now())) {
            throw new ValidationException(message);
        }
    }

    /**
     * Validates that the specified date is in the future.
     *
     * @param date the date to validate
     * @param message the exception message if validation fails
     * @throws ValidationException if the date is not in the future
     */
    public static void validateFuture(LocalDate date, String message) {
        validateNotNull(date, message);
        
        if (date.isBefore(LocalDate.now()) || date.isEqual(LocalDate.now())) {
            throw new ValidationException(message);
        }
    }

    /**
     * Validates that the specified date is in the future.
     *
     * @param dateTime the date/time to validate
     * @param message the exception message if validation fails
     * @throws ValidationException if the date/time is not in the future
     */
    public static void validateFuture(LocalDateTime dateTime, String message) {
        validateNotNull(dateTime, message);
        
        if (dateTime.isBefore(LocalDateTime.now()) || dateTime.isEqual(LocalDateTime.now())) {
            throw new ValidationException(message);
        }
    }
    
    /**
     * Validates that the specified value is between the specified minimum and maximum values.
     *
     * @param value the value to validate
     * @param min the minimum allowed value
     * @param max the maximum allowed value
     * @param message the exception message if validation fails
     * @throws ValidationException if the value is not between the minimum and maximum values
     */
    public static void validateRange(int value, int min, int max, String message) {
        if (value < min || value > max) {
            throw new ValidationException(message);
        }
    }
    
    /**
     * Validates that the specified value is between the specified minimum and maximum values.
     *
     * @param value the value to validate
     * @param min the minimum allowed value
     * @param max the maximum allowed value
     * @param message the exception message if validation fails
     * @throws ValidationException if the value is not between the minimum and maximum values
     */
    public static void validateRange(long value, long min, long max, String message) {
        if (value < min || value > max) {
            throw new ValidationException(message);
        }
    }
    
    /**
     * Validates that the specified value is between the specified minimum and maximum values.
     *
     * @param value the value to validate
     * @param min the minimum allowed value
     * @param max the maximum allowed value
     * @param message the exception message if validation fails
     * @throws ValidationException if the value is not between the minimum and maximum values
     */
    public static void validateRange(double value, double min, double max, String message) {
        if (value < min || value > max) {
            throw new ValidationException(message);
        }
    }
    
    /**
     * Validates that the specified condition is true.
     *
     * @param condition the condition to validate
     * @param message the exception message if validation fails
     * @throws ValidationException if the condition is false
     */
    public static void validateTrue(boolean condition, String message) {
        if (!condition) {
            throw new ValidationException(message);
        }
    }
    
    /**
     * Validates that the specified condition is false.
     *
     * @param condition the condition to validate
     * @param message the exception message if validation fails
     * @throws ValidationException if the condition is true
     */
    public static void validateFalse(boolean condition, String message) {
        if (condition) {
            throw new ValidationException(message);
        }
    }
}
