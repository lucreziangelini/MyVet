package it.ispwproject.myvet.controller.gui;

import it.ispwproject.myvet.controller.applicativo.LoginController;
import it.ispwproject.myvet.controller.applicativo.LoginController.LoginResult;
import it.ispwproject.myvet.exception.LoginException;
import it.ispwproject.myvet.view.gui.LoginGUIView;

import javafx.stage.Stage;

public class LoginGUI {

    private final Stage stage;

    private final LoginController loginController =
            new LoginController();

    private final LoginGUIView view =
            new LoginGUIView();

    public LoginGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        stage.setScene(
                GUIUtils.createScene(
                        view.buildRoot(
                                this::handleLogin,
                                MainGUI::showRegistration
                        )
                )
        );

        stage.show();
    }

    private void handleLogin() {
        String email =
                view.emailField.getText().trim();

        String password =
                view.passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            view.setError(
                    "Inserisci sia email che password."
            );
            return;
        }

        try {
            LoginResult result =
                    loginController.login(email, password);

            switch (result) {
                case SUCCESSO_PET_OWNER ->
                        MainGUI.showDashboardPetOwner();

                case SUCCESSO_VETERINARIAN ->
                        MainGUI.showDashboardVeterinarian();

                case SUCCESSO_ADMIN ->
                        MainGUI.showDashboardAdmin();
            }

        } catch (LoginException | IllegalStateException e) {
            view.setError(e.getMessage());
        }
    }
}