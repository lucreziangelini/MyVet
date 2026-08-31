package it.ispwproject.myvet.controller.cli;

import it.ispwproject.myvet.pattern.state.AbstractCLIState;
import it.ispwproject.myvet.pattern.state.CLIStateMachine;

import it.ispwproject.myvet.dao.ConnectionFactory;
import it.ispwproject.myvet.pattern.singleton.SessionManager;
import it.ispwproject.myvet.view.cli.DashboardAdminCLIView;

public class DashboardAdminCLI
        extends AbstractCLIState {

    private final DashboardAdminCLIView view = new DashboardAdminCLIView();

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
                            new StatisticsCLI()
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
                        "Scelta non valida.");
                goNext(context, this);
            }
        }
    }
}
