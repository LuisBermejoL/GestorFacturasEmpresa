package DAO;

import Modelo.ConexionBD;
import Modelo.Cliente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author luisb
 */
public class ClienteDAO {

    public void añadir(Cliente c, long empresaId) {
        String sqlEntidad = "INSERT INTO entidad (empresa_id, nombre, nif, email, telefono) VALUES (?, ?, ?, ?, ?)";
        String sqlCliente = "INSERT INTO clientes (id_entidad, codigo) VALUES (?, ?)";

        try (Connection conn = ConexionBD.get()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmtEntidad = conn.prepareStatement(sqlEntidad, Statement.RETURN_GENERATED_KEYS)) {
                stmtEntidad.setLong(1, empresaId);
                stmtEntidad.setString(2, c.getNombre());
                stmtEntidad.setString(3, c.getNif());
                stmtEntidad.setString(4, c.getEmail());
                stmtEntidad.setString(5, c.getTelefono());
                stmtEntidad.executeUpdate();

                ResultSet rs = stmtEntidad.getGeneratedKeys();
                if (rs.next()) {
                    long idEntidad = rs.getLong(1);
                    try (PreparedStatement stmtCliente = conn.prepareStatement(sqlCliente)) {
                        stmtCliente.setLong(1, idEntidad);
                        stmtCliente.setInt(2, c.getCodigo());
                        stmtCliente.executeUpdate();
                    }
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void modificar(Cliente c) {
        String sqlEntidad = "UPDATE entidad SET nombre=?, nif=?, email=?, telefono=? WHERE id=?";
        String sqlCliente = "UPDATE clientes SET codigo=? WHERE id_entidad=?";

        try (Connection conn = ConexionBD.get()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmtEntidad = conn.prepareStatement(sqlEntidad)) {
                stmtEntidad.setString(1, c.getNombre());
                stmtEntidad.setString(2, c.getNif());
                stmtEntidad.setString(3, c.getEmail());
                stmtEntidad.setString(4, c.getTelefono());
                stmtEntidad.setLong(5, c.getId());
                stmtEntidad.executeUpdate();
            }

            try (PreparedStatement stmtCliente = conn.prepareStatement(sqlCliente)) {
                stmtCliente.setInt(1, c.getCodigo());
                stmtCliente.setLong(2, c.getId());
                stmtCliente.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void borrarPorId(long idEntidad) {
        String sql = "DELETE FROM entidad WHERE id=?";
        try (Connection conn = ConexionBD.get(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idEntidad);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Cliente consultarPorCodigo(int codigo) {
        String sql = "SELECT e.id, e.nombre, e.nif, e.email, e.telefono, c.codigo FROM entidad e JOIN clientes c ON e.id = c.id_entidad WHERE c.codigo=?";
        try (Connection conn = ConexionBD.get(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, codigo);
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

    public List<Cliente> consultarTodos() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT e.id, e.nombre, e.nif, e.email, e.telefono, c.codigo FROM entidad e JOIN clientes c ON e.id = c.id_entidad";
        try (Connection conn = ConexionBD.get(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
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