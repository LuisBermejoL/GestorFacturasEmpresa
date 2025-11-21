package Controlador;

import DAO.ProductoDAO;
import Modelo.Producto;
import java.util.List;

/**
 *
 * @author luisb
 */
public class ProductoController {
    private final ProductoDAO productoDAO;

    public ProductoController() {
        this.productoDAO = new ProductoDAO();
    }

    public void añadirProducto(Producto p) {
        productoDAO.añadir(p);
    }

    public void modificarProducto(Producto p) {
        productoDAO.modificar(p);
    }

    public void borrarProductoPorCodigo(String codigo) {
        productoDAO.borrarPorCodigo(codigo);
    }

    public Producto consultarProductoPorCodigo(String codigo) {
        return productoDAO.consultarPorCodigo(codigo);
    }

    public List<Producto> consultarTodosProductos() {
        return productoDAO.consultarTodos();
    }
}