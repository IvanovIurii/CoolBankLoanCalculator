package org.example.loancalculator.controller;

import jakarta.validation.Valid;
import org.example.loancalculator.model.RequestPayload;
import org.example.loancalculator.model.Payment;
import org.example.loancalculator.model.ResponsePayments;
import org.example.loancalculator.service.LoanCalculator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LoanCalculatorController {

    private final LoanCalculator loanCalculator;

    public LoanCalculatorController(LoanCalculator loanCalculator) {
        this.loanCalculator = loanCalculator;
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/generate-plan")
    public ResponseEntity<ResponsePayments> calculateLoan(@Valid @RequestBody RequestPayload payload) {
        List<Payment> payments = loanCalculator.generatePaymentPlan(
                payload.getLoanAmount(),
                payload.getNominalRate(),
                payload.getDuration(),
                payload.getStartDate()
        );
        ResponsePayments responsePayments = new ResponsePayments(payments);
        return ResponseEntity.ok(responsePayments);
    }
}
