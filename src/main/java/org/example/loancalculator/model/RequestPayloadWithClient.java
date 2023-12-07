package org.example.loancalculator.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class RequestPayloadWithClient extends RequestPayload {
    @Email
    @NotBlank
    private String email;

    public RequestPayloadWithClient(double loanAmount, double nominalRate, int duration, String startDate, String email) {
        super(loanAmount, nominalRate, duration, startDate);
        this.email = email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
