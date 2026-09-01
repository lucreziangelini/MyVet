package it.ispwproject.myvet.view.cli;

import it.ispwproject.myvet.bean.StatisticsBean;

import java.util.Map;

@SuppressWarnings("java:S106") // System.out e' l'output previsto di questa boundary CLI.
public class StatisticsCLIView {

    public void mostraIntestazione() {
        CLIRenderer.intestazione(
                "MyVet – Statistiche e resoconti"
        );
    }

    public void mostraStatistiche(
            StatisticsBean statistics) {

        CLIRenderer.sezione(
                "Appuntamenti"
        );

        CLIRenderer.campo(
                "Totali",
                String.valueOf(
                        statistics.getTotalBookings()
                )
        );

        CLIRenderer.campo(
                "Annullati",
                String.valueOf(
                        statistics.getCancelledBookings()
                )
        );

        CLIRenderer.campo(
                "Tasso",
                String.format(
                        "%.1f%%",
                        statistics.getCancellationRate()
                )
        );

        CLIRenderer.sezione(
                "Top 3 veterinari"
        );

        if (statistics.getTopVeterinarians() == null
                || statistics.getTopVeterinarians().isEmpty()) {

            CLIRenderer.messaggio(
                    "Nessun dato disponibile."
            );

        } else {
            int position = 1;

            for (Map.Entry<String, Integer> entry :
                    statistics.getTopVeterinarians()
                            .entrySet()) {

                System.out.printf(
                        "  %d.  %-28s  %d appuntamenti%n",
                        position++,
                        entry.getKey(),
                        entry.getValue()
                );
            }
        }

        CLIRenderer.separatore();
    }

    public void mostraErrore(String messaggio) {
        CLIRenderer.errore(messaggio);
    }
}
