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

    /**
     * Añadir un nuevo proveedor vinculado a una empresa.
     * 
     * @param p         Objeto Proveedor con los datos a insertar
     * @param empresaId ID de la empresa a la que pertenece el proveedor
     */
    public void añadir(Proveedor p, long empresaId) {
        proveedorDAO.añadir(p, empresaId);
    }

    /**
     * Modificar los datos de un proveedor existente.
     * 
     * @param p Objeto Proveedor con los datos actualizados
     */
    public void modificar(Proveedor p) {
        proveedorDAO.modificar(p);
    }

    /**
     * Borrar un proveedor por su ID de entidad.
     * 
     * @param idEntidad Identificador único de la entidad asociada al proveedor
     */
    public void borrarPorId(long idEntidad) {
        proveedorDAO.borrarPorId(idEntidad);
    }

    /**
     * Consultar un proveedor por su código.
     * 
     * @param codigo Código único del proveedor
     * @return Proveedor encontrado o null si no existe
     */
    public Proveedor consultarPorCodigo(int codigo) {
        return proveedorDAO.consultarPorCodigo(codigo);
    }

    /**
     * Consultar todos los proveedores registrados.
     * 
     * @return Lista de objetos Proveedor
     */
    public List<Proveedor> consultarTodos() {
        return proveedorDAO.consultarTodos();
    }
}