package org.example.loancalculator.model;

import jakarta.validation.constraints.Positive;

public class TestField {
    @Positive(message = "Loan amount should be greater than 0")
    private double loanAmount;

    public void setLoanAmount(double loanAmount) {
        this.loanAmount = loanAmount;
    }

    public double getLoanAmount() {
        return loanAmount;
    }
}
