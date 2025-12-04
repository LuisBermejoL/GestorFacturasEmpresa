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
        // 1. Validar que todos los campos obligatorios estén rellenos
        if (!validarCamposObligatorios()) {
            return; // Se mostró alerta, no seguimos
        }

        // 2. Validar formatos específicos
        String nif = txtEmpresaNif.getText().trim().toUpperCase();
        String telefono = txtEmpresaTelefono.getText().trim();
        String email = txtEmpresaCorreo.getText().trim();
        String web = txtEmpresaWeb.getText().trim();
        String cp = txtEmpresaCp.getText().trim();

        if (!validarNIF(nif)) {
            mostrarAlerta("NIF incorrecto",
                    "El NIF de empresa debe tener:\n"
                    + "• 1 letra (A,B,C,E,F,G,J)\n"
                    + "• 7 números\n"
                    + "• 1 dígito de control (0-9 o A-J)\n\n"
                    + "Ejemplo: B1234567C");
            return;
        }

        if (!validarTelefono(telefono)) {
            mostrarAlerta("Teléfono incorrecto", "El teléfono debe tener exactamente 9 dígitos.");
            return;
        }

        if (!validarEmail(email)) {
            mostrarAlerta("Email incorrecto", "Por favor, introduce un correo electrónico válido.\nEjemplo: empresa@dominio.com");
            return;
        }

        if (!web.isEmpty() && !validarWeb(web)) {
            mostrarAlerta("Web incorrecta", "La página web debe tener un formato válido.\nEjemplo: www.empresa.com");
            return;
        }

        if (!validarCodigoPostal(cp)) {
            mostrarAlerta("Código Postal incorrecto", "El C.P. debe tener exactamente 5 dígitos.");
            return;
        }

        // 3. Si todo está bien → crear empresa
        Empresa empresa = new Empresa();
        // Si estamos editando, usamos el ID original
        if (this.empresaEnEdicion != null) {
            empresa.setId(this.empresaEnEdicion.getId());
        }
        empresa.setNombre(txtEmpresaNombre.getText().trim());
        empresa.setNif(nif);
        empresa.setTelefono(telefono);
        empresa.setEmail(email);
        empresa.setWeb(web.isEmpty() ? null : web);
        empresa.setDomicilioFiscal(txtEmpresaDfiscal.getText().trim());
        empresa.setContacto(txtEmpresaContacto.getText().trim());
        empresa.setDireccion(txtEmpresaDireccion.getText().trim());
        empresa.setPais(txtEmpresaPais.getText().trim().isEmpty() ? "España" : txtEmpresaPais.getText().trim());
        empresa.setCiudad(txtEmpresaCiudad.getText().trim());
        empresa.setProvincia(txtEmpresaProvincia.getText().trim());
        empresa.setCp(cp);

        try {
            if (this.empresaEnEdicion != null) {
                // Modo edición: llama a MODIFICAR
                empresaDAO.modificar(empresa);
                mostrarAlerta("Éxito", "Empresa actualizada correctamente.");
            } else {
                // Modo nueva empresa: llama a AÑADIR
                empresaDAO.añadir(empresa);
                mostrarAlerta("Éxito", "Empresa creada correctamente.");
            }
            abrirListaEmpresas();

        } catch (Exception ex) {
            mostrarAlerta("Error", "No se pudo guardar la empresa: " + ex.getMessage());
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
     * @param mensaje Contenido del mensaje
     */
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private boolean validarCamposObligatorios() {
        StringBuilder faltan = new StringBuilder("Los siguientes campos son obligatorios:\n");

        if (txtEmpresaNombre.getText().trim().isEmpty()) {
            faltan.append("• Nombre de la empresa\n");
        }
        if (txtEmpresaNif.getText().trim().isEmpty()) {
            faltan.append("• NIF\n");
        }
        if (txtEmpresaTelefono.getText().trim().isEmpty()) {
            faltan.append("• Teléfono\n");
        }
        if (txtEmpresaCorreo.getText().trim().isEmpty()) {
            faltan.append("• Email\n");
        }
        if (txtEmpresaDireccion.getText().trim().isEmpty()) {
            faltan.append("• Dirección\n");
        }
        if (txtEmpresaCiudad.getText().trim().isEmpty()) {
            faltan.append("• Ciudad\n");
        }
        if (txtEmpresaCp.getText().trim().isEmpty()) {
            faltan.append("• Código Postal\n");
        }

        if (faltan.length() > 50) { // Si hay errores
            mostrarAlerta("Faltan datos obligatorios", faltan.toString());
            return false;
        }
        return true;
    }

    private boolean validarNIF(String nif) {
        nif = nif.trim().toUpperCase();

        // Validar formato de NIF empresa: 1 letra + 7 números + 1 dígito (letra o número)
        return nif.matches("^[ABCEFGJ]\\d{7}[0-9A-J]$");
    }

    private boolean validarTelefono(String telefono) {
        return telefono.matches("\\d{9}");
    }

    private boolean validarEmail(String email) {
        return email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }

    private boolean validarWeb(String web) {
        return web.matches("^(www\\.)?[\\w\\-]+\\.[\\w\\-]+(\\.[\\w\\-]+)*$");
    }

    private boolean validarCodigoPostal(String cp) {
        return cp.matches("\\d{5}");
    }

}
