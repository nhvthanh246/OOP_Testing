package com.ecosystem.controllers;

import com.ecosystem.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;

public class MainMenuController {

    @FXML
    public void handleBalancedScenario(ActionEvent event) {
        Main.loadSimulation("Balanced");
    }

    @FXML
    public void handleOverpopulationScenario(ActionEvent event) {
        // Now redirects to Setup screen instead of jumping straight in
        Main.setRoot("Setup", 1100, 750);
    }

    @FXML
    public void handleExtinctionScenario(ActionEvent event) {
        Main.loadSimulation("Extinction");
    }

    @FXML
    public void handleHelp(ActionEvent event) {
        Main.setRoot("Help", 800, 600);
    }

    @FXML
    public void handleQuit(ActionEvent event) {
        System.exit(0);
    }
}
