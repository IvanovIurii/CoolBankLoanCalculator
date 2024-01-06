package org.example.loancalculator.ui.components;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.converter.StringToDoubleConverter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.shared.Registration;
import org.example.loancalculator.model.RequestPayloadWithClient;
import org.example.loancalculator.ui.components.common.AbstractFormView;
import org.example.loancalculator.ui.components.common.InputField;
import org.example.loancalculator.ui.components.common.InputTextFieldBuilder;
import org.example.loancalculator.utils.DateFormatter;

import java.time.LocalDate;
import java.util.List;

public class LoanConditionsFormView extends AbstractFormView<RequestPayloadWithClient> {
    private final BeanValidationBinder<RequestPayloadWithClient> binder = new BeanValidationBinder<>(RequestPayloadWithClient.class);
    private final InputTextFieldBuilder builder = InputTextFieldBuilder.getInstance();

    InputField emailField = builder
            .withLabel("Email")
            .onValueChange(binder::validate)
            .build();

    InputField loanAmountField = builder
            .withLabel("Loan Amount")
            .withPrefix("€")
            .onValueChange(binder::validate)
            .build();

    InputField nominalRateField = builder
            .withLabel("Nominal Rate")
            .withPrefix("%")
            .onValueChange(binder::validate)
            .build();

    InputField durationField = builder
            .withLabel("Duration")
            .withPlaceholder("in months")
            .onValueChange(binder::validate)
            .build();

    DatePicker datePicker = createDatePicker();
    Button button = new Button("Calculate");

    public LoanConditionsFormView() {
        bind(loanAmountField, "loanAmount", StringToDoubleConverter::new);
        bind(nominalRateField, "nominalRate", StringToDoubleConverter::new);
        bind(durationField, "duration", StringToIntegerConverter::new);
        bind(emailField, "email");

        button.addClickListener(event -> validateAndSave());

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.add(
                emailField,
                loanAmountField,
                nominalRateField,
                durationField,
                datePicker,
                button
        );
        add(verticalLayout);
    }

    private void validateAndSave() {
        RequestPayloadWithClient requestPayloadWithClient = null;
        List<ValidationResult> validationErrors = getValidationErrors();
        if (validationErrors.isEmpty()) {
            requestPayloadWithClient = new RequestPayloadWithClient(
                    Double.parseDouble(loanAmountField.getValue()),
                    Double.parseDouble(nominalRateField.getValue()),
                    Integer.parseInt(durationField.getValue()),
                    DateFormatter.format(datePicker.getValue()),
                    emailField.getValue()
            );
        }
        fireEvent(new CalculateEvent(this, requestPayloadWithClient));
    }

    private DatePicker createDatePicker() {
        // 01-01-2024 hard coded for now
        DatePicker datePicker = new DatePicker("Start date");
        datePicker.setReadOnly(true);
        datePicker.setLabel("Read-only");
        datePicker.setValue(LocalDate.of(2024, 1, 1));

        return datePicker;
    }

    private static abstract class LoanConditionsFormEvent extends ComponentEvent<LoanConditionsFormView> {
        private final RequestPayloadWithClient payloadWithClient;

        protected LoanConditionsFormEvent(LoanConditionsFormView source, RequestPayloadWithClient payloadWithClient) {
            super(source, false);
            this.payloadWithClient = payloadWithClient;
        }

        public RequestPayloadWithClient getPayloadWithClient() {
            return payloadWithClient;
        }
    }

    public static class CalculateEvent extends LoanConditionsFormEvent {
        CalculateEvent(LoanConditionsFormView source, RequestPayloadWithClient contact) {
            super(source, contact);
        }
    }

    public Registration addCalculateListener(ComponentEventListener<CalculateEvent> listener) {
        return addListener(CalculateEvent.class, listener);
    }

    @Override
    protected Class<RequestPayloadWithClient> getBeanType() {
        return RequestPayloadWithClient.class;
    }
}