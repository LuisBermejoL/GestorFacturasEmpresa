package Controlador;

import DAO.EmpresaDAO;
import Modelo.Empresa;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

/**
 * Controlador para la vista "Abrir Empresa". Muestra todas las empresas
 * registradas en una tabla y permite abrir, eliminar o actualizar una empresa
 * seleccionada.
 *
 * Al pulsar "Abrir", se carga la vista facturaClientes.fxml para gestionar
 * facturas/clientes de la empresa seleccionada.
 *
 * @author luisb
 */
public class AbrirEmpresaController {

    // === ELEMENTOS DE LA INTERFAZ ===
    @FXML
    private ImageView retroceder; // Imagen de flecha para volver atrás

    @FXML
    private TableView<Empresa> tablaEmpresas; // Tabla que muestra las empresas

    // Columnas de la tabla (se enlazan con atributos de Empresa)
    @FXML
    private TableColumn<Empresa, Long> colId;
    @FXML
    private TableColumn<Empresa, String> colNombre;
    @FXML
    private TableColumn<Empresa, String> colNif;
    @FXML
    private TableColumn<Empresa, String> colTelefono;
    @FXML
    private TableColumn<Empresa, String> colCorreo;
    @FXML
    private TableColumn<Empresa, String> colDireccion;
    @FXML
    private TableColumn<Empresa, String> colCiudad;
    @FXML
    private TableColumn<Empresa, String> colCp;

    @FXML
    private Button btnAbrir;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnModificar;

    // DAO para acceder a la base de datos
    private final EmpresaDAO empresaDAO = new EmpresaDAO();

    // Lista observable para la tabla
    private ObservableList<Empresa> listaEmpresas;

    // === MÉTODOS DE INICIALIZACIÓN ===
    /**
     * Se ejecuta automáticamente al cargar el FXML. Configura las columnas de
     * la tabla y carga los datos de las empresas.
     */
    @FXML
    private void initialize() {
        // Configurar columnas con los atributos de Empresa
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colNif.setCellValueFactory(new PropertyValueFactory<>("nif"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("email"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colCiudad.setCellValueFactory(new PropertyValueFactory<>("ciudad"));
        colCp.setCellValueFactory(new PropertyValueFactory<>("cp"));

        // Cargar empresas desde la BD
        cargarEmpresas();

        // Eventos de los botones
        retroceder.setOnMouseClicked(e -> volverAPrincipal());
        btnAbrir.setOnAction(e -> abrirEmpresaSeleccionada());
        btnEliminar.setOnAction(e -> eliminarEmpresaSeleccionada());
        btnModificar.setOnAction(e -> modificarEmpresaSeleccionada());
    }

    // === MÉTODOS DE TABLA ===
    /**
     * Carga todas las empresas desde la base de datos en la tabla.
     */
    private void cargarEmpresas() {
        List<Empresa> empresas = empresaDAO.consultarTodas();
        listaEmpresas = FXCollections.observableArrayList(empresas);
        tablaEmpresas.setItems(listaEmpresas);
    }

    /**
     * Abre la empresa seleccionada en una nueva ventana. Se carga la vista
     * facturaClientes.fxml y se pasa la empresa seleccionada.
     */
    private void abrirEmpresaSeleccionada() {
        Empresa seleccionada = tablaEmpresas.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            try {
                // Cargar el FXML correcto: facturaClientes.fxml
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/luis/gestorfacturasempresa/gestionEmpresa.fxml"));
                Parent root = loader.load();

                // Obtener el controlador de facturaClientes.fxml
                GestionEmpresaController detalleController = loader.getController();
                detalleController.setEmpresa(seleccionada); // Pasamos la empresa seleccionada

                // Crear nueva ventana para mostrar los datos
                Stage detalleStage = new Stage();
                detalleStage.setTitle("Factura Clientes - " + seleccionada.getNombre());
                detalleStage.setScene(new Scene(root, 1450, 700));
                detalleStage.show();

                // Cerrar la ventana Abrir Lista de Empresas
                Stage actualStage = (Stage) btnModificar.getScene().getWindow();
                actualStage.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Modifica la empresa seleccionada abriendo la vista de edición.
     */
    private void modificarEmpresaSeleccionada() {
        Empresa seleccionada = tablaEmpresas.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            try {
                // Cargar la vista de nueva empresa para editar
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/luis/gestorfacturasempresa/nuevaEmpresa.fxml"));
                Parent root = loader.load();

                // Obtener el controlador de la vista cargada
                NuevaEmpresaController editarController = loader.getController();

                // *** ESTA ES LA LÍNEA CLAVE: Pasar la empresa y cambiar a modo edición ***
                editarController.cargarEmpresaParaEditar(seleccionada);

                // Crear nueva ventana para editar
                Stage editarStage = new Stage();
                editarStage.setTitle("Modificar Empresa - " + seleccionada.getNombre());
                editarStage.setScene(new Scene(root, 450, 550));
                editarStage.show();

                // Cerrar la ventana Abrir Lista de Empresas
                Stage actualStage = (Stage) btnModificar.getScene().getWindow();
                actualStage.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Elimina la empresa seleccionada de la base de datos y refresca la tabla.
     */
    private void eliminarEmpresaSeleccionada() {
        Empresa seleccionada = tablaEmpresas.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            empresaDAO.borrarPorNIF(seleccionada.getNif());
            cargarEmpresas(); // refrescar tabla
        }
    }

    // === NAVEGACIÓN ===
    /**
     * Vuelve al menú principal (Inicio).
     */
    private void volverAPrincipal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/luis/gestorfacturasempresa/inicio.fxml"));
            Parent root = loader.load();

            Stage principalStage = new Stage();
            principalStage.setTitle("Gestor de Facturas - Empresa");
            principalStage.setScene(new Scene(root, 600, 400));
            principalStage.centerOnScreen();

            Stage actualStage = (Stage) retroceder.getScene().getWindow();
            actualStage.close();

            principalStage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
