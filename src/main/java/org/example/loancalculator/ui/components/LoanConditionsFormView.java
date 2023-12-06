package org.example.loancalculator.ui.components;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.converter.StringToDoubleConverter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import org.example.loancalculator.model.RequestPayload;
import org.example.loancalculator.ui.components.common.InputField;
import org.example.loancalculator.ui.components.common.InputTextFieldBuilder;
import org.example.loancalculator.utils.DateFormatter;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class LoanConditionsFormView extends FormLayout {
    private static final String FORM_LABEL = "Conditions";

    private final BeanValidationBinder<RequestPayload> binder = new BeanValidationBinder<>(RequestPayload.class);
    private final InputTextFieldBuilder builder = InputTextFieldBuilder.getInstance();

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

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.add(
                new H2(FORM_LABEL),
                loanAmountField,
                nominalRateField,
                durationField,
                datePicker
        );
        add(verticalLayout);
    }

    private void bind(InputField inputField, String propertyName, Function<String, Converter<String, ?>> converterFunction) {
        binder.forField(inputField)
                .asRequired(inputField.getLabel() + " is required")
                .withConverter(converterFunction.apply(inputField.getLabel() + " is invalid"))
                .bind(propertyName);
    }

    private DatePicker createDatePicker() {
        // 01-01-2024 hard coded for now
        DatePicker datePicker = new DatePicker("Start date");
        datePicker.setReadOnly(true);
        datePicker.setLabel("Read-only");
        datePicker.setValue(LocalDate.of(2024, 1, 1));

        return datePicker;
    }

    public Optional<RequestPayload> getRequestPayload() {
        if (getValidationErrors().isEmpty()) {
            return Optional.of(new RequestPayload(
                    Double.parseDouble(loanAmountField.getValue()),
                    Double.parseDouble(nominalRateField.getValue()),
                    Integer.parseInt(durationField.getValue()),
                    DateFormatter.format(datePicker.getValue())
            ));
        }
        return Optional.empty();
    }

    private List<ValidationResult> getValidationErrors() {
        return binder.validate().getValidationErrors();
    }
}