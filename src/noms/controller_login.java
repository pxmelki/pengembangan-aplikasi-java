package noms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;

public class controller_login {
    private Connection conn;

    public controller_login() {
        conn = connectdb.getConnection();
    }

    public String cekLogin(model_login loginModel) {
        String role = "";
        try {
            String sql = "SELECT role FROM user WHERE username=? AND password=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, loginModel.getUsername());
            ps.setString(2, loginModel.getPassword());
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                role = rs.getString("role");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error login: " + e.getMessage());
        }
        return role;
    }
}
