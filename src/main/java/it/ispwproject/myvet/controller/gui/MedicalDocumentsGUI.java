package it.ispwproject.myvet.controller.gui;

import it.ispwproject.myvet.bean.MedicalDocumentBean;
import it.ispwproject.myvet.bean.PetBean;
import it.ispwproject.myvet.controller.applicativo.ActivityController;
import it.ispwproject.myvet.controller.applicativo.BookingController;
import it.ispwproject.myvet.controller.applicativo.MedicalDocumentController;
import it.ispwproject.myvet.enumerator.DocumentType;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.pattern.singleton.SessionManager;
import it.ispwproject.myvet.view.gui.MedicalDocumentsGUIView;

import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.List;

public class MedicalDocumentsGUI {

    private static final String ERROR_PREFIX = "Errore: ";

    private final Stage stage;

    private final MedicalDocumentController documentController =
            new MedicalDocumentController();

    private final BookingController bookingController =
            new BookingController();

    private final ActivityController activityController =
            new ActivityController();

    private final MedicalDocumentsGUIView view =
            new MedicalDocumentsGUIView();

    private BorderPane root;
    private PetBean selectedPet;

    public MedicalDocumentsGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        boolean veterinarian =
                SessionManager.getInstance().isVeterinarian();

        Runnable backAction;

        if (veterinarian) {
            backAction = MainGUI::showDashboardVeterinarian;
        } else {
            backAction = MainGUI::showDashboardPetOwner;
        }

        root = view.buildRoot(backAction);
        view.clearError();
        view.setUploadVisible(veterinarian);

        loadPets(veterinarian);

        view.petCombo.setOnAction(event -> {
            selectedPet = view.petCombo.getValue();

            if (selectedPet != null) {
                loadDocuments();
            }
        });

        view.uploadBtn.setOnAction(event -> {
            if (selectedPet == null) {
                view.setError(
                        "Seleziona prima un animale."
                );
                return;
            }

            handleUpload();
        });

        stage.setScene(
                GUIUtils.createScene(root)
        );

        stage.show();
    }

    private void loadPets(boolean veterinarian) {
        try {
            List<PetBean> pets;

            if (veterinarian) {
                pets = activityController.getPets();
            } else {
                pets = bookingController.getRegisteredPets();
            }

            view.setPets(pets, veterinarian);

        } catch (DAOException e) {
            view.setPets(List.of(), veterinarian);
            view.setError(
                    ERROR_PREFIX + e.getMessage()
            );
        }
    }

    private void loadDocuments() {
        try {
            List<MedicalDocumentBean> documents =
                    documentController.getDocuments(
                            selectedPet.getId()
                    );

            view.showDocuments(
                    selectedPet,
                    documents
            );

        } catch (DAOException e) {
            view.setError(
                    ERROR_PREFIX + e.getMessage()
            );
        }
    }

    private void handleUpload() {
        String title =
                view.chiediTitoloDocumento();

        if (title == null || title.isBlank()) {
            return;
        }

        DocumentType type =
                view.chiediTipoDocumento();

        if (type == null) {
            return;
        }

        String storageReference =
                view.chiediRiferimentoDocumento();

        if (storageReference == null
                || storageReference.isBlank()) {
            return;
        }

        MedicalDocumentBean document =
                new MedicalDocumentBean(
                        0,
                        selectedPet,
                        null,
                        title,
                        type,
                        storageReference,
                        null
                );

        try {
            documentController.uploadDocument(document);

            Alert alert =
                    new Alert(Alert.AlertType.INFORMATION);

            alert.setTitle("Documento caricato");
            alert.setHeaderText(null);
            alert.setContentText(
                    "✓ Documento medico caricato con successo!"
            );

            alert.showAndWait();
            loadDocuments();

        } catch (DAOException e) {
            view.setError(
                    ERROR_PREFIX + e.getMessage()
            );
        }
    }
}
