module luis.gestorfacturasempresa {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens luis.gestorfacturasempresa to javafx.fxml;
    opens Modelo to javafx.fxml;
    opens Controlador to javafx.fxml;
    opens DAO to javafx.fxml;
    
    exports luis.gestorfacturasempresa;
    exports Modelo;
    exports Controlador;
    exports DAO;
}