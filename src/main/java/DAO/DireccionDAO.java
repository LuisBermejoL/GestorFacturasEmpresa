package DAO;

import Modelo.ConexionBD;
import Modelo.Direccion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) para la entidad Direccion.
 * Contiene métodos CRUD (Crear, Leer, Actualizar, Borrar).
 * 
 * En la base de datos corresponde a la tabla 'direcciones'.
 * 
 * @author luisb
 */
public class DireccionDAO {

    // === CREAR ===
    /**
     * Inserta una nueva dirección en la base de datos.
     * 
     * @param d Objeto Direccion con los datos a insertar
     */
    public void añadir(Direccion d) {
        String sql = "INSERT INTO direcciones (entidad_id, etiqueta, direccion, cp, ciudad, provincia, pais) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, d.getEntidadId());   // Relación con la entidad (cliente, proveedor, empresa)
            stmt.setString(2, d.getEtiqueta());  // Etiqueta descriptiva (ej. "Oficina", "Almacén")
            stmt.setString(3, d.getDireccion()); // Calle y número
            stmt.setString(4, d.getCp());        // Código postal
            stmt.setString(5, d.getCiudad());    // Ciudad
            stmt.setString(6, d.getProvincia()); // Provincia
            stmt.setString(7, d.getPais());      // País

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // === LEER TODOS ===
    /**
     * Consulta todas las direcciones registradas en la base de datos.
     * 
     * @return Lista de objetos Direccion
     */
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
    /**
     * Consulta una dirección por su ID.
     * 
     * @param id Identificador único de la dirección
     * @return Direccion encontrada o null si no existe
     */
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
    /**
     * Modifica los datos de una dirección existente.
     * 
     * @param d Objeto Direccion con los datos actualizados
     */
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
    /**
     * Elimina una dirección por su ID.
     * 
     * @param id Identificador único de la dirección
     */
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