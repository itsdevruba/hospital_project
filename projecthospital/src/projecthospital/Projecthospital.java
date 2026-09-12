
package projecthospital;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Projecthospital extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the Login FXML screen
            Parent root = FXMLLoader.load(getClass().getResource("/resources/fxml/Login.fxml"));

            // Create the scene
            Scene scene = new Scene(root);

            // Configure the primary stage
            primaryStage.setTitle("Hospital Appointment System - Login");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();

        } catch (Exception e) {
            System.err.println("Error loading application: " + e.getMessage());
            e.printStackTrace();
        }
    }

    
    public static void main(String[] args) {
        launch(args);
    }
}
