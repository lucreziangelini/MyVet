package it.ispwproject.myvet.controller.cli;

import it.ispwproject.myvet.bean.TimeSlotBean;
import it.ispwproject.myvet.controller.applicativo.AvailabilityController;
import it.ispwproject.myvet.exception.AvailabilityException;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.pattern.state.AbstractCLIState;
import it.ispwproject.myvet.pattern.state.CLIStateMachine;
import it.ispwproject.myvet.view.cli.SetAvailabilityCLIView;

import java.time.LocalDate;
import java.time.LocalTime;

public class SetAvailabilityCLI extends AbstractCLIState {

    private final AvailabilityController availabilityController =
            new AvailabilityController();

    private final SetAvailabilityCLIView view =
            new SetAvailabilityCLIView();

    @Override
    public void entry(CLIStateMachine context) {
        view.mostraIntestazione();
    }

    @Override
    public void action(CLIStateMachine context) {
        try {
            LocalDate date = view.chiediData();
            LocalTime startTime = view.chiediOra("Ora inizio");
            LocalTime endTime = view.chiediOra("Ora fine");

            TimeSlotBean slotBean = new TimeSlotBean(
                    0,
                    date,
                    startTime,
                    endTime,
                    true
            );

            view.mostraMessaggio("  Data   : " + date);
            view.mostraMessaggio(
                    "  Orario : " + startTime + " – " + endTime
            );

            if (!view.chiediConferma("Confermare lo slot?")) {
                view.mostraMessaggio("Operazione annullata.");
                goBack(context);
                return;
            }

            availabilityController.addSlot(slotBean);
            view.mostraSuccesso();

        } catch (DAOException | AvailabilityException e) {
            view.mostraErrore(e.getMessage());
        }

        goBack(context);
    }
}