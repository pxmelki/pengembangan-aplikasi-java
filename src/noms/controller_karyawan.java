package noms;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class controller_karyawan {
    private view_karyawan view;
    private Connection conn;

    public controller_karyawan(view_karyawan v) {
        this.view = v;
        conn = connectdb.getConnection();
        loadTable();
        initActions();
    }

    private void initActions() {
        view.getBtntambah().addActionListener(e -> insert());
        view.getBtnubah().addActionListener(e -> update());
        view.getBtnhapus().addActionListener(e -> delete());
        view.getBtnreset().addActionListener(e -> clear());
        view.getTbkaryawan().getSelectionModel().addListSelectionListener(e -> fillForm());
    }

    private void loadTable() {
        try {
            String sql = "SELECT * FROM karyawan";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(new Object[] {"ID", "Nama", "Jabatan", "No_Telepon", "Email", "Alamat"});

            while (rs.next()) {
                model.addRow(new Object[] {
                    rs.getString("ID_Karyawan"),
                    rs.getString("Nama"),
                    rs.getString("Jabatan"),
                    rs.getString("No_Telepon"),
                    rs.getString("Email"),
                    rs.getString("Alamat")
                });
            }

            view.getTbkaryawan().setModel(model);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal load data: " + e.getMessage());
        }
    }

    private String generateIdKaryawan() {
        try {
            String sql = "SELECT ID_Karyawan FROM karyawan ORDER BY ID_Karyawan DESC LIMIT 1";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String lastId = rs.getString("ID_Karyawan").substring(1);
                int newId = Integer.parseInt(lastId) + 1;
                return String.format("K%03d", newId);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal generate ID: " + e.getMessage());
        }
        return "K001";
    }

    private void insert() {
        try {
            String id = generateIdKaryawan();
            String sql = "INSERT INTO karyawan (ID_Karyawan, Nama, Jabatan, No_Telepon, Email, Alamat) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ps.setString(2, view.getTxtnama().getText());
            ps.setString(3, view.getTxtjabatan().getText());
            ps.setString(4, view.getTxttelepon().getText());
            ps.setString(5, view.getTxtemail().getText());
            ps.setString(6, view.getTxtalamat().getText());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Data berhasil ditambahkan!");
            loadTable();
            clear();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal tambah data: " + e.getMessage());
        }
    }

    private void update() {
        int row = view.getTbkaryawan().getSelectedRow();
        if (row == -1) return;

        String id = view.getTbkaryawan().getValueAt(row, 0).toString();

        try {
            String sql = "UPDATE karyawan SET Nama=?, Jabatan=?, No_Telepon=?, Email=?, Alamat=? WHERE ID_Karyawan=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, view.getTxtnama().getText());
            ps.setString(2, view.getTxtjabatan().getText());
            ps.setString(3, view.getTxttelepon().getText());
            ps.setString(4, view.getTxtemail().getText());
            ps.setString(5, view.getTxtalamat().getText());
            ps.setString(6, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Data berhasil diubah!");
            loadTable();
            clear();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal ubah data: " + e.getMessage());
        }
    }

    private void delete() {
        int row = view.getTbkaryawan().getSelectedRow();
        if (row == -1) return;

        String id = view.getTbkaryawan().getValueAt(row, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(null, "Hapus data ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            String sql = "DELETE FROM karyawan WHERE ID_Karyawan=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Data berhasil dihapus!");
            loadTable();
            clear();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal hapus data: " + e.getMessage());
        }
    }

    private void clear() {
        view.getTxtnama().setText("");
        view.getTxtjabatan().setText("");
        view.getTxttelepon().setText("");
        view.getTxtemail().setText("");
        view.getTxtalamat().setText("");
        view.getTbkaryawan().clearSelection();
    }

    private void fillForm() {
        int row = view.getTbkaryawan().getSelectedRow();
        if (row != -1) {
            view.getTxtnama().setText(view.getTbkaryawan().getValueAt(row, 1).toString());
            view.getTxtjabatan().setText(view.getTbkaryawan().getValueAt(row, 2).toString());
            view.getTxttelepon().setText(view.getTbkaryawan().getValueAt(row, 3).toString());
            view.getTxtemail().setText(view.getTbkaryawan().getValueAt(row, 4).toString());
            view.getTxtalamat().setText(view.getTbkaryawan().getValueAt(row, 5).toString());
        }
    }
}
