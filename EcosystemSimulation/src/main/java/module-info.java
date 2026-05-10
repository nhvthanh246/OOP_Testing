module ecosystem.simulation {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;

    opens com.ecosystem to javafx.fxml;
    opens com.ecosystem.controllers to javafx.fxml;
    exports com.ecosystem;
    exports com.ecosystem.controllers;
    exports com.ecosystem.models;
}
