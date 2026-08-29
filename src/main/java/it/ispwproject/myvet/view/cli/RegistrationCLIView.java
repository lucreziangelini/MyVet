package it.ispwproject.myvet.view.cli;

import it.ispwproject.myvet.enumerator.Role;

public class RegistrationCLIView {

    public void mostraIntestazione() {
        CLIRenderer.intestazione(
                "MyVet – Registrazione"
        );
    }

    public String chiediCampo(String label) {
        return CLIRenderer.chiediCampo(label);
    }

    public String chiediPassword(String label) {
        // Nella CLI eseguita da IntelliJ la password resta visibile.
        return CLIRenderer.chiediCampo(label);
    }

    public Role chiediRuolo() {
        while (true) {
            CLIRenderer.sezione("Ruolo");

            CLIRenderer.voceMenu(
                    1,
                    "Pet Owner"
            );

            CLIRenderer.voceMenu(
                    2,
                    "Veterinario"
            );

            String input =
                    CLIRenderer.chiediSceltaStringa(
                            "Scelta [1-2]"
                    );

            if ("1".equals(input)) {
                return Role.PET_OWNER;
            }

            if ("2".equals(input)) {
                return Role.VETERINARIAN;
            }

            CLIRenderer.errore(
                    "Scelta non valida."
            );
        }
    }

    public void mostraSuccesso() {
        CLIRenderer.vuota();

        CLIRenderer.successo(
                "Registrazione completata! "
                        + "Ora puoi effettuare il login."
        );
    }

    public void mostraErrore(String messaggio) {
        CLIRenderer.errore(messaggio);
    }
}