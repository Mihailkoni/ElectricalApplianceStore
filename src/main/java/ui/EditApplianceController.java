package ui;

import dao.ApplianceDAO;
import dao.ApplianceTypeDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import model.Appliance;
import model.ApplianceType;

import java.sql.SQLException;
import java.util.List;

public class EditApplianceController {
    @FXML private TextField nameField;
    @FXML private ComboBox<ApplianceType> typeComboBox;
    @FXML private TextField priceField;
    @FXML private TextField stockQuantityField;

    private Appliance appliance;
    private ManagerTasksController managerTasksController;

    public void setManagerTasksController(ManagerTasksController managerTasksController) {
        this.managerTasksController = managerTasksController;
        loadApplianceTypes();
    }

    public void setAppliance(Appliance appliance) {
        this.appliance = appliance;
        nameField.setText(appliance.getName());
        priceField.setText(String.valueOf(appliance.getPrice()));
        stockQuantityField.setText(String.valueOf(appliance.getStockQuantity()));

        // Устанавливаем выбранный тип электроприбора
        try {
            ApplianceTypeDAO typeDAO = new ApplianceTypeDAO();
            ApplianceType selectedType = typeDAO.getApplianceTypeById(appliance.getTypeId());
            typeComboBox.getSelectionModel().select(selectedType);
            typeComboBox.setDisable(true); // Запрещаем изменение типа
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Ошибка при загрузке типа электроприбора: " + e.getMessage());
        }
    }

    private void loadApplianceTypes() {
        try {
            ApplianceTypeDAO typeDAO = new ApplianceTypeDAO();
            List<ApplianceType> types = typeDAO.getAllApplianceTypes();
            typeComboBox.getItems().setAll(types);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Ошибка при загрузке типов электроприборов: " + e.getMessage());
        }
    }

    @FXML
    private void handleSaveAppliance() {
        try {
            // Проверка на пустые поля
            if (priceField.getText().isEmpty() || stockQuantityField.getText().isEmpty() || typeComboBox.getValue() == null) {
                showAlert("Ошибка", "Поля 'Цена', 'Количество' и 'Тип' должны быть заполнены.");
                return;
            }

            // Проверка на корректность цены (должна быть положительной)
            double price = Double.parseDouble(priceField.getText());
            if (price <= 0) {
                showAlert("Ошибка", "Цена должна быть положительной.");
                return;
            }

            // Проверка на слишком большую цену
            double maxPrice = 1_000_000.0; // Максимальная цена
            if (price > maxPrice) {
                showAlert("Ошибка", "Цена не может превышать " + maxPrice + " рублей.");
                return;
            }

            // Проверка на корректность количества (должно быть неотрицательным)
            int stockQuantity = Integer.parseInt(stockQuantityField.getText());
            if (stockQuantity < 0) {
                showAlert("Ошибка", "Количество не может быть отрицательным.");
                return;
            }

            // Обновляем данные электроприбора
            appliance.setPrice(price);
            appliance.setStockQuantity(stockQuantity);
            appliance.setTypeId(typeComboBox.getValue().getTypeId()); // Устанавливаем ID типа
            appliance.setTypeName(typeComboBox.getValue().getTypeName()); // Устанавливаем название типа

            // Сохраняем изменения в базе данных
            ApplianceDAO applianceDAO = new ApplianceDAO();
            applianceDAO.updateAppliance(appliance);

            // Обновляем таблицу в ManagerTasksController
            if (managerTasksController != null) {
                managerTasksController.viewAppliances();
            }

            // Закрываем окно редактирования
            nameField.getScene().getWindow().hide();
        } catch (NumberFormatException e) {
            showAlert("Ошибка", "Некорректный формат числа.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Ошибка при обновлении данных электроприбора: " + e.getMessage());
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