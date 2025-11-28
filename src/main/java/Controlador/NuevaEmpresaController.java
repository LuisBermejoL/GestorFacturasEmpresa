package Controlador;

import DAO.EmpresaDAO;
import Modelo.Empresa;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class NuevaEmpresaController {

    @FXML
    private ImageView retroceder; // Imagen punta de flecha para volver al menú anterior

    // Campos de Contacto Empresa
    @FXML
    private TextField txtEmpresaNombre, txtEmpresaNif, txtEmpresaTelefono, txtEmpresaCorreo, txtEmpresaWeb, txtEmpresaDfiscal, txtEmpresaContacto;

    // Campos de Localización Empresa
    @FXML
    private TextField txtEmpresaDireccion, txtEmpresaPais, txtEmpresaCiudad, txtEmpresaProvincia, txtEmpresaCp;

    // botón "Aceptar"
    @FXML
    private Button btnAceptar;

    private EmpresaDAO empresaDAO = new EmpresaDAO();

    @FXML
    private void initialize() {
        // Añadimos el evento de clic a la flecha de retroceder
        retroceder.setOnMouseClicked(event -> volverAPrincipal());
        btnAceptar.setOnAction(e -> guardarEmpresa());
    }

    @FXML
    private void volverAPrincipal() {
        try {
            // 1. Cargamos la ventana principal
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/luis/gestorfacturasempresa/inicio.fxml"));
            Parent root = loader.load();

            // 2. Creamos una NUEVA ventana (Stage)
            Stage principalStage = new Stage();
            principalStage.setTitle("Gestor de Facturas - Empresa");
            principalStage.setScene(new Scene(root, 600, 400));
            principalStage.setMinWidth(600);
            principalStage.setMinHeight(400);
            principalStage.centerOnScreen();

            // 3. Cerramos la ventana actual (Nueva Empresa)
            Stage nuevaEmpresaStage = (Stage) retroceder.getScene().getWindow();
            nuevaEmpresaStage.close();

            // 4. Mostramos la nueva ventana con efecto
            principalStage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void guardarEmpresa() {
                Empresa empresa = new Empresa();
        empresa.setNombre(txtEmpresaNombre.getText().trim());
        empresa.setNif(txtEmpresaNif.getText().trim());
        empresa.setTelefono(txtEmpresaTelefono.getText().trim());
        empresa.setEmail(txtEmpresaCorreo.getText().trim());
        empresa.setWeb(txtEmpresaWeb.getText().trim());
        empresa.setDomicilioFiscal(txtEmpresaDfiscal.getText().trim());
        empresa.setDireccion(txtEmpresaDireccion.getText());
        empresa.setPais(txtEmpresaPais.getText().trim().trim().isEmpty() ? "España" : txtEmpresaPais.getText().trim());
        empresa.setCiudad(txtEmpresaCiudad.getText().trim());
        empresa.setProvincia(txtEmpresaProvincia.getText().trim());
        empresa.setCp(txtEmpresaCp.getText().trim());
        empresa.setContacto(txtEmpresaContacto.getText().trim());

        empresaDAO.añadir(empresa);

        // Volver al menú principal
        volverAPrincipal();
    }

}
