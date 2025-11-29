package Controlador;

import Modelo.Empresa;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;

/**
 * Controlador para la vista facturaClientes.fxml.
 * Gestiona los datos de Cliente, Proveedor, Producto y Facturas
 * asociados a la empresa seleccionada.
 *
 * Este controlador recibe la empresa desde AbrirEmpresaController
 * y carga sus datos en los campos correspondientes.
 *
 * @author luisb
 */
public class FacturaClientesController {

    // === ELEMENTOS DEL FXML ===
    @FXML private TabPane MenuCliente;

    // Datos de Cliente
    @FXML private VBox datosCliente;
    @FXML private VBox direccionCliente;
    @FXML private TextField txtClienteNombre;
    @FXML private TextField txtClienteNif;
    @FXML private TextField txtClienteCorreo;
    @FXML private TextField txtClienteTelefono;
    @FXML private TextField txtClienteDireccion;
    @FXML private TextField txtClienteCp;
    @FXML private TextField txtClienteCiudad;
    @FXML private TextField txtClienteProvincia;
    @FXML private TextField txtClientePais;
    @FXML private ComboBox<String> comboClienteDireccion;

    // Datos de Proveedor
    @FXML private VBox datosProveedor;
    @FXML private VBox direccionProveedor;
    @FXML private TextField txtProveedorNombre;
    @FXML private TextField txtProveedorNif;
    @FXML private TextField txtProveedorCorreo;
    @FXML private TextField txtProveedorTelefono;
    @FXML private TextField txtProveedorDireccion;
    @FXML private TextField txtProveedorCp;
    @FXML private TextField txtProveedorCiudad;
    @FXML private TextField txtProveedorProvincia;
    @FXML private TextField txtProveedorPais;
    @FXML private ComboBox<String> comboProveedorDireccion;

    // Datos de Producto
    @FXML private VBox datosProveedor1; // puedes renombrar a datosProducto
    @FXML private TextField txtProductoCodigo;
    @FXML private TextField txtProductoDescripcion;
    @FXML private TextField txtProductoReferencia;
    @FXML private TextField txtProductoIva;
    @FXML private TextField txtProductoPrecioCoste;
    @FXML private TextField txtProductoPrecioVenta;
    @FXML private TextField txtProductoStock;

    // Datos de Facturas
    @FXML private VBox datosFacturas;
    @FXML private ComboBox<String> comboFacturaTipo;
    @FXML private TextField txtFacturaNumero;
    @FXML private TextField txtFacturaFecha;
    @FXML private TextField txtFacturaConcepto;
    @FXML private TextField txtFacturaBase;
    @FXML private TextField txtFacturaIvaTotal;
    @FXML private TextField txtFacturaTotal;
    @FXML private ComboBox<String> comboFacturaEstado;
    @FXML private TextField txtFacturaObservaciones;

    // Botón Añadir
    @FXML private Button btnAñadir;

    // === OBJETO EMPRESA SELECCIONADA ===
    private Empresa empresa;

    /**
     * Método para recibir la empresa seleccionada desde AbrirEmpresaController.
     * @param empresa Empresa seleccionada en la tabla
     */
    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
        cargarDatosEmpresa();
    }

    /**
     * Inicialización automática al cargar el FXML.
     * Aquí se configuran las opciones de los ComboBox y eventos de botones.
     */
    @FXML
    private void initialize() {
        // Opciones de dirección
        if (comboClienteDireccion != null) {
            comboClienteDireccion.getItems().setAll("Fiscal", "Envío", "Otro");
        }
        if (comboProveedorDireccion != null) {
            comboProveedorDireccion.getItems().setAll("Fiscal", "Envío", "Otro");
        }

        // Opciones de factura
        if (comboFacturaTipo != null) {
            comboFacturaTipo.getItems().setAll("Venta", "Compra");
        }
        if (comboFacturaEstado != null) {
            comboFacturaEstado.getItems().setAll("PENDIENTE", "PAGADA", "ANULADA");
            comboFacturaEstado.setValue("PENDIENTE"); // valor por defecto
        }

        // Evento del botón Añadir
        if (btnAñadir != null) {
            btnAñadir.setOnAction(e -> handleAñadir());
        }
    }

    /**
     * Cargar los datos básicos de la empresa en los campos de Cliente.
     */
    private void cargarDatosEmpresa() {
        if (empresa != null) {
            if (txtClienteNombre != null) txtClienteNombre.setText(empresa.getNombre());
            if (txtClienteNif != null) txtClienteNif.setText(empresa.getNif());
            if (txtClienteCorreo != null) txtClienteCorreo.setText(empresa.getEmail());
            if (txtClienteTelefono != null) txtClienteTelefono.setText(empresa.getTelefono());
        }
    }

    /**
     * Acción del botón Añadir.
     * Recoge los datos de los campos y los muestra o guarda.
     */
    @FXML
    private void handleAñadir() {
        // Ejemplo: recoger datos de cliente
        String nombre = txtClienteNombre.getText();
        String nif = txtClienteNif.getText();
        String correo = txtClienteCorreo.getText();
        String telefono = txtClienteTelefono.getText();
        String tipoDireccion = comboClienteDireccion.getValue();

        System.out.println("Añadiendo cliente: " + nombre + " - " + nif +
                " (" + tipoDireccion + ")");

        // Aquí puedes llamar a tu DAO para insertar en la base de datos
        // clienteDAO.insertar(new Cliente(nombre, nif, correo, telefono, tipoDireccion));
    }

    /**
     * Método para limpiar todos los campos de la vista.
     */
    @FXML
    private void limpiarCampos() {
        // Cliente
        txtClienteNombre.clear();
        txtClienteNif.clear();
        txtClienteCorreo.clear();
        txtClienteTelefono.clear();
        txtClienteDireccion.clear();
        txtClienteCp.clear();
        txtClienteCiudad.clear();
        txtClienteProvincia.clear();
        txtClientePais.clear();
        comboClienteDireccion.getSelectionModel().clearSelection();

        // Proveedor
        txtProveedorNombre.clear();
        txtProveedorNif.clear();
        txtProveedorCorreo.clear();
        txtProveedorTelefono.clear();
        txtProveedorDireccion.clear();
        txtProveedorCp.clear();
        txtProveedorCiudad.clear();
        txtProveedorProvincia.clear();
        txtProveedorPais.clear();
        comboProveedorDireccion.getSelectionModel().clearSelection();

        // Producto
        txtProductoCodigo.clear();
        txtProductoDescripcion.clear();
        txtProductoReferencia.clear();
        txtProductoIva.clear();
        txtProductoPrecioCoste.clear();
        txtProductoPrecioVenta.clear();
        txtProductoStock.clear();

        // Factura
        txtFacturaNumero.clear();
        txtFacturaFecha.clear();
        txtFacturaConcepto.clear();
        txtFacturaBase.clear();
        txtFacturaIvaTotal.clear();
        txtFacturaTotal.clear();
        txtFacturaObservaciones.clear();
        comboFacturaTipo.getSelectionModel().clearSelection();
        comboFacturaEstado.getSelectionModel().clearSelection();
    }

    /**
     * Método para actualizar datos (ejemplo).
     */
    @FXML
    private void actualizarDatos() {
        System.out.println("Actualizando datos...");
        // Aquí iría la lógica de actualización en BD
    }

    /**
     * Método para eliminar datos (ejemplo).
     */
    @FXML
    private void eliminarDatos() {
        System.out.println("Eliminando datos...");
        // Aquí iría la lógica de eliminación en BD
    }
}