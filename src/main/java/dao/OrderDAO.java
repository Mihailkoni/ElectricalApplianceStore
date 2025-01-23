package dao;

import database.DatabaseConnection;
import model.Order;
import model.OrderDetailView;
import model.OrderView;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    // получение заказов клиента по номеру паспорта
    public List<OrderView> getOrdersByClientPassport(String passportNumber) throws SQLException {
        List<OrderView> orders = new ArrayList<>();
        String sql = "SELECT o.order_id, " +
                "STRING_AGG(CONCAT(a.article_number,' - ', ' (', a.name, ', ', od.quantity, ' шт., ', a.price, ' руб.)'), ', ') AS appliance_details, " +
                "SUM(a.price * od.quantity) AS total_price, " +
                "o.order_date, " +
                "o.manager_email " +
                "FROM orders o " +
                "JOIN order_details od ON o.order_id = od.order_id " +
                "JOIN appliances a ON od.appliance_article_number = a.article_number " +
                "WHERE o.client_passport_number = ? " +
                "GROUP BY o.order_id, o.order_date, o.manager_email";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, passportNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int orderId = rs.getInt("order_id");
                    String applianceDetails = rs.getString("appliance_details");
                    double totalPrice = rs.getDouble("total_price");
                    Date orderDate = rs.getDate("order_date");
                    String managerEmail = rs.getString("manager_email");

                    OrderView orderView = new OrderView(orderId, applianceDetails, totalPrice, orderDate, managerEmail);
                    orders.add(orderView);
                }
            }
        }
        return orders;
    }

    // добавление заказа
    public int addOrder(Order order) throws SQLException {
        String sql = "INSERT INTO orders (client_passport_number, manager_email, order_date) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, order.getClientPassportNumber());
            stmt.setString(2, order.getManagerEmail());
            stmt.setDate(3, order.getOrderDate());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }
        throw new SQLException("Не удалось создать заказ.");
    }

    // добавление деталей заказа
    public void addOrderDetail(int orderId, int applianceArticleNumber, int quantity) throws SQLException {
        String sql = "INSERT INTO order_details (order_id, appliance_article_number, quantity) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            stmt.setInt(2, applianceArticleNumber);
            stmt.setInt(3, quantity);
            stmt.executeUpdate();
        }
    }

    // получение всех заказов с деталями для менеджера
    public List<OrderView> getAllOrdersWithDetailsForManager() throws SQLException {
        List<OrderView> orders = new ArrayList<>();
        String sql = "SELECT " +
                "o.order_id, " +
                "o.client_passport_number, " +
                "c.full_name AS client_full_name, " +
                "STRING_AGG(CONCAT(a.article_number, ' - ', a.name, ' (', od.quantity, ' шт., ', a.price, ' руб.)'), ', ') AS appliance_details, " +
                "SUM(a.price * od.quantity) AS total_price, " +
                "o.order_date, " +
                "o.manager_email " +
                "FROM orders o " +
                "JOIN order_details od ON o.order_id = od.order_id " +
                "JOIN appliances a ON od.appliance_article_number = a.article_number " +
                "JOIN clients c ON o.client_passport_number = c.passport_number " +
                "GROUP BY o.order_id, o.client_passport_number, c.full_name, o.order_date, o.manager_email";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                String clientPassportNumber = rs.getString("client_passport_number");
                String clientFullName = rs.getString("client_full_name");  // Получаем имя клиента
                String applianceDetails = rs.getString("appliance_details");
                double totalPrice = rs.getDouble("total_price");
                Date orderDate = rs.getDate("order_date");
                String managerEmail = rs.getString("manager_email");

                OrderView orderView = new OrderView(orderId, clientPassportNumber, clientFullName, applianceDetails, totalPrice, orderDate, managerEmail);
                orders.add(orderView);
            }
        }
        return orders;
    }

    // получение деталей заказа по номеру заказа
    public List<OrderDetailView> getOrderDetailsByOrderId(int orderId) throws SQLException {
        List<OrderDetailView> orderDetails = new ArrayList<>();
        String sql = "SELECT a.article_number, a.name, od.quantity, a.stock_quantity " +
                "FROM order_details od " +
                "JOIN appliances a ON od.appliance_article_number = a.article_number " +
                "WHERE od.order_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int articleNumber = rs.getInt("article_number");
                    String applianceName = rs.getString("name");
                    int quantity = rs.getInt("quantity");
                    int stockQuantity = rs.getInt("stock_quantity");
                    orderDetails.add(new OrderDetailView(articleNumber, applianceName, quantity, stockQuantity));
                }
            }
        }
        return orderDetails;
    }

    // удаление деталей заказа
    public void deleteOrderDetail(int orderId, int articleNumber) throws SQLException {
        String sql = "DELETE FROM order_details WHERE order_id = ? AND appliance_article_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            stmt.setInt(2, articleNumber);
            stmt.executeUpdate();
        }
    }

    // обновление количества товара в заказе
    public void updateOrderDetail(int orderId, int articleNumber, int quantity) throws SQLException {
        String sql = "UPDATE order_details SET quantity = ? WHERE order_id = ? AND appliance_article_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quantity);
            stmt.setInt(2, orderId);
            stmt.setInt(3, articleNumber);
            stmt.executeUpdate();
        }
    }

    // удаление заказа
    public void deleteOrder(int orderId) throws SQLException {
        String sql = "DELETE FROM orders WHERE order_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            stmt.executeUpdate();
        }
    }
}