package noms;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class controller_transaksi {
    private model_transaksi model;
    private Connection conn;
    private Map<String, String> menuMap = new HashMap<>();

    public controller_transaksi(model_transaksi model) {
        this.model = model;
        conn = connectdb.getConnection();
    }

    public ArrayList<String> loadMenu() {
        ArrayList<String> listMenu = new ArrayList<>();
        try {
            String query = "SELECT id_menu, Nama_Menu FROM daftar_menu";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String id = rs.getString("id_menu");
                String nama = rs.getString("Nama_Menu");
                menuMap.put(nama, id); 
                listMenu.add(nama);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listMenu;
    }

    public String getIdMenuByName(String namaMenu) {
        return menuMap.getOrDefault(namaMenu, "-");
    }

    public double getHargaByMenu(String namaMenu) {
        double harga = 0;
        try {
            String query = "SELECT Harga FROM daftar_menu WHERE Nama_Menu = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, namaMenu);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                harga = rs.getDouble("Harga");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return harga;
    }

    public String generateIdTransaksi() {
        String newId = "TRX001";
        try {
            String query = "SELECT MAX(ID_Transaksi) AS last_id FROM transaksi";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String lastId = rs.getString("last_id");
                if (lastId != null) {
                    int number = Integer.parseInt(lastId.replaceAll("[^0-9]", "")) + 1;
                    newId = String.format("TRX%03d", number);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return newId;
    }

    public boolean simpanTransaksi() {
        boolean success = false;

        try {
            conn.setAutoCommit(false);

            String sqlTrans = "INSERT INTO transaksi (ID_Transaksi, Tanggal, Total_Harga, karyawan_ID_Karyawan) VALUES (?, ?, ?, ?)";
            PreparedStatement psTrans = conn.prepareStatement(sqlTrans);
            psTrans.setString(1, model.getIdTransaksi());
            psTrans.setString(2, model.getTanggal());
            psTrans.setDouble(3, model.getTotalHarga());
            psTrans.setString(4, model.getCurrentUser());
            psTrans.executeUpdate();

            String sqlDetail = "INSERT INTO detail_transaksi (Transaksi_ID_Transaksi, Daftar_Menu_ID_Menu, Jumlah_Item, Sub_Total) VALUES (?, ?, ?, ?)";
            int idDetail = generateNextDetailId();
            for (model_transaksi.model_item item : model.getDaftarItem()) {
                String idMenu = item.getKodeMenu();

                PreparedStatement psDetail = conn.prepareStatement(sqlDetail);
                psDetail.setString(1, model.getIdTransaksi());
                psDetail.setString(2, idMenu);
                psDetail.setInt(3, item.getJumlah());
                psDetail.setDouble(4, item.getSubtotal());
                psDetail.executeUpdate();
            }

            conn.commit();
            success = true;
        } catch (SQLException e) {
            System.out.println("Gagal simpan transaksi: " + e.getMessage());
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return success;
    }

    private int generateNextDetailId() throws SQLException {
        String query = "SELECT MAX(ID_Detail) AS max_id FROM detail_transaksi";
        PreparedStatement ps = conn.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt("max_id") + 1;
        }
        return 1;
    }

}
