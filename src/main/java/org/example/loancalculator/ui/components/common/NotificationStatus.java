package org.example.loancalculator.ui.components.common;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

public class NotificationStatus extends Notification {
    public static void show(String message, int duration, Notification.Position position, NotificationVariant variant) {
        Notification notification = new Notification();
        notification.addThemeVariants(variant);
        notification.setDuration(duration);
        notification.setPosition(position);
        notification.add(message);
        notification.open();
    }
}
