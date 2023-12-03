package org.example.loancalculator.model;

import java.math.BigDecimal;

public record Payment(
        String date,
        BigDecimal borrowerPaymentAmount,
        BigDecimal principal,
        BigDecimal interest,
        BigDecimal initialOutstandingPrincipal,
        BigDecimal remainingOutstandingPrincipal
) {
}
