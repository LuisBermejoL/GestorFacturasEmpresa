package Modelo;

import java.sql.Date;

/**
 *
 * @author luisb
 */
public class Factura {
    private long id;
    private char tipo; // Venta o Compra
    private String numero;
    private Date fechaEmision;
    private long entidadId;
    private String concepto;
    private double baseImponible;
    private double ivaTotal;
    private double totalFactura;
    private String estado;
    private String observaciones;

    public Factura() {
    }

    public Factura(long id, char tipo, String numero, Date fechaEmision, long entidadId, String concepto, double baseImponible, double ivaTotal, double totalFactura, String estado, String observaciones) {
        this.id = id;
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public char getTipo() {
        return tipo;
    }

    public void setTipo(char tipo) {
        this.tipo = tipo;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Date getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public long getEntidadId() {
        return entidadId;
    }

    public void setEntidadId(long entidadId) {
        this.entidadId = entidadId;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public double getBaseImponible() {
        return baseImponible;
    }

    public void setBaseImponible(double baseImponible) {
        this.baseImponible = baseImponible;
    }

    public double getIvaTotal() {
        return ivaTotal;
    }

    public void setIvaTotal(double ivaTotal) {
        this.ivaTotal = ivaTotal;
    }

    public double getTotalFactura() {
        return totalFactura;
    }

    public void setTotalFactura(double totalFactura) {
        this.totalFactura = totalFactura;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}