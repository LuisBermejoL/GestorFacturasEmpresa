package Modelo;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author luisb
 */
public class ConexionBD {
    private static final Properties PROPIEDADES = new Properties();
    private static String URL;
    private static String USER;
    private static String PASSWORD;

    static {
        try (InputStream input = ConexionBD.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) throw new RuntimeException("Archivo db.properties no encontrado.");

            PROPIEDADES.load(input);

            URL = PROPIEDADES.getProperty("db.url");
            USER = PROPIEDADES.getProperty("db.user");
            PASSWORD = PROPIEDADES.getProperty("db.password");

        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static Connection get() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}