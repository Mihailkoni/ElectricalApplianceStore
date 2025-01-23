package ui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;
import model.OrderDetailView;
import dao.OrderDAO;
import dao.ApplianceDAO;

import java.sql.SQLException;
import java.util.List;

public class EditOrderController {
    @FXML private TableView<OrderDetailView> orderDetailsTable;
    @FXML private TableColumn<OrderDetailView, Integer> articleColumn;
    @FXML private TableColumn<OrderDetailView, String> nameColumn;
    @FXML private TableColumn<OrderDetailView, Integer> quantityColumn;
    @FXML private TableColumn<OrderDetailView, Integer> stockColumn;

    private int orderId; // Номер заказа
    private ManagerTasksController managerTasksController; // Ссылка на контроллер менеджера

    // Метод для установки ссылки на ManagerTasksController
    public void setManagerTasksController(ManagerTasksController managerTasksController) {
        this.managerTasksController = managerTasksController;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
        loadOrderDetails();
    }

    @FXML
    public void initialize() {
        // Инициализация столбцов таблицы
        articleColumn.setCellValueFactory(new PropertyValueFactory<>("articleNumber"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("applianceName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));

        // Настройка столбца для ввода количества в заказе
        quantityColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        quantityColumn.setOnEditCommit(event -> {
            OrderDetailView orderDetail = event.getRowValue();
            orderDetail.setQuantity(event.getNewValue()); // Устанавливаем новое количество
        });

        // Включаем редактирование таблицы
        orderDetailsTable.setEditable(true);
    }

    private void loadOrderDetails() {
        try {
            OrderDAO orderDAO = new OrderDAO();
            List<OrderDetailView> orderDetails = orderDAO.getOrderDetailsByOrderId(orderId);
            orderDetailsTable.getItems().setAll(orderDetails);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Ошибка при загрузке деталей заказа: " + e.getMessage());
        }
    }

    @FXML
    private void handleSaveOrder() {
        try {
            OrderDAO orderDAO = new OrderDAO();
            ApplianceDAO applianceDAO = new ApplianceDAO();

            // Получаем текущие детали заказа из базы данных
            List<OrderDetailView> currentOrderDetails = orderDAO.getOrderDetailsByOrderId(orderId);

            // Проходим по всем строкам в таблице
            for (OrderDetailView orderDetail : orderDetailsTable.getItems()) {
                int newQuantity = orderDetail.getQuantity();
                int articleNumber = orderDetail.getArticleNumber();
                int stockQuantity = orderDetail.getStockQuantity();

                // Проверка на отрицательное количество
                if (newQuantity < 0) {
                    showAlert("Ошибка", "Количество товара не может быть отрицательным: " + orderDetail.getApplianceName());
                    return; // Прерываем выполнение
                }

                // Проверка на превышение количества на складе
                if (newQuantity > stockQuantity) {
                    showAlert("Ошибка", "Недостаточно товара на складе: " + orderDetail.getApplianceName());
                    return; // Прерываем выполнение
                }

                // Находим исходное количество товара в заказе до редактирования
                int oldQuantity = currentOrderDetails.stream()
                        .filter(detail -> detail.getArticleNumber() == articleNumber)
                        .findFirst()
                        .map(OrderDetailView::getQuantity)
                        .orElse(0);

                // Если количество товара в заказе равно 0, удаляем его из заказа
                if (newQuantity == 0) {
                    orderDAO.deleteOrderDetail(orderId, articleNumber);

                    // Увеличиваем количество на складе на удаленное количество
                    int updatedStockQuantity = stockQuantity + oldQuantity;
                    applianceDAO.updateApplianceStock(articleNumber, updatedStockQuantity);
                } else {
                    // Обновляем количество товара в заказе
                    orderDAO.updateOrderDetail(orderId, articleNumber, newQuantity);

                    // Обновляем количество на складе
                    int updatedStockQuantity = stockQuantity + (oldQuantity - newQuantity);
                    applianceDAO.updateApplianceStock(articleNumber, updatedStockQuantity);
                }
            }

            // Удаляем строки с количеством 0 из таблицы
            orderDetailsTable.getItems().removeIf(detail -> detail.getQuantity() == 0);

            // Проверяем, остались ли товары в заказе
            if (orderDetailsTable.getItems().isEmpty()) {
                // Если товаров нет, удаляем заказ
                orderDAO.deleteOrder(orderId);
                showAlert("Успех", "Заказ удалён, так как в нём не осталось товаров.");
            } else {
                showAlert("Успех", "Изменения сохранены успешно.");
            }

            // Обновляем таблицу заказов в ManagerTasksController
            if (managerTasksController != null) {
                managerTasksController.viewOrders(); // Обновляем таблицу
            }

            // Закрываем окно редактирования
            orderDetailsTable.getScene().getWindow().hide();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Ошибка при сохранении изменений: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}