package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Appliance;
import model.Client;
import model.Manager;
import model.OrderView;
import dao.ApplianceDAO;
import dao.ClientDAO;
import dao.OrderDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ManagerTasksController {

    @FXML
    private TableView<OrderView> orderTable;
    @FXML
    private TableColumn<OrderView, Integer> orderIdColumn;
    @FXML
    private TableColumn<OrderView, String> clientPassportNumberColumn;
    @FXML
    private TableColumn<OrderView, String> clientFullNameColumn;
    @FXML
    private TableColumn<OrderView, String> applianceDetailsColumn;
    @FXML
    private TableColumn<OrderView, Double> totalPriceColumn;
    @FXML
    private TableColumn<OrderView, String> orderDateColumn;
    @FXML
    private TableColumn<OrderView, String> managerEmailColumn;

    @FXML
    private TableView<Appliance> applianceTable;
    @FXML
    private TableColumn<Appliance, Integer> articleColumn;
    @FXML
    private TableColumn<Appliance, String> nameColumn;
    @FXML
    private TableColumn<Appliance, String> typeColumn;
    @FXML
    private TableColumn<Appliance, Double> priceColumn;
    @FXML
    private TableColumn<Appliance, Integer> stockColumn;

    @FXML
    private TableView<Client> clientTable;
    @FXML
    private TableColumn<Client, String> passportNumberColumn;
    @FXML
    private TableColumn<Client, String> passportSeriesColumn;
    @FXML
    private TableColumn<Client, String> fullNameColumn;
    @FXML
    private TableColumn<Client, String> phoneColumn;

    @FXML
    private Label emailLabel;
    @FXML
    private Label loginLabel;

    private ObservableList<OrderView> orderList = FXCollections.observableArrayList();
    private ObservableList<Appliance> applianceList = FXCollections.observableArrayList();
    private ObservableList<Client> clientList = FXCollections.observableArrayList();

    private Manager manager;

    public void initialize() {
        // Инициализация столбцов для таблицы заказов
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        clientPassportNumberColumn.setCellValueFactory(new PropertyValueFactory<>("clientPassportNumber"));
        clientFullNameColumn.setCellValueFactory(new PropertyValueFactory<>("clientFullName"));
        applianceDetailsColumn.setCellValueFactory(new PropertyValueFactory<>("applianceDetails"));
        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        orderDateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        managerEmailColumn.setCellValueFactory(new PropertyValueFactory<>("managerEmail"));

        // Инициализация столбцов для таблицы электроприборов
        articleColumn.setCellValueFactory(new PropertyValueFactory<>("articleNumber"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("typeName"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));

        // Инициализация столбцов для таблицы клиентов
        passportNumberColumn.setCellValueFactory(new PropertyValueFactory<>("passportNumber"));
        passportSeriesColumn.setCellValueFactory(new PropertyValueFactory<>("passportSeries"));
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        // Привязка ObservableList к таблицам
        orderTable.setItems(orderList);
        applianceTable.setItems(applianceList);
        clientTable.setItems(clientList);

        // Загрузка данных
        loadOrders();
        loadAppliances();
        loadClients();
    }

    private void loadOrders() {
        try {
            OrderDAO orderDAO = new OrderDAO();
            List<OrderView> orders = orderDAO.getAllOrdersWithDetailsForManager();
            orderList.setAll(orders);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Ошибка при загрузке заказов");
        }
    }

    @FXML
    private void handleAddClient() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/AddClient.fxml"));
            Stage stage = new Stage();
            stage.getIcons().add(new javafx.scene.image.Image("file:src/icons/store.png"));
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Добавление клиента");

            AddClientController controller = loader.getController();
            controller.setManagerTasksController(this);  // Передаем ссылку на текущий контроллер

            stage.showAndWait();  // Ждем закрытия окна

            // После добавления обновляем таблицы
            refreshAllTables();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditClient() {
        Client selectedClient = clientTable.getSelectionModel().getSelectedItem();
        if (selectedClient != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/EditClient.fxml"));
                Stage stage = new Stage();
                stage.getIcons().add(new javafx.scene.image.Image("file:src/icons/store.png"));
                stage.setScene(new Scene(loader.load()));
                stage.setTitle("Редактирование клиента");

                EditClientController controller = loader.getController();
                controller.setClient(selectedClient);
                controller.setManagerTasksController(this);  // Передаем ссылку на текущий контроллер

                stage.showAndWait();  // Ждем закрытия окна

                // После редактирования обновляем таблицы
                refreshAllTables();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert("Выберите клиента для редактирования");
        }
    }

    @FXML
    private void handleEditOrder() {
        OrderView selectedOrder = orderTable.getSelectionModel().getSelectedItem();
        if (selectedOrder != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/EditOrder.fxml"));
                Stage stage = new Stage();
                stage.getIcons().add(new javafx.scene.image.Image("file:src/icons/store.png"));
                stage.setScene(new Scene(loader.load()));
                stage.setTitle("Редактирование заказа");

                EditOrderController controller = loader.getController();
                controller.setOrderId(selectedOrder.getOrderId());
                controller.setManagerTasksController(this);  // Передаем ссылку на текущий контроллер

                stage.showAndWait();  // Ждем закрытия окна

                // После редактирования обновляем таблицы
                refreshAllTables();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert("Выберите заказ для редактирования");
        }
    }

    private void loadAppliances() {
        try {
            ApplianceDAO applianceDAO = new ApplianceDAO();
            List<Appliance> appliances = applianceDAO.getAllAppliances();
            applianceList.setAll(appliances);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Ошибка при загрузке электроприборов");
        }
    }

    private void loadClients() {
        try {
            ClientDAO clientDAO = new ClientDAO();
            List<Client> clients = clientDAO.getAllClients();
            clientList.setAll(clients);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Ошибка при загрузке клиентов");
        }
    }

    @FXML
    private void handleAddAppliance() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/AddAppliance.fxml"));
            Stage stage = new Stage();
            stage.getIcons().add(new javafx.scene.image.Image("file:src/icons/store.png"));
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Добавление электроприбора");

            AddApplianceController controller = loader.getController();
            controller.setManagerTasksController(this);  // Передаем ссылку на текущий контроллер

            stage.showAndWait();  // Ждем закрытия окна

            // После добавления обновляем таблицы
            refreshAllTables();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditAppliance() {
        Appliance selectedAppliance = applianceTable.getSelectionModel().getSelectedItem();
        if (selectedAppliance != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/EditAppliance.fxml"));
                Stage stage = new Stage();
                stage.getIcons().add(new javafx.scene.image.Image("file:src/icons/store.png"));
                stage.setScene(new Scene(loader.load()));
                stage.setTitle("Редактирование электроприбора");

                EditApplianceController controller = loader.getController();
                controller.setAppliance(selectedAppliance);
                controller.setManagerTasksController(this);  // Передаем ссылку на текущий контроллер

                stage.showAndWait();  // Ждем закрытия окна

                // После редактирования обновляем таблицы
                refreshAllTables();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert("Выберите электроприбор для редактирования");
        }
    }

    @FXML
    private void handleAddOrder() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/AddOrder.fxml"));
            Stage stage = new Stage();
            stage.getIcons().add(new javafx.scene.image.Image("file:src/icons/store.png"));
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Оформление заказа");

            AddOrderController controller = loader.getController();
            controller.setManagerTasksController(this);  // Передаем ссылку на текущий контроллер

            stage.showAndWait();  // Ждем закрытия окна

            refreshAllTables(); // обновляем таблицы
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Метод для установки менеджера
    public void setManager(Manager manager) {
        this.manager = manager;
        updateManagerInfo();
    }

    private void updateManagerInfo() {
        if (manager != null) {
            emailLabel.setText("Email: " + manager.getEmail());
            loginLabel.setText("Логин: " + manager.getLogin());
        }
    }

    // Метод для обновления таблицы заказов
    public void viewOrders() {
        try {
            OrderDAO orderDAO = new OrderDAO();
            List<OrderView> orders = orderDAO.getAllOrdersWithDetailsForManager();
            orderList.setAll(orders);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Ошибка при загрузке заказов");
        }
    }

    public void viewClients() {
        try {
            ClientDAO clientDAO = new ClientDAO();
            List<Client> clients = clientDAO.getAllClients();
            clientList.setAll(clients);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Ошибка при загрузке клиентов");
        }
    }

    public void viewAppliances() {
        try {
            ApplianceDAO applianceDAO = new ApplianceDAO();
            List<Appliance> appliances = applianceDAO.getAllAppliances();
            applianceList.setAll(appliances);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Ошибка при загрузке электроприборов");
        }
    }

    @FXML
    public void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/RoleSelection.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load(), 350, 200));
            stage.getIcons().add(new javafx.scene.image.Image("file:src/icons/store.png"));
            stage.setTitle("Выбор роли");
            stage.show();

            // Закрываем текущее окно
            Stage currentStage = (Stage) orderTable.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Метод для получения менеджера
    public Manager getManager() {
        return manager;
    }

    private void refreshAllTables() {
        loadAppliances();
        loadClients();
        loadOrders();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}