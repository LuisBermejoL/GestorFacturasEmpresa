package Controlador;

import DAO.ClienteDAO;
import Modelo.Cliente;
import java.util.List;

/**
 *
 * @author luisb
 */
public class ClienteController {
    private final ClienteDAO clienteDAO;

    public ClienteController() {
        this.clienteDAO = new ClienteDAO();
    }

    public void añadirCliente(Cliente c, long empresaId) {
        clienteDAO.añadir(c, empresaId);
    }

    public void modificarCliente(Cliente c) {
        clienteDAO.modificar(c);
    }

    public void borrarClientePorId(long idEntidad) {
        clienteDAO.borrarPorId(idEntidad);
    }

    public Cliente consultarClientePorCodigo(int codigo) {
        return clienteDAO.consultarPorCodigo(codigo);
    }

    public List<Cliente> consultarTodosClientes() {
        return clienteDAO.consultarTodos();
    }
}