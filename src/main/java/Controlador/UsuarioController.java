package Controlador;

import DAO.UsuarioDAO;
import Modelo.Usuario;
import java.util.List;

/**
 *
 * @author luisb
 */
public class UsuarioController {
    private final UsuarioDAO usuarioDAO;

    public UsuarioController() {
        this.usuarioDAO = new UsuarioDAO();
    }

    public void añadirUsuario(Usuario u) {
        usuarioDAO.añadir(u);
    }

    public void modificarUsuario(Usuario u) {
        usuarioDAO.modificar(u);
    }

    public void borrarUsuarioPorId(int id) {
        usuarioDAO.borrarPorId(id);
    }

    public Usuario consultarUsuarioPorId(int id) {
        return usuarioDAO.consultarPorId(id);
    }

    public List<Usuario> consultarTodosUsuarios() {
        return usuarioDAO.consultarTodos();
    }

    public boolean autenticarUsuario(String email, String password) {
        return usuarioDAO.autenticar(email, password);
    }
}