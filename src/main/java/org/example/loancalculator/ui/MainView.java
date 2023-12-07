package org.example.loancalculator.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import org.example.loancalculator.entity.Client;
import org.example.loancalculator.model.Payment;
import org.example.loancalculator.model.RequestPayload;
import org.example.loancalculator.model.RequestPayloadWithClient;
import org.example.loancalculator.repository.ClientRepository;
import org.example.loancalculator.service.LoanCalculator;
import org.example.loancalculator.ui.components.LoanConditionsFormView;
import org.example.loancalculator.ui.components.common.NotificationStatus;

import java.util.List;
import java.util.Optional;

@Route
public class MainView extends VerticalLayout {

    private static String VIEW_NAME = "Loan Calculator";

    public MainView(LoanCalculator loanCalculator, ClientRepository clientRepository) {
        LoanConditionsFormView form = new LoanConditionsFormView();
        Button button = new Button("Calculate");
        button.addClickListener(click -> {
            Optional<RequestPayloadWithClient> optionalRequestPayload = form.getFormInputObject();
            if (optionalRequestPayload.isPresent()) {
                NotificationStatus.show(
                        "Calculating request was made",
                        3000,
                        Notification.Position.TOP_CENTER,
                        NotificationVariant.LUMO_PRIMARY
                );
                RequestPayload payload = optionalRequestPayload.get();
                // todo: save through the service, not direct repo call
                clientRepository.save(
                        new Client("test@example.com")
                );

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
        add(new H2(VIEW_NAME), form, button);
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
