package it.ispwproject.myvet.view.gui;

import it.ispwproject.myvet.bean.BookingResponseBean;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.function.Consumer;

public class ViewAppointmentGUIView extends PageGUIView {

    public final Label errorLabel =
            buildErrorLabel();

    public void setError(String message) {
        errorLabel.setText(message);
    }

    public void clearError() {
        errorLabel.setText("");
    }

    // ────────────────────────────────────────────────────────────────────────
    // Costruzione della pagina degli appuntamenti
    // ────────────────────────────────────────────────────────────────────────

    public BorderPane buildRoot(Runnable onBack) {
        return buildShell(
                "I miei appuntamenti",
                onBack
        );
    }

    // ────────────────────────────────────────────────────────────────────────
    // Costruzione del contenuto principale
    // ────────────────────────────────────────────────────────────────────────

    public void buildContent(
            BorderPane root,
            List<BookingResponseBean> confirmed,
            List<BookingResponseBean> cancelled,
            List<BookingResponseBean> past,
            Consumer<BookingResponseBean> onCancel) {

        VBox content = new VBox(12);

        content.setPadding(new Insets(24));
        content.setAlignment(Pos.TOP_CENTER);

        // Pulsanti per cambiare categoria
        ToggleButton confirmedButton =
                new ToggleButton(
                        "Confermati (" + confirmed.size() + ")"
                );

        ToggleButton cancelledButton =
                new ToggleButton(
                        "Cancellati (" + cancelled.size() + ")"
                );

        ToggleButton pastButton =
                new ToggleButton(
                        "Passati (" + past.size() + ")"
                );

        confirmedButton.getStyleClass().add(
                "toggle-card"
        );

        cancelledButton.getStyleClass().addAll(
                "toggle-card",
                "cancelled"
        );

        pastButton.getStyleClass().addAll(
                "toggle-card",
                "expired"
        );

        configureToggleButton(confirmedButton);
        configureToggleButton(cancelledButton);
        configureToggleButton(pastButton);

        ToggleGroup categoryGroup =
                new ToggleGroup();

        confirmedButton.setToggleGroup(categoryGroup);
        cancelledButton.setToggleGroup(categoryGroup);
        pastButton.setToggleGroup(categoryGroup);

        confirmedButton.setSelected(true);

        HBox toggleBar = new HBox(
                8,
                confirmedButton,
                cancelledButton,
                pastButton
        );

        toggleBar.setAlignment(Pos.CENTER);
        toggleBar.setMaxWidth(640);

        // Contenitore della lista degli appuntamenti
        VBox listBox = new VBox(12);

        listBox.setAlignment(
                Pos.TOP_CENTER
        );

        // Gli appuntamenti passati possono essere filtrati per animale
        List<String> petNames = past.stream()
                .filter(booking ->
                        booking.getPet() != null
                )
                .map(booking ->
                        booking.getPet().getName()
                )
                .distinct()
                .sorted()
                .toList();

        FlowPane petFilterBar =
                buildPetFilterBar();

        petFilterBar.setVisible(false);
        petFilterBar.setManaged(false);

        String[] selectedPet = {null};

        // Aggiorna i pulsanti del filtro per animale
        Runnable refreshPetFilter = () -> {
            petFilterBar.getChildren().clear();

            ToggleGroup petGroup =
                    new ToggleGroup();

            ToggleButton allButton =
                    new ToggleButton("Tutti");

            allButton.getStyleClass().addAll(
                    "toggle-card",
                    "expired"
            );

            allButton.setToggleGroup(petGroup);
            allButton.setPrefHeight(30);
            allButton.setSelected(
                    selectedPet[0] == null
            );

            allButton.setOnAction(event -> {
                selectedPet[0] = null;

                refreshPastList(
                        listBox,
                        past,
                        null
                );
            });

            petFilterBar.getChildren().add(
                    allButton
            );

            for (String petName : petNames) {
                ToggleButton petButton =
                        new ToggleButton(petName);

                petButton.getStyleClass().addAll(
                        "toggle-card",
                        "expired"
                );

                petButton.setToggleGroup(petGroup);
                petButton.setPrefHeight(30);

                petButton.setSelected(
                        petName.equals(selectedPet[0])
                );

                petButton.setOnAction(event -> {
                    selectedPet[0] = petName;

                    refreshPastList(
                            listBox,
                            past,
                            petName
                    );
                });

                petFilterBar.getChildren().add(
                        petButton
                );
            }
        };

        refreshPetFilter.run();

        // Aggiorna la lista in base alla categoria selezionata
        Runnable refreshList = () -> {
            listBox.getChildren().clear();

            boolean showingPast =
                    pastButton.isSelected();

            petFilterBar.setVisible(showingPast);
            petFilterBar.setManaged(showingPast);

            if (confirmedButton.isSelected()) {
                showConfirmedBookings(
                        listBox,
                        confirmed,
                        onCancel
                );

            } else if (cancelledButton.isSelected()) {
                showCancelledBookings(
                        listBox,
                        cancelled
                );

            } else {
                selectedPet[0] = null;
                refreshPetFilter.run();

                refreshPastList(
                        listBox,
                        past,
                        null
                );
            }
        };

        refreshList.run();

        confirmedButton.setOnAction(
                event -> refreshList.run()
        );

        cancelledButton.setOnAction(
                event -> refreshList.run()
        );

        pastButton.setOnAction(
                event -> refreshList.run()
        );

        content.getChildren().addAll(
                toggleBar,
                petFilterBar,
                listBox,
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

    // ────────────────────────────────────────────────────────────────────────
    // Appuntamenti confermati
    // ────────────────────────────────────────────────────────────────────────

    private void showConfirmedBookings(
            VBox listBox,
            List<BookingResponseBean> bookings,
            Consumer<BookingResponseBean> onCancel) {

        if (bookings.isEmpty()) {
            listBox.getChildren().add(
                    emptyLabel(
                            "Non hai appuntamenti confermati."
                    )
            );

            return;
        }

        for (BookingResponseBean booking : bookings) {
            listBox.getChildren().add(
                    buildBookingCard(
                            booking,
                            true,
                            onCancel
                    )
            );
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // Appuntamenti cancellati
    // ────────────────────────────────────────────────────────────────────────

    private void showCancelledBookings(
            VBox listBox,
            List<BookingResponseBean> bookings) {

        if (bookings.isEmpty()) {
            listBox.getChildren().add(
                    emptyLabel(
                            "Non hai appuntamenti cancellati."
                    )
            );

            return;
        }

        for (BookingResponseBean booking : bookings) {
            listBox.getChildren().add(
                    buildBookingCard(
                            booking,
                            false,
                            ignored -> {
                                // Gli appuntamenti cancellati
                                // non possono essere annullati nuovamente.
                            }
                    )
            );
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // Appuntamenti passati
    // ────────────────────────────────────────────────────────────────────────

    private void refreshPastList(
            VBox listBox,
            List<BookingResponseBean> past,
            String petFilter) {

        listBox.getChildren().clear();

        List<BookingResponseBean> filtered =
                petFilter == null
                        ? past
                        : past.stream()
                        .filter(booking ->
                                booking.getPet() != null
                                        && booking.getPet()
                                        .getName()
                                        .equals(petFilter)
                        )
                        .toList();

        if (filtered.isEmpty()) {
            listBox.getChildren().add(
                    emptyLabel(
                            "Nessun appuntamento passato."
                    )
            );

            return;
        }

        List<String> petNames = filtered.stream()
                .filter(booking ->
                        booking.getPet() != null
                )
                .map(booking ->
                        booking.getPet().getName()
                )
                .distinct()
                .sorted()
                .toList();

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "dd/MM/yyyy"
                );

        // Raggruppa gli appuntamenti passati per animale
        for (String petName : petNames) {
            List<BookingResponseBean> group =
                    filtered.stream()
                            .filter(booking ->
                                    booking.getPet()
                                            .getName()
                                            .equals(petName)
                            )
                            .sorted((first, second) ->
                                    second.getTimeSlot()
                                            .getDate()
                                            .compareTo(
                                                    first.getTimeSlot()
                                                            .getDate()
                                            )
                            )
                            .toList();

            HBox petHeader =
                    buildPetHeader(
                            petName,
                            group.size()
                    );

            listBox.getChildren().add(
                    petHeader
            );

            VBox groupCard =
                    new VBox(0);

            groupCard.getStyleClass().add(
                    "info-card"
            );

            groupCard.setMaxWidth(640);

            for (int index = 0;
                 index < group.size();
                 index++) {

                BookingResponseBean booking =
                        group.get(index);

                groupCard.getChildren().add(
                        buildPastBookingRow(
                                booking,
                                formatter
                        )
                );

                if (index < group.size() - 1) {
                    groupCard.getChildren().add(
                            buildSeparatorLine()
                    );
                }
            }

            listBox.getChildren().add(
                    groupCard
            );
        }
    }

    // Costruisce l'intestazione di un gruppo di appuntamenti
    private HBox buildPetHeader(
            String petName,
            int appointmentCount) {

        HBox header = new HBox(8);

        header.setAlignment(Pos.CENTER_LEFT);
        header.setMaxWidth(640);

        header.setPadding(
                new Insets(8, 0, 4, 0)
        );

        Label petLabel =
                new Label(petName);

        petLabel.getStyleClass().add(
                "small-label"
        );

        petLabel.setStyle(
                "-fx-font-weight: bold;"
        );

        String unit =
                appointmentCount == 1
                        ? "appuntamento"
                        : "appuntamenti";

        Label countLabel =
                new Label(
                        "("
                                + appointmentCount
                                + " "
                                + unit
                                + ")"
                );

        countLabel.getStyleClass().add(
                "info-text"
        );

        countLabel.setStyle(
                "-fx-text-fill: #888;"
        );

        header.getChildren().addAll(
                petLabel,
                countLabel
        );

        return header;
    }

    // Costruisce una riga relativa a un appuntamento passato
    private HBox buildPastBookingRow(
            BookingResponseBean booking,
            DateTimeFormatter formatter) {

        HBox row = new HBox(12);

        row.setAlignment(Pos.CENTER_LEFT);

        row.setPadding(
                new Insets(8, 12, 8, 12)
        );

        row.setMaxWidth(Double.MAX_VALUE);

        Label dateLabel =
                new Label(
                        booking.getTimeSlot()
                                .getDate()
                                .format(formatter)
                );

        dateLabel.getStyleClass().add(
                "register-label"
        );

        dateLabel.setPrefWidth(90);

        Label timeLabel =
                new Label(
                        booking.getTimeSlot()
                                .getStartTime()
                                + " – "
                                + booking.getTimeSlot()
                                .getEndTime()
                );

        timeLabel.getStyleClass().add(
                "info-text"
        );

        timeLabel.setPrefWidth(110);

        Label veterinarianLabel =
                new Label(
                        booking.getVeterinarian()
                                .getFullName()
                );

        veterinarianLabel.getStyleClass().add(
                "register-label"
        );

        HBox.setHgrow(
                veterinarianLabel,
                Priority.ALWAYS
        );

        row.getChildren().addAll(
                dateLabel,
                timeLabel,
                veterinarianLabel
        );

        return row;
    }

    // Crea la barra usata per filtrare gli appuntamenti per animale
    private FlowPane buildPetFilterBar() {
        FlowPane filterBar =
                new FlowPane(6, 6);

        filterBar.setAlignment(
                Pos.CENTER_LEFT
        );

        filterBar.setMaxWidth(640);

        filterBar.setPadding(
                new Insets(0, 0, 4, 0)
        );

        return filterBar;
    }

    // ────────────────────────────────────────────────────────────────────────
    // Card di un appuntamento
    // ────────────────────────────────────────────────────────────────────────

    private VBox buildBookingCard(
            BookingResponseBean booking,
            boolean cancellable,
            Consumer<BookingResponseBean> onCancel) {

        boolean canCancel = cancellable
                && booking.getTimeSlot() != null
                && booking.getTimeSlot().getDate() != null
                && booking.getTimeSlot().getStartTime() != null
                && LocalDateTime.of(
                booking.getTimeSlot().getDate(),
                booking.getTimeSlot().getStartTime()
        ).isAfter(LocalDateTime.now(ZoneId.systemDefault()));

        VBox card = new VBox(8);

        card.getStyleClass().add(
                "info-card"
        );

        card.setMaxWidth(640);

        VBox information = new VBox(4);

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "dd/MM/yyyy"
                );

        // Data e orario
        Label statusDot =
                new Label("●");

        statusDot.getStyleClass().add(
                cancellable
                        ? "success-label"
                        : "error-label"
        );

        statusDot.setStyle(
                "-fx-font-size: 14px;"
        );

        Label dateTimeLabel =
                new Label(
                        booking.getTimeSlot()
                                .getDate()
                                .format(formatter)
                                + "   "
                                + booking.getTimeSlot()
                                .getStartTime()
                                + " – "
                                + booking.getTimeSlot()
                                .getEndTime()
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

        // Stato della prenotazione
        Label statusLabel =
                new Label(
                        cancellable
                                ? "Confermato"
                                : "Cancellato"
                );

        statusLabel.getStyleClass().add(
                cancellable
                        ? "success-label"
                        : "error-label"
        );

        statusLabel.setStyle(
                "-fx-font-weight: bold;"
        );

        // Animale selezionato
        Label petLabel =
                new Label(
                        "Animale: "
                                + booking.getPet().getName()
                );

        petLabel.getStyleClass().add(
                "small-label"
        );

        // Veterinario selezionato
        Label veterinarianLabel =
                new Label(
                        "Veterinario: "
                                + booking.getVeterinarian()
                                .getFullName()
                );

        veterinarianLabel.getStyleClass().add(
                "register-label"
        );

        information.getChildren().addAll(
                dateRow,
                statusLabel,
                petLabel,
                veterinarianLabel
        );

        if (booking.getVeterinarian().getEmail() != null) {
            Label emailLabel =
                    new Label(
                            "Email: "
                                    + booking.getVeterinarian()
                                    .getEmail()
                    );

            emailLabel.getStyleClass().add(
                    "info-text"
            );

            information.getChildren().add(
                    emailLabel
            );
        }

        // Il pulsante di annullamento è presente solo
        // per gli appuntamenti futuri confermati
        if (canCancel) {
            HBox bottomRow = new HBox();

            bottomRow.setAlignment(
                    Pos.CENTER_LEFT
            );

            Region spacer = new Region();

            HBox.setHgrow(
                    spacer,
                    Priority.ALWAYS
            );

            Button cancelButton =
                    new Button("Annulla");

            cancelButton.getStyleClass().add(
                    "danger-button"
            );

            cancelButton.setOnAction(
                    event ->
                            onCancel.accept(booking)
            );

            bottomRow.getChildren().addAll(
                    spacer,
                    cancelButton
            );

            information.getChildren().add(
                    bottomRow
            );
        }

        card.getChildren().add(
                information
        );

        return card;
    }

    // Costruisce una linea di separazione tra due appuntamenti
    private Region buildSeparatorLine() {
        Region line = new Region();

        line.setPrefHeight(1);
        line.setMaxWidth(Double.MAX_VALUE);

        line.setStyle(
                "-fx-background-color: #eef2f6;"
        );

        line.setMouseTransparent(true);

        return line;
    }

    // Costruisce un messaggio per una lista vuota
    private Label emptyLabel(String text) {
        Label label = new Label(text);

        label.getStyleClass().add(
                "register-label"
        );

        return label;
    }
}
