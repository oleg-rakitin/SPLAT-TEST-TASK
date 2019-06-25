package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        //Parent root = FXMLLoader.load(getClass().getResource("../resources/fxml/sample.fxml"));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../resources/fxml/sample.fxml"));
        Parent root = (Parent)loader.load();
        MainMenu controller = (MainMenu)loader.getController();
        controller.setStage(primaryStage);
        primaryStage.setTitle("SPLAT App");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    private Stage myStage;
    public void setStage(Stage stage) {
        myStage = stage;
    }
    public static void main(String[] args) {
        launch(args);
    }
}
