package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/RoleSelection.fxml"));
        Scene scene = new Scene(loader.load(), 350, 200);
        stage.getIcons().add(new javafx.scene.image.Image("file:src/icons/store.png"));
        stage.setScene(scene);
        stage.setTitle("Магазин электроприборов");
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}