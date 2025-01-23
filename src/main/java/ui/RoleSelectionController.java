package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class RoleSelectionController {

    @FXML
    public void handleClient() {
        openLoginScreen("/ui/ClientLogin.fxml", "Авторизация клиента");
    }

    @FXML
    public void handleManager() {
        openLoginScreen("/ui/ManagerLogin.fxml", "Авторизация менеджера");
    }

    private void openLoginScreen(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Stage stage = new Stage();
            stage.getIcons().add(new javafx.scene.image.Image("file:src/icons/store.png"));
            stage.setScene(new Scene(loader.load()));
            stage.setTitle(title);
            stage.show();

            // Закрываем текущее окно
            Stage currentStage = (Stage) Stage.getWindows().stream()
                    .filter(Window::isShowing)
                    .findFirst()
                    .orElse(null);
            if (currentStage != null) {
                currentStage.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}