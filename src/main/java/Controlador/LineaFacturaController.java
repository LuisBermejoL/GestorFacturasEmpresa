package Controlador;

import DAO.LineaFacturaDAO;
import Modelo.LineaFactura;
import java.util.List;

/**
 *
 * @author luisb
 */
public class LineaFacturaController {
    private final LineaFacturaDAO lineaFacturaDAO;

    public LineaFacturaController() {
        this.lineaFacturaDAO = new LineaFacturaDAO();
    }

    public void añadirLineaFactura(LineaFactura lf) {
        lineaFacturaDAO.añadir(lf);
    }

    public void modificarLineaFactura(LineaFactura lf) {
        lineaFacturaDAO.modificar(lf);
    }

    public void borrarLineaFacturaPorId(long id) {
        lineaFacturaDAO.borrarPorId(id);
    }

    public LineaFactura consultarLineaFacturaPorId(long id) {
        return lineaFacturaDAO.consultarPorId(id);
    }

    public List<LineaFactura> consultarTodasLineasFactura() {
        return lineaFacturaDAO.consultarTodos();
    }

    public List<LineaFactura> consultarLineasPorFacturaId(long facturaId) {
        return lineaFacturaDAO.consultarPorFacturaId(facturaId);
    }

    public void borrarLineasPorFacturaId(long facturaId) {
        lineaFacturaDAO.borrarPorFacturaId(facturaId);
    }
}