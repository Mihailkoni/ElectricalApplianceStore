package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Appliance;
import model.Client;
import model.OrderView;
import dao.ApplianceDAO;
import dao.ClientDAO;
import dao.OrderDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

public class ClientTasksController {

    @FXML
    private TextField passportNumberField;
    @FXML
    private TextField passportSeriesField;
    @FXML
    private TextField fullNameField;
    @FXML
    private TextField phoneNumberField;

    @FXML
    private TableView<Appliance> applianceTable;
    @FXML
    private TableColumn<Appliance, String> articleColumn;
    @FXML
    private TableColumn<Appliance, String> nameColumn;
    @FXML
    private TableColumn<Appliance, String> typeColumn;
    @FXML
    private TableColumn<Appliance, Double> priceColumn;
    @FXML
    private TableColumn<Appliance, Integer> stockColumn;

    @FXML
    private TableView<OrderView> orderTable;
    @FXML
    private TableColumn<OrderView, Integer> orderIdColumn;
    @FXML
    private TableColumn<OrderView, String> applianceDetailsColumn;
    @FXML
    private TableColumn<OrderView, Double> totalPriceColumn;
    @FXML
    private TableColumn<OrderView, String> orderDateColumn;
    @FXML
    private TableColumn<OrderView, String> managerEmailColumn;

    private Client client;

    public void setClient(Client client) {
        this.client = client;
        passportSeriesField.setText(client.getPassportSeries());
        passportNumberField.setText(client.getPassportNumber());
        fullNameField.setText(client.getFullName());

        // Получаем номер телефона из базы данных
        try {
            ClientDAO clientDAO = new ClientDAO();
            int clientId = clientDAO.getClientIdByPassport(client.getPassportNumber());
            String phoneNumber = clientDAO.getPhoneNumberByClientId(clientId);
            phoneNumberField.setText(phoneNumber);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Ошибка, Не удалось загрузить номер телефона: " + e.getMessage());
        }

        // Загружаем данные при открытии личного кабинета
        loadAppliances();
        loadOrders();
    }

    @FXML
    public void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/RoleSelection.fxml"));
            Stage stage = new Stage();
            stage.getIcons().add(new javafx.scene.image.Image("file:src/icons/store.png"));
            stage.setScene(new Scene(loader.load(), 350, 200));
            stage.setTitle("Выбор роли");
            stage.show();

            // Закрываем текущее окно
            Stage currentStage = (Stage) passportNumberField.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadAppliances() {
        try {
            ApplianceDAO applianceDAO = new ApplianceDAO();
            List<Appliance> appliances = applianceDAO.getAllAppliances();
            appliances.sort(Comparator.comparingInt(Appliance::getArticleNumber));
            applianceTable.getItems().setAll(appliances);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Ошибка при загрузке каталога электроприборов");
        }
    }

    private void loadOrders() {
        try {
            OrderDAO orderDAO = new OrderDAO();
            List<OrderView> orders = orderDAO.getOrdersByClientPassport(client.getPassportNumber());
            orders.sort(Comparator.comparingInt(OrderView::getOrderId));
            orderTable.getItems().setAll(orders);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Ошибка при загрузке заказов");
        }
    }

    public void initialize() {
        // Инициализация столбцов для таблицы электроприборов
        articleColumn.setCellValueFactory(new PropertyValueFactory<>("articleNumber"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("typeName"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));

        // Инициализация столбцов для таблицы заказов
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        applianceDetailsColumn.setCellValueFactory(new PropertyValueFactory<>("applianceDetails"));
        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        orderDateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        managerEmailColumn.setCellValueFactory(new PropertyValueFactory<>("managerEmail"));
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}