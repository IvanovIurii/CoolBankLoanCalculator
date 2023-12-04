package org.example.loancalculator.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import org.example.loancalculator.model.Payment;
import org.example.loancalculator.model.RequestPayload;
import org.example.loancalculator.service.LoanCalculator;
import org.example.loancalculator.ui.components.LoanConditionsFormView;
import org.example.loancalculator.ui.components.common.NotificationStatus;

import java.util.List;
import java.util.Optional;

@Route
public class MainView extends VerticalLayout {

    public MainView(LoanCalculator loanCalculator) {
        LoanConditionsFormView form = new LoanConditionsFormView();
        Button button = new Button("Calculate");
        button.addClickListener(click -> {
            Optional<RequestPayload> optionalRequestPayload = form.getRequestPayload();
            if (optionalRequestPayload.isPresent()) {
                NotificationStatus.show(
                        "Calculating request was made",
                        3000,
                        Notification.Position.TOP_CENTER,
                        NotificationVariant.LUMO_PRIMARY
                );
                RequestPayload payload = optionalRequestPayload.get();
                // todo: just accept the object RequestPayload in LoanCalculator::generatePaymentPlan
                List<Payment> payments = loanCalculator.generatePaymentPlan(
                        payload.getLoanAmount(),
                        payload.getNominalRate(),
                        payload.getDuration(),
                        payload.getStartDate()
                );
                Grid<Payment> grid = initPaymentPlanGrid();
                grid.setItems(payments);
                add(grid);
            } else {
                NotificationStatus.show(
                        "Please fix the validation errors",
                        3000,
                        Notification.Position.TOP_CENTER,
                        NotificationVariant.LUMO_ERROR
                );
            }
        });
        add(form, button);
    }

    private Grid<Payment> initPaymentPlanGrid() {
        Grid<Payment> grid = new Grid<>(Payment.class, false);
        grid.addColumn(Payment::date).setHeader("Date");
        grid.addColumn(Payment::borrowerPaymentAmount).setHeader("Payment Amount");
        grid.addColumn(Payment::principal).setHeader("Principal");
        grid.addColumn(Payment::interest).setHeader("Interest");
        grid.addColumn(Payment::remainingOutstandingPrincipal).setHeader("Remaining");

        return grid;
    }
}
