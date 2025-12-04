package Controlador;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class InicioController {

    // Paneles de contenido
    @FXML
    private AnchorPane nuevoMenu;
    @FXML
    private AnchorPane abrirMenu;

    // Botones del menú lateral
    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnAbrir;

    // Contenedores clicables
    @FXML
    private VBox contenedorNuevo;
    @FXML
    private VBox contenedorAbrir;

    // Se ejecuta automáticamente después de cargar el FXML
    @FXML
    private void initialize() {
        // Mostrar "Nuevo" por defecto al iniciar
        mostrarSeccion(nuevoMenu, btnNuevo, abrirMenu, btnAbrir);

        // Listeners de los botones
        btnNuevo.setOnAction(e -> mostrarSeccion(nuevoMenu, btnNuevo, abrirMenu, btnAbrir));
        btnAbrir.setOnAction(e -> mostrarSeccion(abrirMenu, btnAbrir, nuevoMenu, btnNuevo));

        // Efectos hover
        agregarEfectosHover();

        // Click en los contenedores
        contenedorNuevo.setOnMouseClicked(e -> {
            try {
                abrirVentana("/luis/gestorfacturasempresa/nuevaEmpresa.fxml", "Nueva Empresa", 450, 550, btnNuevo);
            } catch (IOException ex) {
                Logger.getLogger(InicioController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        contenedorAbrir.setOnMouseClicked(e -> {
            try {
                abrirVentana("/luis/gestorfacturasempresa/abrirListaEmpresas.fxml", "Lista de Empresas", 650, 450, btnAbrir);
            } catch (IOException ex) {
                Logger.getLogger(InicioController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    // Método genérico para mostrar secciones
    private void mostrarSeccion(AnchorPane mostrar, Button botonActivo, AnchorPane ocultar, Button botonInactivo) {
        mostrar.setVisible(true);
        mostrar.setManaged(true);
        ocultar.setVisible(false);
        ocultar.setManaged(false);

        // Animación suave
        animarTransicion(mostrar);

        // Estilos de botones
        botonActivo.setStyle("-fx-background-color: #6F9E11; -fx-text-fill: white; -fx-font-weight: bold;");
        botonInactivo.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
    }

    // Animación de transición
    private void animarTransicion(AnchorPane panel) {
        FadeTransition ft = new FadeTransition(Duration.millis(300), panel);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
    }

    // Efecto visual al pasar el ratón
    private void agregarEfectosHover() {
        btnNuevo.setOnMouseEntered(e -> {
            if (!nuevoMenu.isVisible()) {
                btnNuevo.setStyle("-fx-background-color: #96D41B; -fx-text-fill: white;");
            }
        });
        btnNuevo.setOnMouseExited(e -> {
            if (!nuevoMenu.isVisible()) {
                btnNuevo.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
            }
        });

        btnAbrir.setOnMouseEntered(e -> {
            if (!abrirMenu.isVisible()) {
                btnAbrir.setStyle("-fx-background-color: #96D41B; -fx-text-fill: white;");
            }
        });
        btnAbrir.setOnMouseExited(e -> {
            if (!abrirMenu.isVisible()) {
                btnAbrir.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
            }
        });
    }

    // Método genérico para abrir ventanas
    private void abrirVentana(String fxmlPath, String titulo, int width, int height, Button origen) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setTitle(titulo);
        stage.setScene(new Scene(root, width, height));
        stage.setMinWidth(width);
        stage.setMinHeight(height);
        stage.centerOnScreen();

        // Si quieres cerrar la ventana principal al abrir otra, deja estas líneas:
        Stage principalStage = (Stage) origen.getScene().getWindow();
        principalStage.close();

        stage.show();
    }
}
