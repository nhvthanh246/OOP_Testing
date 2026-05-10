package com.ecosystem.controllers;

import com.ecosystem.Main;
import com.ecosystem.models.*;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
import javafx.scene.chart.XYChart;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;

public class SimulationController implements EcosystemObserver {

    @FXML private Canvas canvas;
    @FXML private Button playPauseBtn;
    @FXML private Slider speedSlider;

    private Ecosystem ecosystem;
    private AnimationTimer timer;
    private boolean isPlaying = true;
    private long tickDelayNs = 100_000_000; // 100ms
    
    private XYChart.Series<Number, Number> wolfSeries = new XYChart.Series<>();
    private XYChart.Series<Number, Number> sheepSeries = new XYChart.Series<>();
    private int dayCount = 0;
    private int tickCounter = 0;
    
    private int currentPlants = 0;
    private int currentHerbivores = 0;
    private int currentCarnivores = 0;
    private int currentOmnivores = 0;
    
    private long totalPlants = 0;
    private long totalHerbivores = 0;
    private long totalCarnivores = 0;

    private String selectedSpecies = "--"; // For click spawning
    private boolean isIslandScenario = false;
    
    private final int CELL_SIZE = 20;
    private final int GRID_WIDTH = 45;
    private final int GRID_HEIGHT = 27;

    public void initialize() {
        wolfSeries.setName("Wolf");
        sheepSeries.setName("Sheep");

        ecosystem = new Ecosystem(GRID_WIDTH, GRID_HEIGHT);
        ecosystem.addObserver(this); 
        
        // Setup Speed Slider
        if (speedSlider != null) {
            // Note: Inverse relationship for UX (sliding right = faster)
            // So we set slider: Min 10 (fastest), Max 500 (slowest)
            // But visually we want right to be fast. 
            // Let's use 1 to 10 as a "Speed Multiplier" instead of raw ms.
            speedSlider.setMin(1);
            speedSlider.setMax(10);
            speedSlider.setValue(5); // Default speed
            
            speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                // Speed 1 -> 500ms (Slow)
                // Speed 10 -> 10ms (Fast)
                // Mapping: 500 - (speed-1)*(490/9)
                double speed = newVal.doubleValue();
                double ms = 500.0 - (speed - 1.0) * (490.0 / 9.0);
                tickDelayNs = (long) (ms * 1_000_000L);
            });
            // Initial setting
            tickDelayNs = (long) ((500.0 - (5.0 - 1.0) * (490.0 / 9.0)) * 1_000_000L);
        }

        // Setup Keyboard listener
        canvas.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case DIGIT1: selectedSpecies = isIslandScenario ? "Cat" : "Wolf"; break;
                case DIGIT2: selectedSpecies = isIslandScenario ? "Bird" : "Sheep"; break;
            }
            draw();
        });
        
        // Setup Mouse listener
        canvas.setOnMouseClicked(e -> {
            canvas.requestFocus(); // request focus so keys work
            int gridX = (int) (e.getX() / CELL_SIZE);
            int gridY = (int) (e.getY() / CELL_SIZE);
            if (!ecosystem.isValid(gridX, gridY)) return;
            
            if ("Wolf".equals(selectedSpecies) || "Cat".equals(selectedSpecies)) {
                ecosystem.addOrganism(OrganismFactory.createOrganism(Carnivore.class, gridX, gridY));
            } else if ("Sheep".equals(selectedSpecies) || "Bird".equals(selectedSpecies)) {
                ecosystem.addOrganism(OrganismFactory.createOrganism(Herbivore.class, gridX, gridY));
            }
            ecosystem.notifyObservers();
            draw();
        });

        timer = new AnimationTimer() {
            private long lastUpdate = 0;
            @Override
            public void handle(long now) {
                if (now - lastUpdate >= tickDelayNs) {
                    ecosystem.update();
                    draw();
                    lastUpdate = now;
                }
            }
        };
        timer.start();

        // Request focus initially
        Platform.runLater(() -> canvas.requestFocus());
    }
    public void initScenario(String scenario) {
        if ("Balanced".equals(scenario)) {
            isIslandScenario = false;
            wolfSeries.setName("Wolf");
            sheepSeries.setName("Sheep");
            ecosystem.setCarryingCapacity(250);
            ecosystem.setSpawnRate(0.1);
            initCustomScenario(5, 20); // 5 wolves, 20 sheep
        } else if ("Extinction".equals(scenario)) {
            isIslandScenario = true;
            wolfSeries.setName("Cat");
            sheepSeries.setName("Bird");
            ecosystem.setCarryingCapacity(100); // Fewer resources
            ecosystem.setSpawnRate(0.05); // Less plant growth
            initCustomScenario(3, 10); // 3 cats, 10 birds
        } else if ("Overpopulation".equals(scenario)) {
            isIslandScenario = false;
            wolfSeries.setName("Wolf");
            sheepSeries.setName("Sheep");
            ecosystem.setCarryingCapacity(250);
            ecosystem.setSpawnRate(0.1);
            initCustomScenario(0, 30); // 0 wolves, 30 sheep
        }
    }

    public void initCustomScenario(int wolves, int sheep) {
        ecosystem.clear();
        totalPlants = 0;
        totalHerbivores = 0;
        totalCarnivores = 0;
        tickCounter = 0;
        dayCount = 0;
        wolfSeries.getData().clear();
        sheepSeries.getData().clear();
        for (int i = 0; i < 50; i++) spawnRandom(Plant.class);
        for (int i = 0; i < sheep; i++) spawnRandom(Herbivore.class);
        for (int i = 0; i < wolves; i++) spawnRandom(Carnivore.class);
        ecosystem.notifyObservers();
    }

    private void spawnRandom(Class<? extends Organism> type) {
        int x = (int) (Math.random() * GRID_WIDTH);
        int y = (int) (Math.random() * GRID_HEIGHT);
        if (ecosystem.isEmpty(x, y)) {
            ecosystem.addOrganism(OrganismFactory.createOrganism(type, x, y));
        }
    }

    @Override
    public void onPopulationChanged(int plants, int herbivores, int carnivores, int omnivores) {
        Platform.runLater(() -> {
            this.currentPlants = plants;
            this.currentHerbivores = herbivores;
            this.currentCarnivores = carnivores;
            this.currentOmnivores = omnivores;
            
            tickCounter++;
            totalPlants += plants;
            totalHerbivores += herbivores;
            totalCarnivores += carnivores;

            if (tickCounter % 5 == 0) { // 5 ticks = 1 Day
                dayCount++;
                wolfSeries.getData().add(new XYChart.Data<>(dayCount, carnivores));
                sheepSeries.getData().add(new XYChart.Data<>(dayCount, herbivores));
            }
        });
    }

    private void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE); // modern white background
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        // Draw Grid Lines (ultra faint gray)
        gc.setStroke(Color.web("#f3f4f6"));
        gc.setLineWidth(1);
        for (int i = 0; i <= canvas.getWidth(); i += CELL_SIZE) {
            gc.strokeLine(i, 0, i, canvas.getHeight());
        }
        for (int j = 0; j <= canvas.getHeight(); j += CELL_SIZE) {
            gc.strokeLine(0, j, canvas.getWidth(), j);
        }

        // Draw Organisms with Emojis and Health Bars
        gc.setFont(javafx.scene.text.Font.font("Segoe UI Emoji", CELL_SIZE * 1.1));
        
        for (Organism org : ecosystem.getOrganisms()) {
            if (!org.isAlive()) continue;

            double x = org.getX() * CELL_SIZE;
            double y = org.getY() * CELL_SIZE;

            // Faux Drop Shadow for Emoji
            gc.setFill(Color.web("#000000", 0.15));
            gc.fillOval(x + 2, y + CELL_SIZE/2.0 + 2, CELL_SIZE * 0.8, CELL_SIZE * 0.4);

            String emoji = "";
            if (org instanceof Plant) emoji = "🌿";
            else if (org instanceof Herbivore) emoji = isIslandScenario ? "🐦" : "🐑";
            else if (org instanceof Carnivore) emoji = isIslandScenario ? "🐈" : "🐺";
            else if (org instanceof Omnivore) emoji = "🐻";

            gc.setFill(Color.BLACK);
            gc.fillText(emoji, x, y + CELL_SIZE - 2);

            // Draw Health Bar for Animals
            if (!(org instanceof Plant)) {
                double hpRatio = org.getEnergy() / org.getMaxEnergy();
                if (hpRatio < 0) hpRatio = 0;
                if (hpRatio > 1) hpRatio = 1;

                double barWidth = CELL_SIZE * 1.2;
                double barHeight = 6;
                double barX = x - CELL_SIZE * 0.1;
                double barY = y + CELL_SIZE + 4;

                // Draw Border
                gc.setStroke(Color.web("#000000", 0.8));
                gc.setLineWidth(1);
                gc.strokeRect(barX - 1, barY - 1, barWidth + 2, barHeight + 2);

                gc.setFill(Color.web("#ef4444")); // Modern Red
                gc.fillRect(barX, barY, barWidth, barHeight);
                gc.setFill(Color.web("#10b981")); // Modern Green
                gc.fillRect(barX, barY, barWidth * hpRatio, barHeight);
            }
        }
        
        // Draw Legend Panel
        gc.setFill(Color.web("#ffffff", 0.9));
        gc.fillRoundRect(canvas.getWidth() - 250, 10, 240, 140, 15, 15); // Top Right Legend
        gc.fillRoundRect(10, canvas.getHeight() - 70, 310, 60, 15, 15); // Bottom Left plate

        // Calculate Averages
        double avgP = tickCounter > 0 ? (double)totalPlants / tickCounter : 0;
        double avgH = tickCounter > 0 ? (double)totalHerbivores / tickCounter : 0;
        double avgC = tickCounter > 0 ? (double)totalCarnivores / tickCounter : 0;

        String pName = "🌿";
        String hName = isIslandScenario ? "🐦" : "🐑";
        String cName = isIslandScenario ? "🐈" : "🐺";

        gc.setFont(javafx.scene.text.Font.font("Segoe UI", javafx.scene.text.FontWeight.NORMAL, 18));
        double startY = 40;
        double gapY = 35;
        double startX = canvas.getWidth() - 230;

        // Line 1: Plant
        gc.setStroke(Color.web("#10b981")); // Emerald Green
        gc.setLineWidth(2);
        gc.strokeLine(startX, startY - 5, startX + 30, startY - 5);
        gc.setFill(Color.BLACK);
        gc.fillText(pName + " " + currentPlants + " (avg. " + String.format("%.1f", avgP) + ")", startX + 40, startY);

        // Line 2: Herbivore
        gc.setStroke(Color.web("#3b82f6")); // Blue
        gc.strokeLine(startX, startY + gapY - 5, startX + 30, startY + gapY - 5);
        gc.fillText(hName + " " + currentHerbivores + " (avg. " + String.format("%.1f", avgH) + ")", startX + 40, startY + gapY);

        // Line 3: Carnivore
        gc.setStroke(Color.web("#ef4444")); // Red
        gc.strokeLine(startX, startY + 2*gapY - 5, startX + 30, startY + 2*gapY - 5);
        gc.fillText(cName + " " + currentCarnivores + " (avg. " + String.format("%.1f", avgC) + ")", startX + 40, startY + 2*gapY);
        
        // Bottom Left Text
        gc.setFont(javafx.scene.text.Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 14));
        String leftText = "Press key to set the species to add\n" +
                          (isIslandScenario ? "Cat (1) | Bird (2)\n" : "Wolf (1) | Sheep (2)\n") +
                          "Current selected: " + selectedSpecies;
        gc.setTextAlign(javafx.scene.text.TextAlignment.LEFT);
        gc.fillText(leftText, 20, canvas.getHeight() - 50);
    }

    @FXML
    private void togglePlayPause(ActionEvent event) {
        if (isPlaying) {
            timer.stop();
            playPauseBtn.setText("Play");
        } else {
            timer.start();
            playPauseBtn.setText("Pause");
        }
        isPlaying = !isPlaying;
    }

    @FXML
    private void handleBack(ActionEvent event) {
        timer.stop();
        Main.setRoot("MainMenu", 1100, 750);
    }
    
    @FXML
    private void handleCheckGraph(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/fxml/Graph.fxml"));
            Parent graphRoot = loader.load();
            GraphController controller = loader.getController();
            
            controller.initData(wolfSeries, sheepSeries, canvas.getScene().getRoot());
            canvas.getScene().setRoot(graphRoot);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
