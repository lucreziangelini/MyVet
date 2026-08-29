package it.ispwproject.myvet.controller.gui;

import it.ispwproject.myvet.bean.ActivityBean;
import it.ispwproject.myvet.bean.PetBean;
import it.ispwproject.myvet.bean.ProgressBean;
import it.ispwproject.myvet.controller.applicativo.ActivityController;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.view.gui.ManagePetCareGUIView;

import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class ManagePetCareGUI {

    private final Stage stage;

    private final ActivityController activityController =
            new ActivityController();

    private final ManagePetCareGUIView view =
            new ManagePetCareGUIView();

    public ManagePetCareGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        BorderPane root = view.buildRoot(
                MainGUI::showDashboardVeterinarian
        );

        try {
            view.petCombo.getItems().setAll(
                    activityController.getPets()
            );
        } catch (DAOException e) {
            view.errorLabel.setText(
                    "Errore: " + e.getMessage()
            );
        }

        view.petCombo.setOnAction(event -> {
            PetBean selected = view.petCombo.getValue();

            if (selected == null) {
                return;
            }

            loadPetCard(selected);
        });

        stage.setScene(GUIUtils.createScene(root));
        stage.show();
    }

    private void loadPetCard(PetBean pet) {
        VBox card = view.getPetCard();

        try {
            ProgressBean progress =
                    activityController.getProgress(pet.getId());

            List<ActivityBean> activities =
                    activityController.getActivities(pet.getId());

            view.buildPetCard(
                    card,
                    pet,
                    progress,
                    activities,
                    notes -> handleUpdateProgress(pet, notes),
                    description -> handleAssignActivity(
                            pet,
                            description
                    )
            );

        } catch (DAOException e) {
            view.errorLabel.setText(
                    "Errore: " + e.getMessage()
            );
        }
    }

    private void handleUpdateProgress(
            PetBean pet,
            String notes) {

        if (notes.isBlank()) {
            view.errorLabel.setText(
                    "Le note non possono essere vuote."
            );
            return;
        }

        try {
            activityController.updateProgress(
                    new ProgressBean(pet, notes, null)
            );

            showInfo("Progressi aggiornati con successo.");
            loadPetCard(pet);

        } catch (DAOException e) {
            view.errorLabel.setText(
                    "Errore: " + e.getMessage()
            );
        }
    }

    private void handleAssignActivity(
            PetBean pet,
            String description) {

        if (description.isBlank()) {
            view.errorLabel.setText(
                    "La descrizione non può essere vuota."
            );
            return;
        }

        try {
            activityController.assignActivity(
                    new ActivityBean(
                            0,
                            pet,
                            description,
                            false,
                            null
                    )
            );

            loadPetCard(pet);

        } catch (DAOException e) {
            view.errorLabel.setText(
                    "Errore: " + e.getMessage()
            );
        }
    }

    private void showInfo(String message) {
        Alert alert =
                new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Operazione completata");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}