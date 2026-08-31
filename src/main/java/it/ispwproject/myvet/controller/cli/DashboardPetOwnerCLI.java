package it.ispwproject.myvet.controller.cli;

import it.ispwproject.myvet.pattern.state.AbstractCLIState;
import it.ispwproject.myvet.pattern.state.CLIStateMachine;

import it.ispwproject.myvet.dao.ConnectionFactory;
import it.ispwproject.myvet.pattern.singleton.SessionManager;
import it.ispwproject.myvet.view.cli.DashboardPetOwnerCLIView;

public class DashboardPetOwnerCLI
        extends AbstractCLIState {

    private final DashboardPetOwnerCLIView view =
            new DashboardPetOwnerCLIView();

    @Override
    public void entry(CLIStateMachine context) {

        var user = SessionManager
                .getInstance()
                .getLoggedUser();

        view.mostraBenvenuto(
                user.getName(),
                user.getWelcome()
        );
    }

    @Override
    public void action(CLIStateMachine context) {

        view.mostraMenu();

        switch (view.chiediScelta()) {

            case "1" ->
                    goNext(
                            context,
                            new BookAppointmentCLI()
                    );

            case "2" ->
                    goNext(
                            context,
                            new ViewAppointmentCLI()
                    );

            case "3" ->
                    goNext(
                            context,
                            new CancelAppointmentCLI()
                    );

            case "4" ->
                    goNext(
                            context,
                            new ManagePetsCLI()
                    );

            case "5" ->
                    goNext(
                            context,
                            new ViewActivitiesCLI()
                    );

            case "6" ->
                    goNext(
                            context,
                            new MedicalDocumentsCLI()
                    );

            case "7" ->
                    goNext(
                            context,
                            new EditProfileCLI()
                    );

            case "0" -> {
                try {
                    ConnectionFactory.clearRole();

                    SessionManager
                            .getInstance()
                            .clearSession();

                    view.mostraMessaggio(
                            "Disconnessione effettuata."
                    );

                    context.reset(new InitialCLI());

                } catch (java.sql.SQLException e) {

                    view.mostraMessaggio(
                            "Errore: impossibile effettuare "
                                    + "il logout in sicurezza. Riprova."
                    );

                    goNext(context, this);
                }
            }

            default -> {
                view.mostraMessaggio(
                        "Scelta non valida."
                );

                goNext(context, this);
            }
        }
    }
}
