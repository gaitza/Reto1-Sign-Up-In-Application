/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import DataTransferObjects.User;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author Gaizka
 */
public class WelcomeController {

    private Stage stage;

    private User user;
    private static final Logger LOGGER = Logger.getLogger("WelcomeController.class");

    @FXML
    private Label labelUsu;
    @FXML
    private Label labelWelcome;
    @FXML
    private Button btnCerrarSesion;

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Method that initialises the window.
     *
     * @param root path of the window
     */
    public void initStage(Parent root) {

        Scene scene = new Scene(root);

        stage.setScene(scene);

        stage.setTitle("Welcome");
        stage.setResizable(false);

        // LabelUsu //
        labelUsu.setText(user.getEmail() + " has iniciado sesion correctamente!");
        // LabelWelcome //
        labelWelcome.setText("Muchas Gracias por elegir nuestra aplicacion");
        // btnContinue //
        btnCerrarSesion.setOnAction(this::SignUp);
        stage.setOnCloseRequest(this::handleExitAction);

        stage.show();
        LOGGER.info("Welcome window initialized");
    }

    private void handleExitAction(WindowEvent event) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to exit? This will close the app.");
        a.showAndWait();
        try {
            if (a.getResult().equals(ButtonType.CANCEL)) {
                event.consume();
            } else {
                Platform.exit();
            }
        } catch (Exception e) {
            String msg = "Error closing the app: " + e.getMessage();
            Alert alert = new Alert(Alert.AlertType.ERROR, msg);
            alert.show();
            LOGGER.log(Level.SEVERE, msg);
        }
    }

    private void SignUp(ActionEvent event) {
        try {
            Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to close the session? .");
            a.showAndWait();
            if (a.getResult().equals(ButtonType.CANCEL)) {
                event.consume();
            } else {
                stage.close();
                LOGGER.info("Welcome window closed");
                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/SignUp.fxml"));
                Parent root = (Parent) loader.load();

                SignUpController controller = ((SignUpController) loader.getController());

                controller.setStage(new Stage());

                controller.initStage(root);
                LOGGER.info("SignUp window opened");
            }
        } catch (IOException ex) {
            Logger.getLogger(SignInController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
