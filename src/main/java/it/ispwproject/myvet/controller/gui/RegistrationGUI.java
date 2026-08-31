package it.ispwproject.myvet.controller.gui;

import it.ispwproject.myvet.bean.RegistrationBean;
import it.ispwproject.myvet.controller.applicativo.RegistrationController;
import it.ispwproject.myvet.enumerator.Gender;
import it.ispwproject.myvet.enumerator.Role;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.exception.RegistrationException;
import it.ispwproject.myvet.view.gui.RegistrationGUIView;

import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class RegistrationGUI {

    private final Stage stage;

    private final RegistrationController registrationController =
            new RegistrationController();

    private final RegistrationGUIView view =
            new RegistrationGUIView();

    public RegistrationGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        view.registerBtn.setOnAction(
                event -> handleRegistration()
        );

        stage.setScene(
                GUIUtils.createScene(
                        view.buildRoot(MainGUI::showLogin)
                )
        );

        stage.show();
    }

    private void handleRegistration() {
        RegistrationBean bean = new RegistrationBean();

        bean.setName(
                view.nameField.getText().trim()
        );

        bean.setSurname(
                view.surnameField.getText().trim()
        );

        bean.setEmail(
                view.emailField.getText().trim()
        );

        bean.setPassword(
                view.passwordField.getText().trim()
        );

        bean.setConfirmPassword(
                view.confirmPasswordField.getText().trim()
        );

        bean.setRole(
                view.veterinarianRadio.isSelected()
                        ? Role.VETERINARIAN
                        : Role.PET_OWNER
        );

        Gender selectedGender = null;
        if (view.femaleRadio.isSelected()) {
            selectedGender = Gender.FEMALE;
        } else if (view.maleRadio.isSelected()) {
            selectedGender = Gender.MALE;
        }
        bean.setGender(selectedGender);

        if (view.veterinarianRadio.isSelected()) {
            bean.setBio(
                    view.bioField.getText().trim()
            );

            bean.setSpecialization(
                    view.specializationField.getText().trim()
            );
        }

        try {
            registrationController.register(bean);
            showSuccess();

        } catch (RegistrationException e) {
            view.setError(e.getMessage());

        } catch (DAOException e) {
            view.setError(
                    "Errore di sistema: " + e.getMessage()
            );
        }
    }

    private void showSuccess() {
        Alert alert =
                new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Registrazione completata");
        alert.setHeaderText(null);

        alert.setContentText(
                "Registrazione completata! "
                        + "Ora puoi effettuare l'accesso."
        );

        alert.showAndWait();
        MainGUI.showLogin();
    }
}
