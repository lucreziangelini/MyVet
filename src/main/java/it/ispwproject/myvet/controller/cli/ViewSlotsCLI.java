package it.ispwproject.myvet.controller.cli;

import it.ispwproject.myvet.bean.TimeSlotBean;
import it.ispwproject.myvet.controller.applicativo.AvailabilityController;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.pattern.state.AbstractCLIState;
import it.ispwproject.myvet.pattern.state.CLIStateMachine;
import it.ispwproject.myvet.view.cli.ViewSlotsCLIView;

import java.util.List;
import java.util.Map;

public class ViewSlotsCLI extends AbstractCLIState {

    private final AvailabilityController availabilityController =
            new AvailabilityController();

    private final ViewSlotsCLIView view =
            new ViewSlotsCLIView();

    @Override
    public void entry(CLIStateMachine context) {
        view.mostraIntestazione();
    }

    @Override
    public void action(CLIStateMachine context) {
        try {
            Map<Integer, String> petBySlot =
                    availabilityController.getPetBySlot();

            List<TimeSlotBean> futuri =
                    availabilityController.getSlots();

            List<TimeSlotBean> passati =
                    availabilityController.getPastSlots();

            List<TimeSlotBean> disponibili = futuri.stream()
                    .filter(TimeSlotBean::isAvailable)
                    .toList();

            List<TimeSlotBean> prenotati = futuri.stream()
                    .filter(slot -> !slot.isAvailable())
                    .toList();

            boolean running = true;

            while (running) {
                view.mostraTab(
                        disponibili.size(),
                        prenotati.size(),
                        passati.size()
                );

                int scelta =
                        view.chiediScelta("Scelta", 0, 4);

                switch (scelta) {
                    case 1 ->
                            view.mostraDisponibili(disponibili);

                    case 2 ->
                            view.mostraPrenotati(
                                    prenotati,
                                    petBySlot
                            );

                    case 3 ->
                            view.mostraPassati(
                                    passati,
                                    petBySlot
                            );

                    case 4 -> running =
                            !deleteAvailableSlot(disponibili);

                    case 0 -> running = false;

                    default ->
                            view.mostraErrore("Scelta non valida.");
                }
            }

        } catch (DAOException e) {
            view.mostraErrore(e.getMessage());
        }

        goBack(context);
    }

    private boolean deleteAvailableSlot(
            List<TimeSlotBean> availableSlots) throws DAOException {

        if (availableSlots.isEmpty()) {
            view.mostraMessaggio(
                    "Nessuna fascia oraria disponibile da eliminare."
            );
            return false;
        }

        view.mostraSlotDisponibili(availableSlots);

        int choice = view.chiediScelta(
                "Seleziona la fascia oraria da eliminare",
                0,
                availableSlots.size()
        );

        if (choice == 0) {
            return false;
        }

        boolean confirmed = view.chiediConferma(
                "Sei sicuro di voler eliminare questa fascia oraria?"
        );

        if (!confirmed) {
            view.mostraMessaggio("Operazione annullata.");
            return false;
        }

        availabilityController.deleteSlot(
                availableSlots.get(choice - 1).getId()
        );
        view.mostraSuccessoEliminazione();
        return true;
    }
}
