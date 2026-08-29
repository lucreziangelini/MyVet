package it.ispwproject.myvet.controller.cli;

import it.ispwproject.myvet.pattern.singleton.SessionManager;
import it.ispwproject.myvet.pattern.state.AbstractCLIState;
import it.ispwproject.myvet.pattern.state.CLIStateMachine;

import it.ispwproject.myvet.controller.applicativo.UserController;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.view.cli.EditProfileCLIView;

public class EditProfileCLI extends AbstractCLIState {

    private final UserController userController = new UserController();
    private final EditProfileCLIView view = new EditProfileCLIView();

    @Override
    public void entry(CLIStateMachine context) {
        view.mostraIntestazione();

        var user = SessionManager.getInstance().getLoggedUser();

        view.mostraDatiAttuali(
                user.getName(),
                user.getSurname(),
                user.getEmail()
        );
    }

    @Override
    public void action(CLIStateMachine context) {
        String scelta = "";

        while (!scelta.equals("0")) {
            view.mostraMenu();
            scelta = view.chiediScelta();

            switch (scelta) {
                case "1" -> editEmail();
                case "0" -> {
                    // Torna alla dashboard
                }
                default -> view.mostraErrore("Scelta non valida.");
            }
        }

        goBack(context);
    }

    private void editEmail() {
        String newEmail = view.chiediCampo("Nuova email");

        if (!view.chiediConferma(
                "Confermare il cambio email a " + newEmail + "?"
        )) {
            view.mostraMessaggio("Operazione annullata.");
            return;
        }

        try {
            userController.updateEmail(newEmail);
            view.mostraSuccesso("Email aggiornata con successo.");
        } catch (DAOException e) {
            view.mostraErrore(e.getMessage());
        }
    }
}