package org.example.loancalculator.ui.components.common;

public class InputTextFieldBuilder {

    private String label;
    private String prefix;
    private String placeholder;
    private Runnable onValueChangeAction;

    public static InputTextFieldBuilder getInstance() {
        return new InputTextFieldBuilder();
    }

    public InputTextFieldBuilder withLabel(String label) {
        this.label = label;
        return this;
    }

    public InputTextFieldBuilder withPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public InputTextFieldBuilder withPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    public InputTextFieldBuilder onValueChange(Runnable action) {
        this.onValueChangeAction = action;
        return this;
    }

    public InputField build() {
        return new InputField(label, prefix, placeholder, onValueChangeAction);
    }
}
