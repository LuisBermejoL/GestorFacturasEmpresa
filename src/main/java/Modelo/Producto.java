package Modelo;

/**
 *
 * @author luisb
 */
public class Producto {
    private long id;
    private String codigo;
    private String descripcion;
    private String referenciaProveedor;
    private Long proveedorId;
    private int tipoIVAId;
    private double precioCoste;
    private double precioVenta;
    private double stock;

    public Producto() {
    }

    public Producto(long id, String codigo, String descripcion, String referenciaProveedor, Long proveedorId, int tipoIVAId, double precioCoste, double precioVenta, double stock) {
        this.id = id;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.referenciaProveedor = referenciaProveedor;
        this.proveedorId = proveedorId;
        this.tipoIVAId = tipoIVAId;
        this.precioCoste = precioCoste;
        this.precioVenta = precioVenta;
        this.stock = stock;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public void setProveedorId(Long proveedorId) {
        this.proveedorId = proveedorId;
    }

    public int getTipoIVAId() {
        return tipoIVAId;
    }

    public void setTipoIVAId(int tipoIVAId) {
        this.tipoIVAId = tipoIVAId;
    }

    public double getPrecioCoste() {
        return precioCoste;
    }

    public void setPrecioCoste(double precioCoste) {
        this.precioCoste = precioCoste;
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