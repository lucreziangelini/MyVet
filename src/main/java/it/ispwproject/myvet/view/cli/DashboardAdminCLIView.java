package it.ispwproject.myvet.view.cli;

public class DashboardAdminCLIView {

    public void mostraBenvenuto(String nome, String saluto) {
        CLIRenderer.intestazioneBenvenuto(
                nome,
                "Amministratore",
                saluto
        );
    }

    public void mostraMenu() {
        CLIRenderer.vuota();
        CLIRenderer.voceMenu(1, "Statistiche e resoconti");
        CLIRenderer.voceMenuZero("Esci");
    }

    public String chiediScelta() {
        return CLIRenderer.chiediSceltaStringa("Scelta");
    }

    public void mostraMessaggio(String messaggio) {
        CLIRenderer.messaggio(messaggio);
    }
}
