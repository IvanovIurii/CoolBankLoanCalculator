package org.example.loancalculator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.loancalculator.model.RequestPayload;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class LoanCalculatorControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldGenerateValidPaymentPlan() throws Exception {
        RequestPayload payload = new RequestPayload(
                5000,
                5.0,
                2,
                "01-01-2024"
        );
        String jsonRequest = objectMapper.writeValueAsString(payload);
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/generate-plan")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest));

        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        resultActions
                .andExpect(jsonPath("$.borrowerPayments[0].date", is("01-01-2024")))
                .andExpect(jsonPath("$.borrowerPayments[0].borrowerPaymentAmount", is(2515.64)))
                .andExpect(jsonPath("$.borrowerPayments[0].principal", is(2494.81)))
                .andExpect(jsonPath("$.borrowerPayments[0].interest", is(20.83)))
                .andExpect(jsonPath("$.borrowerPayments[0].initialOutstandingPrincipal", is(5000.00)))
                .andExpect(jsonPath("$.borrowerPayments[0].remainingOutstandingPrincipal", is(2505.19)));

        resultActions
                .andExpect(jsonPath("$.borrowerPayments[1].date", is("01-02-2024")))
                .andExpect(jsonPath("$.borrowerPayments[1].borrowerPaymentAmount", is(2515.64)))
                .andExpect(jsonPath("$.borrowerPayments[1].principal", is(2505.20)))
                .andExpect(jsonPath("$.borrowerPayments[1].interest", is(10.44)))
                .andExpect(jsonPath("$.borrowerPayments[1].initialOutstandingPrincipal", is(2505.19)))
                .andExpect(jsonPath("$.borrowerPayments[1].remainingOutstandingPrincipal", is(0.00)));

    }

    @Test
    void shouldFailValidationWhenInvalidStartDateOnly() throws Exception {
        RequestPayload payload = new RequestPayload(
                123,
                5,
                1,
                "null"
        );
        String jsonRequest = objectMapper.writeValueAsString(payload);
        mockMvc.perform(MockMvcRequestBuilders.post("/generate-plan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title", is("Validation failed")))
                .andExpect(jsonPath("$.validationErrors", is(List.of("startDate: Invalid date format or date is in the past"))))
                .andExpect(jsonPath("$.status", is("BAD_REQUEST")));
    }

    @Test
    void shouldFailValidationWhenRequestPayloadIsInvalid() throws Exception {
        String jsonString = "{" +
                "\"loanAmount\":\"-42\"," +
                "\"nominalRate\":\"-1\"," +
                "\"duration\":\"0\"," +
                "\"startDate\":\"null\"" +
                "}";

        mockMvc.perform(MockMvcRequestBuilders.post("/generate-plan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title", is("Validation failed")))
                .andExpect(jsonPath("$.validationErrors", containsInAnyOrder(
                        "startDate: Invalid date format or date is in the past",
                        "nominalRate: must be greater than or equal to 0",
                        "loanAmount: Loan amount should be greater than 0",
                        "duration: must be greater than or equal to 1"
                )))
                .andExpect(jsonPath("$.status", is("BAD_REQUEST")));
    }
}