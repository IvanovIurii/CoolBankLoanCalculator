package org.example.loancalculator.service;

import org.example.loancalculator.entity.Client;
import org.example.loancalculator.repository.ClientRepository;
import org.springframework.stereotype.Service;

@Service
public class ClientService {
    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public void addClient(String email) {
        Client client = new Client(email);
        clientRepository.save(client);
    }
}
