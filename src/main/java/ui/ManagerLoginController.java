package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Manager;
import dao.ManagerDAO;

import java.io.IOException;
import java.sql.SQLException;

public class ManagerLoginController {

    @FXML
    private TextField emailField;

    @FXML
    public void handleLogin() {
        String email = emailField.getText();

        try {
            ManagerDAO managerDAO = new ManagerDAO();
            Manager manager = managerDAO.getManagerByEmail(email);
            if (manager != null) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/ManagerTasks.fxml"));
                    Stage stage = new Stage();
                    stage.getIcons().add(new javafx.scene.image.Image("file:src/icons/store.png"));
                    stage.setScene(new Scene(loader.load()));
                    stage.setTitle("Личный кабинет менеджера");
                    stage.show();

                    // Передаем данные менеджера в контроллер
                    ManagerTasksController managerTasksController = loader.getController();
                    managerTasksController.setManager(manager);

                    // Закрываем текущее окно
                    Stage currentStage = (Stage) emailField.getScene().getWindow();
                    currentStage.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                showAlert("Менеджер не найден");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Ошибка при авторизации менеджера");
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
            Stage currentStage = (Stage) emailField.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}