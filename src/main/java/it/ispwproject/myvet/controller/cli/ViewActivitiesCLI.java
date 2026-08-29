package it.ispwproject.myvet.controller.cli;

import it.ispwproject.myvet.bean.ActivityBean;
import it.ispwproject.myvet.controller.applicativo.ActivityController;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.pattern.state.AbstractCLIState;
import it.ispwproject.myvet.pattern.state.CLIStateMachine;
import it.ispwproject.myvet.view.cli.ViewActivitiesCLIView;

import java.util.List;

public class ViewActivitiesCLI extends AbstractCLIState {

    private final ActivityController activityController =
            new ActivityController();

    private final ViewActivitiesCLIView view =
            new ViewActivitiesCLIView();

    @Override
    public void entry(CLIStateMachine context) {
        view.mostraIntestazione();
    }

    @Override
    public void action(CLIStateMachine context) {
        try {
            List<ActivityBean> activities =
                    activityController.getMyActivities();

            view.mostraAttivita(activities);

            List<ActivityBean> pending = activities.stream()
                    .filter(activity -> !activity.isCompleted())
                    .toList();

            if (pending.isEmpty()) {
                view.mostraMessaggio(
                        "Tutte le attività sono state completate!"
                );
                goBack(context);
                return;
            }

            if (!view.chiediConferma(
                    "Vuoi segnare un'attività come completata?"
            )) {
                goBack(context);
                return;
            }

            view.mostraPendingPerSelezione(pending);

            int choice = view.chiediScelta(
                    "Seleziona un'attività",
                    0,
                    pending.size()
            );

            if (choice == 0) {
                goBack(context);
                return;
            }

            activityController.markActivityCompleted(
                    pending.get(choice - 1).getId()
            );

            view.mostraSuccesso(
                    "Attività segnata come completata."
            );

        } catch (DAOException e) {
            view.mostraErrore(e.getMessage());
        }

        goBack(context);
    }
}