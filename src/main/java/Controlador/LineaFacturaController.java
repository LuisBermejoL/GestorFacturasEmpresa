package Controlador;

import DAO.LineaFacturaDAO;
import Modelo.LineaFactura;
import java.util.List;

/**
 * Controlador para la entidad LineaFactura.
 * Gestiona las líneas de factura y delega en LineaFacturaDAO
 * las operaciones CRUD y consultas por factura.
 *
 * @author luisb
 */
public class LineaFacturaController {
    private final LineaFacturaDAO lineaFacturaDAO = new LineaFacturaDAO();

    // === CREAR ===
    /**
     * Añadir una nueva línea de factura.
     *
     * @param lineaFactura Objeto LineaFactura con los datos a insertar
     */
    public void añadir(LineaFactura lineaFactura) {
        // Delegamos en el DAO la inserción de la línea
        lineaFacturaDAO.añadir(lineaFactura);
    }

    // === ACTUALIZAR ===
    /**
     * Modificar una línea de factura existente.
     *
     * @param lineaFactura Objeto LineaFactura con los datos actualizados
     */
    public void modificar(LineaFactura lineaFactura) {
        // Delegamos en el DAO la actualización
        lineaFacturaDAO.modificar(lineaFactura);
    }

    // === BORRAR ===
    /**
     * Borrar una línea de factura por su ID.
     *
     * @param id Identificador único de la línea de factura
     */
    public void borrarPorId(long id) {
        // Delegamos en el DAO el borrado por ID
        lineaFacturaDAO.borrarPorId(id);
    }

    // === LEER UNO ===
    /**
     * Consultar una línea de factura por su ID dentro de una empresa.
     *
     * @param empresaId ID de la empresa
     * @param id        Identificador único de la línea de factura
     * @return LineaFactura encontrada o null si no existe
     */
    public LineaFactura consultarPorId(long empresaId, long id) {
        // Delegamos en el DAO la consulta por empresa e ID
        return lineaFacturaDAO.consultarPorId(empresaId, id);
    }

    // === LEER TODOS ===
    /**
     * Consultar todas las líneas de una factura dentro de una empresa.
     *
     * @param empresaId ID de la empresa
     * @param facturaId ID de la factura
     * @return Lista de objetos LineaFactura
     */
    public List<LineaFactura> consultarTodos(long empresaId, long facturaId) {
        // Delegamos en el DAO la consulta de todas las líneas de una factura
        return lineaFacturaDAO.consultarTodos(empresaId, facturaId);
    }

    // === UTILIDADES ===
    /**
     * Consultar todas las líneas asociadas a una factura concreta.
     *
     * @param facturaId Identificador único de la factura
     * @return Lista de objetos LineaFactura asociados a la factura
     */
    public List<LineaFactura> consultarPorFacturaId(long facturaId) {
        // Delegamos en el DAO la consulta por facturaId
        return lineaFacturaDAO.consultarPorFacturaId(facturaId);
    }

    /**
     * Borrar todas las líneas asociadas a una factura concreta.
     *
     * @param facturaId Identificador único de la factura
     */
    public void borrarPorFacturaId(long facturaId) {
        // Delegamos en el DAO el borrado de líneas por facturaId
        lineaFacturaDAO.borrarPorFacturaId(facturaId);
    }
}