package Controlador;

import DAO.ClienteDAO;
import Modelo.Cliente;
import java.util.List;

/**
 * Controlador para la entidad Cliente.
 * Gestiona clientes como especialización de 'entidad',
 * delegando en ClienteDAO las operaciones CRUD.
 * 
 * @author luisb
 */
public class ClienteController {
    private final ClienteDAO clienteDAO = new ClienteDAO();

    /**
     * Añadir un nuevo cliente vinculado a una empresa.
     * 
     * @param c         Objeto Cliente con los datos a insertar
     * @param empresaId ID de la empresa a la que pertenece el cliente
     */
    public void añadir(Cliente c, long empresaId) {
        clienteDAO.añadir(c, empresaId);
    }

    /**
     * Modificar los datos de un cliente existente.
     * 
     * @param c Objeto Cliente con los datos actualizados
     */
    public void modificar(Cliente c) {
        clienteDAO.modificar(c);
    }

    /**
     * Borrar un cliente por su ID de entidad.
     * 
     * @param idEntidad Identificador único de la entidad asociada al cliente
     */
    public void borrarPorId(long idEntidad) {
        clienteDAO.borrarPorId(idEntidad);
    }

    /**
     * Consultar un cliente por su código.
     * 
     * @param codigo Código único del cliente
     * @return Cliente encontrado o null si no existe
     */
    public Cliente consultarPorCodigo(int codigo) {
        return clienteDAO.consultarPorCodigo(codigo);
    }

    /**
     * Consultar todos los clientes registrados.
     * 
     * @return Lista de objetos Cliente
     */
    public List<Cliente> consultarTodos() {
        return clienteDAO.consultarTodos();
    }
}