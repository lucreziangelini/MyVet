package it.ispwproject.myvet.view.cli;

public class EditProfileCLIView {

    public void mostraIntestazione() {
        CLIRenderer.intestazione(
                "MyVet – Profilo"
        );
    }

    public void mostraMenu() {
        CLIRenderer.vuota();

        CLIRenderer.voceMenu(
                1,
                "Modifica email"
        );

        CLIRenderer.voceMenuZero("Indietro");
    }

    public void mostraDatiAttuali(
            String nome,
            String cognome,
            String email) {

        CLIRenderer.sezione(
                "Profilo attuale"
        );

        CLIRenderer.campo(
                "Nome",
                nome
        );

        CLIRenderer.campo(
                "Cognome",
                cognome
        );

        CLIRenderer.campo(
                "Email",
                email
        );
    }

    public String chiediScelta() {
        return CLIRenderer.chiediSceltaStringa(
                "Scelta"
        );
    }

    public String chiediCampo(String label) {
        return CLIRenderer.chiediCampo(label);
    }

    public boolean chiediConferma(String prompt) {
        return CLIRenderer.chiediConferma(prompt);
    }

    public void mostraSuccesso(String messaggio) {
        CLIRenderer.successo(messaggio);
    }

    public void mostraErrore(String messaggio) {
        CLIRenderer.errore(messaggio);
    }

    public void mostraMessaggio(String messaggio) {
        CLIRenderer.messaggio(messaggio);
    }
}