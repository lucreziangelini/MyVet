package it.ispwproject.myvet.view.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class RegistrationGUIView {

    private static final double FORM_WIDTH = 880;

    private static final double VETERINARIAN_SECTION_WIDTH =
            840;
    private static final String ROLE_RADIO_STYLE = "role-radio";

    // Campi esposti al controller
    public final TextField nameField =
            new TextField();

    public final TextField surnameField =
            new TextField();

    public final TextField emailField =
            new TextField();

    public final PasswordField passwordField =
            new PasswordField();

    public final PasswordField confirmPasswordField =
            new PasswordField();

    public final TextField visiblePasswordField =
            new TextField();

    public final TextField visibleConfirmPasswordField =
            new TextField();

    public final RadioButton petOwnerRadio =
            new RadioButton("Proprietario di un animale");

    public final RadioButton veterinarianRadio =
            new RadioButton("Veterinario");

    public final RadioButton femaleRadio =
            new RadioButton("Donna");

    public final RadioButton maleRadio =
            new RadioButton("Uomo");

    // Campi utilizzati solamente per la registrazione del veterinario
    public final TextArea bioField =
            new TextArea();

    public final TextField specializationField =
            new TextField();

    public final Label errorLabel =
            new Label("");

    public final Button registerBtn =
            new Button("Registrati");

    private VBox veterinarianSection;

    private final Label registrationTitle =
            new Label("Crea un account proprietario");

    private final Label registrationSubtitle =
            new Label(
                    "Registra i tuoi animali e prenota le visite veterinarie."
            );

    public RegistrationGUIView() {
        configurePasswordFields();
        configureErrorLabel();
        configureRegisterButton();
        configureVeterinarianFields();
    }

    // Configura i campi per mostrare o nascondere le password
    private void configurePasswordFields() {
        visiblePasswordField.setVisible(false);
        visibleConfirmPasswordField.setVisible(false);

        visiblePasswordField.managedProperty().bind(
                visiblePasswordField.visibleProperty()
        );

        visibleConfirmPasswordField.managedProperty().bind(
                visibleConfirmPasswordField.visibleProperty()
        );

        passwordField.managedProperty().bind(
                passwordField.visibleProperty()
        );

        confirmPasswordField.managedProperty().bind(
                confirmPasswordField.visibleProperty()
        );

        visiblePasswordField.textProperty()
                .bindBidirectional(
                        passwordField.textProperty()
                );

        visibleConfirmPasswordField.textProperty()
                .bindBidirectional(
                        confirmPasswordField.textProperty()
                );
    }

    // Configura l'etichetta usata per mostrare gli errori
    private void configureErrorLabel() {
        errorLabel.setWrapText(true);

        errorLabel.getStyleClass().add(
                "error-label"
        );

        errorLabel.setMaxWidth(FORM_WIDTH);
    }

    // Configura il pulsante di registrazione
    private void configureRegisterButton() {
        registerBtn.getStyleClass().add(
                "button"
        );

        registerBtn.setPrefWidth(130);
        registerBtn.setPrefHeight(34);
    }

    // Configura i campi specifici del veterinario
    private void configureVeterinarianFields() {
        bioField.setPromptText(
                "Breve descrizione di te e delle tue competenze"
        );

        bioField.setPrefRowCount(3);
        bioField.setPrefWidth(
                VETERINARIAN_SECTION_WIDTH
        );

        bioField.setWrapText(true);

        specializationField.setPromptText(
                "Inserisci la tua specializzazione"
        );

        specializationField.setPrefWidth(
                VETERINARIAN_SECTION_WIDTH
        );

        specializationField.setPrefHeight(34);
    }

    // ────────────────────────────────────────────────────────────────────────
    // Costruzione della schermata di registrazione
    // ────────────────────────────────────────────────────────────────────────

    public ScrollPane buildRoot(Runnable onBack) {
        veterinarianSection =
                buildVeterinarianSection();

        veterinarianSection.setVisible(false);
        veterinarianSection.setManaged(false);

        bindRoleRadios();

        VBox root = new VBox(14);

        root.setPadding(
                new Insets(18, 40, 28, 40)
        );

        root.setAlignment(Pos.TOP_CENTER);

        root.getStyleClass().add(
                "myvet-background"
        );

        ScrollPane scrollPane =
                new ScrollPane();

        scrollPane.setFitToWidth(true);

        scrollPane.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scrollPane.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );

        scrollPane.getStyleClass().addAll(
                "transparent-scroll",
                "myvet-scroll"
        );

        scrollPane.viewportBoundsProperty().addListener(
                (observable, oldBounds, newBounds) ->
                        root.setMinHeight(
                                newBounds.getHeight()
                        )
        );

        // Intestazione ispirata allo storyboard, con marchio e ritorno al login
        HBox header = new HBox();

        header.setAlignment(Pos.CENTER);
        header.setMaxWidth(940);

        HBox brand = new HBox(8);
        brand.setAlignment(Pos.CENTER_LEFT);

        ImageView logoView = buildLogo();
        logoView.setFitWidth(48);
        logoView.setFitHeight(48);

        Label brandName = new Label("MyVet");
        brandName.getStyleClass().add("myvet-brand-name");
        brand.getChildren().addAll(logoView, brandName);

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        Button backButton =
                new Button("‹ Indietro");

        backButton.getStyleClass().add(
                "back-button"
        );

        backButton.setOnAction(
                event -> onBack.run()
        );

        header.getChildren().addAll(
                brand,
                headerSpacer,
                backButton
        );

        registrationTitle.getStyleClass().add(
                "auth-title"
        );

        registrationSubtitle.getStyleClass().add("auth-subtitle");
        registrationSubtitle.setWrapText(true);
        registrationSubtitle.setMaxWidth(680);

        GridPane form =
                buildForm();

        VBox formCard = new VBox(
                12,
                registrationTitle,
                registrationSubtitle,
                form,
                veterinarianSection,
                errorLabel,
                registerBtn
        );

        formCard.setAlignment(Pos.TOP_CENTER);
        formCard.setPadding(
                new Insets(24, 28, 24, 28)
        );
        formCard.setMaxWidth(940);
        formCard.getStyleClass().addAll(
                "auth-card",
                "storyboard-registration-card"
        );

        root.getChildren().addAll(
                header,
                formCard
        );

        scrollPane.setContent(root);

        return scrollPane;
    }

    // Carica il logo MyVet se la risorsa è disponibile
    private ImageView buildLogo() {
        ImageView logoView = new ImageView();

        var logoStream = getClass().getResourceAsStream(
                "/images/myvet_logo.png"
        );

        if (logoStream != null) {
            logoView.setImage(
                    new Image(logoStream, 90, 90, true, true)
            );
            logoView.setFitHeight(68);
            logoView.setFitWidth(68);
            logoView.setPreserveRatio(true);
            logoView.setSmooth(true);
        }

        return logoView;
    }

    // ────────────────────────────────────────────────────────────────────────
    // Costruzione del form principale
    // ────────────────────────────────────────────────────────────────────────

    private GridPane buildForm() {
        GridPane grid = new GridPane();

        grid.setHgap(16);
        grid.setVgap(8);
        grid.setPrefWidth(FORM_WIDTH);
        grid.setMaxWidth(FORM_WIDTH);
        grid.setAlignment(Pos.CENTER);

        configureMainFields();

        // Colonna con i dati personali
        VBox leftColumn = new VBox(
                6,
                fieldBlock("Nome *", nameField),
                fieldBlock("Cognome *", surnameField),
                buildGenderBlock(),
                fieldBlock("Email *", emailField),
                requiredLabel()
        );

        // Colonna con la selezione del ruolo
        VBox centerColumn = new VBox(12);

        centerColumn.setAlignment(
                Pos.TOP_LEFT
        );

        centerColumn.getChildren().addAll(
                fieldLabel("Ruolo *"),
                buildRoleBox()
        );

        // Controllo per mostrare o nascondere le password
        CheckBox showPasswords =
                new CheckBox("Mostra password");

        showPasswords.selectedProperty().addListener(
                (observable, oldValue, show) -> {

                    visiblePasswordField.setVisible(show);
                    passwordField.setVisible(!show);

                    visibleConfirmPasswordField.setVisible(show);
                    confirmPasswordField.setVisible(!show);
                }
        );

        HBox showPasswordsBox =
                new HBox(showPasswords);

        showPasswordsBox.setAlignment(
                Pos.CENTER_RIGHT
        );

        // Colonna con password e conferma password
        VBox rightColumn = new VBox(
                6,
                passwordBlock(),
                passwordRules(),
                confirmPasswordBlock(),
                showPasswordsBox
        );

        grid.add(leftColumn, 0, 0);
        grid.add(centerColumn, 1, 0);
        grid.add(rightColumn, 2, 0);

        ColumnConstraints firstColumn =
                new ColumnConstraints();

        firstColumn.setPrefWidth(250);

        ColumnConstraints secondColumn =
                new ColumnConstraints();

        secondColumn.setPrefWidth(300);

        ColumnConstraints thirdColumn =
                new ColumnConstraints();

        thirdColumn.setPrefWidth(265);

        grid.getColumnConstraints().addAll(
                firstColumn,
                secondColumn,
                thirdColumn
        );

        return grid;
    }

    private VBox buildGenderBlock() {
        ToggleGroup genderGroup = new ToggleGroup();
        femaleRadio.setToggleGroup(genderGroup);
        maleRadio.setToggleGroup(genderGroup);

        femaleRadio.getStyleClass().add(ROLE_RADIO_STYLE);
        maleRadio.getStyleClass().add(ROLE_RADIO_STYLE);

        HBox choices = new HBox(18, femaleRadio, maleRadio);
        choices.setAlignment(Pos.CENTER_LEFT);
        choices.setMinHeight(34);

        return new VBox(
                4,
                fieldLabel("Genere *"),
                choices
        );
    }

    // Configura dimensioni e prompt dei campi principali
    private void configureMainFields() {
        configureTextField(
                nameField,
                "Inserisci nome",
                250
        );

        configureTextField(
                surnameField,
                "Inserisci cognome",
                250
        );

        configureTextField(
                emailField,
                "Inserisci email",
                250
        );

        configureTextField(
                passwordField,
                "Inserisci password",
                265
        );

        configureTextField(
                confirmPasswordField,
                "Ripeti password",
                265
        );

        configureTextField(
                visiblePasswordField,
                "Inserisci password",
                265
        );

        configureTextField(
                visibleConfirmPasswordField,
                "Ripeti password",
                265
        );
    }

    private void configureTextField(
            TextField field,
            String prompt,
            double width) {

        field.setPromptText(prompt);
        field.setPrefWidth(width);
        field.setPrefHeight(30);
    }

    // ────────────────────────────────────────────────────────────────────────
    // Gestione del ruolo selezionato
    // ────────────────────────────────────────────────────────────────────────

    private void bindRoleRadios() {
        veterinarianRadio.setOnAction(event -> {
            veterinarianSection.setVisible(true);
            veterinarianSection.setManaged(true);
            registrationTitle.setText(
                    "Crea un account veterinario"
            );
            registrationSubtitle.setText(
                    "Presenta la tua specializzazione e la tua bio professionale."
            );
        });

        petOwnerRadio.setOnAction(event -> {
            veterinarianSection.setVisible(false);
            veterinarianSection.setManaged(false);
            registrationTitle.setText(
                    "Crea un account proprietario"
            );
            registrationSubtitle.setText(
                    "Registra i tuoi animali e prenota le visite veterinarie."
            );
        });
    }

    private VBox buildRoleBox() {
        ToggleGroup roleGroup =
                new ToggleGroup();

        petOwnerRadio.setToggleGroup(roleGroup);
        petOwnerRadio.setSelected(true);

        veterinarianRadio.setToggleGroup(roleGroup);

        petOwnerRadio.getStyleClass().add(ROLE_RADIO_STYLE);
        veterinarianRadio.getStyleClass().add(ROLE_RADIO_STYLE);

        VBox roleBox = new VBox(
                10,
                buildRoleOption(
                        petOwnerRadio,
                        "Prenota visite e gestisci animali, "
                                + "attività e documenti."
                ),
                buildRoleOption(
                        veterinarianRadio,
                        "Gestisci disponibilità, appuntamenti "
                                + "e percorsi di cura."
                )
        );

        roleBox.setAlignment(
                Pos.CENTER_LEFT
        );

        roleBox.setMaxWidth(Double.MAX_VALUE);

        return roleBox;
    }

    // Costruisce una scelta di ruolo leggibile con una breve descrizione
    private VBox buildRoleOption(
            RadioButton radioButton,
            String descriptionText) {

        radioButton.setWrapText(true);
        radioButton.setMaxWidth(Double.MAX_VALUE);

        Label description = new Label(descriptionText);
        description.setWrapText(true);
        description.getStyleClass().add("role-description");

        VBox option = new VBox(3, radioButton, description);
        option.setMaxWidth(Double.MAX_VALUE);
        option.setPadding(new Insets(10, 12, 10, 12));
        option.getStyleClass().add("registration-role-option");

        return option;
    }

    // Costruisce la sezione visibile solamente al veterinario
    private VBox buildVeterinarianSection() {
        VBox section = new VBox(8);

        section.setAlignment(Pos.TOP_LEFT);

        section.setPrefWidth(
                VETERINARIAN_SECTION_WIDTH
        );

        section.setMaxWidth(
                VETERINARIAN_SECTION_WIDTH
        );

        section.setPadding(
                new Insets(16)
        );

        section.getStyleClass().add(
                "registration-veterinarian-section"
        );

        Label sectionTitle = new Label(
                "Informazioni professionali"
        );
        sectionTitle.getStyleClass().add(
                "veterinarian-section-title"
        );

        section.getChildren().addAll(
                sectionTitle,
                fieldLabel("Specializzazione *"),
                specializationField,
                fieldLabel("Bio *"),
                bioField
        );

        return section;
    }

    // ────────────────────────────────────────────────────────────────────────
    // Componenti riutilizzabili del form
    // ────────────────────────────────────────────────────────────────────────

    private VBox fieldBlock(
            String labelText,
            Control field) {

        return new VBox(
                3,
                fieldLabel(labelText),
                field
        );
    }

    private Label fieldLabel(String text) {
        Label label = new Label(text);

        label.getStyleClass().add(
                "small-label"
        );

        return label;
    }

    private Label requiredLabel() {
        Label label =
                new Label("* campi obbligatori");

        label.getStyleClass().add(
                "register-label"
        );

        return label;
    }

    private Label passwordRules() {
        Label label = new Label("""
                La password deve includere:
                • almeno 8 caratteri
                • una lettera maiuscola
                • almeno un numero
                """);

        label.getStyleClass().add(
                "register-label"
        );

        label.setWrapText(true);
        label.setMaxWidth(300);
        label.setMinHeight(82);
        label.setMaxHeight(Double.MAX_VALUE);

        return label;
    }

    private VBox passwordBlock() {
        return new VBox(
                3,
                fieldLabel("Password *"),
                new StackPane(
                        passwordField,
                        visiblePasswordField
                )
        );
    }

    private VBox confirmPasswordBlock() {
        return new VBox(
                3,
                fieldLabel("Conferma password *"),
                new StackPane(
                        confirmPasswordField,
                        visibleConfirmPasswordField
                )
        );
    }

    // Mostra un messaggio di errore nel form
    public void setError(String message) {
        errorLabel.setText(message);
    }
}
