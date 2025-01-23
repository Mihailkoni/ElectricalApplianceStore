package ui;

import dao.ClientDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import model.Client;

import java.sql.SQLException;

public class EditClientController {
    @FXML private TextField passportSeriesField;
    @FXML private TextField passportNumberField;
    @FXML private TextField fullNameField;
    @FXML private TextField phoneNumberField;

    private Client client;
    private ManagerTasksController managerTasksController;

    public void setManagerTasksController(ManagerTasksController managerTasksController) {
        this.managerTasksController = managerTasksController;
    }

    public void setClient(Client client) {
        this.client = client;
        passportSeriesField.setText(client.getPassportSeries());
        passportNumberField.setText(client.getPassportNumber());
        fullNameField.setText(client.getFullName());

        // Получаем номер телефона из базы данных
        try {
            ClientDAO clientDAO = new ClientDAO();
            String phoneNumber = clientDAO.getPhoneNumberByClientId(clientDAO.getClientIdByPassport(client.getPassportNumber()));
            phoneNumberField.setText(phoneNumber);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить номер телефона: " + e.getMessage());
        }
    }

    @FXML
    private void handleSaveClient() {
        try {
            // Проверка на пустые поля
            if (fullNameField.getText().isEmpty() || phoneNumberField.getText().isEmpty()) {
                showAlert("Ошибка", "Поля 'ФИО' и 'Телефон' должны быть заполнены.");
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

            // Проверка на существование телефона у другого клиента
            String newPhoneNumber = phoneNumberField.getText();
            ClientDAO clientDAO = new ClientDAO();
            int clientId = clientDAO.getClientIdByPassport(client.getPassportNumber());
            String currentPhoneNumber = clientDAO.getPhoneNumberByClientId(clientId);

            if (clientDAO.isPhoneNumberExists(newPhoneNumber) && !newPhoneNumber.equals(currentPhoneNumber)) {
                showAlert("Ошибка", "Клиент с таким номером телефона уже существует.");
                return;
            }

            // Обновляем данные клиента
            client.setFullName(fullName);

            // Сохраняем изменения в базе данных
            clientDAO.updateClient(client, newPhoneNumber);

            // Обновляем таблицу в ManagerTasksController
            if (managerTasksController != null) {
                managerTasksController.viewClients(); // Вызов метода для обновления таблицы
            }

            // Закрываем окно редактирования
            passportNumberField.getScene().getWindow().hide();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Ошибка при обновлении данных клиента: " + e.getMessage());
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