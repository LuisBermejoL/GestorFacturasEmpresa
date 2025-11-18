package DAO;

import Modelo.ConexionBD;
import Modelo.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author luisb
 */
public class UsuarioDAO {
    public void registrar(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nombre, email, password) VALUES (?, ?, ?)";

        try (Connection conn = ConexionBD.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getPassword());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean autenticar(String email, String password) {
        String sql = "SELECT * FROM usuarios WHERE email = ? AND password = ?";

        try (Connection conn = ConexionBD.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            return rs.next(); // true si existe el usuario
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}