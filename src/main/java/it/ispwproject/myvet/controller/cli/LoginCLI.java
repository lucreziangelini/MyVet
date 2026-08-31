package it.ispwproject.myvet.controller.cli;

import it.ispwproject.myvet.pattern.state.AbstractCLIState;
import it.ispwproject.myvet.pattern.state.CLIStateMachine;

import it.ispwproject.myvet.controller.applicativo.LoginController;
import it.ispwproject.myvet.controller.applicativo.LoginController.LoginResult;
import it.ispwproject.myvet.exception.LoginException;
import it.ispwproject.myvet.pattern.singleton.SessionManager;
import it.ispwproject.myvet.view.cli.LoginCLIView;

public class LoginCLI extends AbstractCLIState {

    private final LoginController loginController = new LoginController();
    private final LoginCLIView view = new LoginCLIView();

    @Override
    public void action(CLIStateMachine context) {
        String[] credenziali = view.chiediCredenziali();
        String email = credenziali[0];
        String password = credenziali[1];

        if (email.isEmpty() || password.isEmpty()) {
            view.mostraErroreInput();
            goNext(context, this);
            return;
        }

        try {
            LoginResult result = loginController.login(email, password);

            var user = SessionManager.getInstance()
                    .getLoggedUser();

            view.mostraSuccesso(
                    user.getName(),
                    user.getWelcome()
            );

            switch (result) {
                case SUCCESSO_PET_OWNER ->
                        goNext(context, new DashboardPetOwnerCLI());

                case SUCCESSO_VETERINARIAN ->
                        goNext(context, new DashboardVeterinarianCLI());

                case SUCCESSO_ADMIN ->
                        goNext(context, new DashboardAdminCLI());
            }

        } catch (LoginException | IllegalStateException e) {
            view.mostraErrore(e.getMessage());
            goNext(context, this);
        }
    }
}
