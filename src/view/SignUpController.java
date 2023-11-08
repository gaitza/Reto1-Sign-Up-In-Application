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
import exceptions.MaxConnectionException;
import exceptions.TimeOutException;
import exceptions.UserExistException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.ModelFactory;

/**
 *
 * @author bayro
 */
public class SignUpController {

    private Stage stage;
    @FXML
    private ComboBox comboPhone;
    @FXML
    private TextField textFieldPhone, textFieldEmail, textFieldPassword, textFieldCode, textFieldDirection, textFieldName, textFieldConfirmPassword;
    @FXML
    private Line lineInvalidPhone, lineInvalidEmail, lineInvalidDirection, lineInvalidCodePostal, lineInvalidName, lineInvalidPassword, lineInvalidConfirmPassword;
    @FXML
    private Text labelInvalidPhone, labelInvalidEmail, labelInvalidCode, labelInvalidAddress, labelInvalidPasswordConfirm, labelInvalidPassword, labelInvalidName;
    @FXML
    private Hyperlink hyperLinkSignIn;
    @FXML
    private PasswordField password, confirmPassword;
    @FXML
    private ToggleButton buttonShowHide;
    @FXML
    private ImageView imageViewButton;
    @FXML
    private Button buttonSignUp;

    private Map<String, String> prefijosTelefonos;
    private static Map<String, String> acronimos = new HashMap<>();
    Map<String, Integer> validate = new HashMap<String, Integer>() {
        {
            put("textFieldPhone", 0);
            put("textFieldEmail", 0);
            put("password", 0);
            put("confirmPassword", 0);
            put("textFieldCode", 0);
            put("textFieldDirection", 0);
            put("textFieldName", 0);
        }
    };
    private String opc;
    long quantityValuesZero = validate.values().stream().filter(valor -> valor == 0).count();
    private final ValidationHelper helper = new ValidationHelper();
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
      
        // HyperLnk //
        //Accion de dirigir a la ventana de SignUp
        hyperLinkSignIn.setOnAction(this::SignIn);

        // ButtonSignIn //
        //Accion de dirigir a la ventana de Welcome
        buttonSignUp.setOnAction(this::Welcome);

        prefijosTelefonos = helper.readCsv(acronimos);

        List<String> claveOrdenadas = new ArrayList<>(prefijosTelefonos.keySet());
        Collections.sort(claveOrdenadas);
        comboPhone.getItems().addAll(claveOrdenadas);

        comboPhone.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                mostrarClaveSeleccionada(newValue, comboPhone);
            }
        });

        helper.formatEmailTextField(textFieldEmail);

        password.setOnKeyReleased(this::copyPassword);
        password.setOnKeyTyped(this::updateLabel);
        password.focusedProperty().addListener(this::focusChange);

        textFieldPassword.focusedProperty().addListener(this::focusChange);
        textFieldPassword.setOnKeyReleased(this::copyPassword);
        textFieldPassword.setOnKeyTyped(this::updateLabel);

        textFieldEmail.focusedProperty().addListener(this::focusChange);
        textFieldEmail.setOnKeyPressed(this::confirmarEmail);
        textFieldEmail.setOnKeyTyped(this::updateLabel);

        textFieldPhone.focusedProperty().addListener(this::focusChange);
        textFieldPhone.setOnKeyTyped(this::updateLabel);

        textFieldCode.focusedProperty().addListener(this::focusChange);
        textFieldCode.setOnKeyTyped(this::updateLabel);

        textFieldName.focusedProperty().addListener(this::focusChange);
        textFieldName.setOnKeyTyped(this::updateLabel);

        textFieldDirection.focusedProperty().addListener(this::focusChange);
        textFieldDirection.setOnKeyTyped(this::updateLabel);

        textFieldConfirmPassword.setOnKeyReleased(this::copyPassword);
        textFieldConfirmPassword.setOnKeyTyped(this::updateLabel);
        textFieldConfirmPassword.focusedProperty().addListener(this::focusChange);

        confirmPassword.setOnKeyReleased(this::copyPassword);
        confirmPassword.setOnKeyTyped(this::updateLabel);
        confirmPassword.focusedProperty().addListener(this::focusChange);

        buttonShowHide.setOnAction(this::handleShowHide);

        stage.show();

        LOGGER.info("SingUp window initialized");
    }

    private void copyPassword(KeyEvent event) {
        helper.copyPassword(password, textFieldPassword);
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

    private void updateLabel(KeyEvent event) {
        if (quantityValuesZero == 1) {
            if (event.getSource() instanceof PasswordField) {
                PasswordField passw = (PasswordField) event.getSource();
                callValidation(passw.getId(), passw.getText());
            }
            if (event.getSource() instanceof TextField) {
                TextField textField = (TextField) event.getSource();
                callValidation(textField.getId(), textField.getText());
            }
        }
    }

    private void focusChange(ObservableValue observable, Boolean oldValue, Boolean newValue) {
        if (oldValue) {
            String field = "";
            String value = "";
            try {
                ReadOnlyBooleanProperty focusedProperty = (ReadOnlyBooleanProperty) observable;
                Node node = (Node) focusedProperty.getBean();

                field = ((Node) node).getId();
                value = ((TextField) node).getText(); // Obtenemos el texto del TextField
                if (field.equals("textFieldPassword")) {
                    field = "password";
                    value = textFieldPassword.getText();
                } else if (field.equals("textFieldConfirmPassword")) {
                    value = textFieldConfirmPassword.getText();
                    field = "confirmPassword";
                }
            } catch (Exception e) {
                field = opc;
            }
            callValidation(field, value);
            quantityValuesZero = validate.values().stream().filter(valor -> valor == 0).count();
        }
    }

    private void callValidation(String field, String value) {

        String acro = acronimos.get(comboPhone.getValue());
        if (field.equalsIgnoreCase("textFieldCode")) {
            validate.put("textFieldCode", 0);
            helper.executeValidations(field, !value.isEmpty() ? value : textFieldCode.getText(), lineInvalidCodePostal, labelInvalidCode, value, validate);
        } else if (field.equalsIgnoreCase("textFieldName")) {
            validate.put("textFieldName", 0);
            helper.executeValidations(field, !value.isEmpty() ? value : textFieldName.getText(), lineInvalidName, labelInvalidName, value, validate);
        } else if (field.equalsIgnoreCase("textFieldDirection")) {
            validate.put("textFieldDirection", 0);
            helper.executeValidations(field, !value.isEmpty() ? value : textFieldDirection.getText(), lineInvalidDirection, labelInvalidAddress, value, validate);
        } else if (field.equalsIgnoreCase("textFieldPhone")) {
            validate.put("textFieldPhone", 0);
            helper.executeValidations(field, !value.isEmpty() ? value : textFieldPhone.getText(), lineInvalidPhone, labelInvalidPhone, acro, validate);
        } else if (field.equalsIgnoreCase("textFieldEmail")) {
            validate.put("textFieldEmail", 0);
            helper.executeValidations(field, !value.isEmpty() ? value : textFieldEmail.getText(), lineInvalidEmail, labelInvalidEmail, value, validate);
        } else if (field.equalsIgnoreCase("password")) {
            validate.put("password", 0);
            helper.executeValidations(field, !value.isEmpty() ? value : password.getText(), lineInvalidPassword, labelInvalidPassword, value, validate);
        } else if (field.equalsIgnoreCase("confirmPassword")) {
            validate.put("confirmPassword", 0);
            helper.executeValidations(field, !value.isEmpty() ? value : confirmPassword.getText(), lineInvalidConfirmPassword, labelInvalidPasswordConfirm, password.getText(), validate);
        }
    }

    private void confirmarEmail(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            textFieldEmail.selectRange(0, 0); // Desseleccionar
            event.consume(); // Prevenir que se procese el evento de tecla Enter
        } else if (event.getCode() == KeyCode.BACK_SPACE && textFieldEmail.getSelection() != null) {
            textFieldEmail.deleteText(textFieldEmail.getSelection());
        } else if (event.getCode() == KeyCode.BACK_SPACE) {
            int caretPosition = textFieldEmail.getCaretPosition();
            textFieldEmail.deleteText(caretPosition - 1, caretPosition);
        }
    }

    private void mostrarClaveSeleccionada(String newValue, ComboBox comboPhone) {
        for (Map.Entry<String, String> entry : prefijosTelefonos.entrySet()) {
            if (entry.getKey().equals(newValue)) {
                Platform.runLater(() -> comboPhone.setValue(entry.getValue()));
                System.out.println("newValue es : " + newValue);
                System.out.println("Clave: " + entry.getKey() + " valor: " + entry.getValue());
                break;
            }
        }
    }

    private void SignIn(ActionEvent event) {
        try {
            stage.close();
            LOGGER.info("SignUp window closed");
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/SignIn.fxml"));
            Parent root = (Parent) loader.load();

            SignInController controller = ((SignInController) loader.getController());

            controller.setStage(new Stage());

            controller.initStage(root);
            LOGGER.info("SignIn window opened");
        } catch (IOException ex) {

        }
    }

    private void Welcome(ActionEvent event) {
        try {

            password.setVisible(true);
            confirmPassword.setVisible(true);
            for (Map.Entry<String, Integer> entry : validate.entrySet()) {
                if (entry.getValue() == 0) {
                    opc = entry.getKey();
                    focusChange(null, Boolean.TRUE, Boolean.FALSE);
                }
            }
            if (quantityValuesZero != 0) {
                throw new CommonException("");
            }
            Model model = ModelFactory.getModel();
            User user = new User(textFieldEmail.getText(), textFieldName.getText(), textFieldDirection.getText(), Integer.parseInt(textFieldCode.getText()), Integer.parseInt(textFieldPhone.getText()), textFieldPassword.getText());
            model.doSignUp(user);
            try {
                stage.close();
                LOGGER.info("SignUp window closed");
                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/SignIn.fxml"));
                Parent root = (Parent) loader.load();

                SignInController controller = ((SignInController) loader.getController());
                
                controller.setStage(new Stage());

                controller.initStage(root);

                LOGGER.info("Welcome window opened");
            } catch (Exception ex) {
                Logger.getLogger(SignInController.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (CommonException | UserExistException | ConnectionErrorException | TimeOutException | MaxConnectionException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
            alert.show();
            LOGGER.log(Level.SEVERE, ex.getMessage());
        } 
    }
}
