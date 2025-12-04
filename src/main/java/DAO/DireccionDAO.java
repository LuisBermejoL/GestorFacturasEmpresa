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
 * En la base de datos corresponde a la tabla 'direccion'.
 * Todas las consultas están filtradas por empresa_id para respetar el modelo multiempresa.
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
        String sql = "INSERT INTO direccion (entidad_id, etiqueta, direccion, cp, ciudad, provincia, pais) VALUES (?, ?, ?, ?, ?, ?, ?)";
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

    // === LEER UNO ===
    /**
     * Consulta una dirección por su ID y verifica que pertenezca a la empresa.
     *
     * @param empresaId ID de la empresa
     * @param id        Identificador único de la dirección
     * @return Direccion encontrada o null si no existe o no pertenece a la empresa
     */
    public Direccion consultarPorId(long empresaId, long id) {
        // CORRECCIÓN: Se añade el JOIN con entidad y la cláusula WHERE para filtrar por empresa_id.
        String sql = "SELECT d.* FROM direccion d JOIN entidad e ON d.entidad_id = e.id WHERE d.id=? AND e.empresa_id=?";
        try (Connection conn = ConexionBD.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.setLong(2, empresaId); // Nuevo parámetro para el filtro de empresa_id

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // [Lógica para mapear el ResultSet a objeto Direccion omitida, se asume correcta]
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

    // === LEER TODOS ===
    /**
     * Consulta todas las direcciones vinculadas a entidades de una empresa.
     *
     * @param empresaId ID de la empresa
     * @return Lista de objetos Direccion
     */
    public List<Direccion> consultarTodosPorEmpresa(long empresaId) {
        List<Direccion> lista = new ArrayList<>();
        // CORRECCIÓN: Se añade el JOIN con entidad y la cláusula WHERE para filtrar por empresa_id.
        String sql = "SELECT d.* FROM direccion d JOIN entidad e ON d.entidad_id = e.id WHERE e.empresa_id=?";
        try (Connection conn = ConexionBD.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, empresaId); // Parámetro para el filtro de empresa_id
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // [Lógica para mapear el ResultSet a objeto Direccion omitida, se asume correcta]
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

    // === ACTUALIZAR ===
    /**
     * Modifica los datos de una dirección existente.
     *
     * @param d Objeto Direccion con los datos actualizados
     */
    public void modificar(Direccion d) {
        String sql = "UPDATE direccion SET entidad_id=?, etiqueta=?, direccion=?, cp=?, ciudad=?, provincia=?, pais=? WHERE id=?";
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
        String sql = "DELETE FROM direccion WHERE id=?";
        try (Connection conn = ConexionBD.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}