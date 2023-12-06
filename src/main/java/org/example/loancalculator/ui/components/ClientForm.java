package org.example.loancalculator.ui.components;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import org.example.loancalculator.entity.Client;
import org.example.loancalculator.ui.components.common.InputField;
import org.example.loancalculator.ui.components.common.InputTextFieldBuilder;

// todo: add abstract form class
public class ClientForm extends FormLayout {
    private static final String FORM_LABEL = "Client";

    private final BeanValidationBinder<Client> binder = new BeanValidationBinder<>(Client.class);
    private final InputTextFieldBuilder builder = InputTextFieldBuilder.getInstance();

    InputField emailField = builder
            .withLabel("Email")
            .onValueChange(binder::validate)
            .build();

    public ClientForm() {
        bind(emailField, "email");

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.add(
                new H2(FORM_LABEL),
                emailField
        );
        add(verticalLayout);
    }

    private void bind(InputField inputField, String propertyName) {
        binder.forField(inputField)
                .asRequired(inputField.getLabel() + " is required")
                .bind(propertyName);
    }

    // todo: add event what to do when the button is clicked
}
