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
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class MedicalDocumentsGUIView extends PageGUIView {

    public final ComboBox<PetBean> petCombo =
            new ComboBox<>();

    public final Button uploadBtn =
            new Button("Carica documento");

    public final Label errorLabel =
            buildErrorLabel();

    public final TextField searchField =
            new TextField();

    private final VBox documentsContainer =
            new VBox(14);

    private final Label emptyStateLabel =
            new Label();

    private PetBean selectedPet;
    private List<MedicalDocumentBean> currentDocuments =
            List.of();

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public MedicalDocumentsGUIView() {
        configurePetCombo();

        uploadBtn.setPrefWidth(200);
        uploadBtn.setPrefHeight(42);
        uploadBtn.getStyleClass().add("button");

        documentsContainer.setAlignment(Pos.TOP_CENTER);
        documentsContainer.setMaxWidth(760);
        documentsContainer.setPadding(new Insets(22));
        documentsContainer.getStyleClass().add("documents-panel");
        documentsContainer.setVisible(false);
        documentsContainer.setManaged(false);

        emptyStateLabel.getStyleClass().add("empty-state-card");
        emptyStateLabel.setWrapText(true);
        emptyStateLabel.setMaxWidth(760);
        emptyStateLabel.setVisible(false);
        emptyStateLabel.setManaged(false);

        searchField.setPromptText("Cerca nei documenti...");
        searchField.setMaxWidth(720);
        searchField.getStyleClass().add("document-search");
        searchField.textProperty().addListener(
                (observable, oldValue, newValue) ->
                        renderDocuments(newValue)
        );
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
        actions.setMaxWidth(760);
        HBox.setHgrow(petCombo, Priority.ALWAYS);

        documentsContainer.getChildren().setAll(
                new Label(
                        "Seleziona un animale per continuare."
                )
        );

        content.getChildren().addAll(
                title,
                instruction,
                actions,
                searchField,
                emptyStateLabel,
                errorLabel,
                documentsContainer
        );

        searchField.setVisible(false);
        searchField.setManaged(false);

        root.setCenter(
                transparentScroll(content)
        );

        return root;
    }

    private void configurePetCombo() {
        petCombo.setPromptText("Seleziona un animale");
        petCombo.setPrefWidth(300);
        petCombo.setMaxWidth(Double.MAX_VALUE);

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
            PetBean pet,
            List<MedicalDocumentBean> documents) {

        selectedPet = pet;
        currentDocuments = documents == null
                ? List.of()
                : List.copyOf(documents);

        emptyStateLabel.setVisible(false);
        emptyStateLabel.setManaged(false);
        searchField.setVisible(true);
        searchField.setManaged(true);
        documentsContainer.setVisible(true);
        documentsContainer.setManaged(true);

        renderDocuments(searchField.getText());
    }

    public void setPets(
            List<PetBean> pets,
            boolean veterinarian) {

        List<PetBean> safePets = pets == null
                ? List.of()
                : pets;

        petCombo.getItems().setAll(safePets);

        boolean hasPets = !safePets.isEmpty();
        petCombo.setDisable(!hasPets);
        uploadBtn.setDisable(!hasPets);
        petCombo.setPromptText(
                hasPets
                        ? "Seleziona un animale"
                        : "Nessun animale disponibile"
        );

        selectedPet = null;
        currentDocuments = List.of();
        searchField.clear();
        searchField.setVisible(false);
        searchField.setManaged(false);
        documentsContainer.setVisible(false);
        documentsContainer.setManaged(false);

        emptyStateLabel.setText(
                veterinarian
                        ? "Non ci sono ancora animali associati ai tuoi appuntamenti."
                        : "Non hai ancora registrato animali. Aggiungine uno dalla sezione I miei animali."
        );
        emptyStateLabel.setVisible(!hasPets);
        emptyStateLabel.setManaged(!hasPets);
    }

    // Aggiorna l'elenco applicando l'eventuale ricerca inserita dall'utente
    private void renderDocuments(String query) {
        documentsContainer.getChildren().clear();

        if (selectedPet == null) {
            Label emptyLabel = new Label(
                    "Seleziona un animale per continuare."
            );
            emptyLabel.getStyleClass().add("info-text");
            documentsContainer.getChildren().add(emptyLabel);
            return;
        }

        Label title = new Label(
                "Documenti di " + selectedPet.getName()
        );
        title.getStyleClass().add("section-title");

        documentsContainer.getChildren().add(title);

        List<MedicalDocumentBean> filteredDocuments =
                currentDocuments.stream()
                        .filter(document -> matchesQuery(document, query))
                        .toList();

        if (filteredDocuments.isEmpty()) {
            Label emptyLabel = new Label(
                    query == null || query.isBlank()
                            ? "Nessun documento medico disponibile."
                            : "Nessun documento corrisponde alla ricerca."
            );
            emptyLabel.getStyleClass().add("info-text");

            documentsContainer
                    .getChildren()
                    .add(emptyLabel);

            return;
        }

        for (MedicalDocumentBean document : filteredDocuments) {
            documentsContainer
                    .getChildren()
                    .add(buildDocumentCard(document));
        }
    }

    private boolean matchesQuery(
            MedicalDocumentBean document,
            String query) {

        if (query == null || query.isBlank()) {
            return true;
        }

        String normalizedQuery = query
                .trim()
                .toLowerCase(Locale.ROOT);

        String veterinarianName = document.getVeterinarian() == null
                ? ""
                : document.getVeterinarian().getFullName();

        return containsIgnoreCase(document.getTitle(), normalizedQuery)
                || containsIgnoreCase(
                formatDocumentType(document.getType()),
                normalizedQuery
        )
                || containsIgnoreCase(veterinarianName, normalizedQuery);
    }

    private boolean containsIgnoreCase(
            String value,
            String normalizedQuery) {

        return value != null
                && value.toLowerCase(Locale.ROOT)
                .contains(normalizedQuery);
    }

    private VBox buildDocumentCard(
            MedicalDocumentBean document) {

        VBox card = new VBox(7);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setPadding(new Insets(13, 8, 13, 8));
        card.getStyleClass().add("document-card");

        Label title = new Label(
                "▧  " + document.getTitle()
        );
        title.getStyleClass().add("section-title");
        title.setWrapText(true);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label uploadedAt = new Label("");
        uploadedAt.getStyleClass().add("document-date");

        if (document.getUploadedAt() != null) {
            uploadedAt.setText(
                    document.getUploadedAt()
                            .format(DATE_FORMATTER)
            );
        }

        HBox header = new HBox(12, title, spacer, uploadedAt);
        header.setAlignment(Pos.CENTER_LEFT);

        Label type = new Label(
                formatDocumentType(
                        document.getType()
                )
        );
        type.getStyleClass().add("document-type-badge");

        card.getChildren().addAll(
                header,
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
