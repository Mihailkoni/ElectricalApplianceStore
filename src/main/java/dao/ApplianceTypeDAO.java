package dao;

import database.DatabaseConnection;
import model.ApplianceType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ApplianceTypeDAO {

    // получение всех типов электроприборов
    public List<ApplianceType> getAllApplianceTypes() throws SQLException {
        List<ApplianceType> types = new ArrayList<>();
        String sql = "SELECT * FROM appliance_types";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ApplianceType type = new ApplianceType(
                        rs.getInt("type_id"),
                        rs.getString("type_name")
                );
                types.add(type);
            }
        }
        return types;
    }

    // получение типа электроприбора по его ID
    public ApplianceType getApplianceTypeById(int typeId) throws SQLException {
        String sql = "SELECT * FROM appliance_types WHERE type_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, typeId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new ApplianceType(
                            rs.getInt("type_id"),
                            rs.getString("type_name")
                    );
                }
            }
        }
        return null;
    }

    // добавление типа электроприбора
    public void addApplianceType(ApplianceType type) throws SQLException {
        String sql = "INSERT INTO appliance_types (type_name) VALUES (?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, type.getTypeName());
            stmt.executeUpdate();

            // Получаем сгенерированный ID
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    type.setTypeId(generatedKeys.getInt(1)); // Устанавливаем ID нового типа
                }
            }
        }
    }
}