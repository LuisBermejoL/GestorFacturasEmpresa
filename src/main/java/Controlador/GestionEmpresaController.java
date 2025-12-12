package Controlador;

// --- IMPORTACIONES ---
// Importamos todas las clases del modelo de datos (Cliente, Proveedor, Factura, etc.)
import Modelo.*;
// Clases necesarias para manejo de archivos y SQL
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
// Clases de JavaFX para la interfaz gráfica
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * Controlador Principal: GESTIÓN DE EMPRESA.
 * * Esta clase es el "cerebro" de la aplicación una vez que se ha abierto una empresa.
 * * Responsabilidades principales:
 * - Gestionar la interacción del usuario con las 4 pestañas principales:
 * - Cliente: CRUD de clientes.
 * - Proveedor: CRUD de proveedores.
 * - Producto: CRUD de productos y control de stock básico.
 * - Facturas: Creación, modificación y generación de PDF de facturas.
 * - Validar la integridad de los datos antes de enviarlos a la base de datos.
 * - Optimizar el rendimiento visual mediante caché de direcciones (evita consultas SQL repetitivas).
 * - Coordinar la lógica de negocio compleja, como el cálculo de totales de factura o la actualización de stock.
 * * @author luisb
 */
public class GestionEmpresaController {

    // ========================================================================
    // 1. VARIABLES GLOBALES Y DE ESTADO
    // ========================================================================
    
    // Almacena la empresa sobre la que estamos trabajando actualmente.
    // Se recibe desde el controlador anterior (AbrirEmpresaController).
    private Empresa empresa; 
    
    // Bandera para saber si la interfaz ya se ha cargado completamente.
    private boolean inicializado = false; 
    
    /**
     * CACHÉ DE DIRECCIONES (OPTIMIZACIÓN DE RENDIMIENTO)
     * * Problema: Las tablas de Clientes y Proveedores muestran la dirección, ciudad, etc.
     * Si hacemos una consulta SQL por cada celda para obtener la dirección, la tabla se vuelve lenta.
     * * Solución: Cargamos TODAS las direcciones de la empresa en esta lista en memoria (RAM).
     * Cuando la tabla necesita mostrar un dato, lo busca aquí en milisegundos en lugar de ir a la BD.
     */
    private List<Direccion> cacheDirecciones = new ArrayList<>();

    // --- INSTANCIAS DE CONTROLADORES (CAPA DE NEGOCIO) ---
    // Estos objetos nos permiten comunicarnos con la base de datos (DAOs) de forma organizada.
    private final EntidadController entidadController = new EntidadController();
    private final ClienteController clienteController = new ClienteController();
    private final ProveedorController proveedorController = new ProveedorController();
    private final ProductoController productoController = new ProductoController();
    private final FacturaController facturaController = new FacturaController();
    private final LineaFacturaController lineaFacturaController = new LineaFacturaController(); // Gestión de líneas de factura
    private final DireccionController direccionController = new DireccionController();

    // ========================================================================
    // 2. ELEMENTOS DE LA INTERFAZ (FXML)
    // ========================================================================
    // Estos campos se inyectan automáticamente desde el archivo gestionEmpresa.fxml

    @FXML private TabPane MenuCliente; // Panel de pestañas principales
    @FXML private Label nombreEmpresa; // Etiqueta superior con el nombre de la empresa
    @FXML private ImageView retroceder; // Icono de flecha para volver al menú anterior

    // --- PESTAÑA CLIENTES ---
    @FXML private TableView<Cliente> tablaClientes;
    // Columnas de la tabla de clientes
    @FXML private TableColumn<Cliente, String> colClienteCodigo, colClienteNombre, colClienteNif, colClienteCorreo, colClienteTelefono;
    @FXML private TableColumn<Cliente, String> colClienteDireccionFiscal, colClienteDireccionEnvio;
    
    // Campos de formulario para editar/crear clientes
    @FXML private TextField txtClienteNombre, txtClienteNif, txtClienteCorreo, txtClienteTelefono;
    
    // NUEVOS CAMPOS DE DIRECCIÓN (FISCAL Y ENVÍO)
    @FXML private TextField txtClienteDireccionFiscal, txtClienteCpFiscal, txtClienteCiudadFiscal, txtClienteProvinciaFiscal, txtClientePaisFiscal;
    @FXML private TextField txtClienteDireccionEnvio, txtClienteCpEnvio, txtClienteCiudadEnvio, txtClienteProvinciaEnvio, txtClientePaisEnvio;

    // --- PESTAÑA PROVEEDORES ---
    @FXML private TableView<Proveedor> tablaProveedores;
    @FXML private TableColumn<Proveedor, String> colProveedorCodigo, colProveedorNombre, colProveedorNif, colProveedorCorreo, colProveedorTelefono;
    @FXML private TableColumn<Proveedor, String> colProveedorDireccionFiscal, colProveedorDireccionEnvio;

    @FXML private TextField txtProveedorNombre, txtProveedorNif, txtProveedorCorreo, txtProveedorTelefono;
    
    // NUEVOS CAMPOS DE DIRECCIÓN (FISCAL Y ENVÍO)
    @FXML private TextField txtProveedorDireccionFiscal, txtProveedorCpFiscal, txtProveedorCiudadFiscal, txtProveedorProvinciaFiscal, txtProveedorPaisFiscal;
    @FXML private TextField txtProveedorDireccionEnvio, txtProveedorCpEnvio, txtProveedorCiudadEnvio, txtProveedorProvinciaEnvio, txtProveedorPaisEnvio;

    // --- PESTAÑA PRODUCTOS ---
    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, String> colProductoCodigo, colProductoDescripcion, colProductoReferencia, colProductoProveedor, colProductoPrecioVenta, colProductoStock;

    @FXML private TextField txtProductoCodigo, txtProductoDescripcion, txtProductoReferencia;
    @FXML private ComboBox<Proveedor> comboProductoProveedor; // Selector desplegable de proveedor
    @FXML private TextField txtProductoPrecioVenta, txtProductoStock;

    // --- PESTAÑA FACTURAS (CABECERA) ---
    @FXML private TableView<Factura> tablaFacturas;
    @FXML private TableColumn<Factura, String> colFacturaEntidadId, colFacturaTipo, colFacturaNumero, colFacturaFecha, colFacturaConcepto,
            colFacturaBase, colFacturaIvaTotal, colFacturaTotal, colFacturaEstado, colFacturaObservaciones;

    @FXML private ComboBox<Entidad> comboFacturaEntidad; // Selector inteligente: Carga Clientes si es Venta, Proveedores si es Compra
    @FXML private TextField txtFacturaNumero, txtFacturaConcepto, txtFacturaObservaciones;
    @FXML private ComboBox<String> comboFacturaTipo, comboFacturaIva, comboFacturaEstado;
    @FXML private DatePicker datePickerFacturaFecha;

    // --- PESTAÑA FACTURAS (LÍNEAS/DETALLES) ---
    // Elementos para añadir productos individuales a la factura
    @FXML private ComboBox<Producto> comboFacturaProducto;
    @FXML private TextField txtFacturaCantidad;
    @FXML private TextField txtFacturaDescuento;
    
    // Tabla pequeña que muestra las líneas temporales antes de guardar la factura
    @FXML private TableView<LineaFactura> tablaLineasFactura;
    @FXML private TableColumn<LineaFactura, String> colLineaProducto, colLineaCantidad, colLineaPrecio, colLineaTotal;
    
    // Botones específicos
    @FXML private Button btnImprimirFactura;
    @FXML private Button btnLimpiar;

    /**
     * Lista Observable para las líneas de factura.
     * Actúa como un "carrito de la compra". Los datos están en memoria RAM hasta que se pulsa "Guardar".
     * Al ser ObservableList, si añadimos algo aquí, la tabla visual se actualiza automáticamente.
     */
    private ObservableList<LineaFactura> lineasTemporales = FXCollections.observableArrayList();

    // ========================================================================
    // 3. INICIALIZACIÓN Y CONFIGURACIÓN
    // ========================================================================

    /**
     * Método llamado automáticamente por JavaFX al cargar la vista.
     * Aquí configuramos el estado inicial de todos los componentes.
     */
    @FXML
    private void initialize() {
        // 1. Configuración de elementos estáticos (Listas fijas)
        if(comboFacturaTipo != null) comboFacturaTipo.setItems(FXCollections.observableArrayList("Venta", "Compra"));
        if(comboFacturaEstado != null) comboFacturaEstado.setItems(FXCollections.observableArrayList("PENDIENTE", "PAGADA", "ANULADA"));
        if(comboFacturaIva != null) comboFacturaIva.setItems(FXCollections.observableArrayList("4%", "10%", "21%"));
        
        // Asignamos la acción a los botones especiales
        if (btnImprimirFactura != null) btnImprimirFactura.setOnAction(e -> handleImprimirFactura());
        if (btnLimpiar != null) btnLimpiar.setOnAction(e -> handleLimpiar());

        // 2. Configurar botón de volver atrás
        if (retroceder != null) retroceder.setOnMouseClicked(event -> volverAListaEmpresas());

        // 3. Configurar Tablas (Binding)
        configurarTablas();

        // 4. Configurar Visualización de Combos
        configurarConvertidoresCombos();

        // 5. Configurar la tabla pequeña de líneas de factura
        configurarTablaLineas();

        // 6. Listener Dinámico para Tipo de Factura
        if (comboFacturaTipo != null) {
            comboFacturaTipo.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) cargarEntidadesEnFactura(newVal);
            });
        }

        // 7. LISTENERS DE SELECCIÓN (AUTO-RELLENADO)
        if(tablaClientes != null) tablaClientes.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> { if(n!=null) cargarDatosCliente(n); });
        if(tablaProveedores != null) tablaProveedores.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> { if(n!=null) cargarDatosProveedor(n); });
        if(tablaProductos != null) tablaProductos.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> { if(n!=null) cargarDatosProducto(n); });
        if(tablaFacturas != null) tablaFacturas.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> { if(n!=null) cargarDatosFactura(n); });

        // 8. Finalizar carga
        inicializado = true;
        
        // Pre-selección inicial
        if (comboFacturaTipo != null) comboFacturaTipo.getSelectionModel().select("Venta");
    }

    /**
     * Recibe el objeto Empresa desde la ventana anterior.
     * Carga los datos iniciales de la base de datos.
     */
    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
        if (nombreEmpresa != null) {
            nombreEmpresa.setText(empresa != null ? empresa.getNombre() + " (" + empresa.getNif() + ")" : "Error");
        }
        if (inicializado && empresa != null) {
            refrescarTodo(); // Carga inicial masiva de datos
        }
    }

    /**
     * Configura las columnas de las tablas principales.
     * Utiliza expresiones lambda para extraer los valores de los objetos.
     * Para direcciones, consulta la caché en memoria para ser más rápido.
     */
    private void configurarTablas() {
        // --- CLIENTES ---
        colClienteCodigo.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getCodigo())));
        colClienteNombre.setCellValueFactory(c -> new SimpleStringProperty(safeStr(c.getValue().getNombre())));
        colClienteNif.setCellValueFactory(c -> new SimpleStringProperty(safeStr(c.getValue().getNif())));
        colClienteCorreo.setCellValueFactory(c -> new SimpleStringProperty(safeStr(c.getValue().getEmail())));
        colClienteTelefono.setCellValueFactory(c -> new SimpleStringProperty(safeStr(c.getValue().getTelefono())));
        
        // CORRECCIÓN AQUÍ: Usar 'obtenerDireccionFormateada' y especificar el tipo
        // Asegúrate de que el nombre de la columna coincide con tu @FXML (colClienteFiscal o colClienteDireccionFiscal)
        colClienteDireccionFiscal.setCellValueFactory(c -> new SimpleStringProperty(obtenerDireccionFormateada(c.getValue().getId(), "Fiscal")));
        colClienteDireccionEnvio.setCellValueFactory(c -> new SimpleStringProperty(obtenerDireccionFormateada(c.getValue().getId(), "Envio")));

        // --- PROVEEDORES ---
        colProveedorCodigo.setCellValueFactory(p -> new SimpleStringProperty(String.valueOf(p.getValue().getCodigo())));
        colProveedorNombre.setCellValueFactory(p -> new SimpleStringProperty(safeStr(p.getValue().getNombre())));
        colProveedorNif.setCellValueFactory(p -> new SimpleStringProperty(safeStr(p.getValue().getNif())));
        colProveedorCorreo.setCellValueFactory(p -> new SimpleStringProperty(safeStr(p.getValue().getEmail())));
        colProveedorTelefono.setCellValueFactory(p -> new SimpleStringProperty(safeStr(p.getValue().getTelefono())));
        
        // CORRECCIÓN AQUÍ TAMBIÉN
        colProveedorDireccionFiscal.setCellValueFactory(p -> new SimpleStringProperty(obtenerDireccionFormateada(p.getValue().getId(), "Fiscal")));
        colProveedorDireccionEnvio.setCellValueFactory(p -> new SimpleStringProperty(obtenerDireccionFormateada(p.getValue().getId(), "Envio")));

        // --- PRODUCTOS (Igual que antes) ---
        colProductoCodigo.setCellValueFactory(p -> new SimpleStringProperty(safeStr(p.getValue().getCodigo())));
        colProductoDescripcion.setCellValueFactory(p -> new SimpleStringProperty(safeStr(p.getValue().getDescripcion())));
        colProductoProveedor.setCellValueFactory(p -> new SimpleStringProperty(safeStr(p.getValue().getProveedorId())));
        colProductoPrecioVenta.setCellValueFactory(p -> new SimpleStringProperty(formatDouble(p.getValue().getPrecioVenta())));
        colProductoStock.setCellValueFactory(p -> new SimpleStringProperty(formatDouble(p.getValue().getStock())));

        // --- FACTURAS (Igual que antes) ---
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

    /**
     * Define cómo se muestran los objetos complejos (Entidad, Producto) dentro de los desplegables.
     * Sin esto, Java mostraría algo feo como "Modelo.Cliente@1a2b3c".
     */
    private void configurarConvertidoresCombos() {
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
    
    /**
     * Configura la tabla temporal de líneas de factura.
     * Esta tabla no lee de base de datos directamente, sino de la lista 'lineasTemporales'.
     */
    private void configurarTablaLineas() {
        if(colLineaProducto != null) colLineaProducto.setCellValueFactory(cell -> new SimpleStringProperty(obtenerNombreProducto(cell.getValue().getProductoId())));
        if(colLineaCantidad != null) colLineaCantidad.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getCantidad())));
        if(colLineaPrecio != null) colLineaPrecio.setCellValueFactory(cell -> new SimpleStringProperty(String.format("%.2f", cell.getValue().getPrecioUnitario())));
        // Calculamos el total de la línea al vuelo (Cantidad * Precio - Descuento)
        if(colLineaTotal != null) colLineaTotal.setCellValueFactory(cell -> new SimpleStringProperty(String.format("%.2f", cell.getValue().getTotalLinea())));
        
        // ENLACE CRÍTICO: Conectamos la lista en memoria con la tabla visual
        if(tablaLineasFactura != null) tablaLineasFactura.setItems(lineasTemporales);
    }

    // ========================================================================
    // 4. GESTIÓN DE EVENTOS (BOTONES PRINCIPALES)
    // ========================================================================
    
    // Estos métodos detectan qué pestaña está activa (Cliente, Proveedor, etc.)
    // y redirigen la acción al método específico correspondiente.
    
    @FXML
    private void handleAñadir() {
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
        else if ("Facturas".equals(tabName)) modificarFactura();
    }

    @FXML
    private void handleConsultar() {
        if (empresa == null) return;

        String tabName = MenuCliente.getSelectionModel().getSelectedItem().getText();

        if (MenuCliente.getSelectionModel().getSelectedItem().getText().equals("Producto")) {
            consultarProductosConFiltro();
        }
        
        if ("Cliente".equals(tabName)) {
            consultarClientesConFiltro();
        } 
        else if ("Proveedor".equals(tabName)) {
            consultarProveedoresConFiltro();
        } 
        else if ("Producto".equals(tabName)) {
            consultarProductosConFiltro();
        } 
        else if ("Facturas".equals(tabName)) {
            consultarFacturasConFiltro();
        }
    }

    // --- MÉTODOS DE FILTRADO ESPECÍFICOS ---

    private void consultarClientesConFiltro() {
        String filtroNombre = safe(txtClienteNombre);
        String filtroNif = safe(txtClienteNif);
        
        List<Cliente> resultados = clienteController.consultarClientes(empresa.getId(), filtroNombre, filtroNif);
        tablaClientes.setItems(FXCollections.observableArrayList(resultados));
        
        if (resultados.isEmpty()) mostrarInfo("No se encontraron clientes.");
    }

    private void consultarProveedoresConFiltro() {
        String filtroNombre = safe(txtProveedorNombre);
        String filtroNif = safe(txtProveedorNif);
        
        List<Proveedor> resultados = proveedorController.consultarProveedores(empresa.getId(), filtroNombre, filtroNif);
        tablaProveedores.setItems(FXCollections.observableArrayList(resultados));
        
        if (resultados.isEmpty()) mostrarInfo("No se encontraron proveedores.");
    }

    private void consultarProductosConFiltro() {
        if (empresa == null) {
            mostrarError("No hay empresa activa.");
            return;
        }

        // Obtener el proveedor seleccionado
        Proveedor proveedorSeleccionado = comboProductoProveedor.getSelectionModel().getSelectedItem();

        List<Producto> resultados;

        if (proveedorSeleccionado != null) {
            // Filtrar solo productos de ese proveedor
            resultados = new ArrayList<>();
            for (Producto p : productoController.consultarTodos(empresa.getId())) {
                if (p.getProveedorId() == proveedorSeleccionado.getId()) {
                    resultados.add(p);
                }
            }
        } else {
            // Si no hay proveedor seleccionado → mostrar todos
            resultados = productoController.consultarTodos(empresa.getId());
        }

        // Actualizar la tabla
        tablaProductos.setItems(FXCollections.observableArrayList(resultados));
    }

    private void consultarFacturasConFiltro() {
        // Filtramos solo por el campo NÚMERO DE FACTURA
        String filtroNumero = safe(txtFacturaNumero);
        
        List<Factura> resultados = facturaController.consultarFacturas(empresa.getId(), filtroNumero);
        tablaFacturas.setItems(FXCollections.observableArrayList(resultados));
        
        if (resultados.isEmpty()) mostrarInfo("No se encontraron facturas con ese número.");
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
    private void handleLimpiar() {
        // Detecta qué pestaña está activa
        String tabName = MenuCliente.getSelectionModel().getSelectedItem().getText();

        if ("Cliente".equals(tabName)) {
            limpiarCliente();
            tablaClientes.getSelectionModel().clearSelection(); // Deseleccionar tabla
        } 
        else if ("Proveedor".equals(tabName)) {
            limpiarProveedor();
            tablaProveedores.getSelectionModel().clearSelection();
        } 
        else if ("Producto".equals(tabName)) {
            limpiarProducto();
            tablaProductos.getSelectionModel().clearSelection();
        } 
        else if ("Facturas".equals(tabName)) {
            limpiarFactura();
            tablaFacturas.getSelectionModel().clearSelection();
            // Resetear cosas específicas de factura
            txtFacturaNumero.clear();
            datePickerFacturaFecha.setValue(null);
        }
        
        mostrarInfo("Formulario limpiado.");
    }
    
    /**
     * Maneja la generación del PDF de la factura seleccionada usando JasperReports.
     */
    @FXML
    private void handleImprimirFactura() {
        // 1. Validamos que haya una factura seleccionada y guardada
        Factura facturaSel = tablaFacturas.getSelectionModel().getSelectedItem();

        if (facturaSel == null) {
            mostrarError("Selecciona una factura de la tabla para imprimir.");
            return;
        }

        if (facturaSel.getId() == 0) {
            mostrarError("Esta factura aún no se ha guardado en la base de datos.");
            return;
        }

        // 2. Abrimos un selector de archivos para que el usuario elija dónde guardar
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Guardar Factura PDF");

        // Nombre por defecto sugerido: Factura_Nº.pdf (limpiando caracteres raros)
        String nombreArchivo = "Factura_" + facturaSel.getNumero() + ".pdf";
        nombreArchivo = nombreArchivo.replaceAll("[\\\\/:*?\"<>|]", "_"); 
        fileChooser.setInitialFileName(nombreArchivo);

        fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));

        java.io.File file = fileChooser.showSaveDialog(null);

        if (file != null) {
        // 3. Delegamos la creación del PDF al gestor especializado
            GestorReportes gestor = new GestorReportes();
            gestor.generarFacturaPdf(facturaSel.getId(), empresa.getId(), file.getAbsolutePath());
        }
    }

    // ========================================================================
    // 5. LÓGICA CRUD: CLIENTES
    // ========================================================================

    private void añadirCliente() {
        if (empresa == null) { mostrarError("No hay empresa activa."); return; }

        // Validaciones de campos obligatorios
        if (!campoEsValido(txtClienteNombre, "Nombre")) return;
        if (!campoEsValido(txtClienteNif, "NIF")) return;
        if (!campoEsValido(txtClienteTelefono, "Teléfono")) return;
        if (!campoEsValido(txtClienteCorreo, "Correo")) return;
        if (!campoEsValido(txtClienteDireccionFiscal, "Dirección Fiscal")) return;
        if (!campoEsValido(txtClienteCpFiscal, "Código Postal Fiscal")) return;
        if (!campoEsValido(txtClienteCiudadFiscal, "Ciudad Fiscal")) return;
        if (!campoEsValido(txtClientePaisFiscal, "País Fiscal")) return;
        if (!campoEsValido(txtClienteDireccionEnvio, "Dirección de Envío")) return;
        if (!campoEsValido(txtClienteCpEnvio, "Código Postal de Envío")) return;
        if (!campoEsValido(txtClienteCiudadEnvio, "Ciudad de Envío")) return;
        if (!campoEsValido(txtClientePaisEnvio, "País de Envío")) return;

        String nif = txtClienteNif.getText().trim().toUpperCase();
        String telefono = txtClienteTelefono.getText().trim();
        String email = txtClienteCorreo.getText().trim();
        String cpFiscal = txtClienteCpFiscal.getText().trim();
        String cpEnvio = txtClienteCpEnvio.getText().trim();

        // Validaciones y control de duplicados (excluyendo al propio cliente)
        if (!validarNIF(nif)) {
            mostrarError("NIF incorrecto.\n\nDebe tener 8 caracteres numéricos y una letra.\nEjemplos:\n• 12345678Z");
            return;
        }

        if (!validarTelefono(telefono)) {
            mostrarError("Teléfono inválido.\nDebe tener 9-15 dígitos, ignora el '+', espacios y guiones.\nEjemplos:\n• 612345678\n• 612 345 678\n• +34 612 345 678\n• 612-345-678");
            return;
        }

        if (!validarEmail(email)) {
            mostrarError("Correo electrónico inválido.\nEjemplo correcto: cliente@dominio.com");
            return;
        }

        if (!validarCP(cpFiscal)) {
            mostrarError("Código Postal inválido.\n\nDebe tener 3-10 caracteres alfanuméricos.\nEjemplos:\n• 28001\n• SW1A 1AA");
            return;
        }
        
        if (!validarCP(cpEnvio)) {
            mostrarError("Código Postal inválido.\n\nDebe tener 3-10 caracteres alfanuméricos.\nEjemplos:\n• 28001\n• SW1A 1AA");
            return;
        }
        // Validaciones de negocio (Duplicados)
        if (existeEntidadConNif(empresa.getId(), nif)) { mostrarError("Ya existe un cliente con ese NIF."); return; }
        if (existeEntidadConTelefono(empresa.getId(), telefono)) { mostrarError("Ya existe un cliente con ese Teléfono."); return; }

        try {
            Cliente c = new Cliente();
            c.setNombre(txtClienteNombre.getText().trim());
            c.setNif(txtClienteNif.getText().trim());
            c.setEmail(txtClienteCorreo.getText().trim());
            c.setTelefono(txtClienteTelefono.getText().trim());
            c.setCodigo(0);

            long entidadId = clienteController.añadir(c, empresa.getId());
            c.setId(entidadId);

            if (entidadId > 0) {
                // GUARDAR FISCAL
                guardarOActualizarDireccion(entidadId, "Fiscal", 
                    txtClienteDireccionFiscal, txtClienteCpFiscal, txtClienteCiudadFiscal, txtClienteProvinciaFiscal, txtClientePaisFiscal);
                
                // GUARDAR ENVÍO
                guardarOActualizarDireccion(entidadId, "Envio", 
                    txtClienteDireccionEnvio, txtClienteCpEnvio, txtClienteCiudadEnvio, txtClienteProvinciaEnvio, txtClientePaisEnvio);
            }
            
            refrescarClientes();
            limpiarCliente();
            mostrarInfo("Cliente añadido correctamente.");
        } catch (Exception e) { mostrarError("Error: " + e.getMessage()); }
    }

    private void modificarCliente() {
        Cliente sel = tablaClientes.getSelectionModel().getSelectedItem();
        if (sel == null) { mostrarError("Selecciona un cliente."); return; }

        // Campos obligatorios
        if (!campoEsValido(txtClienteNombre, "Nombre")) return;
        if (!campoEsValido(txtClienteNif, "NIF")) return;
        if (!campoEsValido(txtClienteTelefono, "Teléfono")) return;
        if (!campoEsValido(txtClienteCorreo, "Correo")) return;
        if (!campoEsValido(txtClienteDireccionFiscal, "Dirección Fiscal")) return;
        if (!campoEsValido(txtClienteCpFiscal, "Código Postal Fiscal")) return;
        if (!campoEsValido(txtClienteCiudadFiscal, "Ciudad Fiscal")) return;
        if (!campoEsValido(txtClientePaisFiscal, "País Fiscal")) return;
        if (!campoEsValido(txtClienteDireccionEnvio, "Dirección de Envío")) return;
        if (!campoEsValido(txtClienteCpEnvio, "Código Postal de Envío")) return;
        if (!campoEsValido(txtClienteCiudadEnvio, "Ciudad de Envío")) return;
        if (!campoEsValido(txtClientePaisEnvio, "País de Envío")) return;
        
        
        String nif = txtClienteNif.getText().trim().toUpperCase();
        String telefono = txtClienteTelefono.getText().trim();
        String email = txtClienteCorreo.getText().trim();
        String cpFiscal = txtClienteCpFiscal.getText().trim();
        String cpEnvio = txtClienteCpEnvio.getText().trim();

        // Validaciones y control de duplicados (excluyendo al propio cliente)
        if (!validarNIF(nif)) {
            mostrarError("NIF incorrecto.\n\nDebe tener 8 caracteres numéricos y una letra.\nEjemplos:\n• 12345678Z");
            return;
        }

        if (!validarTelefono(telefono)) {
            mostrarError("Teléfono inválido.\nDebe tener 9-15 dígitos, ignora el '+', espacios y guiones.\nEjemplos:\n• 612345678\n• 612 345 678\n• +34 612 345 678\n• 612-345-678");
            return;
        }

        if (!validarEmail(email)) {
            mostrarError("Correo electrónico inválido.\nEjemplo correcto: cliente@dominio.com");
            return;
        }

        if (!validarCP(cpFiscal)) {
            mostrarError("Código Postal inválido.\n\n3-10 caracteres alfanuméricos.\nEjemplos:\n• 28001\n• SW1A 1AA");
            return;
        }
        
        if (!validarCP(cpEnvio)) {
            mostrarError("Código Postal inválido.\n\n3-10 caracteres alfanuméricos.\nEjemplos:\n• 28001\n• SW1A 1AA");
            return;
        }
        
        // === VALIDACIONES DE DUPLICADOS (excluyendo al propio) ===
        if (existeNifDuplicado(empresa.getId(), nif, sel.getId())) {
            mostrarError("Ya existe otro cliente o proveedor con ese NIF/NIE.");
            return;
        }
        if (existeTelefonoDuplicado(empresa.getId(), telefono, sel.getId())) {
            mostrarError("Ya existe otro cliente o proveedor con ese teléfono.");
            return;
        }
    
        // Actualización de objeto
        sel.setNombre(txtClienteNombre.getText().trim());
        sel.setNif(nif);
        sel.setEmail(txtClienteCorreo.getText().trim());
        sel.setTelefono(telefono);

        try {
            clienteController.modificar(sel);
            
            // ACTUALIZAR FISCAL
            guardarOActualizarDireccion(sel.getId(), "Fiscal", 
                txtClienteDireccionFiscal, txtClienteCpFiscal, txtClienteCiudadFiscal, txtClienteProvinciaFiscal, txtClientePaisFiscal);
            
            // ACTUALIZAR ENVÍO
            guardarOActualizarDireccion(sel.getId(), "Envio", 
                txtClienteDireccionEnvio, txtClienteCpEnvio, txtClienteCiudadEnvio, txtClienteProvinciaEnvio, txtClientePaisEnvio);

            refrescarClientes();
            limpiarCliente();
            mostrarInfo("Cliente modificado correctamente.");
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
    // (Lógica muy similar a Clientes, ya que ambos heredan de Entidad)
    // ========================================================================

    private void añadirProveedor() {
        if (empresa == null) { mostrarError("No hay empresa activa."); return; }

        if (!campoEsValido(txtProveedorNombre, "Nombre")) return;
        if (!campoEsValido(txtProveedorNif, "NIF")) return;
        if (!campoEsValido(txtProveedorTelefono, "Teléfono")) return;
        if (!campoEsValido(txtProveedorCorreo, "Correo")) return;
        if (!campoEsValido(txtProveedorDireccionFiscal, "Dirección Fiscal")) return;
        if (!campoEsValido(txtProveedorCpFiscal, "Código Postal Fiscal")) return;
        if (!campoEsValido(txtProveedorCiudadFiscal, "Ciudad Fiscal")) return;
        if (!campoEsValido(txtProveedorPaisFiscal, "País Fiscal")) return;

        String nif = txtProveedorNif.getText().trim().toUpperCase();
        String telefono = txtProveedorTelefono.getText().trim();
        String email = txtProveedorCorreo.getText().trim();
        String cpFiscal = txtProveedorCpFiscal.getText().trim();
        
        // Validaciones y control de duplicados (excluyendo al propio proveedor)
        if (!validarNIF(nif)) {
            mostrarError("NIF incorrecto.\n\nDebe tener 8 caracteres numéricos y una letra.\nEjemplos:\n• 12345678Z");
            return;
        }

        if (!validarTelefono(telefono)) {
            mostrarError("Teléfono inválido.\nDebe tener 9-15 dígitos, ignora el '+', espacios y guiones.\nEjemplos:\n• 612345678\n• 612 345 678\n• +34 612 345 678\n• 612-345-678");
            return;
        }

        if (!validarEmail(email)) {
            mostrarError("Correo electrónico inválido.\nEjemplo correcto: cliente@dominio.com");
            return;
        }

        if (!validarCP(cpFiscal)) {
            mostrarError("Código Postal inválido.\n\n3-10 caracteres alfanuméricos.\nEjemplos:\n• 28001\n• SW1A 1AA");
            return;
        }

        if (existeEntidadConNif(empresa.getId(), nif)) { mostrarError("NIF duplicado."); return; }
        if (existeEntidadConTelefono(empresa.getId(), telefono)) { mostrarError("Teléfono duplicado."); return; }

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
                // GUARDAR FISCAL
                guardarOActualizarDireccion(entidadId, "Fiscal", 
                    txtProveedorDireccionFiscal, txtProveedorCpFiscal, txtProveedorCiudadFiscal, txtProveedorProvinciaFiscal, txtProveedorPaisFiscal);
                
                // GUARDAR ENVÍO
                guardarOActualizarDireccion(entidadId, "Envio", 
                    txtProveedorDireccionEnvio, txtProveedorCpEnvio, txtProveedorCiudadEnvio, txtProveedorProvinciaEnvio, txtProveedorPaisEnvio);
            }
            refrescarProveedores();
            limpiarProveedor();
            mostrarInfo("Proveedor añadido.");
        } catch (Exception e) { mostrarError(e.getMessage()); }
    }

    private void modificarProveedor() {
        // 1. Obtener proveedor seleccionado
        Proveedor sel = tablaProveedores.getSelectionModel().getSelectedItem();
        if (sel == null) { mostrarError("Selecciona un proveedor de la tabla."); return; }

        // 2. Validaciones de campos
        if (!campoEsValido(txtProveedorNombre, "Nombre")) return;
        if (!campoEsValido(txtProveedorNif, "NIF")) return;
        if (!campoEsValido(txtProveedorTelefono, "Teléfono")) return;
        if (!campoEsValido(txtProveedorCorreo, "Correo")) return;
        if (!campoEsValido(txtProveedorDireccionFiscal, "Dirección Fiscal")) return;
        if (!campoEsValido(txtProveedorCpFiscal, "Código Postal Fiscal")) return;
        if (!campoEsValido(txtProveedorCiudadFiscal, "Ciudad Fiscal")) return;
        if (!campoEsValido(txtProveedorPaisFiscal, "País Fiscal")) return;
        
        String nif = txtProveedorNif.getText().trim().toUpperCase();
        String telefono = txtProveedorTelefono.getText().trim();
        String email = txtProveedorCorreo.getText().trim();
        String cpFiscal = txtProveedorCpFiscal.getText().trim();
        
        // Validaciones y control de duplicados (excluyendo al propio proveedor)
        if (!validarNIF(nif)) {
            mostrarError("NIF incorrecto.\n\nDebe tener 8 caracteres numéricos y una letra.\nEjemplos:\n• 12345678Z");
            return;
        }

        if (!validarTelefono(telefono)) {
            mostrarError("Teléfono inválido.\nDebe tener 9-15 dígitos, ignora el '+', espacios y guiones.\nEjemplos:\n• 612345678\n• 612 345 678\n• +34 612 345 678\n• 612-345-678");
            return;
        }

        if (!validarEmail(email)) {
            mostrarError("Correo electrónico inválido.\nEjemplo correcto: cliente@dominio.com");
            return;
        }

        if (!validarCP(cpFiscal)) {
            mostrarError("Código Postal inválido.\n\n3-10 caracteres alfanuméricos.\nEjemplos:\n• 28001\n• SW1A 1AA");
            return;
        }

        // 3. Validar duplicados (EXCLUYENDO al propio proveedor que editamos)
        if (existeNifDuplicado(empresa.getId(), nif, sel.getId())) { 
            mostrarError("Ya existe otro proveedor o cliente con ese NIF."); 
            return; 
        }
        if (existeTelefonoDuplicado(empresa.getId(), telefono, sel.getId())) { 
            mostrarError("Ya existe otro proveedor o cliente con ese Teléfono."); 
            return; 
        }

        // 4. Actualizar objeto
        sel.setNombre(txtProveedorNombre.getText().trim());
        sel.setNif(nif);
        sel.setEmail(txtProveedorCorreo.getText().trim());
        sel.setTelefono(telefono);

        try {
            // Guardar cambios principales
            proveedorController.modificar(sel);
            
            // 5. Actualizar DIRECCIONES (Fiscal y Envío)
            guardarOActualizarDireccion(sel.getId(), "Fiscal", 
                txtProveedorDireccionFiscal, txtProveedorCpFiscal, txtProveedorCiudadFiscal, txtProveedorProvinciaFiscal, txtProveedorPaisFiscal);
            
            guardarOActualizarDireccion(sel.getId(), "Envio", 
                txtProveedorDireccionEnvio, txtProveedorCpEnvio, txtProveedorCiudadEnvio, txtProveedorProvinciaEnvio, txtProveedorPaisEnvio);

            // Refrescar vista
            refrescarProveedores();
            limpiarProveedor();
            mostrarInfo("Proveedor modificado correctamente.");
        } catch (Exception e) { 
            mostrarError("Error al modificar: " + e.getMessage()); 
        }
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

        // 1. Validaciones de campos
        if (!campoEsValido(txtProductoCodigo, "Código")) return;
        if (!campoEsValido(txtProductoDescripcion, "Descripción")) return;
        if (!campoEsValido(txtProductoPrecioVenta, "Precio de venta")) return;
        if (!campoEsValido(txtProductoStock, "Stock inicial")) return;
        
        String codigoNuevo = txtProductoCodigo.getText().trim();
        String descripcion = txtProductoDescripcion.getText().trim();
        String precioStr = txtProductoPrecioVenta.getText().trim();
        String stockStr = txtProductoStock.getText().trim();
    
        if (!validarCodigoProducto(codigoNuevo)) {
            mostrarError("Código de producto inválido.\n\nMáximo 13 caracteres alfanuméricos.\nEjemplos válidos:\n• ABC123\n• PROD-001\n• LAPTOP2025");
            return;
        }
        
        if (existeProductoConCodigo(codigoNuevo)) {
            mostrarError("Ya existe un producto con el código '" + codigoNuevo + "'.\nPor favor, usa otro código.");
            return;
        }
        
        if (comboProductoProveedor.getValue() == null) {
            mostrarError("Debes seleccionar un Proveedor de la lista.");
            return;
        }

        double precio = 0;
        try {
            precio = Double.parseDouble(precioStr.replace(",", "."));
            if (!validarPrecio(precio)) {
                mostrarError("El precio debe ser mayor que 0.\nEjemplo: 99.99");
                return;
            }
        } catch (NumberFormatException e) {
            mostrarError("Precio inválido. Usa formato numérico.\nEjemplos: 150, 99.95");
            return;
        }

        double stock = 0;
        try {
            stock = Double.parseDouble(stockStr);
            if (!validarStock(stock)) {
                mostrarError("El stock debe ser un número entero positivo o cero.\nEjemplo: 25");
                return;
            }
        } catch (NumberFormatException e) {
            mostrarError("Stock inválido. Debe ser un número entero.\nEjemplo: 10");
            return;
        }

        try {
            Producto p = new Producto();
            p.setEmpresaId(empresa.getId());
            p.setCodigo(codigoNuevo);
            p.setDescripcion(txtProductoDescripcion.getText().trim());
            p.setProveedorId(comboProductoProveedor.getValue().getId());
            p.setPrecioVenta(precio);
            p.setStock(stock);

            productoController.añadir(p, empresa.getId());
            
            refrescarProductos();
            limpiarProducto();
            mostrarInfo("Producto añadido correctamente.");
        } catch (Exception e) { 
            mostrarError("Error al guardar: " + e.getMessage()); 
        }
    }

    private void modificarProducto() {
        Producto sel = tablaProductos.getSelectionModel().getSelectedItem();
        if(sel == null) { mostrarError("Selecciona un producto de la tabla."); return; }

        if (!campoEsValido(txtProductoDescripcion, "Descripción")) {
            return;
        }
        if (!campoEsValido(txtProductoPrecioVenta, "Precio de venta")) {
            return;
        }
        if (!campoEsValido(txtProductoStock, "Stock")) {
            return;
        }
        if (comboProductoProveedor.getSelectionModel().getSelectedItem() == null) {
            mostrarError("Debes seleccionar un proveedor.");
            return;
        }

        String codigo = txtProductoCodigo.getText().trim().toUpperCase();
        String descripcion = txtProductoDescripcion.getText().trim();
        String precioStr = txtProductoPrecioVenta.getText().trim();
        String stockStr = txtProductoStock.getText().trim();

        // El código no se puede modificar (o si quieres permitirlo, valida duplicado excluyéndolo)
        if (!codigo.equals(sel.getCodigo())) {
            if (!validarCodigoProducto(codigo)) {
                mostrarError("Código inválido (máx. 13 caracteres alfanuméricos).");
                return;
            }
            if (existeProductoConCodigo(codigo)) {
                mostrarError("El código '" + codigo + "' ya está en uso por otro producto.");
                return;
            }
        }

        double precio = 0;
        try {
            precio = Double.parseDouble(precioStr.replace(",", "."));
            if (!validarPrecio(precio)) {
                mostrarError("El precio debe ser mayor que 0.");
                return;
            }
        } catch (NumberFormatException e) {
            mostrarError("Precio inválido. Usa formato numérico (ej: 150.99)");
            return;
        }

        double stock = 0;
        try {
            stock = Double.parseDouble(stockStr);
            if (!validarStock(stock)) {
                mostrarError("El stock debe ser un número entero >= 0.");
                return;
            }
        } catch (NumberFormatException e) {
            mostrarError("Stock inválido. Debe ser número entero.");
            return;
        }
        
        // NOTA: El stock NO se modifica aquí para proteger la integridad.
        // El stock solo debe cambiar a través de facturas de compra/venta.

        try {
            productoController.modificar(sel);
            refrescarProductos();
            limpiarProducto();
            mostrarInfo("Producto modificado correctamente.");
        } catch (Exception e) { 
            mostrarError("Error al modificar: " + e.getMessage()); 
        }
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
        if (empresa == null) {
            mostrarError("No hay empresa activa.");
            return;
        }

        // Validaciones obligatorias
        if (comboFacturaTipo.getValue() == null) {
            mostrarError("Debes seleccionar el tipo de factura (Venta o Compra).");
            return;
        }
        if (comboFacturaEntidad.getValue() == null) {
            mostrarError("Debes seleccionar un Cliente o Proveedor.");
            return;
        }
        if (!validarCampoObligatorio(txtFacturaNumero, "Número de factura")) {
            return;
        }
        if (datePickerFacturaFecha.getValue() == null) {
            mostrarError("La fecha de emisión es obligatoria.");
            return;
        }
        if (datePickerFacturaFecha.getValue().isAfter(java.time.LocalDate.now())) {
            mostrarError("La fecha no puede ser futura.");
            return;
        }
        if (comboFacturaEstado.getValue() == null) {
            mostrarError("Debes seleccionar un estado.");
            return;
        }
        if (lineasTemporales.isEmpty()) {
            mostrarError("Debes añadir al menos una línea de producto.");
            return;
        }

        String numeroFactura = txtFacturaNumero.getText().trim();
        if (existeNumeroFactura(numeroFactura)) {
            mostrarError("Ya existe una factura con el número '" + numeroFactura + "'.");
            return;
        }

        // === CÁLCULO SEGURO DE TOTALES (descuento vacío = 0%) ===
        double baseImponible = 0.0;

        for (LineaFactura linea : lineasTemporales) {
            if (linea.getCantidad() <= 0) {
                mostrarError("La cantidad debe ser mayor que 0 en todas las líneas.");
                return;
            }
            if (linea.getPrecioUnitario() < 0) {
                mostrarError("El precio unitario no puede ser negativo.");
                return;
            }

            // Si descuento está vacío o no es número → tratar como 0%
            double descuento = 0.0;
            try {
                String descTexto = linea.getDescuento() + "".trim();
                if (!descTexto.isEmpty()) {
                    descuento = Double.parseDouble(descTexto);
                }
            } catch (Exception e) {
                descuento = 0.0; // Si hay error → 0%
            }

            if (descuento < 0 || descuento > 100) {
                mostrarError("El descuento debe estar entre 0 y 100.");
                return;
            }

            linea.setDescuento(descuento); // Aseguramos valor correcto
            baseImponible += linea.getTotalLinea();
        }
        
        double ivaTotal = baseImponible * obtenerTasaIvaSeleccionada();
        double totalFactura = baseImponible + ivaTotal;

        // === GUARDAR FACTURA ===
        Factura f = new Factura();
        f.setEmpresaId(empresa.getId());
        f.setEntidadId(comboFacturaEntidad.getValue().getId());
        f.setTipo(mapTipoFactura(comboFacturaTipo.getValue()));
        f.setNumero(numeroFactura);
        f.setFechaEmision(java.sql.Date.valueOf(datePickerFacturaFecha.getValue()));
        f.setConcepto(txtFacturaConcepto.getText().trim());
        f.setEstado(comboFacturaEstado.getValue());
        f.setObservaciones(safe(txtFacturaObservaciones));
        f.setBaseImponible(baseImponible);
        f.setIvaTotal(ivaTotal);
        f.setTotalFactura(totalFactura);

        try {
            facturaController.añadir(f, new ArrayList<>(lineasTemporales));
            actualizarStockProductos(f.getTipo(), lineasTemporales);
            refrescarFacturas();
            refrescarProductos();
            limpiarFactura();
            mostrarInfo("Factura guardada correctamente.\nTotal: " + String.format("%.2f €", totalFactura));
        } catch (Exception e) {
            mostrarError("Error al guardar factura: " + e.getMessage());
        }
    }
    
    private void modificarFactura() {
        Factura sel = tablaFacturas.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarError("Selecciona una factura para modificar.");
            return;
        }

        // Mismas validaciones que añadir
        if (comboFacturaTipo.getValue() == null) {
            mostrarError("Debes seleccionar el tipo de factura.");
            return;
        }
        if (comboFacturaEntidad.getValue() == null) {
            mostrarError("Debes seleccionar un Cliente o Proveedor.");
            return;
        }
        if (!validarCampoObligatorio(txtFacturaNumero, "Número de factura")) {
            return;
        }
        if (datePickerFacturaFecha.getValue() == null) {
            mostrarError("La fecha de emisión es obligatoria.");
            return;
        }
        if (datePickerFacturaFecha.getValue().isAfter(java.time.LocalDate.now())) {
            mostrarError("La fecha no puede ser futura.");
            return;
        }
        if (comboFacturaEstado.getValue() == null) {
            mostrarError("Debes seleccionar un estado.");
            return;
        }
        if (lineasTemporales.isEmpty()) {
            mostrarError("La factura debe tener al menos una línea.");
            return;
        }

        String numeroNuevo = txtFacturaNumero.getText().trim();
        if (!numeroNuevo.equals(sel.getNumero()) && existeNumeroFactura(numeroNuevo)) {
            mostrarError("El número '" + numeroNuevo + "' ya está en uso.");
            return;
        }

        // === REVERTIR STOCK ANTIGUO ===
        List<LineaFactura> lineasAntiguas = lineaFacturaController.consultarPorFacturaId(sel.getId());
        char tipoInverso = (sel.getTipo() == 'V') ? 'C' : 'V';
        actualizarStockProductos(tipoInverso, lineasAntiguas);

        // === RECÁLCULO SEGURO CON DESCUENTO 0% SI VACÍO ===
        double baseImponible = 0.0;
        for (LineaFactura linea : lineasTemporales) {
            if (linea.getCantidad() <= 0 || linea.getPrecioUnitario() < 0) {
                mostrarError("Cantidad y precio deben ser positivos.");
                return;
            }

            double descuento = 0.0;
            try {
                String desc = linea.getDescuento() + "".trim();
                if (!desc.isEmpty()) {
                    descuento = Double.parseDouble(desc);
                }
            } catch (Exception ignored) {
            }
            if (descuento < 0 || descuento > 100) {
                mostrarError("Descuento debe estar entre 0 y 100.");
                return;
            }

            linea.setDescuento(descuento);
            baseImponible += linea.getTotalLinea();
        }

        double ivaTotal = baseImponible * obtenerTasaIvaSeleccionada();
        double totalFactura = baseImponible + ivaTotal;

        // === ACTUALIZAR FACTURA ===
        sel.setEntidadId(comboFacturaEntidad.getValue().getId());
        sel.setTipo(mapTipoFactura(comboFacturaTipo.getValue()));
        sel.setNumero(numeroNuevo);
        sel.setFechaEmision(java.sql.Date.valueOf(datePickerFacturaFecha.getValue()));
        sel.setConcepto(txtFacturaConcepto.getText().trim());
        sel.setEstado(comboFacturaEstado.getValue());
        sel.setObservaciones(safe(txtFacturaObservaciones));
        sel.setBaseImponible(baseImponible);
        sel.setIvaTotal(ivaTotal);
        sel.setTotalFactura(totalFactura);

        try {
            facturaController.modificar(sel);

            lineaFacturaController.borrarPorFacturaId(sel.getId());
            for (LineaFactura lf : lineasTemporales) {
                lf.setFacturaId(sel.getId());
                lineaFacturaController.añadir(lf);
            }

            actualizarStockProductos(sel.getTipo(), lineasTemporales);

            refrescarFacturas();
            refrescarProductos();
            limpiarFactura();
            mostrarInfo("Factura modificada correctamente.");
        } catch (Exception e) {
            mostrarError("Error al modificar: " + e.getMessage());
        }
    }

    private void eliminarFactura() {
        Factura sel = tablaFacturas.getSelectionModel().getSelectedItem();
        if(sel != null && confirmar("¿Eliminar factura?")) {
            facturaController.borrarPorId(empresa.getId(), sel.getId());
            refrescarFacturas();
        }
    }
    
    // Convierte "21%" -> 0.21, "10%" -> 0.10, "4%" -> 0.04.
    private double obtenerTasaIvaSeleccionada() {
        String seleccion = comboFacturaIva.getValue();
        if (seleccion == null || seleccion.isEmpty()) return 0.0;
        
        try {
            // Quitamos el símbolo % y espacios, convertimos a double y dividimos por 100
            String numeroStr = seleccion.replace("%", "").trim();
            return Double.parseDouble(numeroStr) / 100.0;
        } catch (Exception e) {
            return 0.0;
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

        // Crear línea temporal y añadirla a la lista observable
        LineaFactura linea = new LineaFactura();
        linea.setProductoId(p.getId());
        linea.setCantidad(cantidad);
        linea.setPrecioUnitario(precio);
        linea.setDescuento(descuento);

        lineasTemporales.add(linea);

        // Limpiar campos de entrada de línea
        txtFacturaCantidad.clear();
        txtFacturaDescuento.clear();
        comboFacturaProducto.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleQuitarLinea() {
        LineaFactura sel = tablaLineasFactura.getSelectionModel().getSelectedItem();
        if (sel != null) {
            lineasTemporales.remove(sel);
        }
    }

    // ========================================================================
    // 9. AUTO-RELLENADO DE FORMULARIOS (SELECCIÓN EN TABLA)
    // ========================================================================

    private void cargarDatosCliente(Cliente c) {
        txtClienteNombre.setText(c.getNombre());
        txtClienteNif.setText(c.getNif());
        txtClienteCorreo.setText(c.getEmail());
        txtClienteTelefono.setText(c.getTelefono());

        // Limpiar campos primero
        txtClienteDireccionFiscal.clear(); txtClienteCpFiscal.clear(); // ... limpiar resto de fiscales
        txtClienteDireccionEnvio.clear(); txtClienteCpEnvio.clear(); // ... limpiar resto de envios

        // Buscar y rellenar
        if (cacheDirecciones != null) {
            for (Direccion d : cacheDirecciones) {
                if (d.getEntidadId() == c.getId()) {
                    if ("Fiscal".equalsIgnoreCase(d.getEtiqueta())) {
                        txtClienteDireccionFiscal.setText(safeStr(d.getDireccion()));
                        txtClienteCpFiscal.setText(safeStr(d.getCp()));
                        txtClienteCiudadFiscal.setText(safeStr(d.getCiudad()));
                        txtClienteProvinciaFiscal.setText(safeStr(d.getProvincia()));
                        txtClientePaisFiscal.setText(safeStr(d.getPais()));
                    } 
                    else if ("Envio".equalsIgnoreCase(d.getEtiqueta())) {
                        txtClienteDireccionEnvio.setText(safeStr(d.getDireccion()));
                        txtClienteCpEnvio.setText(safeStr(d.getCp()));
                        txtClienteCiudadEnvio.setText(safeStr(d.getCiudad()));
                        txtClienteProvinciaEnvio.setText(safeStr(d.getProvincia()));
                        txtClientePaisEnvio.setText(safeStr(d.getPais()));
                    }
                }
            }
        }
    }
    
    private void cargarDatosProveedor(Proveedor p) {
        // 1. Cargar datos básicos
        txtProveedorNombre.setText(p.getNombre());
        txtProveedorNif.setText(p.getNif());
        txtProveedorCorreo.setText(p.getEmail());
        txtProveedorTelefono.setText(p.getTelefono());
        
        // 2. Limpiar campos de dirección antes de rellenar
        txtProveedorDireccionFiscal.clear(); txtProveedorCpFiscal.clear(); 
        txtProveedorCiudadFiscal.clear(); txtProveedorProvinciaFiscal.clear(); txtProveedorPaisFiscal.clear();
        
        txtProveedorDireccionEnvio.clear(); txtProveedorCpEnvio.clear();
        txtProveedorCiudadEnvio.clear(); txtProveedorProvinciaEnvio.clear(); txtProveedorPaisEnvio.clear();

        // 3. Buscar direcciones en la caché y rellenar según etiqueta
        if (cacheDirecciones != null) {
            for (Direccion d : cacheDirecciones) {
                if (d.getEntidadId() == p.getId()) {
                    if ("Fiscal".equalsIgnoreCase(d.getEtiqueta())) {
                        txtProveedorDireccionFiscal.setText(safeStr(d.getDireccion()));
                        txtProveedorCpFiscal.setText(safeStr(d.getCp()));
                        txtProveedorCiudadFiscal.setText(safeStr(d.getCiudad()));
                        txtProveedorProvinciaFiscal.setText(safeStr(d.getProvincia()));
                        txtProveedorPaisFiscal.setText(safeStr(d.getPais()));
                    } 
                    else if ("Envio".equalsIgnoreCase(d.getEtiqueta())) {
                        txtProveedorDireccionEnvio.setText(safeStr(d.getDireccion()));
                        txtProveedorCpEnvio.setText(safeStr(d.getCp()));
                        txtProveedorCiudadEnvio.setText(safeStr(d.getCiudad()));
                        txtProveedorProvinciaEnvio.setText(safeStr(d.getProvincia()));
                        txtProveedorPaisEnvio.setText(safeStr(d.getPais()));
                    }
                }
            }
        }
    }

    private void cargarDatosProducto(Producto p) {
        txtProductoCodigo.setText(p.getCodigo());
        txtProductoDescripcion.setText(p.getDescripcion());
        txtProductoPrecioVenta.setText(formatDouble(p.getPrecioVenta()).replace(",", "."));
        txtProductoStock.setText(formatDouble(p.getStock()).replace(",", "."));

        // AL EDITAR: Bloquear el stock para evitar modificaciones manuales accidentales
        txtProductoStock.setDisable(true); 

        // Seleccionar proveedor en el combo
        if (p.getProveedorId() != null) {
            for (Proveedor prov : comboProductoProveedor.getItems()) {
                if (prov.getId() == p.getProveedorId()) { comboProductoProveedor.getSelectionModel().select(prov); break; }
            }
        } else comboProductoProveedor.getSelectionModel().clearSelection();
    }
    
    private void cargarDatosFactura(Factura f) {
        // Carga básica de campos
        txtFacturaNumero.setText(f.getNumero());
        
        if (f.getFechaEmision() != null) {
            datePickerFacturaFecha.setValue(f.getFechaEmision().toLocalDate());
        } else {
            datePickerFacturaFecha.setValue(null);
        }
        
        txtFacturaConcepto.setText(f.getConcepto());
        txtFacturaObservaciones.setText(f.getObservaciones());
        
        // Configurar combos
        String tipoEtiqueta = (f.getTipo() == 'V') ? "Venta" : "Compra";
        comboFacturaTipo.setValue(tipoEtiqueta);
        comboFacturaEstado.setValue(f.getEstado());
        
        // --- CALCULAR Y SELECCIONAR EL IVA ---
        // Si hay base imponible, calculamos el porcentaje inverso
        if (f.getBaseImponible() > 0) {
            double ratio = f.getIvaTotal() / f.getBaseImponible(); // Ej: 0.21
            long porcentaje = Math.round(ratio * 100); // Ej: 21
            comboFacturaIva.setValue(porcentaje + "%");
        } else {
            comboFacturaIva.setValue("21%"); // Por defecto si es 0
        }
        
        // FORZAR RECARGA DE ENTIDADES: Necesario para que el combo tenga datos si el tipo no cambia
        cargarEntidadesEnFactura(tipoEtiqueta); 
        
        // Seleccionar entidad correcta
        comboFacturaEntidad.getSelectionModel().clearSelection();
        if (comboFacturaEntidad.getItems() != null) {
            for (Entidad e : comboFacturaEntidad.getItems()) {
                if (e.getId() == f.getEntidadId()) {
                    comboFacturaEntidad.getSelectionModel().select(e);
                    break;
                }
            }
        }
        
        // CARGAR LÍNEAS DESDE BD A TABLA VISUAL
        lineasTemporales.clear();
        if (f.getId() > 0) {
            List<LineaFactura> lineasDeLaBD = lineaFacturaController.consultarPorFacturaId(f.getId());
            lineasTemporales.addAll(lineasDeLaBD);
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
    // 10. REFRESCAR Y LIMPIAR (GESTIÓN DE ESTADO UI)
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
            
            // También refrescamos la lista de proveedores en la pestaña productos
            List<Proveedor> listaProvs = proveedorController.consultarTodos(empresa.getId());
            if(comboProductoProveedor != null) comboProductoProveedor.setItems(FXCollections.observableArrayList(listaProvs));
        }
    }

    private void refrescarFacturas() {
        if (empresa != null) tablaFacturas.setItems(FXCollections.observableArrayList(facturaController.consultarTodas(empresa.getId())));
    }

    private void limpiarCliente() {
        // Datos básicos
        txtClienteNombre.clear(); 
        txtClienteNif.clear(); 
        txtClienteCorreo.clear(); 
        txtClienteTelefono.clear();

        // Dirección Fiscal
        txtClienteDireccionFiscal.clear(); 
        txtClienteCpFiscal.clear(); 
        txtClienteCiudadFiscal.clear(); 
        txtClienteProvinciaFiscal.clear(); 
        txtClientePaisFiscal.clear();
        
        // Dirección de Envío
        txtClienteDireccionEnvio.clear(); 
        txtClienteCpEnvio.clear(); 
        txtClienteCiudadEnvio.clear(); 
        txtClienteProvinciaEnvio.clear(); 
        txtClientePaisEnvio.clear();
    }

    private void limpiarProveedor() {
        // Datos básicos
        txtProveedorNombre.clear(); 
        txtProveedorNif.clear(); 
        txtProveedorCorreo.clear(); 
        txtProveedorTelefono.clear();
        
        // Dirección Fiscal
        txtProveedorDireccionFiscal.clear(); 
        txtProveedorCpFiscal.clear(); 
        txtProveedorCiudadFiscal.clear(); 
        txtProveedorProvinciaFiscal.clear(); 
        txtProveedorPaisFiscal.clear();
        
        // Dirección de Envío
        txtProveedorDireccionEnvio.clear(); 
        txtProveedorCpEnvio.clear(); 
        txtProveedorCiudadEnvio.clear(); 
        txtProveedorProvinciaEnvio.clear(); 
        txtProveedorPaisEnvio.clear();
    }
    
    private void limpiarProducto() {
        txtProductoCodigo.clear(); 
        txtProductoDescripcion.clear(); 
        comboProductoProveedor.getSelectionModel().clearSelection();
        txtProductoPrecioVenta.clear(); 
        txtProductoStock.clear();

        // AL CREAR: Habilitamos el stock para que se pueda poner el inventario inicial
        txtProductoStock.setDisable(false); 
        comboProductoProveedor.getSelectionModel().clearSelection();
        consultarProductosConFiltro();
    }

    private void limpiarFactura() {
        txtFacturaNumero.clear(); 
        datePickerFacturaFecha.setValue(java.time.LocalDate.now()); // Pone la fecha de hoy por comodidad
        comboFacturaEntidad.getSelectionModel().clearSelection();
        txtFacturaConcepto.clear(); 
        txtFacturaObservaciones.clear(); 
        comboFacturaEstado.getSelectionModel().clearSelection();
        comboFacturaIva.getSelectionModel().select("21%"); // Resetear al 21% por defecto
        
        txtFacturaCantidad.clear(); 
        txtFacturaDescuento.clear(); 
        comboFacturaProducto.getSelectionModel().clearSelection();
        lineasTemporales.clear();
    }

    // ========================================================================
    // 11. VALIDACIONES Y HELPERS (UTILIDADES)
    // ========================================================================

    /**
     * Valida únicamente el formato: 8 números y 1 letra.
     * No comprueba si la letra es matemáticamente correcta.
     * Admite guiones o espacios (los ignora).
     */
    private boolean validarNIF(String nif) {
        if (nif == null) return false;

        // 1. Limpiar: Quitamos espacios, puntos y guiones para quedarnos solo con el contenido
        String limpio = nif.trim().replaceAll("[^a-zA-Z0-9]", "");

        // 2. Validar: Debe tener exactamente 8 dígitos seguidos de 1 letra (mayúscula o minúscula)
        return limpio.matches("^[0-9]{8}[A-Za-z]$");
    }
    /**
     * Valida el teléfono permitiendo formatos flexibles.
     * Admite espacios, guiones, puntos o prefijos con '+'.
     * Solo comprueba que, al quitar los adornos, queden entre 9 y 15 dígitos.
     */
    private boolean validarTelefono(String tel) {
        if (tel == null) return false;

        // 1. Limpiar: Quitamos todo lo que NO sea un número (0-9)
        // Esto elimina espacios, guiones, el símbolo +, paréntesis, etc.
        String soloNumeros = tel.replaceAll("[^0-9]", "");

        // 2. Validar longitud:
        // 9 es el estándar en España (ej: 666111222).
        // 15 es el máximo internacional estándar.
        return soloNumeros.length() >= 9 && soloNumeros.length() <= 15;
    }
    private boolean validarEmail(String email) { return email.matches("^[\\w._%+-]+@[\\w.-]+\\.[A-Za-z]{2,6}$"); }
    private boolean validarCP(String cp) { return cp.matches("^[A-Z0-9\\-\\s]{3,10}$"); }

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
    
    private boolean validarCodigoProducto(String codigo) {return codigo.trim().toUpperCase().matches("^[A-Z0-9\\-_]{1,13}$"); }
    private boolean validarPrecio(double precio) { return precio > 0; }
    private boolean validarStock(double stock) { return stock >= 0 && stock == Math.floor(stock); }
    
    private boolean validarCampoObligatorio(TextField tf, String nombre) { return campoEsValido(tf, nombre); }

    /**
     * Actualiza el stock de los productos afectados por una factura.
     * Busca el producto por ID en la BD y suma o resta según el tipo.
     */
    private void actualizarStockProductos(char tipoFactura, List<LineaFactura> lineas) {
        List<Producto> todosLosProductos = productoController.consultarTodos(empresa.getId());

        for (LineaFactura linea : lineas) {
            Producto productoAfectado = null;
            for (Producto p : todosLosProductos) {
                if (p.getId() == linea.getProductoId()) {
                    productoAfectado = p;
                    break;
                }
            }

            if (productoAfectado != null) {
                double cantidad = linea.getCantidad();
                double stockActual = productoAfectado.getStock();
                double nuevoStock;

                // Venta = Resta stock | Compra = Suma stock
                if (tipoFactura == 'V') {
                    nuevoStock = stockActual - cantidad;
                } else {
                    nuevoStock = stockActual + cantidad;
                }

                productoAfectado.setStock(nuevoStock);
                productoController.modificar(productoAfectado);
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
    
    private String obtenerDireccionFormateada(Long entidadId, String tipoEtiqueta) {
        if (empresa == null || entidadId == null || cacheDirecciones == null) return "";
        
        for (Direccion d : cacheDirecciones) {
            // Buscamos coincidencia de ID y Tipo (Fiscal/Envio)
            if (d.getEntidadId() == entidadId && d.getEtiqueta().equalsIgnoreCase(tipoEtiqueta)) {
                StringBuilder sb = new StringBuilder();
                if (d.getDireccion() != null && !d.getDireccion().isEmpty()) sb.append(d.getDireccion());
                if (d.getCiudad() != null && !d.getCiudad().isEmpty()) sb.append(", ").append(d.getCiudad());
                if (d.getCp() != null && !d.getCp().isEmpty()) sb.append(" (").append(d.getCp()).append(")");
                return sb.toString();
            }
        }
        return ""; 
    }

    /**
     * Método unificado para guardar, actualizar o borrar direcciones.
     * Busca una dirección específica por su Entidad y su Etiqueta (Fiscal/Envio).
     */
    private void guardarOActualizarDireccion(long entidadId, String tipoEtiqueta, 
                                             TextField txtDir, TextField txtCp, TextField txtCiu, TextField txtPro, TextField txtPai) {
        
        String direccionTexto = safe(txtDir);
        
        // 1. Buscar si ya existe esa dirección específica en la caché
        Direccion dirExistente = null;
        if (cacheDirecciones != null) {
            for (Direccion d : cacheDirecciones) {
                // CLAVE: Buscamos por ID de entidad Y por etiqueta para no mezclar Fiscal con Envío
                if (d.getEntidadId() == entidadId && d.getEtiqueta().equalsIgnoreCase(tipoEtiqueta)) {
                    dirExistente = d;
                    break;
                }
            }
        }

        // 2. Lógica de persistencia
        if (dirExistente != null) {
            // Si la dirección YA EXISTE...
            if (direccionTexto.isEmpty()) {
                // Si el usuario ha borrado el texto, eliminamos la dirección de la BD para no dejar basura
                direccionController.borrarPorId(dirExistente.getId());
            } else {
                // Si hay texto, actualizamos los datos
                dirExistente.setDireccion(direccionTexto);
                dirExistente.setCp(safe(txtCp));
                dirExistente.setCiudad(safe(txtCiu));
                dirExistente.setProvincia(safe(txtPro));
                dirExistente.setPais(safe(txtPai));
                direccionController.modificar(dirExistente);
            }
        } else {
            // Si la dirección NO EXISTE y hay datos... la creamos nueva
            if (!direccionTexto.isEmpty()) {
                Direccion nueva = new Direccion();
                nueva.setEntidadId(entidadId);
                nueva.setEtiqueta(tipoEtiqueta); // "Fiscal" o "Envio"
                nueva.setDireccion(direccionTexto);
                nueva.setCp(safe(txtCp));
                nueva.setCiudad(safe(txtCiu));
                nueva.setProvincia(safe(txtPro));
                nueva.setPais(safe(txtPai));
                direccionController.añadir(nueva);
            }
        }
    }

    private boolean existeEntidadConNif(long empresaId, String nif) {
        List<Entidad> entidades = entidadController.consultarTodos(empresaId);
        for (Entidad e : entidades) { if (e.getNif().equalsIgnoreCase(nif)) return true; }
        return false;
    }
    
    private boolean existeNifDuplicado(long empresaId, String nif, long idPropio) {
        List<Entidad> entidades = entidadController.consultarTodos(empresaId);
        for (Entidad e : entidades) {
            if (e.getNif().equalsIgnoreCase(nif) && e.getId() != idPropio) return true;
        }
        return false;
    }
    
    private boolean existeEntidadConTelefono(long empresaId, String telefono) {
        List<Entidad> entidades = entidadController.consultarTodos(empresaId);
        for (Entidad e : entidades) {
            if (e.getTelefono() != null && e.getTelefono().equals(telefono)) {
                return true;
            }
        }
        return false;
    }

    private boolean existeTelefonoDuplicado(long empresaId, String telefono, long idPropio) {
        List<Entidad> entidades = entidadController.consultarTodos(empresaId);
        for (Entidad e : entidades) {
            if (e.getTelefono() != null && e.getTelefono().equals(telefono) && e.getId() != idPropio) {
                return true;
            }
        }
        return false;
    }
    
    // Método auxiliar para comprobar duplicados sin usar consultarPorCodigo
    private boolean existeProductoConCodigo(String codigo) {
        // Obtenemos la lista actual de la base de datos
        List<Producto> lista = productoController.consultarTodos(empresa.getId());
        for (Producto p : lista) {
            if (p.getCodigo().equalsIgnoreCase(codigo)) {
                return true; // Ya existe
            }
        }
        return false;
    }
    
    // Método auxiliar para comprobar si ya existe el número de factura
    private boolean existeNumeroFactura(String numero) {
        // 1. Obtenemos todas las facturas de la empresa actual desde la base de datos
        List<Factura> facturas = facturaController.consultarTodas(empresa.getId());
        
        // 2. Recorremos la lista buscando si alguna tiene el mismo número
        for (Factura f : facturas) {
            if (f.getNumero().equalsIgnoreCase(numero)) {
                return true; // ¡Encontrado! Ya existe.
            }
        }
        return false; // No existe, el número está libre.
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

    // Helpers para seguridad y conversión de tipos
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
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/icono.png")));
            stage.setTitle("Lista de Empresas");
            stage.setScene(new Scene(root));
            stage.show();
            Stage actual = (Stage) retroceder.getScene().getWindow();
            actual.close();
        } catch (IOException ex) { ex.printStackTrace(); }
    }
}