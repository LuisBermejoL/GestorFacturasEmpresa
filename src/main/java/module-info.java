module luis.gestorfacturasempresa {
    requires javafx.controls;
    requires javafx.fxml;

    opens luis.gestorfacturasempresa.Controlador to javafx.fxml;     // para el fx:controller
    opens luis.gestorfacturasempresa to javafx.fxml;
    exports luis.gestorfacturasempresa;
}
