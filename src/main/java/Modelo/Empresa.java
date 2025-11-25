package Modelo;

/**
 * Clase que representa una empresa en el sistema.
 * Contiene información fiscal, de contacto y localización.
 * 
 * En la base de datos corresponde a la tabla 'empresa'.
 * 
 * @author luisb
 */
public class Empresa {

    // Identificador único de la empresa (clave primaria en la BD)
    private long id;

    // Nombre fiscal o razón social de la empresa
    private String nombre;

    // Número de identificación fiscal (NIF) único
    private String nif;

    // Dirección principal de la empresa
    private String direccion;

    // Código postal
    private String cp;

    // Ciudad donde se ubica la empresa
    private String ciudad;

    // Provincia de la empresa
    private String provincia;

    // País (por defecto 'ES' en la BD)
    private String pais;

    // Teléfono de contacto
    private String telefono;

    // Correo electrónico de contacto
    private String email;

    // Página web de la empresa
    private String web;

    // Dirección fiscal (puede diferir de la dirección principal)
    private String domicilioFiscal;

    // Persona de contacto principal
    private String contacto;

    /**
     * Constructor vacío necesario para frameworks, serialización y formularios JavaFX.
     */
    public Empresa() {
    }

    /**
     * Constructor completo para inicializar todos los campos de la empresa.
     * 
     * @param id              Identificador único de la empresa
     * @param nombre          Nombre fiscal o razón social
     * @param nif             Número de identificación fiscal
     * @param direccion       Dirección principal
     * @param cp              Código postal
     * @param ciudad          Ciudad
     * @param provincia       Provincia
     * @param pais            País
     * @param telefono        Teléfono de contacto
     * @param email           Correo electrónico
     * @param web             Página web
     * @param domicilioFiscal Dirección fiscal
     * @param contacto        Persona de contacto
     */
    public Empresa(long id, String nombre, String nif, String direccion, String cp,
                   String ciudad, String provincia, String pais, String telefono,
                   String email, String web, String domicilioFiscal, String contacto) {
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

    // Getters y setters para todos los atributos

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getNif() { return nif; }
    public void setNif(String nif) { this.nif = nif; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getCp() { return cp; }
    public void setCp(String cp) { this.cp = cp; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getProvincia() { return provincia; }
    public void setProvincia(String provincia) { this.provincia = provincia; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getWeb() { return web; }
    public void setWeb(String web) { this.web = web; }

    public String getDomicilioFiscal() { return domicilioFiscal; }
    public void setDomicilioFiscal(String domicilioFiscal) { this.domicilioFiscal = domicilioFiscal; }

    public String getContacto() { return contacto; }
    public void setContacto(String contacto) { this.contacto = contacto; }
}