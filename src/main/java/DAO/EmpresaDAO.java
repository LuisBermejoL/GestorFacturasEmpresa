package DAO;

import Modelo.ConexionBD;
import Modelo.Empresa;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author luisb
 */
public class EmpresaDAO {
    public void insertar(Empresa e) {
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
}