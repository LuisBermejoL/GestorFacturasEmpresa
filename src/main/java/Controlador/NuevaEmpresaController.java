package Controlador;

import DAO.EmpresaDAO;
import Modelo.Empresa;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * Controlador para la vista "Nueva Empresa". Permite crear una nueva empresa y,
 * tras guardarla, abre directamente la lista de empresas registradas.
 *
 * @author luisb
 */
public class NuevaEmpresaController {

    // Variable para almacenar la empresa si estamos en modo edición
    private Empresa empresaEnEdicion;

    // === ELEMENTOS DE LA INTERFAZ ===
    @FXML
    private ImageView retroceder; // Imagen de flecha para volver atrás

    // Campos de contacto de la empresa
    @FXML
    private TextField txtEmpresaNombre;
    @FXML
    private TextField txtEmpresaNif;
    @FXML
    private TextField txtEmpresaTelefono;
    @FXML
    private TextField txtEmpresaCorreo;
    @FXML
    private TextField txtEmpresaWeb;
    @FXML
    private TextField txtEmpresaDfiscal;
    @FXML
    private TextField txtEmpresaContacto;

    // Campos de localización de la empresa
    @FXML
    private TextField txtEmpresaDireccion;
    @FXML
    private TextField txtEmpresaPais;
    @FXML
    private TextField txtEmpresaCiudad;
    @FXML
    private TextField txtEmpresaProvincia;
    @FXML
    private TextField txtEmpresaCp;

    // Botón "Aceptar" para guardar la empresa
    @FXML
    private Button btnAceptar;

    // DAO para operaciones con la tabla empresa
    private final EmpresaDAO empresaDAO = new EmpresaDAO();

    // === MÉTODOS DE INICIALIZACIÓN ===
    /**
     * Se ejecuta automáticamente al cargar el FXML. Configura los eventos de
     * los botones e imágenes.
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
     * Método para volver al menú principal (Inicio). Se usa al pulsar la flecha
     * retroceder.
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
     * Método para guardar una nueva empresa en la base de datos. Tras guardar,
     * abre directamente la lista de empresas.
     */
    @FXML
    private void guardarEmpresa() {
        // CAMPOS OBLIGATORIOS
        if (!campoEsValido(txtEmpresaNombre, "Nombre de la empresa")) {
            return;
        }
        if (!campoEsValido(txtEmpresaNif, "NIF")) {
            return;
        }
        if (!campoEsValido(txtEmpresaTelefono, "Teléfono")) {
            return;
        }
        if (!campoEsValido(txtEmpresaCorreo, "Correo electrónico")) {
            return;
        }
        if (!campoEsValido(txtEmpresaDireccion, "Dirección")) {
            return;
        }
        if (!campoEsValido(txtEmpresaCiudad, "Ciudad")) {
            return;
        }
        if (!campoEsValido(txtEmpresaPais, "País")) {
            return;
        }
        if (!campoEsValido(txtEmpresaCp, "Código Postal")) {
            return;
        }
        if (!campoEsValido(txtEmpresaDfiscal, "Domicilio Fiscal")) {
            return;
        }
        if (!campoEsValido(txtEmpresaContacto, "Persona de contacto")) {
            return;
        }

        String nif = txtEmpresaNif.getText().trim().toUpperCase();
        String telefono = txtEmpresaTelefono.getText().trim();
        String email = txtEmpresaCorreo.getText().trim();
        String cp = txtEmpresaCp.getText().trim();

        // VALIDACIONES DE FORMATO
        if (!validarNIF(nif)) {
            mostrarError("Campo NIF.\n\nDebe tener 5-20 caracteres alfanuméricos.\nEjemplos:\n• B12345678\n• DE123456789");
            return;
        }
        if (!validarTelefono(telefono)) {
            mostrarError("Teléfono inválido.\n\nDebe tener 6-15 dígitos (puede empezar con +).\nEjemplos:\n• 612345678\n• +34612345678");
            return;
        }
        if (!validarEmail(email)) {
            mostrarError("Correo electrónico inválido.\nEjemplo: empresa@dominio.com");
            return;
        }
        if (!validarCP(cp)) {
            mostrarError("Código Postal inválido.\n\nDebe tener 3-10 caracteres alfanuméricos.\nEjemplos:\n• 28001\n• SW1A 1AA");
            return;
        }

        // GUARDAR EMPRESA
        Empresa empresa = new Empresa();
        if (empresaEnEdicion != null) {
            empresa.setId(empresaEnEdicion.getId());
        }

        empresa.setNombre(txtEmpresaNombre.getText().trim());
        empresa.setNif(nif);
        empresa.setTelefono(telefono);
        empresa.setEmail(email);
        empresa.setWeb(txtEmpresaWeb.getText().trim().isEmpty() ? null : txtEmpresaWeb.getText().trim());
        empresa.setDomicilioFiscal(txtEmpresaDfiscal.getText().trim());
        empresa.setContacto(txtEmpresaContacto.getText().trim());
        empresa.setDireccion(txtEmpresaDireccion.getText().trim());
        empresa.setPais(txtEmpresaPais.getText().trim());
        empresa.setCiudad(txtEmpresaCiudad.getText().trim());
        empresa.setProvincia(txtEmpresaProvincia.getText().trim());
        empresa.setCp(cp);

        try {
            if (empresaEnEdicion != null) {
                empresaDAO.modificar(empresa);
                mostrarInfo("Empresa modificada correctamente.");
            } else {
                empresaDAO.añadir(empresa);
                mostrarInfo("Empresa creada correctamente.");
            }
            abrirListaEmpresas();
        } catch (Exception ex) {
            mostrarError("Error al guardar la empresa:\n" + ex.getMessage());
        }
    }

    /**
     * Carga los datos de una empresa existente para editar.
     *
     * @param empresa Empresa a editar
     */
    public void cargarEmpresaParaEditar(Empresa empresa) {
        txtEmpresaNombre.setText(empresa.getNombre());
        txtEmpresaNif.setText(empresa.getNif());
        txtEmpresaTelefono.setText(empresa.getTelefono());
        txtEmpresaCorreo.setText(empresa.getEmail());
        txtEmpresaWeb.setText(empresa.getWeb() != null ? empresa.getWeb() : "");
        txtEmpresaDfiscal.setText(empresa.getDomicilioFiscal());
        txtEmpresaContacto.setText(empresa.getContacto());
        txtEmpresaDireccion.setText(empresa.getDireccion());
        txtEmpresaPais.setText(empresa.getPais());
        txtEmpresaCiudad.setText(empresa.getCiudad());
        txtEmpresaProvincia.setText(empresa.getProvincia());
        txtEmpresaCp.setText(empresa.getCp());

        // Guardar el ID de la empresa que se está editando
        this.empresaEnEdicion = empresa;
    }

    // === ABRIR LISTA DE EMPRESAS ===
    /**
     * Método para abrir la vista de lista de empresas. Se ejecuta tras guardar
     * una nueva empresa.
     */
    private void abrirListaEmpresas() {
        try {
            // 1. Cargar el FXML de lista de empresas
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/luis/gestorfacturasempresa/abrirListaEmpresas.fxml"));
            Parent root = loader.load();

            // 2. Crear nueva ventana (Stage)
            Stage listaStage = new Stage();
            listaStage.setTitle("Lista de Empresas");
            listaStage.setScene(new Scene(root, 650, 450));
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
     * @param titulo Título de la alerta
     */
    private void mostrarError(String mensaje) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }

    private void mostrarInfo(String mensaje) {
        Alert a = new Alert(AlertType.CONFIRMATION);
        a.setTitle("Exito");
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }

    private boolean validarNIF(String taxId) {
        if (taxId == null || taxId.trim().isEmpty()) {
            return false;
        }
        return taxId.trim().toUpperCase().matches("^[A-Z0-9\\-\\s\\.]{5,20}$");
    }

    private boolean validarTelefono(String tel) {
        if (tel == null || tel.trim().isEmpty()) {
            return false;
        }
        return tel.trim().matches("^\\+?[0-9]{6,15}$");
    }

    private boolean validarEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return true;
        }
        return email.trim().matches("^[\\w._%+-]+@[\\w.-]+\\.[A-Za-z]{2,6}$");
    }

    private boolean validarCP(String cp) {
        if (cp == null || cp.trim().isEmpty()) {
            return false;
        }
        return cp.trim().matches("^[A-Z0-9\\-\\s]{3,10}$");
    }

    private boolean campoEsValido(TextField campo, String nombre) {
        if (campo.getText() == null || campo.getText().trim().isEmpty()) {
            mostrarError("El campo '" + nombre + "' es obligatorio.");
            return false;
        }
        return true;
    }

}
