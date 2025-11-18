package DAO;

import Modelo.ConexionBD;
import Modelo.LineaFactura;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author luisb
 */
public class LineaFacturaDAO {
    public void insertar(LineaFactura l) {
        String sql = "INSERT INTO factura_producto (factura_id, producto_id, cantidad, precio_unitario, descuento) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.get(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, l.getFacturaId());
            stmt.setLong(2, l.getProductoId());
            stmt.setDouble(3, l.getCantidad());
            stmt.setDouble(4, l.getPrecioUnitario());
            stmt.setDouble(5, l.getDescuento());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}