package DAO;

import Modelo.ConexionBD;
import Modelo.Producto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la entidad Producto.
 * Corrección: Ahora lee correctamente el empresa_id al consultar.
 * @author luisb
 */
public class ProductoDAO {

    // === CREAR ===
    public void añadir(Producto producto, long empresaId) {
        String sql = "INSERT INTO producto (empresa_id, codigo, descripcion, proveedor_id, precio_venta, stock) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, empresaId);
            stmt.setString(2, producto.getCodigo());
            stmt.setString(3, producto.getDescripcion());

            if (producto.getProveedorId() != null) {
                stmt.setLong(4, producto.getProveedorId());
            } else {
                stmt.setNull(4, Types.BIGINT);
            }

            stmt.setDouble(5, producto.getPrecioVenta());
            stmt.setDouble(6, producto.getStock());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // === LEER TODOS ===
    public List<Producto> consultarTodos(long empresaId) {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM producto WHERE empresa_id=?";

        try (Connection conn = ConexionBD.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, empresaId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Producto p = new Producto();
                p.setId(rs.getLong("id"));
                // CORRECCIÓN IMPORTANTE: Cargar el empresa_id
                p.setEmpresaId(rs.getLong("empresa_id")); 
                
                p.setCodigo(rs.getString("codigo"));
                p.setDescripcion(rs.getString("descripcion"));
                p.setProveedorId(rs.getObject("proveedor_id", Long.class));
                
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
    public List<Producto> consultarProductos(long empresaId, String busquedaCodigo) {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM producto WHERE empresa_id=? AND codigo LIKE ?";

        try (Connection conn = ConexionBD.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, empresaId);
            stmt.setString(2, "%" + busquedaCodigo + "%");
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Producto p = new Producto();
                p.setId(rs.getLong("id"));
                p.setEmpresaId(rs.getLong("empresa_id"));
                p.setCodigo(rs.getString("codigo"));
                p.setDescripcion(rs.getString("descripcion"));
                p.setProveedorId(rs.getObject("proveedor_id", Long.class));
                p.setPrecioVenta(rs.getDouble("precio_venta"));
                p.setStock(rs.getDouble("stock"));
                lista.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // === ACTUALIZAR ===
    public void modificar(Producto producto) {
        String sql = "UPDATE producto SET descripcion=?, proveedor_id=?, precio_venta=?, stock=? " +
                     "WHERE codigo=? AND empresa_id=?";

        try (Connection conn = ConexionBD.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, producto.getDescripcion());

            if (producto.getProveedorId() != null) {
                stmt.setLong(2, producto.getProveedorId());
            } else {
                stmt.setNull(2, Types.BIGINT);
            }

            stmt.setDouble(3, producto.getPrecioVenta());
            stmt.setDouble(4, producto.getStock());
            
            // WHERE
            stmt.setString(5, producto.getCodigo());
            stmt.setLong(6, producto.getEmpresaId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // === BORRAR ===
    public void borrarPorCodigo(long empresaId, String codigo) {
        String sql = "DELETE FROM producto WHERE codigo=? AND empresa_id=?";
        try (Connection conn = ConexionBD.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codigo);
            stmt.setLong(2, empresaId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}