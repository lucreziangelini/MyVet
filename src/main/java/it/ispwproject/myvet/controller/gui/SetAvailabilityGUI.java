package it.ispwproject.myvet.controller.gui;

import it.ispwproject.myvet.bean.TimeSlotBean;
import it.ispwproject.myvet.controller.applicativo.AvailabilityController;
import it.ispwproject.myvet.exception.AvailabilityException;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.view.gui.SetAvailabilityGUIView;

import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class SetAvailabilityGUI {

    private final Stage stage;

    private final AvailabilityController availabilityController =
            new AvailabilityController();

    private final SetAvailabilityGUIView view =
            new SetAvailabilityGUIView();

    public SetAvailabilityGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        view.saveBtn.setOnAction(
                event -> handleSave()
        );

        stage.setScene(
                GUIUtils.createScene(
                        view.buildRoot(
                                MainGUI::showDashboardVeterinarian
                        )
                )
        );

        stage.show();
    }

    private void handleSave() {
        view.clearError();

        LocalDate date = view.datePicker.getValue();

        if (date == null) {
            view.setError("Seleziona una data valida.");
            return;
        }

        LocalTime startTime;
        LocalTime endTime;

        try {
            startTime = LocalTime.parse(
                    view.startTimeField.getText().trim()
            );
        } catch (DateTimeParseException e) {
            view.setError(
                    "Formato ora inizio non valido. Usa HH:MM."
            );
            return;
        }

        try {
            endTime = LocalTime.parse(
                    view.endTimeField.getText().trim()
            );
        } catch (DateTimeParseException e) {
            view.setError(
                    "Formato ora fine non valido. Usa HH:MM."
            );
            return;
        }

        try {
            availabilityController.addSlot(
                    new TimeSlotBean(
                            0,
                            date,
                            startTime,
                            endTime,
                            true
                    )
            );

            Alert alert =
                    new Alert(Alert.AlertType.INFORMATION);

            alert.setTitle("Slot aggiunto");
            alert.setHeaderText(null);
            alert.setContentText(
                    "✓ Slot aggiunto con successo!"
            );

            alert.showAndWait();

            MainGUI.showDashboardVeterinarian();

        } catch (DAOException | AvailabilityException e) {
            view.setError(
                    "Errore: " + e.getMessage()
            );
        }
    }
}