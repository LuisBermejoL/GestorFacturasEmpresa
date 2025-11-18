package Controlador;

import Modelo.Producto;
import DAO.ProductoDAO;
import java.util.List;

/**
 *
 * @author luisb
 */
public class ProductoController {
    private ProductoDAO dao = new ProductoDAO();

    public void crear(Producto p) {
        dao.insertar(p);
    }

    public List<Producto> obtenerTodos() {
        return dao.listar();
    }
}