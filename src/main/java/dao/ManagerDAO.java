package dao;

import database.DatabaseConnection;
import model.Manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ManagerDAO {

    // Получение менеджера по email
    public Manager getManagerByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM managers WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Manager(
                            rs.getString("login"),
                            rs.getString("email")
                    );
                }
            }
        }
        return null;
    }
}