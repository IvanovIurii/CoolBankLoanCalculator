package org.example.loancalculator.ui.components;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.converter.StringToDoubleConverter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import org.example.loancalculator.model.RequestPayloadWithClient;
import org.example.loancalculator.ui.components.common.AbstractFormView;
import org.example.loancalculator.ui.components.common.InputField;
import org.example.loancalculator.ui.components.common.InputTextFieldBuilder;
import org.example.loancalculator.utils.DateFormatter;

import java.time.LocalDate;

public class LoanConditionsFormView extends AbstractFormView<RequestPayloadWithClient> {
    private static final String FORM_LABEL = "Conditions";

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

    public LoanConditionsFormView() {
        bind(loanAmountField, "loanAmount", StringToDoubleConverter::new);
        bind(nominalRateField, "nominalRate", StringToDoubleConverter::new);
        bind(durationField, "duration", StringToIntegerConverter::new);
        bind(emailField, "email");

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.add(
                new H2(FORM_LABEL),
                emailField,
                loanAmountField,
                nominalRateField,
                durationField,
                datePicker
        );
        add(verticalLayout);
    }

    private DatePicker createDatePicker() {
        // 01-01-2024 hard coded for now
        DatePicker datePicker = new DatePicker("Start date");
        datePicker.setReadOnly(true);
        datePicker.setLabel("Read-only");
        datePicker.setValue(LocalDate.of(2024, 1, 1));

        return datePicker;
    }

    @Override
    protected RequestPayloadWithClient createFormInputObject() {
        return new RequestPayloadWithClient(
                Double.parseDouble(loanAmountField.getValue()),
                Double.parseDouble(nominalRateField.getValue()),
                Integer.parseInt(durationField.getValue()),
                DateFormatter.format(datePicker.getValue()),
                emailField.getValue()
        );
    }

    @Override
    protected Class<RequestPayloadWithClient> getBeanType() {
        return RequestPayloadWithClient.class;
    }
}