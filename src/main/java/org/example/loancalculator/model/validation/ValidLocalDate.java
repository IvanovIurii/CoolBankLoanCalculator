package org.example.loancalculator.model.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LocalDateValidator.class)
public @interface ValidLocalDate {
    String message() default "Invalid date format or date is in the past";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
