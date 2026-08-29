package it.ispwproject.myvet.controller.cli;

import it.ispwproject.myvet.bean.MedicalDocumentBean;
import it.ispwproject.myvet.bean.PetBean;
import it.ispwproject.myvet.controller.applicativo.ActivityController;
import it.ispwproject.myvet.controller.applicativo.BookingController;
import it.ispwproject.myvet.controller.applicativo.MedicalDocumentController;
import it.ispwproject.myvet.enumerator.DocumentType;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.pattern.singleton.SessionManager;
import it.ispwproject.myvet.pattern.state.AbstractCLIState;
import it.ispwproject.myvet.pattern.state.CLIStateMachine;
import it.ispwproject.myvet.view.cli.MedicalDocumentsCLIView;

import java.util.List;

public class MedicalDocumentsCLI extends AbstractCLIState {

    private final MedicalDocumentController documentController =
            new MedicalDocumentController();

    private final BookingController bookingController =
            new BookingController();

    private final ActivityController activityController =
            new ActivityController();

    private final MedicalDocumentsCLIView view =
            new MedicalDocumentsCLIView();

    @Override
    public void entry(CLIStateMachine context) {
        view.mostraIntestazione();
    }

    @Override
    public void action(CLIStateMachine context) {
        try {
            List<PetBean> pets;

            if (SessionManager.getInstance().isVeterinarian()) {
                pets = activityController.getPets();
            } else {
                pets = bookingController.getRegisteredPets();
            }

            if (pets.isEmpty()) {
                view.mostraMessaggio("Nessun animale disponibile.");
                goBack(context);
                return;
            }

            view.mostraAnimali(pets);

            int choice = view.chiediScelta(
                    "Seleziona un animale",
                    0,
                    pets.size()
            );

            if (choice == 0) {
                goBack(context);
                return;
            }

            PetBean pet = pets.get(choice - 1);
            manageDocuments(pet);

        } catch (DAOException e) {
            view.mostraErrore(e.getMessage());
        }

        goBack(context);
    }

    private void manageDocuments(PetBean pet) throws DAOException {
        boolean running = true;

        while (running) {
            List<MedicalDocumentBean> documents =
                    documentController.getDocuments(pet.getId());

            view.mostraDocumenti(pet, documents);

            if (SessionManager.getInstance().isVeterinarian()) {
                view.mostraMenuVeterinario();

                int choice = view.chiediScelta("Scelta", 0, 1);

                switch (choice) {
                    case 1 -> uploadDocument(pet);
                    case 0 -> running = false;
                    default ->
                            view.mostraErrore("Scelta non valida.");
                }

            } else {
                view.attesaInvio();
                running = false;
            }
        }
    }

    private void uploadDocument(PetBean pet) throws DAOException {
        String title = view.chiediCampo("Titolo del documento");
        DocumentType type = view.chiediTipoDocumento();
        String storageReference =
                view.chiediCampo("Percorso del documento");

        if (title.isBlank() || storageReference.isBlank()) {
            view.mostraErrore("Dati del documento non validi.");
            return;
        }

        MedicalDocumentBean document = new MedicalDocumentBean(
                0,
                pet,
                null,
                title,
                type,
                storageReference,
                null
        );

        documentController.uploadDocument(document);
        view.mostraSuccesso("Documento caricato con successo.");
    }
}