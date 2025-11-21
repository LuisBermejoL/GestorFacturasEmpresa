package Controlador;

import DAO.FacturaDAO;
import Modelo.Factura;
import java.util.List;

/**
 *
 * @author luisb
 */
public class FacturaController {
    private final FacturaDAO facturaDAO;

    public FacturaController() {
        this.facturaDAO = new FacturaDAO();
    }

    public void añadirFactura(Factura f) {
        facturaDAO.añadir(f);
    }

    public void modificarFactura(Factura f) {
        facturaDAO.modificar(f);
    }

    public void borrarFacturaPorId(long id) {
        facturaDAO.borrarPorId(id);
    }

    public Factura consultarFacturaPorId(long id) {
        return facturaDAO.consultarPorId(id);
    }

    public List<Factura> consultarTodasFacturas() {
        return facturaDAO.consultarTodos();
    }
}