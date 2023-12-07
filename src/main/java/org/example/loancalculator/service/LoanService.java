package org.example.loancalculator.service;

import org.example.loancalculator.entity.Loan;
import org.example.loancalculator.model.Payment;
import org.example.loancalculator.repository.LoanRepository;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

@Service
public class LoanService {

    private final LoanRepository loanRepository;

    public LoanService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    public void savePayments(List<Payment> payments) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)
        ) {
            objectOutputStream.writeObject(payments);
            byte[] data = byteArrayOutputStream.toByteArray();
            Loan loan = new Loan(data);
            loanRepository.save(loan);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // unique client_id + hash of payment conditions
    public Loan getLoan(String email, byte[] data) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)
        ) {
            return (Loan) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

