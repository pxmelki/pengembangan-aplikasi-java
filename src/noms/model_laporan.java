package noms;

import java.sql.*;

public class model_laporan {

public ResultSet getLaporanByTanggal(String tanggalAwal, String tanggalAkhir) throws SQLException {
    Connection conn = connectdb.getConnection();
    String sql = "SELECT " +
                 "t.Tanggal, t.ID_Transaksi, " +
                 "GROUP_CONCAT(CONCAT(m.Nama_Menu, ' (', d.Jumlah_Item, ')') SEPARATOR ', ') AS Menu, " +
                 "GROUP_CONCAT(DISTINCT m.Kategori SEPARATOR ', ') AS Kategori, " +
                 "SUM(d.Sub_Total) AS Subtotal, " +
                 "t.Total_Harga AS Total, " +
                 "k.Nama AS Kasir, " +
                 "IFNULL(p.Nama, '-') AS Pelanggan " +
                 "FROM transaksi t " +
                 "JOIN detail_transaksi d ON t.ID_Transaksi = d.Transaksi_ID_Transaksi " +
                 "JOIN daftar_menu m ON m.ID_Menu = d.Daftar_Menu_ID_Menu " +
                 "JOIN karyawan k ON k.ID_Karyawan = t.Karyawan_ID_Karyawan " +
                 "LEFT JOIN pelanggan p ON p.ID_Pelanggan = t.Pelanggan_ID_Pelanggan " +
                 "WHERE t.Tanggal BETWEEN ? AND ? " +
                 "GROUP BY t.ID_Transaksi " +
                 "ORDER BY t.ID_Transaksi";

    PreparedStatement ps = conn.prepareStatement(sql);
    ps.setString(1, tanggalAwal);
    ps.setString(2, tanggalAkhir);

    return ps.executeQuery();
}

    
}
