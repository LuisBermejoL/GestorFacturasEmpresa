package Controlador;

import DAO.DireccionDAO;
import Modelo.Direccion;
import java.util.List;

/**
 * Controlador para la entidad Direccion.
 * Gestiona direcciones vinculadas a entidades (empresa, cliente, proveedor)
 * y delega en DireccionDAO las operaciones CRUD.
 *
 * @author luisb
 */
public class DireccionController {
    private final DireccionDAO direccionDAO = new DireccionDAO();

    // === CREAR ===
    /**
     * Añadir una nueva dirección.
     *
     * @param direccion Objeto Direccion con los datos a insertar
     */
    public void añadir(Direccion direccion) {
        // Delegamos en el DAO la inserción de la dirección
        direccionDAO.añadir(direccion);
    }

    // === ACTUALIZAR ===
    /**
     * Modificar una dirección existente.
     *
     * @param direccion Objeto Direccion con los datos actualizados
     */
    public void modificar(Direccion direccion) {
        // Delegamos en el DAO la actualización
        direccionDAO.modificar(direccion);
    }

    // === BORRAR ===
    /**
     * Borrar una dirección por su ID.
     *
     * @param id Identificador único de la dirección
     */
    public void borrarPorId(long id) {
        // Delegamos en el DAO el borrado por ID
        direccionDAO.borrarPorId(id);
    }

    // === LEER UNO ===
    /**
     * Consultar una dirección por su ID dentro de una empresa.
     *
     * @param empresaId ID de la empresa
     * @param id        Identificador único de la dirección
     * @return Direccion encontrada o null si no existe
     */
    public Direccion consultarPorId(long empresaId, long id) {
        // Delegamos en el DAO la consulta por empresa e ID
        return direccionDAO.consultarPorId(empresaId, id);
    }

    // === LEER TODOS ===
    /**
     * Consultar todas las direcciones registradas en una empresa.
     *
     * @param empresaId ID de la empresa
     * @return Lista de objetos Direccion
     */
    public List<Direccion> consultarTodos(long empresaId) {
        // Delegamos en el DAO la consulta de todas las direcciones de la empresa
        return direccionDAO.consultarTodos(empresaId);
    }
}