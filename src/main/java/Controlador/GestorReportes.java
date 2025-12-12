package Controlador;

import Modelo.ConexionBD;
import net.sf.jasperreports.engine.*;
import javafx.scene.control.Alert;
import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author luisb
 */
public class GestorReportes {

    /**
     *
     * @param facturaId
     * @param empresaId
     * @param rutaDestino
     */
    public void generarFacturaPdf(long facturaId, long empresaId, String rutaDestino) {
        Connection conn = null;
        try {
            conn = ConexionBD.get();

            // 1. Cargar los archivos FUENTE .jrxml
            InputStream mainStream = getClass().getResourceAsStream("/reports/Factura.jrxml");
            InputStream subStream = getClass().getResourceAsStream("/reports/LineaFactura.jrxml");

            if (mainStream == null || subStream == null) {
                mostrarAlerta("Error Crítico", "No se encuentran los archivos .jrxml en /resources/reports/");
                return;
            }

            // 2. Compilar los reportes en tiempo de ejecución (Soluciona incompatibilidades de versión)
            JasperReport mainReport = JasperCompileManager.compileReport(mainStream);
            JasperReport subReport = JasperCompileManager.compileReport(subStream);

            // 3. Configurar Parámetros
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("P_FACTURA_ID", facturaId);
            parameters.put("P_EMPRESA_ID", empresaId);
            
            // 4. PASAR EL SUBREPORTE COMO OBJETO (Esencial para que Factura.jrxml lo encuentre)
            parameters.put("SUBREPORT_OBJECT", subReport);

            // 5. Llenar y Exportar
            JasperPrint jasperPrint = JasperFillManager.fillReport(mainReport, parameters, conn);
            JasperExportManager.exportReportToPdfFile(jasperPrint, rutaDestino);

            mostrarInfo("Factura Generada", "El PDF se ha guardado en:\n" + rutaDestino);

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error al generar PDF", "Detalle: " + e.getMessage());
        } finally {
            try { if (conn != null && !conn.isClosed()) conn.close(); } catch (Exception e) {}
        }
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
    
    private void mostrarInfo(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}