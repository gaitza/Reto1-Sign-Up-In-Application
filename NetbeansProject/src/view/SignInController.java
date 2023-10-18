/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.io.IOException;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author bayro
 */
public class SignInController {

    private Stage stage;

    private static final Logger LOGGER = Logger.getLogger("SignInController.class");

    @FXML
    private TextField textFieldUser;
    @FXML
    private TextField textFieldPassword;
    @FXML
    private PasswordField password;
    @FXML
    private Hyperlink hyperLinkSignUp;
    @FXML
    private Button buttonSignIn;
    @FXML
    private Line lineUser;
    @FXML
    private Line linePassword;
    @FXML
    private Text labelInvalidUser;
    @FXML
    private Text labelInvalidPassword;
    @FXML
    private ToggleButton buttonShowHide;
    @FXML
    private ImageView imageViewButton;

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
    public void initStage(Parent root) {
        Scene scene = new Scene(root);

        stage.setScene(scene);

        stage.setTitle("SignIn");
        stage.setResizable(false);
        // HyperLnk //
        //Accion de dirigir a la ventana de SignUp
        hyperLinkSignUp.setOnAction(event -> SignUp());

        // USERNAME TEXT FIELD //
        // Comprobar si el texto cambia
        textFieldUser.setOnKeyTyped(this::textChanged);
        // Comprobacion del cambio de foco en el campo de texto
        textFieldUser.focusedProperty().addListener(this::focusedChange);
        textFieldUser.setOnKeyTyped(this::updateLabel);

        // PASSWORD FIELD //
        // Comprobar si el texto cambia
        password.setOnKeyReleased(this::copyPassword);
        password.setOnKeyTyped(this::textChanged);
        // Comprobacion del cambio de foco en el campo de contraseña
        password.focusedProperty().addListener(this::focusedChange);
        password.setOnKeyTyped(this::updateLabel);

        // PASSWORD TEXT FIELD //
        // Comprobacion del cambio de foco en el campo de texto
        textFieldPassword.focusedProperty().addListener(this::focusedChange);
        // Comprobar si el texto cambia
        textFieldPassword.setOnKeyTyped(this::textChanged);
        textFieldPassword.setOnKeyReleased(this::copyPassword);

        buttonShowHide.setOnAction(this::handleShowHide);

        stage.show();
        LOGGER.info("SingIn window initialized");
    }

    private void SignUp() {
        try {
            stage.close();
            LOGGER.info("SignIn window closed");
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/SignUp.fxml"));
            Parent root = (Parent) loader.load();

            SignUpController controller = ((SignUpController) loader.getController());

            controller.setStage(new Stage());

            controller.initStage(root);
            LOGGER.info("SignUp window opened");
        } catch (IOException ex) {

        }
    }

    /**
     * Check what state (pressed/not pressed) the password is in.
     *
     * @param event an ActionEvent.ACTION event type for when the button is
     * pressed
     */
    private void handleShowHide(ActionEvent event) {
        if (buttonShowHide.isSelected()) {
            imageViewButton.setImage(new Image(getClass().getResourceAsStream("/resources/iconEye2.png")));
            password.setVisible(false);
            textFieldPassword.setVisible(true);
        } else {
            // Si no está presionado se muestra un passwordField y la imagen de imageShowHide es showIcon.
            imageViewButton.setImage(new Image(getClass().getResourceAsStream("/resources/iconEye.png")));
            password.setVisible(true);
            textFieldPassword.setVisible(false);
        }
    }

    private void updateLabel(KeyEvent event) {
        try {

            if (event.getSource() instanceof PasswordField) {
                PasswordField passw = (PasswordField) event.getSource();
                if (passw.getText().length() <= 8) {
                    throw new IOException("Password must be al least 8 characters long");

                }
                linePassword.setStroke(Color.GRAY);
                labelInvalidPassword.setText("");
            }
            if (event.getSource() instanceof TextField) {
                TextField textField = (TextField) event.getSource();
                if (!textField.getText().isEmpty() && !textField.getText().contains(" ")) {
                    throw new IOException();
                }
                if (textField.equals(textFieldPassword) && textField.getText().length() <= 8) {
                    throw new IOException("Password must be al least 8 characters long");
                }
                lineUser.setStroke(Color.GRAY);
                labelInvalidUser.setText("");
            }

        } catch (IOException ex) {
            if (ex.getMessage() != null) {
                linePassword.setStroke(Color.RED);
                LOGGER.info(ex.getMessage());
                labelInvalidPassword.setText(ex.getMessage());
            }
        }
    }

    private void copyPassword(KeyEvent event) {
        if (password.isVisible()) {
            // Cuando se escribe un carácter en el passwordField se copia en el textFieldPassword.
            textFieldPassword.setText(password.getText());
        } else if (textFieldPassword.isVisible()) {
            // Cuando se escribe un carácter en el textFieldPassword se copia en el passwordField.
            password.setText(textFieldPassword.getText());
        }
    }

    private void textChanged(KeyEvent event) {
        if (((TextField) event.getSource()).getText().length() >= 20) {
            event.consume();
            ((TextField) event.getSource()).setText(((TextField) event.getSource()).getText().substring(0, 20));
        }
    }

    private void focusedChange(ObservableValue observable, Boolean oldValue, Boolean newValue) {
        if (oldValue) {
            if (!textFieldUser.isFocused()) {
                try {
                    if (textFieldUser.getText().isEmpty()) {
                        throw new IOException("Enter a username");
                    }
                    if (textFieldUser.getText().contains(" ")) {
                        throw new IOException("Username can't contain an empty space");
                    }
                    lineUser.setStroke(Color.GRAY);
                    labelInvalidUser.setText("");
                } catch (IOException ex) {
                    lineUser.setStroke(Color.RED);
                    LOGGER.info(ex.getMessage());
                    labelInvalidUser.setText(ex.getMessage());
                }
            }
            if (!password.isFocused() && !textFieldPassword.isFocused()) {
                try {
                    if (password.getText().isEmpty()) {
                        throw new IOException("Enter a password");
                    }
                    if (password.getText().contains(" ")) {
                        throw new IOException("Password can't contain an empty space");
                    }
                    if (password.getText().length() < 8) {
                        throw new IOException("Password must be al least 8 characters long");
                    }
                    linePassword.setStroke(Color.GRAY);
                    labelInvalidPassword.setText("");
                } catch (IOException ex) {
                    linePassword.setStroke(Color.RED);
                    LOGGER.info(ex.getMessage());
                    labelInvalidPassword.setText(ex.getMessage());
                }
            }

        }
    }

}
