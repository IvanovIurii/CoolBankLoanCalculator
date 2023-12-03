package org.example.loancalculator.service;

import org.example.loancalculator.model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LoanCalculatorTest {
    private LoanCalculator sut;

    @BeforeEach
    public void setUp() {
        sut = new LoanCalculator();
    }

    @Test
    public void shouldHavePaymentsPlanFor3MonthsLoanWithZeroRate() {
        // in my opinion 0 rate should be possible
        List<Payment> payments = sut.generatePaymentPlan(1000, 0, 3, "01-01-2024");

        assertThat(payments.size()).isEqualTo(3);

        Payment payment1 = payments.get(0);
        Payment payment2 = payments.get(1);
        Payment payment3 = payments.get(2);

        BigDecimal principal1 = payment1.principal();
        BigDecimal principal2 = payment2.principal();
        BigDecimal principal3 = payment3.principal();

        assertThat(principal1.add(principal2).add(principal3)).isEqualTo("1000.00");

        assertThat(payment1.initialOutstandingPrincipal()).isEqualTo("1000.00");
        assertThat(payment1.borrowerPaymentAmount()).isEqualTo("333.33");
        assertThat(payment1.date()).isEqualTo("01-01-2024");
        assertThat(payment1.interest()).isEqualTo("0.00");
        assertThat(payment1.principal()).isEqualTo("333.33");
        assertThat(payment1.remainingOutstandingPrincipal()).isEqualTo("666.67");

        assertThat(payment2.initialOutstandingPrincipal()).isEqualTo("666.67");
        assertThat(payment2.borrowerPaymentAmount()).isEqualTo("333.33");
        assertThat(payment2.date()).isEqualTo("01-02-2024");
        assertThat(payment2.interest()).isEqualTo("0.00");
        assertThat(payment2.principal()).isEqualTo("333.33");
        assertThat(payment2.remainingOutstandingPrincipal()).isEqualTo("333.34");

        assertThat(payment3.initialOutstandingPrincipal()).isEqualTo("333.34");
        assertThat(payment3.borrowerPaymentAmount()).isEqualTo("333.34");
        assertThat(payment3.date()).isEqualTo("01-03-2024");
        assertThat(payment3.interest()).isEqualTo("0.00");
        assertThat(payment3.principal()).isEqualTo("333.34");
        assertThat(payment3.remainingOutstandingPrincipal()).isEqualTo("0.00");
    }

    @Test
    public void shouldHavePaymentsPlanFor2MonthsLoan() {
        List<Payment> payments = sut.generatePaymentPlan(999, 2, 2, "01-01-2024");

        assertThat(payments.size()).isEqualTo(2);

        Payment payment1 = payments.get(0);
        Payment payment2 = payments.get(1);

        BigDecimal principal1 = payment1.principal();
        BigDecimal principal2 = payment2.principal();

        assertThat(principal1.add(principal2)).isEqualTo("999.00");

        assertThat(payment1.initialOutstandingPrincipal()).isEqualTo("999.00");
        assertThat(payment1.borrowerPaymentAmount()).isEqualTo("500.75");
        assertThat(payment1.date()).isEqualTo("01-01-2024");
        assertThat(payment1.interest()).isEqualTo("1.67");
        assertThat(payment1.principal()).isEqualTo("499.08");
        assertThat(payment1.remainingOutstandingPrincipal()).isEqualTo("499.92");

        assertThat(payment2.initialOutstandingPrincipal()).isEqualTo("499.92");
        assertThat(payment2.borrowerPaymentAmount()).isEqualTo("500.75");
        assertThat(payment2.date()).isEqualTo("01-02-2024");
        assertThat(payment2.interest()).isEqualTo("0.83");
        assertThat(payment2.principal()).isEqualTo("499.92");
        assertThat(payment2.remainingOutstandingPrincipal()).isEqualTo("0.00");
    }

    @Test
    public void shouldHavePaymentsPlanFor1MonthLoan() {
        List<Payment> payments = sut.generatePaymentPlan(256, 10, 1, "01-01-2024");
        Payment payment1 = payments.get(0);

        assertThat(payments.size()).isEqualTo(1);

        assertThat(payment1.initialOutstandingPrincipal()).isEqualTo("256.00");
        assertThat(payment1.borrowerPaymentAmount()).isEqualTo("258.13");
        assertThat(payment1.date()).isEqualTo("01-01-2024");
        assertThat(payment1.interest()).isEqualTo("2.13");
        assertThat(payment1.principal()).isEqualTo("256.00");
        assertThat(payment1.remainingOutstandingPrincipal()).isEqualTo("0.00");
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    public void shouldThrowExceptionOnInvalidDurationPeriod(int duration) {
        assertThatThrownBy(() -> sut.generatePaymentPlan(1000, 1, duration, "01-01-2024"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid duration period");
    }

    @ParameterizedTest
    @ValueSource(doubles = {-42, 0})
    public void shouldThrowExceptionOnInvalidLoanAmount(double loanAmount) {
        assertThatThrownBy(() -> sut.generatePaymentPlan(loanAmount, 1, 12, "01-01-2024"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Loan amount must be greater than zero");
    }

    @ParameterizedTest
    @ValueSource(doubles = {-2, 110})
    public void shouldThrowExceptionOnInvalidNominalRate(double nominalRate) {
        assertThatThrownBy(() -> sut.generatePaymentPlan(2301, nominalRate, 12, "01-01-2024"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid interest rate");
    }

    @Test
    public void shouldThrowExceptionOnInvalidDate() {
        assertThatThrownBy(() -> sut.generatePaymentPlan(2301, 1, 12, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid date");
    }

    @ParameterizedTest
    @ValueSource(strings = {"01-01-202", "01-01-2024-01", "", " "})
    public void shouldThrowExceptionOnInvalidDate(String date) {
        assertThatThrownBy(() -> sut.generatePaymentPlan(2301, 1, 12, date))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid date");
    }
}