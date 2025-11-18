package Modelo;

/**
 *
 * @author luisb
 */
public class Empresa {
    private long id;
    private String nombre;
    private String nif;
    private String direccion;
    private String cp;
    private String ciudad;
    private String provincia;
    private String pais;
    private String telefono;
    private String email;
    private String web;
    private String domicilioFiscal;
    private String contacto;

    public Empresa() {
    }

    public Empresa(long id, String nombre, String nif, String direccion, String cp, String ciudad, String provincia, String pais, String telefono, String email, String web, String domicilioFiscal, String contacto) {
        this.id = id;
        this.nombre = nombre;
        this.nif = nif;
        this.direccion = direccion;
        this.cp = cp;
        this.ciudad = ciudad;
        this.provincia = provincia;
        this.pais = pais;
        this.telefono = telefono;
        this.email = email;
        this.web = web;
        this.domicilioFiscal = domicilioFiscal;
        this.contacto = contacto;
    }

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

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public String getDomicilioFiscal() {
        return domicilioFiscal;
    }

    public void setDomicilioFiscal(String domicilioFiscal) {
        this.domicilioFiscal = domicilioFiscal;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }
}