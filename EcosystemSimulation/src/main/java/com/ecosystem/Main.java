package com.ecosystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.ecosystem.controllers.SimulationController;

import java.io.IOException;

public class Main extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        stage.setTitle("Interactive Ecosystem Food Chain Simulation");
        setRoot("MainMenu", 800, 600);
        stage.show();
    }

    public static void setRoot(String fxml, int width, int height) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/fxml/" + fxml + ".fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, width, height);
            scene.getStylesheets().add(Main.class.getResource("/css/style.css").toExternalForm());
            primaryStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Could not load FXML: " + fxml);
        }
    }

    public static void loadSimulation(String scenario) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/fxml/Simulation.fxml"));
            Parent root = loader.load();

            SimulationController controller = loader.getController();
            controller.initScenario(scenario);

            Scene scene = new Scene(root, 1100, 750);
            scene.getStylesheets().add(Main.class.getResource("/css/style.css").toExternalForm());
            primaryStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
