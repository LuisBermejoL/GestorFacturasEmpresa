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

    // === CREAR ===
    /**
     * Añadir un nuevo cliente vinculado a una empresa.
     *
     * @param c         Objeto Cliente con los datos a insertar
     * @param empresaId ID de la empresa a la que pertenece el cliente
     * @return idEntidad generado en la tabla entidad
     */
    public long añadir(Cliente c, long empresaId) {
        return clienteDAO.añadir(c, empresaId);
    }

    // === ACTUALIZAR ===
    /**
     * Modificar los datos de un cliente existente.
     *
     * @param c Objeto Cliente con los datos actualizados
     */
    public void modificar(Cliente c) {
        clienteDAO.modificar(c);
    }

    // === BORRAR ===
    /**
     * Borrar un cliente por su ID de entidad.
     *
     * @param idEntidad Identificador único de la entidad asociada al cliente
     */
    public void borrarPorId(long idEntidad) {
        clienteDAO.borrarPorId(idEntidad);
    }

    // === LEER UNO ===
    /**
     * Consultar un cliente por su código dentro de una empresa.
     *
     * @param empresaId ID de la empresa
     * @param codigo    Código único del cliente
     * @return Cliente encontrado o null si no existe
     */
    public Cliente consultarPorCodigo(long empresaId, int codigo) {
        return clienteDAO.consultarPorCodigo(empresaId, codigo);
    }

    // === LEER TODOS ===
    /**
     * Consultar todos los clientes registrados en una empresa.
     *
     * @param empresaId ID de la empresa
     * @return Lista de objetos Cliente
     */
    public List<Cliente> consultarTodos(long empresaId) {
        return clienteDAO.consultarTodos(empresaId);
    }
}