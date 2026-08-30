package it.ispwproject.myvet.controller.gui;

import it.ispwproject.myvet.bean.BookingRequestBean;
import it.ispwproject.myvet.bean.PetBean;
import it.ispwproject.myvet.bean.PetOwnerBean;
import it.ispwproject.myvet.bean.TimeSlotBean;
import it.ispwproject.myvet.bean.VeterinarianBean;
import it.ispwproject.myvet.controller.applicativo.BookingController;
import it.ispwproject.myvet.exception.BookingException;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.model.PetOwner;
import it.ispwproject.myvet.pattern.singleton.SessionManager;
import it.ispwproject.myvet.view.gui.BookAppointmentGUIView;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BookAppointmentGUI {

    private static final int RESERVATION_SECONDS = 300;

    private final javafx.stage.Stage stage;

    private final BookingController bookingController =
            new BookingController();

    private final BookAppointmentGUIView view =
            new BookAppointmentGUIView();

    private PetBean selectedPet;
    private LocalDate selectedDate;
    private VeterinarianBean selectedVeterinarian;
    private TimeSlotBean selectedSlot;

    private ToggleGroup veterinarianGroup = new ToggleGroup();
    private ToggleGroup slotGroup = new ToggleGroup();

    public BookAppointmentGUI(javafx.stage.Stage stage) {
        this.stage = stage;
    }

    public void show() {
        loadPets();
        bindPetList();
        bindDatePicker();
        bindBookButton();

        stage.setScene(GUIUtils.createScene(view.buildRoot()));
        stage.show();
    }

    private void loadPets() {
        try {
            List<PetBean> pets =
                    bookingController.getRegisteredPets();

            view.petList.getItems().setAll(pets);

            if (pets.isEmpty()) {
                view.setError("Nessun animale registrato.");
            }

        } catch (DAOException e) {
            view.setError(
                    "Errore durante il caricamento degli animali: "
                            + e.getMessage()
            );
        }
    }

    private void bindPetList() {
        view.petList.setOnMouseClicked(event -> {
            PetBean pet = view.petList
                    .getSelectionModel()
                    .getSelectedItem();

            if (pet == null) {
                return;
            }

            selectedPet = pet;
            selectedDate = null;
            selectedVeterinarian = null;
            selectedSlot = null;

            view.setStepDone(view.step1Dot);
            view.setStepPending(view.step2Dot);
            view.setDateSectionEnabled(true);
            view.datePicker.setValue(null);
            view.clearError();

            resetVeterinarianSection();
        });
    }

    private void bindDatePicker() {
        view.datePicker.valueProperty().addListener(
                (observable, oldDate, newDate) -> {

                    selectedDate = null;
                    selectedVeterinarian = null;
                    selectedSlot = null;

                    resetVeterinarianSection();

                    if (newDate == null) {
                        return;
                    }

                    if (newDate.isBefore(LocalDate.now())) {
                        view.setError(
                                "Non puoi selezionare una data nel passato."
                        );
                        return;
                    }

                    selectedDate = newDate;

                    view.setStepDone(view.step2Dot);
                    view.setStepPending(view.step3Dot);
                    view.clearError();

                    loadVeterinarians(newDate);
                }
        );
    }

    private void loadVeterinarians(LocalDate date) {
        try {
            List<VeterinarianBean> veterinarians =
                    bookingController.getAvailableVeterinarians(date);

            veterinarianGroup = new ToggleGroup();
            view.veterinarianList.getChildren().clear();
            view.setVeterinarianSectionEnabled(true);

            if (veterinarians.isEmpty()) {
                view.veterinarianList.getChildren().add(
                        view.buildHintLabel(
                                "Nessun veterinario disponibile nella data selezionata"
                        )
                );
                return;
            }

            List<VeterinarianBean> ordered =
                    new ArrayList<>(veterinarians);

            ordered.sort(
                    (first, second) -> Boolean.compare(
                            !first.isFavourite(),
                            !second.isFavourite()
                    )
            );

            for (VeterinarianBean veterinarian : ordered) {
                view.veterinarianList.getChildren().add(
                        view.buildVeterinarianRow(
                                veterinarian,
                                veterinarianGroup,
                                () -> showVeterinarianBio(veterinarian),
                                veterinarian.isFavourite()
                        )
                );
            }

            veterinarianGroup.selectedToggleProperty().addListener(
                    (observable, oldToggle, newToggle) -> {
                        if (newToggle == null) {
                            return;
                        }

                        selectedVeterinarian =
                                (VeterinarianBean) newToggle.getUserData();

                        selectedSlot = null;

                        view.setStepDone(view.step3Dot);
                        view.setStepPending(view.step4Dot);

                        loadTimeSlots(
                                selectedVeterinarian,
                                selectedDate
                        );
                    }
            );

        } catch (DAOException e) {
            view.setError("Errore: " + e.getMessage());
        }
    }

    private void loadTimeSlots(
            VeterinarianBean veterinarian,
            LocalDate date) {

        try {
            List<TimeSlotBean> slots =
                    bookingController.getVeterinarianAvailability(
                                    veterinarian,
                                    date
                            )
                            .stream()
                            .filter(TimeSlotBean::isAvailable)
                            .toList();

            slotGroup = new ToggleGroup();

            view.slotList.getChildren().clear();
            view.setSlotSectionEnabled(true);
            view.bookBtn.setDisable(true);
            view.clearError();

            if (slots.isEmpty()) {
                view.slotList.getChildren().add(
                        view.buildHintLabel(
                                "Nessun orario disponibile"
                        )
                );
                return;
            }

            for (TimeSlotBean slot : slots) {
                view.slotList.getChildren().add(
                        view.buildToggle(
                                slot.getStartTime()
                                        + " – "
                                        + slot.getEndTime(),
                                slot,
                                slotGroup
                        )
                );
            }

            slotGroup.selectedToggleProperty().addListener(
                    (observable, oldToggle, newToggle) -> {
                        if (newToggle == null) {
                            return;
                        }

                        selectedSlot =
                                (TimeSlotBean) newToggle.getUserData();

                        view.setStepDone(view.step4Dot);
                        view.bookBtn.setDisable(false);
                    }
            );

        } catch (DAOException e) {
            view.setError("Errore: " + e.getMessage());
        }
    }

    private void bindBookButton() {
        view.bookBtn.setOnAction(event -> {
            if (selectedPet == null
                    || selectedDate == null
                    || selectedVeterinarian == null
                    || selectedSlot == null) {

                view.setError("Completa tutte le selezioni.");
                return;
            }

            try {
                BookingRequestBean request = buildRequest();

                bookingController.prepareBookingSummary(request);
                showCountdownDialog(request);

            } catch (DAOException | BookingException e) {
                view.setError("Errore: " + e.getMessage());
            }
        });
    }

    private void showVeterinarianBio(VeterinarianBean veterinarian) {
        Alert alert =
                new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Informazioni veterinario");

        alert.setHeaderText(
                veterinarian.getName()
                        + " "
                        + veterinarian.getSurname()
        );

        alert.setResizable(true);

        String bio = veterinarian.getBio() == null
                || veterinarian.getBio().isBlank()
                ? "Bio non disponibile."
                : veterinarian.getBio();

        String specialization =
                veterinarian.getSpecialization() == null
                        || veterinarian.getSpecialization().isBlank()
                        ? "Non specificata"
                        : veterinarian.getSpecialization();

        TextArea textArea = new TextArea(
                "Specializzazione: "
                        + specialization
                        + "\n\n"
                        + bio
        );

        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefWidth(400);
        textArea.setPrefHeight(200);

        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }

    private void showCountdownDialog(
            BookingRequestBean request) {

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd/MM/yyyy");

        int[] secondsLeft = {RESERVATION_SECONDS};

        Alert confirm =
                new Alert(Alert.AlertType.CONFIRMATION);

        confirm.setTitle("Conferma appuntamento");
        confirm.setHeaderText(
                "⏱ Slot riservato per 5 minuti"
        );

        Label contentLabel = new Label(
                buildSummaryText(formatter, 5, 0)
        );

        confirm.getDialogPane().setContent(contentLabel);

        Timeline countdown = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    secondsLeft[0]--;

                    int minutes = secondsLeft[0] / 60;
                    int seconds = secondsLeft[0] % 60;

                    contentLabel.setText(
                            buildSummaryText(
                                    formatter,
                                    minutes,
                                    seconds
                            )
                    );

                    if (secondsLeft[0] <= 0) {
                        confirm.close();

                        try {
                            bookingController.releaseSlot(
                                    selectedSlot.getId()
                            );
                        } catch (DAOException ignored) {
                            // Lo slot potrebbe essere già stato rilasciato.
                        }

                        Platform.runLater(() ->
                                view.setError(
                                        "Tempo scaduto. Lo slot è stato rilasciato."
                                )
                        );
                    }
                })
        );

        countdown.setCycleCount(RESERVATION_SECONDS);
        countdown.play();

        confirm.showAndWait().ifPresent(result -> {
            countdown.stop();

            if (result == ButtonType.OK) {
                confirmBooking(request);
            } else {
                try {
                    bookingController.releaseSlot(
                            selectedSlot.getId()
                    );
                } catch (DAOException e) {
                    view.setError(
                            "Errore: " + e.getMessage()
                    );
                }
            }
        });
    }

    private String buildSummaryText(
            DateTimeFormatter formatter,
            int minutes,
            int seconds) {

        return "Animale:       "
                + selectedPet.getName()
                + "\n"
                + "Veterinario:  "
                + selectedVeterinarian.getFullName()
                + "\n"
                + "Data:          "
                + selectedDate.format(formatter)
                + "\n"
                + "Orario:        "
                + selectedSlot.getStartTime()
                + " – "
                + selectedSlot.getEndTime()
                + "\n\n"
                + String.format(
                "Tempo rimasto: %d:%02d",
                minutes,
                seconds
        );
    }

    private void confirmBooking(
            BookingRequestBean request) {

        try {
            bookingController.createBooking(request);

            Alert alert =
                    new Alert(Alert.AlertType.INFORMATION);

            alert.setTitle("Appuntamento confermato");
            alert.setHeaderText(null);
            alert.setContentText(
                    "✓ Appuntamento prenotato con successo!"
            );

            alert.showAndWait();

            MainGUI.showDashboardPetOwner();

        } catch (BookingException e) {
            try {
                bookingController.releaseSlot(
                        selectedSlot.getId()
                );
            } catch (DAOException ignored) {
                // Lo slot potrebbe essere già stato rilasciato.
            }

            view.setError("Errore: " + e.getMessage());

        } catch (DAOException e) {
            view.setError("Errore: " + e.getMessage());
        }
    }

    private void resetVeterinarianSection() {
        view.veterinarianList.getChildren().setAll(
                view.buildHintLabel(
                        "Seleziona prima una data"
                )
        );

        veterinarianGroup.getToggles().clear();
        view.setVeterinarianSectionEnabled(false);

        resetSlotSection();
    }

    private void resetSlotSection() {
        view.slotList.getChildren().setAll(
                view.buildHintLabel(
                        "Seleziona prima un veterinario"
                )
        );

        slotGroup.getToggles().clear();
        view.setSlotSectionEnabled(false);

        selectedVeterinarian = null;
        selectedSlot = null;
        view.bookBtn.setDisable(true);
    }

    private BookingRequestBean buildRequest() {
        PetOwner owner =
                (PetOwner) SessionManager.getInstance()
                        .getLoggedUser();

        PetOwnerBean ownerBean = new PetOwnerBean(
                owner.getId(),
                owner.getName(),
                owner.getSurname(),
                owner.getEmail()
        );

        return new BookingRequestBean(
                ownerBean,
                selectedPet,
                selectedVeterinarian,
                selectedSlot
        );
    }
}