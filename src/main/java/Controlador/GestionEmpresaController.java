package Controlador;

// --- IMPORTACIONES ---
import Modelo.*;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * Controlador Principal: GESTIÓN DE EMPRESA.
 * * Responsabilidades:
 * 1. Gestionar la interacción del usuario con las pestañas (Cliente, Proveedor, Producto, Factura).
 * 2. Validar datos antes de enviarlos a la base de datos (Integridad de datos).
 * 3. Optimizar el rendimiento visual mediante caché de datos.
 * 4. Coordinar la lógica de negocio compleja (Stock, Facturación con líneas).
 */
public class GestionEmpresaController {

    // ========================================================================
    // 1. VARIABLES GLOBALES Y DE ESTADO
    // ========================================================================
    
    private Empresa empresa; // La empresa activa sobre la que trabajamos
    private boolean inicializado = false; // Bandera para controlar la carga inicial
    
    // CACHÉ DE DIRECCIONES (OPTIMIZACIÓN DE RENDIMIENTO)
    // Guardamos todas las direcciones en RAM para evitar hacer 1 consulta SQL por cada celda de la tabla.
    // Esto reduce el tiempo de carga de segundos a milisegundos.
    private List<Direccion> cacheDirecciones = new ArrayList<>();

    // --- INSTANCIAS DE CONTROLADORES (CAPA DE NEGOCIO) ---
    private final EntidadController entidadController = new EntidadController();
    private final ClienteController clienteController = new ClienteController();
    private final ProveedorController proveedorController = new ProveedorController();
    private final ProductoController productoController = new ProductoController();
    private final FacturaController facturaController = new FacturaController();
    private final DireccionController direccionController = new DireccionController();

    // ========================================================================
    // 2. ELEMENTOS DE LA INTERFAZ (FXML)
    // ========================================================================

    @FXML private TabPane MenuCliente; // Pestañas principales
    @FXML private Label nombreEmpresa; // Título
    @FXML private ImageView retroceder; // Botón volver

    // --- PESTAÑA CLIENTES ---
    @FXML private TableView<Cliente> tablaClientes;
    @FXML private TableColumn<Cliente, String> colClienteCodigo, colClienteNombre, colClienteNif, colClienteCorreo, colClienteTelefono;
    @FXML private TableColumn<Cliente, String> colClienteDireccion, colClienteCiudad, colClienteProvincia, colClientePais, colClienteCp;
    
    @FXML private TextField txtClienteNombre, txtClienteNif, txtClienteCorreo, txtClienteTelefono;
    @FXML private ComboBox<String> comboClienteDireccion;
    @FXML private TextField txtClienteDireccion, txtClienteCp, txtClienteCiudad, txtClienteProvincia, txtClientePais;

    // --- PESTAÑA PROVEEDORES ---
    @FXML private TableView<Proveedor> tablaProveedores;
    @FXML private TableColumn<Proveedor, String> colProveedorCodigo, colProveedorNombre, colProveedorNif, colProveedorCorreo, colProveedorTelefono;
    @FXML private TableColumn<Proveedor, String> colProveedorDireccion, colProveedorCiudad, colProveedorProvincia, colProveedorPais, colProveedorCp;

    @FXML private TextField txtProveedorNombre, txtProveedorNif, txtProveedorCorreo, txtProveedorTelefono;
    @FXML private ComboBox<String> comboProveedorDireccion;
    @FXML private TextField txtProveedorDireccion, txtProveedorCp, txtProveedorCiudad, txtProveedorProvincia, txtProveedorPais;

    // --- PESTAÑA PRODUCTOS ---
    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, String> colProductoCodigo, colProductoDescripcion, colProductoReferencia, colProductoProveedor, colProductoIva,
            colProductoPrecioCoste, colProductoPrecioVenta, colProductoStock;

    @FXML private TextField txtProductoCodigo, txtProductoDescripcion, txtProductoReferencia;
    @FXML private ComboBox<Proveedor> comboProductoProveedor; // Selector de proveedor para el producto
    @FXML private TextField txtProductoIva, txtProductoPrecioCoste, txtProductoPrecioVenta, txtProductoStock;

    // --- PESTAÑA FACTURAS (CABECERA) ---
    @FXML private TableView<Factura> tablaFacturas;
    @FXML private TableColumn<Factura, String> colFacturaEntidadId, colFacturaTipo, colFacturaNumero, colFacturaFecha, colFacturaConcepto,
            colFacturaBase, colFacturaIvaTotal, colFacturaTotal, colFacturaEstado, colFacturaObservaciones;

    @FXML private ComboBox<Entidad> comboFacturaEntidad; // Selector inteligente (Cliente o Proveedor según tipo)
    @FXML private TextField txtFacturaNumero, txtFacturaFecha, txtFacturaConcepto,
            txtFacturaBase, txtFacturaIvaTotal, txtFacturaTotal, txtFacturaObservaciones;
    @FXML private ComboBox<String> comboFacturaTipo, comboFacturaEstado;

    // --- PESTAÑA FACTURAS (LÍNEAS/DETALLES) ---
    @FXML private ComboBox<Producto> comboFacturaProducto;
    @FXML private TextField txtFacturaCantidad;
    @FXML private TextField txtFacturaDescuento;
    
    // Tabla temporal para ver qué estamos añadiendo a la factura antes de guardarla
    @FXML private TableView<LineaFactura> tablaLineasFactura;
    @FXML private TableColumn<LineaFactura, String> colLineaProducto, colLineaCantidad, colLineaPrecio, colLineaTotal;
    
    // Botón para generar el pdf de la factura
    @FXML private Button btnImprimirFactura;

    // Lista en memoria para las líneas (Carrito de compra de la factura)
    private ObservableList<LineaFactura> lineasTemporales = FXCollections.observableArrayList();

    // ========================================================================
    // 3. INICIALIZACIÓN Y CONFIGURACIÓN
    // ========================================================================

    @FXML
    private void initialize() {
        // 1. Configuración de elementos estáticos (Listas fijas)
        if(comboFacturaTipo != null) comboFacturaTipo.setItems(FXCollections.observableArrayList("Venta", "Compra"));
        if(comboFacturaEstado != null) comboFacturaEstado.setItems(FXCollections.observableArrayList("PENDIENTE", "PAGADA", "ANULADA"));
        if(comboClienteDireccion != null) comboClienteDireccion.setItems(FXCollections.observableArrayList("Fiscal", "Envío", "Otro"));
        if(comboProveedorDireccion != null) comboProveedorDireccion.setItems(FXCollections.observableArrayList("Fiscal", "Envío", "Otro"));
        if (btnImprimirFactura != null) btnImprimirFactura.setOnAction(e -> handleImprimirFactura());

        // 2. Evento volver atrás
        if (retroceder != null) retroceder.setOnMouseClicked(event -> volverAListaEmpresas());

        // 3. Configurar Tablas (Binding): Decimos qué dato va en qué columna
        configurarTablas();

        // 4. Configurar Visualización de Combos (Para ver nombres en vez de códigos de memoria)
        configurarConvertidoresCombos();

        // 5. Configurar Tabla de Líneas (Tabla pequeña de factura)
        configurarTablaLineas();

        // 6. Listener para Carga Dinámica de Entidades en Factura
        // Si seleccionas "Venta" carga Clientes. Si es "Compra" carga Proveedores.
        if (comboFacturaTipo != null) {
            comboFacturaTipo.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) cargarEntidadesEnFactura(newVal);
            });
        }

        // 7. LISTENERS DE SELECCIÓN (AUTO-RELLENADO DE FORMULARIOS)
        // Al hacer clic en una fila, los datos pasan a los campos de texto para poder editar.
        if(tablaClientes != null) tablaClientes.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> { if(n!=null) cargarDatosCliente(n); });
        if(tablaProveedores != null) tablaProveedores.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> { if(n!=null) cargarDatosProveedor(n); });
        if(tablaProductos != null) tablaProductos.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> { if(n!=null) cargarDatosProducto(n); });
        if(tablaFacturas != null) tablaFacturas.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> { if(n!=null) cargarDatosFactura(n); });

        // 8. Finalizar
        inicializado = true;
        // Pre-selección para evitar combos vacíos
        if (comboFacturaTipo != null) comboFacturaTipo.getSelectionModel().select("Venta");
    }

    // Método llamado al abrir la ventana
    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
        if (nombreEmpresa != null) {
            nombreEmpresa.setText(empresa != null ? empresa.getNombre() + " (" + empresa.getNif() + ")" : "Error");
        }
        if (inicializado && empresa != null) {
            refrescarTodo(); // Carga inicial de datos desde BD
        }
    }

    private void configurarTablas() {
        // Enlace de datos para Clientes (usando caché para direcciones)
        colClienteCodigo.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getCodigo())));
        colClienteNombre.setCellValueFactory(c -> new SimpleStringProperty(safeStr(c.getValue().getNombre())));
        colClienteNif.setCellValueFactory(c -> new SimpleStringProperty(safeStr(c.getValue().getNif())));
        colClienteCorreo.setCellValueFactory(c -> new SimpleStringProperty(safeStr(c.getValue().getEmail())));
        colClienteTelefono.setCellValueFactory(c -> new SimpleStringProperty(safeStr(c.getValue().getTelefono())));
        // Columnas calculadas dinámicamente desde la caché
        colClienteDireccion.setCellValueFactory(c -> new SimpleStringProperty(obtenerDireccionCampo(c.getValue().getId(), "direccion")));
        colClienteCiudad.setCellValueFactory(c -> new SimpleStringProperty(obtenerDireccionCampo(c.getValue().getId(), "ciudad")));
        colClienteProvincia.setCellValueFactory(c -> new SimpleStringProperty(obtenerDireccionCampo(c.getValue().getId(), "provincia")));
        colClientePais.setCellValueFactory(c -> new SimpleStringProperty(obtenerDireccionCampo(c.getValue().getId(), "pais")));
        colClienteCp.setCellValueFactory(c -> new SimpleStringProperty(obtenerDireccionCampo(c.getValue().getId(), "cp")));

        // Enlace de datos para Proveedores
        colProveedorCodigo.setCellValueFactory(p -> new SimpleStringProperty(String.valueOf(p.getValue().getCodigo())));
        colProveedorNombre.setCellValueFactory(p -> new SimpleStringProperty(safeStr(p.getValue().getNombre())));
        colProveedorNif.setCellValueFactory(p -> new SimpleStringProperty(safeStr(p.getValue().getNif())));
        colProveedorCorreo.setCellValueFactory(p -> new SimpleStringProperty(safeStr(p.getValue().getEmail())));
        colProveedorTelefono.setCellValueFactory(p -> new SimpleStringProperty(safeStr(p.getValue().getTelefono())));
        colProveedorDireccion.setCellValueFactory(p -> new SimpleStringProperty(obtenerDireccionCampo(p.getValue().getId(), "direccion")));
        colProveedorCiudad.setCellValueFactory(p -> new SimpleStringProperty(obtenerDireccionCampo(p.getValue().getId(), "ciudad")));
        colProveedorProvincia.setCellValueFactory(p -> new SimpleStringProperty(obtenerDireccionCampo(p.getValue().getId(), "provincia")));
        colProveedorPais.setCellValueFactory(p -> new SimpleStringProperty(obtenerDireccionCampo(p.getValue().getId(), "pais")));
        colProveedorCp.setCellValueFactory(p -> new SimpleStringProperty(obtenerDireccionCampo(p.getValue().getId(), "cp")));

        // Enlace de datos para Productos
        colProductoCodigo.setCellValueFactory(p -> new SimpleStringProperty(safeStr(p.getValue().getCodigo())));
        colProductoDescripcion.setCellValueFactory(p -> new SimpleStringProperty(safeStr(p.getValue().getDescripcion())));
        colProductoReferencia.setCellValueFactory(p -> new SimpleStringProperty(safeStr(p.getValue().getReferenciaProveedor())));
        colProductoProveedor.setCellValueFactory(p -> new SimpleStringProperty(safeStr(p.getValue().getProveedorId())));
        colProductoIva.setCellValueFactory(p -> new SimpleStringProperty(String.valueOf(p.getValue().getTipoIVAId())));
        colProductoPrecioCoste.setCellValueFactory(p -> new SimpleStringProperty(formatDouble(p.getValue().getPrecioCoste())));
        colProductoPrecioVenta.setCellValueFactory(p -> new SimpleStringProperty(formatDouble(p.getValue().getPrecioVenta())));
        colProductoStock.setCellValueFactory(p -> new SimpleStringProperty(formatDouble(p.getValue().getStock())));

        // Enlace de datos para Facturas
        colFacturaEntidadId.setCellValueFactory(f -> new SimpleStringProperty(safeStr(f.getValue().getEntidadId())));
        colFacturaTipo.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getTipo() == 'V' ? "Venta" : "Compra"));
        colFacturaNumero.setCellValueFactory(f -> new SimpleStringProperty(safeStr(f.getValue().getNumero())));
        colFacturaFecha.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getFechaEmision() != null ? f.getValue().getFechaEmision().toString() : ""));
        colFacturaConcepto.setCellValueFactory(f -> new SimpleStringProperty(safeStr(f.getValue().getConcepto())));
        colFacturaBase.setCellValueFactory(f -> new SimpleStringProperty(formatDouble(f.getValue().getBaseImponible())));
        colFacturaIvaTotal.setCellValueFactory(f -> new SimpleStringProperty(formatDouble(f.getValue().getIvaTotal())));
        colFacturaTotal.setCellValueFactory(f -> new SimpleStringProperty(formatDouble(f.getValue().getTotalFactura())));
        colFacturaEstado.setCellValueFactory(f -> new SimpleStringProperty(safeStr(f.getValue().getEstado())));
        colFacturaObservaciones.setCellValueFactory(f -> new SimpleStringProperty(safeStr(f.getValue().getObservaciones())));
    }

    private void configurarConvertidoresCombos() {
        // Define cómo se muestran los objetos complejos en los desplegables
        javafx.util.StringConverter<Proveedor> convProv = new javafx.util.StringConverter<>() {
            public String toString(Proveedor p) { return p == null ? null : p.getNombre() + " (" + p.getNif() + ")"; }
            public Proveedor fromString(String s) { return null; }
        };
        if(comboProductoProveedor != null) comboProductoProveedor.setConverter(convProv);

        javafx.util.StringConverter<Entidad> convEnt = new javafx.util.StringConverter<>() {
            public String toString(Entidad e) { return e == null ? null : e.getNombre() + " (" + e.getNif() + ")"; }
            public Entidad fromString(String s) { return null; }
        };
        if(comboFacturaEntidad != null) comboFacturaEntidad.setConverter(convEnt);

        javafx.util.StringConverter<Producto> convProd = new javafx.util.StringConverter<>() {
            public String toString(Producto p) { return p == null ? null : p.getCodigo() + " - " + p.getDescripcion(); }
            public Producto fromString(String s) { return null; }
        };
        if(comboFacturaProducto != null) comboFacturaProducto.setConverter(convProd);
    }
    
    private void configurarTablaLineas() {
        // Configuración de la tabla pequeña de factura
        if(colLineaProducto != null) colLineaProducto.setCellValueFactory(cell -> new SimpleStringProperty(obtenerNombreProducto(cell.getValue().getProductoId())));
        if(colLineaCantidad != null) colLineaCantidad.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getCantidad())));
        if(colLineaPrecio != null) colLineaPrecio.setCellValueFactory(cell -> new SimpleStringProperty(String.format("%.2f", cell.getValue().getPrecioUnitario())));
        if(colLineaTotal != null) colLineaTotal.setCellValueFactory(cell -> new SimpleStringProperty(String.format("%.2f", cell.getValue().getTotalLinea())));
        
        // ENLACE CRÍTICO: Conectamos la lista visual con la tabla
        if(tablaLineasFactura != null) tablaLineasFactura.setItems(lineasTemporales);
    }

    // ========================================================================
    // 4. GESTIÓN DE EVENTOS (BOTONES PRINCIPALES)
    // ========================================================================
    
    @FXML
    private void handleAñadir() {
        // Detecta qué pestaña está activa y llama al método correspondiente
        String tabName = MenuCliente.getSelectionModel().getSelectedItem().getText();
        if ("Cliente".equals(tabName)) añadirCliente();
        else if ("Proveedor".equals(tabName)) añadirProveedor();
        else if ("Producto".equals(tabName)) añadirProducto();
        else if ("Facturas".equals(tabName)) añadirFactura();
    }

    @FXML
    private void handleModificar() {
        String tabName = MenuCliente.getSelectionModel().getSelectedItem().getText();
        if ("Cliente".equals(tabName)) modificarCliente();
        else if ("Proveedor".equals(tabName)) modificarProveedor();
        else if ("Producto".equals(tabName)) modificarProducto();
        // Modificar facturas complejas (cabecera + líneas) se suele omitir en ejemplos básicos por su complejidad
    }

    @FXML
    private void handleConsultar() {
        refrescarTodo();
    }

    @FXML
    private void handleEliminar() {
        String tabName = MenuCliente.getSelectionModel().getSelectedItem().getText();
        if ("Cliente".equals(tabName)) eliminarCliente();
        else if ("Proveedor".equals(tabName)) eliminarProveedor();
        else if ("Producto".equals(tabName)) eliminarProducto();
        else if ("Facturas".equals(tabName)) eliminarFactura();
    }
    
    @FXML
    private void handleImprimirFactura() {
        // 1. Obtener factura seleccionada
        Factura facturaSel = tablaFacturas.getSelectionModel().getSelectedItem();

        if (facturaSel == null) {
            mostrarError("Selecciona una factura de la tabla para imprimir.");
            return;
        }

        // 2. Verificar que la factura ya esté guardada en BD (tenga ID)
        if (facturaSel.getId() == 0) {
            mostrarError("Esta factura aún no se ha guardado en la base de datos.");
            return;
        }

        // 3. Abrir selector de archivos para elegir dónde guardar
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Guardar Factura PDF");

        // Nombre sugerido: Factura_F-2024-001.pdf
        String nombreArchivo = "Factura_" + facturaSel.getNumero() + ".pdf";
        nombreArchivo = nombreArchivo.replaceAll("[\\\\/:*?\"<>|]", "_"); // Limpiar caracteres inválidos
        fileChooser.setInitialFileName(nombreArchivo);

        fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));

        java.io.File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            // 4. Llamar al gestor para crear el PDF
            GestorReportes gestor = new GestorReportes();
            gestor.generarFacturaPdf(facturaSel.getId(), empresa.getId(), file.getAbsolutePath());
        }
    }

    // ========================================================================
    // 5. LÓGICA CRUD: CLIENTES (AÑADIR / MODIFICAR / ELIMINAR)
    // ========================================================================

    private void añadirCliente() {
        if (empresa == null) { mostrarError("No hay empresa activa."); return; }

        // Validaciones básicas
        if (!campoEsValido(txtClienteNombre, "Nombre")) return;
        if (!campoEsValido(txtClienteNif, "NIF")) return;
        if (!campoEsValido(txtClienteTelefono, "Teléfono")) return;
        if (!campoEsValido(txtClienteCorreo, "Correo")) return;

        String nif = txtClienteNif.getText().trim().toUpperCase();
        String telefono = txtClienteTelefono.getText().trim();

        // Validaciones de formato
        if (!validarNIF(nif)) { mostrarError("El NIF debe tener 7 números y 1 letra."); return; }
        if (!validarTelefono(telefono)) { mostrarError("El teléfono debe tener 9 dígitos."); return; }
        if (!validarEmail(txtClienteCorreo.getText().trim())) { mostrarError("Email incorrecto."); return; }
        if (!txtClienteCp.getText().trim().isEmpty() && !validarCP(txtClienteCp.getText().trim())) { mostrarError("CP incorrecto."); return; }

        // --- VALIDACIONES DE DUPLICADOS ---
        if (existeEntidadConNif(empresa.getId(), nif)) { mostrarError("Ya existe un cliente con ese NIF."); return; }
        if (existeEntidadConTelefono(empresa.getId(), telefono)) { mostrarError("Ya existe un cliente con ese Teléfono."); return; } // <--- NUEVO

        try {
            Cliente c = new Cliente();
            c.setNombre(txtClienteNombre.getText().trim());
            c.setNif(nif);
            c.setEmail(txtClienteCorreo.getText().trim());
            c.setTelefono(telefono);
            c.setCodigo(0);

            long entidadId = clienteController.añadir(c, empresa.getId());
            c.setId(entidadId);

            if (entidadId > 0) {
                añadirDireccionSiRellenada(entidadId, comboClienteDireccion, txtClienteDireccion, txtClienteCp, txtClienteCiudad, txtClienteProvincia, txtClientePais);
            }
            refrescarClientes();
            limpiarCliente();
            mostrarInfo("Cliente añadido correctamente.");
        } catch (Exception e) { mostrarError("Error: " + e.getMessage()); }
    }

    private void modificarCliente() {
        Cliente sel = tablaClientes.getSelectionModel().getSelectedItem();
        if (sel == null) { mostrarError("Selecciona un cliente."); return; }

        if (!campoEsValido(txtClienteNombre, "Nombre")) return;
        String nif = txtClienteNif.getText().trim().toUpperCase();
        String telefono = txtClienteTelefono.getText().trim();

        if (!validarNIF(nif)) { mostrarError("NIF incorrecto."); return; }
        if (!validarTelefono(telefono)) { mostrarError("Teléfono incorrecto."); return; }

        // --- VALIDACIONES DUPLICADOS (EXCLUYENDO PROPIO) ---
        if (existeNifDuplicado(empresa.getId(), nif, sel.getId())) { mostrarError("NIF duplicado en otro cliente."); return; }
        if (existeTelefonoDuplicado(empresa.getId(), telefono, sel.getId())) { mostrarError("Teléfono duplicado en otro cliente."); return; } // <--- NUEVO

        sel.setNombre(txtClienteNombre.getText().trim());
        sel.setNif(nif);
        sel.setEmail(txtClienteCorreo.getText().trim());
        sel.setTelefono(telefono);

        try {
            clienteController.modificar(sel);
            modificarDireccionSiExiste(sel.getId(), comboClienteDireccion, txtClienteDireccion, txtClienteCp, txtClienteCiudad, txtClienteProvincia, txtClientePais);
            refrescarClientes();
            limpiarCliente();
            mostrarInfo("Cliente modificado.");
        } catch (Exception e) { mostrarError(e.getMessage()); }
    }

    private void eliminarCliente() {
        Cliente sel = tablaClientes.getSelectionModel().getSelectedItem();
        if (sel != null && confirmar("¿Eliminar cliente?")) {
            clienteController.borrarPorId(sel.getId());
            refrescarClientes();
        }
    }

    // ========================================================================
    // 6. LÓGICA CRUD: PROVEEDORES
    // ========================================================================

    private void añadirProveedor() {
        if (empresa == null) { mostrarError("No hay empresa activa."); return; }

        if (!campoEsValido(txtProveedorNombre, "Nombre")) return;
        if (!campoEsValido(txtProveedorNif, "NIF")) return;
        if (!campoEsValido(txtProveedorTelefono, "Teléfono")) return;

        String nif = txtProveedorNif.getText().trim().toUpperCase();
        String telefono = txtProveedorTelefono.getText().trim();

        if (!validarNIF(nif)) { mostrarError("NIF incorrecto."); return; }
        if (!validarTelefono(telefono)) { mostrarError("Teléfono incorrecto."); return; }

        // --- DUPLICADOS ---
        if (existeEntidadConNif(empresa.getId(), nif)) { mostrarError("NIF duplicado."); return; }
        if (existeEntidadConTelefono(empresa.getId(), telefono)) { mostrarError("Teléfono duplicado."); return; } // <--- NUEVO

        try {
            Proveedor p = new Proveedor();
            p.setNombre(txtProveedorNombre.getText().trim());
            p.setNif(nif);
            p.setEmail(txtProveedorCorreo.getText().trim());
            p.setTelefono(telefono);
            p.setCodigo(0);

            long entidadId = proveedorController.añadir(p, empresa.getId());
            p.setId(entidadId);

            if (entidadId > 0) {
                añadirDireccionSiRellenada(entidadId, comboProveedorDireccion, txtProveedorDireccion, txtProveedorCp, txtProveedorCiudad, txtProveedorProvincia, txtProveedorPais);
            }
            refrescarProveedores();
            limpiarProveedor();
            mostrarInfo("Proveedor añadido.");
        } catch (Exception e) { mostrarError(e.getMessage()); }
    }

    private void modificarProveedor() {
        Proveedor sel = tablaProveedores.getSelectionModel().getSelectedItem();
        if (sel == null) { mostrarError("Selecciona un proveedor."); return; }

        if (!campoEsValido(txtProveedorNombre, "Nombre")) return;
        String nif = txtProveedorNif.getText().trim().toUpperCase();
        String telefono = txtProveedorTelefono.getText().trim();

        if (!validarNIF(nif)) { mostrarError("NIF incorrecto."); return; }
        if (!validarTelefono(telefono)) { mostrarError("Teléfono incorrecto."); return; }

        // --- DUPLICADOS EN MODIFICACIÓN ---
        if (existeNifDuplicado(empresa.getId(), nif, sel.getId())) { mostrarError("NIF duplicado."); return; }
        if (existeTelefonoDuplicado(empresa.getId(), telefono, sel.getId())) { mostrarError("Teléfono duplicado."); return; } // <--- NUEVO

        sel.setNombre(txtProveedorNombre.getText().trim());
        sel.setNif(nif);
        sel.setEmail(txtProveedorCorreo.getText().trim());
        sel.setTelefono(telefono);

        try {
            proveedorController.modificar(sel);
            modificarDireccionSiExiste(sel.getId(), comboProveedorDireccion, txtProveedorDireccion, txtProveedorCp, txtProveedorCiudad, txtProveedorProvincia, txtProveedorPais);
            refrescarProveedores();
            limpiarProveedor();
            mostrarInfo("Proveedor modificado.");
        } catch (Exception e) { mostrarError(e.getMessage()); }
    }

    private void eliminarProveedor() {
        Proveedor sel = tablaProveedores.getSelectionModel().getSelectedItem();
        if (sel != null && confirmar("¿Eliminar proveedor?")) {
            proveedorController.borrarPorId(sel.getId());
            refrescarProveedores();
        }
    }

    // ========================================================================
    // 7. LÓGICA CRUD: PRODUCTOS
    // ========================================================================

    private void añadirProducto() {
        if (empresa == null) { mostrarError("No hay empresa activa."); return; }

        if (!campoEsValido(txtProductoCodigo, "Código")) return;
        if (!esDecimalValido(txtProductoPrecioCoste, "Precio Coste")) return;
        
        try {
            Producto p = new Producto();
            p.setEmpresaId(empresa.getId());
            p.setCodigo(txtProductoCodigo.getText().trim());
            p.setDescripcion(txtProductoDescripcion.getText().trim());
            p.setReferenciaProveedor(safe(txtProductoReferencia));
            
            Proveedor prov = comboProductoProveedor.getValue();
            p.setProveedorId(prov != null ? prov.getId() : null);

            p.setTipoIVAId(parseIntSafe(txtProductoIva, 0));
            p.setPrecioCoste(parseDoubleSafe(txtProductoPrecioCoste, 0.0));
            p.setPrecioVenta(parseDoubleSafe(txtProductoPrecioVenta, 0.0));
            p.setStock(parseDoubleSafe(txtProductoStock, 0.0));

            productoController.añadir(p, empresa.getId());
            refrescarProductos();
            limpiarProducto();
            mostrarInfo("Producto añadido.");
        } catch (Exception e) { mostrarError("Error: " + e.getMessage()); }
    }

    private void modificarProducto() {
        Producto sel = tablaProductos.getSelectionModel().getSelectedItem();
        if(sel == null) { mostrarError("Selecciona un producto."); return; }

        sel.setDescripcion(safe(txtProductoDescripcion));
        sel.setReferenciaProveedor(safe(txtProductoReferencia));
        
        Proveedor prov = comboProductoProveedor.getValue();
        sel.setProveedorId(prov != null ? prov.getId() : null);
        
        sel.setTipoIVAId(parseIntSafe(txtProductoIva, 0));
        sel.setPrecioCoste(parseDoubleSafe(txtProductoPrecioCoste, 0.0));
        sel.setPrecioVenta(parseDoubleSafe(txtProductoPrecioVenta, 0.0));
        sel.setStock(parseDoubleSafe(txtProductoStock, 0.0));

        try {
            productoController.modificar(sel);
            refrescarProductos();
            limpiarProducto();
            mostrarInfo("Producto modificado.");
        } catch (Exception e) { mostrarError(e.getMessage()); }
    }

    private void eliminarProducto() {
        Producto sel = tablaProductos.getSelectionModel().getSelectedItem();
        if(sel != null && confirmar("¿Eliminar producto?")) {
            productoController.borrarPorCodigo(empresa.getId(), sel.getCodigo());
            refrescarProductos();
        }
    }

    // ========================================================================
    // 8. LÓGICA DE FACTURAS (CABECERA + LÍNEAS + STOCK)
    // ========================================================================

    private void añadirFactura() {
        if (empresa == null) { mostrarError("Error de empresa."); return; }
        if (lineasTemporales.isEmpty()) { mostrarError("La factura está vacía."); return; }

        Entidad entidadSeleccionada = comboFacturaEntidad.getValue();
        if (entidadSeleccionada == null) { mostrarError("Selecciona un Cliente/Proveedor."); return; }
        if (!validarCampoObligatorio(txtFacturaNumero, "Número")) return;

        Factura f = new Factura();
        f.setEmpresaId(empresa.getId());
        f.setEntidadId(entidadSeleccionada.getId());
        f.setTipo(mapTipoFactura(comboFacturaTipo.getValue()));
        f.setNumero(txtFacturaNumero.getText().trim());
        try { f.setFechaEmision(Date.valueOf(txtFacturaFecha.getText().trim())); } 
        catch (Exception e) { mostrarError("Fecha inválida."); return; }

        f.setConcepto(txtFacturaConcepto.getText().trim());
        f.setBaseImponible(parseDoubleSafe(txtFacturaBase, 0.0));
        f.setIvaTotal(parseDoubleSafe(txtFacturaIvaTotal, 0.0));
        f.setTotalFactura(parseDoubleSafe(txtFacturaTotal, 0.0));
        f.setEstado(comboFacturaEstado.getValue());
        f.setObservaciones(safe(txtFacturaObservaciones));

        try {
            // TRANSACCIÓN: Guardamos Factura + Líneas
            facturaController.añadir(f, new ArrayList<>(lineasTemporales));
            
            // LÓGICA DE STOCK: Actualizamos cantidades según si es Venta o Compra
            actualizarStockProductos(f.getTipo(), lineasTemporales);

            refrescarFacturas();
            refrescarProductos(); // Para refrescar el stock en la otra pestaña
            limpiarFactura();
            mostrarInfo("Factura guardada.");
        } catch (Exception e) { mostrarError("Error: " + e.getMessage()); }
    }

    private void eliminarFactura() {
        Factura sel = tablaFacturas.getSelectionModel().getSelectedItem();
        if(sel != null && confirmar("¿Eliminar factura?")) {
            facturaController.borrarPorId(empresa.getId(), sel.getId());
            refrescarFacturas();
        }
    }

    // --- MÉTODOS DE LA SUB-SECCIÓN DE LÍNEAS ---

    @FXML
    private void handleAgregarLinea() {
        if (comboFacturaProducto.getValue() == null) { mostrarError("Selecciona producto."); return; }
        if (!esDecimalValido(txtFacturaCantidad, "Cantidad")) return;

        Producto p = comboFacturaProducto.getValue();
        double cantidad = parseDoubleSafe(txtFacturaCantidad, 1.0);
        double descuento = parseDoubleSafe(txtFacturaDescuento, 0.0);
        double precio = p.getPrecioVenta();

        // Crear línea temporal
        LineaFactura linea = new LineaFactura();
        linea.setProductoId(p.getId());
        linea.setCantidad(cantidad);
        linea.setPrecioUnitario(precio);
        linea.setDescuento(descuento);

        lineasTemporales.add(linea); // Añadir a visual
        recalcularTotalesFactura();  // Actualizar sumas

        // Reset campos pequeños
        txtFacturaCantidad.clear();
        txtFacturaDescuento.clear();
        comboFacturaProducto.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleQuitarLinea() {
        LineaFactura sel = tablaLineasFactura.getSelectionModel().getSelectedItem();
        if (sel != null) {
            lineasTemporales.remove(sel);
            recalcularTotalesFactura();
        }
    }

    private void recalcularTotalesFactura() {
        double base = 0.0;
        for (LineaFactura lf : lineasTemporales) base += lf.getTotalLinea();
        double iva = base * 0.21;
        double total = base + iva;

        txtFacturaBase.setText(String.format("%.2f", base).replace(",", "."));
        txtFacturaIvaTotal.setText(String.format("%.2f", iva).replace(",", "."));
        txtFacturaTotal.setText(String.format("%.2f", total).replace(",", "."));
    }

    // ========================================================================
    // 9. AUTO-RELLENADO DE FORMULARIOS (SELECCIÓN EN TABLA)
    // ========================================================================

    private void cargarDatosCliente(Cliente c) {
        txtClienteNombre.setText(c.getNombre());
        txtClienteNif.setText(c.getNif());
        txtClienteCorreo.setText(c.getEmail());
        txtClienteTelefono.setText(c.getTelefono());
        cargarDireccionEnFormulario(c.getId(), comboClienteDireccion, txtClienteDireccion, txtClienteCp, txtClienteCiudad, txtClienteProvincia, txtClientePais);
    }

    private void cargarDatosProveedor(Proveedor p) {
        txtProveedorNombre.setText(p.getNombre());
        txtProveedorNif.setText(p.getNif());
        txtProveedorCorreo.setText(p.getEmail());
        txtProveedorTelefono.setText(p.getTelefono());
        cargarDireccionEnFormulario(p.getId(), comboProveedorDireccion, txtProveedorDireccion, txtProveedorCp, txtProveedorCiudad, txtProveedorProvincia, txtProveedorPais);
    }

    private void cargarDatosProducto(Producto p) {
        txtProductoCodigo.setText(p.getCodigo());
        txtProductoDescripcion.setText(p.getDescripcion());
        txtProductoReferencia.setText(p.getReferenciaProveedor());
        txtProductoIva.setText(String.valueOf(p.getTipoIVAId()));
        txtProductoPrecioCoste.setText(formatDouble(p.getPrecioCoste()).replace(",", "."));
        txtProductoPrecioVenta.setText(formatDouble(p.getPrecioVenta()).replace(",", "."));
        txtProductoStock.setText(formatDouble(p.getStock()).replace(",", "."));
        
        if (p.getProveedorId() != null) {
            for (Proveedor prov : comboProductoProveedor.getItems()) {
                if (prov.getId() == p.getProveedorId()) { comboProductoProveedor.getSelectionModel().select(prov); break; }
            }
        } else comboProductoProveedor.getSelectionModel().clearSelection();
    }
    
    private void cargarDatosFactura(Factura f) {
        txtFacturaNumero.setText(f.getNumero());
        txtFacturaFecha.setText(f.getFechaEmision().toString());
        txtFacturaConcepto.setText(f.getConcepto());
        txtFacturaBase.setText(formatDouble(f.getBaseImponible()));
        txtFacturaIvaTotal.setText(formatDouble(f.getIvaTotal()));
        txtFacturaTotal.setText(formatDouble(f.getTotalFactura()));
        txtFacturaObservaciones.setText(f.getObservaciones());
        comboFacturaTipo.setValue(f.getTipo() == 'V' ? "Venta" : "Compra");
        comboFacturaEstado.setValue(f.getEstado());
        
        if (comboFacturaEntidad.getItems() != null) {
            for(Entidad e : comboFacturaEntidad.getItems()) {
                if(e.getId() == f.getEntidadId()) { comboFacturaEntidad.getSelectionModel().select(e); break; }
            }
        }
    }

    private void cargarDireccionEnFormulario(long entidadId, ComboBox<String> combo, TextField dir, TextField cp, TextField ciu, TextField pro, TextField pai) {
        combo.getSelectionModel().clearSelection(); dir.clear(); cp.clear(); ciu.clear(); pro.clear(); pai.clear();
        if (cacheDirecciones != null) {
            for (Direccion d : cacheDirecciones) {
                if (d.getEntidadId() == entidadId) {
                    combo.setValue(d.getEtiqueta()); dir.setText(d.getDireccion()); cp.setText(d.getCp());
                    ciu.setText(d.getCiudad()); pro.setText(d.getProvincia()); pai.setText(d.getPais());
                    break;
                }
            }
        }
    }

    // ========================================================================
    // 10. REFRESCAR Y LIMPIAR
    // ========================================================================

    private void refrescarTodo() {
        if (empresa != null) cacheDirecciones = direccionController.consultarTodosPorEmpresa(empresa.getId());
        else cacheDirecciones = new ArrayList<>();
        refrescarClientes();
        refrescarProveedores();
        refrescarProductos();
        refrescarFacturas();
    }

    private void refrescarClientes() {
        if (empresa != null) {
            cacheDirecciones = direccionController.consultarTodosPorEmpresa(empresa.getId());
            tablaClientes.setItems(FXCollections.observableArrayList(clienteController.consultarTodos(empresa.getId())));
        }
    }

    private void refrescarProveedores() {
        if (empresa != null) {
            cacheDirecciones = direccionController.consultarTodosPorEmpresa(empresa.getId());
            List<Proveedor> lista = proveedorController.consultarTodos(empresa.getId());
            tablaProveedores.setItems(FXCollections.observableArrayList(lista));
            if(comboProductoProveedor != null) comboProductoProveedor.setItems(FXCollections.observableArrayList(lista));
        }
    }

    private void refrescarProductos() {
        if (empresa != null) {
            List<Producto> lista = productoController.consultarTodos(empresa.getId());
            ObservableList<Producto> datos = FXCollections.observableArrayList(lista);
            if(tablaProductos != null) tablaProductos.setItems(datos);
            if(comboFacturaProducto != null) comboFacturaProducto.setItems(datos);
            List<Proveedor> listaProvs = proveedorController.consultarTodos(empresa.getId());
            if(comboProductoProveedor != null) comboProductoProveedor.setItems(FXCollections.observableArrayList(listaProvs));
        }
    }

    private void refrescarFacturas() {
        if (empresa != null) tablaFacturas.setItems(FXCollections.observableArrayList(facturaController.consultarTodas(empresa.getId())));
    }

    private void limpiarCliente() {
        txtClienteNombre.clear(); txtClienteNif.clear(); txtClienteCorreo.clear(); txtClienteTelefono.clear();
        comboClienteDireccion.getSelectionModel().clearSelection(); txtClienteDireccion.clear(); txtClienteCp.clear();
        txtClienteCiudad.clear(); txtClienteProvincia.clear(); txtClientePais.clear();
    }

    private void limpiarProveedor() {
        txtProveedorNombre.clear(); txtProveedorNif.clear(); txtProveedorCorreo.clear(); txtProveedorTelefono.clear();
        comboProveedorDireccion.getSelectionModel().clearSelection(); txtProveedorDireccion.clear(); txtProveedorCp.clear();
        txtProveedorCiudad.clear(); txtProveedorProvincia.clear(); txtProveedorPais.clear();
    }
    
    private void limpiarProducto() {
        txtProductoCodigo.clear(); txtProductoDescripcion.clear(); txtProductoReferencia.clear();
        comboProductoProveedor.getSelectionModel().clearSelection(); txtProductoIva.clear();
        txtProductoPrecioCoste.clear(); txtProductoPrecioVenta.clear(); txtProductoStock.clear();
    }

    private void limpiarFactura() {
        txtFacturaNumero.clear(); txtFacturaFecha.clear(); comboFacturaEntidad.getSelectionModel().clearSelection();
        txtFacturaConcepto.clear(); txtFacturaObservaciones.clear(); comboFacturaEstado.getSelectionModel().clearSelection();
        txtFacturaBase.clear(); txtFacturaIvaTotal.clear(); txtFacturaTotal.clear();
        txtFacturaCantidad.clear(); txtFacturaDescuento.clear(); comboFacturaProducto.getSelectionModel().clearSelection();
        lineasTemporales.clear();
    }

    // ========================================================================
    // 11. VALIDACIONES Y HELPERS
    // ========================================================================

    private boolean validarNIF(String nif) { return nif != null && nif.trim().matches("^[0-9]{7}[A-Za-z]$"); }
    private boolean validarTelefono(String tel) { return tel.matches("\\d{9}"); }
    private boolean validarEmail(String email) { return email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"); }
    private boolean validarCP(String cp) { return cp.matches("\\d{5}"); }

    private boolean campoEsValido(TextField campo, String nombre) {
        if (campo.getText() == null || campo.getText().trim().isEmpty()) {
            mostrarError("Campo '" + nombre + "' obligatorio.");
            return false;
        }
        return true;
    }

    private boolean esDecimalValido(TextField campo, String nombre) {
        try { Double.parseDouble(campo.getText().trim().replace(",", ".")); return true; } 
        catch (NumberFormatException e) { mostrarError("Campo '" + nombre + "' debe ser numérico."); return false; }
    }
    
    private boolean validarCampoObligatorio(TextField tf, String nombre) { return campoEsValido(tf, nombre); }

    private void actualizarStockProductos(char tipoFactura, List<LineaFactura> lineas) {
        for (LineaFactura linea : lineas) {
            Producto p = productoController.consultarPorCodigo(empresa.getId(), obtenerCodigoProducto(linea.getProductoId()));
            if (p != null) {
                double nuevoStock = (tipoFactura == 'V') ? p.getStock() - linea.getCantidad() : p.getStock() + linea.getCantidad();
                p.setStock(nuevoStock);
                productoController.modificar(p);
            }
        }
    }

    private void cargarEntidadesEnFactura(String tipoFactura) {
        if (empresa == null || comboFacturaEntidad == null) return;
        try {
            comboFacturaEntidad.getItems().clear();
            if ("Venta".equals(tipoFactura)) {
                comboFacturaEntidad.getItems().addAll(clienteController.consultarTodos(empresa.getId()));
            } else {
                comboFacturaEntidad.getItems().addAll(proveedorController.consultarTodos(empresa.getId()));
            }
        } catch (Exception e) { System.err.println(e.getMessage()); }
    }

    private String obtenerDireccionCampo(Long entidadId, String campo) {
        if (empresa == null || entidadId == null || cacheDirecciones == null) return "";
        for (Direccion d : cacheDirecciones) {
            if (entidadId.equals(d.getEntidadId())) {
                switch (campo) {
                    case "direccion": return safeStr(d.getDireccion());
                    case "ciudad": return safeStr(d.getCiudad());
                    case "cp": return safeStr(d.getCp());
                    case "provincia": return safeStr(d.getProvincia());
                    case "pais": return safeStr(d.getPais());
                    default: return "";
                }
            }
        }
        return "";
    }

    private void añadirDireccionSiRellenada(Long entidadId, ComboBox<String> combo, TextField dir, TextField cp, TextField ciu, TextField pro, TextField pai) {
        if (entidadId <= 0 || safe(dir).isEmpty()) return;
        Direccion d = new Direccion();
        d.setEntidadId(entidadId);
        d.setEtiqueta(combo.getValue() != null ? combo.getValue() : "Fiscal");
        d.setDireccion(safe(dir)); d.setCp(safe(cp)); d.setCiudad(safe(ciu));
        d.setProvincia(safe(pro)); d.setPais(safe(pai));
        direccionController.añadir(d);
    }
    
    // Método para actualizar dirección al modificar, buscando si ya existe para usar su ID
    private void modificarDireccionSiExiste(long entidadId, ComboBox<String> combo, TextField dir, TextField cp, TextField ciu, TextField pro, TextField pai) {
        if (entidadId <= 0) return;
        Direccion existente = null;
        if (cacheDirecciones != null) {
            for (Direccion d : cacheDirecciones) {
                if (d.getEntidadId() == entidadId) { existente = d; break; }
            }
        }
        if (existente != null) {
            if (safe(dir).isEmpty()) {
                direccionController.borrarPorId(existente.getId());
            } else {
                existente.setEtiqueta(combo.getValue() != null ? combo.getValue() : "Fiscal");
                existente.setDireccion(safe(dir)); existente.setCp(safe(cp));
                existente.setCiudad(safe(ciu)); existente.setProvincia(safe(pro)); existente.setPais(safe(pai));
                direccionController.modificar(existente);
            }
        } else {
            if (!safe(dir).isEmpty()) añadirDireccionSiRellenada(entidadId, combo, dir, cp, ciu, pro, pai);
        }
    }

    // Verifica si un NIF existe en la base de datos (para añadir)
    private boolean existeEntidadConNif(long empresaId, String nif) {
        List<Entidad> entidades = entidadController.consultarTodos(empresaId);
        for (Entidad e : entidades) { if (e.getNif().equalsIgnoreCase(nif)) return true; }
        return false;
    }
    
    // Verifica si un NIF existe ignorando el ID propio (para modificar)
    private boolean existeNifDuplicado(long empresaId, String nif, long idPropio) {
        List<Entidad> entidades = entidadController.consultarTodos(empresaId);
        for (Entidad e : entidades) {
            if (e.getNif().equalsIgnoreCase(nif) && e.getId() != idPropio) return true;
        }
        return false;
    }
    
    // Comprueba si el teléfono ya existe en la empresa (Para AÑADIR)
    private boolean existeEntidadConTelefono(long empresaId, String telefono) {
        List<Entidad> entidades = entidadController.consultarTodos(empresaId);
        for (Entidad e : entidades) {
            if (e.getTelefono() != null && e.getTelefono().equals(telefono)) {
                return true; // Duplicado encontrado
            }
        }
        return false;
    }

    // Comprueba si el teléfono existe en OTRA entidad distinta a la que editamos (Para MODIFICAR)
    private boolean existeTelefonoDuplicado(long empresaId, String telefono, long idPropio) {
        List<Entidad> entidades = entidadController.consultarTodos(empresaId);
        for (Entidad e : entidades) {
            // Si el teléfono coincide Y el ID no es el mío -> Es de otro cliente
            if (e.getTelefono() != null && e.getTelefono().equals(telefono) && e.getId() != idPropio) {
                return true;
            }
        }
        return false;
    }

    private String obtenerCodigoProducto(long id) {
        for(Producto p : tablaProductos.getItems()) if(p.getId() == id) return p.getCodigo();
        return "";
    }
    
    private String obtenerNombreProducto(long productoId) {
        if (tablaProductos != null && tablaProductos.getItems() != null) {
            for (Producto p : tablaProductos.getItems()) if (p.getId() == productoId) return p.getCodigo() + " - " + p.getDescripcion();
        }
        return "ID: " + productoId;
    }

    private String safe(TextField tf) { return tf == null ? "" : tf.getText().trim(); }
    private String safeStr(String s) { return s == null ? "" : s; }
    private String safeStr(Long l) { return l == null ? "" : l.toString(); }
    private int parseIntSafe(TextField tf, int def) { try { return Integer.parseInt(tf.getText().trim()); } catch (Exception e) { return def; } }
    private double parseDoubleSafe(TextField tf, double def) { try { return Double.parseDouble(tf.getText().trim().replace(",", ".")); } catch (Exception e) { return def; } }
    private String formatDouble(Double d) { return d == null ? "" : String.format("%.2f", d); }
    private char mapTipoFactura(String s) { return "Venta".equalsIgnoreCase(s) ? 'V' : 'C'; }
    
    private void mostrarError(String msg) { Alert a = new Alert(AlertType.ERROR); a.setTitle("Error"); a.setHeaderText(null); a.setContentText(msg); a.showAndWait(); }
    private void mostrarInfo(String msg) { Alert a = new Alert(AlertType.INFORMATION); a.setTitle("Info"); a.setHeaderText(null); a.setContentText(msg); a.showAndWait(); }
    private boolean confirmar(String msg) { Alert a = new Alert(AlertType.CONFIRMATION); a.setTitle("Confirmar"); a.setHeaderText(null); a.setContentText(msg); return a.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK; }
    
    private void volverAListaEmpresas() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/luis/gestorfacturasempresa/abrirListaEmpresas.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Lista de Empresas");
            stage.setScene(new Scene(root));
            stage.show();
            Stage actual = (Stage) retroceder.getScene().getWindow();
            actual.close();
        } catch (IOException ex) { ex.printStackTrace(); }
    }
}