package Modelo;

/**
 * Clase que representa un producto o artículo en el sistema.
 * Está vinculado a una empresa y puede tener un proveedor habitual.
 * Incluye información comercial, fiscal y de stock.
 *
 * En la base de datos corresponde a la tabla 'producto'.
 *
 * @author luisb
 */
public class Producto {

    // Identificador único del producto (clave primaria en la BD)
    private long id;

    // ID de la empresa a la que pertenece el producto (clave foránea en la BD)
    private long empresaId;

    // Código del producto (máximo 13 caracteres, único por empresa)
    private String codigo;

    // Descripción general del producto
    private String descripcion;

    // Código de referencia que el proveedor habitual asigna al producto.
    // Sirve para identificar el producto en el catálogo del proveedor.
    // Ejemplo: tu producto interno "P-001" puede ser "ABC-123" en el proveedor.
    private String referenciaProveedor;

    // ID del proveedor habitual (puede ser null si no tiene proveedor asignado)
    private long proveedorId;

    // Precio de venta del producto
    private double precioVenta;

    // Stock actual del producto
    private double stock;

    /**
     * Constructor vacío necesario para frameworks, serialización y formularios JavaFX.
     */
    public Producto() {
    }

    /**
     * Constructor completo para inicializar todos los campos del producto.
     *
     * @param id                  ID único del producto
     * @param empresaId           ID de la empresa propietaria
     * @param codigo              Código del producto
     * @param descripcion         Descripción general
     * @param referenciaProveedor Código de referencia del proveedor
     * @param proveedorId         ID del proveedor habitual
     * @param precioVenta         Precio de venta
     * @param stock               Stock actual
     */
    public Producto(long id, long empresaId, String codigo, String descripcion, String referenciaProveedor, long proveedorId, double precioVenta, double stock) {
        this.id = id;
        this.empresaId = empresaId;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.referenciaProveedor = referenciaProveedor;
        this.proveedorId = proveedorId;
        this.precioVenta = precioVenta;
        this.stock = stock;
    }

    // === Getters y setters para todos los atributos ===

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(long empresaId) {
        this.empresaId = empresaId;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getReferenciaProveedor() {
        return referenciaProveedor;
    }

    public void setReferenciaProveedor(String referenciaProveedor) {
        this.referenciaProveedor = referenciaProveedor;
    }

    public Long getProveedorId() {
        return proveedorId;
    }

    public void setProveedorId(long proveedorId) {
        this.proveedorId = proveedorId;
    }

    public double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public double getStock() {
        return stock;
    }

    public void setStock(double stock) {
        this.stock = stock;
    }
}