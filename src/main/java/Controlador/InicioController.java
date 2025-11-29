package Controlador;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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

    // VBox clickables
    @FXML
    private VBox contenedorNuevo;

    @FXML
    private VBox contenedorAbrir;

    // Se ejecuta automáticamente después de cargar el FXML
    @FXML
    private void initialize() throws IOException {
        // Mostrar "Nuevo" por defecto al iniciar
        mostrarSeccionNuevo();

        // === AÑADIMOS LOS LISTENERS ===
        btnNuevo.setOnAction(event -> mostrarSeccionNuevo());
        btnAbrir.setOnAction(event -> mostrarSeccionAbrir());

        // Efecto hover
        agregarEfectosHover();

        // Llamar a Nueva Empresa (Formulario)
        contenedorNuevo.setOnMouseClicked(event -> {
            try {
                nuevaEmpresa();
            } catch (IOException ex) {
                Logger.getLogger(InicioController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        // Llamar a Abrir Empresa (Lista de Empresas)
        contenedorAbrir.setOnMouseClicked(event -> {
            try {
                abrirEmpresa();
            } catch (IOException ex) {
                Logger.getLogger(InicioController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

    }

    private void mostrarSeccionNuevo() {
        nuevoMenu.setVisible(true);
        nuevoMenu.setManaged(true);

        abrirMenu.setVisible(false);
        abrirMenu.setManaged(false);

        // Resaltar botón activo
        btnNuevo.setStyle("-fx-background-color: #6F9E11; -fx-text-fill: white; -fx-font-weight: bold;");
        btnAbrir.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
    }

    private void mostrarSeccionAbrir() {
        abrirMenu.setVisible(true);
        abrirMenu.setManaged(true);

        nuevoMenu.setVisible(false);
        nuevoMenu.setManaged(false);

        // Resaltar botón activo
        btnAbrir.setStyle("-fx-background-color: #6F9E11; -fx-text-fill: white; -fx-font-weight: bold;");
        btnNuevo.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
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

    @FXML
    private void nuevaEmpresa() throws IOException {
        // 1. Cargar FXML de nueva empresa
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/luis/gestorfacturasempresa/nuevaEmpresa.fxml"));
        Parent root = loader.load();

        // 2. Crear nueva ventana
        Stage nuevoStage = new Stage();
        nuevoStage.setTitle("Nueva Empresa");
        nuevoStage.setScene(new Scene(root, 465, 510));
        nuevoStage.setMinWidth(465);
        nuevoStage.setMinHeight(510);
        nuevoStage.centerOnScreen();

        // 3. Cerrar la ventana principal (facturaPrincipal)
        Stage principalStage = (Stage) btnNuevo.getScene().getWindow();
        principalStage.close();

        // 4. Abrir ventana Nueva Empresa
        nuevoStage.show();
    }

    @FXML
    private void abrirEmpresa() throws IOException {
        // 1. Cargar FXML de lista de empresas
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/luis/gestorfacturasempresa/abrirListaEmpresas.fxml"));
        Parent root = loader.load();

        // 2. Crear nueva ventana
        Stage abrirStage = new Stage();
        abrirStage.setTitle("Lista de Empresas");
        abrirStage.setScene(new Scene(root, 600, 450));
        abrirStage.setMinWidth(600);
        abrirStage.setMinHeight(500);

        // 3. Cerrar la ventana principal (facturaPrincipal)
        Stage principalStage = (Stage) btnAbrir.getScene().getWindow();
        principalStage.close();

        // 4. Abrir ventana Nueva Empresa
        abrirStage.show();
    }

}