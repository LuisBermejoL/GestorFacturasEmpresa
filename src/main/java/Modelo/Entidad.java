package Modelo;

/**
 * Clase que representa una entidad en el sistema.
 * Una entidad puede ser tanto un cliente como un proveedor,
 * ya que comparten información común como nombre, NIF, email y teléfono.
 *
 * En la base de datos corresponde a la tabla 'entidad'.
 * Cada entidad está vinculada a una empresa mediante empresa_id.
 *
 * @author luisb
 */
public class Entidad {

    // Identificador único de la entidad (clave primaria en la BD)
    private long id;

    // ID de la empresa a la que pertenece la entidad (clave foránea en la BD)
    private long empresaId;

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
     * @param empresaId ID de la empresa propietaria
     * @param nombre    Nombre de la entidad
     * @param nif       Número de identificación fiscal
     * @param email     Correo electrónico
     * @param telefono  Teléfono de contacto
     */
    public Entidad(long id, long empresaId, String nombre, String nif, String email, String telefono) {
        this.id = id;
        this.empresaId = empresaId;
        this.nombre = nombre;
        this.nif = nif;
        this.email = email;
        this.telefono = telefono;
    }

    // === Getters y setters para todos los atributos ===

    /**
     *
     * @return
     */

    public long getId() {
        return id;
    }

    /**
     *
     * @param id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     *
     * @return
     */
    public long getEmpresaId() {
        return empresaId;
    }

    /**
     *
     * @param empresaId
     */
    public void setEmpresaId(long empresaId) {
        this.empresaId = empresaId;
    }

    /**
     *
     * @return
     */
    public String getNombre() {
        return nombre;
    }

    /**
     *
     * @param nombre
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     *
     * @return
     */
    public String getNif() {
        return nif;
    }

    /**
     *
     * @param nif
     */
    public void setNif(String nif) {
        this.nif = nif;
    }

    /**
     *
     * @return
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     *
     * @return
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     *
     * @param telefono
     */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}