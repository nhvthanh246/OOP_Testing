package com.ecosystem.controllers;

import com.ecosystem.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class SetupController {

    @FXML private TextField wolfInput;
    @FXML private TextField sheepInput;

    @FXML
    private void handleBack(ActionEvent event) {
        Main.setRoot("MainMenu", 1100, 750);
    }

    @FXML
    private void handleContinue(ActionEvent event) {
        try {
            int wolves = Integer.parseInt(wolfInput.getText());
            int sheep = Integer.parseInt(sheepInput.getText());

            if (wolves < 0 || wolves > 100 || sheep < 0 || sheep > 100) {
                showAlert("Please enter integers between 0 and 100.");
                return;
            }

            // Load simulation with custom parameters
            try {
                FXMLLoader loader = new FXMLLoader(Main.class.getResource("/fxml/Simulation.fxml"));
                Parent root = loader.load();
                SimulationController controller = loader.getController();
                
                // We add a new initCustomScenario to SimulationController
                controller.initCustomScenario(wolves, sheep);
                
                Stage stage = (Stage) wolfInput.getScene().getWindow();
                Scene scene = new Scene(root, 1100, 750);
                scene.getStylesheets().add(Main.class.getResource("/css/style.css").toExternalForm());
                stage.setScene(scene);
                
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (NumberFormatException e) {
            showAlert("Please enter valid integers.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Input");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
