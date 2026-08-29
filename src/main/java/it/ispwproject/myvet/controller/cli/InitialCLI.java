package it.ispwproject.myvet.controller.cli;

import it.ispwproject.myvet.pattern.state.AbstractCLIState;
import it.ispwproject.myvet.pattern.state.CLIStateMachine;
import it.ispwproject.myvet.view.cli.InitialCLIView;

/**
 * Stato iniziale della CLIStateMachine.
 * Primo ConcreteState e punto di ingresso della CLI.
 */
public class InitialCLI extends AbstractCLIState {

    private final InitialCLIView view = new InitialCLIView();

    @Override
    public void entry(CLIStateMachine context) {
        view.mostraBenvenuto();
    }

    @Override
    public void action(CLIStateMachine context) {
        view.mostraMenu();

        switch (view.chiediScelta()) {
            case "1" -> goNext(context, new LoginCLI());
            case "2" -> goNext(context, new RegistrationCLI());
            case "0" -> context.setState(null);

            default -> {
                view.mostraErrore("Scelta non valida.");
                goNext(context, this);
            }
        }
    }
}