package Controlador;

import DAO.ProveedorDAO;
import Modelo.Proveedor;
import java.util.List;

/**
 * Controlador para la entidad Proveedor.
 * Gestiona proveedores como especialización de 'entidad',
 * delegando en ProveedorDAO las operaciones CRUD.
 *
 * @author luisb
 */
public class ProveedorController {
    private final ProveedorDAO proveedorDAO = new ProveedorDAO();

    // === CREAR ===
    /**
     * Añadir un nuevo proveedor vinculado a una empresa.
     *
     * @param p         Objeto Proveedor con los datos a insertar
     * @param empresaId ID de la empresa a la que pertenece el proveedor
     */
    public long añadir(Proveedor p, long empresaId) {
        return proveedorDAO.añadir(p, empresaId);
    }

    // === ACTUALIZAR ===
    /**
     * Modificar los datos de un proveedor existente.
     *
     * @param p Objeto Proveedor con los datos actualizados
     */
    public void modificar(Proveedor p) {
        proveedorDAO.modificar(p);
    }

    // === BORRAR ===
    /**
     * Borrar un proveedor por su ID de entidad.
     *
     * @param idEntidad Identificador único de la entidad asociada al proveedor
     */
    public void borrarPorId(long idEntidad) {
        proveedorDAO.borrarPorId(idEntidad);
    }

    // === LEER UNO ===
    /**
     * Consultar un proveedor por su código dentro de una empresa.
     *
     * @param empresaId ID de la empresa
     * @param codigo    Código único del proveedor
     * @return Proveedor encontrado o null si no existe
     */
    public Proveedor consultarPorCodigo(long empresaId, int codigo) {
        return proveedorDAO.consultarPorCodigo(empresaId, codigo);
    }

    // === LEER TODOS ===
    /**
     * Consultar todos los proveedores registrados en una empresa.
     *
     * @param empresaId ID de la empresa
     * @return Lista de objetos Proveedor
     */
    public List<Proveedor> consultarTodos(long empresaId) {
        return proveedorDAO.consultarTodos(empresaId);
    }
}