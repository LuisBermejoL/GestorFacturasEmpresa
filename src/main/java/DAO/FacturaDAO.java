package DAO;

import Modelo.ConexionBD;
import Modelo.Factura;
import Modelo.LineaFactura;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) para la entidad Factura.
 * Gestiona operaciones CRUD y la inserción de facturas con sus líneas
 * en una sola transacción.
 *
 * En la base de datos corresponde a las tablas 'factura' y 'factura_producto'.
 * Todas las consultas están filtradas por empresa_id para respetar el modelo multiempresa.
 *
 * @author luisb
 */
public class FacturaDAO {

    // === CREAR (Factura + Líneas) ===
    /**
     * Inserta una factura junto con todas sus líneas en una sola transacción.
     * Se asegura de vincular la factura a la empresa activa mediante empresa_id.
     *
     * @param f       Objeto Factura con los datos de cabecera
     * @param lineas  Lista de objetos LineaFactura con los detalles de la factura
     */
    public void añadir(Factura f, List<LineaFactura> lineas) {
        // SQL para insertar cabecera de factura
        String sqlFactura = "INSERT INTO factura (empresa_id, tipo, numero, fecha_emision, entidad_id, concepto, base_imponible, iva_total, total_factura, estado, observaciones) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        // SQL para insertar líneas de factura
        String sqlLinea = "INSERT INTO factura_producto (factura_id, producto_id, cantidad, precio_unitario, descuento) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.get()) {
            conn.setAutoCommit(false); // Iniciamos transacción manual

            try (PreparedStatement stmtFactura = conn.prepareStatement(sqlFactura, Statement.RETURN_GENERATED_KEYS)) {
                // Insertar cabecera de factura
                stmtFactura.setLong(1, f.getEmpresaId()); // empresa_id
                stmtFactura.setString(2, String.valueOf(f.getTipo())); // tipo (V/C)
                stmtFactura.setString(3, f.getNumero()); // número único por empresa
                stmtFactura.setDate(4, f.getFechaEmision()); // fecha emisión
                stmtFactura.setLong(5, f.getEntidadId()); // cliente/proveedor asociado
                stmtFactura.setString(6, f.getConcepto()); // concepto
                stmtFactura.setDouble(7, f.getBaseImponible()); // base imponible
                stmtFactura.setDouble(8, f.getIvaTotal()); // total IVA
                stmtFactura.setDouble(9, f.getTotalFactura()); // total factura
                stmtFactura.setString(10, f.getEstado()); // estado (PENDIENTE, PAGADA, etc.)
                stmtFactura.setString(11, f.getObservaciones()); // observaciones
                stmtFactura.executeUpdate();

                // Obtener ID generado para la factura
                ResultSet rs = stmtFactura.getGeneratedKeys();
                if (rs.next()) {
                    long facturaId = rs.getLong(1);

                    // Insertar todas las líneas asociadas
                    try (PreparedStatement stmtLinea = conn.prepareStatement(sqlLinea)) {
                        for (LineaFactura lf : lineas) {
                            stmtLinea.setLong(1, facturaId); // factura_id
                            stmtLinea.setLong(2, lf.getProductoId()); // producto_id
                            stmtLinea.setDouble(3, lf.getCantidad()); // cantidad
                            stmtLinea.setDouble(4, lf.getPrecioUnitario()); // precio unitario
                            stmtLinea.setDouble(5, lf.getDescuento()); // descuento
                            stmtLinea.addBatch(); // añadir al lote
                        }
                        stmtLinea.executeBatch(); // ejecutar todas las inserciones
                    }
                }

                conn.commit(); // confirmar transacción
            } catch (SQLException e) {
                conn.rollback(); // revertir si algo falla
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // === LEER TODOS ===
    /**
     * Consulta todas las facturas de una empresa.
     *
     * @param empresaId ID de la empresa
     * @return Lista de facturas de esa empresa
     */
    public List<Factura> consultarTodos(long empresaId) {
        List<Factura> lista = new ArrayList<>();
        String sql = "SELECT * FROM factura WHERE empresa_id=?";

        try (Connection conn = ConexionBD.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, empresaId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Factura f = new Factura();
                f.setId(rs.getLong("id"));
                f.setEmpresaId(rs.getLong("empresa_id"));
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
     * Consulta una factura por su ID dentro de una empresa.
     *
     * @param empresaId ID de la empresa
     * @param busquedaNumero Número de la factura
     * @return Factura encontrada o null si no existe
     */
    public List<Factura> consultarFacturas(long empresaId, String busquedaNumero) {
        List<Factura> lista = new ArrayList<>();
        String sql = "SELECT * FROM factura WHERE empresa_id=? AND numero LIKE ?";

        try (Connection conn = ConexionBD.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, empresaId);
            stmt.setString(2, "%" + busquedaNumero + "%");
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Factura f = new Factura();
                f.setId(rs.getLong("id"));
                f.setEmpresaId(rs.getLong("empresa_id"));
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

    // === ACTUALIZAR ===
    /**
     * Modifica los datos de una factura existente.
     * Se asegura de que la factura pertenece a la empresa indicada.
     *
     * @param f Objeto Factura con los datos actualizados
     */
    public void modificar(Factura f) {
        String sql = "UPDATE factura SET tipo=?, numero=?, fecha_emision=?, entidad_id=?, concepto=?, base_imponible=?, iva_total=?, total_factura=?, estado=?, observaciones=? " +
                     "WHERE id=? AND empresa_id=?";
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
            stmt.setLong(12, f.getEmpresaId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // === BORRAR ===
    /**
     * Elimina una factura por su ID dentro de una empresa.
     *
     * @param empresaId ID de la empresa
     * @param id        ID de la factura
     */
    public void borrarPorId(long empresaId, long id) {
        String sql = "DELETE FROM factura WHERE id=? AND empresa_id=?";
        try (Connection conn = ConexionBD.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.setLong(2, empresaId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}