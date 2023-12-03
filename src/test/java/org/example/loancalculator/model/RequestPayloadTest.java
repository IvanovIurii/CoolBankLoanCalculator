package org.example.loancalculator.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RequestPayloadTest {

    private RequestPayloadValidator validator;

    @BeforeEach
    public void setup() {
        validator = new RequestPayloadValidator();
    }

    @Test
    public void shouldPassValidation() {
        RequestPayload payload = RequestPayloadBuilder.payload().build();
        RequestPayloadValidator.Validated validated = validator.validate(payload);

        assertThat(validated.hasErrors()).isFalse();
    }

    @ParameterizedTest
    @ValueSource(doubles = {-1, 0})
    public void shouldHaveError_WhenLoanAmountLessOrEqualZero(double loanAmount) {
        RequestPayload payload = RequestPayloadBuilder.payload()
                .withLoanAmount(loanAmount)
                .build();

        RequestPayloadValidator.Validated validated = validator.validate(payload);

        assertThat(validated.hasErrors()).isTrue();
        assertThat(validated.containsErrors("Loan amount should be greater than 0")).isTrue();
    }

    @ParameterizedTest
    @CsvSource({
            "-1, must be greater than or equal to 0",
            "101, must be less than or equal to 100"
    })
    public void shouldHaveError_WhenNominalRateLessThanZeroOrGreaterThanOneHundred(double nominalRate, String errorMessage) {
        RequestPayload payload = RequestPayloadBuilder.payload()
                .withNominalRate(nominalRate)
                .build();

        RequestPayloadValidator.Validated validated = validator.validate(payload);

        assertThat(validated.hasErrors()).isTrue();
        assertThat(validated.containsErrors(errorMessage)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    public void shouldHaveError_WhenDurationEqualOrLessThanZero(int duration) {
        RequestPayload payload = RequestPayloadBuilder.payload()
                .withDuration(duration)
                .build();

        RequestPayloadValidator.Validated validated = validator.validate(payload);

        assertThat(validated.hasErrors()).isTrue();
        assertThat(validated.containsErrors("must be greater than or equal to 1")).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "invalid date", "2024-01-01", "2023-01-01"})
    public void shouldHaveError_WhenStartDateIsInvalid(String startDate) {
        RequestPayload payload = RequestPayloadBuilder.payload()
                .withStartDate(startDate)
                .build();

        RequestPayloadValidator.Validated validated = validator.validate(payload);

        assertThat(validated.hasErrors()).isTrue();
        assertThat(validated.containsErrors("Invalid date format or date is in the past")).isTrue();
    }

    @Test
    public void shouldHaveError_WhenStartDateIsNull() {
        RequestPayload payload = RequestPayloadBuilder.payload()
                .withStartDate(null)
                .build();

        RequestPayloadValidator.Validated validated = validator.validate(payload);

        assertThat(validated.hasErrors()).isTrue();
        assertThat(validated.containsErrors("Invalid date format or date is in the past")).isTrue();
    }
}

class RequestPayloadValidator {
    private final Validator validator;

    public RequestPayloadValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    public Validated validate(RequestPayload payload) {
        Set<ConstraintViolation<RequestPayload>> constraintViolations = validator.validate(payload);
        return new Validated(constraintViolations);
    }

    static class Validated {
        private final Set<String> errors = new HashSet<>();

        private Validated(Set<ConstraintViolation<RequestPayload>> constraintViolations) {
            constraintViolations.forEach(constraintViolation -> errors.add(constraintViolation.getMessage()));
        }

        public boolean hasErrors() {
            return !errors.isEmpty();
        }

        public boolean containsErrors(String... errorMessage) {
            for (String message : errorMessage) {
                if (!errors.contains(message)) {
                    return false;
                }
            }
            return true;
        }
    }
}

class RequestPayloadBuilder {

    // defaults
    private double loanAmount = 1000;
    private double nominalRate = 5;
    private int duration = 12;
    private String startDate = "01-01-2024";

    public static RequestPayloadBuilder payload() {
        return new RequestPayloadBuilder();
    }

    public RequestPayloadBuilder withLoanAmount(double loanAmount) {
        this.loanAmount = loanAmount;
        return this;
    }

    public RequestPayloadBuilder withNominalRate(double nominalRate) {
        this.nominalRate = nominalRate;
        return this;
    }

    public RequestPayloadBuilder withDuration(int duration) {
        this.duration = duration;
        return this;
    }

    public RequestPayloadBuilder withStartDate(String startDate) {
        this.startDate = startDate;
        return this;
    }

    public RequestPayload build() {
        return new RequestPayload(loanAmount, nominalRate, duration, startDate);
    }
}

