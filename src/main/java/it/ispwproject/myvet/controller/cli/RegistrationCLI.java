package it.ispwproject.myvet.controller.cli;

import it.ispwproject.myvet.bean.RegistrationBean;
import it.ispwproject.myvet.controller.applicativo.RegistrationController;
import it.ispwproject.myvet.enumerator.Role;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.exception.RegistrationException;
import it.ispwproject.myvet.pattern.state.AbstractCLIState;
import it.ispwproject.myvet.pattern.state.CLIStateMachine;
import it.ispwproject.myvet.view.cli.RegistrationCLIView;

public class RegistrationCLI extends AbstractCLIState {

    private final RegistrationController registrationController =
            new RegistrationController();

    private final RegistrationCLIView view =
            new RegistrationCLIView();

    @Override
    public void entry(CLIStateMachine context) {
        view.mostraIntestazione();
    }

    @Override
    public void action(CLIStateMachine context) {
        try {
            RegistrationBean bean = new RegistrationBean();

            bean.setName(view.chiediCampo("Nome"));
            bean.setSurname(view.chiediCampo("Cognome"));
            bean.setEmail(view.chiediCampo("Email"));
            bean.setPassword(view.chiediPassword("Password"));
            bean.setConfirmPassword(
                    view.chiediPassword("Conferma password")
            );

            Role role = view.chiediRuolo();
            bean.setRole(role);

            if (role == Role.VETERINARIAN) {
                bean.setBio(
                        view.chiediCampo("Bio (breve descrizione)")
                );

                bean.setSpecialization(
                        view.chiediCampo("Specializzazione")
                );
            }

            registrationController.register(bean);
            view.mostraSuccesso();

            goNext(context, new LoginCLI());

        } catch (RegistrationException e) {
            view.mostraErrore(e.getMessage());
            goNext(context, this);

        } catch (DAOException e) {
            view.mostraErrore(
                    "Errore di sistema: " + e.getMessage()
            );
            goNext(context, new LoginCLI());
        }
    }
}