package Controlador;

import DAO.DireccionDAO;
import Modelo.Direccion;
import java.util.List;

/**
 *
 * @author luisb
 */
public class DireccionController {
    private final DireccionDAO direccionDAO;

    public DireccionController() {
        this.direccionDAO = new DireccionDAO();
    }

    public void añadirDireccion(Direccion d) {
        direccionDAO.añadir(d);
    }

    public void modificarDireccion(Direccion d) {
        direccionDAO.modificar(d);
    }

    public void borrarDireccionPorId(long id) {
        direccionDAO.borrarPorId(id);
    }

    public Direccion consultarDireccionPorId(long id) {
        return direccionDAO.consultarPorId(id);
    }

    public List<Direccion> consultarTodasDirecciones() {
        return direccionDAO.consultarTodos();
    }
}