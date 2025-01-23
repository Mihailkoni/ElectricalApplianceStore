package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Client;
import dao.ClientDAO;

import java.io.IOException;
import java.sql.SQLException;

public class ClientLoginController {

    @FXML
    private TextField passportNumberField;

    @FXML
    public void handleLogin() {
        String passportNumber = passportNumberField.getText();

        try {
            ClientDAO clientDAO = new ClientDAO();
            Client client = clientDAO.getClientByPassport(passportNumber);
            if (client != null) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/ClientTasks.fxml"));
                    Stage stage = new Stage();
                    stage.getIcons().add(new javafx.scene.image.Image("file:src/icons/store.png"));
                    stage.setScene(new Scene(loader.load()));
                    stage.setTitle("Личный кабинет клиента");
                    stage.show();

                    // Передаем данные клиента в контроллер
                    ClientTasksController clientTasksController = loader.getController();
                    clientTasksController.setClient(client);

                    // Закрываем текущее окно
                    Stage currentStage = (Stage) passportNumberField.getScene().getWindow();
                    currentStage.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                showAlert("Клиент не найден");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Ошибка при авторизации клиента");
        }
    }

    @FXML
    public void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/RoleSelection.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load(),350,200));
            stage.getIcons().add(new javafx.scene.image.Image("file:src/icons/store.png"));
            stage.setTitle("Выбор роли");
            stage.show();

            // Закрываем текущее окно
            Stage currentStage = (Stage) passportNumberField.getScene().getWindow();
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