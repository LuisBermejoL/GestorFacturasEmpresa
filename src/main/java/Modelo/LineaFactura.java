package Modelo;

/**
 * Clase que representa una línea dentro de una factura.
 * Cada línea está asociada a un producto y a una factura concreta,
 * incluyendo cantidad, precio unitario y posibles descuentos.
 * 
 * En la base de datos corresponde a la tabla factura_producto.
 * 
 * @author luisb
 */
public class LineaFactura {

    // Identificador único de la línea (clave primaria en la BD)
    private long id;

    // ID de la factura a la que pertenece esta línea (clave foránea)
    private long facturaId;

    // ID del producto asociado a esta línea (clave foránea)
    private long productoId;

    // Cantidad de unidades del producto en la línea
    private double cantidad;

    // Precio unitario del producto en esta factura
    private double precioUnitario;

    // Descuento aplicado a esta línea (en valor monetario, no porcentaje)
    private double descuento;

    /**
     * Constructor vacío necesario para frameworks, serialización y formularios JavaFX.
     */
    public LineaFactura() {
    }

    /**
     * Constructor completo para inicializar todos los campos de la línea de factura.
     * 
     * @param id             Identificador único de la línea
     * @param facturaId      ID de la factura asociada
     * @param productoId     ID del producto asociado
     * @param cantidad       Cantidad de producto
     * @param precioUnitario Precio unitario del producto
     * @param descuento      Descuento aplicado
     */
    public LineaFactura(long id, long facturaId, long productoId,
                        double cantidad, double precioUnitario, double descuento) {
        this.id = id;
        this.facturaId = facturaId;
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.descuento = descuento;
    }

    // Getters y setters para todos los atributos

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
    public long getFacturaId() {
        return facturaId;
    }

    /**
     *
     * @param facturaId
     */
    public void setFacturaId(long facturaId) {
        this.facturaId = facturaId;
    }

    /**
     *
     * @return
     */
    public long getProductoId() {
        return productoId;
    }

    /**
     *
     * @param productoId
     */
    public void setProductoId(long productoId) {
        this.productoId = productoId;
    }

    /**
     *
     * @return
     */
    public double getCantidad() {
        return cantidad;
    }

    /**
     *
     * @param cantidad
     */
    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    /**
     *
     * @return
     */
    public double getPrecioUnitario() {
        return precioUnitario;
    }

    /**
     *
     * @param precioUnitario
     */
    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    /**
     *
     * @return
     */
    public double getDescuento() {
        return descuento;
    }

    /**
     *
     * @param descuento
     */
    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    /**
     * Método auxiliar que calcula el total de la línea de factura.
     * Fórmula: (cantidad * precioUnitario) - descuento
     * 
     * @return Total de la línea después de aplicar el descuento
     */
    public double getTotalLinea() {
        return precioUnitario * (1 - descuento / 100.0) * cantidad;
    }
}