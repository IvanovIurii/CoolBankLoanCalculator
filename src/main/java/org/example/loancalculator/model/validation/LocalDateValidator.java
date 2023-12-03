package org.example.loancalculator.model.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.loancalculator.utils.DateFormatter;

import java.time.LocalDate;

public class LocalDateValidator implements ConstraintValidator<ValidLocalDate, String> {

    @Override
    public void initialize(ValidLocalDate constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        try {
            LocalDate date = DateFormatter.format(value);
            return !date.isBefore(LocalDate.now());
        } catch (Exception e) {
            return false;
        }
    }
}
