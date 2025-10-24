package com.dibyendu.dashboard.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@Route
@RouteAlias("")
public class DashboardView extends Main {
    public DashboardView(){
        Button button1 = new Button("Hello");
        TextField textField1 = new TextField("Name");

        HorizontalLayout hl = new HorizontalLayout(textField1, button1);
        hl.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);

        // Adding functionality to button
        button1.addClickListener(buttonClickEvent -> {
            Notification notif = Notification.show("Hello " + textField1.getValue());
            notif.setPosition(Notification.Position.TOP_CENTER);
            notif.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
        });
        add(hl);
    }
}
