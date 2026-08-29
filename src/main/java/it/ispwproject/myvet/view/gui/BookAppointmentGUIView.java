package it.ispwproject.myvet.view.gui;

import it.ispwproject.myvet.bean.PetBean;
import it.ispwproject.myvet.bean.VeterinarianBean;
import it.ispwproject.myvet.controller.gui.MainGUI;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class BookAppointmentGUIView {

    // Lifeline
    public final Region step1Dot = stepDot();
    public final Region step2Dot = stepDot();
    public final Region step3Dot = stepDot();
    public final Region step4Dot = stepDot();

    // Selezione animale
    public final ListView<PetBean> petList =
            new ListView<>();

    // Selezione data
    public final DatePicker datePicker =
            new DatePicker();

    public final VBox dateSection =
            buildSection("2. Data");

    // Selezione veterinario
    public final VBox veterinarianList =
            new VBox(6);

    public final VBox veterinarianSection =
            buildSection("3. Veterinario");

    // Selezione orario
    public final VBox slotList =
            new VBox(6);

    public final VBox slotSection =
            buildSection("4. Orario");

    // Conferma
    public final Button bookBtn =
            new Button("Prenota appuntamento");

    public final Label errorLabel =
            new Label("");

    public BookAppointmentGUIView() {
        configurePetList();
        configureDateSection();
        configureVeterinarianSection();
        configureSlotSection();
        configureBookButton();
        configureErrorLabel();
    }

    private void configurePetList() {
        petList.getStyleClass().add("list-view");
        petList.setPrefHeight(150);
        petList.setCellFactory(listView -> petCell());
    }

    private void configureDateSection() {
        datePicker.setPromptText("Seleziona una data");
        datePicker.setMaxWidth(Double.MAX_VALUE);
        datePicker.setPrefHeight(40);

        dateSection.setOpacity(0.5);
        datePicker.setDisable(true);

        dateSection.getChildren().add(datePicker);
    }

    private void configureVeterinarianSection() {
        veterinarianSection.setOpacity(0.5);

        veterinarianList.getChildren().add(
                hintLabel("Seleziona prima una data")
        );

        veterinarianSection.getChildren().add(
                veterinarianList
        );
    }

    private void configureSlotSection() {
        slotSection.setOpacity(0.5);

        slotList.getChildren().add(
                hintLabel("Seleziona prima un veterinario")
        );

        slotSection.getChildren().add(slotList);
    }

    private void configureBookButton() {
        bookBtn.getStyleClass().add("button");
        bookBtn.setPrefWidth(220);
        bookBtn.setPrefHeight(44);
        bookBtn.setDisable(true);
    }

    private void configureErrorLabel() {
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setWrapText(true);
        errorLabel.setMaxWidth(500);
    }

    public BorderPane buildRoot() {
        BorderPane root = new BorderPane();

        root.getStyleClass().add(
                "myvet-background"
        );

        root.setTop(buildTopBar());
        root.setCenter(buildScrollContent());

        return root;
    }

    // Stato dei passaggi

    public void setStepDone(Region dot) {
        dot.getStyleClass().setAll(
                "step-dot-done"
        );
    }

    public void setStepPending(Region dot) {
        dot.getStyleClass().setAll(
                "step-dot"
        );
    }

    public void setDateSectionEnabled(boolean enabled) {
        dateSection.setOpacity(
                enabled ? 1.0 : 0.5
        );

        datePicker.setDisable(!enabled);
    }

    public void setVeterinarianSectionEnabled(
            boolean enabled) {

        veterinarianSection.setOpacity(
                enabled ? 1.0 : 0.5
        );
    }

    public void setSlotSectionEnabled(boolean enabled) {
        slotSection.setOpacity(
                enabled ? 1.0 : 0.5
        );
    }

    public void setError(String message) {
        errorLabel.setText(message);
    }

    public void clearError() {
        errorLabel.setText("");
    }

    // Componenti usati dal controller

    public ToggleButton buildToggle(
            String text,
            Object userData,
            ToggleGroup group) {

        ToggleButton button =
                new ToggleButton(text);

        button.getStyleClass().add(
                "toggle-card"
        );

        button.setToggleGroup(group);
        button.setUserData(userData);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(38);

        return button;
    }

    public HBox buildVeterinarianRow(
            VeterinarianBean veterinarian,
            ToggleGroup group,
            Runnable onInfoClick,
            boolean favourite) {

        ToggleButton toggle =
                new ToggleButton(
                        veterinarian.getFullName()
                );

        toggle.getStyleClass().add(
                "toggle-card"
        );

        toggle.setToggleGroup(group);
        toggle.setUserData(veterinarian);
        toggle.setPrefHeight(40);
        toggle.setMaxWidth(Double.MAX_VALUE);

        HBox.setHgrow(
                toggle,
                Priority.ALWAYS
        );

        Button infoButton =
                new Button("ⓘ");

        infoButton.getStyleClass().add(
                "icon-button"
        );

        infoButton.setPrefWidth(38);
        infoButton.setPrefHeight(38);
        infoButton.setOnAction(
                event -> onInfoClick.run()
        );

        Label favouriteLabel =
                new Label(favourite ? "★" : "");

        favouriteLabel.getStyleClass().add(
                "favourite-star"
        );

        favouriteLabel.setStyle(
                "-fx-text-fill: #F1C40F;"
                        + "-fx-font-size: 18px;"
        );

        favouriteLabel.setMinWidth(24);
        favouriteLabel.setAlignment(
                Pos.CENTER
        );

        HBox row = new HBox(
                6,
                toggle,
                infoButton,
                favouriteLabel
        );

        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(Double.MAX_VALUE);

        return row;
    }

    public Label buildHintLabel(String text) {
        return hintLabel(text);
    }

    // Contenuto centrale

    private ScrollPane buildScrollContent() {
        VBox petSection =
                buildSection("1. Animale");

        petSection.getChildren().add(
                petList
        );

        HBox buttonRow =
                new HBox(bookBtn);

        buttonRow.setAlignment(Pos.CENTER);

        VBox form = new VBox(10);

        form.setAlignment(Pos.TOP_CENTER);
        form.setPadding(
                new Insets(20, 0, 30, 0)
        );

        form.setPrefWidth(540);

        form.getChildren().addAll(
                petSection,
                dateSection,
                veterinarianSection,
                slotSection,
                errorLabel,
                buttonRow
        );

        Region rightSpacer = new Region();
        rightSpacer.setPrefWidth(90);

        HBox formWrapper = new HBox(
                buildLifeline(),
                form,
                rightSpacer
        );

        formWrapper.setAlignment(Pos.TOP_CENTER);

        formWrapper.getStyleClass().add(
                "myvet-background"
        );

        formWrapper.setPadding(
                new Insets(20, 0, 0, 0)
        );

        HBox.setHgrow(
                form,
                Priority.ALWAYS
        );

        ScrollPane scrollPane =
                new ScrollPane(formWrapper);

        scrollPane.getStyleClass().add(
                "transparent-scroll"
        );

        scrollPane.setFitToWidth(true);

        return scrollPane;
    }

    // Lifeline laterale

    private GridPane buildLifeline() {
        GridPane lifeline = new GridPane();

        lifeline.setPadding(
                new Insets(30, 12, 0, 20)
        );

        lifeline.setMinWidth(110);

        ColumnConstraints dotColumn =
                new ColumnConstraints(20);

        dotColumn.setHalignment(HPos.CENTER);

        ColumnConstraints textColumn =
                new ColumnConstraints();

        lifeline.getColumnConstraints().addAll(
                dotColumn,
                textColumn
        );

        addStep(
                lifeline,
                step1Dot,
                "Animale",
                0
        );

        addLine(lifeline, 1);

        addStep(
                lifeline,
                step2Dot,
                "Data",
                2
        );

        addLine(lifeline, 3);

        addStep(
                lifeline,
                step3Dot,
                "Veterinario",
                4
        );

        addLine(lifeline, 5);

        addStep(
                lifeline,
                step4Dot,
                "Orario",
                6
        );

        return lifeline;
    }

    private void addStep(
            GridPane lifeline,
            Region dot,
            String text,
            int row) {

        lifeline.add(dot, 0, row);

        lifeline.add(
                stepLabel(text),
                1,
                row
        );
    }

    private void addLine(
            GridPane lifeline,
            int row) {

        Region line = stepLine();

        lifeline.add(line, 0, row);

        GridPane.setHalignment(
                line,
                HPos.CENTER
        );
    }

    // Barra superiore

    private HBox buildTopBar() {
        HBox bar = new HBox();

        bar.getStyleClass().add("navbar");
        bar.setAlignment(Pos.CENTER_LEFT);

        Button backButton =
                new Button("⟪  Indietro");

        backButton.getStyleClass().add(
                "back-button"
        );

        backButton.setOnAction(
                event ->
                        MainGUI.showDashboardPetOwner()
        );

        HBox left = new HBox(backButton);
        left.setAlignment(Pos.CENTER_LEFT);
        left.setPrefWidth(170);

        HBox.setHgrow(
                left,
                Priority.ALWAYS
        );

        Label title =
                new Label("Prenota appuntamento");

        title.getStyleClass().add(
                "page-title"
        );

        title.setMaxWidth(Double.MAX_VALUE);
        title.setAlignment(Pos.CENTER);

        HBox.setHgrow(
                title,
                Priority.ALWAYS
        );

        HBox right = new HBox();

        right.setAlignment(Pos.CENTER_RIGHT);
        right.setPrefWidth(170);

        HBox.setHgrow(
                right,
                Priority.ALWAYS
        );

        var logoStream =
                getClass().getResourceAsStream(
                        "/images/logo.png"
                );

        if (logoStream != null) {
            ImageView logo = new ImageView(
                    new Image(
                            logoStream,
                            60,
                            60,
                            true,
                            true
                    )
            );

            logo.setFitHeight(56);
            logo.setPreserveRatio(true);
            logo.setSmooth(true);

            right.getChildren().add(logo);
        }

        bar.getChildren().addAll(
                left,
                title,
                right
        );

        return bar;
    }

    // Builder privati

    private static Region stepDot() {
        Region dot = new Region();

        dot.getStyleClass().add(
                "step-dot"
        );

        return dot;
    }

    private static Region stepLine() {
        Region line = new Region();

        line.setPrefWidth(2);
        line.setMaxWidth(2);
        line.setPrefHeight(20);

        line.setStyle(
                "-fx-background-color: #b8d4ea;"
        );

        return line;
    }

    private static Label stepLabel(String text) {
        Label label = new Label(text);

        label.setStyle(
                "-fx-font-size: 11px;"
                        + "-fx-text-fill: #4B4B4B;"
                        + "-fx-padding: 0 0 0 6;"
        );

        return label;
    }

    private static VBox buildSection(String title) {
        VBox section = new VBox(10);

        section.getStyleClass().add(
                "info-card"
        );

        section.setMaxWidth(500);

        Label label = new Label(title);

        label.getStyleClass().add(
                "small-label"
        );

        section.getChildren().add(label);

        return section;
    }

    private static Label hintLabel(String text) {
        Label label = new Label(text);

        label.getStyleClass().add(
                "info-text"
        );

        label.setStyle(
                "-fx-text-fill: #aaa;"
                        + "-fx-font-style: italic;"
        );

        return label;
    }

    private static ListCell<PetBean> petCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(
                    PetBean pet,
                    boolean empty) {

                super.updateItem(pet, empty);

                if (empty || pet == null) {
                    setText(null);
                    return;
                }

                String text =
                        pet.getName()
                                + " – "
                                + pet.getSpecies();

                if (pet.getBreed() != null
                        && !pet.getBreed().isBlank()) {

                    text += " – " + pet.getBreed();
                }

                setText(text);
            }
        };
    }
}