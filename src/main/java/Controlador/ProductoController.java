package Controlador;

import DAO.ProductoDAO;
import Modelo.Producto;
import java.util.List;

/**
 * Controlador para la entidad Producto.
 * Encapsula la lógica de negocio y delega en ProductoDAO
 * las operaciones CRUD sobre la tabla 'producto'.
 *
 * @author luisb
 */
public class ProductoController {
    private final ProductoDAO productoDAO = new ProductoDAO();

    /**
     * Añadir un nuevo producto vinculado a una empresa.
     *
     * @param producto  Objeto Producto con los datos a insertar
     * @param empresaId ID de la empresa a la que pertenece el producto
     */
    public void añadir(Producto producto, long empresaId) {
        // Delegamos en el DAO la inserción del producto con su empresa
        productoDAO.añadir(producto, empresaId);
    }

    /**
     * Modificar los datos de un producto existente.
     *
     * @param producto Objeto Producto con los datos actualizados
     */
    public void modificar(Producto producto) {
        // Delegamos en el DAO la actualización por código del producto
        productoDAO.modificar(producto);
    }

    /**
     * Borrar un producto por su código.
     *
     * @param codigo Código único del producto
     */
    public void borrarPorCodigo(String codigo) {
        // Delegamos en el DAO el borrado por código
        productoDAO.borrarPorCodigo(codigo);
    }

    /**
     * Consultar un producto por su código.
     *
     * @param codigo Código único del producto
     * @return Producto encontrado o null si no existe
     */
    public Producto consultarPorCodigo(String codigo) {
        // Delegamos en el DAO la consulta por código
        return productoDAO.consultarPorCodigo(codigo);
    }

    /**
     * Consultar todos los productos registrados.
     *
     * @return Lista de objetos Producto
     */
    public List<Producto> consultarTodos() {
        // Delegamos en el DAO la consulta de todos los productos
        return productoDAO.consultarTodos();
    }
}