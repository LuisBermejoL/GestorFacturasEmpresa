package Modelo;

/**
 *
 * @author luisb
 */
public class Direccion {
    private long id;
    private long entidadId;
    private String etiqueta; // 'fiscal', 'envio', 'otro'
    private String direccion;
    private String cp;
    private String ciudad;
    private String provincia;
    private String pais;

    // Constructor vacío
    public Direccion() {}

    // Constructor completo
    public Direccion(long id, long entidadId, String etiqueta, String direccion, String cp, String ciudad, String provincia, String pais) {
        this.id = id;
        this.entidadId = entidadId;
        this.etiqueta = etiqueta;
        this.direccion = direccion;
        this.cp = cp;
        this.ciudad = ciudad;
        this.provincia = provincia;
        this.pais = pais;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getEntidadId() {
        return entidadId;
    }

    public void setEntidadId(long entidadId) {
        this.entidadId = entidadId;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }
}