package it.ispwproject.myvet.controller.gui;

import it.ispwproject.myvet.bean.TimeSlotBean;
import it.ispwproject.myvet.controller.applicativo.AvailabilityController;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.view.gui.ViewSlotsGUIView;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public class ViewSlotsGUI {

    private final Stage stage;

    private final AvailabilityController availabilityController =
            new AvailabilityController();

    private final ViewSlotsGUIView view =
            new ViewSlotsGUIView();

    public ViewSlotsGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        BorderPane root = view.buildRoot(
                MainGUI::showDashboardVeterinarian
        );

        view.clearError();

        try {
            List<TimeSlotBean> futureSlots =
                    availabilityController.getSlots();

            List<TimeSlotBean> pastSlots =
                    availabilityController.getPastSlots();

            Map<Integer, String> petBySlot =
                    availabilityController.getPetBySlot();

            List<TimeSlotBean> bookedSlots =
                    futureSlots.stream()
                            .filter(slot -> !slot.isAvailable())
                            .toList();

            List<TimeSlotBean> availableSlots =
                    futureSlots.stream()
                            .filter(TimeSlotBean::isAvailable)
                            .toList();

            view.buildContent(
                    root,
                    availableSlots,
                    bookedSlots,
                    pastSlots,
                    petBySlot,
                    this::handleDelete
            );

        } catch (DAOException e) {
            view.setError(
                    "Errore: " + e.getMessage()
            );

            root.setCenter(view.errorLabel);
        }

        stage.setScene(
                GUIUtils.createScene(root)
        );

        stage.show();
    }

    private void handleDelete(TimeSlotBean slot) {
        Alert confirm =
                new Alert(Alert.AlertType.CONFIRMATION);

        confirm.setTitle("Conferma eliminazione");
        confirm.setHeaderText(null);

        confirm.setContentText(
                "Vuoi eliminare questa fascia oraria?\n"
                        + slot.getDate()
                        + "  "
                        + slot.getStartTime()
                        + " – "
                        + slot.getEndTime()
        );

        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    availabilityController.deleteSlot(
                            slot.getId()
                    );

                    show();

                } catch (DAOException e) {
                    view.setError(
                            "Errore: " + e.getMessage()
                    );
                }
            }
        });
    }
}
