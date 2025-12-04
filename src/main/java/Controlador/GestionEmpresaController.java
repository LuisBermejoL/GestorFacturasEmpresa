package Controlador;

import Modelo.*;
import java.io.IOException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
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

public class GestionEmpresaController {

    // Empresa activa (multiempresa)
    private Empresa empresa;
    private boolean inicializado = false;
    private List<Direccion> cacheDirecciones;

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
        // Mostrar el nombre de la empresa en el Label
        if (nombreEmpresa != null) {
            if (empresa != null) {
                nombreEmpresa.setText(empresa.getNombre() + " (" + empresa.getNif() + ")");
            } else {
                nombreEmpresa.setText("Ninguna empresa seleccionada");
            }
        }

        // Cargar datos solo si la vista ya está inicializada
        if (inicializado && empresa != null) {
            configurarTablas();
            refrescarTodo();
        }
    }

    // Controladores de negocio (delegan en DAOs)
    private final EntidadController entidadController = new EntidadController();
    private final ClienteController clienteController = new ClienteController();
    private final ProveedorController proveedorController = new ProveedorController();
    private final ProductoController productoController = new ProductoController();
    private final FacturaController facturaController = new FacturaController();
    private final DireccionController direccionController = new DireccionController();

    // TabPane principal
    @FXML
    private TabPane MenuCliente;

    // Nombre de la empresa
    @FXML
    private Label nombreEmpresa;

    @FXML
    private ImageView retroceder; // Imagen de flecha para volver atrás

    // CLIENTES
    @FXML private TableView<Cliente> tablaClientes;
    @FXML private TableColumn<Cliente, String> colClienteCodigo, colClienteNombre, colClienteNif, colClienteCorreo, colClienteTelefono;
    @FXML private TableColumn<Cliente, String> colClienteDireccion, colClienteCiudad, colClienteProvincia, colClientePais, colClienteCp;
    @FXML private TextField txtClienteNombre, txtClienteNif, txtClienteCorreo, txtClienteTelefono;
    @FXML private ComboBox<String> comboClienteDireccion;
    @FXML private TextField txtClienteDireccion, txtClienteCp, txtClienteCiudad, txtClienteProvincia, txtClientePais;

    // PROVEEDORES
    @FXML private TableView<Proveedor> tablaProveedores;
    @FXML private TableColumn<Proveedor, String> colProveedorCodigo, colProveedorNombre, colProveedorNif, colProveedorCorreo, colProveedorTelefono;
    @FXML private TableColumn<Proveedor, String> colProveedorDireccion, colProveedorCiudad, colProveedorProvincia, colProveedorPais, colProveedorCp;
    @FXML private TextField txtProveedorNombre, txtProveedorNif, txtProveedorCorreo, txtProveedorTelefono;
    @FXML private ComboBox<String> comboProveedorDireccion;
    @FXML private TextField txtProveedorDireccion, txtProveedorCp, txtProveedorCiudad, txtProveedorProvincia, txtProveedorPais;

    // PRODUCTOS
    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, String> colProductoCodigo, colProductoDescripcion, colProductoReferencia, colProductoProveedor, colProductoIva, colProductoPrecioCoste, colProductoPrecioVenta, colProductoStock;
    @FXML private TextField txtProductoCodigo, txtProductoDescripcion, txtProductoReferencia, txtProductoIva, txtProductoPrecioCoste, txtProductoPrecioVenta, txtProductoStock;
    @FXML private ComboBox<Proveedor> comboProductoProveedor;

    // FACTURAS
    @FXML private TableView<Factura> tablaFacturas;
    @FXML private TableColumn<Factura, String> colFacturaEntidadId, colFacturaTipo, colFacturaNumero, colFacturaFecha, colFacturaConcepto, colFacturaBase, colFacturaIvaTotal, colFacturaTotal, colFacturaEstado, colFacturaObservaciones;
    @FXML private TextField txtFacturaNumero, txtFacturaFecha, txtFacturaConcepto, txtFacturaBase, txtFacturaIvaTotal, txtFacturaTotal, txtFacturaObservaciones;
    @FXML private ComboBox<Entidad> comboFacturaEntidad;
    @FXML private ComboBox<String> comboFacturaTipo, comboFacturaEstado;
    @FXML private ComboBox<Producto> comboFacturaProducto;
    @FXML private TextField txtFacturaCantidad;
    @FXML private TextField txtFacturaDescuento;
    
    // Tabla pequeña para ver las líneas antes de guardar
    @FXML private TableView<LineaFactura> tablaLineasFactura;
    @FXML private TableColumn<LineaFactura, String> colLineaProducto;
    @FXML private TableColumn<LineaFactura, String> colLineaCantidad;
    @FXML private TableColumn<LineaFactura, String> colLineaPrecio;
    @FXML private TableColumn<LineaFactura, String> colLineaTotal;

    // Lista temporal en memoria (RAM)
    private javafx.collections.ObservableList<LineaFactura> lineasTemporales = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // --- 1. Configuración Básica (Combos estáticos y Navegación) ---
        if (comboFacturaTipo != null) {
            comboFacturaTipo.setItems(FXCollections.observableArrayList("Venta", "Compra"));
        }
        if (comboFacturaEstado != null) {
            comboFacturaEstado.setItems(FXCollections.observableArrayList("PENDIENTE", "PAGADA", "ANULADA"));
        }

        // Botón volver atrás
        if (retroceder != null) {
            retroceder.setOnMouseClicked(event -> volverAListaEmpresas());
        }

        // --- 2. Configurar Tablas Principales ---
        // Vincula las columnas de Clientes, Proveedores, Productos y Facturas con el modelo
        configurarTablas();

        // --- 3. Configurar Visualización de Desplegables (StringConverters) ---
        
        // A) Pestaña Producto: Selector de Proveedor -> Muestra "Nombre (NIF)"
        if (comboProductoProveedor != null) {
            comboProductoProveedor.setConverter(new javafx.util.StringConverter<Proveedor>() {
                @Override
                public String toString(Proveedor p) {
                    return p == null ? null : p.getNombre() + " (" + p.getNif() + ")";
                }
                @Override
                public Proveedor fromString(String string) { return null; }
            });
        }

        // B) Pestaña Facturas: Selector de Entidad (Cliente/Prov) -> Muestra "Nombre (NIF)"
        if (comboFacturaEntidad != null) {
            comboFacturaEntidad.setConverter(new javafx.util.StringConverter<Entidad>() {
                @Override
                public String toString(Entidad e) {
                    return e == null ? null : e.getNombre() + " (" + e.getNif() + ")";
                }
                @Override
                public Entidad fromString(String string) { return null; }
            });
        }

        // C) Pestaña Facturas: Selector de Producto (para líneas) -> Muestra "Código - Descripción"
        if (comboFacturaProducto != null) {
            comboFacturaProducto.setConverter(new javafx.util.StringConverter<Producto>() {
                @Override
                public String toString(Producto p) {
                    return p == null ? null : p.getCodigo() + " - " + p.getDescripcion();
                }
                @Override
                public Producto fromString(String s) { return null; }
            });
        }

        // --- 4. Configurar Tabla Temporal de Líneas de Factura ---
        
        // Columna Producto: Muestra el nombre buscando por ID
        if (colLineaProducto != null) {
            colLineaProducto.setCellValueFactory(cell -> 
                new SimpleStringProperty(obtenerNombreProducto(cell.getValue().getProductoId())));
        }
        // Columna Cantidad
        if (colLineaCantidad != null) {
            colLineaCantidad.setCellValueFactory(cell -> 
                new SimpleStringProperty(String.valueOf(cell.getValue().getCantidad())));
        }
        // Columna Precio
        if (colLineaPrecio != null) {
            colLineaPrecio.setCellValueFactory(cell -> 
                new SimpleStringProperty(String.format("%.2f", cell.getValue().getPrecioUnitario())));
        }
        // Columna Total
        if (colLineaTotal != null) {
            colLineaTotal.setCellValueFactory(cell -> 
                new SimpleStringProperty(String.format("%.2f", cell.getValue().getTotalLinea())));
        }
        // Enlazar datos
        if (tablaLineasFactura != null) {
            tablaLineasFactura.setItems(lineasTemporales);
        }

        // --- 5. Lógica Dinámica de Facturas ---
        
        // Listener: Cuando cambia "Venta/Compra", cargamos Clientes o Proveedores
        if (comboFacturaTipo != null) {
            comboFacturaTipo.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    cargarEntidadesEnFactura(newVal);
                }
            });
        }

        // --- 6. Finalizar ---
        inicializado = true;

        // Selección por defecto para evitar listas vacías al iniciar
        if (comboFacturaTipo != null) {
            comboFacturaTipo.getSelectionModel().select("Venta");
        }
    }

    // Configurar columnas de tablas
    private void configurarTablas() {
        // --- Clientes ---
        colClienteCodigo.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getCodigo())));
        colClienteNombre.setCellValueFactory(c -> new SimpleStringProperty(safeStr(c.getValue().getNombre())));
        colClienteNif.setCellValueFactory(c -> new SimpleStringProperty(safeStr(c.getValue().getNif())));
        colClienteCorreo.setCellValueFactory(c -> new SimpleStringProperty(safeStr(c.getValue().getEmail())));
        colClienteTelefono.setCellValueFactory(c -> new SimpleStringProperty(safeStr(c.getValue().getTelefono())));
        // Direcciones calculadas dinámicamente
        colClienteDireccion.setCellValueFactory(c -> new SimpleStringProperty(obtenerDireccionCampo(c.getValue().getId(), "direccion")));
        colClienteCiudad.setCellValueFactory(c -> new SimpleStringProperty(obtenerDireccionCampo(c.getValue().getId(), "ciudad")));
        colClienteProvincia.setCellValueFactory(c -> new SimpleStringProperty(obtenerDireccionCampo(c.getValue().getId(), "provincia")));
        colClientePais.setCellValueFactory(c -> new SimpleStringProperty(obtenerDireccionCampo(c.getValue().getId(), "pais")));
        colClienteCp.setCellValueFactory(c -> new SimpleStringProperty(obtenerDireccionCampo(c.getValue().getId(), "cp")));

        // --- Proveedores ---
        colProveedorCodigo.setCellValueFactory(p -> new SimpleStringProperty(String.valueOf(p.getValue().getCodigo())));
        colProveedorNombre.setCellValueFactory(p -> new SimpleStringProperty(safeStr(p.getValue().getNombre())));
        colProveedorNif.setCellValueFactory(p -> new SimpleStringProperty(safeStr(p.getValue().getNif())));
        colProveedorCorreo.setCellValueFactory(p -> new SimpleStringProperty(safeStr(p.getValue().getEmail())));
        colProveedorTelefono.setCellValueFactory(p -> new SimpleStringProperty(safeStr(p.getValue().getTelefono())));
        // Direcciones calculadas dinámicamente
        colProveedorDireccion.setCellValueFactory(p -> new SimpleStringProperty(obtenerDireccionCampo(p.getValue().getId(), "direccion")));
        colProveedorCiudad.setCellValueFactory(p -> new SimpleStringProperty(obtenerDireccionCampo(p.getValue().getId(), "ciudad")));
        colProveedorProvincia.setCellValueFactory(p -> new SimpleStringProperty(obtenerDireccionCampo(p.getValue().getId(), "provincia")));
        colProveedorPais.setCellValueFactory(p -> new SimpleStringProperty(obtenerDireccionCampo(p.getValue().getId(), "pais")));
        colProveedorCp.setCellValueFactory(p -> new SimpleStringProperty(obtenerDireccionCampo(p.getValue().getId(), "cp")));

        // --- Productos ---
        colProductoCodigo.setCellValueFactory(p -> new SimpleStringProperty(safeStr(p.getValue().getCodigo())));
        colProductoDescripcion.setCellValueFactory(p -> new SimpleStringProperty(safeStr(p.getValue().getDescripcion())));
        colProductoReferencia.setCellValueFactory(p -> new SimpleStringProperty(safeStr(p.getValue().getReferenciaProveedor())));
        colProductoProveedor.setCellValueFactory(p -> new SimpleStringProperty(safeStr(p.getValue().getProveedorId())));
        colProductoIva.setCellValueFactory(p -> new SimpleStringProperty(String.valueOf(p.getValue().getTipoIVAId())));
        colProductoPrecioCoste.setCellValueFactory(p -> new SimpleStringProperty(formatDouble(p.getValue().getPrecioCoste())));
        colProductoPrecioVenta.setCellValueFactory(p -> new SimpleStringProperty(formatDouble(p.getValue().getPrecioVenta())));
        colProductoStock.setCellValueFactory(p -> new SimpleStringProperty(formatDouble(p.getValue().getStock())));

        // --- Facturas ---
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

    // Botones inferiores (genéricos)
    @FXML
    private void handleAñadir() {
        String tabName = MenuCliente.getSelectionModel().getSelectedItem().getText();
        if ("Cliente".equals(tabName)) {
            añadirCliente();
        } else if ("Proveedor".equals(tabName)) {
            añadirProveedor();
        } else if ("Producto".equals(tabName)) {
            añadirProducto();
        } else if ("Facturas".equals(tabName)) {
            añadirFactura();
        }
    }

    @FXML
    private void handleModificar() {
        String tabName = MenuCliente.getSelectionModel().getSelectedItem().getText();
        if ("Cliente".equals(tabName)) {
            modificarCliente();
        } else if ("Proveedor".equals(tabName)) {
            modificarProveedor();
        } else if ("Producto".equals(tabName)) {
            modificarProducto();
        } else if ("Facturas".equals(tabName)) {
            modificarFactura();
        }
    }

    @FXML
    private void handleConsultar() {
        String tabName = MenuCliente.getSelectionModel().getSelectedItem().getText();
        if ("Cliente".equals(tabName)) {
            refrescarClientes();
        } else if ("Proveedor".equals(tabName)) {
            refrescarProveedores();
        } else if ("Producto".equals(tabName)) {
            refrescarProductos();
        } else if ("Facturas".equals(tabName)) {
            refrescarFacturas();
        }
    }

    @FXML
    private void handleEliminar() {
        String tabName = MenuCliente.getSelectionModel().getSelectedItem().getText();
        if ("Cliente".equals(tabName)) {
            eliminarCliente();
        } else if ("Proveedor".equals(tabName)) {
            eliminarProveedor();
        } else if ("Producto".equals(tabName)) {
            eliminarProducto();
        } else if ("Facturas".equals(tabName)) {
            eliminarFactura();
        }
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

    // Validaciones y alertas
    private boolean validarCampoObligatorio(TextField tf, String nombreCampo) {
        if (tf == null || tf.getText().trim().isEmpty()) {
            mostrarError("El campo '" + nombreCampo + "' es obligatorio.");
            return false;
        }
        return true;
    }

    private boolean validarNumero(TextField tf, String nombreCampo) {
        try {
            Double.parseDouble(tf.getText().trim());
            return true;
        } catch (Exception e) {
            mostrarError("El campo '" + nombreCampo + "' debe ser numérico.");
            return false;
        }
    }

    private void mostrarError(String msg) {
        Alert a = new Alert(AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void mostrarInfo(String msg) {
        Alert a = new Alert(AlertType.INFORMATION);
        a.setTitle("Información");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private boolean confirmar(String msg) {
        Alert a = new Alert(AlertType.CONFIRMATION);
        a.setTitle("Confirmación");
        a.setHeaderText(null);
        a.setContentText(msg);
        return a.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    /**
     * Comprueba si existe ya una entidad con ese NIF en la empresa.
     */
    private boolean existeEntidadConNif(long empresaId, String nif) {
        List<Entidad> entidades = entidadController.consultarTodos(empresaId);
        for (Entidad e : entidades) {
            if (e.getNif() != null && e.getNif().equalsIgnoreCase(nif)) {
                return true;
            }
        }
        return false;
    }

    // CRUD Clientes
    private void añadirCliente() {
        if (empresa == null) {
            mostrarError("No hay empresa activa.");
            return;
        }
        if (!validarCampoObligatorio(txtClienteNombre, "Nombre")) {
            return;
        }
        if (!validarCampoObligatorio(txtClienteNif, "NIF")) {
            return;
        }

        String nif = txtClienteNif.getText().trim();

        // Validar duplicado en ENTIDAD
        if (existeEntidadConNif(empresa.getId(), nif)) {
            mostrarError("Ya existe una entidad con ese NIF en esta empresa.");
        } else {
            Cliente c = new Cliente();
            c.setNombre(txtClienteNombre.getText().trim());
            c.setNif(nif);
            c.setEmail(safe(txtClienteCorreo));
            c.setTelefono(safe(txtClienteTelefono));
            c.setCodigo(0);

            long entidadId = clienteController.añadir(c, empresa.getId());
            c.setId(entidadId);

            if (entidadId > 0) {
                añadirDireccionSiRellenada(entidadId, comboClienteDireccion,
                        txtClienteDireccion, txtClienteCp, txtClienteCiudad,
                        txtClienteProvincia, txtClientePais);
            }

            refrescarClientes();
            limpiarCliente();
            mostrarInfo("Cliente añadido correctamente.");
        }
    }

    private void modificarCliente() {
        Cliente sel = tablaClientes.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarError("Selecciona un cliente de la tabla.");
            return;
        }
        sel.setNombre(safe(txtClienteNombre));
        sel.setNif(safe(txtClienteNif));
        sel.setEmail(safe(txtClienteCorreo));
        sel.setTelefono(safe(txtClienteTelefono));
        try {
            clienteController.modificar(sel);
            añadirDireccionSiRellenada(sel.getId(), comboClienteDireccion, txtClienteDireccion, txtClienteCp, txtClienteCiudad, txtClienteProvincia, txtClientePais);
            refrescarClientes();
            mostrarInfo("Cliente modificado.");
        } catch (Exception e) {
            mostrarError("Error al modificar cliente: " + e.getMessage());
        }
    }

    private void eliminarCliente() {
        Cliente sel = tablaClientes.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarError("Selecciona un cliente de la tabla.");
            return;
        }
        if (!confirmar("¿Eliminar el cliente seleccionado?")) {
            return;
        }
        try {
            clienteController.borrarPorId(sel.getId());
            refrescarClientes();
            mostrarInfo("Cliente eliminado.");
        } catch (Exception e) {
            mostrarError("Error al eliminar cliente: " + e.getMessage());
        }
    }

    private void refrescarClientes() {
        try {
            // 1. Recargar caché de direcciones
            if (empresa != null) {
                cacheDirecciones = direccionController.consultarTodosPorEmpresa(empresa.getId());
            }
            
            // 2. Cargar clientes
            tablaClientes.setItems(FXCollections.observableArrayList(clienteController.consultarTodos(empresa.getId())));
        } catch (Exception e) {
            mostrarError("Error al consultar clientes: " + e.getMessage());
        }
    }

    private void limpiarCliente() {
        txtClienteNombre.clear();
        txtClienteNif.clear();
        txtClienteCorreo.clear();
        txtClienteTelefono.clear();
        comboClienteDireccion.getSelectionModel().clearSelection();
        txtClienteDireccion.clear();
        txtClienteCp.clear();
        txtClienteCiudad.clear();
        txtClienteProvincia.clear();
        txtClientePais.clear();
    }

    // CRUD Proveedores
    private void añadirProveedor() {
        if (empresa == null) {
            mostrarError("No hay empresa activa.");
            return;
        }
        if (!validarCampoObligatorio(txtProveedorNombre, "Nombre")) {
            return;
        }
        if (!validarCampoObligatorio(txtProveedorNif, "NIF")) {
            return;
        }

        String nif = txtProveedorNif.getText().trim();

        // Validar duplicado en ENTIDAD
        if (existeEntidadConNif(empresa.getId(), nif)) {
            mostrarError("Ya existe una entidad con ese NIF en esta empresa.");
        } else {
            Proveedor p = new Proveedor();
            p.setNombre(txtProveedorNombre.getText().trim());
            p.setNif(nif);
            p.setEmail(safe(txtProveedorCorreo));
            p.setTelefono(safe(txtProveedorTelefono));
            p.setCodigo(0);

            long entidadId = proveedorController.añadir(p, empresa.getId());
            p.setId(entidadId);

            if (entidadId > 0) {
                añadirDireccionSiRellenada(entidadId, comboProveedorDireccion,
                        txtProveedorDireccion, txtProveedorCp, txtProveedorCiudad,
                        txtProveedorProvincia, txtProveedorPais);
            }

            refrescarProveedores();
            limpiarProveedor();
            mostrarInfo("Proveedor añadido correctamente.");
        }
    }

    private void modificarProveedor() {
        Proveedor sel = tablaProveedores.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarError("Selecciona un proveedor de la tabla.");
            return;
        }
        sel.setNombre(safe(txtProveedorNombre));
        sel.setNif(safe(txtProveedorNif));
        sel.setEmail(safe(txtProveedorCorreo));
        sel.setTelefono(safe(txtProveedorTelefono));
        try {
            proveedorController.modificar(sel);
            añadirDireccionSiRellenada(sel.getId(), comboProveedorDireccion, txtProveedorDireccion, txtProveedorCp, txtProveedorCiudad, txtProveedorProvincia, txtProveedorPais);
            refrescarProveedores();
            mostrarInfo("Proveedor modificado.");
        } catch (Exception e) {
            mostrarError("Error al modificar proveedor: " + e.getMessage());
        }
    }

    private void eliminarProveedor() {
        Proveedor sel = tablaProveedores.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarError("Selecciona un proveedor de la tabla.");
            return;
        }
        if (!confirmar("¿Eliminar el proveedor seleccionado?")) {
            return;
        }
        try {
            proveedorController.borrarPorId(sel.getId());
            refrescarProveedores();
            mostrarInfo("Proveedor eliminado.");
        } catch (Exception e) {
            mostrarError("Error al eliminar proveedor: " + e.getMessage());
        }
    }

    private void refrescarProveedores() {
        try {
            // 1. Recargar la caché de direcciones desde la BD
            // Si no hacemos esto, la nueva dirección no aparecerá hasta reiniciar la ventana
            if (empresa != null) {
                cacheDirecciones = direccionController.consultarTodosPorEmpresa(empresa.getId());
            }

            // 2. Cargar la lista de proveedores
            List<Proveedor> listaProveedores = proveedorController.consultarTodos(empresa.getId());
            tablaProveedores.setItems(FXCollections.observableArrayList(listaProveedores));
            
            // 3. Actualizar también el selector de la pestaña Productos (si lo añadiste)
            if (comboProductoProveedor != null) {
                comboProductoProveedor.setItems(FXCollections.observableArrayList(listaProveedores));
            }

        } catch (Exception e) {
            mostrarError("Error al consultar proveedores: " + e.getMessage());
        }
    }

    private void limpiarProveedor() {
        txtProveedorNombre.clear();
        txtProveedorNif.clear();
        txtProveedorCorreo.clear();
        txtProveedorTelefono.clear();
        comboProveedorDireccion.getSelectionModel().clearSelection();
        txtProveedorDireccion.clear();
        txtProveedorCp.clear();
        txtProveedorCiudad.clear();
        txtProveedorProvincia.clear();
        txtProveedorPais.clear();
    }

    // CRUD Productos
    private void añadirProducto() {
        if (empresa == null) {
            mostrarError("No hay empresa activa.");
            return;
        }
        if (!validarCampoObligatorio(txtProductoCodigo, "Código")) {
            return;
        }
        if (!validarCampoObligatorio(txtProductoDescripcion, "Descripción")) {
            return;
        }
        if (!validarNumero(txtProductoPrecioCoste, "Precio Coste")) {
            return;
        }
        if (!validarNumero(txtProductoPrecioVenta, "Precio Venta")) {
            return;
        }
        if (!validarNumero(txtProductoStock, "Stock")) {
            return;
        }

        Producto p = new Producto();
        p.setEmpresaId(empresa.getId());
        p.setCodigo(txtProductoCodigo.getText().trim());
        p.setDescripcion(txtProductoDescripcion.getText().trim());
        p.setReferenciaProveedor(safe(txtProductoReferencia));
        
        // Obtener el ID del proveedor seleccionado
        Proveedor proveedorSeleccionado = comboProductoProveedor.getValue();
        if (proveedorSeleccionado != null) {
            p.setProveedorId(proveedorSeleccionado.getId());
        } else {
            p.setProveedorId(null); // Opcional: permitir productos sin proveedor
        }

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

    private void modificarProducto() {
        Producto sel = tablaProductos.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarError("Selecciona un producto de la tabla.");
            return;
        }
        String desc = safe(txtProductoDescripcion);
        if (!desc.isEmpty()) {
            sel.setDescripcion(desc);
        }
        String ref = safe(txtProductoReferencia);
        if (!ref.isEmpty()) {
            sel.setReferenciaProveedor(ref);
        }
        String ivaTxt = safe(txtProductoIva);
        if (!ivaTxt.isEmpty()) {
            sel.setTipoIVAId(parseIntSafe(txtProductoIva, sel.getTipoIVAId()));
        }
        String coste = safe(txtProductoPrecioCoste);
        if (!coste.isEmpty()) {
            sel.setPrecioCoste(parseDoubleSafe(txtProductoPrecioCoste, sel.getPrecioCoste()));
        }
        String venta = safe(txtProductoPrecioVenta);
        if (!venta.isEmpty()) {
            sel.setPrecioVenta(parseDoubleSafe(txtProductoPrecioVenta, sel.getPrecioVenta()));
        }
        String stock = safe(txtProductoStock);
        if (!stock.isEmpty()) {
            sel.setStock(parseDoubleSafe(txtProductoStock, sel.getStock()));
        }
        try {
            productoController.modificar(sel);
            refrescarProductos();
            mostrarInfo("Producto modificado.");
        } catch (Exception e) {
            mostrarError("Error al modificar producto: " + e.getMessage());
        }
    }

    private void eliminarProducto() {
        Producto sel = tablaProductos.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarError("Selecciona un producto de la tabla.");
            return;
        }
        if (!confirmar("¿Eliminar el producto seleccionado?")) {
            return;
        }
        try {
            productoController.borrarPorCodigo(empresa.getId(), sel.getCodigo());
            refrescarProductos();
            mostrarInfo("Producto eliminado.");
        } catch (Exception e) {
            mostrarError("Error al eliminar producto: " + e.getMessage());
        }
    }

    private void refrescarProductos() {
        try {
            // 1. Obtener datos de la base de datos
            List<Producto> lista = productoController.consultarTodos(empresa.getId());
            ObservableList<Producto> datos = FXCollections.observableArrayList(lista);

            // 2. Actualizar Tabla de Productos (Pestaña Producto)
            if (tablaProductos != null) {
                tablaProductos.setItems(datos);
            }

            // 3. Actualizar Desplegable de Proveedores en Producto (si existe)
            if (comboProductoProveedor != null) {
                 // Aquí deberíamos cargar PROVEEDORES, no productos. 
                 // (Corrigiendo posible error lógico previo si copiaste y pegaste rápido)
                 List<Proveedor> listaProvs = proveedorController.consultarTodos(empresa.getId());
                 comboProductoProveedor.setItems(FXCollections.observableArrayList(listaProvs));
            }

            // 4. Actualizar Desplegable de Productos en Factura (Pestaña Facturas)
            if (comboFacturaProducto != null) {
                comboFacturaProducto.setItems(datos);
            }

        } catch (Exception e) {
            mostrarError("Error al refrescar productos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void limpiarProducto() {
        txtProductoCodigo.clear();
        txtProductoDescripcion.clear();
        txtProductoReferencia.clear();
        comboProductoProveedor.getSelectionModel().clearSelection();
        txtProductoIva.clear();
        txtProductoPrecioCoste.clear();
        txtProductoPrecioVenta.clear();
        txtProductoStock.clear();
    }

    // CRUD Facturas
    private void añadirFactura() {
        // 1. Validaciones iniciales
        if (empresa == null) {
            mostrarError("No hay empresa activa.");
            return;
        }
        if (lineasTemporales.isEmpty()) {
            mostrarError("No puedes guardar una factura vacía. Añade líneas primero.");
            return;
        }
        
        // CORRECCIÓN DE ERROR: Usamos el ComboBox en lugar del TextField manual
        Entidad entidadSeleccionada = comboFacturaEntidad.getValue();
        if (entidadSeleccionada == null) {
            mostrarError("Debes seleccionar un Cliente o Proveedor.");
            return;
        }

        if (!validarCampoObligatorio(txtFacturaNumero, "Número de factura")) return;
        if (!validarCampoObligatorio(txtFacturaFecha, "Fecha")) return;

        // 2. Creación del objeto Factura
        Factura f = new Factura();
        f.setEmpresaId(empresa.getId());
        f.setEntidadId(entidadSeleccionada.getId()); // Usamos el ID del objeto seleccionado
        f.setTipo(mapTipoFactura(comboFacturaTipo.getValue()));
        f.setNumero(txtFacturaNumero.getText().trim());

        try {
            f.setFechaEmision(Date.valueOf(txtFacturaFecha.getText().trim()));
        } catch (IllegalArgumentException e) {
            mostrarError("Formato de fecha inválido. Usa yyyy-MM-dd.");
            return;
        }

        f.setConcepto(txtFacturaConcepto.getText().trim());
        
        // Totales calculados automáticamente
        f.setBaseImponible(parseDoubleSafe(txtFacturaBase, 0.0));
        f.setIvaTotal(parseDoubleSafe(txtFacturaIvaTotal, 0.0));
        f.setTotalFactura(parseDoubleSafe(txtFacturaTotal, 0.0));
        
        f.setEstado(comboFacturaEstado.getValue());
        f.setObservaciones(safe(txtFacturaObservaciones));

        try {
            // 3. GUARDAR FACTURA Y LÍNEAS EN BASE DE DATOS
            facturaController.añadir(f, new ArrayList<>(lineasTemporales));

            // 4. ACTUALIZACIÓN DE STOCK (NUEVO)
            // Si la factura se guardó bien, actualizamos el stock de los productos
            actualizarStockProductos(f.getTipo(), lineasTemporales);

            // 5. Refrescar interfaz
            refrescarFacturas();
            refrescarProductos(); // Para ver el nuevo stock en la otra pestaña
            limpiarFactura();
            mostrarInfo("Factura creada correctamente y stock actualizado.");

        } catch (Exception e) {
            mostrarError("Error al guardar la factura: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void actualizarStockProductos(char tipoFactura, List<LineaFactura> lineas) {
        for (LineaFactura linea : lineas) {
            // Buscamos el producto original
            Producto p = productoController.consultarPorCodigo(empresa.getId(), 
                         obtenerCodigoProducto(linea.getProductoId())); 
            // (Nota: Si tienes un método 'consultarPorId' en ProductoController sería más directo, 
            // pero este funciona buscando por código si ya lo tienes implementado)
            
            if (p != null) {
                double nuevoStock;
                if (tipoFactura == 'V') {
                    // VENTA: El producto sale del almacén (Resta)
                    nuevoStock = p.getStock() - linea.getCantidad();
                } else {
                    // COMPRA: El producto entra al almacén (Suma)
                    nuevoStock = p.getStock() + linea.getCantidad();
                }
                p.setStock(nuevoStock);
                
                // Guardamos el cambio en la base de datos
                productoController.modificar(p);
            }
        }
    }
    
    // Método auxiliar necesario si no tienes 'consultarPorId'
    private String obtenerCodigoProducto(long id) {
        for(Producto p : tablaProductos.getItems()) {
            if(p.getId() == id) return p.getCodigo();
        }
        return "";
    }

    private void modificarFactura() {
        Factura sel = tablaFacturas.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarError("Selecciona una factura de la tabla.");
            return;
        }
        if (comboFacturaTipo.getValue() != null) {
            sel.setTipo(mapTipoFactura(comboFacturaTipo.getValue()));
        }
        String numero = safe(txtFacturaNumero);
        if (!numero.isEmpty()) {
            sel.setNumero(numero);
        }
        String fechaTxt = safe(txtFacturaFecha);
        if (!fechaTxt.isEmpty()) {
            try {
                sel.setFechaEmision(Date.valueOf(fechaTxt));
            } catch (IllegalArgumentException fe) {
                mostrarError("Formato de fecha inválido (yyyy-MM-dd).");
                return;
            }
        }
        String concepto = safe(txtFacturaConcepto);
        if (!concepto.isEmpty()) {
            sel.setConcepto(concepto);
        }
        String base = safe(txtFacturaBase);
        if (!base.isEmpty()) {
            sel.setBaseImponible(parseDoubleSafe(txtFacturaBase, sel.getBaseImponible()));
        }
        String iva = safe(txtFacturaIvaTotal);
        if (!iva.isEmpty()) {
            sel.setIvaTotal(parseDoubleSafe(txtFacturaIvaTotal, sel.getIvaTotal()));
        }
        String total = safe(txtFacturaTotal);
        if (!total.isEmpty()) {
            sel.setTotalFactura(parseDoubleSafe(txtFacturaTotal, sel.getTotalFactura()));
        }
        if (comboFacturaEstado.getValue() != null) {
            sel.setEstado(comboFacturaEstado.getValue());
        }
        String obs = safe(txtFacturaObservaciones);
        if (!obs.isEmpty()) {
            sel.setObservaciones(obs);
        }

        try {
            facturaController.modificar(sel);
            refrescarFacturas();
            mostrarInfo("Factura modificada.");
        } catch (Exception e) {
            mostrarError("Error al modificar factura: " + e.getMessage());
        }
    }

    private void eliminarFactura() {
        Factura sel = tablaFacturas.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarError("Selecciona una factura de la tabla.");
            return;
        }
        if (!confirmar("¿Eliminar la factura seleccionada?")) {
            return;
        }
        try {
            facturaController.borrarPorId(empresa.getId(), sel.getId());
            refrescarFacturas();
            mostrarInfo("Factura eliminada.");
        } catch (Exception e) {
            mostrarError("Error al eliminar factura: " + e.getMessage());
        }
    }

    private void refrescarFacturas() {
        try {
            tablaFacturas.setItems(FXCollections.observableArrayList(facturaController.consultarTodas(empresa.getId())));
        } catch (Exception e) {
            mostrarError("Error al consultar facturas: " + e.getMessage());
        }
    }

    private void limpiarFactura() {
        // Cabecera
        txtFacturaNumero.clear();
        txtFacturaFecha.clear();
        comboFacturaEntidad.getSelectionModel().clearSelection(); // Limpia el combo nuevo
        comboFacturaTipo.getSelectionModel().clearSelection();
        
        // Detalles
        txtFacturaConcepto.clear();
        txtFacturaObservaciones.clear();
        comboFacturaEstado.getSelectionModel().clearSelection();
        
        // Totales (se limpian solos pero por si acaso)
        txtFacturaBase.clear();
        txtFacturaIvaTotal.clear();
        txtFacturaTotal.clear();
        
        // Sección de líneas (mini formulario)
        txtFacturaCantidad.clear();
        txtFacturaDescuento.clear();
        comboFacturaProducto.getSelectionModel().clearSelection();
        
        // Vaciar la lista temporal de líneas
        lineasTemporales.clear();
    }

    // Refrescar todas las pestañas
    private void refrescarTodo() {
        // 1. CARGAMOS LA CACHÉ DE DIRECCIONES (Solo 1 consulta a la BD)
        if (empresa != null) {
            cacheDirecciones = direccionController.consultarTodosPorEmpresa(empresa.getId());
        } else {
            cacheDirecciones = new java.util.ArrayList<>();
        }

        // 2. Ahora refrescamos las tablas (que usarán la caché)
        refrescarClientes();
        refrescarProveedores();
        refrescarProductos();
        refrescarFacturas();
    }

    // Utilidades
    private String safeStr(Long l) {
        return l == null ? "" : l.toString();
    }

    private String safe(TextField tf) {
        return tf == null ? "" : tf.getText().trim();
    }

    private String safeStr(String s) {
        return s == null ? "" : s;
    }

    private int parseIntSafe(TextField tf, int def) {
        try {
            return Integer.parseInt(tf.getText().trim());
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private double parseDoubleSafe(TextField tf, double def) {
        try {
            return Double.parseDouble(tf.getText().trim());
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private String formatDouble(Double d) {
        return d == null ? "" : String.format("%.2f", d);
    }

    private char mapTipoFactura(String tipoUi) {
        return "Venta".equalsIgnoreCase(tipoUi) ? 'V' : 'C';
    }

    // Dirección: obtener un campo para mostrar en tabla (primer registro encontrado)
    private String obtenerDireccionCampo(Long entidadId, String campo) {
        // Validación básica
        if (empresa == null || entidadId == null || cacheDirecciones == null) {
            return "";
        }

        // Buscamos en la LISTA EN MEMORIA (Instantáneo)
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

    // Dirección: crear si hay datos suficientes (etiqueta y dirección)
    private void añadirDireccionSiRellenada(Long entidadId,
            ComboBox<String> comboEtiqueta,
            TextField txtDir, TextField txtCp, TextField txtCiudad,
            TextField txtProv, TextField txtPais) {
        if (entidadId == null || entidadId <= 0) {
            return;
        }
        String etiqueta = comboEtiqueta != null ? comboEtiqueta.getValue() : null;
        String direccion = safe(txtDir);
        if (etiqueta == null || etiqueta.isEmpty() || direccion.isEmpty()) {
            return;
        }

        Direccion d = new Direccion();
        d.setEntidadId(entidadId);
        d.setEtiqueta(etiqueta);
        d.setDireccion(direccion);
        d.setCp(safe(txtCp));
        d.setCiudad(safe(txtCiudad));
        d.setProvincia(safe(txtProv));
        d.setPais(safe(txtPais));

        try {
            direccionController.añadir(d);
        } catch (Exception e) {
            mostrarError("Error al añadir dirección: " + e.getMessage());
        }
    }

    /**
     * Cierra la ventana actual y vuelve a cargar la lista de empresas.
     */
    private void volverAListaEmpresas() {
        try {
            // 1. Cargar el FXML de la lista de empresas (AbrirEmpresaController.java)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/luis/gestorfacturasempresa/abrirListaEmpresas.fxml"));
            Parent root = loader.load();

            // 2. Crear nueva ventana (Stage) para la lista
            Stage listaStage = new Stage();
            listaStage.setTitle("Lista de Empresas");
            listaStage.setScene(new Scene(root));

            // Centrar la ventana al abrir
            listaStage.centerOnScreen();

            // 4. Mostrar la ventana de lista de empresas
            listaStage.show();

            // 3. Cerrar la ventana actual (la de detalle de empresa)
            // Usamos el nodo 'retroceder' para obtener el Stage actual y cerrarlo.
            Stage actualStage = (Stage) retroceder.getScene().getWindow();
            actualStage.close();

        } catch (IOException ex) {
            System.err.println("Error al cargar la lista de empresas: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}