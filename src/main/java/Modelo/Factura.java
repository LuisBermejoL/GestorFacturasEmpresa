package Modelo;

import java.sql.Date;
import java.util.List;

/**
 * Clase que representa una factura en el sistema.
 * Puede ser de tipo Venta (V) o Compra (C).
 * Incluye datos generales, totales e información de estado.
 * 
 * En la base de datos corresponde a la tabla factura.
 * 
 * @author luisb
 */
public class Factura {

    // Identificador único de la factura (clave primaria en la BD)
    private long id;
    
    // Identificador único de la empresa
    private long empresaId;

    // Tipo de factura: 'V' = Venta, 'C' = Compra
    private char tipo;

    // Número de factura (único por empresa)
    private String numero;

    // Fecha de emisión de la factura
    private Date fechaEmision;

    // ID de la entidad (cliente o proveedor) asociada
    private long entidadId;

    // Concepto o descripción general de la factura
    private String concepto;

    // Base imponible (suma de las líneas sin impuestos)
    private double baseImponible;

    // Total de IVA aplicado
    private double ivaTotal;

    // Total final de la factura (base imponible + IVA)
    private double totalFactura;

    // Estado de la factura (ej. PENDIENTE, PAGADA, ANULADA)
    private String estado;

    // Observaciones adicionales
    private String observaciones;

    /**
     * Constructor vacío necesario para frameworks, serialización y formularios JavaFX.
     */
    public Factura() {
    }

    /**
     * Constructor completo para inicializar todos los campos de la factura.
     * 
     * @param id             Identificador único de la factura
     * @param empresaId      Identificador único de la empresa
     * @param tipo           Tipo de factura ('V' = Venta, 'C' = Compra)
     * @param numero         Número de factura (único por empresa)
     * @param fechaEmision   Fecha de emisión de la factura
     * @param entidadId      ID de la entidad (cliente o proveedor) asociada
     * @param concepto       Concepto o descripción general de la factura
     * @param baseImponible  Base imponible (suma de las líneas sin impuestos)
     * @param ivaTotal       Total de IVA aplicado
     * @param totalFactura   Total final de la factura (base imponible + IVA)
     * @param estado         Estado de la factura (ej. PENDIENTE, PAGADA, ANULADA)
     * @param observaciones  Observaciones adicionales
     */
    public Factura(long id, long empresaId, char tipo, String numero, Date fechaEmision, long entidadId,
                   String concepto, double baseImponible, double ivaTotal,
                   double totalFactura, String estado, String observaciones) {
        this.id = id;
        this.empresaId = empresaId;
        this.tipo = tipo;
        this.numero = numero;
        this.fechaEmision = fechaEmision;
        this.entidadId = entidadId;
        this.concepto = concepto;
        this.baseImponible = baseImponible;
        this.ivaTotal = ivaTotal;
        this.totalFactura = totalFactura;
        this.estado = estado;
        this.observaciones = observaciones;
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
    public long getEmpresaId() { return empresaId; }

    /**
     *
     * @param empresaId
     */
    public void setEmpresaId(long empresaId) { this.empresaId = empresaId; }
    
    /**
     *
     * @return
     */
    public char getTipo() { return tipo; }

    /**
     *
     * @param tipo
     */
    public void setTipo(char tipo) { this.tipo = tipo; }

    /**
     *
     * @return
     */
    public String getNumero() { return numero; }

    /**
     *
     * @param numero
     */
    public void setNumero(String numero) { this.numero = numero; }

    /**
     *
     * @return
     */
    public Date getFechaEmision() { return fechaEmision; }

    /**
     *
     * @param fechaEmision
     */
    public void setFechaEmision(Date fechaEmision) { this.fechaEmision = fechaEmision; }

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
    public String getConcepto() { return concepto; }

    /**
     *
     * @param concepto
     */
    public void setConcepto(String concepto) { this.concepto = concepto; }

    /**
     *
     * @return
     */
    public double getBaseImponible() { return baseImponible; }

    /**
     *
     * @param baseImponible
     */
    public void setBaseImponible(double baseImponible) { this.baseImponible = baseImponible; }

    /**
     *
     * @return
     */
    public double getIvaTotal() { return ivaTotal; }

    /**
     *
     * @param ivaTotal
     */
    public void setIvaTotal(double ivaTotal) { this.ivaTotal = ivaTotal; }

    /**
     *
     * @return
     */
    public double getTotalFactura() { return totalFactura; }

    /**
     *
     * @param totalFactura
     */
    public void setTotalFactura(double totalFactura) { this.totalFactura = totalFactura; }

    /**
     *
     * @return
     */
    public String getEstado() { return estado; }

    /**
     *
     * @param estado
     */
    public void setEstado(String estado) { this.estado = estado; }

    /**
     *
     * @return
     */
    public String getObservaciones() { return observaciones; }

    /**
     *
     * @param observaciones
     */
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    /**
     * Método auxiliar que calcula los totales de la factura
     * a partir de sus líneas (LineaFactura).
     * 
     * @param lineas Lista de líneas de factura asociadas
     * @param porcentajeIVA Porcentaje de IVA a aplicar (ej. 0.21 para 21%)
     */
    public void calcularTotales(List<LineaFactura> lineas, double porcentajeIVA) {
        double subtotal = 0;
        for (LineaFactura linea : lineas) {
            subtotal += linea.getTotalLinea();
        }
        this.baseImponible = subtotal;
        this.ivaTotal = subtotal * porcentajeIVA;
        this.totalFactura = baseImponible + ivaTotal;
    }
}