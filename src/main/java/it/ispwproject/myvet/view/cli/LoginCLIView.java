package it.ispwproject.myvet.view.cli;

public class LoginCLIView {

    public String[] chiediCredenziali() {
        CLIRenderer.intestazione(
                "MyVet – Accedi"
        );

        CLIRenderer.vuota();

        String email =
                CLIRenderer.chiediCampo("Email");

        String password =
                CLIRenderer.chiediCampo("Password");

        return new String[]{
                email,
                password
        };
    }

    public void mostraErroreInput() {
        CLIRenderer.errore(
                "Inserisci sia email che password."
        );
    }

    public void mostraErrore(String messaggio) {
        CLIRenderer.errore(messaggio);
    }

    public void mostraSuccesso(String nome, String saluto) {
        CLIRenderer.messaggio(
                CLIRenderer.OK
                        + " " + saluto + ", "
                        + nome
                        + "!"
        );
    }
}
