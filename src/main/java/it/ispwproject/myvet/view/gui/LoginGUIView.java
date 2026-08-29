package it.ispwproject.myvet.view.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class LoginGUIView {

    public final TextField emailField =
            new TextField();

    public final PasswordField passwordField =
            new PasswordField();

    public final TextField visiblePasswordField =
            new TextField();

    public final Label errorLabel =
            new Label("");

    public final Button loginBtn =
            new Button("Accedi");

    public LoginGUIView() {
        configureEmailField();
        configurePasswordFields();
        configureErrorLabel();
        configureLoginButton();
    }

    // Configurazione del campo email
    private void configureEmailField() {
        emailField.setPromptText(
                "Inserisci email"
        );

        emailField.setPrefWidth(250);
        emailField.setPrefHeight(48);
    }

    // Configurazione dei campi password nascosta e visibile
    private void configurePasswordFields() {
        passwordField.setPromptText(
                "Inserisci password"
        );

        passwordField.setPrefWidth(250);
        passwordField.setPrefHeight(48);

        visiblePasswordField.setPromptText(
                "Inserisci password"
        );

        visiblePasswordField.setPrefWidth(250);
        visiblePasswordField.setPrefHeight(48);
        visiblePasswordField.setVisible(false);

        visiblePasswordField.managedProperty().bind(
                visiblePasswordField.visibleProperty()
        );

        passwordField.managedProperty().bind(
                passwordField.visibleProperty()
        );

        // I due campi condividono lo stesso contenuto
        visiblePasswordField.textProperty()
                .bindBidirectional(
                        passwordField.textProperty()
                );
    }

    // Configurazione dell'etichetta usata per mostrare gli errori
    private void configureErrorLabel() {
        errorLabel.setWrapText(true);

        errorLabel.getStyleClass().add(
                "error-label"
        );
    }

    // Configurazione del pulsante di accesso
    private void configureLoginButton() {
        loginBtn.getStyleClass().add(
                "button"
        );

        loginBtn.setPrefWidth(95);
        loginBtn.setPrefHeight(42);
    }

    // Costruisce la schermata completa di login
    public HBox buildRoot(
            Runnable onLogin,
            Runnable onRegister) {

        HBox root = new HBox(75);

        root.setAlignment(Pos.CENTER);

        root.setPadding(
                new Insets(25, 40, 25, 40)
        );

        root.getStyleClass().add(
                "myvet-background"
        );

        root.getChildren().addAll(
                buildLeftPanel(),
                buildRightPanel(
                        onLogin,
                        onRegister
                )
        );

        return root;
    }

    // Costruisce il pannello con logo e identità di MyVet
    private VBox buildLeftPanel() {
        VBox panel = new VBox(12);

        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(10));

        Label welcomeLabel =
                new Label("Benvenuto su");

        welcomeLabel.getStyleClass().add(
                "title-label"
        );

        ImageView logoView = buildLogo();

        Label brandLabel =
                new Label("MyVet");

        brandLabel.getStyleClass().add(
                "brand-label"
        );

        Label taglineLabel =
                new Label(
                        "La salute del tuo animale, sempre con te!"
                );

        taglineLabel.getStyleClass().add(
                "subtitle-label"
        );

        panel.getChildren().addAll(
                welcomeLabel,
                logoView,
                brandLabel,
                taglineLabel
        );

        return panel;
    }

    // Carica il logo senza generare errori se la risorsa non è disponibile
    private ImageView buildLogo() {
        ImageView logoView =
                new ImageView();

        var logoStream =
                getClass().getResourceAsStream(
                        "/images/logo.png"
                );

        if (logoStream != null) {
            logoView.setImage(
                    new Image(logoStream)
            );
        }

        logoView.setFitWidth(145);
        logoView.setPreserveRatio(true);
        logoView.setSmooth(true);

        return logoView;
    }

    // Costruisce il pannello contenente il form di accesso
    private VBox buildRightPanel(
            Runnable onLogin,
            Runnable onRegister) {

        VBox panel = new VBox(14);

        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(20));
        panel.setMaxWidth(300);

        // Email
        Label emailLabel =
                fieldLabel("Email");

        emailField.setOnAction(
                event ->
                        passwordField.requestFocus()
        );

        VBox emailBox = new VBox(
                5,
                emailLabel,
                emailField
        );

        emailBox.setAlignment(
                Pos.CENTER_LEFT
        );

        // Password
        Label passwordLabel =
                fieldLabel("Password");

        passwordField.setOnAction(
                event -> onLogin.run()
        );

        visiblePasswordField.setOnAction(
                event -> onLogin.run()
        );

        StackPane passwordBox =
                new StackPane(
                        passwordField,
                        visiblePasswordField
                );

        CheckBox showPasswordCheck =
                new CheckBox("Mostra password");

        showPasswordCheck.selectedProperty().addListener(
                (observable, oldValue, showPassword) -> {

                    visiblePasswordField.setVisible(
                            showPassword
                    );

                    passwordField.setVisible(
                            !showPassword
                    );
                }
        );

        VBox passwordContainer = new VBox(
                5,
                passwordLabel,
                passwordBox,
                showPasswordCheck
        );

        passwordContainer.setAlignment(
                Pos.CENTER_LEFT
        );

        // Bottone di accesso
        loginBtn.setOnAction(
                event -> onLogin.run()
        );

        // Collegamento alla registrazione
        Label registrationLabel =
                new Label(
                        "Non hai ancora un account?"
                );

        registrationLabel.getStyleClass().add(
                "register-label"
        );

        Hyperlink registrationLink =
                new Hyperlink("Registrati qui");

        registrationLink.setOnAction(
                event -> onRegister.run()
        );

        VBox registrationBox = new VBox(
                0,
                registrationLabel,
                registrationLink
        );

        registrationBox.setAlignment(Pos.CENTER);

        panel.getChildren().addAll(
                emailBox,
                passwordContainer,
                errorLabel,
                loginBtn,
                registrationBox
        );

        return panel;
    }

    // Mostra un messaggio di errore nel form
    public void setError(String message) {
        errorLabel.setText(message);
    }

    // Costruisce l'etichetta di un campo del form
    private Label fieldLabel(String text) {
        Label label = new Label(text);

        label.getStyleClass().add(
                "field-label"
        );

        return label;
    }
}