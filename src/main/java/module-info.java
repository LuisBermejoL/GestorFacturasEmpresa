module luis.gestorfacturasempresa {
    requires javafx.controls;
    requires javafx.fxml;

    opens luis.gestorfacturasempresa to javafx.fxml;
    exports luis.gestorfacturasempresa;
}
