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
import javafx.application.Platform;
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
import javafx.scene.control.ButtonType;
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
import javafx.stage.WindowEvent;
import model.ModelFactory;

/**
 * Establece la instancia de Stage asociada a este controlador.
 *
 * @author bayro
 */
public class SignInController {

    private Stage stage;

    private static final Logger LOGGER = Logger.getLogger("SignInController.class");
    private String parametro = " ";
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
    //Map para verificar que campos estan validados. 0 es que no esta validado y si se cambia a 1 es que esta validado.
    Map<String, Integer> validate = new HashMap<String, Integer>() {
        {
            put("textFieldEmail", 0);
            put("passwordSignIn", 0);
        }
    };
    //Long que recoge del map cuandos campos estan en 0, sirve para luego validar si todos los campos estan validados.
    long quantityValuesZero = validate.values().stream().filter(valor -> valor == 0).count();
    //Inicializo la clase que contiene las validaciones.
    private final ValidationHelper helper = new ValidationHelper();

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

    public void setParametro(String parametro) {
        this.parametro = parametro;
    }

    /**
     * Metodo que inicializa la ventana
     *
     * @param root
     */
    public void initStage(Parent root) {
        Scene scene = new Scene(root);

        stage.setScene(scene);

        stage.setTitle("SignIn");
        stage.setResizable(false);
        //Pone en el textFieldEmail un email, sirve para cuando viene de la ventana SignUp y ha completado exitosamente el registro
        textFieldEmail.setText(parametro);
        // HyperLnk //
        //Accion de dirigir a la ventana SignUp
        hyperLinkSignUp.setOnAction(this::SignUp);

        // ButtonSignIn //
        //Accion de dirigir a la ventana de Welcome
        buttonSignIn.setOnAction(this::Welcome);

        // USERNAME TEXT FIELD //
        // Comprueba si el campo cambia.
        textFieldEmail.setOnKeyTyped(this::textChanged);
        //Comprueba si cambia el foco.
        textFieldEmail.focusedProperty().addListener(this::focusedChange);

        // PASSWORD FIELD //
        // Comprueba si cambia el foco.
        passwordSignIn.setOnKeyReleased(this::copyPassword);
        //Comprueba si cambia el foco en el passwordField
        passwordSignIn.focusedProperty().addListener(this::focusedChange);

        // PASSWORD TEXT FIELD //
        // Comprueba si cambia el foco en el TextFieldPAssword
        textFieldPassword.focusedProperty().addListener(this::focusedChange);
        textFieldPassword.setOnKeyReleased(this::copyPassword);

        //Aplica la accion de ver la contraseña
        buttonShowHide.setOnAction(this::handleShowHide);
        
        stage.setOnCloseRequest(this::handleExitAction);
        
        stage.show();
        LOGGER.info("SingIn window initialized");

    }

    /**
     * Abre la ventana SignUp y cierra la actual
     *
     * @param event un tipo de evento ActionEvent.ACTION para cuando el botón
     * está presionado
     */
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
            Logger.getLogger(SignInController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * LLama al helper para mostrar la contraseña
     *
     * @param event un tipo de evento ActionEvent.ACTION para cuando el botón
     * está presionado
     */
    private void handleShowHide(ActionEvent event) {
        helper.togglePasswordFieldVisibility(buttonShowHide, imageViewButton, passwordSignIn, textFieldPassword);
    }

    /**
     * Llama al helper para copiar la contraseña.
     *
     * @param event un tipo de evento ActionEvent.ACTION para cuando el botón
     * está presionado
     */
    private void copyPassword(KeyEvent event) {
        helper.copyPassword(passwordSignIn, textFieldPassword);
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
     * Comprueba el cambio de foco de los campos
     *
     * @param observable Valor actual
     * @param oldValue Valor viejo
     * @param newValue Nuevo valor
     */
    private void focusedChange(ObservableValue observable, Boolean oldValue, Boolean newValue) {
        if (oldValue) {
            String field = "";
            if (oldValue) {
                try {
                    ReadOnlyBooleanProperty focusedProperty = (ReadOnlyBooleanProperty) observable;
                    Node node = (Node) focusedProperty.getBean();
                    field = node.getId();

                } catch (Exception e) {
                    //Le asigno el valor de la variable opc.Porque esta en el catch y se que cuando entra aqui viene del metodo
                    //Welcome(). Entonces observable es null y siempre entrara aqui.
                    field = opc;
                }

                if (field.equals("textFieldEmail")) {
                    helper.executeValidations(textFieldEmail.getId(), textFieldEmail.getText(), lineUser, labelInvalidUser, "", validate);
                    //mando siempre el id de passwordSignIn, independientemente de si el metodo lo ha invocado el textfield o el passwordField
                } else if (field.equals("passwordSignIn") || field.equals("textFieldPassword")) {
                    helper.executeValidations(passwordSignIn.getId(), passwordSignIn.getText(), linePassword, labelInvalidPassword, "", validate);
                }
            }
        }
    }

    /**
     * Metodo para ir a la ventana Welcome. Primero comprueba con el long si hay
     * algun campo sin validar. Si hay algun campo sin validar recorre el array
     * para validar todos y mostrar los errores correspondientes. Si no hubiera
     * ningun error mandao al model, un user con los campos correctos y si este
     * no devuelve errores abrira la ventana welcome y cerrara esta.
     *
     * @param event un tipo de evento ActionEvent.ACTION para cuando el botón
     * está presionado
     */
    private void Welcome(ActionEvent event) {
        try {
            //Compruebo en el momento de invocar a este metodo si hay algun campo sin validar.
            quantityValuesZero = validate.values().stream().filter(valor -> valor == 0).count();
            //Si es distinto a 0, es que algun campo no ha sido validado.Por lo que recorro el map y valido los campos
            if (quantityValuesZero != 0) {
                for (Map.Entry<String, Integer> entry : validate.entrySet()) {
                    if (entry.getValue() == 0) {
                        opc = entry.getKey();
                        focusedChange(null, Boolean.TRUE, Boolean.FALSE);
                    }
                }
                //Lanzo una excepcion para parar la ejecucion del codigo.
                //Avisando al usuario mas tarde con un alert de que algun dato es incorrecto.
                throw new CommonException("data");
            }
            Model model = ModelFactory.getModel();
            //Instancio un usuario y le doy los valores recogidos.
            User user = new User();
            user.setEmail(textFieldEmail.getText());
            user.setPassword(textFieldPassword.getText());
            //LLamo al modelo para realizar el SignIn
            user = model.doSignIn(user);
            //Si no ha devuelto ninguna excepción seguira con el codigo y abrira la ventana de Welcome
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
        //Si se lanza alguna excepcion la mostrare por un alert.
        } catch (CommonException | InvalidUserException | MaxConnectionException | ConnectionErrorException | TimeOutException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
            alert.show();
            LOGGER.log(Level.SEVERE, ex.getMessage());
        }
    }
}
