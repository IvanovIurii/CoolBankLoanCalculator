package org.example.loancalculator.ui;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.example.loancalculator.model.Payment;
import org.example.loancalculator.model.RequestPayloadWithClient;
import org.example.loancalculator.service.ClientService;
import org.example.loancalculator.service.LoanCalculator;
import org.example.loancalculator.service.LoanService;
import org.example.loancalculator.ui.components.LoanConditionsFormView;
import org.example.loancalculator.ui.components.common.NotificationStatus;

import java.util.List;

@Route
public class MainView extends VerticalLayout {
    private static String VIEW_NAME = "Loan Calculator";
    private final LoanCalculator loanCalculator;
    private final ClientService clientService;
    private final LoanService loanService;

    public MainView(LoanCalculator loanCalculator, ClientService clientService, LoanService loanService) {
        this.loanCalculator = loanCalculator;
        this.clientService = clientService;
        this.loanService = loanService;

        LoanConditionsFormView form = new LoanConditionsFormView();
        form.addCalculateListener(this::calculate);
        add(new H2(VIEW_NAME), form);
    }

    private void calculate(LoanConditionsFormView.CalculateEvent event) {
        RequestPayloadWithClient payloadWithClient = event.getPayloadWithClient();
        if (payloadWithClient == null) {
            notifyError();
            return;
        }
        notifySuccess();

        List<Payment> payments = calculatePaymentsPlan(
                payloadWithClient.getLoanAmount(),
                payloadWithClient.getNominalRate(),
                payloadWithClient.getDuration(),
                payloadWithClient.getStartDate()
        );
        savePaymentForClient(payloadWithClient.getEmail(), payments);
        Grid<Payment> grid = configureGrid();
        grid.setItems(payments);
        add(grid);
    }

    // todo: @Transactional and in a service
    private void savePaymentForClient(String email, List<Payment> payments) {
        loanService.savePayments(payments);
        clientService.addClient(email);
    }

    private List<Payment> calculatePaymentsPlan(double loanAmount, double nominalRate, int durationInMonths, String date) {
        return loanCalculator.generatePaymentPlan(
                loanAmount,
                nominalRate,
                durationInMonths,
                date
        );
    }

    private void notifySuccess() {
        notify("Calculating request was made", NotificationVariant.LUMO_PRIMARY);
    }

    private void notifyError() {
        notify("Please fix the validation errors", NotificationVariant.LUMO_ERROR);
    }

    private void notify(String message, NotificationVariant notificationType) {
        NotificationStatus.show(
                message,
                3000,
                Notification.Position.TOP_CENTER,
                notificationType
        );
    }

    private Grid<Payment> configureGrid() {
        Grid<Payment> grid = new Grid<>(Payment.class, false);
        grid.addColumn(Payment::date).setHeader("Date");
        grid.addColumn(Payment::borrowerPaymentAmount).setHeader("Payment Amount");
        grid.addColumn(Payment::principal).setHeader("Principal");
        grid.addColumn(Payment::interest).setHeader("Interest");
        grid.addColumn(Payment::remainingOutstandingPrincipal).setHeader("Remaining");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        return grid;
    }
}
