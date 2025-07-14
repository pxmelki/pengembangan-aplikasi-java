package noms;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class connectdb {
    private static Connection conn;

    public static Connection getConnection() {
        if(conn == null){
            try {
                String url = "jdbc:mysql://localhost:3306/noms";
                String user = "root";
                String pass = "";
                Class.forName("com.mysql.cj.jdbc.Driver");
                conn = DriverManager.getConnection(url, user, pass);
            } catch (ClassNotFoundException | SQLException e) {
                System.out.println("Koneksi Gagal : " + e.getMessage());
            }
        }
        return conn;
    }
}
