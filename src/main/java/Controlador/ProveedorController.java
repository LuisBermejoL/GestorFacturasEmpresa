package Controlador;

import DAO.ProveedorDAO;
import Modelo.Proveedor;
import java.util.List;

/**
 *
 * @author luisb
 */
public class ProveedorController {
    private final ProveedorDAO proveedorDAO;

    public ProveedorController() {
        this.proveedorDAO = new ProveedorDAO();
    }

    public void añadirProveedor(Proveedor p, long empresaId) {
        proveedorDAO.añadir(p, empresaId);
    }

    public void modificarProveedor(Proveedor p) {
        proveedorDAO.modificar(p);
    }

    public void borrarProveedorPorId(long idEntidad) {
        proveedorDAO.borrarPorId(idEntidad);
    }

    public Proveedor consultarProveedorPorCodigo(int codigo) {
        return proveedorDAO.consultarPorCodigo(codigo);
    }

    public List<Proveedor> consultarTodosProveedores() {
        return proveedorDAO.consultarTodos();
    }
}