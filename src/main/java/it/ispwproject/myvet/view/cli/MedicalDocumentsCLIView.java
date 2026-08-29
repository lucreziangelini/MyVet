package it.ispwproject.myvet.view.cli;

import it.ispwproject.myvet.bean.MedicalDocumentBean;
import it.ispwproject.myvet.bean.PetBean;
import it.ispwproject.myvet.enumerator.DocumentType;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class MedicalDocumentsCLIView {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public void mostraIntestazione() {
        CLIRenderer.intestazione(
                "MyVet – Documenti medici"
        );
    }

    public void mostraAnimali(List<PetBean> pets) {
        CLIRenderer.sezione("Seleziona un animale");

        for (int i = 0; i < pets.size(); i++) {
            PetBean pet = pets.get(i);

            String description =
                    pet.getName() + " – " + pet.getSpecies();

            if (pet.getBreed() != null
                    && !pet.getBreed().isBlank()) {
                description += " – " + pet.getBreed();
            }

            CLIRenderer.voceMenu(
                    i + 1,
                    description
            );
        }

        CLIRenderer.voceMenuZero("Indietro");
    }

    public void mostraDocumenti(
            PetBean pet,
            List<MedicalDocumentBean> documents) {

        CLIRenderer.sezione(
                "Documenti medici di " + pet.getName()
        );

        if (documents.isEmpty()) {
            CLIRenderer.messaggio(
                    "Nessun documento medico disponibile."
            );
            return;
        }

        for (MedicalDocumentBean document : documents) {
            CLIRenderer.vuota();
            System.out.println(
                    "  " + CLIRenderer.LINE_THIN
            );

            CLIRenderer.campo(
                    "Titolo",
                    document.getTitle()
            );

            CLIRenderer.campo(
                    "Tipo",
                    formatDocumentType(document.getType())
            );

            if (document.getVeterinarian() != null) {
                CLIRenderer.campo(
                        "Veterinario",
                        document.getVeterinarian().getFullName()
                );
            }

            if (document.getUploadedAt() != null) {
                CLIRenderer.campo(
                        "Caricato",
                        document.getUploadedAt()
                                .format(DATE_TIME_FORMATTER)
                );
            }

            CLIRenderer.campo(
                    "Documento",
                    document.getStorageReference()
            );
        }

        CLIRenderer.vuota();
        CLIRenderer.separatore();
    }

    public void mostraMenuVeterinario() {
        CLIRenderer.sezione("Azioni");

        CLIRenderer.voceMenu(
                1,
                "Carica un documento medico"
        );

        CLIRenderer.voceMenuZero(
                "Torna alla lista degli animali"
        );
    }

    public DocumentType chiediTipoDocumento() {
        DocumentType[] types = DocumentType.values();

        CLIRenderer.sezione("Tipo di documento");

        for (int i = 0; i < types.length; i++) {
            CLIRenderer.voceMenu(
                    i + 1,
                    formatDocumentType(types[i])
            );
        }

        int choice = CLIRenderer.chiediScelta(
                "Seleziona il tipo",
                1,
                types.length
        );

        return types[choice - 1];
    }

    public String chiediCampo(String label) {
        return CLIRenderer.chiediCampo(label);
    }

    public int chiediScelta(
            String prompt,
            int min,
            int max) {

        return CLIRenderer.chiediScelta(
                prompt,
                min,
                max
        );
    }

    public void mostraSuccesso(String message) {
        CLIRenderer.successo(message);
    }

    public void mostraErrore(String message) {
        CLIRenderer.errore(message);
    }

    public void mostraMessaggio(String message) {
        CLIRenderer.messaggio(message);
    }

    public void attesaInvio() {
        CLIRenderer.chiediCampo(
                "[ INVIO per tornare ]"
        );
    }

    private String formatDocumentType(DocumentType type) {
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