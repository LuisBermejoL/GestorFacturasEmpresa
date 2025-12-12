package DAO;

import Modelo.Proveedor;
import Modelo.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) para la entidad Proveedor.
 * Contiene métodos CRUD (Crear, Leer, Actualizar, Borrar).
 *
 * En la base de datos, cada proveedor está vinculado a una entidad mediante id_entidad.
 * Todas las consultas están filtradas por empresa_id para respetar el modelo multiempresa.
 *
 * @author luisb
 */
public class ProveedorDAO {

    // === CREAR ===
    /**
     * Inserta un nuevo proveedor en la base de datos.
     * Primero se inserta en la tabla 'entidad' y luego en 'proveedores'.
     *
     * @param p          Objeto Proveedor con los datos a insertar
     * @param empresaId  ID de la empresa a la que pertenece el proveedor
     * @return 
     */
    public long añadir(Proveedor p, long empresaId) {
        String sqlEntidad = "INSERT INTO entidad (empresa_id, nombre, nif, email, telefono) VALUES (?, ?, ?, ?, ?)";
        String sqlProveedor = "INSERT INTO proveedores (id_entidad, codigo) VALUES (?, ?)";
        long idEntidad = -1;

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
                    idEntidad = rs.getLong(1);

                    try (PreparedStatement stmtProv = conn.prepareStatement(sqlProveedor)) {
                        stmtProv.setLong(1, idEntidad);
                        stmtProv.setInt(2, p.getCodigo());
                        stmtProv.executeUpdate();
                    }
                }
                rs.close();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return idEntidad;
    }

    // === ACTUALIZAR ===
    /**
     * Modifica los datos de un proveedor existente.
     * Actualiza tanto la tabla 'entidad' como 'proveedores'.
     *
     * @param p Objeto Proveedor con los datos actualizados
     */
    public void modificar(Proveedor p) {
        String sqlEntidad = "UPDATE entidad SET nombre=?, nif=?, email=?, telefono=? WHERE id=?";
        String sqlProveedor = "UPDATE proveedores SET codigo=? WHERE id_entidad=?";

        try (Connection conn = ConexionBD.get()) {
            conn.setAutoCommit(false);

            // Actualizar datos comunes en entidad
            try (PreparedStatement stmtEntidad = conn.prepareStatement(sqlEntidad)) {
                stmtEntidad.setString(1, p.getNombre());
                stmtEntidad.setString(2, p.getNif());
                stmtEntidad.setString(3, p.getEmail());
                stmtEntidad.setString(4, p.getTelefono());
                stmtEntidad.setLong(5, p.getId());
                stmtEntidad.executeUpdate();
            }

            // Actualizar datos específicos en proveedores
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

    // === BORRAR ===
    /**
     * Elimina un proveedor por su ID de entidad.
     *
     * @param idEntidad Identificador único de la entidad asociada al proveedor
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
     * Consulta un proveedor por su código dentro de una empresa.
     *
     * @param empresaId ID de la empresa
     * @param busquedaNombre Nombre del cliente
     * @param busquedaNif NIF del cliente
     * @return Proveedor encontrado o null si no existe
     */
    public List<Proveedor> consultarProveedores(long empresaId, String busquedaNombre, String busquedaNif) {
        List<Proveedor> lista = new ArrayList<>();
        String sql = "SELECT e.id, e.nombre, e.nif, e.email, e.telefono, p.codigo " +
                     "FROM entidad e JOIN proveedores p ON e.id = p.id_entidad " +
                     "WHERE e.empresa_id=? AND e.nombre LIKE ? AND e.nif LIKE ?";
        try (Connection conn = ConexionBD.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, empresaId);
            stmt.setString(2, "%" + busquedaNombre + "%");
            stmt.setString(3, "%" + busquedaNif + "%");
            
            ResultSet rs = stmt.executeQuery();
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

    // === LEER TODOS ===
    /**
     * Consulta todos los proveedores de una empresa.
     *
     * @param empresaId ID de la empresa
     * @return Lista de objetos Proveedor
     */
    public List<Proveedor> consultarTodos(long empresaId) {
        List<Proveedor> lista = new ArrayList<>();
        String sql = "SELECT e.id, e.nombre, e.nif, e.email, e.telefono, p.codigo " +
                     "FROM entidad e JOIN proveedores p ON e.id = p.id_entidad " +
                     "WHERE e.empresa_id=?";
        try (Connection conn = ConexionBD.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, empresaId);
            ResultSet rs = stmt.executeQuery();
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