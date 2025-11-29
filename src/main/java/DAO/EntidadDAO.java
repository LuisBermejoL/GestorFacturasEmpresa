package DAO;

import Modelo.ConexionBD;
import Modelo.Entidad;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla entidad.
 * Gestiona datos comunes de clientes, proveedores, etc.
 * 
 * @author luisb
 */
public class EntidadDAO {

    /**
     * Consulta todas las entidades de una empresa.
     *
     * @param empresaId ID de la empresa
     * @return Lista de entidades
     */
    public List<Entidad> consultarTodos(long empresaId) {
        List<Entidad> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, nif, email, telefono " +
                     "FROM entidad WHERE empresa_id=?";
        try (Connection conn = ConexionBD.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, empresaId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Entidad e = new Entidad();
                e.setId(rs.getLong("id"));
                e.setNombre(rs.getString("nombre"));
                e.setNif(rs.getString("nif"));
                e.setEmail(rs.getString("email"));
                e.setTelefono(rs.getString("telefono"));
                lista.add(e);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // sube el error si quieres
        }
        return lista;
    }
}