package DAO;

import Modelo.ConexionBD;
import Modelo.Producto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) para la entidad Producto.
 * Contiene métodos CRUD (Crear, Leer, Actualizar, Borrar).
 *
 * En la base de datos corresponde a la tabla 'producto'.
 * Todas las consultas están filtradas por empresa_id para respetar el modelo multiempresa.
 *
 * @author luisb
 */
public class ProductoDAO {

    // === CREAR ===
    /**
     * Inserta un nuevo producto en la base de datos.
     *
     * @param producto   Objeto Producto con los datos a insertar
     * @param empresaId  ID de la empresa a la que pertenece el producto
     */
    public void añadir(Producto producto, long empresaId) {
        String sql = "INSERT INTO producto (empresa_id, codigo, descripcion, referencia_proveedor, proveedor_id, tipo_iva_id, precio_coste, precio_venta, stock) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, empresaId);
            stmt.setString(2, producto.getCodigo());
            stmt.setString(3, producto.getDescripcion());
            stmt.setString(4, producto.getReferenciaProveedor());

            if (producto.getProveedorId() != null) {
                stmt.setLong(5, producto.getProveedorId());
            } else {
                stmt.setNull(5, Types.BIGINT);
            }

            stmt.setInt(6, producto.getTipoIVAId());
            stmt.setDouble(7, producto.getPrecioCoste());
            stmt.setDouble(8, producto.getPrecioVenta());
            stmt.setDouble(9, producto.getStock());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // === LEER TODOS ===
    /**
     * Consulta todos los productos de una empresa.
     *
     * @param empresaId ID de la empresa
     * @return Lista de objetos Producto
     */
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
    /**
     * Consulta un producto por su código dentro de una empresa.
     *
     * @param empresaId ID de la empresa
     * @param codigo    Código único del producto
     * @return Producto encontrado o null si no existe
     */
    public Producto consultarPorCodigo(long empresaId, String codigo) {
        String sql = "SELECT * FROM producto WHERE empresa_id=? AND codigo=?";
        try (Connection conn = ConexionBD.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, empresaId);
            stmt.setString(2, codigo);
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
    /**
     * Modifica los datos de un producto existente.
     * Se asegura de que el producto pertenece a la empresa indicada.
     *
     * @param producto Objeto Producto con los datos actualizados
     */
    public void modificar(Producto producto) {
        String sql = "UPDATE producto SET descripcion=?, referencia_proveedor=?, proveedor_id=?, tipo_iva_id=?, precio_coste=?, precio_venta=?, stock=? " +
                     "WHERE codigo=? AND empresa_id=?";

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
            stmt.setLong(9, producto.getEmpresaId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // === BORRAR ===
    /**
     * Elimina un producto por su código dentro de una empresa.
     *
     * @param empresaId ID de la empresa
     * @param codigo    Código único del producto
     */
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