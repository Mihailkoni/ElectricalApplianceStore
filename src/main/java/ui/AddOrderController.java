package ui;

import dao.ClientDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;
import model.Appliance;
import model.Order;
import dao.OrderDAO;
import dao.ApplianceDAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AddOrderController {
    @FXML private TextField clientPassportNumberField;
    @FXML private TableView<Appliance> applianceTable;
    @FXML private TableColumn<Appliance, Integer> articleColumn;
    @FXML private TableColumn<Appliance, String> nameColumn;
    @FXML private TableColumn<Appliance, String> typeColumn;
    @FXML private TableColumn<Appliance, Double> priceColumn;
    @FXML private TableColumn<Appliance, Integer> stockColumn;
    @FXML private TableColumn<Appliance, Integer> quantityColumn;

    private ManagerTasksController managerTasksController;
    private String managerEmail;

    public void setManagerTasksController(ManagerTasksController managerTasksController) {
        this.managerTasksController = managerTasksController;
        this.managerEmail = managerTasksController.getManager().getEmail();  // Получаем email менеджера
    }

    @FXML
    public void initialize() {
        // Инициализация столбцов таблицы электроприборов
        articleColumn.setCellValueFactory(new PropertyValueFactory<>("articleNumber"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("typeName"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));

        // Настройка столбца для ввода количества
        quantityColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        quantityColumn.setOnEditCommit(event -> {
            Appliance appliance = event.getRowValue();
            appliance.setQuantity(event.getNewValue()); // Устанавливаем новое количество
        });

        // Включаем редактирование таблицы
        applianceTable.setEditable(true);

        // Загрузка данных в таблицу
        loadAppliances();
    }

    private void loadAppliances() {
        try {
            ApplianceDAO applianceDAO = new ApplianceDAO();
            List<Appliance> appliances = applianceDAO.getAllAppliances();
            applianceTable.getItems().setAll(appliances);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Ошибка при загрузке электроприборов: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddOrder() {
        try {
            // Проверка на пустые поля
            if (clientPassportNumberField.getText().isEmpty()) {
                showAlert("Ошибка", "Номер паспорта клиента должен быть заполнен.");
                return;
            }

            // Проверка на корректность номера паспорта (ровно 6 цифр)
            if (!clientPassportNumberField.getText().matches("\\d{6}")) {
                showAlert("Ошибка", "Номер паспорта должен состоять из 6 цифр.");
                return;
            }

            // Проверка на существование клиента с указанным номером паспорта
            String passportNumber = clientPassportNumberField.getText();
            ClientDAO clientDAO = new ClientDAO();
            if (!clientDAO.isClientExists(passportNumber)) {
                showAlert("Ошибка", "Клиент с таким номером паспорта не существует.");
                return;
            }

            // Проверка, что выбраны электроприборы и указано количество
            boolean hasItems = false;
            for (Appliance appliance : applianceTable.getItems()) {
                if (appliance.getQuantity() > 0) {
                    hasItems = true;
                    break;
                }
            }

            if (!hasItems) {
                showAlert("Ошибка", "Выберите хотя бы один электроприбор и укажите количество.");
                return;
            }

            // Проверка наличия товара на складе
            for (Appliance appliance : applianceTable.getItems()) {
                int quantity = appliance.getQuantity();
                if (quantity > 0 && quantity > appliance.getStockQuantity()) {
                    showAlert("Ошибка", "Недостаточно товара на складе: " + appliance.getName());
                    return; // Прерываем выполнение, если товара недостаточно
                }
            }

            // Создание заказа (только если все проверки пройдены)
            Order order = new Order(
                    0, // orderId будет сгенерирован базой данных
                    passportNumber,
                    managerEmail, // Используем почту менеджера
                    new java.sql.Date(System.currentTimeMillis()),
                    new ArrayList<>()
            );

            // Добавление заказа и деталей заказа
            OrderDAO orderDAO = new OrderDAO();
            int orderId = orderDAO.addOrder(order);

            // Добавление деталей заказа и обновление количества на складе
            for (Appliance appliance : applianceTable.getItems()) {
                int quantity = appliance.getQuantity();
                if (quantity > 0) {
                    // Добавляем детали заказа
                    orderDAO.addOrderDetail(orderId, appliance.getArticleNumber(), quantity);

                    // Обновляем количество на складе
                    appliance.setStockQuantity(appliance.getStockQuantity() - quantity);
                    ApplianceDAO applianceDAO = new ApplianceDAO();
                    applianceDAO.updateAppliance(appliance);
                }
            }

            // Обновление таблицы в ManagerTasksController
            if (managerTasksController != null) {
                managerTasksController.viewOrders(); // Обновляем таблицу заказов
            }

            // Закрытие окна
            clientPassportNumberField.getScene().getWindow().hide();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Ошибка при оформлении заказа: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Произошла непредвиденная ошибка: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}