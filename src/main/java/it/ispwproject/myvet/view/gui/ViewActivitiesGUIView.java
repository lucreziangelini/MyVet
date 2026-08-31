package it.ispwproject.myvet.view.gui;

import it.ispwproject.myvet.bean.ActivityBean;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

public class ViewActivitiesGUIView
        extends PageGUIView {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String SMALL_LABEL_STYLE = "small-label";

    public final Label errorLabel =
            buildErrorLabel();

    public void setError(String message) {
        errorLabel.setText(message);
    }

    public void clearError() {
        errorLabel.setText("");
    }

    // ────────────────────────────────────────────────────────────────────────
    // Costruzione della pagina delle attività di cura
    // ────────────────────────────────────────────────────────────────────────

    public BorderPane buildRoot(Runnable onBack) {
        return buildShell(
                "Attività di cura",
                onBack
        );
    }

    // ────────────────────────────────────────────────────────────────────────
    // Costruzione del contenuto principale
    // ────────────────────────────────────────────────────────────────────────

    public void buildContent(
            BorderPane root,
            List<ActivityBean> pendingActivities,
            List<ActivityBean> completedActivities,
            Consumer<ActivityBean> onMarkDone) {

        VBox content =
                new VBox(16);

        content.setPadding(
                new Insets(24)
        );

        content.setAlignment(
                Pos.TOP_CENTER
        );

        if (pendingActivities.isEmpty()
                && completedActivities.isEmpty()) {

            Label emptyLabel =
                    new Label(
                            "Nessuna attività di cura assegnata."
                    );

            emptyLabel.getStyleClass().add(
                    "register-label"
            );

            content.getChildren().add(
                    emptyLabel
            );

        } else {
            // Attività ancora da completare
            addPendingSection(
                    content,
                    pendingActivities,
                    onMarkDone
            );

            // Attività già completate
            addCompletedSection(
                    content,
                    completedActivities,
                    onMarkDone
            );
        }

        content.getChildren().add(
                errorLabel
        );

        root.setCenter(
                transparentScroll(content)
        );
    }

    // ────────────────────────────────────────────────────────────────────────
    // Sezione delle attività da completare
    // ────────────────────────────────────────────────────────────────────────

    private void addPendingSection(
            VBox content,
            List<ActivityBean> activities,
            Consumer<ActivityBean> onMarkDone) {

        if (activities.isEmpty()) {
            return;
        }

        Label title =
                new Label("Da completare");

        title.getStyleClass().add(
                SMALL_LABEL_STYLE
        );

        content.getChildren().add(
                title
        );

        for (ActivityBean activity : activities) {
            content.getChildren().add(
                    buildActivityCard(
                            activity,
                            false,
                            onMarkDone
                    )
            );
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // Sezione delle attività completate
    // ────────────────────────────────────────────────────────────────────────

    private void addCompletedSection(
            VBox content,
            List<ActivityBean> activities,
            Consumer<ActivityBean> onMarkDone) {

        if (activities.isEmpty()) {
            return;
        }

        Label title =
                new Label("Completate");

        title.getStyleClass().add(
                SMALL_LABEL_STYLE
        );

        content.getChildren().add(
                title
        );

        for (ActivityBean activity : activities) {
            content.getChildren().add(
                    buildActivityCard(
                            activity,
                            true,
                            onMarkDone
                    )
            );
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // Costruzione della card di un'attività
    // ────────────────────────────────────────────────────────────────────────

    private HBox buildActivityCard(
            ActivityBean activity,
            boolean completed,
            Consumer<ActivityBean> onMarkDone) {

        HBox card =
                new HBox(16);

        card.getStyleClass().add(
                "info-card"
        );

        card.setAlignment(
                Pos.CENTER_LEFT
        );

        card.setMaxWidth(640);

        // Icona che rappresenta lo stato dell'attività
        Label statusIcon =
                new Label(
                        completed ? "✓" : "○"
                );

        statusIcon.setStyle(
                "-fx-font-size: 18px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: "
                        + (completed
                        ? "#27AE60"
                        : "#E67E22")
                        + ";"
        );

        VBox information =
                new VBox(4);

        HBox.setHgrow(
                information,
                Priority.ALWAYS
        );

        // Descrizione dell'attività
        Label descriptionLabel =
                new Label(
                        activity.getDescription()
                );

        descriptionLabel.getStyleClass().add(
                "register-label"
        );

        descriptionLabel.setWrapText(true);

        if (completed) {
            descriptionLabel.setStyle(
                    "-fx-strikethrough: true;"
            );
        }

        information.getChildren().add(
                descriptionLabel
        );

        // Animale a cui è stata assegnata l'attività
        if (activity.getPet() != null) {
            Label petLabel =
                    new Label(
                            "Animale: "
                                    + activity.getPet().getName()
                    );

            petLabel.getStyleClass().add(
                    SMALL_LABEL_STYLE
            );

            information.getChildren().add(
                    petLabel
            );
        }

        // Veterinario che ha assegnato l'attività
        if (activity.getVeterinarian() != null) {
            Label veterinarianLabel =
                    new Label(
                            "Assegnata da: "
                                    + activity.getVeterinarian()
                                    .getFullName()
                    );

            veterinarianLabel.getStyleClass().add(
                    "info-text"
            );

            information.getChildren().add(
                    veterinarianLabel
            );
        }

        // Data di creazione dell'attività
        if (activity.getCreatedAt() != null) {
            Label creationDateLabel =
                    new Label(
                            "📅 "
                                    + activity.getCreatedAt()
                                    .toLocalDate()
                                    .format(DATE_FORMATTER)
                    );

            creationDateLabel.setStyle(
                    "-fx-font-size: 11px;"
                            + "-fx-text-fill: #888;"
            );

            information.getChildren().add(
                    creationDateLabel
            );
        }

        card.getChildren().addAll(
                statusIcon,
                information
        );

        // Il pulsante è disponibile solamente
        // per le attività non ancora completate
        if (!completed) {
            Button completeButton =
                    new Button("Segna completata");

            completeButton.getStyleClass().add(
                    "success-button"
            );

            completeButton.setOnAction(
                    event ->
                            onMarkDone.accept(activity)
            );

            card.getChildren().add(
                    completeButton
            );
        }

        return card;
    }
}
