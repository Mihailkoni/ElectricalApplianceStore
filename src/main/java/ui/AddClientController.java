package ui;

import dao.ClientDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import model.Client;

import java.sql.SQLException;

public class AddClientController {
    @FXML private TextField passportSeriesField;
    @FXML private TextField passportNumberField;
    @FXML private TextField fullNameField;
    @FXML private TextField phoneNumberField;

    private ManagerTasksController managerTasksController;

    public void setManagerTasksController(ManagerTasksController managerTasksController) {
        this.managerTasksController = managerTasksController;
    }

    @FXML
    private void handleAddClient() {
        try {
            // Проверка на пустые поля
            if (passportSeriesField.getText().isEmpty() || passportNumberField.getText().isEmpty() ||
                    fullNameField.getText().isEmpty() || phoneNumberField.getText().isEmpty()) {
                showAlert("Ошибка", "Все поля должны быть заполнены.");
                return;
            }

            // Проверка на корректность серии паспорта (ровно 4 цифры)
            if (!passportSeriesField.getText().matches("\\d{4}")) {
                showAlert("Ошибка", "Серия паспорта должна состоять из 4 цифр.");
                return;
            }

            // Проверка на корректность номера паспорта (ровно 6 цифр)
            if (!passportNumberField.getText().matches("\\d{6}")) {
                showAlert("Ошибка", "Номер паспорта должен состоять из 6 цифр.");
                return;
            }

            // Проверка на корректность номера телефона (ровно 11 цифр)
            if (!phoneNumberField.getText().matches("\\d{11}")) {
                showAlert("Ошибка", "Номер телефона должен состоять из 11 цифр.");
                return;
            }

            // Проверка на длину ФИО (не более 100 символов)
            String fullName = fullNameField.getText();
            if (fullName.length() > 100) {
                showAlert("Ошибка", "ФИО не должно превышать 100 символов.");
                return;
            }

            // Проверка на существование клиента с таким номером паспорта
            ClientDAO clientDAO = new ClientDAO();
            if (clientDAO.isClientExists(passportNumberField.getText())) {
                showAlert("Ошибка", "Клиент с таким номером паспорта уже существует.");
                return;
            }

            // Проверка на существование клиента с таким номером телефона
            if (clientDAO.isPhoneNumberExists(phoneNumberField.getText())) {
                showAlert("Ошибка", "Клиент с таким номером телефона уже существует.");
                return;
            }

            // Создание объекта Client
            Client client = new Client(
                    passportNumberField.getText(),
                    passportSeriesField.getText(),
                    fullName
            );

            // Добавление клиента в базу данных
            clientDAO.addClient(client, phoneNumberField.getText());

            // Обновление таблицы в ManagerTasksController
            if (managerTasksController != null) {
                managerTasksController.viewClients();
            }

            // Закрытие окна
            passportNumberField.getScene().getWindow().hide();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Ошибка при добавлении клиента в базу данных: " + e.getMessage());
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