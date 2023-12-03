package org.example.loancalculator.ui;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.converter.StringToDoubleConverter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.router.Route;
import org.example.loancalculator.model.Payment;
import org.example.loancalculator.model.RequestPayload;
import org.example.loancalculator.service.LoanCalculator;
import org.example.loancalculator.utils.DateFormatter;

import java.time.LocalDate;
import java.util.List;

@Route
public class MainView extends VerticalLayout {

    private final LoanCalculator loanCalculator;

    private BeanValidationBinder<RequestPayload> binder = new BeanValidationBinder<>(RequestPayload.class);

    public MainView(LoanCalculator loanCalculator) {
        this.loanCalculator = loanCalculator;

        TextField loanAmountField = initLoanAmountField();
        TextField nominalRateField = initNominalRateField();
        TextField durationField = initDurationField();
        DatePicker datePicker = initStartDateField();

        Button button = new Button("Calculate");

        button.addClickListener(click -> {
            BinderValidationStatus<RequestPayload> validated = binder.validate();
            List<ValidationResult> validationErrors = validated.getValidationErrors();

            if (!validationErrors.isEmpty()) {
                Notification notification = new Notification();
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.setDuration(3000);
                notification.setPosition(Notification.Position.TOP_CENTER);
                notification.add("Please fix the validation errors");
                notification.open();
            } else {
                Notification.show("Calculating request was made", 3000, Notification.Position.TOP_CENTER);

                String loanAmount = loanAmountField.getValue();
                String nominalRate = nominalRateField.getValue();
                String duration = durationField.getValue();
                String date = DateFormatter.format(datePicker.getValue());

                List<Payment> payments = loanCalculator.generatePaymentPlan(
                        Double.parseDouble(loanAmount),
                        Double.parseDouble(nominalRate),
                        Integer.parseInt(duration),
                        date
                );

                Grid<Payment> grid = new Grid<>(Payment.class, false);
                grid.addColumn(Payment::date).setHeader("Date");
                grid.addColumn(Payment::borrowerPaymentAmount).setHeader("Payment Amount");
                grid.addColumn(Payment::principal).setHeader("Principal");
                grid.addColumn(Payment::interest).setHeader("Interest");
                grid.addColumn(Payment::remainingOutstandingPrincipal).setHeader("Remaining");
                grid.setItems(payments);

                add(grid);
            }
        });
        add(
                new H2("Loan Calculator"),
                loanAmountField,
                nominalRateField,
                durationField,
                datePicker,
                button
        );
    }

    private TextField initLoanAmountField() {
        TextField loanAmountField = new TextField("Loan Amount");
        loanAmountField.addValueChangeListener(e -> binder.validate());
        binder
                .forField(loanAmountField)
                .asRequired("Loan amount is required")
                .withConverter(new StringToDoubleConverter("It has to be a valid amount"))
                .bind("loanAmount");

        return loanAmountField;
    }

    private TextField initNominalRateField() {
        TextField nominalRateField = new TextField("Nominal Rate");
        Div percentagePrefix = new Div();
        percentagePrefix.setText("%");
        nominalRateField.setPrefixComponent(percentagePrefix);
        nominalRateField.addValueChangeListener(e -> binder.validate());
        binder
                .forField(nominalRateField)
                .asRequired("Nominal rate is required")
                .withConverter(new StringToDoubleConverter("It has to be a valid rate"))
                .bind("nominalRate");

        return nominalRateField;
    }

    private TextField initDurationField() {
        TextField durationField = new TextField("Duration");
        durationField.setPlaceholder("in months");
        durationField.addValueChangeListener(e -> binder.validate());
        binder
                .forField(durationField)
                .asRequired("Duration is required")
                .withConverter(new StringToIntegerConverter("It has to be a valid duration"))
                .bind("duration");

        return durationField;
    }

    private DatePicker initStartDateField() {
        // 01-01-2024 hard coded for now
        DatePicker datePicker = new DatePicker("Start date");
        datePicker.setReadOnly(true);
        datePicker.setLabel("Read-only");
        datePicker.setValue(LocalDate.of(2024, 1, 1));

        return datePicker;
    }
}
