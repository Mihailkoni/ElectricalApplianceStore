package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Client;
import model.Manager;
import dao.ClientDAO;
import dao.ManagerDAO;

import java.io.IOException;
import java.sql.SQLException;

public class LoginScreenController {

    @FXML
    private Label roleLabel;
    @FXML
    private TextField passportNumberField;
    @FXML
    private TextField emailField;

    private String role;

    @FXML
    public void handleLogin() {
        if (role.equals("Клиент")) {
            String passportNumber = passportNumberField.getText();

            try {
                ClientDAO clientDAO = new ClientDAO();
                Client client = clientDAO.getClientByPassport(passportNumber); // Используем новый метод
                if (client != null) {
                    try {
                        openClientTasks();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    showAlert("Клиент не найден");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Ошибка при авторизации клиента");
            }
        } else if (role.equals("Менеджер")) {
            String email = emailField.getText();

            try {
                ManagerDAO managerDAO = new ManagerDAO();
                Manager manager = managerDAO.getManagerByEmail(email);
                if (manager != null) {
                    try {
                        openManagerTasks();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    showAlert("Менеджер не найден");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Ошибка при авторизации менеджера");
            }
        }
    }

    @FXML
    public void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/RoleSelection.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Выбор роли");
            stage.show();

            // Закрываем текущее окно
            Stage currentStage = (Stage) roleLabel.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openClientTasks() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/ClientTasks.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Личный кабинет клиента");
        stage.show();
    }

    private void openManagerTasks() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/ManagerTasks.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Личный кабинет менеджера");
        stage.show();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}