/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import exceptions.InvalidEmailValueException;
import exceptions.InvalidPasswordException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import javafx.scene.paint.Paint;
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
    private TextField textFieldUser, textFieldPassword;
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

    private Color customColorGreen = Color.web("#14FF0D");
    
    private boolean userException = false;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

     private final ValidationHelper helper = new ValidationHelper();
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
        //Action of directing to the SignUp window
        hyperLinkSignUp.setOnAction(this::SignUp);

        // ButtonSignIn //
        //Action of directing to the Welcome window
        buttonSignIn.setOnAction(this::Welcome);

        // USERNAME TEXT FIELD //
        // Check if the text changes
        textFieldUser.setOnKeyTyped(this::textChanged);
        //Checking focus change in text field
        textFieldUser.focusedProperty().addListener(this::focusedChange);
        textFieldUser.setOnKeyTyped(this::updateLabelPassword);
        textFieldUser.setOnKeyTyped(this::updateLabelUser);

        // PASSWORD FIELD //
        // Checking focus change in text field
        password.setOnKeyReleased(this::copyPassword);
        password.setOnKeyTyped(this::textChanged);
        //Checking the change of focus in the password field
        password.focusedProperty().addListener(this::focusedChange);
        password.setOnKeyTyped(this::updateLabelPassword);

        // PASSWORD TEXT FIELD //
        // Checking the change of focus in the text field
        textFieldPassword.focusedProperty().addListener(this::focusedChange);
        // Check if the text changes
        textFieldPassword.setOnKeyTyped(this::textChanged);
        textFieldPassword.setOnKeyReleased(this::copyPassword);

        buttonShowHide.setOnAction(this::handleShowHide);

        stage.show();
        LOGGER.info("SingIn window initialized");

    }

    private void SignUp(ActionEvent event) {
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
        helper.togglePasswordFieldVisibility(buttonShowHide, imageViewButton, password, textFieldPassword);
    }

    private void updateLabelPassword(KeyEvent event) {
        try {
            if (event.getSource() instanceof PasswordField) {
                PasswordField passw = (PasswordField) event.getSource();
                if (passw.getText().length() <= 8) {
                    
                    throw new InvalidPasswordException("Password must be al least 8 characters long");

                }
                linePassword.setStroke(customColorGreen);
                labelInvalidPassword.setText("");
            }
            if (event.getSource() instanceof TextField) {
                TextField textField = (TextField) event.getSource();
                if (!textField.getText().isEmpty() && !textField.getText().contains(" ")) {
                    throw new InvalidPasswordException();
                }
                if (textField.equals(textFieldPassword) && textField.getText().length() <= 8) {
                    throw new InvalidPasswordException("Password must be al least 8 characters long");
                }
                lineUser.setStroke(customColorGreen);
                labelInvalidUser.setText("");
            }

        } catch (InvalidPasswordException ex) {
            if (ex.getMessage() != null) {
                linePassword.setStroke(Color.RED);
                LOGGER.info(ex.getMessage());
                labelInvalidPassword.setText(ex.getMessage());
            }
        }
    }

    private void updateLabelUser(KeyEvent event) {
        try {
            TextField passw = (TextField) event.getSource();
            boolean match = false;
            String user = passw.getText();
            System.out.println(user);
            if (user.contains(" ")) {
                throw new InvalidEmailValueException("Username can't contain an empty space");
            }
            Pattern pattern = Pattern.compile(emailPattern);
            Matcher matcher = pattern.matcher(user);
            if (matcher.find()) {
                match = true;
            }
            if (!match) {
                throw new InvalidEmailValueException("Invalid format of email (*@*.*)");
            }
            userException = false;
            lineUser.setStroke(Color.GREY);
            labelInvalidUser.setText("");
        } catch (InvalidEmailValueException ex) {
            lineUser.setStroke(Color.RED);
            LOGGER.info(ex.getMessage());
            labelInvalidUser.setText(ex.getMessage());
            userException = true;
        }

    }

    private void copyPassword(KeyEvent event) {
        helper.copyPassword(password, textFieldPassword);
    }

    private void textChanged(KeyEvent event) {
        if (((TextField) event.getSource()).getText().length() >= 20) {
            event.consume();
            ((TextField) event.getSource()).setText(((TextField) event.getSource()).getText().substring(0, 20));
        }
    }

    private void focusedChange(ObservableValue observable, Boolean oldValue, Boolean newValue) {

        if (oldValue) {
            if (!textFieldUser.isFocused() && !userException) {
                try {
                    if (textFieldUser.getText().isEmpty()) {
                        throw new IOException("Enter a username");
                    }
                    lineUser.setStroke(customColorGreen);
                    labelInvalidUser.setText("");
                } catch (IOException ex) {
                    lineUser.setStroke(Color.RED);
                    LOGGER.info(ex.getMessage());
                    labelInvalidUser.setText(ex.getMessage());
                }
            }
            if (!password.isFocused() && !textFieldPassword.isFocused() && !linePassword.getStroke().equals(Color.RED)) {
                try {
                    if (password.getText().isEmpty()) {
                        throw new IOException("Enter a password");
                    }
                    linePassword.setStroke(customColorGreen);
                    labelInvalidPassword.setText("");
                } catch (IOException ex) {
                    linePassword.setStroke(Color.RED);
                    LOGGER.info(ex.getMessage());
                    labelInvalidPassword.setText(ex.getMessage());
                }
            }

        }
    }

    private void Welcome(ActionEvent event) {
        try {
            stage.close();
            LOGGER.info("SignIn window closed");
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/Welcome.fxml"));
            Parent root = (Parent) loader.load();

            WelcomeController controller = ((WelcomeController) loader.getController());

            controller.setStage(new Stage());

            controller.initStage(root);
            LOGGER.info("Welcome window opened");
        } catch (IOException ex) {

        }
    }
}