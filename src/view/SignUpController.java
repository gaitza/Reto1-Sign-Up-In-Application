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
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
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
    private ToggleButton buttonShowHide, buttonShowHideConfirm;
    @FXML
    private ImageView imageViewButton, imageViewButtonConfirm;
    @FXML
    private Button buttonSignUp;

    //Map para los prefijos de los telefonos.
    private Map<String, String> prefijosTelefonos;
    //Map para los acronimos, ya que la api solo funciona con estos.
    private static Map<String, String> acronimos = new HashMap<>();
    //Map para verificar luego las verificaciones.
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
    //Long para contar si hay algun campo sin validar.
    long quantityValuesZero = validate.values().stream().filter(valor -> valor == 0).count();
    //Helper para validar los campos
    private final ValidationHelper helper = new ValidationHelper();
    private static final Logger LOGGER = Logger.getLogger("SignUpController.class");

    public Stage getStage() {
        return stage;
    }

    /**
     * Establece la instancia de Stage asociada a este controlador.
     *
     * @param stage La instancia de Stage que se asignará a este controlador.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Metodo que inicializa la ventana
     *
     * @param root
     */
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

        //Cargo en el map los prefijos y numeros
        prefijosTelefonos = helper.readCsv(acronimos);
        //Las ordeno para mostrarlas ordenadas
        List<String> claveOrdenadas = new ArrayList<>(prefijosTelefonos.keySet());
        Collections.sort(claveOrdenadas);
        comboPhone.getItems().addAll(claveOrdenadas);

        comboPhone.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                mostrarClaveSeleccionada(newValue, comboPhone);
            }
        });

        // PASSWORD FIELD //
        // Comprueba si cambia el foco.
        password.focusedProperty().addListener(this::focusChange);
        //Copia la contraseña.

        //Para actualizar el label en caso de que sea el ultimo campo sin validar.
        password.setOnKeyReleased(this::updateLabel);

        // Comprueba si cambia el foco.
        textFieldPassword.focusedProperty().addListener(this::focusChange);

        //Para actualizar el label en caso de que sea el ultimo campo sin validar.
        textFieldPassword.setOnKeyReleased(this::updateLabel);

        // Comprueba si cambia el foco.
        textFieldEmail.focusedProperty().addListener(this::focusChange);
        //Para actualizar el label en caso de que sea el ultimo campo sin validar.
        textFieldEmail.setOnKeyReleased(this::updateLabel);
        textFieldEmail.setOnKeyTyped(this::textChanged);

        // Comprueba si cambia el foco.
        textFieldPhone.focusedProperty().addListener(this::focusChange);
        //Para actualizar el label en caso de que sea el ultimo campo sin validar.
        textFieldPhone.setOnKeyReleased(this::updateLabel);

        // Comprueba si cambia el foco.
        textFieldCode.focusedProperty().addListener(this::focusChange);
        //Para actualizar el label en caso de que sea el ultimo campo sin validar.
        textFieldCode.setOnKeyReleased(this::updateLabel);

        // Comprueba si cambia el foco.
        textFieldName.focusedProperty().addListener(this::focusChange);
        //Para actualizar el label en caso de que sea el ultimo campo sin validar.
        textFieldName.setOnKeyReleased(this::updateLabel);

        // Comprueba si cambia el foco.
        textFieldDirection.focusedProperty().addListener(this::focusChange);
        //Para actualizar el label en caso de que sea el ultimo campo sin validar.
        textFieldDirection.setOnKeyReleased(this::updateLabel);

        //Copia la contraseña y 
        //Para actualizar el label en caso de que sea el ultimo campo sin validar.
        textFieldConfirmPassword.setOnKeyReleased(this::updateLabel);
        // Comprueba si cambia el foco.
        textFieldConfirmPassword.focusedProperty().addListener(this::focusChange);

        //Copia la contraseña y
        //Para actualizar el label en caso de que sea el ultimo campo sin validar.
        confirmPassword.setOnKeyReleased(this::updateLabel);
        // Comprueba si cambia el foco.
        confirmPassword.focusedProperty().addListener(this::focusChange);

        //Accion para mostrar u ocultar la contraseña
        buttonShowHide.setOnAction(this::handleShowHide);
        buttonShowHideConfirm.setOnAction(this::handleShowHide);
        stage.show();

        stage.setOnCloseRequest(this::handleExitAction);

        LOGGER.info("SingUp window initialized");
    }
    
      /**
     * Comprueba si el texto tiene menos de 30 caracteres. Si llega al maximo no
     * permite ingresar mas y consume el evento del teclado
     *
     * @param event un tipo de evento ActionEvent.ACTION para cuando el botón
     * está presionado
     */
    private void textChanged(KeyEvent event) {
        if (((TextField) event.getSource()).getText().length() >= 30) {
            event.consume();
            ((TextField) event.getSource()).setText(((TextField) event.getSource()).getText().substring(0, 30));
        }
    }

    /**
     * LLama al helper para mostrar la contraseña
     *
     * @param event un tipo de evento ActionEvent.ACTION para cuando el botón
     * está presionado
     */
    private void handleShowHide(ActionEvent event) {
        String sourceId = ((Node) event.getSource()).getId();
        if (sourceId.equals("buttonShowHide")) {
            helper.togglePasswordFieldVisibility(buttonShowHide, imageViewButton, password, textFieldPassword);
        } else {
            helper.togglePasswordFieldVisibility(buttonShowHideConfirm, imageViewButtonConfirm, confirmPassword, textFieldConfirmPassword);
        }
    }

    /**
     * Comprueba si la cantidad de campos no validados es igual a 1. Si es asi
     * realiza las comprobaciones conforme cambia el valor del campo. Y tambien
     * Llama al helper para copiar la contraseña.
     *
     * @param event un tipo de evento ActionEvent.ACTION para cuando el botón
     * está presionado
     */
    private void updateLabel(KeyEvent event) {
        if (quantityValuesZero == 1) {
            if (event.getSource() instanceof PasswordField) {
                PasswordField passw = (PasswordField) event.getSource();
                callValidation(passw.getId(), passw.getText());
            }
            if (event.getSource() instanceof TextField) {
                TextField textField = (TextField) event.getSource();
                System.out.println(textField.getText());
                callValidation(textField.getId(), textField.getText());
            }
        }
        String sourceId = ((Node) event.getSource()).getId();
        if (sourceId.equals("password") || sourceId.equals("textFieldPassword")) {
            helper.copyPassword(password, textFieldPassword);
        } else {
            helper.copyPassword(confirmPassword, textFieldConfirmPassword);
        }
    }

    /**
     * Comprueba el cambio de foco de los campos
     *
     * @param observable Valor actual
     * @param oldValue Valor viejo
     * @param newValue Nuevo valor
     */
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

    /**
     * LLama al metodo de ejecutar las validaciones, pasandole los parametros
     * especificos para cada campo.
     *
     * @param field String que contiene el id del campo
     * @param value String que contiene el valor del campo
     */
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

    /**
     * Como en el comboBox se visualiza el nombre de los paises, aqui busco el
     * nombre del pais y muestro su prefijo.
     *
     * @param newValue
     * @param comboPhone
     */
    private void mostrarClaveSeleccionada(String newValue, ComboBox comboPhone) {
        for (Map.Entry<String, String> entry : prefijosTelefonos.entrySet()) {
            if (entry.getKey().equals(newValue)) {
                Platform.runLater(() -> comboPhone.setValue(entry.getValue()));
                break;
            }
        }
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

    /**
     * Abre la ventana SignIn y cierra la actual
     *
     * @param event un tipo de evento ActionEvent.ACTION para cuando el botón
     * está presionado
     */
    private void SignIn(ActionEvent event) {
        try {
            Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to exit?.You will lose the data entered");
            a.showAndWait();
            if (a.getResult().equals(ButtonType.CANCEL)) {
                event.consume();
            } else {
                stage.close();
                LOGGER.info("SignUp window closed");
                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/SignIn.fxml"));
                Parent root = (Parent) loader.load();

                SignInController controller = ((SignInController) loader.getController());

                controller.setStage(new Stage());

                controller.initStage(root);
                LOGGER.info("SignIn window opened");
            }

        } catch (IOException ex) {
            Logger.getLogger(SignInController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Metodo para ejecutar el registro. Primero comprueba con el long si hay
     * algun campo sin validar. Si hay algun campo sin validar recorre el array
     * para validar todos y mostrar los errores correspondientes. Si no hubiera
     * ningun error mandao al model, un user con los campos correctos y si este
     * no devuelve errores abrira la ventana SignIn y cerrara esta.
     *
     * @param event un tipo de evento ActionEvent.ACTION para cuando el botón
     * está presionado
     */
    private void Welcome(ActionEvent event) {
        try {

            password.setVisible(true);
            confirmPassword.setVisible(true);
            //Recorro el map para ver si hay campos sin validar.
            for (Map.Entry<String, Integer> entry : validate.entrySet()) {
                //Si esta sin validar llamo al metodo focus change que validara el campo.
                if (entry.getValue() == 0) {
                    opc = entry.getKey();
                    focusChange(null, Boolean.TRUE, Boolean.FALSE);
                }
            }
            //Si quantityValuesZero es distinto a 0 es que hay algun campo que no cumple los requisitos y por tanto esta mal
            //Lanzo la excepcion para detener la ejecución del codigo.
            if (quantityValuesZero != 0) {
                throw new CommonException("data");
            }
            Model model = ModelFactory.getModel();
            User user = new User(textFieldEmail.getText(), textFieldName.getText(), textFieldDirection.getText(), Integer.parseInt(textFieldCode.getText()), Integer.parseInt(textFieldPhone.getText()), textFieldPassword.getText());
            model.doSignUp(user);
            //Cierro la ventana actual y abro la ventana de SignIn.
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

            //Si se lanza alguna excepcion la lanzo en un alert.
        } catch (CommonException | UserExistException | ConnectionErrorException | TimeOutException | MaxConnectionException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
            alert.show();
            LOGGER.log(Level.SEVERE, ex.getMessage());
        }
    }
}
