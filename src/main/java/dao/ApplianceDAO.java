package dao;

import database.DatabaseConnection;
import model.Appliance;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ApplianceDAO {

    // добавление электроприбора
    public void addAppliance(Appliance appliance) throws SQLException {
        String sql = "INSERT INTO appliances (name, type_id, price, stock_quantity) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, appliance.getName());
            stmt.setInt(2, appliance.getTypeId());
            stmt.setDouble(3, appliance.getPrice());
            stmt.setInt(4, appliance.getStockQuantity());
            stmt.executeUpdate();
        }
    }

    // обновление данных о электроприбре (цена, количество)
    public void updateAppliance(Appliance appliance) throws SQLException {
        String sql = "UPDATE appliances SET name = ?, type_id = ?, price = ?, stock_quantity = ? WHERE article_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, appliance.getName());
            stmt.setInt(2, appliance.getTypeId());
            stmt.setDouble(3, appliance.getPrice());
            stmt.setInt(4, appliance.getStockQuantity());
            stmt.setInt(5, appliance.getArticleNumber());
            stmt.executeUpdate();
        }
    }

    // обновление количества товара на складе
    public void updateApplianceStock(int articleNumber, int stockQuantity) throws SQLException {
        String sql = "UPDATE appliances SET stock_quantity = ? WHERE article_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, stockQuantity);
            stmt.setInt(2, articleNumber);
            stmt.executeUpdate();
        }
    }

    // получение данных о электроприборах
    public List<Appliance> getAllAppliances() throws SQLException {
        List<Appliance> appliances = new ArrayList<>();
        String sql = "SELECT a.article_number, a.name, a.type_id, t.type_name, a.price, a.stock_quantity " +
                "FROM appliances a " +
                "JOIN appliance_types t ON a.type_id = t.type_id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Appliance appliance = new Appliance(
                        rs.getInt("article_number"),
                        rs.getString("name"),
                        rs.getInt("type_id"),
                        rs.getString("type_name"),
                        rs.getDouble("price"),
                        rs.getInt("stock_quantity")
                );
                appliances.add(appliance);
            }
        }
        return appliances;
    }
}