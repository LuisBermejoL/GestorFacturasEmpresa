package Controlador;

import DAO.FacturaDAO;
import Modelo.Factura;
import Modelo.LineaFactura;
import java.util.List;

/**
 * Controlador para la entidad Factura.
 * Gestiona facturas y sus líneas, delegando en FacturaDAO.
 * 
 * @author luisb
 */
public class FacturaController {
    private final FacturaDAO facturaDAO = new FacturaDAO();

    /**
     * Añadir una factura junto con sus líneas.
     * 
     * @param f      Objeto Factura con los datos de la cabecera
     * @param lineas Lista de objetos LineaFactura con los detalles
     */
    public void añadir(Factura f, List<LineaFactura> lineas) {
        facturaDAO.añadir(f, lineas);
    }

    /**
     * Modificar los datos de una factura existente.
     * 
     * @param f Objeto Factura con los datos actualizados
     */
    public void modificar(Factura f) {
        facturaDAO.modificar(f);
    }

    /**
     * Borrar una factura por su ID.
     * 
     * @param id Identificador único de la factura
     */
    public void borrarPorId(long id) {
        facturaDAO.borrarPorId(id);
    }

    /**
     * Consultar una factura por su ID.
     * 
     * @param id Identificador único de la factura
     * @return Factura encontrada o null si no existe
     */
    public Factura consultarPorId(long id) {
        return facturaDAO.consultarPorId(id);
    }

    /**
     * Consultar todas las facturas registradas.
     * 
     * @return Lista de objetos Factura
     */
    public List<Factura> consultarTodas() {
        return facturaDAO.consultarTodos();
    }
}