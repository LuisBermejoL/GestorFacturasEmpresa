package luis.gestorfacturasempresa;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Clase principal de la aplicación JavaFX "Gestor de Facturas Empresa".
 * Carga la interfaz principal definida en el archivo FXML y lanza la ventana.
 */
public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Carga el archivo FXML desde la carpeta de recursos
            FXMLLoader loader = new FXMLLoader(getClass().getResource("inicio.fxml"));

            // Crea la escena con el contenido del FXML
            Scene scene = new Scene(loader.load());

            // Configura la ventana principal
            primaryStage.setTitle("Gestor de Facturas - Empresa");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.show();

        } catch (IOException e) {
            System.err.println("Error al cargar la interfaz FXML:");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}