package it.ispwproject.myvet.controller.cli;

import it.ispwproject.myvet.bean.StatisticsBean;
import it.ispwproject.myvet.controller.applicativo.StatisticsController;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.pattern.state.AbstractCLIState;
import it.ispwproject.myvet.pattern.state.CLIStateMachine;
import it.ispwproject.myvet.view.cli.StatisticsCLIView;

public class StatisticsCLI extends AbstractCLIState {

    private final StatisticsController controller =
            new StatisticsController();

    private final StatisticsCLIView view =
            new StatisticsCLIView();

    @Override
    public void entry(CLIStateMachine context) {
        view.mostraIntestazione();
    }

    @Override
    public void action(CLIStateMachine context) {
        try {
            StatisticsBean statistics =
                    controller.getStatistics();

            view.mostraStatistiche(statistics);

        } catch (DAOException e) {
            view.mostraErrore(e.getMessage());
        }

        goBack(context);
    }
}