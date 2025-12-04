package Controlador;

import Modelo.*;
import java.io.IOException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

import java.sql.Date;
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class GestionEmpresaController {

    // Empresa activa (multiempresa)
    private Empresa empresa;
    private boolean inicializado = false;

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
    @FXML
    private TableView<Cliente> tablaClientes;
    @FXML
    private TableColumn<Cliente, String> colClienteNombre, colClienteNif, colClienteCorreo, colClienteTelefono;
    @FXML
    private TableColumn<Cliente, String> colClienteDireccion, colClienteCiudad, colClienteCp;
    @FXML
    private TextField txtClienteNombre, txtClienteNif, txtClienteCorreo, txtClienteTelefono;
    @FXML
    private ComboBox<String> comboClienteDireccion;
    @FXML
    private TextField txtClienteDireccion, txtClienteCp, txtClienteCiudad, txtClienteProvincia, txtClientePais;

    // PROVEEDORES
    @FXML
    private TableView<Proveedor> tablaProveedores;
    @FXML
    private TableColumn<Proveedor, String> colProveedorNombre, colProveedorNif, colProveedorCorreo, colProveedorTelefono;
    @FXML
    private TableColumn<Proveedor, String> colProveedorDireccion, colProveedorCiudad, colProveedorCp;
    @FXML
    private TextField txtProveedorNombre, txtProveedorNif, txtProveedorCorreo, txtProveedorTelefono;
    @FXML
    private ComboBox<String> comboProveedorDireccion;
    @FXML
    private TextField txtProveedorDireccion, txtProveedorCp, txtProveedorCiudad, txtProveedorProvincia, txtProveedorPais;

    // PRODUCTOS
    @FXML
    private TableView<Producto> tablaProductos;
    @FXML
    private TableColumn<Producto, String> colProductoCodigo, colProductoDescripcion, colProductoReferencia, colProductoIva,
            colProductoPrecioCoste, colProductoPrecioVenta, colProductoStock;
    @FXML
    private TextField txtProductoCodigo, txtProductoDescripcion, txtProductoReferencia,
            txtProductoIva, txtProductoPrecioCoste, txtProductoPrecioVenta, txtProductoStock;

    // FACTURAS
    @FXML
    private TableView<Factura> tablaFacturas;
    @FXML
    private TableColumn<Factura, String> colFacturaEntidadId, colFacturaTipo, colFacturaNumero, colFacturaFecha, colFacturaIvaTotal,
            colFacturaTotal, colFacturaEstado;
    @FXML
    private TextField txtFacturaEntidadID, txtFacturaNumero, txtFacturaFecha, txtFacturaConcepto,
            txtFacturaBase, txtFacturaIvaTotal, txtFacturaTotal, txtFacturaObservaciones;
    @FXML
    private ComboBox<String> comboFacturaTipo, comboFacturaEstado;

    @FXML
    private void initialize() {
        comboFacturaTipo.setItems(FXCollections.observableArrayList("Venta", "Compra"));
        comboFacturaEstado.setItems(FXCollections.observableArrayList("PENDIENTE", "PAGADA", "ANULADA"));

        // Evento de clic en la flecha retroceder → volver a la lista de empresas
        retroceder.setOnMouseClicked(event -> volverAListaEmpresas());
    }

    // Configurar columnas de tablas
    private void configurarTablas() {
        // Clientes
        colClienteNombre.setCellValueFactory(c -> new SimpleStringProperty(safeStr(c.getValue().getNombre())));
        colClienteNif.setCellValueFactory(c -> new SimpleStringProperty(safeStr(c.getValue().getNif())));
        colClienteCorreo.setCellValueFactory(c -> new SimpleStringProperty(safeStr(c.getValue().getEmail())));
        colClienteTelefono.setCellValueFactory(c -> new SimpleStringProperty(safeStr(c.getValue().getTelefono())));
        colClienteDireccion.setCellValueFactory(c -> new SimpleStringProperty(obtenerDireccionCampo(c.getValue().getId(), "direccion")));
        colClienteCiudad.setCellValueFactory(c -> new SimpleStringProperty(obtenerDireccionCampo(c.getValue().getId(), "ciudad")));
        colClienteCp.setCellValueFactory(c -> new SimpleStringProperty(obtenerDireccionCampo(c.getValue().getId(), "cp")));

        // Proveedores
        colProveedorNombre.setCellValueFactory(c -> new SimpleStringProperty(safeStr(c.getValue().getNombre())));
        colProveedorNif.setCellValueFactory(c -> new SimpleStringProperty(safeStr(c.getValue().getNif())));
        colProveedorCorreo.setCellValueFactory(c -> new SimpleStringProperty(safeStr(c.getValue().getEmail())));
        colProveedorTelefono.setCellValueFactory(c -> new SimpleStringProperty(safeStr(c.getValue().getTelefono())));
        colProveedorDireccion.setCellValueFactory(c -> new SimpleStringProperty(obtenerDireccionCampo(c.getValue().getId(), "direccion")));
        colProveedorCiudad.setCellValueFactory(c -> new SimpleStringProperty(obtenerDireccionCampo(c.getValue().getId(), "ciudad")));
        colProveedorCp.setCellValueFactory(c -> new SimpleStringProperty(obtenerDireccionCampo(c.getValue().getId(), "cp")));

        // Productos
        colProductoCodigo.setCellValueFactory(p -> new SimpleStringProperty(safeStr(p.getValue().getCodigo())));
        colProductoDescripcion.setCellValueFactory(p -> new SimpleStringProperty(safeStr(p.getValue().getDescripcion())));
        colProductoReferencia.setCellValueFactory(p -> new SimpleStringProperty(safeStr(p.getValue().getReferenciaProveedor())));
        colProductoIva.setCellValueFactory(p -> new SimpleStringProperty(String.valueOf(p.getValue().getTipoIVAId())));
        colProductoPrecioCoste.setCellValueFactory(p -> new SimpleStringProperty(formatDouble(p.getValue().getPrecioCoste())));
        colProductoPrecioVenta.setCellValueFactory(p -> new SimpleStringProperty(formatDouble(p.getValue().getPrecioVenta())));
        colProductoStock.setCellValueFactory(p -> new SimpleStringProperty(formatDouble(p.getValue().getStock())));

        // Facturas
        colFacturaEntidadId.setCellValueFactory(f -> new SimpleStringProperty(safeStr(f.getValue().getEmpresaId())));
        colFacturaTipo.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getTipo() == 'V' ? "Venta" : "Compra"));
        colFacturaNumero.setCellValueFactory(f -> new SimpleStringProperty(safeStr(f.getValue().getNumero())));
        colFacturaFecha.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getFechaEmision() != null ? f.getValue().getFechaEmision().toString() : ""));
        colFacturaIvaTotal.setCellValueFactory(f -> new SimpleStringProperty(formatDouble(f.getValue().getIvaTotal())));
        colFacturaTotal.setCellValueFactory(f -> new SimpleStringProperty(formatDouble(f.getValue().getTotalFactura())));
        colFacturaEstado.setCellValueFactory(f -> new SimpleStringProperty(safeStr(f.getValue().getEstado())));
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

    // Validaciones y alertas
    private boolean validarCampoObligatorio(TextField tf, String nombreCampo) {
        if (tf == null || tf.getText().trim().isEmpty()) {
            mostrarError("El campo '" + nombreCampo + "' es obligatorio.");
            return false;
        }
        return true;
    }

    // Comprueba si existe la entidad en la empresa
    private boolean existeEntidad(long empresaId, long entidadId) {
        List<Entidad> entidades = entidadController.consultarTodos(empresaId);
        for (Entidad e : entidades) {
            if (e.getId() == entidadId) {
                return true;
            }
        }
        return false;
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
            tablaProveedores.setItems(FXCollections.observableArrayList(proveedorController.consultarTodos(empresa.getId())));
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
            tablaProductos.setItems(FXCollections.observableArrayList(productoController.consultarTodos(empresa.getId())));
        } catch (Exception e) {
            mostrarError("Error al consultar productos: " + e.getMessage());
        }
    }

    private void limpiarProducto() {
        txtProductoCodigo.clear();
        txtProductoDescripcion.clear();
        txtProductoReferencia.clear();
        txtProductoIva.clear();
        txtProductoPrecioCoste.clear();
        txtProductoPrecioVenta.clear();
        txtProductoStock.clear();
    }

    // CRUD Facturas
    // Obtiene el id de la entidad seleccionada (cliente o proveedor)
    // Aquí debes adaptar según tu interfaz: por ejemplo, si tienes combo o selección en tabla
    private long obtenerEntidadIdSeleccionada() {
        Cliente clienteSel = tablaClientes.getSelectionModel().getSelectedItem();
        if (clienteSel != null) {
            return clienteSel.getId();
        }

        Proveedor proveedorSel = tablaProveedores.getSelectionModel().getSelectedItem();
        if (proveedorSel != null) {
            return proveedorSel.getId();
        }

        return -1; // ningún cliente/proveedor seleccionado
    }

    private void añadirFactura() {
        if (empresa == null) { mostrarError("No hay empresa activa."); return; }
        if (!validarCampoObligatorio(txtFacturaEntidadID,"NIF de entidad")) return;
        if (!validarCampoObligatorio(txtFacturaNumero,"Número de factura")) return;
        if (!validarCampoObligatorio(txtFacturaFecha,"Fecha")) return;
        if (!validarCampoObligatorio(txtFacturaConcepto,"Concepto")) return;
        if (!validarNumero(txtFacturaBase,"Base imponible")) return;
        if (!validarNumero(txtFacturaIvaTotal,"IVA Total")) return;
        if (!validarNumero(txtFacturaTotal,"Total Factura")) return;

        Factura f = new Factura();
        f.setEmpresaId(empresa.getId());
        f.setEntidadId(Long.parseLong(txtFacturaEntidadID.getText().trim()));
        f.setTipo(mapTipoFactura(comboFacturaTipo.getValue()));
        f.setNumero(txtFacturaNumero.getText().trim());

        try {
            f.setFechaEmision(Date.valueOf(txtFacturaFecha.getText().trim()));
        } catch (IllegalArgumentException e) {
            mostrarError("Formato de fecha inválido. Usa yyyy-MM-dd.");
            return;
        }

        f.setConcepto(txtFacturaConcepto.getText().trim());
        f.setBaseImponible(parseDoubleSafe(txtFacturaBase,0.0));
        f.setIvaTotal(parseDoubleSafe(txtFacturaIvaTotal,0.0));
        f.setTotalFactura(parseDoubleSafe(txtFacturaTotal,0.0));
        f.setEstado(comboFacturaEstado.getValue());
        f.setObservaciones(safe(txtFacturaObservaciones));

        facturaController.añadir(f, java.util.Collections.emptyList());
        refrescarFacturas();
        limpiarFactura();
        mostrarInfo("Factura añadida correctamente.");
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

    // Refrescar todas las pestañas
    private void refrescarTodo() {
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
        } catch (Exception e) {
            return def;
        }
    }

    private double parseDoubleSafe(TextField tf, double def) {
        try {
            return Double.parseDouble(tf.getText().trim());
        } catch (Exception e) {
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
        if (empresa == null || entidadId == null) {
            return "";
        }
        List<Direccion> todas = direccionController.consultarTodosPorEmpresa(empresa.getId());
        for (Direccion d : todas) {
            if (entidadId.equals(d.getEntidadId())) {
                if ("direccion".equals(campo)) {
                    return safeStr(d.getDireccion());
                }
                if ("ciudad".equals(campo)) {
                    return safeStr(d.getCiudad());
                }
                if ("cp".equals(campo)) {
                    return safeStr(d.getCp());
                }
                return "";
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
