package Modelo;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Clase que gestiona la conexión a la base de datos MySQL.
 * 
 * Utiliza un archivo externo "db.properties" ubicado en el classpath
 * para cargar la configuración (URL, usuario y contraseña).
 * 
 * @author luisb
 */
public class ConexionBD {

    // Objeto Properties para almacenar la configuración de conexión
    private static final Properties PROPIEDADES = new Properties();

    // Variables de configuración cargadas desde db.properties
    private static String URL;
    private static String USER;
    private static String PASSWORD;

    // Bloque estático que se ejecuta al cargar la clase
    static {
        try (InputStream input = ConexionBD.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new RuntimeException("Archivo db.properties no encontrado en el classpath.");
            }

            // Cargar las propiedades desde el archivo
            PROPIEDADES.load(input);

            // Asignar valores a las variables
            URL = PROPIEDADES.getProperty("db.url");
            USER = PROPIEDADES.getProperty("db.user");
            PASSWORD = PROPIEDADES.getProperty("db.password");

        } catch (Exception e) {
            // Si ocurre un error al cargar la configuración, se lanza una excepción crítica
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Método estático que devuelve una conexión activa a la base de datos.
     * 
     * @return Objeto Connection listo para usar
     * @throws SQLException Si ocurre un error al establecer la conexión
     */
    public static Connection get() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}