package DAO;

import Modelo.ConexionBD;
import Modelo.Factura;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author luisb
 */
public class FacturaDAO {
    public void insertar(Factura f) {
        String sql = "INSERT INTO factura (tipo, numero, fecha_emision, entidad_id, concepto, base_imponible, iva_total, total_factura, estado, observaciones) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.get(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, String.valueOf(f.getTipo()));
            stmt.setString(2, f.getNumero());
            stmt.setDate(3, new java.sql.Date(f.getFechaEmision().getTime()));
            stmt.setLong(4, f.getEntidadId());
            stmt.setString(5, f.getConcepto());
            stmt.setDouble(6, f.getBaseImponible());
            stmt.setDouble(7, f.getIvaTotal());
            stmt.setDouble(8, f.getTotalFactura());
            stmt.setString(9, f.getEstado());
            stmt.setString(10, f.getObservaciones());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}