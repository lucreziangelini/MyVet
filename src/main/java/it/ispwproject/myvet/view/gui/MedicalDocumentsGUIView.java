package it.ispwproject.myvet.view.gui;

import it.ispwproject.myvet.bean.MedicalDocumentBean;
import it.ispwproject.myvet.bean.PetBean;
import it.ispwproject.myvet.enumerator.DocumentType;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class MedicalDocumentsGUIView extends PageGUIView {

    public final ComboBox<PetBean> petCombo =
            new ComboBox<>();

    public final Button uploadBtn =
            new Button("Carica documento");

    public final Label errorLabel =
            buildErrorLabel();

    private final VBox documentsContainer =
            new VBox(14);

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public MedicalDocumentsGUIView() {
        configurePetCombo();

        uploadBtn.setPrefWidth(200);
        uploadBtn.setPrefHeight(42);
        uploadBtn.getStyleClass().add("button");

        documentsContainer.setAlignment(Pos.TOP_CENTER);
        documentsContainer.setMaxWidth(680);
    }

    public BorderPane buildRoot(Runnable onBack) {
        BorderPane root = buildShell(
                "Documenti medici",
                onBack
        );

        VBox content = new VBox(18);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(28));
        content.getStyleClass().add("myvet-background");

        Label title = new Label(
                "Gestione documenti medici"
        );
        title.getStyleClass().add("page-title");

        Label instruction = new Label(
                "Seleziona un animale per visualizzare "
                        + "i documenti disponibili."
        );
        instruction.getStyleClass().add("info-text");
        instruction.setWrapText(true);

        HBox actions = new HBox(
                12,
                petCombo,
                uploadBtn
        );
        actions.setAlignment(Pos.CENTER);

        documentsContainer.getChildren().setAll(
                new Label(
                        "Seleziona un animale per continuare."
                )
        );

        content.getChildren().addAll(
                title,
                instruction,
                actions,
                errorLabel,
                documentsContainer
        );

        root.setCenter(
                transparentScroll(content)
        );

        return root;
    }

    private void configurePetCombo() {
        petCombo.setPromptText("Seleziona un animale");
        petCombo.setPrefWidth(300);

        petCombo.setConverter(
                new StringConverter<>() {

                    @Override
                    public String toString(PetBean pet) {
                        if (pet == null) {
                            return "";
                        }

                        String description =
                                pet.getName()
                                        + " – "
                                        + pet.getSpecies();

                        if (pet.getBreed() != null
                                && !pet.getBreed().isBlank()) {

                            description +=
                                    " – " + pet.getBreed();
                        }

                        return description;
                    }

                    @Override
                    public PetBean fromString(String value) {
                        return null;
                    }
                }
        );
    }

    public void showDocuments(
            BorderPane root,
            PetBean pet,
            List<MedicalDocumentBean> documents) {

        documentsContainer.getChildren().clear();

        Label title = new Label(
                "Documenti medici di " + pet.getName()
        );
        title.getStyleClass().add("section-title");

        documentsContainer.getChildren().add(title);

        if (documents == null || documents.isEmpty()) {
            Label emptyLabel = new Label(
                    "Nessun documento medico disponibile."
            );
            emptyLabel.getStyleClass().add("info-text");

            documentsContainer
                    .getChildren()
                    .add(emptyLabel);

            return;
        }

        for (MedicalDocumentBean document : documents) {
            documentsContainer
                    .getChildren()
                    .add(buildDocumentCard(document));
        }
    }

    private VBox buildDocumentCard(
            MedicalDocumentBean document) {

        VBox card = new VBox(7);
        card.setMaxWidth(650);
        card.setPadding(new Insets(16));
        card.getStyleClass().add("info-card");

        Label title = new Label(
                document.getTitle()
        );
        title.getStyleClass().add("section-title");
        title.setWrapText(true);

        Label type = new Label(
                "Tipo: "
                        + formatDocumentType(
                        document.getType()
                )
        );
        type.getStyleClass().add("info-text");

        card.getChildren().addAll(
                title,
                type
        );

        if (document.getVeterinarian() != null) {
            Label veterinarian = new Label(
                    "Veterinario: "
                            + document
                            .getVeterinarian()
                            .getFullName()
            );

            veterinarian
                    .getStyleClass()
                    .add("info-text");

            card.getChildren().add(veterinarian);
        }

        if (document.getUploadedAt() != null) {
            Label uploadedAt = new Label(
                    "Caricato il: "
                            + document
                            .getUploadedAt()
                            .format(DATE_FORMATTER)
            );

            uploadedAt
                    .getStyleClass()
                    .add("info-text");

            card.getChildren().add(uploadedAt);
        }

        if (document.getStorageReference() != null
                && !document
                .getStorageReference()
                .isBlank()) {

            Label reference = new Label(
                    "Riferimento: "
                            + document
                            .getStorageReference()
            );

            reference.setWrapText(true);
            reference
                    .getStyleClass()
                    .add("info-text");

            card.getChildren().add(reference);
        }

        return card;
    }

    public void setUploadVisible(boolean visible) {
        uploadBtn.setVisible(visible);
        uploadBtn.setManaged(visible);
    }

    public String chiediTitoloDocumento() {
        TextInputDialog dialog =
                new TextInputDialog();

        dialog.setTitle("Carica documento");
        dialog.setHeaderText(
                "Inserisci il titolo del documento"
        );
        dialog.setContentText("Titolo:");

        return dialog
                .showAndWait()
                .orElse(null);
    }

    public DocumentType chiediTipoDocumento() {
        List<String> documentTypes = List.of(
                "Referto medico",
                "Risultato di laboratorio",
                "Prescrizione",
                "Certificato di vaccinazione",
                "Altro"
        );

        ChoiceDialog<String> dialog =
                new ChoiceDialog<>(
                        documentTypes.get(0),
                        documentTypes
                );

        dialog.setTitle("Tipo documento");
        dialog.setHeaderText(
                "Seleziona il tipo di documento"
        );
        dialog.setContentText("Tipo:");

        String selected = dialog
                .showAndWait()
                .orElse(null);

        if (selected == null) {
            return null;
        }

        return switch (selected) {
            case "Referto medico" ->
                    DocumentType.MEDICAL_REPORT;

            case "Risultato di laboratorio" ->
                    DocumentType.LAB_RESULT;

            case "Prescrizione" ->
                    DocumentType.PRESCRIPTION;

            case "Certificato di vaccinazione" ->
                    DocumentType.VACCINATION_CERTIFICATE;

            default ->
                    DocumentType.OTHER;
        };
    }

    public String chiediRiferimentoDocumento() {
        TextInputDialog dialog =
                new TextInputDialog();

        dialog.setTitle("Riferimento documento");
        dialog.setHeaderText(
                "Inserisci il percorso o il riferimento "
                        + "del documento"
        );
        dialog.setContentText("Riferimento:");

        return dialog
                .showAndWait()
                .orElse(null);
    }

    public void setError(String message) {
        errorLabel.setText(message);
    }

    public void clearError() {
        errorLabel.setText("");
    }

    private String formatDocumentType(
            DocumentType type) {

        if (type == null) {
            return "Non specificato";
        }

        return switch (type) {
            case MEDICAL_REPORT ->
                    "Referto medico";

            case LAB_RESULT ->
                    "Risultato di laboratorio";

            case PRESCRIPTION ->
                    "Prescrizione";

            case VACCINATION_CERTIFICATE ->
                    "Certificato di vaccinazione";

            case OTHER ->
                    "Altro";
        };
    }
}