package com.dibyendu.dashboard.ui.view;

import com.dibyendu.entity.UserEntity;
import com.dibyendu.models.CountryCode;
import com.dibyendu.models.UpdateUserDto;
import com.dibyendu.service.UserService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Route("/users")
public class UserListView extends Main {
    private final Grid<UserEntity> userEntityGrid = new Grid<>(UserEntity.class, false);
    private final TextField filterText = new TextField();

    private final UserService userService;
    public UserListView(UserService userService) {
        this.userService = userService;
        setSizeFull();
        addClassName("user-list-view");
        Button changeThemeButton = new Button(new Icon(VaadinIcon.SUN_RISE));
//        changeThemeButton.addClickListener(e -> toggleTheme(changeThemeButton));
        H2 title = new H2("Manage Users");
        H4 description = new H4("View and manage users");

        VerticalLayout titleNdDesc = new VerticalLayout(title, description);
        title.addClassName("page-title");

        Button addUser = new Button("Add user", new Icon(VaadinIcon.PLUS_CIRCLE));
        addUser.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
//        Button addUser = createUserDialog();
        addUser.addClassName("add-user-btn");
        addUser.addClickListener(e -> userDtlsDialog("Add user", new UserEntity()).open());

        HorizontalLayout header = new HorizontalLayout(titleNdDesc, addUser, changeThemeButton);
        header.setMargin(true);

        add(header);
        configureGrid();

        VerticalLayout content = new VerticalLayout(
                getToolBar(),
                userEntityGrid
        );
        content.setSizeFull();
        content.setPadding(true);
        content.setSpacing(true);
        content.setAlignItems(FlexComponent.Alignment.STRETCH);

//        add(content);

        Card userDetailsCard = new Card();
        userDetailsCard.add(
                content
        );

        add(userDetailsCard);
    }

    private void toggleTheme(Button button) {
        UI ui = UI.getCurrent();
        Element element = ui.getElement();

        String currentTheme = element.getAttribute("theme");
        boolean isDark = "dark".equals(currentTheme);

        if (isDark) {
            element.removeAttribute("theme");
            button.setText("🌞 Light Mode");
        } else {
            element.setAttribute("theme", "dark");
            button.setText("🌙 Dark Mode");
        }
    }


    private Component getToolBar() {
        filterText.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        filterText.setPlaceholder("Search by email...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.setWidth("250px"); // compact width for input field

        // Responsive flex toolbar
        FlexLayout toolbar = new FlexLayout();
        toolbar.add(filterText);
        toolbar.addClassName("toolbar");
        toolbar.setWidthFull();
        toolbar.setJustifyContentMode(FlexLayout.JustifyContentMode.BETWEEN);
        toolbar.setAlignItems(FlexComponent.Alignment.CENTER);
        toolbar.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        toolbar.getStyle().set("gap", "var(--lumo-space-m)");

        return toolbar;
    }

    private void configureGrid() {
        userEntityGrid.addClassName("user-grid");
        userEntityGrid.setEmptyStateText("No users found.");
        userEntityGrid.setSizeFull();
        userEntityGrid.setAllRowsVisible(true);
        userEntityGrid.setColumns("name","email","role");
        userEntityGrid.addColumn(userEntity ->
                userEntity.getCountryCode() + userEntity.getPhoneNumber()
        ).setHeader("Phone number");
        userEntityGrid.addComponentColumn(userEntity -> {
            MenuBar menuBar = new MenuBar();
            menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);

            Icon menubarIcon = new Icon(VaadinIcon.ELLIPSIS_DOTS_V);
            menubarIcon.setColor("black");
            menubarIcon.setSize("20px");
            Button dotsButton = new Button(menubarIcon);
            dotsButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE); // small, icon-only look

            MenuItem action = menuBar.addItem(dotsButton);
            SubMenu subMenu = action.getSubMenu();

            // Update option
            subMenu.addItem("Update", e -> {
                userDtlsDialog("Update user details",userEntity).open(); // You’ll define this method below
            });

            // Delete option
            subMenu.addItem("Delete", e -> {
                ConfirmDialog deleteUserDialog = new ConfirmDialog();
                deleteUserDialog.setHeader("Delete User");
                deleteUserDialog.setText("Are you sure you want to delete " + userEntity.getName() + "?");
                deleteUserDialog.setCancelable(true);
                deleteUserDialog.setConfirmText("Delete");
                deleteUserDialog.setConfirmButtonTheme("error primary");
                deleteUserDialog.addConfirmListener(event -> {
                    userService.delete(userEntity.getId());
                    userEntityGrid.setItems(userService.findAll());
                    Notification userDelNotif = new Notification();
                    userDelNotif.setPosition(Notification.Position.TOP_CENTER);
                    userDelNotif.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
                    Icon icon = VaadinIcon.CHECK_CIRCLE.create();
                    var layout = new HorizontalLayout(icon,
                            new Text("User Deleted!"));
                    layout.setAlignItems(FlexComponent.Alignment.CENTER);
                    userDelNotif.add(layout);
                    userDelNotif.setDuration(3000);
                    userDelNotif.open();
                });
                deleteUserDialog.open();
            });

            return menuBar;
        }).setHeader("Action");
        userEntityGrid.getColumns().forEach(col-> col.setAutoWidth(true));
//        userEntityGrid.addColumn(UserEntity::getName).setHeader("Name").setAutoWidth(true);
//        userEntityGrid.addColumn(UserEntity::getPhoneNumber).setHeader("Phone Number").setAutoWidth(true);
//        userEntityGrid.addColumn(UserEntity::getEmail).setHeader("Email").setAutoWidth(true);

        userEntityGrid.getStyle().set("border-radius", "var(--lumo-border-radius-m)");
        userEntityGrid.getStyle().set("box-shadow", "0 2px 8px rgba(0, 0, 0, 0.1)");
        List<UserEntity> userEntityList = userService.findAll();
        userEntityGrid.setItems(userEntityList);
    }

    private Dialog userDtlsDialog(String dialogTitle, UserEntity userEntity){
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(dialogTitle);

        TextField nameField = new TextField("Name");
        nameField.setWidthFull();

        EmailField emailField = new EmailField("Email");
        emailField.setWidthFull();

        Select<CountryCode> countryCodeSelect = new Select<>();
        countryCodeSelect.setWidth("130px");
        countryCodeSelect.setLabel("Country code");
        countryCodeSelect.setItems(
                new CountryCode("+1", "US"),
                new CountryCode("+44", "UK"),
                new CountryCode("+91", "IN"),
                new CountryCode("+61", "AU"),
                new CountryCode("+81", "JP")
        );
        countryCodeSelect.setValue(new CountryCode("+91", "IN"));

        Select<String> userRoleSelect = new Select<>();
        userRoleSelect.setPlaceholder("Select user role");
        userRoleSelect.setItems("user","reviewer");
        userRoleSelect.setWidthFull();

        NumberField phoneNumberField = new NumberField("Phone Number");
        phoneNumberField.setWidthFull();

        PasswordField passwordField = new PasswordField("Password");
        passwordField.setWidthFull();
        passwordField.setClearButtonVisible(true);
        passwordField.setPrefixComponent(VaadinIcon.LOCK.create());

        HorizontalLayout phoneLayout = new HorizontalLayout(countryCodeSelect, phoneNumberField);
        phoneLayout.setWidthFull();
        phoneLayout.setFlexGrow(1, phoneNumberField);
        phoneLayout.setAlignItems(FlexComponent.Alignment.END);

        Button saveButton = new Button("Add");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancel");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_WARNING);
        cancelButton.addClickListener(e -> dialogClose(dialog));
        cancelButton.addThemeVariants(ButtonVariant.LUMO_WARNING);
        cancelButton.addClickListener(e -> dialogClose(dialog));

        FormLayout formLayout;

        if (userEntity.getId()!=null){
            nameField.setValue(userEntity.getName());
            emailField.setValue(userEntity.getEmail());
//            countryCode.setValue(userEntity.getCountryCode());
            phoneNumberField.setPrefixComponent(new Span(userEntity.getCountryCode()));
            phoneNumberField.setValue(Double.valueOf(userEntity.getPhoneNumber()));
            userRoleSelect.setValue(userEntity.getRole());
            saveButton.setText("update");

            formLayout = new FormLayout(nameField, emailField, phoneNumberField, userRoleSelect);

            // functionality of updating user
            saveButton.addClickListener(
                    updateClickEvent->{
                        UpdateUserDto updateUserDto = new UpdateUserDto();
                        updateUserDto.setId(userEntity.getId());
                        updateUserDto.setEmail(emailField.getValue());
                        updateUserDto.setPhone(String.format("%.0f", phoneNumberField.getValue()));
                        updateUserDto.setName(nameField.getValue());
                        updateUserDto.setRole(userRoleSelect.getValue());
                        boolean update = userService.update(updateUserDto);
                        if (update){
                            Notification successfulUpdateNotif = Notification.show("User updated");
                            successfulUpdateNotif.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                            successfulUpdateNotif.setPosition(Notification.Position.TOP_CENTER);
                            userEntityGrid.setItems(userService.findAll());
                            dialogClose(dialog);
                        }else {
                            Notification unSuccessfulUpdateNotif = Notification.show("Error while updating user");
                            unSuccessfulUpdateNotif.addThemeVariants(NotificationVariant.LUMO_ERROR);
                            unSuccessfulUpdateNotif.setPosition(Notification.Position.TOP_CENTER);
                        }
                    }
            );
        }else {
            nameField.setPlaceholder("Enter full name");
            emailField.setPlaceholder("Enter email address");
            passwordField.setPlaceholder("Enter password");
            countryCodeSelect.setPlaceholder("Select country code");
            phoneNumberField.setPlaceholder("Enter phone number");
            userRoleSelect.setPlaceholder("Select user role");

            formLayout = new FormLayout(nameField, emailField, passwordField, phoneLayout, userRoleSelect);

            // Functionality of adding user
            saveButton.addClickListener(
                    buttonClickEvent -> {
                        if (nameField.isEmpty() || emailField.isEmpty() || passwordField.isEmpty() || phoneNumberField.isEmpty()) {
                            Notification.show("Name, Email, phone number are required!");
                        } else {
                            String phoneValue = String.format("%.0f", phoneNumberField.getValue());

                            log.info("Setting values for user entity.");
                            userEntity.setName(nameField.getValue());
                            userEntity.setEmail(emailField.getValue());
                            userEntity.setPassword(passwordField.getValue());
                            userEntity.setPhoneNumber(phoneValue);
                            userEntity.setCountryCode(countryCodeSelect.getValue().getCode());
                            userEntity.setRole(userRoleSelect.getValue());
                            log.info(userEntity.toString());
                            UserEntity savedUser = userService.save(userEntity);

                            if (savedUser.getId() != null){

                                Notification userCreateNotification = new Notification();
                                userCreateNotification.setPosition(Notification.Position.TOP_CENTER);
                                userCreateNotification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                                Icon userCreateNotificationIcon = VaadinIcon.CHECK_CIRCLE.create();
                                Text userCreateNotificationText = new Text("User added: "+emailField.getValue());
                                HorizontalLayout userCreateNotificationLayout = new HorizontalLayout(userCreateNotificationIcon, userCreateNotificationText);
                                userCreateNotificationLayout.setAlignItems(FlexComponent.Alignment.CENTER);

                                userCreateNotification.add(userCreateNotificationLayout);
                                userCreateNotification.setDuration(3000);
                                userCreateNotification.open();

                                userEntityGrid.setItems(userService.findAll());
                                dialogClose(dialog);
                            }else {
                                Notification errorNotif = Notification.show("Error while adding user");
                                errorNotif.addThemeVariants(NotificationVariant.LUMO_ERROR);
                                errorNotif.setPosition(Notification.Position.TOP_CENTER);
                            }

                        }
                    }
            );
        }

        formLayout.setWidth("400px");
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1)
        );



        // --- Footer Layout ---
        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);
        buttonLayout.setWidthFull(); // full width footer
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        buttonLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        buttonLayout.setSpacing(true);
        buttonLayout.getStyle().set("margin-top", "var(--lumo-space-m)");

// --- Dialog Layout ---
        VerticalLayout dialogLayout = new VerticalLayout(formLayout);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);

        dialog.add(dialogLayout);
        dialog.getFooter().add(buttonLayout);

        return dialog;

    }

    private void dialogClose(Dialog dialog){
//        nameField.clear();
//        emailField.clear();
//        phoneField.clear();
//        passwordField.clear();
//        countryCodeSelect.setValue("+91 (IN)");
        dialog.close();
    }
}
