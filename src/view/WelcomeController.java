/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.io.IOException;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 *
 * @author Gaizka
 */
public class WelcomeController {
    
    private Stage stage;
    
    private static final Logger LOGGER = Logger.getLogger("WelcomeController.class");
    
    @FXML
    private Label labelUsu;
    @FXML
    private Label labelWelcome;
    @FXML
    private Button btnContinue;
    
    
    
    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    /**
     * Method that initialises the window.
     *
     * @param root path of the window
     */
    public void initStage (Parent root) {
        
        Scene scene = new Scene(root);

        stage.setScene(scene);

        stage.setTitle("Welcome");
        stage.setResizable(false);
        
        // LabelUsu //
        labelUsu.setText("¡@Usuario has iniciado sesion correctamente!");
        // LabelWelcome //
        labelWelcome.setText("Muchas Gracias por elegir nuestra aplicacion");
        // btnContinue //
        btnContinue.setOnAction(event -> SignUp());
        
        stage.show();
        LOGGER.info("Welcome window initialized");
    }

    private void SignUp() {
        try {
            stage.close();
            LOGGER.info("Welcome window closed");
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/SignUp.fxml"));
            Parent root = (Parent) loader.load();

            SignUpController controller = ((SignUpController) loader.getController());

            controller.setStage(new Stage());

            controller.initStage(root);
            LOGGER.info("SignUp window opened");
        } catch (IOException ex) {

        }
    }
    
}
