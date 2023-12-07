package org.example.loancalculator.service;

import org.example.loancalculator.model.Payment;
import org.example.loancalculator.utils.DateFormatter;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoanCalculator {

    private final static int DAYS_IN_MONTH = 30;
    private final static int DAYS_IN_YEAR = 360;

    private final static int SCALE = 2;

    public List<Payment> generatePaymentPlan(double loanAmount, double nominalRate, int durationInMonths, String date) {
        nominalRate = nominalRate / 100;
        BigDecimal monthlyPayment = calculateMonthlyPayment(loanAmount, nominalRate, durationInMonths).setScale(SCALE, RoundingMode.HALF_UP);
        BigDecimal initialPrincipal = BigDecimal.valueOf(loanAmount).setScale(SCALE, RoundingMode.HALF_UP);

        List<Payment> payments = new ArrayList<>();

        for (int month = 0; month < durationInMonths; month++) {
            BigDecimal interest = calculateInterest(nominalRate, initialPrincipal);
            BigDecimal principal = calculatePrincipal(monthlyPayment, interest);
            BigDecimal remainingPrincipal = calculateRemainingOutstandingPrincipal(initialPrincipal, principal);

            if (principal.compareTo(initialPrincipal) > 0) {
                remainingPrincipal = calculateRemainingOutstandingPrincipal(initialPrincipal, initialPrincipal);
            }

            payments.add(
                    new Payment(
                            DateFormatter.plusMonths(date, month),
                            monthlyPayment,
                            principal,
                            interest,
                            initialPrincipal,
                            remainingPrincipal
                    )
            );

            initialPrincipal = remainingPrincipal;
        }

        if (initialPrincipal.compareTo(BigDecimal.ZERO) > 0) {
            addReminderToTheLastPayment(payments, initialPrincipal);
        }

        return payments;
    }

    // it is possible at the end to have remainingOutstandingPrincipal value as not ZERO, because of roundings and divides,
    // we should avoid this situation, therefore if this situation occurs, that amount is added to the latest payment
    private void addReminderToTheLastPayment(List<Payment> payments, BigDecimal reminder) {
        int lastIndex = payments.size() - 1;
        Payment lastPayment = payments.get(lastIndex);
        Payment newPayment = new Payment(
                lastPayment.date(),
                lastPayment.borrowerPaymentAmount().add(reminder),
                lastPayment.borrowerPaymentAmount().add(reminder),
                lastPayment.interest(),
                lastPayment.initialOutstandingPrincipal(),
                BigDecimal.ZERO.setScale(SCALE, RoundingMode.HALF_UP)
        );

        payments.set(lastIndex, newPayment);
    }

    private BigDecimal calculateInterest(double interestRate, BigDecimal initialOutstandingPrincipal) {
        return initialOutstandingPrincipal
                .multiply(BigDecimal.valueOf(interestRate))
                .multiply(BigDecimal.valueOf(DAYS_IN_MONTH))
                .divide(BigDecimal.valueOf(DAYS_IN_YEAR), SCALE, RoundingMode.HALF_UP);
    }

    // annuity
    private BigDecimal calculateMonthlyPayment(double loanAmount, double interestRate, int durationInMonths) {
        if (loanAmount <= 0) {
            throw new IllegalArgumentException("Loan amount must be greater than zero");
        }
        if (durationInMonths <= 0) {
            throw new IllegalArgumentException("Invalid duration period");
        }
        if (interestRate < 0 || interestRate > 1) {
            throw new IllegalArgumentException("Invalid interest rate");
        }

        double monthlyInterestRate = interestRate / 12;
        double monthlyPayment = monthlyInterestRate != 0
                ? (loanAmount * monthlyInterestRate) / (1 - Math.pow(1 + monthlyInterestRate, -durationInMonths))
                : loanAmount / durationInMonths;

        return BigDecimal.valueOf(monthlyPayment);
    }

    private BigDecimal calculatePrincipal(BigDecimal annuity, BigDecimal interest) {
        return annuity.subtract(interest);
    }

    private BigDecimal calculateRemainingOutstandingPrincipal(BigDecimal initialPrincipal, BigDecimal principal) {
        return initialPrincipal.subtract(principal);
    }
}
