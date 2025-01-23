package dao;

import database.DatabaseConnection;
import model.Client;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClientDAO {

    // получение ID клиента по номеру паспорта
    public int getClientIdByPassport(String passportNumber) throws SQLException {
        String sql = "SELECT client_id FROM clients WHERE passport_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, passportNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("client_id");
                }
            }
        }
        return -1;
    }

    // добавление клиента
    public void addClient(Client client, String phoneNumber) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String sqlClient = "INSERT INTO clients (passport_number, passport_series, full_name) VALUES (?, ?, ?)";
            String sqlContact = "INSERT INTO contact_info (client_id, phone_number) VALUES (?, ?)";

            try (PreparedStatement stmtClient = conn.prepareStatement(sqlClient, PreparedStatement.RETURN_GENERATED_KEYS);
                 PreparedStatement stmtContact = conn.prepareStatement(sqlContact)) {

                stmtClient.setString(1, client.getPassportNumber());
                stmtClient.setString(2, client.getPassportSeries());
                stmtClient.setString(3, client.getFullName());
                stmtClient.executeUpdate();

                try (ResultSet generatedKeys = stmtClient.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int clientId = generatedKeys.getInt(4);

                        stmtContact.setInt(1, clientId);
                        stmtContact.setString(2, phoneNumber);
                        stmtContact.executeUpdate();
                    } else {
                        throw new SQLException("Не удалось получить сгенерированный ID клиента.");
                    }
                }

                conn.commit();
            }
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
            }
        }
    }

    // обновление данных клиента и его номера телефона
    public void updateClient(Client client, String phoneNumber) throws SQLException {
        String sqlClient = "UPDATE clients SET full_name = ? WHERE passport_number = ? AND passport_series = ?";
        String sqlContact = "UPDATE contact_info SET phone_number = ? WHERE client_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmtClient = conn.prepareStatement(sqlClient);
             PreparedStatement stmtContact = conn.prepareStatement(sqlContact)) {

            stmtClient.setString(1, client.getFullName());
            stmtClient.setString(2, client.getPassportNumber());
            stmtClient.setString(3, client.getPassportSeries());
            stmtClient.executeUpdate();

            int clientId = getClientIdByPassport(client.getPassportNumber());

            stmtContact.setString(1, phoneNumber);
            stmtContact.setInt(2, clientId);
            stmtContact.executeUpdate();
        }
    }

    // получение клиента по номеру паспорта
    public Client getClientByPassport(String passportNumber) throws SQLException {
        String sql = "SELECT c.passport_number, c.passport_series, c.full_name, ci.phone_number " +
                "FROM clients c " +
                "LEFT JOIN contact_info ci ON c.client_id = ci.client_id " +
                "WHERE c.passport_number = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, passportNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String passportSeries = rs.getString("passport_series");
                    String fullName = rs.getString("full_name");
                    String phoneNumber = rs.getString("phone_number");

                    Client client = new Client(passportNumber, passportSeries, fullName);
                    return client;
                }
            }
        }
        return null;
    }

    // получение всех клиентов
    public List<Client> getAllClients() throws SQLException {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT c.client_id, c.passport_number, c.passport_series, c.full_name, ci.phone_number " +
                "FROM clients c " +
                "LEFT JOIN contact_info ci ON c.client_id = ci.client_id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Client client = new Client(
                        rs.getString("passport_number"),
                        rs.getString("passport_series"),
                        rs.getString("full_name")
                );
                client.setPhoneNumber(rs.getString("phone_number")); // Устанавливаем номер телефона
                clients.add(client);
            }
        }
        return clients;
    }

    // получение номера телефона по ID клиента
    public String getPhoneNumberByClientId(int clientId) throws SQLException {
        String sql = "SELECT phone_number FROM contact_info WHERE client_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clientId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("phone_number");
                }
            }
        }
        return null;
    }

    // проверка существования клиента
    public boolean isClientExists(String passportNumber) throws SQLException {
        String sql = "SELECT COUNT(*) FROM clients WHERE passport_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, passportNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // проверка существования номера телефона
    public boolean isPhoneNumberExists(String phoneNumber) throws SQLException {
        String sql = "SELECT COUNT(*) FROM contact_info WHERE phone_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, phoneNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}