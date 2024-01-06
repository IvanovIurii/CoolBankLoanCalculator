package org.example.loancalculator.ui.components.common;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.Converter;

import java.util.List;
import java.util.function.Function;

public abstract class AbstractFormView<T> extends FormLayout {
    private final BeanValidationBinder<T> binder = new BeanValidationBinder<>(getBeanType());

    protected void bind(InputField inputField, String propertyName, Function<String, Converter<String, ?>> converterFunction) {
        forFieldAsRequired(inputField)
                .withConverter(converterFunction.apply(inputField.getLabel() + " is invalid"))
                .bind(propertyName);
    }

    protected void bind(InputField inputField, String propertyName) {
        forFieldAsRequired(inputField)
                .bind(propertyName);
    }

    private Binder.BindingBuilder<T, String> forFieldAsRequired(InputField inputField) {
        return binder.forField(inputField)
                .asRequired(inputField.getLabel() + " is required");
    }

    protected abstract Class<T> getBeanType();

    protected List<ValidationResult> getValidationErrors() {
        return binder.validate().getValidationErrors();
    }
}
