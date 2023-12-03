package org.example.loancalculator.model;

import java.util.List;

public record ResponsePayments(List<Payment> borrowerPayments) {
}
