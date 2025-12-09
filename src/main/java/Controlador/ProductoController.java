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

    // === CREAR ===
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

    // === ACTUALIZAR ===
    /**
     * Modificar los datos de un producto existente.
     *
     * @param producto Objeto Producto con los datos actualizados
     */
    public void modificar(Producto producto) {
        // Delegamos en el DAO la actualización por código y empresa
        productoDAO.modificar(producto);
    }

    // === BORRAR ===
    /**
     * Borrar un producto por su código dentro de una empresa.
     *
     * @param empresaId ID de la empresa
     * @param codigo    Código único del producto
     */
    public void borrarPorCodigo(long empresaId, String codigo) {
        // Delegamos en el DAO el borrado por código y empresa
        productoDAO.borrarPorCodigo(empresaId, codigo);
    }

    // === LEER UNO ===
    /**
     * Consultar un producto por su código dentro de una empresa.
     *
     * @param empresaId ID de la empresa
     * @param codigoProducto    Código único del producto
     * @return Producto encontrado o null si no existe
     */
    public List<Producto> consultarProductos(long empresaId, String codigoProducto) {
        // Delegamos en el DAO la consulta por código y empresa
        return productoDAO.consultarProductos(empresaId, codigoProducto);
    }

    // === LEER TODOS ===
    /**
     * Consultar todos los productos registrados en una empresa.
     *
     * @param empresaId ID de la empresa
     * @return Lista de objetos Producto
     */
    public List<Producto> consultarTodos(long empresaId) {
        // Delegamos en el DAO la consulta de todos los productos de la empresa
        return productoDAO.consultarTodos(empresaId);
    }
}