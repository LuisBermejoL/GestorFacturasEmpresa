package DAO;

import Modelo.Proveedor;
import Modelo.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author luisb
 */
public class ProveedorDAO {

    public void añadir(Proveedor p, long empresaId) {
        String sqlEntidad = "INSERT INTO entidad (empresa_id, nombre, nif, email, telefono) VALUES (?, ?, ?, ?, ?)";
        String sqlProveedor = "INSERT INTO proveedores (id_entidad, codigo) VALUES (?, ?)";

        try (Connection conn = ConexionBD.get()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmtEntidad = conn.prepareStatement(sqlEntidad, Statement.RETURN_GENERATED_KEYS)) {
                stmtEntidad.setLong(1, empresaId);
                stmtEntidad.setString(2, p.getNombre());
                stmtEntidad.setString(3, p.getNif());
                stmtEntidad.setString(4, p.getEmail());
                stmtEntidad.setString(5, p.getTelefono());
                stmtEntidad.executeUpdate();

                ResultSet rs = stmtEntidad.getGeneratedKeys();
                if (rs.next()) {
                    long idEntidad = rs.getLong(1);
                    try (PreparedStatement stmtProveedor = conn.prepareStatement(sqlProveedor)) {
                        stmtProveedor.setLong(1, idEntidad);
                        stmtProveedor.setInt(2, p.getCodigo());
                        stmtProveedor.executeUpdate();
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

    public void modificar(Proveedor p) {
        String sqlEntidad = "UPDATE entidad SET nombre=?, nif=?, email=?, telefono=? WHERE id=?";
        String sqlProveedor = "UPDATE proveedores SET codigo=? WHERE id_entidad=?";

        try (Connection conn = ConexionBD.get()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmtEntidad = conn.prepareStatement(sqlEntidad)) {
                stmtEntidad.setString(1, p.getNombre());
                stmtEntidad.setString(2, p.getNif());
                stmtEntidad.setString(3, p.getEmail());
                stmtEntidad.setString(4, p.getTelefono());
                stmtEntidad.setLong(5, p.getId());
                stmtEntidad.executeUpdate();
            }

            try (PreparedStatement stmtProveedor = conn.prepareStatement(sqlProveedor)) {
                stmtProveedor.setInt(1, p.getCodigo());
                stmtProveedor.setLong(2, p.getId());
                stmtProveedor.executeUpdate();
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

    public Proveedor consultarPorCodigo(int codigo) {
        String sql = "SELECT e.id, e.nombre, e.nif, e.email, e.telefono, p.codigo FROM entidad e JOIN proveedores p ON e.id = p.id_entidad WHERE p.codigo=?";
        try (Connection conn = ConexionBD.get(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, codigo);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Proveedor p = new Proveedor();
                p.setId(rs.getLong("id"));
                p.setNombre(rs.getString("nombre"));
                p.setNif(rs.getString("nif"));
                p.setEmail(rs.getString("email"));
                p.setTelefono(rs.getString("telefono"));
                p.setCodigo(rs.getInt("codigo"));
                return p;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Proveedor> consultarTodos() {
        List<Proveedor> lista = new ArrayList<>();
        String sql = "SELECT e.id, e.nombre, e.nif, e.email, e.telefono, p.codigo FROM entidad e JOIN proveedores p ON e.id = p.id_entidad";
        try (Connection conn = ConexionBD.get(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Proveedor p = new Proveedor();
                p.setId(rs.getLong("id"));
                p.setNombre(rs.getString("nombre"));
                p.setNif(rs.getString("nif"));
                p.setEmail(rs.getString("email"));
                p.setTelefono(rs.getString("telefono"));
                p.setCodigo(rs.getInt("codigo"));
                lista.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}