package DAO;

import Modelo.ConexionBD;
import Modelo.Direccion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author luisb
 */
public class DireccionDAO {

    // === CREAR ===
    public void añadir(Direccion d) {
        String sql = "INSERT INTO direcciones (entidad_id, etiqueta, direccion, cp, ciudad, provincia, pais) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

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

    // === LEER TODOS ===
    public List<Direccion> consultarTodos() {
        List<Direccion> lista = new ArrayList<>();
        String sql = "SELECT * FROM direcciones";

        try (Connection conn = ConexionBD.get();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Direccion d = new Direccion();
                d.setId(rs.getLong("id"));
                d.setEntidadId(rs.getLong("entidad_id"));
                d.setEtiqueta(rs.getString("etiqueta"));
                d.setDireccion(rs.getString("direccion"));
                d.setCp(rs.getString("cp"));
                d.setCiudad(rs.getString("ciudad"));
                d.setProvincia(rs.getString("provincia"));
                d.setPais(rs.getString("pais"));
                lista.add(d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // === LEER UNO ===
    public Direccion consultarPorId(long id) {
        String sql = "SELECT * FROM direcciones WHERE id=?";
        try (Connection conn = ConexionBD.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Direccion d = new Direccion();
                d.setId(rs.getLong("id"));
                d.setEntidadId(rs.getLong("entidad_id"));
                d.setEtiqueta(rs.getString("etiqueta"));
                d.setDireccion(rs.getString("direccion"));
                d.setCp(rs.getString("cp"));
                d.setCiudad(rs.getString("ciudad"));
                d.setProvincia(rs.getString("provincia"));
                d.setPais(rs.getString("pais"));
                return d;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // === ACTUALIZAR ===
    public void modificar(Direccion d) {
        String sql = "UPDATE direcciones SET entidad_id=?, etiqueta=?, direccion=?, cp=?, ciudad=?, provincia=?, pais=? WHERE id=?";
        try (Connection conn = ConexionBD.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, d.getEntidadId());
            stmt.setString(2, d.getEtiqueta());
            stmt.setString(3, d.getDireccion());
            stmt.setString(4, d.getCp());
            stmt.setString(5, d.getCiudad());
            stmt.setString(6, d.getProvincia());
            stmt.setString(7, d.getPais());
            stmt.setLong(8, d.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // === BORRAR ===
    public void borrarPorId(long id) {
        String sql = "DELETE FROM direcciones WHERE id=?";
        try (Connection conn = ConexionBD.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}