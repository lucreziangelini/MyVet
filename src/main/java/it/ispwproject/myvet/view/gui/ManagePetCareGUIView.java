package it.ispwproject.myvet.view.gui;

import it.ispwproject.myvet.bean.ActivityBean;
import it.ispwproject.myvet.bean.PetBean;
import it.ispwproject.myvet.bean.ProgressBean;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

public class ManagePetCareGUIView extends PageGUIView {

    public final ComboBox<PetBean> petCombo =
            new ComboBox<>();

    public final Label errorLabel =
            buildErrorLabel();

    private final Label emptyStateLabel =
            new Label(
                    "Non ci sono ancora animali associati ai tuoi appuntamenti."
            );

    public ManagePetCareGUIView() {
        petCombo.getStyleClass().add(
                "combo-box"
        );

        petCombo.setPromptText(
                "Seleziona un animale..."
        );

        petCombo.setMaxWidth(Double.MAX_VALUE);

        petCombo.setCellFactory(
                listView -> petCell()
        );

        petCombo.setButtonCell(
                petCell()
        );

        emptyStateLabel.getStyleClass().add("empty-state-card");
        emptyStateLabel.setWrapText(true);
        emptyStateLabel.setMaxWidth(720);
        emptyStateLabel.setVisible(false);
        emptyStateLabel.setManaged(false);
    }

    // ────────────────────────────────────────────────────────────────────────
    // Costruzione della pagina
    // ────────────────────────────────────────────────────────────────────────

    public BorderPane buildRoot(Runnable onBack) {
        BorderPane root = buildShell(
                "Gestisci cura animali",
                onBack
        );

        VBox content = new VBox(12);

        content.setPadding(
                new Insets(28, 48, 28, 48)
        );

        content.setAlignment(
                Pos.TOP_CENTER
        );

        // Card per la selezione dell'animale
        VBox selectorCard = new VBox(10);

        selectorCard.getStyleClass().add(
                "info-card"
        );

        selectorCard.setMaxWidth(720);
        selectorCard.setAlignment(
                Pos.CENTER_LEFT
        );

        Label petLabel =
                new Label("Seleziona animale");

        petLabel.getStyleClass().add(
                "small-label"
        );

        selectorCard.getChildren().addAll(
                petLabel,
                petCombo
        );

        // Card contenente progressi e attività dell'animale selezionato
        VBox petCard = new VBox(8);

        petCard.setMaxWidth(720);
        petCard.setVisible(false);
        petCard.setManaged(false);

        // Il controller aggancerà l'azione sulla combo e recupererà la card
        petCombo.setUserData(petCard);

        content.getChildren().addAll(
                selectorCard,
                emptyStateLabel,
                petCard,
                errorLabel
        );

        ScrollPane scrollPane =
                new ScrollPane(content);

        scrollPane.getStyleClass().add(
                "transparent-scroll"
        );

        scrollPane.setFitToWidth(true);

        root.setCenter(scrollPane);

        return root;
    }

    // Restituisce la card aggiornata dal controller
    public VBox getPetCard() {
        return (VBox) petCombo.getUserData();
    }

    public void setPets(List<PetBean> pets) {
        List<PetBean> safePets = pets == null
                ? List.of()
                : pets;

        petCombo.getItems().setAll(safePets);

        boolean hasPets = !safePets.isEmpty();
        petCombo.setDisable(!hasPets);
        petCombo.setPromptText(
                hasPets
                        ? "Seleziona un animale..."
                        : "Nessun animale disponibile"
        );

        emptyStateLabel.setVisible(!hasPets);
        emptyStateLabel.setManaged(!hasPets);

        if (!hasPets) {
            VBox petCard = getPetCard();
            petCard.setVisible(false);
            petCard.setManaged(false);
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // Costruzione della scheda dell'animale
    // ────────────────────────────────────────────────────────────────────────

    public void buildPetCard(
            VBox card,
            PetBean pet,
            ProgressBean progress,
            List<ActivityBean> activities,
            Consumer<String> onSaveProgress,
            Consumer<String> onAssignActivity) {

        card.getChildren().clear();
        card.setVisible(true);
        card.setManaged(true);

        // ── Header animale ────────────────────────────────────────────────
        HBox petHeader = buildPetHeader(pet);

        // ── Pannello progressi ────────────────────────────────────────────
        VBox progressBox = buildProgressBox(
                progress,
                onSaveProgress
        );

        // ── Pannello attività di cura ─────────────────────────────────────
        VBox activityBox = buildActivityBox(
                activities,
                onAssignActivity
        );

        HBox panels = new HBox(
                16,
                progressBox,
                activityBox
        );

        panels.setAlignment(Pos.TOP_CENTER);

        HBox.setHgrow(
                progressBox,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                activityBox,
                Priority.ALWAYS
        );

        card.getChildren().addAll(
                petHeader,
                panels
        );
    }

    // Costruisce l'intestazione con le informazioni dell'animale
    private HBox buildPetHeader(PetBean pet) {
        HBox petHeader = new HBox(12);

        petHeader.setAlignment(
                Pos.CENTER_LEFT
        );

        petHeader.getStyleClass().add(
                "info-card"
        );

        petHeader.setMaxWidth(720);

        petHeader.setPadding(
                new Insets(12, 16, 12, 16)
        );

        // Avatar costruito con la prima lettera del nome dell'animale
        String initial =
                pet.getName() == null
                        || pet.getName().isBlank()
                        ? "?"
                        : String.valueOf(
                        pet.getName()
                                .charAt(0)
                ).toUpperCase();

        Label avatar = new Label(initial);

        avatar.setStyle(
                "-fx-background-color: #8EADC2;"
                        + "-fx-background-radius: 20;"
                        + "-fx-text-fill: white;"
                        + "-fx-font-weight: bold;"
                        + "-fx-font-size: 14px;"
                        + "-fx-min-width: 40;"
                        + "-fx-min-height: 40;"
                        + "-fx-alignment: center;"
        );

        VBox petInfo = new VBox(4);

        Label nameLabel =
                new Label(pet.getName());

        nameLabel.getStyleClass().add(
                "welcome-label"
        );

        Label speciesLabel =
                new Label(
                        "Specie: "
                                + valueOrDash(
                                pet.getSpecies()
                        )
                );

        speciesLabel.getStyleClass().add(
                "info-text"
        );

        petInfo.getChildren().addAll(
                nameLabel,
                speciesLabel
        );

        if (pet.getBreed() != null
                && !pet.getBreed().isBlank()) {

            Label breedLabel =
                    new Label(
                            "Razza: "
                                    + pet.getBreed()
                    );

            breedLabel.getStyleClass().add(
                    "info-text"
            );

            petInfo.getChildren().add(
                    breedLabel
            );
        }

        if (pet.getBirthDate() != null) {
            Label birthDateLabel =
                    new Label(
                            "Data di nascita: "
                                    + pet.getBirthDate()
                                    .format(
                                            DateTimeFormatter
                                                    .ofPattern(
                                                            "dd/MM/yyyy"
                                                    )
                                    )
                    );

            birthDateLabel.getStyleClass().add(
                    "info-text"
            );

            petInfo.getChildren().add(
                    birthDateLabel
            );
        }

        petHeader.getChildren().addAll(
                avatar,
                petInfo
        );

        return petHeader;
    }

    // ────────────────────────────────────────────────────────────────────────
    // Pannello progressi
    // ────────────────────────────────────────────────────────────────────────

    private VBox buildProgressBox(
            ProgressBean progress,
            Consumer<String> onSaveProgress) {

        VBox progressBox = new VBox(12);

        progressBox.getStyleClass().add(
                "info-card"
        );

        progressBox.setPrefWidth(340);
        progressBox.setMinWidth(280);
        progressBox.setMaxHeight(
                Region.USE_PREF_SIZE
        );

        Label progressTitle =
                new Label("📝  Progressi");

        progressTitle.getStyleClass().add(
                "small-label"
        );

        TextArea notesArea =
                new TextArea();

        notesArea.getStyleClass().add(
                "text-area"
        );

        notesArea.setPrefRowCount(3);
        notesArea.setWrapText(true);

        notesArea.setPromptText(
                "Note sui progressi dell'animale..."
        );

        if (progress != null
                && progress.getNotes() != null) {

            notesArea.setText(
                    progress.getNotes()
            );
        }

        HBox progressFooter =
                new HBox(12);

        progressFooter.setAlignment(
                Pos.CENTER_LEFT
        );

        Label lastUpdate =
                new Label(
                        formatLastUpdate(progress)
                );

        lastUpdate.getStyleClass().add(
                "info-text"
        );

        lastUpdate.setStyle(
                "-fx-text-fill: #999;"
        );

        HBox.setHgrow(
                lastUpdate,
                Priority.ALWAYS
        );

        Button updateButton =
                new Button("Salva");

        updateButton.getStyleClass().add(
                "save-button"
        );

        updateButton.setPrefWidth(80);

        updateButton.setOnAction(
                event ->
                        onSaveProgress.accept(
                                notesArea.getText()
                        )
        );

        progressFooter.getChildren().addAll(
                lastUpdate,
                updateButton
        );

        progressBox.getChildren().addAll(
                progressTitle,
                notesArea,
                progressFooter
        );

        return progressBox;
    }

    // Restituisce la data dell'ultimo aggiornamento dei progressi
    private String formatLastUpdate(
            ProgressBean progress) {

        if (progress == null
                || progress.getUpdatedAt() == null) {
            return "Nessun aggiornamento";
        }

        return "Aggiornato il "
                + progress.getUpdatedAt().format(
                DateTimeFormatter.ofPattern(
                        "dd/MM/yyyy 'alle' HH:mm"
                )
        );
    }

    // ────────────────────────────────────────────────────────────────────────
    // Pannello attività di cura
    // ────────────────────────────────────────────────────────────────────────

    private VBox buildActivityBox(
            List<ActivityBean> activities,
            Consumer<String> onAssignActivity) {

        VBox activityBox = new VBox(8);

        activityBox.getStyleClass().add(
                "info-card"
        );

        activityBox.setPrefWidth(340);
        activityBox.setMinWidth(280);

        Label activityTitle =
                new Label("✅  Attività di cura");

        activityTitle.getStyleClass().add(
                "small-label"
        );

        activityBox.getChildren().add(
                activityTitle
        );

        VBox activityList =
                new VBox(6);

        activityList.setMaxWidth(
                Double.MAX_VALUE
        );

        if (activities.isEmpty()) {
            Label noActivities =
                    new Label(
                            "Nessuna attività assegnata"
                    );

            noActivities.getStyleClass().add(
                    "info-text"
            );

            noActivities.setStyle(
                    "-fx-text-fill: #aaa;"
                            + "-fx-font-style: italic;"
            );

            activityList.getChildren().add(
                    noActivities
            );

        } else {
            for (ActivityBean activity : activities) {
                activityList.getChildren().add(
                        buildActivityRow(activity)
                );
            }
        }

        activityBox.getChildren().addAll(
                activityList,
                new Separator(),
                buildAssignActivityRow(
                        onAssignActivity
                )
        );

        return activityBox;
    }

    // Costruisce una riga relativa a un'attività assegnata
    private HBox buildActivityRow(
            ActivityBean activity) {

        HBox activityRow =
                new HBox(8);

        activityRow.setAlignment(
                Pos.TOP_LEFT
        );

        activityRow.setMaxWidth(
                Double.MAX_VALUE
        );

        Label statusIcon =
                new Label(
                        activity.isCompleted()
                                ? "✓"
                                : "○"
                );

        statusIcon.getStyleClass().add(
                activity.isCompleted()
                        ? "success-label"
                        : "info-text"
        );

        statusIcon.setStyle(
                "-fx-font-size: 14px;"
        );

        Label descriptionLabel =
                new Label(
                        activity.getDescription()
                );

        descriptionLabel.getStyleClass().add(
                activity.isCompleted()
                        ? "success-label"
                        : "register-label"
        );

        descriptionLabel.setMaxWidth(240);
        descriptionLabel.setWrapText(true);
        descriptionLabel.setTextOverrun(
                javafx.scene.control.OverrunStyle.CLIP
        );

        HBox.setHgrow(
                descriptionLabel,
                Priority.ALWAYS
        );

        if (activity.isCompleted()) {
            descriptionLabel.setStyle(
                    "-fx-strikethrough: true;"
            );
        }

        activityRow.getChildren().addAll(
                statusIcon,
                descriptionLabel
        );

        return activityRow;
    }

    // Costruisce la riga per assegnare una nuova attività
    private HBox buildAssignActivityRow(
            Consumer<String> onAssignActivity) {

        HBox addRow =
                new HBox(8);

        addRow.setAlignment(
                Pos.CENTER_LEFT
        );

        TextField activityField =
                new TextField();

        activityField.getStyleClass().add(
                "text-field"
        );

        activityField.setPromptText(
                "Nuova attività di cura..."
        );

        activityField.setPrefHeight(34);

        HBox.setHgrow(
                activityField,
                Priority.ALWAYS
        );

        Button assignButton =
                new Button("＋");

        assignButton.getStyleClass().add(
                "save-button"
        );

        assignButton.setPrefWidth(36);
        assignButton.setPrefHeight(34);

        assignButton.setOnAction(event -> {
            String description =
                    activityField.getText().trim();

            if (!description.isBlank()) {
                onAssignActivity.accept(
                        description
                );

                activityField.clear();
            }
        });

        addRow.getChildren().addAll(
                activityField,
                assignButton
        );

        return addRow;
    }

    // Costruisce le celle usate nella selezione dell'animale
    private ListCell<PetBean> petCell() {
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

                String description =
                        pet.getName()
                                + " ("
                                + valueOrDash(
                                pet.getSpecies()
                        )
                                + ")";

                setText(description);
            }
        };
    }

    private String valueOrDash(String value) {
        return value == null || value.isBlank()
                ? "—"
                : value;
    }
}
