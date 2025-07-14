package noms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class controller_mt {
    private Connection conn;

    public controller_mt() {
        conn = connectdb.getConnection();
    }

    public void tampilData(DefaultTableModel model) {
        try {
            String sql = "SELECT * FROM daftar_menu ORDER BY ID_Menu ASC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Object[] data = {
                    rs.getString("ID_Menu"),
                    rs.getString("Nama_Menu"),
                    rs.getDouble("Harga"),
                    rs.getString("Kategori")
                };
                model.addRow(data);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal menampilkan data: " + e.getMessage());
        }
    }

    public void insert(model_mt model) {
        try {
            String idBaru = generateIdMenu(model.getKategori());
            String sql = "INSERT INTO daftar_menu (ID_Menu, Nama_Menu, Harga, Kategori) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, idBaru);
            ps.setString(2, model.getNama_menu());
            ps.setDouble(3, model.getHarga());
            ps.setString(4, model.getKategori());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Data berhasil ditambahkan!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal menambahkan data: " + e.getMessage());
        }
    }

    public void update(model_mt model) {
        try {
            String sql = "UPDATE daftar_menu SET Nama_Menu=?, Harga=?, Kategori=? WHERE ID_Menu=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, model.getNama_menu());
            ps.setDouble(2, model.getHarga());
            ps.setString(3, model.getKategori());
            ps.setString(4, model.getId_menu());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Data berhasil diubah!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal mengubah data: " + e.getMessage());
        }
    }

    public void delete(String id_menu) {
        try {
            String sql = "DELETE FROM daftar_menu WHERE ID_Menu=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id_menu);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Data berhasil dihapus!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal menghapus data: " + e.getMessage());
        }
    }

    private String generateIdMenu(String kategori) {
        String prefix = kategori.equalsIgnoreCase("Makanan") ? "MK" : "MN";
        String newId = prefix + "001";
        try {
            String sql = "SELECT ID_Menu FROM daftar_menu WHERE ID_Menu LIKE ? ORDER BY LENGTH(ID_Menu) DESC, ID_Menu DESC LIMIT 1";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, prefix + "%");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String lastId = rs.getString("ID_Menu");
                int lastNum = Integer.parseInt(lastId.substring(2));
                int nextNum = lastNum + 1;
                newId = String.format(prefix + "%03d", nextNum);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error generate ID: " + e.getMessage());
        }
        return newId;
    }
}
