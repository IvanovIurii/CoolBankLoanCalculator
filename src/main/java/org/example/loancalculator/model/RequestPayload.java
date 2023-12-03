package org.example.loancalculator.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.example.loancalculator.model.validation.ValidLocalDate;

public class RequestPayload {
    @Positive(message = "Loan amount should be greater than 0")
    private double loanAmount;
    @Min(0)
    @Max(100)
    private double nominalRate;
    @Min(1)
    private int duration;
    @ValidLocalDate
    private String startDate;

    public RequestPayload(double loanAmount, double nominalRate, int duration, String startDate) {
        this.loanAmount = loanAmount;
        this.nominalRate = nominalRate;
        this.duration = duration;
        this.startDate = startDate;
    }

    public double getLoanAmount() {
        return loanAmount;
    }

    public double getNominalRate() {
        return nominalRate;
    }

    public int getDuration() {
        return duration;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setLoanAmount(double loanAmount) {
        this.loanAmount = loanAmount;
    }

    public void setNominalRate(double nominalRate) {
        this.nominalRate = nominalRate;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
}
