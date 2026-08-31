package it.ispwproject.myvet.view.gui;

import it.ispwproject.myvet.bean.TimeSlotBean;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ViewSlotsGUIView extends PageGUIView {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public final Label errorLabel =
            buildErrorLabel();

    public void setError(String message) {
        errorLabel.setText(message);
    }

    public void clearError() {
        errorLabel.setText("");
    }

    // ────────────────────────────────────────────────────────────────────────
    // Costruzione della pagina delle fasce orarie del veterinario
    // ────────────────────────────────────────────────────────────────────────

    public BorderPane buildRoot(Runnable onBack) {
        return buildShell(
                "Le mie fasce orarie",
                onBack
        );
    }

    // ────────────────────────────────────────────────────────────────────────
    // Costruzione del contenuto principale
    // ────────────────────────────────────────────────────────────────────────

    public void buildContent(
            BorderPane root,
            List<TimeSlotBean> availableSlots,
            List<TimeSlotBean> bookedSlots,
            List<TimeSlotBean> pastSlots,
            Map<Integer, String> petBySlot,
            Consumer<TimeSlotBean> onDelete) {

        VBox content = new VBox(12);

        content.setPadding(new Insets(24));
        content.setAlignment(Pos.TOP_CENTER);

        // Pulsanti per cambiare categoria
        ToggleButton availableButton =
                new ToggleButton(
                        "Disponibili ("
                                + availableSlots.size()
                                + ")"
                );

        ToggleButton bookedButton =
                new ToggleButton(
                        "Prenotati ("
                                + bookedSlots.size()
                                + ")"
                );

        ToggleButton pastButton =
                new ToggleButton(
                        "Passati ("
                                + pastSlots.size()
                                + ")"
                );

        availableButton.getStyleClass().add(
                "toggle-card"
        );

        bookedButton.getStyleClass().addAll(
                "toggle-card",
                "cancelled"
        );

        pastButton.getStyleClass().addAll(
                "toggle-card",
                "expired"
        );

        configureToggleButton(availableButton);
        configureToggleButton(bookedButton);
        configureToggleButton(pastButton);

        ToggleGroup categoryGroup =
                new ToggleGroup();

        availableButton.setToggleGroup(categoryGroup);
        bookedButton.setToggleGroup(categoryGroup);
        pastButton.setToggleGroup(categoryGroup);

        availableButton.setSelected(true);

        HBox toggleBar = new HBox(
                8,
                availableButton,
                bookedButton,
                pastButton
        );

        toggleBar.setAlignment(Pos.CENTER);
        toggleBar.setMaxWidth(640);

        // Contenitore della lista delle fasce orarie
        VBox listBox = new VBox(12);

        listBox.setAlignment(
                Pos.TOP_CENTER
        );

        // Aggiorna la lista in base alla categoria selezionata
        Runnable refreshList = () -> {
            listBox.getChildren().clear();

            List<TimeSlotBean> currentSlots;
            String emptyMessage;
            boolean past;

            if (availableButton.isSelected()) {
                currentSlots = availableSlots;
                emptyMessage =
                        "Nessuna fascia oraria disponibile.";
                past = false;

            } else if (bookedButton.isSelected()) {
                currentSlots = bookedSlots;
                emptyMessage =
                        "Nessuna fascia oraria prenotata.";
                past = false;

            } else {
                currentSlots = pastSlots;
                emptyMessage =
                        "Nessuna fascia oraria passata.";
                past = true;
            }

            if (currentSlots.isEmpty()) {
                listBox.getChildren().add(
                        emptyLabel(emptyMessage)
                );

                return;
            }

            for (TimeSlotBean slot : currentSlots) {
                String bookedPetName =
                        findBookedPetName(
                                slot,
                                petBySlot
                        );

                listBox.getChildren().add(
                        buildSlotCard(
                                slot,
                                bookedPetName,
                                past,
                                onDelete
                        )
                );
            }
        };

        refreshList.run();

        availableButton.setOnAction(
                event -> refreshList.run()
        );

        bookedButton.setOnAction(
                event -> refreshList.run()
        );

        pastButton.setOnAction(
                event -> refreshList.run()
        );

        if (availableSlots.isEmpty()
                && bookedSlots.isEmpty()
                && pastSlots.isEmpty()) {

            content.getChildren().addAll(
                    toggleBar,
                    emptyLabel(
                            "Non hai ancora creato alcuna fascia oraria."
                    )
            );

        } else {
            content.getChildren().addAll(
                    toggleBar,
                    listBox
            );
        }

        content.getChildren().add(
                errorLabel
        );

        root.setCenter(
                transparentScroll(content)
        );
    }

    // Configura le dimensioni di un pulsante di categoria
    private void configureToggleButton(
            ToggleButton button) {

        button.setPrefWidth(180);
        button.setPrefHeight(36);
    }

    // Cerca il nome dell'animale associato alla fascia oraria
    private String findBookedPetName(
            TimeSlotBean slot,
            Map<Integer, String> petBySlot) {

        String petName =
                petBySlot != null
                        ? petBySlot.get(slot.getId())
                        : null;

        if (petName == null || petName.isBlank()) {
            petName = slot.getBookedPetName();
        }

        return petName;
    }

    // ────────────────────────────────────────────────────────────────────────
    // Costruzione della scheda di una fascia oraria
    // ────────────────────────────────────────────────────────────────────────

    private HBox buildSlotCard(
            TimeSlotBean slot,
            String bookedPetName,
            boolean past,
            Consumer<TimeSlotBean> onDelete) {

        HBox card = new HBox(16);

        card.getStyleClass().add(
                "info-card"
        );

        card.setAlignment(Pos.CENTER_LEFT);
        card.setMaxWidth(640);

        VBox information = new VBox(4);

        HBox.setHgrow(
                information,
                Priority.ALWAYS
        );

        Label statusDot =
                new Label("●");

        statusDot.setStyle(
                "-fx-font-size: 14px;"
        );

        Label dateTimeLabel =
                new Label(
                        slot.getDate()
                                .format(DATE_FORMATTER)
                                + "   "
                                + slot.getStartTime()
                                + " – "
                                + slot.getEndTime()
                );

        dateTimeLabel.getStyleClass().add(
                "welcome-label"
        );

        HBox dateRow = new HBox(
                8,
                statusDot,
                dateTimeLabel
        );

        dateRow.setAlignment(
                Pos.CENTER_LEFT
        );

        Label statusLabel =
                new Label();

        information.getChildren().addAll(
                dateRow,
                statusLabel
        );

        if (past) {
            configurePastSlot(
                    slot,
                    bookedPetName,
                    information,
                    statusDot,
                    statusLabel
            );

        } else if (!slot.isAvailable()) {
            configureBookedSlot(
                    bookedPetName,
                    information,
                    statusDot,
                    statusLabel
            );

        } else {
            configureAvailableSlot(
                    slot,
                    information,
                    statusDot,
                    statusLabel,
                    onDelete
            );
        }

        card.getChildren().add(
                information
        );

        return card;
    }

    // Configura una fascia oraria passata
    private void configurePastSlot(
            TimeSlotBean slot,
            String bookedPetName,
            VBox information,
            Label statusDot,
            Label statusLabel) {

        if (!slot.isAvailable()) {
            statusDot.getStyleClass().add(
                    "error-label"
            );

            statusLabel.setText("Utilizzato");

            statusLabel.getStyleClass().add(
                    "error-label"
            );

            addBookingDetails(
                    information,
                    bookedPetName
            );

        } else {
            statusDot.getStyleClass().add(
                    "past-label"
            );

            statusLabel.setText(
                    "Non utilizzato"
            );

            statusLabel.getStyleClass().add(
                    "past-label"
            );
        }
    }

    // Configura una fascia oraria futura già prenotata
    private void configureBookedSlot(
            String bookedPetName,
            VBox information,
            Label statusDot,
            Label statusLabel) {

        statusDot.getStyleClass().add(
                "error-label"
        );

        statusLabel.setText("Prenotato");

        statusLabel.getStyleClass().add(
                "error-label"
        );

        addBookingDetails(
                information,
                bookedPetName
        );
    }

    // Configura una fascia oraria futura ancora disponibile
    private void configureAvailableSlot(
            TimeSlotBean slot,
            VBox information,
            Label statusDot,
            Label statusLabel,
            Consumer<TimeSlotBean> onDelete) {

        statusDot.getStyleClass().add(
                "success-label"
        );

        statusLabel.setText("Disponibile");

        statusLabel.getStyleClass().add(
                "success-label"
        );

        Button deleteButton =
                new Button("Elimina");

        deleteButton.getStyleClass().add(
                "danger-button"
        );

        deleteButton.setOnAction(
                event -> onDelete.accept(slot)
        );

        HBox buttonRow =
                new HBox(deleteButton);

        buttonRow.setAlignment(
                Pos.CENTER_RIGHT
        );

        information.getChildren().add(
                buttonRow
        );
    }

    // Aggiunge i dati dell'animale associato alla prenotazione
    private void addBookingDetails(
            VBox information,
            String bookedPetName) {

        if (bookedPetName == null
                || bookedPetName.isBlank()) {

            return;
        }

        Label petLabel =
                new Label(
                        "Animale: " + bookedPetName
                );

        petLabel.getStyleClass().add(
                "register-label"
        );

        information.getChildren().add(
                petLabel
        );
    }

    // Costruisce un messaggio per una lista vuota
    private Label emptyLabel(String message) {
        Label label = new Label(message);

        label.getStyleClass().add(
                "register-label"
        );

        return label;
    }
}
