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

    // === CREAR ===
    /**
     * Añadir una factura junto con sus líneas.
     *
     * @param f      Objeto Factura con los datos de la cabecera
     * @param lineas Lista de objetos LineaFactura con los detalles
     */
    public void añadir(Factura f, List<LineaFactura> lineas) {
        facturaDAO.añadir(f, lineas);
    }

    // === ACTUALIZAR ===
    /**
     * Modificar los datos de una factura existente.
     *
     * @param f Objeto Factura con los datos actualizados
     */
    public void modificar(Factura f) {
        facturaDAO.modificar(f);
    }

    // === BORRAR ===
    /**
     * Borrar una factura por su ID dentro de una empresa.
     *
     * @param empresaId ID de la empresa
     * @param id        Identificador único de la factura
     */
    public void borrarPorId(long empresaId, long id) {
        facturaDAO.borrarPorId(empresaId, id);
    }

    // === LEER UNO ===
    /**
     * Consultar una factura por su ID dentro de una empresa.
     *
     * @param empresaId ID de la empresa
     * @param numeroFactura Número de la factura
     * @return Factura encontrada o null si no existe
     */
    public List<Factura> consultarFacturas(long empresaId, String numeroFactura) {
        return facturaDAO.consultarFacturas(empresaId, numeroFactura);
    }

    // === LEER TODOS ===
    /**
     * Consultar todas las facturas registradas en una empresa.
     *
     * @param empresaId ID de la empresa
     * @return Lista de objetos Factura
     */
    public List<Factura> consultarTodas(long empresaId) {
        return facturaDAO.consultarTodos(empresaId);
    }
}