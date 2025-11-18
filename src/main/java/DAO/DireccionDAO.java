package DAO;

import Modelo.ConexionBD;
import Modelo.Direccion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author luisb
 */
public class DireccionDAO {
    public void insertar(Direccion d) {
        String sql = "INSERT INTO direccion (entidad_id, etiqueta, direccion, cp, ciudad, provincia, pais) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.get(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, d.getEntidadId());
            stmt.setString(2, d.getEtiqueta());
            stmt.setString(3, d.getDireccion());
            stmt.setString(4, d.getCp());
            stmt.setString(5, d.getCiudad());
            stmt.setString(6, d.getProvincia());
            stmt.setString(7, d.getPais());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}