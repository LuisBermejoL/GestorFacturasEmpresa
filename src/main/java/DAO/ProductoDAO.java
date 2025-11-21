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

    // === CREAR ===
    public void añadir(Producto producto) {
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

    // === LEER TODOS ===
    public List<Producto> consultarTodos() {
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

    // === LEER UNO ===
    public Producto consultarPorCodigo(String codigo) {
        String sql = "SELECT * FROM producto WHERE codigo=?";
        try (Connection conn = ConexionBD.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, codigo);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
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
                return p;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // === ACTUALIZAR ===
    public void modificar(Producto producto) {
        String sql = "UPDATE producto SET descripcion=?, referencia_proveedor=?, proveedor_id=?, tipo_iva_id=?, precio_coste=?, precio_venta=?, stock=? WHERE codigo=?";

        try (Connection conn = ConexionBD.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, producto.getDescripcion());
            stmt.setString(2, producto.getReferenciaProveedor());
            if (producto.getProveedorId() != null) {
                stmt.setLong(3, producto.getProveedorId());
            } else {
                stmt.setNull(3, Types.BIGINT);
            }
            stmt.setInt(4, producto.getTipoIVAId());
            stmt.setDouble(5, producto.getPrecioCoste());
            stmt.setDouble(6, producto.getPrecioVenta());
            stmt.setDouble(7, producto.getStock());
            stmt.setString(8, producto.getCodigo());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // === BORRAR ===
    public void borrarPorCodigo(String codigo) {
        String sql = "DELETE FROM producto WHERE codigo=?";
        try (Connection conn = ConexionBD.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, codigo);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}