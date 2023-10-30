/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import com.google.i18n.phonenumbers.NumberParseException;
import exceptions.CommonException;
import exceptions.InvalidEmailValueException;
import exceptions.InvalidPasswordException;
import exceptions.InvalidPhoneNumberException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
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
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author bayro
 */
public class SignUpController {

    private Stage stage;
    @FXML
    private ComboBox comboPhone;
    @FXML
    private TextField textFieldPhone, textFieldEmail, textFieldPassword, textFieldCode, textFieldDirection, textFieldName, confirmPassword;
    @FXML
    private Line lineInvalidPhone, lineInvalidEmail, lineInvalidDirection, lineInvalidCodePostal, lineInvalidName;
    @FXML
    private Text labelInvalidPhone, labelInvalidEmail, labelInvalidCode, labelInvalidAddress, labelInvalidPasswordConfirm, labelInvalidPassword, labelInvalidName;
    @FXML
    private Hyperlink hyperLinkSignIn;
    @FXML
    private PasswordField password;
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
           
            put("textFieldCode", 0);
            put("textFieldDirection", 0);
            put("textFieldName", 0);
           
        }
    };
    private String opc;
    long quantityValuesZero = validate.values().stream().filter(valor -> valor == 0).count();
    private ValidationHelper helper = new ValidationHelper();
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
 System.out.println(quantityValuesZero);
        // HyperLnk //
        //Accion de dirigir a la ventana de SignUp
        hyperLinkSignIn.setOnAction(this::SignIn);

        // ButtonSignIn //
        //Accion de dirigir a la ventana de Welcome
        buttonSignUp.setOnAction(this::Welcome);

        prefijosTelefonos = leerCsv();

        List<String> claveOrdenadas = new ArrayList<>(prefijosTelefonos.keySet());
        Collections.sort(claveOrdenadas);
        comboPhone.getItems().addAll(claveOrdenadas);

        comboPhone.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                mostrarClaveSeleccionada(newValue, comboPhone);
            }
        });

        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.endsWith("@h")) {
                change.setText("hotmail.com");
                change.setCaretPosition(change.getCaretPosition() + "otmail.com".length());
            } else if (newText.endsWith("@g")) {
                change.setText("gmail.com");
                change.setCaretPosition(change.getCaretPosition() + "mail.com".length());
            }
            return change;
        };

        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        textFieldEmail.setTextFormatter(textFormatter);
        textFieldEmail.focusedProperty().addListener(this::focusChange);
        textFieldEmail.setOnKeyPressed(this::confirmarEmail);

        textFieldPhone.focusedProperty().addListener(this::focusChange);

        textFieldCode.focusedProperty().addListener(this::focusChange);

        textFieldName.focusedProperty().addListener(this::focusChange);

        textFieldDirection.focusedProperty().addListener(this::focusChange);
        
        textFieldCode.setOnKeyTyped(this::updateLabel);
        textFieldPhone.setOnKeyTyped(this::updateLabel);
        textFieldName.setOnKeyTyped(this::updateLabel);
        textFieldDirection.setOnKeyTyped(this::updateLabel);
        textFieldEmail.setOnKeyTyped(this::updateLabel);
       
        stage.show();

        LOGGER.info("SingUp window initialized");
    }

    private void updateLabel(KeyEvent event) {
        if (quantityValuesZero == 1) {
            String field = "";
            String value = " ";
            if (event.getSource() instanceof PasswordField) {
                PasswordField passw = (PasswordField) event.getSource();
                field = passw.getId();
                value = passw.getText();
            }
            if (event.getSource() instanceof TextField) {
                TextField textField = (TextField) event.getSource();
                field = textField.getId();
                value = textField.getText();
            }
            callValidation(field, value);
        }
    }

    private void focusChange(ObservableValue observable, Boolean oldValue, Boolean newValue) {
        if (oldValue) {
            String field = " ";
            String value = " ";
            try {
                ReadOnlyBooleanProperty focusedProperty = (ReadOnlyBooleanProperty) observable;
                Node node = (Node) focusedProperty.getBean();
                if (node instanceof TextField) {
                    TextField textField = (TextField) node;
                    field = textField.getId();
                    value = textField.getText();
                }

            } catch (Exception e) {
                field = opc;
            }
            callValidation(field,value);
        }
    }

    private void callValidation(String field, String value) {
        
         String acro = acronimos.get(comboPhone.getValue());
        if (field.equalsIgnoreCase("textFieldCode")) {
            String val = textFieldCode.getText();
            helper.executeValidations(field, value, lineInvalidCodePostal, labelInvalidCode, value, validate);
            System.out.println(validate.get("textFieldCode"));
        } else if (field.equalsIgnoreCase("textFieldName")) {
            String val = textFieldName.getText();
            helper.executeValidations(field, value, lineInvalidName, labelInvalidName, value, validate);
        } else if (field.equalsIgnoreCase("textFieldDirection")) {
            String val = textFieldDirection.getText();
            helper.executeValidations(field, value, lineInvalidDirection, labelInvalidAddress, value, validate);
        } else if (field.equalsIgnoreCase("textFieldPhone")) {
            String val = textFieldPhone.getText();
            helper.executeValidations(field, value, lineInvalidPhone, labelInvalidPhone, acro, validate);
        } else if (field.equalsIgnoreCase("textFieldEmail")) {
            String val = textFieldEmail.getText();
            helper.executeValidations(field, value, lineInvalidEmail, labelInvalidEmail, value, validate);
        } else if (field.equalsIgnoreCase("textFieldPassword")) {
            String val = textFieldPassword.getText();
            helper.executeValidations(field, value, lineInvalidCodePostal, labelInvalidCode, value, validate);
        } else if (field.equalsIgnoreCase("confirmPassword")) {
            String val = confirmPassword.getText();
            helper.executeValidations(field, value, lineInvalidCodePostal, labelInvalidCode, value, validate);
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

    public static Map<String, String> leerCsv() {
        Map<String, String> datos = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(".\\src\\resources\\paises.csv"))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(",");
                String clave = partes[1].replaceAll("^\"|\"$", ""); // Remove quotes
                String valor = partes[5].replaceAll("^\"|\"$", ""); // Remove quotes
                String acronimo = partes[3].replaceAll("^\"|\"$", ""); //Remove quotes
                if (!clave.equals(" name")) {
                    datos.put(clave, valor);
                    acronimos.put(valor, acronimo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return datos;
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

    private void Welcome(ActionEvent event) {
        try {
            ObservableValue observable = null;
            for (Map.Entry<String, Integer> entry : validate.entrySet()) {
                if (entry.getValue() == 0) {
                    opc = entry.getKey();
                    focusChange(observable, Boolean.TRUE, Boolean.FALSE);
                }
            }

            if (quantityValuesZero != 0) {
                String msg = "Error some data is wrong";
                Alert alert = new Alert(Alert.AlertType.ERROR, msg);
                alert.show();
                LOGGER.log(Level.SEVERE, msg);
                return;
            }
            stage.close();
            LOGGER.info("SignUp window closed");
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
