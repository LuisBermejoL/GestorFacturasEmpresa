package DAO;

import Modelo.ConexionBD;
import Modelo.Cliente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) para la entidad Cliente.
 * Un cliente se almacena en dos tablas:
 *   - entidad: datos comunes (nombre, NIF, email, teléfono, empresa_id)
 *   - clientes: datos específicos de cliente (id_entidad, código)
 *
 * Todas las consultas están filtradas por empresa_id para respetar el modelo multiempresa.
 *
 * @author luisb
 */
public class ClienteDAO {

    // === CREAR ===
    /**
     * Inserta un nuevo cliente en la base de datos.
     * Primero crea la entidad asociada y luego el registro en clientes.
     *
     * @param c         Objeto Cliente con los datos
     * @param empresaId ID de la empresa a la que pertenece el cliente
     */
    public void añadir(Cliente c, long empresaId) {
        // SQL para insertar en entidad
        String sqlEntidad = "INSERT INTO entidad (empresa_id, nombre, nif, email, telefono) VALUES (?, ?, ?, ?, ?)";
        // SQL para insertar en clientes
        String sqlCliente = "INSERT INTO clientes (id_entidad, codigo) VALUES (?, ?)";

        try (Connection conn = ConexionBD.get()) {
            conn.setAutoCommit(false); // Iniciamos transacción manual

            try (PreparedStatement stmtEntidad = conn.prepareStatement(sqlEntidad, Statement.RETURN_GENERATED_KEYS)) {
                // Insertar datos comunes en entidad
                stmtEntidad.setLong(1, empresaId);
                stmtEntidad.setString(2, c.getNombre());
                stmtEntidad.setString(3, c.getNif());
                stmtEntidad.setString(4, c.getEmail());
                stmtEntidad.setString(5, c.getTelefono());
                stmtEntidad.executeUpdate();

                // Obtener el ID generado para la entidad
                ResultSet rs = stmtEntidad.getGeneratedKeys();
                if (rs.next()) {
                    long idEntidad = rs.getLong(1);

                    // Insertar datos específicos en clientes
                    try (PreparedStatement stmtCliente = conn.prepareStatement(sqlCliente)) {
                        stmtCliente.setLong(1, idEntidad);
                        stmtCliente.setInt(2, c.getCodigo());
                        stmtCliente.executeUpdate();
                    }
                }

                conn.commit(); // Confirmar transacción
            } catch (SQLException e) {
                conn.rollback(); // Revertir si algo falla
                throw e;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // === ACTUALIZAR ===
    /**
     * Modifica los datos de un cliente existente.
     * Actualiza tanto la tabla entidad como la tabla clientes.
     *
     * @param c Objeto Cliente con los datos actualizados
     */
    public void modificar(Cliente c) {
        // SQL para actualizar entidad
        String sqlEntidad = "UPDATE entidad SET nombre=?, nif=?, email=?, telefono=? WHERE id=?";
        // SQL para actualizar clientes
        String sqlCliente = "UPDATE clientes SET codigo=? WHERE id_entidad=?";

        try (Connection conn = ConexionBD.get()) {
            conn.setAutoCommit(false);

            // Actualizar datos comunes en entidad
            try (PreparedStatement stmtEntidad = conn.prepareStatement(sqlEntidad)) {
                stmtEntidad.setString(1, c.getNombre());
                stmtEntidad.setString(2, c.getNif());
                stmtEntidad.setString(3, c.getEmail());
                stmtEntidad.setString(4, c.getTelefono());
                stmtEntidad.setLong(5, c.getId());
                stmtEntidad.executeUpdate();
            }

            // Actualizar datos específicos en clientes
            try (PreparedStatement stmtCliente = conn.prepareStatement(sqlCliente)) {
                stmtCliente.setInt(1, c.getCodigo());
                stmtCliente.setLong(2, c.getId());
                stmtCliente.executeUpdate();
            }

            conn.commit(); // Confirmar cambios
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // === BORRAR ===
    /**
     * Elimina un cliente por su ID de entidad.
     *
     * @param idEntidad Identificador único de la entidad asociada al cliente
     */
    public void borrarPorId(long idEntidad) {
        String sql = "DELETE FROM entidad WHERE id=?";
        try (Connection conn = ConexionBD.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idEntidad);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // === LEER UNO ===
    /**
     * Consulta un cliente por su código dentro de una empresa.
     *
     * @param empresaId ID de la empresa
     * @param codigo    Código único del cliente
     * @return Cliente encontrado o null si no existe
     */
    public Cliente consultarPorCodigo(long empresaId, int codigo) {
        String sql = "SELECT e.id, e.nombre, e.nif, e.email, e.telefono, c.codigo " +
                     "FROM entidad e JOIN clientes c ON e.id = c.id_entidad " +
                     "WHERE e.empresa_id=? AND c.codigo=?";
        try (Connection conn = ConexionBD.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, empresaId);
            stmt.setInt(2, codigo);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Cliente c = new Cliente();
                c.setId(rs.getLong("id"));
                c.setNombre(rs.getString("nombre"));
                c.setNif(rs.getString("nif"));
                c.setEmail(rs.getString("email"));
                c.setTelefono(rs.getString("telefono"));
                c.setCodigo(rs.getInt("codigo"));
                return c;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // === LEER TODOS ===
    /**
     * Consulta todos los clientes registrados en una empresa.
     *
     * @param empresaId ID de la empresa
     * @return Lista de objetos Cliente
     */
    public List<Cliente> consultarTodos(long empresaId) {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT e.id, e.nombre, e.nif, e.email, e.telefono, c.codigo " +
                     "FROM entidad e JOIN clientes c ON e.id = c.id_entidad " +
                     "WHERE e.empresa_id=?";
        try (Connection conn = ConexionBD.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, empresaId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Cliente c = new Cliente();
                c.setId(rs.getLong("id"));
                c.setNombre(rs.getString("nombre"));
                c.setNif(rs.getString("nif"));
                c.setEmail(rs.getString("email"));
                c.setTelefono(rs.getString("telefono"));
                c.setCodigo(rs.getInt("codigo"));
                lista.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}