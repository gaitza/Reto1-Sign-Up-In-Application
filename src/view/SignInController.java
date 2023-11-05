/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import DataTransferObjects.Model;
import DataTransferObjects.User;
import exceptions.CommonException;
import exceptions.ConnectionErrorException;
import exceptions.InvalidUserException;
import exceptions.MaxConnectionException;
import exceptions.TimeOutException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.ModelFactory;

/**
 *
 * @author bayro
 */
public class SignInController {

    private Stage stage;

    private static final Logger LOGGER = Logger.getLogger("SignInController.class");

    @FXML
    private TextField textFieldEmail, textFieldPassword;
    @FXML
    private PasswordField passwordSignIn;
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
    String opc;
    Map<String, Integer> validate = new HashMap<String, Integer>() {
        {
            put("textFieldEmail", 0);
            put("passwordSignIn", 0);
        }
    };
    long quantityValuesZero = validate.values().stream().filter(valor -> valor == 0).count();
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
        textFieldEmail.setOnKeyTyped(this::textChanged);
        //Checking focus change in text field
        textFieldEmail.focusedProperty().addListener(this::focusedChange);

        // PASSWORD FIELD //
        // Checking focus change in text field
        passwordSignIn.setOnKeyReleased(this::copyPassword);
        passwordSignIn.setOnKeyTyped(this::textChanged);
        //Checking the change of focus in the password field
        passwordSignIn.focusedProperty().addListener(this::focusedChange);

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
        helper.togglePasswordFieldVisibility(buttonShowHide, imageViewButton, passwordSignIn, textFieldPassword);
    }

    private void copyPassword(KeyEvent event) {
        helper.copyPassword(passwordSignIn, textFieldPassword);
    }

    private void textChanged(KeyEvent event) {
        if (((TextField) event.getSource()).getText().length() >= 20) {
            event.consume();
            ((TextField) event.getSource()).setText(((TextField) event.getSource()).getText().substring(0, 20));
        }
    }

    private void focusedChange(ObservableValue observable, Boolean oldValue, Boolean newValue) {
        if (oldValue) {
            String field = "";
            if (oldValue) {
                try {
                    ReadOnlyBooleanProperty focusedProperty = (ReadOnlyBooleanProperty) observable;
                    Node node = (Node) focusedProperty.getBean();
                    field = node.getId();

                } catch (Exception e) {
                    field = opc;
                }
                if (field.equals("textFieldEmail")) {
                    helper.executeValidations(textFieldEmail.getId(), textFieldEmail.getText(), lineUser, labelInvalidUser, "", validate);
                } else if (field.equals("passwordSignIn") || field.equals("textFieldPassword")) {
                    helper.executeValidations(passwordSignIn.getId(), passwordSignIn.getText(), linePassword, labelInvalidPassword, "", validate);
                }
            }
        }
    }

    private void Welcome(ActionEvent event) {
        try {
            quantityValuesZero = validate.values().stream().filter(valor -> valor == 0).count();
            if (quantityValuesZero != 0) {
                for (Map.Entry<String, Integer> entry : validate.entrySet()) {
                    if (entry.getValue() == 0) {
                        opc = entry.getKey();
                        focusedChange(null, Boolean.TRUE, Boolean.FALSE);
                    }
                }
                throw new CommonException("");
            }
            Model model = ModelFactory.getModel();
            User user = new User();
            user.setEmail(textFieldEmail.getText());
            user.setPassword(textFieldPassword.getText());
            user = model.doSignIn(user);
            try {

                stage.close();
                LOGGER.info("SignIn window closed");
                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/Welcome.fxml"));
                Parent root = (Parent) loader.load();

                WelcomeController controller = ((WelcomeController) loader.getController());

                controller.setStage(new Stage());

                controller.initStage(root);
                LOGGER.info("Welcome window opened");
            } catch (Exception ex) {
                Logger.getLogger(SignInController.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (CommonException | InvalidUserException | MaxConnectionException | ConnectionErrorException | TimeOutException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
            alert.show();
            LOGGER.log(Level.SEVERE, ex.getMessage());
        }
    }
}
