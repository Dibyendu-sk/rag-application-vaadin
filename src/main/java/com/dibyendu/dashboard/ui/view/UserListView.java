package com.dibyendu.dashboard.ui.view;

import com.dibyendu.entity.UserEntity;
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
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Main;
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
    // Input fields
    TextField nameField = new TextField("Name");
    EmailField emailField = new EmailField("Email");
    // Country code dropdown + phone number field
    Select<String> countryCodeSelect = new Select<>();
    NumberField phoneField = new NumberField("Phone number");
    PasswordField passwordField = new PasswordField("Password");
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
        addUser.addClickListener(e -> openAddUserDialog());

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
        userEntityGrid.setSizeFull();
        userEntityGrid.setAllRowsVisible(true);
        userEntityGrid.setColumns("name","phoneNumber","email");
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
                openUpdateUserDialog(userEntity); // You’ll define this method below
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

    private void openUpdateUserDialog(UserEntity userEntity) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Update user details");

        TextField updatedNameField = new TextField();
        updatedNameField.setValue(userEntity.getName());
        updatedNameField.setWidthFull();

        EmailField updatedEmailField = new EmailField();
        updatedEmailField.setValue(userEntity.getEmail());
        updatedEmailField.setWidthFull();
        updatedEmailField.setErrorMessage("Please enter a valid email");

        String fullPhone = userEntity.getPhoneNumber();
        String countryCode = "+91";
        String phoneNumber = "";
        if (fullPhone.startsWith("+1")) {
            countryCode = "+1 (US)";
            phoneNumber = fullPhone.substring(2);
        } else if (fullPhone.startsWith("+44")) {
            countryCode = "+44 (UK)";
            phoneNumber = fullPhone.substring(3);
        } else if (fullPhone.startsWith("+91")) {
            countryCode = "+91 (IN)";
            phoneNumber = fullPhone.substring(3);
        } else if (fullPhone.startsWith("+61")) {
            countryCode = "+61 (AU)";
            phoneNumber = fullPhone.substring(3);
        } else if (fullPhone.startsWith("+81")) {
            countryCode = "+81 (JP)";
            phoneNumber = fullPhone.substring(3);
        }

        Select<String> updatedCountryCodeSelect = new Select<>();
        updatedCountryCodeSelect.setLabel("Country Code");
        updatedCountryCodeSelect.setItems("+1 (US)", "+44 (UK)", "+91 (IN)", "+61 (AU)", "+81 (JP)");
        updatedCountryCodeSelect.setValue(countryCode);
        updatedCountryCodeSelect.setWidth("130px");

//        phoneField.setLabel("Phone Number");
        NumberField updatedPhoneField = new NumberField();
        updatedPhoneField.setValue(Double.valueOf(phoneNumber));
        phoneField.setWidthFull();

        HorizontalLayout updatedPhoneLayout = new HorizontalLayout(countryCodeSelect, phoneField);
        updatedPhoneLayout.setWidthFull();
        updatedPhoneLayout.setFlexGrow(1, phoneField);
        updatedPhoneLayout.setAlignItems(FlexComponent.Alignment.END);

        String updatedPhoneVal = String.format("%.0f", updatedPhoneField.getValue());
        String updatedFullPhone = updatedCountryCodeSelect.getValue().split(" ")[0]  + updatedPhoneVal;

        FormLayout formLayout = new FormLayout(nameField, emailField, updatedPhoneLayout);
        formLayout.setWidth("400px");
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1)
        );

        Button updateButton = new Button("update");
        Button cancelButton = new Button("cancel");

        updateButton.addClickListener(
                e -> {
                    UpdateUserDto updateUserDto = new UpdateUserDto();
                    updateUserDto.setId(userEntity.getId());
                    updateUserDto.setEmail(updatedEmailField.getValue());
                    updateUserDto.setPhone(updatedFullPhone);

                    boolean update = userService.update(updateUserDto);
                    if (update){
                        Notification successfulUpdateNotif = Notification.show("User updated");
                        successfulUpdateNotif.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                        successfulUpdateNotif.setPosition(Notification.Position.TOP_END);
                    }else {
                        Notification unSuccessfulUpdateNotif = Notification.show("Error while updating user");
                        unSuccessfulUpdateNotif.addThemeVariants(NotificationVariant.LUMO_ERROR);
                        unSuccessfulUpdateNotif.setPosition(Notification.Position.TOP_END);
                    }
                }
        );
        HorizontalLayout buttonLayout = new HorizontalLayout(updateButton, cancelButton);
        buttonLayout.setWidthFull(); // full width footer
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        buttonLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        buttonLayout.setSpacing(true);
        buttonLayout.getStyle().set("margin-top", "var(--lumo-space-m)");

        VerticalLayout dialogLayout = new VerticalLayout(formLayout);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);

        dialog.add(dialogLayout);
        dialog.getFooter().add(buttonLayout);
        dialog.open();
    }

    private void openAddUserDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Add New User");
//        dialog.setModal(true);
        dialog.setResizable(false);
//        dialog.setDraggable(true);


        nameField.setPlaceholder("Enter name");
        nameField.setWidthFull();

        emailField.setPlaceholder("Enter email");
        emailField.setWidthFull();
        emailField.setErrorMessage("Please enter a valid email");


        countryCodeSelect.setLabel("Country Code");
        countryCodeSelect.setItems("+1 (US)", "+44 (UK)", "+91 (IN)", "+61 (AU)", "+81 (JP)");
        countryCodeSelect.setValue("+91 (IN)");
        countryCodeSelect.setWidth("130px");

//        phoneField.setLabel("Phone Number");
        phoneField.setPlaceholder("Enter phone number");
        phoneField.setWidthFull();

        HorizontalLayout phoneLayout = new HorizontalLayout(countryCodeSelect, phoneField);
        phoneLayout.setWidthFull();
        phoneLayout.setFlexGrow(1, phoneField);
        phoneLayout.setAlignItems(FlexComponent.Alignment.END);

//        passwordField.setLabel("Password");
        passwordField.setPlaceholder("Enter password");
//        passwordField.setTooltipText("Tooltip text");
        passwordField.setClearButtonVisible(true);
        passwordField.setPrefixComponent(VaadinIcon.LOCK.create());
        passwordField.setWidthFull();

        // Form layout
        FormLayout formLayout = new FormLayout(nameField, emailField, passwordField, phoneLayout);
        formLayout.setWidth("400px");
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1)
        );

        // Buttons
        Button saveButton = new Button("Save", new Icon(VaadinIcon.CHECK));
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClassName("add-user-btn");
        saveButton.addClickListener(e -> {
            if (nameField.isEmpty() || emailField.isEmpty() || passwordField.isEmpty() || phoneField.isEmpty()) {
                Notification.show("Name, Email, phone number are required!");
            } else {
                String phoneValue = String.format("%.0f", phoneField.getValue());
                String fullPhone = countryCodeSelect.getValue().split(" ")[0]  + phoneValue;

                log.info("Setting values for user entity.");
                UserEntity userEntity = new UserEntity();
                userEntity.setName(nameField.getValue());
                userEntity.setEmail(emailField.getValue());
                userEntity.setPassword(passwordField.getValue());
                userEntity.setPhoneNumber(fullPhone);

                log.info(userEntity.toString());
                UserEntity savedUser = userService.save(userEntity);

                if (savedUser.getId() != null){
                    Notification.show("User added: " + nameField.getValue() + " (" + fullPhone + ")").setPosition(Notification.Position.TOP_STRETCH);
                    userEntityGrid.setItems(userService.findAll());
                    dialogClose(dialog);
                }

            }
        });

        Button cancelButton = new Button("Cancel", new Icon(VaadinIcon.CLOSE));
        cancelButton.addThemeVariants(ButtonVariant.LUMO_WARNING);
        cancelButton.addClickListener(e -> dialogClose(dialog));

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

// --- Add to dialog ---
        dialog.add(dialogLayout);
        dialog.getFooter().add(buttonLayout);
        dialog.open();
    }

    private void dialogClose(Dialog dialog){
        nameField.clear();
        emailField.clear();
        phoneField.clear();
        passwordField.clear();
        countryCodeSelect.setValue("+91 (IN)");
        dialog.close();
    }
}
