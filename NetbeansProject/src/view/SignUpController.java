/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.util.logging.Logger;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author bayro
 */
public class SignUpController {
    private Stage stage;
    
     private static final Logger LOGGER = Logger.getLogger("SignUpController.class");
     public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
     public void initStage(Parent root) {
        Scene scene = new Scene(root);

        stage.setScene(scene);

        stage.setTitle("SignUp");
        stage.setResizable(false);
        
       
                
        stage.show();
        LOGGER.info("SingUp window initialized");
    }
}
