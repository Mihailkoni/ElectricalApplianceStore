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

public class AddApplianceController {
    @FXML private TextField nameField;
    @FXML private ComboBox<ApplianceType> typeComboBox;
    @FXML private TextField newTypeField;
    @FXML private TextField priceField;
    @FXML private TextField stockQuantityField;

    private ManagerTasksController managerTasksController;


    public void setManagerTasksController(ManagerTasksController managerTasksController) {
        this.managerTasksController = managerTasksController;
        loadApplianceTypes();
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
    private void handleAddAppliance() {
        try {
            // Проверка на пустые поля
            if (nameField.getText().isEmpty() || priceField.getText().isEmpty() || stockQuantityField.getText().isEmpty()) {
                showAlert("Ошибка", "Все поля должны быть заполнены.");
                return;
            }

            // Проверка на длину названия (не более 100 символов)
            String name = nameField.getText();
            if (name.length() > 100) {
                showAlert("Ошибка", "Название не может превышать 100 символов.");
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

            // Получаем выбранный тип или создаем новый
            String newTypeName = newTypeField.getText().trim();
            int typeId;
            String typeName;

            if (!newTypeName.isEmpty()) {
                // Проверка на длину нового типа (не более 50 символов)
                if (newTypeName.length() > 50) {
                    showAlert("Ошибка", "Название типа не может превышать 50 символов.");
                    return;
                }

                // Если введен новый тип, игнорируем выбор из ComboBox
                ApplianceTypeDAO typeDAO = new ApplianceTypeDAO();
                ApplianceType newType = new ApplianceType(0, newTypeName);
                typeDAO.addApplianceType(newType); // Добавляем новый тип в базу данных
                typeId = newType.getTypeId(); // Получаем ID нового типа
                typeName = newType.getTypeName();
            } else {
                // Если новый тип не введен, используем выбранный из ComboBox
                ApplianceType selectedType = typeComboBox.getValue();
                if (selectedType == null) {
                    showAlert("Ошибка", "Выберите тип или введите новый.");
                    return;
                }
                typeId = selectedType.getTypeId();
                typeName = selectedType.getTypeName();
            }

            // Создание объекта Appliance
            Appliance appliance = new Appliance(
                    0, // Артикул будет сгенерирован в базе данных
                    nameField.getText(),
                    typeId,
                    typeName,
                    price,
                    stockQuantity
            );

            // Добавление в базу данных
            ApplianceDAO applianceDAO = new ApplianceDAO();
            applianceDAO.addAppliance(appliance);

            // Обновление таблицы в ManagerTasksController
            if (managerTasksController != null) {
                managerTasksController.viewAppliances();
            }

            // Закрытие окна
            nameField.getScene().getWindow().hide();
            // Очистка поля для нового типа
            newTypeField.clear();
        } catch (NumberFormatException e) {
            showAlert("Ошибка", "Некорректный формат числа.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Ошибка при добавлении электроприбора в базу данных: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Произошла непредвиденная ошибка: " + e.getMessage());
        }
    }

    @FXML
    private void initialize() {
        // Очистка ComboBox при вводе нового типа
        newTypeField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                typeComboBox.getSelectionModel().clearSelection();
            }
        });

        // Очистка поля для нового типа при выборе из ComboBox
        typeComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                newTypeField.clear();
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}