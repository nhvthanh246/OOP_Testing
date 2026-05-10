package com.ecosystem.controllers;

import com.ecosystem.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class HelpController {

    @FXML
    private void handleBack(ActionEvent event) {
        Main.setRoot("MainMenu", 800, 600);
    }
}
