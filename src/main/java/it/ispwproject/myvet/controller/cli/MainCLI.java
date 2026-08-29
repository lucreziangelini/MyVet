package it.ispwproject.myvet.controller.cli;

import it.ispwproject.myvet.pattern.state.CLIStateMachine;
import it.ispwproject.myvet.pattern.state.CLIStateMachineImpl;

public class MainCLI {

    private MainCLI() {
        // Utility class
    }

    public static void start() {
        CLIStateMachine machine = new CLIStateMachineImpl();
        machine.start();
    }
}