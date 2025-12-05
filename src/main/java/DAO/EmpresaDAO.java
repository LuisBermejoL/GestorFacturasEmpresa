package DAO;

import Modelo.ConexionBD;
import Modelo.Empresa;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) para la entidad Empresa. Contiene métodos CRUD
 * (Crear, Leer, Actualizar, Borrar).
 *
 * En la base de datos corresponde a la tabla 'empresa'.
 *
 * @author luisb
 */
public class EmpresaDAO {

    // === CREAR ===
    /**
     * Inserta una nueva empresa en la base de datos.
     *
     * @param e Objeto Empresa con los datos a insertar
     */
    public void añadir(Empresa e) {
        String sql = "INSERT INTO empresa (nombre, nif, direccion, cp, ciudad, provincia, pais, telefono, email, web, domicilio_fiscal, contacto) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.get(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, e.getNombre());
            stmt.setString(2, e.getNif());
            stmt.setString(3, e.getDireccion());
            stmt.setString(4, e.getCp());
            stmt.setString(5, e.getCiudad());
            stmt.setString(6, e.getProvincia());
            stmt.setString(7, e.getPais());
            stmt.setString(8, e.getTelefono());
            stmt.setString(9, e.getEmail());
            stmt.setString(10, e.getWeb());
            stmt.setString(11, e.getDomicilioFiscal());
            stmt.setString(12, e.getContacto());

            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // === ACTUALIZAR ===
    /**
     * Modifica los datos de una empresa existente. Se busca por NIF, que actúa
     * como identificador único.
     *
     * @param e Objeto Empresa con los datos actualizados
     */
    public void modificar(Empresa e) {
        String sql = "UPDATE empresa SET nombre=?, direccion=?, cp=?, ciudad=?, provincia=?, pais=?, telefono=?, email=?, web=?, domicilio_fiscal=?, contacto=? WHERE nif=?";
        try (Connection conn = ConexionBD.get(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, e.getNombre());
            stmt.setString(2, e.getDireccion());
            stmt.setString(3, e.getCp());
            stmt.setString(4, e.getCiudad());
            stmt.setString(5, e.getProvincia());
            stmt.setString(6, e.getPais());
            stmt.setString(7, e.getTelefono());
            stmt.setString(8, e.getEmail());
            stmt.setString(9, e.getWeb());
            stmt.setString(10, e.getDomicilioFiscal());
            stmt.setString(11, e.getContacto());
            stmt.setString(12, e.getNif());

            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // === BORRAR ===
    /**
     * Elimina una empresa por su NIF.
     *
     * @param nif Identificador fiscal único de la empresa
     */
    public void borrarPorNIF(String nif) {
        String sql = "DELETE FROM empresa WHERE nif=?";
        try (Connection conn = ConexionBD.get(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nif);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // === LEER UNO ===
    /**
     * Consulta una empresa por su NIF.
     *
     * @param nif Identificador fiscal único de la empresa
     * @return Empresa encontrada o null si no existe
     */
    public Empresa consultarPorNIF(String nif) {
        String sql = "SELECT * FROM empresa WHERE nif=?";
        try (Connection conn = ConexionBD.get(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nif);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Empresa e = new Empresa();
                e.setId(rs.getLong("id"));
                e.setNombre(rs.getString("nombre"));
                e.setNif(rs.getString("nif"));
                e.setDireccion(rs.getString("direccion"));
                e.setCp(rs.getString("cp"));
                e.setCiudad(rs.getString("ciudad"));
                e.setProvincia(rs.getString("provincia"));
                e.setPais(rs.getString("pais"));
                e.setTelefono(rs.getString("telefono"));
                e.setEmail(rs.getString("email"));
                e.setWeb(rs.getString("web"));
                e.setDomicilioFiscal(rs.getString("domicilio_fiscal"));
                e.setContacto(rs.getString("contacto"));
                return e;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    // === LEER TODOS ===
    /**
     * Consulta todas las empresas registradas en la base de datos.
     *
     * @return Lista de objetos Empresa
     */
    public List<Empresa> consultarTodas() {
        List<Empresa> lista = new ArrayList<>();
        String sql = "SELECT * FROM empresa";
        try (Connection conn = ConexionBD.get(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Empresa e = new Empresa();
                e.setId(rs.getLong("id"));
                e.setNombre(rs.getString("nombre"));
                e.setNif(rs.getString("nif"));
                e.setDireccion(rs.getString("direccion"));
                e.setCp(rs.getString("cp"));
                e.setCiudad(rs.getString("ciudad"));
                e.setProvincia(rs.getString("provincia"));
                e.setPais(rs.getString("pais"));
                e.setTelefono(rs.getString("telefono"));
                e.setEmail(rs.getString("email"));
                e.setWeb(rs.getString("web"));
                e.setDomicilioFiscal(rs.getString("domicilio_fiscal"));
                e.setContacto(rs.getString("contacto"));
                lista.add(e);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }
}
