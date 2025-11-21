package DAO;

import Modelo.ConexionBD;
import Modelo.Factura;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author luisb
 */
public class FacturaDAO {

    // === CREAR ===
    public void añadir(Factura f) {
        String sql = "INSERT INTO facturas (tipo, numero, fecha_emision, entidad_id, concepto, base_imponible, iva_total, total_factura, estado, observaciones) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, String.valueOf(f.getTipo())); // char convertido a String
            stmt.setString(2, f.getNumero());
            stmt.setDate(3, f.getFechaEmision());
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

    // === LEER TODOS ===
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