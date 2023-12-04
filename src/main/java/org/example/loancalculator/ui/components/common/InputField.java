package org.example.loancalculator.ui.components.common;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;

public class InputField extends TextField {
    InputField(String label, String prefix, String placeholder, Runnable onValueChangeAction) {
        super(label);
        this.setPlaceholder(placeholder);
        this.setPrefix(prefix);

        if (onValueChangeAction != null) {
            this.addValueChangeListener(e -> onValueChangeAction.run());
        }
    }

    private void setPrefix(String prefix) {
        Div prefixComponent = new Div();
        prefixComponent.setText(prefix);
        this.setPrefixComponent(prefixComponent);
    }
}
