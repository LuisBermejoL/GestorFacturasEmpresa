package Controlador;

import Modelo.Usuario;
import DAO.UsuarioDAO;

/**
 *
 * @author luisb
 */
public class UsuarioController {
    private UsuarioDAO dao = new UsuarioDAO();

    public boolean login(String email, String password) {
        return dao.autenticar(email, password);
    }

    public void registrar(Usuario u) {
        dao.registrar(u);
    }
}