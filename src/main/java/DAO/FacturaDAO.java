package DAO;

import Modelo.ConexionBD;
import Modelo.Factura;
import Modelo.LineaFactura;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) para la entidad Factura.
 * Contiene métodos CRUD (Leer, Actualizar, Borrar) y un único método de creación
 * que inserta la factura junto con sus líneas en una sola transacción.
 * 
 * En la base de datos corresponde a la tabla 'facturas'.
 * 
 * @author luisb
 */
public class FacturaDAO {

    // === CREAR (Factura + Líneas) ===
    /**
     * Inserta una factura junto con todas sus líneas en una sola transacción.
     * 
     * @param f       Objeto Factura con los datos de la cabecera
     * @param lineas  Lista de objetos LineaFactura con los detalles de la factura
     */
    public void añadir(Factura f, List<LineaFactura> lineas) {
        // SQL para insertar la cabecera de la factura
        String sqlFactura = "INSERT INTO facturas (tipo, numero, fecha_emision, entidad_id, concepto, base_imponible, iva_total, total_factura, estado, observaciones) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        // SQL para insertar las líneas de la factura
        String sqlLinea = "INSERT INTO lineas_factura (factura_id, producto_id, cantidad, precio_unitario, descuento) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.get()) {
            conn.setAutoCommit(false); // Iniciamos transacción manual

            try (PreparedStatement stmtFactura = conn.prepareStatement(sqlFactura, Statement.RETURN_GENERATED_KEYS)) {
                // Insertar cabecera de la factura
                stmtFactura.setString(1, String.valueOf(f.getTipo())); // char convertido a String
                stmtFactura.setString(2, f.getNumero());
                stmtFactura.setDate(3, f.getFechaEmision());
                stmtFactura.setLong(4, f.getEntidadId());
                stmtFactura.setString(5, f.getConcepto());
                stmtFactura.setDouble(6, f.getBaseImponible());
                stmtFactura.setDouble(7, f.getIvaTotal());
                stmtFactura.setDouble(8, f.getTotalFactura());
                stmtFactura.setString(9, f.getEstado());
                stmtFactura.setString(10, f.getObservaciones());
                stmtFactura.executeUpdate();

                // Obtener el ID generado para la factura
                ResultSet rs = stmtFactura.getGeneratedKeys();
                if (rs.next()) {
                    long facturaId = rs.getLong(1);

                    // Insertar todas las líneas asociadas a la factura
                    try (PreparedStatement stmtLinea = conn.prepareStatement(sqlLinea)) {
                        for (LineaFactura lf : lineas) {
                            stmtLinea.setLong(1, facturaId);              // ID de la factura recién creada
                            stmtLinea.setLong(2, lf.getProductoId());     // Producto asociado
                            stmtLinea.setDouble(3, lf.getCantidad());     // Cantidad
                            stmtLinea.setDouble(4, lf.getPrecioUnitario());// Precio unitario
                            stmtLinea.setDouble(5, lf.getDescuento());    // Descuento aplicado
                            stmtLinea.addBatch();                         // Añadir al lote
                        }
                        stmtLinea.executeBatch(); // Ejecutar todas las inserciones de líneas
                    }
                }

                conn.commit(); // Confirmar transacción
            } catch (SQLException e) {
                conn.rollback(); // Revertir si algo falla
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // === LEER TODOS ===
    /**
     * Consulta todas las facturas de la base de datos.
     * 
     * @return Lista de objetos Factura
     */
    public List<Factura> consultarTodos() {
        List<Factura> lista = new ArrayList<>();
        String sql = "SELECT * FROM facturas";

        try (Connection conn = ConexionBD.get();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Factura f = new Factura();
                f.setId(rs.getLong("id"));
                f.setTipo(rs.getString("tipo").charAt(0));
                f.setNumero(rs.getString("numero"));
                f.setFechaEmision(rs.getDate("fecha_emision"));
                f.setEntidadId(rs.getLong("entidad_id"));
                f.setConcepto(rs.getString("concepto"));
                f.setBaseImponible(rs.getDouble("base_imponible"));
                f.setIvaTotal(rs.getDouble("iva_total"));
                f.setTotalFactura(rs.getDouble("total_factura"));
                f.setEstado(rs.getString("estado"));
                f.setObservaciones(rs.getString("observaciones"));
                lista.add(f);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // === LEER UNO ===
    /**
     * Consulta una factura por su ID.
     * 
     * @param id Identificador único de la factura
     * @return Factura encontrada o null si no existe
     */
    public Factura consultarPorId(long id) {
        String sql = "SELECT * FROM facturas WHERE id=?";
        try (Connection conn = ConexionBD.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Factura f = new Factura();
                f.setId(rs.getLong("id"));
                f.setTipo(rs.getString("tipo").charAt(0));
                f.setNumero(rs.getString("numero"));
                f.setFechaEmision(rs.getDate("fecha_emision"));
                f.setEntidadId(rs.getLong("entidad_id"));
                f.setConcepto(rs.getString("concepto"));
                f.setBaseImponible(rs.getDouble("base_imponible"));
                f.setIvaTotal(rs.getDouble("iva_total"));
                f.setTotalFactura(rs.getDouble("total_factura"));
                f.setEstado(rs.getString("estado"));
                f.setObservaciones(rs.getString("observaciones"));
                return f;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // === ACTUALIZAR ===
    /**
     * Modifica los datos de una factura existente.
     * 
     * @param f Objeto Factura con los datos actualizados
     */
    public void modificar(Factura f) {
        String sql = "UPDATE facturas SET tipo=?, numero=?, fecha_emision=?, entidad_id=?, concepto=?, base_imponible=?, iva_total=?, total_factura=?, estado=?, observaciones=? WHERE id=?";
        try (Connection conn = ConexionBD.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, String.valueOf(f.getTipo()));
            stmt.setString(2, f.getNumero());
            stmt.setDate(3, f.getFechaEmision());
            stmt.setLong(4, f.getEntidadId());
            stmt.setString(5, f.getConcepto());
            stmt.setDouble(6, f.getBaseImponible());
            stmt.setDouble(7, f.getIvaTotal());
            stmt.setDouble(8, f.getTotalFactura());
            stmt.setString(9, f.getEstado());
            stmt.setString(10, f.getObservaciones());
            stmt.setLong(11, f.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // === BORRAR ===
    /**
     * Elimina una factura por su ID.
     * 
     * @param id Identificador único de la factura
     */
    public void borrarPorId(long id) {
        String sql = "DELETE FROM facturas WHERE id=?";
        try (Connection conn = ConexionBD.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}