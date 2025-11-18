package DAO;

import Modelo.ConexionBD;
import Modelo.Producto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author luisb
 */
public class ProductoDAO {
    public void insertar(Producto producto) {
        String sql = "INSERT INTO producto (codigo, descripcion, referencia_proveedor, proveedor_id, tipo_iva_id, precio_coste, precio_venta, stock) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, producto.getCodigo());
            stmt.setString(2, producto.getDescripcion());
            stmt.setString(3, producto.getReferenciaProveedor());
            if (producto.getProveedorId() != null) {
                stmt.setLong(4, producto.getProveedorId());
            } else {
                stmt.setNull(4, Types.BIGINT);
            }
            stmt.setInt(5, producto.getTipoIVAId());
            stmt.setDouble(6, producto.getPrecioCoste());
            stmt.setDouble(7, producto.getPrecioVenta());
            stmt.setDouble(8, producto.getStock());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Producto> listar() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM producto";

        try (Connection conn = ConexionBD.get();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Producto p = new Producto();
                p.setId(rs.getLong("id"));
                p.setCodigo(rs.getString("codigo"));
                p.setDescripcion(rs.getString("descripcion"));
                p.setReferenciaProveedor(rs.getString("referencia_proveedor"));
                p.setProveedorId(rs.getObject("proveedor_id", Long.class));
                p.setTipoIVAId(rs.getInt("tipo_iva_id"));
                p.setPrecioCoste(rs.getDouble("precio_coste"));
                p.setPrecioVenta(rs.getDouble("precio_venta"));
                p.setStock(rs.getDouble("stock"));
                lista.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
}