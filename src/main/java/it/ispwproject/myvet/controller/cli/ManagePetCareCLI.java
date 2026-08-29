package it.ispwproject.myvet.controller.cli;

import it.ispwproject.myvet.bean.ActivityBean;
import it.ispwproject.myvet.bean.PetBean;
import it.ispwproject.myvet.bean.ProgressBean;
import it.ispwproject.myvet.controller.applicativo.ActivityController;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.pattern.state.AbstractCLIState;
import it.ispwproject.myvet.pattern.state.CLIStateMachine;
import it.ispwproject.myvet.view.cli.ManagePetCareCLIView;

import java.util.List;

public class ManagePetCareCLI extends AbstractCLIState {

    private final ActivityController activityController =
            new ActivityController();

    private final ManagePetCareCLIView view =
            new ManagePetCareCLIView();

    @Override
    public void entry(CLIStateMachine context) {
        view.mostraIntestazione();
    }

    @Override
    public void action(CLIStateMachine context) {
        try {
            List<PetBean> pets = activityController.getPets();
            view.mostraAnimali(pets);

            if (pets.isEmpty()) {
                goBack(context);
                return;
            }

            int choice = view.chiediScelta(
                    "Seleziona un animale",
                    0,
                    pets.size()
            );

            if (choice == 0) {
                goBack(context);
                return;
            }

            managePet(context, pets.get(choice - 1));

        } catch (DAOException e) {
            view.mostraErrore(e.getMessage());
            goBack(context);
        }
    }

    private void managePet(CLIStateMachine context, PetBean pet)
            throws DAOException {

        while (true) {
            ProgressBean progress =
                    activityController.getProgress(pet.getId());

            view.mostraSchedaAnimale(pet, progress);
            view.mostraMenuAnimale();

            int choice = view.chiediScelta("Scelta", 0, 3);

            switch (choice) {
                case 1 -> annotaProgressi(pet);
                case 2 -> assegnaAttivita(pet);
                case 3 -> visualizzaAttivita(pet);

                case 0 -> {
                    goBack(context);
                    return;
                }

                default ->
                        view.mostraMessaggio("❌ Scelta non valida.");
            }
        }
    }

    private void annotaProgressi(PetBean pet) throws DAOException {
        ProgressBean existing =
                activityController.getProgress(pet.getId());

        if (existing != null) {
            view.mostraMessaggio(
                    "Note attuali: " + existing.getNotes()
            );
            view.mostraMessaggio(
                    "Riscrivi il testo modificato:"
            );
        }

        String notes = view.chiediTesto("Note");

        if (notes.isBlank()) {
            view.mostraMessaggio("Note non valide.");
            return;
        }

        activityController.updateProgress(
                new ProgressBean(pet, notes, null)
        );

        view.mostraSuccesso("Progressi aggiornati.");
    }

    private void assegnaAttivita(PetBean pet) throws DAOException {
        String description =
                view.chiediTesto("Descrizione attività");

        if (description.isBlank()) {
            view.mostraMessaggio("Descrizione non valida.");
            return;
        }

        ActivityBean activity = new ActivityBean(
                0,
                pet,
                description,
                false,
                null
        );

        activityController.assignActivity(activity);
        view.mostraSuccesso("Attività assegnata.");
    }

    private void visualizzaAttivita(PetBean pet)
            throws DAOException {

        view.mostraAttivita(
                activityController.getActivities(pet.getId())
        );

        view.attesaInvio();
    }
}