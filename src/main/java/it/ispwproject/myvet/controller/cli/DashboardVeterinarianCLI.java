package it.ispwproject.myvet.controller.cli;

import it.ispwproject.myvet.pattern.state.AbstractCLIState;
import it.ispwproject.myvet.pattern.state.CLIStateMachine;

import it.ispwproject.myvet.dao.ConnectionFactory;
import it.ispwproject.myvet.pattern.singleton.SessionManager;
import it.ispwproject.myvet.view.cli.DashboardVeterinarianCLIView;

public class DashboardVeterinarianCLI extends AbstractCLIState {

    private final DashboardVeterinarianCLIView view =
            new DashboardVeterinarianCLIView();

    @Override
    public void entry(CLIStateMachine context) {
        String nome = SessionManager.getInstance().getLoggedUser().getName();
        view.mostraBenvenuto(nome);
    }

    @Override
    public void action(CLIStateMachine context) {
        view.mostraMenu();

        switch (view.chiediScelta()) {
            case "1" -> goNext(context, new SetAvailabilityCLI());
            case "2" -> goNext(context, new ViewSlotsCLI());
            case "3" -> goNext(context, new ManagePetCareCLI());
            case "4" -> goNext(context, new MedicalDocumentsCLI());
            case "5" -> goNext(context, new EditProfileCLI());

            case "0" -> {
                try {
                    ConnectionFactory.clearRole();
                    SessionManager.getInstance().clearSession();
                    view.mostraMessaggio("✓ Logout effettuato.");
                    context.reset(new InitialCLI());
                } catch (java.sql.SQLException ex) {
                    view.mostraMessaggio(
                            "❌ Errore: impossibile effettuare il logout in sicurezza. Riprova."
                    );
                    goNext(context, this);
                }
            }

            default -> {
                view.mostraMessaggio("❌ Scelta non valida.");
                goNext(context, this);
            }
        }
    }
}
