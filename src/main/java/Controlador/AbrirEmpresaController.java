package Controlador;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class AbrirEmpresaController {
    
    @FXML
    private ImageView retroceder; // Imagen punta de flecha para volver al menú anterior

    @FXML
    private void initialize() {
        // Añadimos el evento de clic a la flecha de retroceder
        retroceder.setOnMouseClicked(event -> volverAPrincipal());
    }

    @FXML
    private void volverAPrincipal() {
        try {
            // 1. Cargamos la ventana principal
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/luis/gestorfacturasempresa/inicio.fxml"));
            Parent root = loader.load();

            // 2. Creamos una NUEVA ventana (Stage)
            Stage principalStage = new Stage();
            principalStage.setTitle("Gestor de Facturas - Empresa");
            principalStage.setScene(new Scene(root, 620, 400));
            principalStage.setMinWidth(600);
            principalStage.setMinHeight(400);
            principalStage.centerOnScreen();

            // 3. Cerramos la ventana actual (Nueva Empresa)
            Stage abrirEmpresaStage = (Stage) retroceder.getScene().getWindow();
            abrirEmpresaStage.close();

            // 4. Mostramos la nueva ventana con efecto
            principalStage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
}
