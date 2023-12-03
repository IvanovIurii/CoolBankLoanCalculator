package org.example.loancalculator.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DateFormatterTest {

    @Test
    void shouldFormatWithDesiredPattern() {
        LocalDate localDate = DateFormatter.format("01-01-2023");
        assertThat(localDate).isEqualTo(LocalDate.of(2023, 1, 1));
    }

    @Test
    void shouldAddMonths() {
        String date = DateFormatter.plusMonths("01-01-2023", 1);
        assertThat(date).isEqualTo("01-02-2023");
    }

    @Test
    void shouldThrowExceptionOnInvalidDate() {
        assertThatThrownBy(() -> DateFormatter.format("2022-01-01"))
                .isInstanceOf(DateTimeParseException.class)
                .hasMessageContaining("Text '2022-01-01' could not be parsed");
    }

    @Test
    void shouldThrowExceptionOnInvalidDateWhenAddMonths() {
        assertThatThrownBy(() -> DateFormatter.plusMonths("2022-01-01", 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid date");
    }
}