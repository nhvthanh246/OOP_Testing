package com.ecosystem.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

public class GraphController {

    @FXML private LineChart<Number, Number> populationChart;
    
    private Parent previousRoot;

    public void initData(XYChart.Series<Number, Number> wolfSeries, XYChart.Series<Number, Number> sheepSeries, Parent previousRoot) {
        this.previousRoot = previousRoot;
        
        // Add series to chart
        populationChart.getData().add(wolfSeries);
        populationChart.getData().add(sheepSeries);
    }

    @FXML
    private void handleBack(ActionEvent event) {
        // Return to previous scene without destroying state
        populationChart.getScene().setRoot(previousRoot);
    }
}
