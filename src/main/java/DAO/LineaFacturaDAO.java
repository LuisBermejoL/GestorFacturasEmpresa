package DAO;

import Modelo.ConexionBD;
import Modelo.LineaFactura;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) para la entidad LineaFactura.
 * Contiene métodos CRUD (Crear, Leer, Actualizar, Borrar) y utilidades
 * para consultar/borrar líneas asociadas a una factura.
 *
 * En la base de datos corresponde a la tabla 'factura_producto'.
 * Todas las consultas están filtradas por empresa_id a través de la relación con la tabla 'factura'.
 *
 * @author luisb
 */
public class LineaFacturaDAO {

    // === CREAR ===
    /**
     * Inserta una nueva línea de factura en la base de datos.
     *
     * @param lf Objeto LineaFactura con los datos a insertar
     */
    public void añadir(LineaFactura lf) {
        String sql = "INSERT INTO factura_producto (factura_id, producto_id, cantidad, precio_unitario, descuento) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, lf.getFacturaId());       // ID de la factura asociada
            stmt.setLong(2, lf.getProductoId());      // ID del producto
            stmt.setDouble(3, lf.getCantidad());      // Cantidad
            stmt.setDouble(4, lf.getPrecioUnitario()); // Precio unitario
            stmt.setDouble(5, lf.getDescuento());     // Descuento

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // === LEER TODOS ===
    /**
     * Consulta todas las líneas de una factura dentro de una empresa.
     *
     * @param empresaId ID de la empresa
     * @param facturaId ID de la factura
     * @return Lista de objetos LineaFactura
     */
    public List<LineaFactura> consultarTodos(long empresaId, long facturaId) {
        List<LineaFactura> lista = new ArrayList<>();
        String sql = "SELECT fp.* FROM factura_producto fp " +
                     "JOIN factura f ON fp.factura_id = f.id " +
                     "WHERE f.empresa_id=? AND f.id=?";

        try (Connection conn = ConexionBD.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, empresaId);
            stmt.setLong(2, facturaId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                LineaFactura lf = new LineaFactura();
                lf.setId(rs.getLong("id"));
                lf.setFacturaId(rs.getLong("factura_id"));
                lf.setProductoId(rs.getLong("producto_id"));
                lf.setCantidad(rs.getDouble("cantidad"));
                lf.setPrecioUnitario(rs.getDouble("precio_unitario"));
                lf.setDescuento(rs.getDouble("descuento"));
                lista.add(lf);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // === LEER UNO ===
    /**
     * Consulta una línea de factura por su ID dentro de una empresa.
     *
     * @param empresaId ID de la empresa
     * @param id        Identificador único de la línea
     * @return LineaFactura encontrada o null si no existe
     */
    public LineaFactura consultarPorId(long empresaId, long id) {
        String sql = "SELECT fp.* FROM factura_producto fp " +
                     "JOIN factura f ON fp.factura_id = f.id " +
                     "WHERE f.empresa_id=? AND fp.id=?";
        try (Connection conn = ConexionBD.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, empresaId);
            stmt.setLong(2, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                LineaFactura lf = new LineaFactura();
                lf.setId(rs.getLong("id"));
                lf.setFacturaId(rs.getLong("factura_id"));
                lf.setProductoId(rs.getLong("producto_id"));
                lf.setCantidad(rs.getDouble("cantidad"));
                lf.setPrecioUnitario(rs.getDouble("precio_unitario"));
                lf.setDescuento(rs.getDouble("descuento"));
                return lf;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // === ACTUALIZAR ===
    /**
     * Modifica los datos de una línea de factura existente.
     *
     * @param lf Objeto LineaFactura con los datos actualizados
     */
    public void modificar(LineaFactura lf) {
        String sql = "UPDATE factura_producto SET factura_id=?, producto_id=?, cantidad=?, precio_unitario=?, descuento=? WHERE id=?";
        try (Connection conn = ConexionBD.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, lf.getFacturaId());
            stmt.setLong(2, lf.getProductoId());
            stmt.setDouble(3, lf.getCantidad());
            stmt.setDouble(4, lf.getPrecioUnitario());
            stmt.setDouble(5, lf.getDescuento());
            stmt.setLong(6, lf.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // === BORRAR ===
    /**
     * Elimina una línea de factura por su ID.
     *
     * @param id Identificador único de la línea
     */
    public void borrarPorId(long id) {
        String sql = "DELETE FROM factura_producto WHERE id=?";
        try (Connection conn = ConexionBD.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // === UTILIDADES ===
    /**
     * Consulta todas las líneas asociadas a una factura.
     *
     * @param facturaId ID de la factura
     * @return Lista de líneas de factura
     */
    public List<LineaFactura> consultarPorFacturaId(long facturaId) {
        List<LineaFactura> lista = new ArrayList<>();
        String sql = "SELECT * FROM factura_producto WHERE factura_id=?";

        try (Connection conn = ConexionBD.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, facturaId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                LineaFactura lf = new LineaFactura();
                lf.setId(rs.getLong("id"));
                lf.setFacturaId(rs.getLong("factura_id"));
                lf.setProductoId(rs.getLong("producto_id"));
                lf.setCantidad(rs.getDouble("cantidad"));
                lf.setPrecioUnitario(rs.getDouble("precio_unitario"));
                lf.setDescuento(rs.getDouble("descuento"));
                lista.add(lf);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Elimina todas las líneas asociadas a una factura.
     *
     * @param facturaId ID de la factura
     */
    public void borrarPorFacturaId(long facturaId) {
        String sql = "DELETE FROM factura_producto WHERE factura_id=?";
        try (Connection conn = ConexionBD.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, facturaId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}