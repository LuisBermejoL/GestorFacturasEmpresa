package Controlador;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ControladorInterfaces {

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

    @FXML
    private VBox contenedorNuevo;  // Este es el VBox clickable

    // Se ejecuta automáticamente después de cargar el FXML
    @FXML
    private void initialize() {
        // Mostrar "Nuevo" por defecto al iniciar
        mostrarSeccionNuevo();

        // === AÑADIMOS LOS LISTENERS ===
        btnNuevo.setOnAction(event -> mostrarSeccionNuevo());
        btnAbrir.setOnAction(event -> mostrarSeccionAbrir());

        // Efecto hover
        agregarEfectosHover();

        // Llamar al formulario Nuevo
        contenedorNuevo.setOnMouseClicked(event -> cargarFormularioEmpresa());
    }

    // Método "Nueva Empresa"
    @FXML
    private void cargarInterfazEmpresa() {
        cargarFormularioEmpresa();
    }

    private void mostrarSeccionNuevo() {
        nuevoMenu.setVisible(true);
        nuevoMenu.setManaged(true);

        abrirMenu.setVisible(false);
        abrirMenu.setManaged(false);

        // Resaltar botón activo
        btnNuevo.setStyle("-fx-background-color: #a02c12; -fx-text-fill: white; -fx-font-weight: bold;");
        btnAbrir.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
    }

    private void mostrarSeccionAbrir() {
        abrirMenu.setVisible(true);
        abrirMenu.setManaged(true);

        nuevoMenu.setVisible(false);
        nuevoMenu.setManaged(false);

        // Resaltar botón activo
        btnAbrir.setStyle("-fx-background-color: #a02c12; -fx-text-fill: white; -fx-font-weight: bold;");
        btnNuevo.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
    }

    // Efecto visual al pasar el ratón
    private void agregarEfectosHover() {
        btnNuevo.setOnMouseEntered(e -> {
            if (!nuevoMenu.isVisible()) {
                btnNuevo.setStyle("-fx-background-color: #d43f1b; -fx-text-fill: white;");
            }
        });
        btnNuevo.setOnMouseExited(e -> {
            if (!nuevoMenu.isVisible()) {
                btnNuevo.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
            }
        });

        btnAbrir.setOnMouseEntered(e -> {
            if (!abrirMenu.isVisible()) {
                btnAbrir.setStyle("-fx-background-color: #d43f1b; -fx-text-fill: white;");
            }
        });
        btnAbrir.setOnMouseExited(e -> {
            if (!abrirMenu.isVisible()) {
                btnAbrir.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
            }
        });
    }

    @FXML
    private void cargarFormularioEmpresa() {
        try {
            // Cargar el FXML del formulario de empresa
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/facturaEmpresa.fxml"));
            Parent formulario = loader.load();

            // Limpiar el contenido anterior del área principal y cargar el nuevo
            VBox contenidoPrincipal = (VBox) nuevoMenu.getParent(); // o usa un fx:id específico
            contenidoPrincipal.getChildren().clear();
            contenidoPrincipal.getChildren().add(formulario);

            // Opcional: hacer que ocupe todo el espacio
            VBox.setVgrow(formulario, Priority.ALWAYS);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("ERROR: No se pudo cargar facturaEmpresa.fxml");
            System.out.println("Ruta intentada: " + getClass().getResource("/fxml/facturaEmpresa.fxml"));
        }
    }
}
