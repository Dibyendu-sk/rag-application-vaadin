package com.dibyendu.dashboard.ui.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;

import java.util.LinkedHashMap;
import java.util.Map;

public class UserForm extends FormLayout {
    private final Map<String, String> countryCodeMap = new LinkedHashMap<>();

    TextField name = new TextField("Name");
    EmailField email = new EmailField("Email");
    Button save = new Button("save");
    Button delete = new Button("delete");
    Button cancel = new Button("cancel");



    public UserForm() {
        add(
                name,
                email,
                getPhoneNumberInput(),
                getButtonLayout()
        );
    }

    private Component getButtonLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        cancel.addClickShortcut(Key.ESCAPE);

        return new HorizontalLayout(save, delete, cancel);
    }

    // Phone number input layout
    private Component getPhoneNumberInput(){
        countryCodeMap.put("🇺🇸 +1", "+1");
        countryCodeMap.put("🇬🇧 +44", "+44");
        countryCodeMap.put("🇮🇳 +91", "+91");
        countryCodeMap.put("🇫🇷 +33", "+33");
        countryCodeMap.put("🇩🇪 +49", "+49");

        ComboBox<String> countryCodeDropdown = new ComboBox<>();
        countryCodeDropdown.setLabel("Country Code");
        countryCodeDropdown.setItems(countryCodeMap.keySet());
        countryCodeDropdown.setWidth("150px");
        countryCodeDropdown.setAllowCustomValue(false);
        countryCodeDropdown.setPlaceholder("Select");

        TextField phoneNumberField = new TextField("Phone Number");
        phoneNumberField.setPlaceholder("1234567890");
        phoneNumberField.setWidth("200px");

        HorizontalLayout phoneInputLayout = new HorizontalLayout(countryCodeDropdown, phoneNumberField);
        phoneInputLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);

        add(phoneInputLayout);

        phoneNumberField.addValueChangeListener(e -> {
            String code = countryCodeMap.getOrDefault(countryCodeDropdown.getValue(), "");
            String number = phoneNumberField.getValue();
            String fullNumber = code + number;

            System.out.println("Full Number: " + fullNumber);
        });

        return phoneInputLayout;
    }
}
