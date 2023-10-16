/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
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
    private TextField textFieldPhone;
    @FXML
    private Line lineInvalidPhone;
    @FXML
    private Text labelInvalidPhone;

    private Map<String, String> prefijosTelefonos;
    private static Map<String, String> acronimos = new HashMap<>();

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

        textFieldPhone.focusedProperty().addListener(this::cambioDeFoco);
        stage.show();
        LOGGER.info("SingUp window initialized");
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

        try (BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\bayro\\Desktop\\Reto1-Sign-Up-In-Application\\src\\resources\\paises.csv"))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(",");
                String clave = partes[1].replaceAll("^\"|\"$", ""); // Elimina comillas
                String valor = partes[5].replaceAll("^\"|\"$", ""); // Elimina comillas
                String acronimo = partes[3].replaceAll("^\"|\"$", ""); // Elimina comillas
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

    private void cambioDeFoco(ObservableValue observable, Boolean oldValue, Boolean newValue) {
        if (oldValue) {
            if (!textFieldPhone.isFocused()) {
                try {
                    if (textFieldPhone.getText().isEmpty()) {
                        throw new IOException("Enter a Phone");
                    }
                    if (textFieldPhone.getText().contains(" ")) {
                        throw new IOException("Phone can´t contains blank spaces");
                    }
                    PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                    PhoneNumber numberProto = phoneUtil.parse(textFieldPhone.getText(), acronimos.get(comboPhone.getValue()));

                    // Verificar si el número es válido
                    boolean isValid = phoneUtil.isValidNumber(numberProto);

                    if (!isValid) {
                        throw new IOException("Incorrect ");
                    }
                    lineInvalidPhone.setStroke(Color.GRAY);
                    labelInvalidPhone.setText("");
                } catch (IOException ex) {
                    lineInvalidPhone.setStroke(Color.RED);
                    LOGGER.info(ex.getMessage());
                    labelInvalidPhone.setText(ex.getMessage());
                } catch (NumberParseException ex) {
                    Logger.getLogger(SignUpController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
