package Modelo;

/**
 * Clase que representa una entidad en el sistema.
 * Una entidad puede ser tanto un cliente como un proveedor,
 * ya que comparten información común como nombre, NIF, email y teléfono.
 * 
 * En la base de datos corresponde a la tabla 'entidad'.
 * 
 * @author luisb
 */
public class Entidad {

    // Identificador único de la entidad (clave primaria en la BD)
    private long id;

    // Nombre de la entidad (razón social o nombre comercial)
    private String nombre;

    // Número de identificación fiscal (NIF) único
    private String nif;

    // Correo electrónico de contacto
    private String email;

    // Teléfono de contacto
    private String telefono;

    /**
     * Constructor vacío necesario para frameworks, serialización y formularios JavaFX.
     */
    public Entidad() {
    }

    /**
     * Constructor completo para inicializar todos los campos de la entidad.
     * 
     * @param id        Identificador único de la entidad
     * @param nombre    Nombre de la entidad
     * @param nif       Número de identificación fiscal
     * @param email     Correo electrónico
     * @param telefono  Teléfono de contacto
     */
    public Entidad(long id, String nombre, String nif, String email, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.nif = nif;
        this.email = email;
        this.telefono = telefono;
    }

    // Getters y setters para todos los atributos

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}