package Controlador;

import DAO.EmpresaDAO;
import Modelo.Empresa;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * Controlador para la vista "Nueva Empresa".
 * Permite crear una nueva empresa y, tras guardarla,
 * abre directamente la lista de empresas registradas.
 *
 * @author luisb
 */
public class NuevaEmpresaController {

    // === ELEMENTOS DE LA INTERFAZ ===

    @FXML
    private ImageView retroceder; // Imagen de flecha para volver atrás

    // Campos de contacto de la empresa
    @FXML private TextField txtEmpresaNombre;
    @FXML private TextField txtEmpresaNif;
    @FXML private TextField txtEmpresaTelefono;
    @FXML private TextField txtEmpresaCorreo;
    @FXML private TextField txtEmpresaWeb;
    @FXML private TextField txtEmpresaDfiscal;
    @FXML private TextField txtEmpresaContacto;

    // Campos de localización de la empresa
    @FXML private TextField txtEmpresaDireccion;
    @FXML private TextField txtEmpresaPais;
    @FXML private TextField txtEmpresaCiudad;
    @FXML private TextField txtEmpresaProvincia;
    @FXML private TextField txtEmpresaCp;

    // Botón "Aceptar" para guardar la empresa
    @FXML private Button btnAceptar;

    // DAO para operaciones con la tabla empresa
    private final EmpresaDAO empresaDAO = new EmpresaDAO();

    // === MÉTODOS DE INICIALIZACIÓN ===

    /**
     * Se ejecuta automáticamente al cargar el FXML.
     * Configura los eventos de los botones e imágenes.
     */
    @FXML
    private void initialize() {
        // Evento de clic en la flecha retroceder → volver al inicio
        retroceder.setOnMouseClicked(event -> volverAPrincipal());

        // Evento de clic en el botón aceptar → guardar empresa
        btnAceptar.setOnAction(e -> guardarEmpresa());
    }

    // === NAVEGACIÓN ===

    /**
     * Método para volver al menú principal (Inicio).
     * Se usa al pulsar la flecha retroceder.
     */
    @FXML
    private void volverAPrincipal() {
        try {
            // 1. Cargar el FXML de inicio
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/luis/gestorfacturasempresa/inicio.fxml"));
            Parent root = loader.load();

            // 2. Crear nueva ventana (Stage)
            Stage principalStage = new Stage();
            principalStage.setTitle("Gestor de Facturas - Empresa");
            principalStage.setScene(new Scene(root, 600, 400));
            principalStage.setMinWidth(600);
            principalStage.setMinHeight(400);
            principalStage.centerOnScreen();

            // 3. Cerrar la ventana actual
            Stage actualStage = (Stage) retroceder.getScene().getWindow();
            actualStage.close();

            // 4. Mostrar la ventana de inicio
            principalStage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // === GUARDAR EMPRESA ===

    /**
     * Método para guardar una nueva empresa en la base de datos.
     * Tras guardar, abre directamente la lista de empresas.
     */
    @FXML
    private void guardarEmpresa() {
        // Validación básica: nombre y NIF son obligatorios
        if (txtEmpresaNombre.getText().trim().isEmpty() || txtEmpresaNif.getText().trim().isEmpty()) {
            mostrarAlerta("Campos obligatorios", "El nombre y NIF son obligatorios.");
            return;
        }

        // Crear objeto Empresa con los datos del formulario
        Empresa empresa = new Empresa();
        empresa.setNombre(txtEmpresaNombre.getText().trim());
        empresa.setNif(txtEmpresaNif.getText().trim());
        empresa.setTelefono(txtEmpresaTelefono.getText().trim());
        empresa.setEmail(txtEmpresaCorreo.getText().trim());
        empresa.setWeb(txtEmpresaWeb.getText().trim());
        empresa.setDomicilioFiscal(txtEmpresaDfiscal.getText().trim());
        empresa.setDireccion(txtEmpresaDireccion.getText().trim());

        // Si el campo país está vacío, se asigna "España" por defecto
        String pais = txtEmpresaPais.getText().trim();
        empresa.setPais(pais.isEmpty() ? "España" : pais);

        empresa.setCiudad(txtEmpresaCiudad.getText().trim());
        empresa.setProvincia(txtEmpresaProvincia.getText().trim());
        empresa.setCp(txtEmpresaCp.getText().trim());
        empresa.setContacto(txtEmpresaContacto.getText().trim());

        // Guardar empresa en la base de datos
        empresaDAO.añadir(empresa);

        // Mostrar mensaje de éxito
        mostrarAlerta("Éxito", "Empresa creada correctamente.");

        // Abrir directamente la lista de empresas
        abrirListaEmpresas();
    }

    // === ABRIR LISTA DE EMPRESAS ===

    /**
     * Método para abrir la vista de lista de empresas.
     * Se ejecuta tras guardar una nueva empresa.
     */
    private void abrirListaEmpresas() {
        try {
            // 1. Cargar el FXML de lista de empresas
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/luis/gestorfacturasempresa/abrirListaEmpresas.fxml"));
            Parent root = loader.load();

            // 2. Crear nueva ventana (Stage)
            Stage listaStage = new Stage();
            listaStage.setTitle("Lista de Empresas");
            listaStage.setScene(new Scene(root, 600, 450));
            listaStage.setMinWidth(600);
            listaStage.setMinHeight(500);
            listaStage.centerOnScreen();

            // 3. Cerrar la ventana actual
            Stage actualStage = (Stage) btnAceptar.getScene().getWindow();
            actualStage.close();

            // 4. Mostrar la ventana de lista de empresas
            listaStage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // === ALERTAS ===

    /**
     * Método auxiliar para mostrar mensajes al usuario.
     *
     * @param titulo  Título de la alerta
     * @param mensaje Contenido del mensaje
     */
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}