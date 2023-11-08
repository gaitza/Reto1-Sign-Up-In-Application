
package main;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import view.SignInController;

/**
 * Main de la app Cliente.
 * @author bayro
 */
public class AppFX extends javafx.application.Application {
    
      /**
     * Este metodo inicializa la aplicacion abriendo al ventana SignIn
     * @param stage stage initialising 
     * @throws java.io.IOException 
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/SignIn.fxml"));
        Parent root = (Parent) loader.load();
        
        SignInController controller = ((SignInController) loader.getController());
        
        controller.setStage(stage);
        
        controller.initStage(root);
    }

    /**
     * @param args 
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
