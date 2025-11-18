package Modelo;

/**
 *
 * @author luisb
 */
public class Entidad {
    private long id;
    private String nombre;
    private String nif;
    private String email;
    private String telefono;

    public Entidad() {
    }

    public Entidad(long id, String nombre, String nif, String email, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.nif = nif;
        this.email = email;
        this.telefono = telefono;
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