package Controlador;

// Importaciones necesarias para conectar Modelo, Vista y utilidades de Java
import Modelo.*;
import java.io.IOException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * Controlador principal de la aplicación.
 * Gestiona toda la lógica de la pantalla "Gestión de Empresa".
 * Implementa patrón MVC: Recibe eventos de la Vista (FXML), procesa con el Modelo
 * y actualiza la UI.
 */
public class GestionEmpresaController {

    // --- VARIABLES GLOBALES DE ESTADO ---
    private Empresa empresa; // La empresa con la que estamos trabajando actualmente
    private boolean inicializado = false; // Bandera para saber si la vista ya cargó
    
    // CACHÉ DE DIRECCIONES:
    // Guardamos todas las direcciones en esta lista en memoria RAM al cargar la empresa.
    // Esto evita hacer una consulta SQL por cada celda de la tabla, mejorando el rendimiento brutalmente.
    private List<Direccion> cacheDirecciones = new ArrayList<>();

    // --- INSTANCIAS DE CONTROLADORES (Nexo con la BD) ---
    private final EntidadController entidadController = new EntidadController();
    private final ClienteController clienteController = new ClienteController();
    private final ProveedorController proveedorController = new ProveedorController();
    private final ProductoController productoController = new ProductoController();
    private final FacturaController facturaController = new FacturaController();
    private final DireccionController direccionController = new DireccionController();

    // --- ELEMENTOS DE LA INTERFAZ (FXML) ---
    
    // Contenedor de pestañas (sirve para saber qué está viendo el usuario: Cliente, Producto, etc.)
    @FXML private TabPane MenuCliente;

    // Cabecera
    @FXML private Label nombreEmpresa;
    @FXML private ImageView retroceder; 

    // -----------------------------------------------------------
    // PESTAÑA 1: CLIENTES
    // -----------------------------------------------------------
    @FXML private TableView<Cliente> tablaClientes;
    // Columnas de la tabla (El primer tipo es el objeto de la fila, el segundo el tipo de dato de la celda)
    @FXML private TableColumn<Cliente, String> colClienteCodigo, colClienteNombre, colClienteNif, colClienteCorreo, colClienteTelefono;
    @FXML private TableColumn<Cliente, String> colClienteDireccion, colClienteCiudad, colClienteProvincia, colClientePais, colClienteCp;
    
    // Formulario de edición/creación
    @FXML private TextField txtClienteNombre, txtClienteNif, txtClienteCorreo, txtClienteTelefono;
    @FXML private ComboBox<String> comboClienteDireccion; // Tipo de dirección (Fiscal, Envío...)
    @FXML private TextField txtClienteDireccion, txtClienteCp, txtClienteCiudad, txtClienteProvincia, txtClientePais;

    // -----------------------------------------------------------
    // PESTAÑA 2: PROVEEDORES
    // -----------------------------------------------------------
    @FXML private TableView<Proveedor> tablaProveedores;
    @FXML private TableColumn<Proveedor, String> colProveedorCodigo, colProveedorNombre, colProveedorNif, colProveedorCorreo, colProveedorTelefono;
    @FXML private TableColumn<Proveedor, String> colProveedorDireccion, colProveedorCiudad, colProveedorProvincia, colProveedorPais, colProveedorCp;

    @FXML private TextField txtProveedorNombre, txtProveedorNif, txtProveedorCorreo, txtProveedorTelefono;
    @FXML private ComboBox<String> comboProveedorDireccion;
    @FXML private TextField txtProveedorDireccion, txtProveedorCp, txtProveedorCiudad, txtProveedorProvincia, txtProveedorPais;

    // -----------------------------------------------------------
    // PESTAÑA 3: PRODUCTOS
    // -----------------------------------------------------------
    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, String> colProductoCodigo, colProductoDescripcion, colProductoReferencia, colProductoProveedor, colProductoIva,
            colProductoPrecioCoste, colProductoPrecioVenta, colProductoStock;

    @FXML private TextField txtProductoCodigo, txtProductoDescripcion, txtProductoReferencia;
    @FXML private ComboBox<Proveedor> comboProductoProveedor; // Para vincular un producto a un proveedor
    @FXML private TextField txtProductoIva, txtProductoPrecioCoste, txtProductoPrecioVenta, txtProductoStock;

    // -----------------------------------------------------------
    // PESTAÑA 4: FACTURAS
    // -----------------------------------------------------------
    @FXML private TableView<Factura> tablaFacturas;
    @FXML private TableColumn<Factura, String> colFacturaEntidadId, colFacturaTipo, colFacturaNumero, colFacturaFecha, colFacturaConcepto,
            colFacturaBase, colFacturaIvaTotal, colFacturaTotal, colFacturaEstado, colFacturaObservaciones;

    @FXML private ComboBox<Entidad> comboFacturaEntidad; // Selector inteligente (carga Clientes o Proveedores según el tipo)
    @FXML private TextField txtFacturaNumero, txtFacturaFecha, txtFacturaConcepto,
            txtFacturaBase, txtFacturaIvaTotal, txtFacturaTotal, txtFacturaObservaciones;
    @FXML private ComboBox<String> comboFacturaTipo, comboFacturaEstado;

    // --- SUB-SECCIÓN: LÍNEAS DE FACTURA (El "Carrito" de la factura) ---
    @FXML private ComboBox<Producto> comboFacturaProducto;
    @FXML private TextField txtFacturaCantidad;
    @FXML private TextField txtFacturaDescuento;
    
    // Tabla pequeña para previsualizar las líneas antes de guardar
    @FXML private TableView<LineaFactura> tablaLineasFactura;
    @FXML private TableColumn<LineaFactura, String> colLineaProducto;
    @FXML private TableColumn<LineaFactura, String> colLineaCantidad;
    @FXML private TableColumn<LineaFactura, String> colLineaPrecio;
    @FXML private TableColumn<LineaFactura, String> colLineaTotal;

    // LISTA TEMPORAL: Aquí guardamos las líneas en memoria (RAM) mientras el usuario crea la factura.
    // No se guardan en BD hasta que se pulsa "Añadir Factura".
    private ObservableList<LineaFactura> lineasTemporales = FXCollections.observableArrayList();

    // =========================================================================
    // MÉTODO DE INICIALIZACIÓN (Se ejecuta automáticamente al abrir la ventana)
    // =========================================================================
    @FXML
    private void initialize() {
        // 1. Configuración de Listas Estáticas (Valores fijos)
        if(comboFacturaTipo != null) comboFacturaTipo.setItems(FXCollections.observableArrayList("Venta", "Compra"));
        if(comboFacturaEstado != null) comboFacturaEstado.setItems(FXCollections.observableArrayList("PENDIENTE", "PAGADA", "ANULADA"));

        // 2. Configuración de Navegación
        if (retroceder != null) retroceder.setOnMouseClicked(event -> volverAListaEmpresas());

        // 3. Configurar Tablas (Vincular columnas de la vista con datos del modelo)
        configurarTablas();

        // 4. Configurar Visualización de Combos (StringConverters)
        // Esto hace que en el desplegable se vea "Nombre" en lugar de "Modelo.Cliente@3f2a..."
        configurarConvertidoresCombos();

        // 5. Configurar Tabla de Líneas de Factura (Columnas y lista observable)
        configurarTablaLineas();

        // 6. Listener (Escuchador) para Facturas
        // Detecta si cambiamos de "Venta" a "Compra" para cargar Clientes o Proveedores en el combo
        if (comboFacturaTipo != null) {
            comboFacturaTipo.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) cargarEntidadesEnFactura(newVal);
            });
        }

        // 7. Finalizar carga
        inicializado = true;
        // Seleccionamos "Venta" por defecto para evitar que el combo de entidades salga vacío
        if (comboFacturaTipo != null) comboFacturaTipo.getSelectionModel().select("Venta");
    }

    /**
     * Método llamado desde la ventana anterior para pasar la empresa seleccionada.
     */
    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
        if (nombreEmpresa != null) {
            nombreEmpresa.setText(empresa != null ? empresa.getNombre() + " (" + empresa.getNif() + ")" : "Ninguna empresa");
        }
        // Si la vista ya cargó, lanzamos la carga de datos de la BD
        if (inicializado && empresa != null) {
            refrescarTodo();
        }
    }

    // =========================================================================
    // CONFIGURACIÓN DE COLUMNAS (Data Binding)
    // =========================================================================
    private void configurarTablas() {
        // --- CLIENTES ---
        // SimpleStringProperty envuelve el dato para que JavaFX pueda pintarlo y detectar cambios.
        // safeStr evita errores si el dato viene nulo de la BD.
        colClienteCodigo.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getCodigo())));
        colClienteNombre.setCellValueFactory(c -> new SimpleStringProperty(safeStr(c.getValue().getNombre())));
        colClienteNif.setCellValueFactory(c -> new SimpleStringProperty(safeStr(c.getValue().getNif())));
        colClienteCorreo.setCellValueFactory(c -> new SimpleStringProperty(safeStr(c.getValue().getEmail())));
        colClienteTelefono.setCellValueFactory(c -> new SimpleStringProperty(safeStr(c.getValue().getTelefono())));
        // Las direcciones se obtienen buscando en la lista CACHÉ (optimización)
        colClienteDireccion.setCellValueFactory(c -> new SimpleStringProperty(obtenerDireccionCampo(c.getValue().getId(), "direccion")));
        colClienteCiudad.setCellValueFactory(c -> new SimpleStringProperty(obtenerDireccionCampo(c.getValue().getId(), "ciudad")));
        colClienteProvincia.setCellValueFactory(c -> new SimpleStringProperty(obtenerDireccionCampo(c.getValue().getId(), "provincia")));
        colClientePais.setCellValueFactory(c -> new SimpleStringProperty(obtenerDireccionCampo(c.getValue().getId(), "pais")));
        colClienteCp.setCellValueFactory(c -> new SimpleStringProperty(obtenerDireccionCampo(c.getValue().getId(), "cp")));

        // --- PROVEEDORES ---
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

        // --- PRODUCTOS ---
        colProductoCodigo.setCellValueFactory(p -> new SimpleStringProperty(safeStr(p.getValue().getCodigo())));
        colProductoDescripcion.setCellValueFactory(p -> new SimpleStringProperty(safeStr(p.getValue().getDescripcion())));
        colProductoReferencia.setCellValueFactory(p -> new SimpleStringProperty(safeStr(p.getValue().getReferenciaProveedor())));
        colProductoProveedor.setCellValueFactory(p -> new SimpleStringProperty(safeStr(p.getValue().getProveedorId()))); 
        colProductoIva.setCellValueFactory(p -> new SimpleStringProperty(String.valueOf(p.getValue().getTipoIVAId())));
        colProductoPrecioCoste.setCellValueFactory(p -> new SimpleStringProperty(formatDouble(p.getValue().getPrecioCoste())));
        colProductoPrecioVenta.setCellValueFactory(p -> new SimpleStringProperty(formatDouble(p.getValue().getPrecioVenta())));
        colProductoStock.setCellValueFactory(p -> new SimpleStringProperty(formatDouble(p.getValue().getStock())));

        // --- FACTURAS ---
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
     * Configura cómo se muestran los objetos en los ComboBox.
     */
    private void configurarConvertidoresCombos() {
        // Para Proveedores en Productos
        javafx.util.StringConverter<Proveedor> convProv = new javafx.util.StringConverter<>() {
            public String toString(Proveedor p) { return p == null ? null : p.getNombre() + " (" + p.getNif() + ")"; }
            public Proveedor fromString(String s) { return null; }
        };
        if(comboProductoProveedor != null) comboProductoProveedor.setConverter(convProv);

        // Para Entidades en Facturas
        javafx.util.StringConverter<Entidad> convEnt = new javafx.util.StringConverter<>() {
            public String toString(Entidad e) { return e == null ? null : e.getNombre() + " (" + e.getNif() + ")"; }
            public Entidad fromString(String s) { return null; }
        };
        if(comboFacturaEntidad != null) comboFacturaEntidad.setConverter(convEnt);

        // Para Productos en Líneas de Factura
        javafx.util.StringConverter<Producto> convProd = new javafx.util.StringConverter<>() {
            public String toString(Producto p) { return p == null ? null : p.getCodigo() + " - " + p.getDescripcion(); }
            public Producto fromString(String s) { return null; }
        };
        if(comboFacturaProducto != null) comboFacturaProducto.setConverter(convProd);
    }
    
    private void configurarTablaLineas() {
        if(colLineaProducto != null) colLineaProducto.setCellValueFactory(cell -> new SimpleStringProperty(obtenerNombreProducto(cell.getValue().getProductoId())));
        if(colLineaCantidad != null) colLineaCantidad.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getCantidad())));
        if(colLineaPrecio != null) colLineaPrecio.setCellValueFactory(cell -> new SimpleStringProperty(String.format("%.2f", cell.getValue().getPrecioUnitario())));
        if(colLineaTotal != null) colLineaTotal.setCellValueFactory(cell -> new SimpleStringProperty(String.format("%.2f", cell.getValue().getTotalLinea())));
        // Enlace directo a la lista observable en memoria
        if(tablaLineasFactura != null) tablaLineasFactura.setItems(lineasTemporales);
    }

    // =========================================================================
    // GESTIÓN DE EVENTOS (BOTONES)
    // =========================================================================
    
    // Método centralizado para "Añadir". Dependiendo de la pestaña activa, llama a un método u otro.
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
        // Nota: Modificar facturas complejas se omite en este ejemplo por seguridad.
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
    
    // Botón "+" (Agregar línea a la tabla temporal)
    @FXML
    private void handleAgregarLinea() {
        Producto p = comboFacturaProducto.getValue();
        if (p == null) {
            mostrarError("Por favor, selecciona un producto.");
            return;
        }

        double cantidad = parseDoubleSafe(txtFacturaCantidad, 1.0);
        double descuento = parseDoubleSafe(txtFacturaDescuento, 0.0);
        
        // Usamos el precio de venta del producto como base
        double precioUnitario = p.getPrecioVenta();

        // Creamos el objeto línea (aún sin ID de base de datos)
        LineaFactura linea = new LineaFactura();
        linea.setProductoId(p.getId());
        linea.setCantidad(cantidad);
        linea.setPrecioUnitario(precioUnitario);
        linea.setDescuento(descuento);

        // Añadimos a la lista visual
        lineasTemporales.add(linea);

        // Recalculamos los totales de la factura automáticamente
        recalcularTotalesFactura();

        // Limpiamos los campos pequeños para meter el siguiente
        txtFacturaCantidad.setText("");
        txtFacturaDescuento.setText("");
        comboFacturaProducto.getSelectionModel().clearSelection();
    }

    // Botón "-" (Quitar línea seleccionada)
    @FXML
    private void handleQuitarLinea() {
        LineaFactura seleccionada = tablaLineasFactura.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            lineasTemporales.remove(seleccionada);
            recalcularTotalesFactura(); // Recalcular al borrar
        } else {
            mostrarError("Selecciona una línea de la tabla pequeña para quitarla.");
        }
    }
    
    // Calcula la suma de todas las líneas y actualiza los TextField de totales
    private void recalcularTotalesFactura() {
        double baseImponible = 0.0;

        for (LineaFactura lf : lineasTemporales) {
            baseImponible += lf.getTotalLinea();
        }

        // Cálculo de IVA (Asumimos 21% general, puedes ajustarlo)
        double porcentajeIVA = 0.21; 
        double ivaTotal = baseImponible * porcentajeIVA;
        double totalFactura = baseImponible + ivaTotal;

        // Actualizamos la interfaz (reemplazamos puntos por comas si es necesario o viceversa)
        txtFacturaBase.setText(String.format("%.2f", baseImponible).replace(",", "."));
        txtFacturaIvaTotal.setText(String.format("%.2f", ivaTotal).replace(",", "."));
        txtFacturaTotal.setText(String.format("%.2f", totalFactura).replace(",", "."));
    }

    // Método vital para que la tabla de líneas muestre el nombre del producto y no un número
    private String obtenerNombreProducto(long productoId) {
        // Buscamos en la tabla grande de productos (ya cargada en memoria)
        if (tablaProductos != null && tablaProductos.getItems() != null) {
            for (Producto p : tablaProductos.getItems()) {
                if (p.getId() == productoId) {
                    return p.getCodigo() + " - " + p.getDescripcion();
                }
            }
        }
        return "Prod. ID: " + productoId;
    }
    
    private void cargarEntidadesEnFactura(String tipoFactura) {
        if (empresa == null || comboFacturaEntidad == null) return;

        try {
            // Limpiamos la lista actual
            comboFacturaEntidad.getItems().clear();
            
            if ("Venta".equals(tipoFactura)) {
                // Cargar CLIENTES
                List<Cliente> clientes = clienteController.consultarTodos(empresa.getId());
                comboFacturaEntidad.getItems().addAll(clientes);
            } else if ("Compra".equals(tipoFactura)) {
                // Cargar PROVEEDORES
                List<Proveedor> proveedores = proveedorController.consultarTodos(empresa.getId());
                comboFacturaEntidad.getItems().addAll(proveedores);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar entidades: " + e.getMessage());
        }
    }

    // =========================================================================
    // LÓGICA DE NEGOCIO (CRUD) - CLIENTES
    // =========================================================================
    private void añadirCliente() {
        if (empresa == null) { mostrarError("No hay empresa activa."); return; }

        // 1. Validaciones
        if (!campoEsValido(txtClienteNombre, "Nombre")) return;
        if (!campoEsValido(txtClienteNif, "NIF")) return;
        if (!campoEsValido(txtClienteTelefono, "Teléfono")) return;
        if (!campoEsValido(txtClienteCorreo, "Correo")) return;

        String nif = txtClienteNif.getText().trim().toUpperCase();
        if (!validarNIF(nif)) { mostrarError("El NIF debe tener exactamente 7 números y 1 letra final."); return; }
        if (existeEntidadConNif(empresa.getId(), nif)) { mostrarError("Ya existe una entidad con ese NIF."); return; }
        if (!validarTelefono(txtClienteTelefono.getText().trim())) { mostrarError("El teléfono debe tener 9 dígitos."); return; }
        if (!validarEmail(txtClienteCorreo.getText().trim())) { mostrarError("Email incorrecto."); return; }
        if (!txtClienteCp.getText().trim().isEmpty() && !validarCP(txtClienteCp.getText().trim())) { mostrarError("El CP debe tener 5 dígitos."); return; }

        // 2. Creación del objeto
        Cliente c = new Cliente();
        c.setNombre(txtClienteNombre.getText().trim());
        c.setNif(nif);
        c.setEmail(txtClienteCorreo.getText().trim());
        c.setTelefono(txtClienteTelefono.getText().trim());
        c.setCodigo(0);

        // 3. Guardado en BD (devuelve el ID generado)
        long entidadId = clienteController.añadir(c, empresa.getId());
        c.setId(entidadId);

        // 4. Guardado de dirección (si existe)
        if (entidadId > 0) {
            añadirDireccionSiRellenada(entidadId, comboClienteDireccion, txtClienteDireccion, txtClienteCp, txtClienteCiudad, txtClienteProvincia, txtClientePais);
        }

        // 5. Refresco UI
        refrescarClientes();
        limpiarCliente();
        mostrarInfo("Cliente añadido correctamente.");
    }

    // =========================================================================
    // LÓGICA DE NEGOCIO (CRUD) - PROVEEDORES
    // =========================================================================
    private void añadirProveedor() {
        if (empresa == null) { mostrarError("No hay empresa activa."); return; }

        // 1. Validaciones (similares a Cliente)
        if (!campoEsValido(txtProveedorNombre, "Nombre")) return;
        if (!campoEsValido(txtProveedorNif, "NIF")) return;
        if (!campoEsValido(txtProveedorTelefono, "Teléfono")) return;
        if (!campoEsValido(txtProveedorCorreo, "Correo")) return;

        String nif = txtProveedorNif.getText().trim().toUpperCase();
        if (!validarNIF(nif)) { mostrarError("El NIF debe tener exactamente 7 números y 1 letra final."); return; }
        if (existeEntidadConNif(empresa.getId(), nif)) { mostrarError("Ya existe una entidad con ese NIF."); return; }
        if (!validarTelefono(txtProveedorTelefono.getText().trim())) { mostrarError("El teléfono debe tener 9 dígitos."); return; }
        if (!validarEmail(txtProveedorCorreo.getText().trim())) { mostrarError("Email incorrecto."); return; }
        if (!txtProveedorCp.getText().trim().isEmpty() && !validarCP(txtProveedorCp.getText().trim())) { mostrarError("El CP debe tener 5 dígitos."); return; }

        // 2. Creación
        Proveedor p = new Proveedor();
        p.setNombre(txtProveedorNombre.getText().trim());
        p.setNif(nif);
        p.setEmail(txtProveedorCorreo.getText().trim());
        p.setTelefono(txtProveedorTelefono.getText().trim());
        p.setCodigo(0); 

        // 3. Guardado
        long entidadId = proveedorController.añadir(p, empresa.getId());
        p.setId(entidadId);

        // 4. Dirección
        if (entidadId > 0) {
            añadirDireccionSiRellenada(entidadId, comboProveedorDireccion, txtProveedorDireccion, txtProveedorCp, txtProveedorCiudad, txtProveedorProvincia, txtProveedorPais);
        }

        refrescarProveedores();
        limpiarProveedor();
        mostrarInfo("Proveedor añadido correctamente.");
    }

    // =========================================================================
    // LÓGICA DE NEGOCIO (CRUD) - PRODUCTOS
    // =========================================================================
    private void añadirProducto() {
        if (empresa == null) { mostrarError("No hay empresa activa."); return; }

        // 1. Validaciones Numéricas
        if (!campoEsValido(txtProductoCodigo, "Código")) return;
        if (!campoEsValido(txtProductoDescripcion, "Descripción")) return;
        if (!esDecimalValido(txtProductoPrecioCoste, "Precio Coste")) return;
        if (!esDecimalValido(txtProductoPrecioVenta, "Precio Venta")) return;
        if (!esDecimalValido(txtProductoStock, "Stock")) return;
        try { Integer.parseInt(txtProductoIva.getText().trim()); } catch (NumberFormatException e) { mostrarError("El IVA debe ser un número entero."); return; }

        Producto p = new Producto();
        p.setEmpresaId(empresa.getId());
        p.setCodigo(txtProductoCodigo.getText().trim());
        p.setDescripcion(txtProductoDescripcion.getText().trim());
        p.setReferenciaProveedor(safe(txtProductoReferencia));
        
        // Obtenemos el proveedor seleccionado del Combo
        Proveedor proveedorSeleccionado = comboProductoProveedor.getValue();
        p.setProveedorId(proveedorSeleccionado != null ? proveedorSeleccionado.getId() : null);

        p.setTipoIVAId(parseIntSafe(txtProductoIva, 0));
        p.setPrecioCoste(parseDoubleSafe(txtProductoPrecioCoste, 0.0));
        p.setPrecioVenta(parseDoubleSafe(txtProductoPrecioVenta, 0.0));
        p.setStock(parseDoubleSafe(txtProductoStock, 0.0));

        try {
            productoController.añadir(p, empresa.getId());
            refrescarProductos();
            limpiarProducto();
            mostrarInfo("Producto añadido correctamente.");
        } catch (Exception e) {
            mostrarError("Error al añadir producto: " + e.getMessage());
        }
    }

    // =========================================================================
    // LÓGICA DE NEGOCIO (CRUD) - FACTURAS (COMPLEJO)
    // =========================================================================
    private void añadirFactura() {
        if (empresa == null) { mostrarError("No hay empresa activa."); return; }
        if (lineasTemporales.isEmpty()) { mostrarError("La factura está vacía. Añade líneas."); return; }

        // Obtener la entidad seleccionada del Combo (Soluciona el error de Constraint)
        Entidad entidadSeleccionada = comboFacturaEntidad.getValue();
        if (entidadSeleccionada == null) { mostrarError("Debes seleccionar un Cliente o Proveedor."); return; }
        if (!validarCampoObligatorio(txtFacturaNumero, "Número")) return;
        if (!validarCampoObligatorio(txtFacturaFecha, "Fecha")) return;

        if (!validarCampoObligatorio(txtFacturaNumero, "Número de factura")) return;
        if (!validarCampoObligatorio(txtFacturaFecha, "Fecha")) return;

        // 2. Creación del objeto Factura
        Factura f = new Factura();
        f.setEmpresaId(empresa.getId());
        f.setEntidadId(entidadSeleccionada.getId());
        f.setTipo(mapTipoFactura(comboFacturaTipo.getValue()));
        f.setNumero(txtFacturaNumero.getText().trim());
        try { f.setFechaEmision(Date.valueOf(txtFacturaFecha.getText().trim())); } 
        catch (Exception e) { mostrarError("Formato fecha inválido (yyyy-MM-dd)"); return; }

        f.setConcepto(txtFacturaConcepto.getText().trim());
        // Los totales se toman de los campos calculados automáticamente
        f.setBaseImponible(parseDoubleSafe(txtFacturaBase, 0.0));
        f.setIvaTotal(parseDoubleSafe(txtFacturaIvaTotal, 0.0));
        f.setTotalFactura(parseDoubleSafe(txtFacturaTotal, 0.0));
        f.setEstado(comboFacturaEstado.getValue());
        f.setObservaciones(safe(txtFacturaObservaciones));

        try {
            // TRANSACCIÓN LÓGICA:
            // 1. Guardar Factura + Líneas en una sola operación
            facturaController.añadir(f, new ArrayList<>(lineasTemporales));
            
            // 2. ACTUALIZACIÓN DE STOCK (Venta resta, Compra suma)
            actualizarStockProductos(f.getTipo(), lineasTemporales);

            // 3. Refrescar todo
            refrescarFacturas();
            refrescarProductos(); // Para que el usuario vea el stock actualizado
            limpiarFactura();
            mostrarInfo("Factura creada y stock actualizado.");
        } catch (Exception e) {
            mostrarError("Error al guardar factura: " + e.getMessage());
        }
    }

    // =========================================================================
    // LÓGICA DE LÍNEAS DE FACTURA (Botones + y -)
    // =========================================================================
    @FXML
    private void handleAgregarLinea() {
        if (comboFacturaProducto.getValue() == null) { mostrarError("Selecciona un producto."); return; }
        if (!esDecimalValido(txtFacturaCantidad, "Cantidad")) return;
        if (!txtFacturaDescuento.getText().isEmpty() && !esDecimalValido(txtFacturaDescuento, "Descuento")) return;

        Producto p = comboFacturaProducto.getValue();
        double cantidad = parseDoubleSafe(txtFacturaCantidad, 1.0);
        double descuento = parseDoubleSafe(txtFacturaDescuento, 0.0);
        double precio = p.getPrecioVenta(); // Base precio venta

        // Crear objeto temporal
        LineaFactura linea = new LineaFactura();
        linea.setProductoId(p.getId());
        linea.setCantidad(cantidad);
        linea.setPrecioUnitario(precio);
        linea.setDescuento(descuento);

        lineasTemporales.add(linea); // Añadir a la lista visual
        recalcularTotalesFactura(); // Actualizar sumas

        // Limpiar campos pequeños
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

    // Calcula Base, IVA y Total sumando la lista temporal
    private void recalcularTotalesFactura() {
        double base = 0.0;
        for (LineaFactura lf : lineasTemporales) base += lf.getTotalLinea();
        double iva = base * 0.21; // IVA Fijo al 21%
        double total = base + iva;

        txtFacturaBase.setText(String.format("%.2f", base).replace(",", "."));
        txtFacturaIvaTotal.setText(String.format("%.2f", iva).replace(",", "."));
        txtFacturaTotal.setText(String.format("%.2f", total).replace(",", "."));
    }

    // =========================================================================
    // MÉTODOS DE MODIFICACIÓN Y ELIMINACIÓN
    // =========================================================================
    private void modificarCliente() {
        Cliente sel = tablaClientes.getSelectionModel().getSelectedItem();
        if (sel == null) { mostrarError("Selecciona un cliente."); return; }
        // Actualizar objeto
        sel.setNombre(safe(txtClienteNombre));
        sel.setNif(safe(txtClienteNif));
        sel.setEmail(safe(txtClienteCorreo));
        sel.setTelefono(safe(txtClienteTelefono));
        try {
            clienteController.modificar(sel);
            // Si modifica, intentamos añadir dirección si no existía o actualizar
            añadirDireccionSiRellenada(sel.getId(), comboClienteDireccion, txtClienteDireccion, txtClienteCp, txtClienteCiudad, txtClienteProvincia, txtClientePais);
            refrescarClientes();
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

    private void modificarProveedor() {
        Proveedor sel = tablaProveedores.getSelectionModel().getSelectedItem();
        if (sel == null) { mostrarError("Selecciona un proveedor."); return; }
        sel.setNombre(safe(txtProveedorNombre));
        sel.setNif(safe(txtProveedorNif));
        sel.setEmail(safe(txtProveedorCorreo));
        sel.setTelefono(safe(txtProveedorTelefono));
        try {
            proveedorController.modificar(sel);
            añadirDireccionSiRellenada(sel.getId(), comboProveedorDireccion, txtProveedorDireccion, txtProveedorCp, txtProveedorCiudad, txtProveedorProvincia, txtProveedorPais);
            refrescarProveedores();
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
    
    private void modificarProducto() {
        Producto sel = tablaProductos.getSelectionModel().getSelectedItem();
        if(sel == null) { mostrarError("Selecciona un producto."); return; }
        sel.setDescripcion(safe(txtProductoDescripcion));
        sel.setPrecioVenta(parseDoubleSafe(txtProductoPrecioVenta, sel.getPrecioVenta()));
        sel.setStock(parseDoubleSafe(txtProductoStock, sel.getStock()));
        productoController.modificar(sel);
        refrescarProductos();
        mostrarInfo("Producto modificado.");
    }
    
    private void eliminarProducto() {
        Producto sel = tablaProductos.getSelectionModel().getSelectedItem();
        if(sel != null && confirmar("¿Eliminar producto?")) {
            productoController.borrarPorCodigo(empresa.getId(), sel.getCodigo());
            refrescarProductos();
        }
    }
    
    private void eliminarFactura() {
        Factura sel = tablaFacturas.getSelectionModel().getSelectedItem();
        if(sel != null && confirmar("¿Eliminar factura?")) {
            facturaController.borrarPorId(empresa.getId(), sel.getId());
            refrescarFacturas();
        }
    }

    // =========================================================================
    // REFRESCAR TABLAS Y LIMPIAR FORMULARIOS
    // =========================================================================
    private void refrescarTodo() {
        // Carga la caché primero para que 'refrescarClientes' y 'Proveedores' la usen
        if (empresa != null) cacheDirecciones = direccionController.consultarTodosPorEmpresa(empresa.getId());
        else cacheDirecciones = new ArrayList<>();
        
        refrescarClientes();
        refrescarProveedores();
        refrescarProductos();
        refrescarFacturas();
    }

    private void refrescarClientes() {
        if (empresa != null) {
            // Recargamos caché por si hubo cambios
            cacheDirecciones = direccionController.consultarTodosPorEmpresa(empresa.getId());
            tablaClientes.setItems(FXCollections.observableArrayList(clienteController.consultarTodos(empresa.getId())));
        }
    }

    private void refrescarProveedores() {
        if (empresa != null) {
            cacheDirecciones = direccionController.consultarTodosPorEmpresa(empresa.getId());
            List<Proveedor> lista = proveedorController.consultarTodos(empresa.getId());
            tablaProveedores.setItems(FXCollections.observableArrayList(lista));
            // Actualizamos también el combo de la pestaña Productos
            if(comboProductoProveedor != null) comboProductoProveedor.setItems(FXCollections.observableArrayList(lista));
        }
    }

    private void refrescarProductos() {
        if (empresa != null) {
            List<Producto> lista = productoController.consultarTodos(empresa.getId());
            ObservableList<Producto> datos = FXCollections.observableArrayList(lista);
            if(tablaProductos != null) tablaProductos.setItems(datos);
            if(comboFacturaProducto != null) comboFacturaProducto.setItems(datos);
            
            // Refresco defensivo del combo de proveedores
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

    // =========================================================================
    // HELPERS, VALIDACIONES Y UTILIDADES
    // =========================================================================

    // Validación estricta: 7 números + 1 letra final
    private boolean validarNIF(String nif) {
        return nif != null && nif.trim().matches("^[0-9]{7}[A-Za-z]$");
    }
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

    /**
     * Actualiza el stock de los productos tras guardar una factura.
     * Venta = Restar stock. Compra = Sumar stock.
     */
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

    /**
     * Carga Clientes o Proveedores en el combo de facturas según si es Venta o Compra.
     */
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

    /**
     * Busca en la caché de direcciones en memoria.
     */
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

    private boolean existeEntidadConNif(long empresaId, String nif) {
        List<Entidad> entidades = entidadController.consultarTodos(empresaId);
        for (Entidad e : entidades) {
            if (e.getNif().equalsIgnoreCase(nif)) {
                return true; // ¡Encontrado repetido!
            }
        }
        return false; // No existe
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

    // Utilidades de conversión y limpieza
    private String safe(TextField tf) { return tf == null ? "" : tf.getText().trim(); }
    private String safeStr(String s) { return s == null ? "" : s; }
    private String safeStr(Long l) { return l == null ? "" : l.toString(); }
    private int parseIntSafe(TextField tf, int def) { try { return Integer.parseInt(tf.getText().trim()); } catch (Exception e) { return def; } }
    private double parseDoubleSafe(TextField tf, double def) { try { return Double.parseDouble(tf.getText().trim().replace(",", ".")); } catch (Exception e) { return def; } }
    private String formatDouble(Double d) { return d == null ? "" : String.format("%.2f", d); }
    private char mapTipoFactura(String s) { return "Venta".equalsIgnoreCase(s) ? 'V' : 'C'; }
    
    private void mostrarError(String msg) {
        Alert a = new Alert(AlertType.ERROR); a.setTitle("Error"); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }
    private void mostrarInfo(String msg) {
        Alert a = new Alert(AlertType.INFORMATION); a.setTitle("Info"); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }
    private boolean confirmar(String msg) {
        Alert a = new Alert(AlertType.CONFIRMATION); a.setTitle("Confirmar"); a.setHeaderText(null); a.setContentText(msg);
        return a.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
    
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
