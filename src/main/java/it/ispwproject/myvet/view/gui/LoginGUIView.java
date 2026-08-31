package it.ispwproject.myvet.view.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.OverrunStyle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

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
        emailField.setPromptText("Inserisci email");
        emailField.setPrefWidth(250);
        emailField.setPrefHeight(48);

        passwordField.setPromptText("Inserisci password");
        passwordField.setPrefWidth(250);
        passwordField.setPrefHeight(48);

        visiblePasswordField.setPromptText("Inserisci password");
        visiblePasswordField.setPrefWidth(250);
        visiblePasswordField.setPrefHeight(48);
        visiblePasswordField.setVisible(false);

        visiblePasswordField.managedProperty().bind(visiblePasswordField.visibleProperty());
        passwordField.managedProperty().bind(passwordField.visibleProperty());
        visiblePasswordField.textProperty().bindBidirectional(passwordField.textProperty());

        errorLabel.setWrapText(true);
        errorLabel.getStyleClass().add("error-label");

        loginBtn.setPrefWidth(95);
        loginBtn.setPrefHeight(42);
    }

    // Costruisce la schermata completa di login
    public StackPane buildRoot(
            Runnable onLogin,
            Runnable onRegister) {

        StackPane root = new StackPane();
        root.getStyleClass().add("auth-background");

        Pane decorations = buildDecorations();

        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(16, 34, 12, 34));

        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);

        var logoStream = getClass().getResourceAsStream(
                "/images/myvet_logo.png"
        );

        if (logoStream != null) {
            ImageView logoView = new ImageView(
                    new Image(logoStream, 54, 54, true, true)
            );
            logoView.setFitWidth(40);
            logoView.setFitHeight(40);
            logoView.setPreserveRatio(true);
            logoView.setSmooth(true);
            header.getChildren().add(logoView);
        }

        Label brandName = new Label("MyVet");
        brandName.getStyleClass().add("myvet-brand-name");
        header.getChildren().add(brandName);

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        Label headerNote = new Label(
                "Cura, appuntamenti e documenti"
        );
        headerNote.getStyleClass().add("auth-header-note");
        header.getChildren().addAll(headerSpacer, headerNote);

        VBox loginCard = buildLoginCard(
                onLogin,
                onRegister
        );

        BorderPane.setAlignment(loginCard, Pos.CENTER);
        BorderPane.setMargin(loginCard, new Insets(6, 0, 6, 0));
        layout.setTop(header);
        layout.setCenter(loginCard);

        HBox animals = buildAnimalDecoration();
        BorderPane.setAlignment(animals, Pos.BOTTOM_CENTER);
        BorderPane.setMargin(animals, new Insets(4, 0, 4, 0));
        layout.setBottom(animals);

        root.getChildren().addAll(decorations, layout);

        return root;
    }

    // Costruisce il pannello contenente il form di accesso
    private VBox buildLoginCard(
            Runnable onLogin,
            Runnable onRegister) {

        VBox panel = new VBox(10);

        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(20, 34, 18, 34));
        panel.setMinWidth(340);
        panel.setPrefWidth(390);
        panel.setMaxWidth(430);
        panel.getStyleClass().addAll(
                "auth-card",
                "storyboard-login-card"
        );

        Label title = new Label("Accedi");
        title.getStyleClass().add("auth-title");

        Label subtitle = new Label(
                "Accedi a MyVet\nper prenderti cura dei tuoi animali."
        );
        subtitle.getStyleClass().add("auth-subtitle");
        subtitle.setWrapText(true);
        subtitle.setTextOverrun(OverrunStyle.CLIP);
        subtitle.setMinHeight(42);
        subtitle.setMaxWidth(340);
        subtitle.setAlignment(Pos.CENTER);

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

        VBox.setMargin(
                showPasswordCheck,
                new Insets(7, 0, 0, 0)
        );

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
                4,
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
                title,
                subtitle,
                emailBox,
                passwordContainer,
                errorLabel,
                loginBtn,
                registrationBox
        );

        return panel;
    }

    private Pane buildDecorations() {
        Pane pane = new Pane();
        pane.setMouseTransparent(true);

        Pane leftTree = buildTree();
        leftTree.setLayoutX(14);
        leftTree.layoutYProperty().bind(
                pane.heightProperty().subtract(340)
        );

        Pane rightTree = buildTree();
        rightTree.layoutXProperty().bind(
                pane.widthProperty().subtract(112)
        );
        rightTree.layoutYProperty().bind(
                pane.heightProperty().subtract(340)
        );

        Circle leftShrub = new Circle(118);
        leftShrub.getStyleClass().add("auth-shrub");
        leftShrub.setLayoutX(32);
        leftShrub.layoutYProperty().bind(
                pane.heightProperty().add(78)
        );

        Circle rightShrub = new Circle(145);
        rightShrub.getStyleClass().add("auth-shrub");
        rightShrub.layoutXProperty().bind(
                pane.widthProperty().subtract(35)
        );
        rightShrub.layoutYProperty().bind(
                pane.heightProperty().add(82)
        );

        pane.getChildren().addAll(
                leftTree,
                rightTree,
                leftShrub,
                rightShrub
        );

        return pane;
    }

    private Pane buildTree() {
        Pane tree = new Pane();
        tree.setPrefSize(100, 280);

        Rectangle trunk = new Rectangle(
                42,
                150,
                13,
                120
        );
        trunk.getStyleClass().add("auth-tree-trunk");

        Polygon crown = new Polygon(
                49.0, 8.0,
                10.0, 94.0,
                31.0, 94.0,
                3.0, 150.0,
                95.0, 150.0,
                67.0, 94.0,
                88.0, 94.0
        );
        crown.getStyleClass().add("auth-tree-crown");

        tree.getChildren().addAll(trunk, crown);
        return tree;
    }

    private HBox buildAnimalDecoration() {
        Label cat = new Label("🐈");
        Label paw = new Label("🐾");
        Label dog = new Label("🐕");

        cat.getStyleClass().add("auth-animal");
        paw.getStyleClass().add("auth-paw");
        dog.getStyleClass().add("auth-animal");

        HBox animals = new HBox(18, cat, paw, dog);
        animals.setAlignment(Pos.BOTTOM_CENTER);
        animals.setPadding(new Insets(0, 0, 6, 0));

        return animals;
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
