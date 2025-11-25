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

    /**
     * Añadir una nueva dirección.
     *
     * @param direccion Objeto Direccion con los datos a insertar
     */
    public void añadir(Direccion direccion) {
        // Delegamos en el DAO la inserción de la dirección
        direccionDAO.añadir(direccion);
    }

    /**
     * Modificar una dirección existente.
     *
     * @param direccion Objeto Direccion con los datos actualizados
     */
    public void modificar(Direccion direccion) {
        // Delegamos en el DAO la actualización
        direccionDAO.modificar(direccion);
    }

    /**
     * Borrar una dirección por su ID.
     *
     * @param id Identificador único de la dirección
     */
    public void borrarPorId(long id) {
        // Delegamos en el DAO el borrado por ID
        direccionDAO.borrarPorId(id);
    }

    /**
     * Consultar una dirección por su ID.
     *
     * @param id Identificador único de la dirección
     * @return Direccion encontrada o null si no existe
     */
    public Direccion consultarPorId(long id) {
        // Delegamos en el DAO la consulta por ID
        return direccionDAO.consultarPorId(id);
    }

    /**
     * Consultar todas las direcciones registradas.
     *
     * @return Lista de objetos Direccion
     */
    public List<Direccion> consultarTodos() {
        // Delegamos en el DAO la consulta de todas las direcciones
        return direccionDAO.consultarTodos();
    }
}