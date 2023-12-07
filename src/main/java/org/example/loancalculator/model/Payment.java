package org.example.loancalculator.model;

import java.io.Serializable;
import java.math.BigDecimal;

public record Payment(
        String date,
        BigDecimal borrowerPaymentAmount,
        BigDecimal principal,
        BigDecimal interest,
        BigDecimal initialOutstandingPrincipal,
        BigDecimal remainingOutstandingPrincipal
) implements Serializable {
}
