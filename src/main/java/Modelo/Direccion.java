package Modelo;

/**
 * Clase que representa una dirección asociada a una entidad (cliente o proveedor).
 * Cada entidad puede tener varias direcciones con diferentes etiquetas:
 *  - 'fiscal' → Dirección fiscal
 *  - 'envio'  → Dirección de envío
 *  - 'otro'   → Otra dirección
 * 
 * En la base de datos corresponde a la tabla 'direccion'.
 * 
 * @author luisb
 */
public class Direccion {

    // Identificador único de la dirección (clave primaria en la BD)
    private long id;

    // ID de la entidad a la que pertenece esta dirección (clave foránea)
    private long entidadId;

    // Etiqueta que clasifica la dirección ('fiscal', 'envio', 'otro')
    private String etiqueta;

    // Dirección completa (calle, número, etc.)
    private String direccion;

    // Código postal
    private String cp;

    // Ciudad
    private String ciudad;

    // Provincia
    private String provincia;

    // País (por defecto 'ES' en la BD)
    private String pais;

    /**
     * Constructor vacío necesario para frameworks, serialización y formularios JavaFX.
     */
    public Direccion() {}

    /**
     * Constructor completo para inicializar todos los campos de la dirección.
     * 
     * @param id         Identificador único de la dirección
     * @param entidadId  ID de la entidad asociada
     * @param etiqueta   Etiqueta de la dirección ('fiscal', 'envio', 'otro')
     * @param direccion  Dirección completa
     * @param cp         Código postal
     * @param ciudad     Ciudad
     * @param provincia  Provincia
     * @param pais       País
     */
    public Direccion(long id, long entidadId, String etiqueta, String direccion,
                     String cp, String ciudad, String provincia, String pais) {
        this.id = id;
        this.entidadId = entidadId;
        this.etiqueta = etiqueta;
        this.direccion = direccion;
        this.cp = cp;
        this.ciudad = ciudad;
        this.provincia = provincia;
        this.pais = pais;
    }

    // Getters y setters para todos los atributos

    /**
     *
     * @return
     */

    public long getId() { return id; }

    /**
     *
     * @param id
     */
    public void setId(long id) { this.id = id; }

    /**
     *
     * @return
     */
    public long getEntidadId() { return entidadId; }

    /**
     *
     * @param entidadId
     */
    public void setEntidadId(long entidadId) { this.entidadId = entidadId; }

    /**
     *
     * @return
     */
    public String getEtiqueta() { return etiqueta; }

    /**
     *
     * @param etiqueta
     */
    public void setEtiqueta(String etiqueta) { this.etiqueta = etiqueta; }

    /**
     *
     * @return
     */
    public String getDireccion() { return direccion; }

    /**
     *
     * @param direccion
     */
    public void setDireccion(String direccion) { this.direccion = direccion; }

    /**
     *
     * @return
     */
    public String getCp() { return cp; }

    /**
     *
     * @param cp
     */
    public void setCp(String cp) { this.cp = cp; }

    /**
     *
     * @return
     */
    public String getCiudad() { return ciudad; }

    /**
     *
     * @param ciudad
     */
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    /**
     *
     * @return
     */
    public String getProvincia() { return provincia; }

    /**
     *
     * @param provincia
     */
    public void setProvincia(String provincia) { this.provincia = provincia; }

    /**
     *
     * @return
     */
    public String getPais() { return pais; }

    /**
     *
     * @param pais
     */
    public void setPais(String pais) { this.pais = pais; }
}