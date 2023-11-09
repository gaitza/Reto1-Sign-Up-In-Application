/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import exceptions.CommonException;
import exceptions.InvalidEmailValueException;
import exceptions.InvalidPasswordException;
import exceptions.InvalidPhoneNumberException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

/**
 * Clase para realizar todas las validaciones en SignIn y SinUpController
 *
 * @author bayro
 */
public class ValidationHelper {

    private static final Logger LOGGER = Logger.getLogger("SignUpController.class");

    /**
     * Validaciones que son comunes para varios campos
     *
     * @param strng string a validar
     * @param email boolean para saber si es un email y no aplicarle la
     * validacion de caracter especiales.
     * @param spaces boolean para aplicar o no la validacion de espacios en
     * blanco.
     * @throws exceptions.CommonException
     */
    public void commomValidations(String strng, boolean email, boolean spaces) throws CommonException {
        if (strng.isEmpty()) {
            throw new CommonException("empty");
        }
        if (strng.contains(" ") && !spaces) {
            throw new CommonException("spaces");
        }
        if (!email) {
            Pattern pattern = Pattern.compile("[^a-zA-Z0-9\\s]");
            Matcher matcher = pattern.matcher(strng);
            boolean match = false;

            if (matcher.find()) {
                match = true;
            }
            if (match) {
                throw new CommonException("symbols");
            }
        }
    }

    /**
     * Validaciones especificas para el campo textFieldPhoneNumber
     *
     * @param number string con el telefono a validar.
     * @param acro acronimo del pais para enviarlo a PhoneNumber
     * @throws exceptions.CommonException
     * @throws exceptions.InvalidPhoneNumberException
     * @throws com.google.i18n.phonenumbers.NumberParseException
     */
    public void phoneNumberValidation(String number, String acro) throws CommonException, InvalidPhoneNumberException, NumberParseException {
        Pattern pattern = Pattern.compile("[a-zA-Z]");
        Matcher matcher = pattern.matcher(number);

        boolean match = false;
        if (matcher.find()) {
            match = true;
        }
        if (match) {
            throw new CommonException("letters");
        }
        if (acro == null) {
            throw new InvalidPhoneNumberException("You must select a country");
        }
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        System.out.println(acro);
        System.out.println(number);
        Phonenumber.PhoneNumber numberProto = phoneUtil.parse(number, acro);
        //Comprueba si el numero cumple el formato especifico.
        boolean isValid = phoneUtil.isValidNumber(numberProto);

        if (!isValid) {
            throw new InvalidPhoneNumberException("Format of phone number is incorrect ");
        }
    }

    /**
     * Validaciones especificas para el textFieldEmail
     *
     * @param email string con el email a validar.
     * @throws exceptions.InvalidEmailValueException
     *
     */
    public void emailValidation(String email) throws InvalidEmailValueException {

        boolean match = false;
        Pattern pattern = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+");
        Matcher matcher = pattern.matcher(email);
        if (matcher.find()) {
            match = true;
        }
        if (!match) {
            throw new InvalidEmailValueException("Invalid format of email (*@*.*)");
        }
    }

    /**
     * Validaciones especificas para el textFieldCode.
     *
     * @param code el valor del campo a validar.
     * @throws exceptions.CommonException
     *
     */
    public void codeValidation(String code) throws CommonException {
        Pattern pattern = Pattern.compile("[a-zA-Z]");
        Matcher matcher = pattern.matcher(code);

        boolean match = false;
        if (matcher.find()) {
            match = true;
        }
        if (match) {
            throw new CommonException("letters");
        }
    }

    /**
     * Validaciones especificas para el textFieldNAme
     *
     * @param name
     * @throws exceptions.CommonException
     *
     */
    public void nameValidation(String name) throws CommonException {
        Pattern pattern = Pattern.compile("^[^0-9]+$");
        Matcher matcher = pattern.matcher(name);

        boolean match = false;
        if (matcher.find()) {
            match = true;
        }
        if (!match) {
            throw new CommonException("numbers");
        }
    }

    /**
     * Validaciones especificas para la contraseña
     *
     * @param password string con la contraseña
     * @param confirmPassword string con la contraseña a confirmar
     * @throws exceptions.CommonException
     * @throws exceptions.InvalidPasswordException
     *
     */
    public void passwordValidation(String password, String confirmPassword) throws CommonException, InvalidPasswordException {

        if (password.length() < 8) {
            throw new InvalidPasswordException("Password must be al least 8 characters long");
        }
        Pattern letter = Pattern.compile("[a-zA-z]");
        Pattern special = Pattern.compile("[!@#$%&*()_+=|<>?{}\\[\\]~-]");
        boolean letters = false;

        Matcher hasLetter = letter.matcher(password);
        Matcher haveSpecial = special.matcher(password);
        boolean specialCh = false;
        
        if (hasLetter.find()) {
            letters = true;
        }
        if(haveSpecial.find()) {
            specialCh = true;
        }
        if(!specialCh) { 
            throw new InvalidPasswordException("Passwords must have one special character.");
        }
        if (!letters) {
            throw new InvalidPasswordException("Passwords must have one letter");
        }
        if (!confirmPassword.equals(password)) {
            throw new InvalidPasswordException("Passwords must match");
        }
    }

    /**
     * Metodo para leer el archivo csv que contiene todos los acronimos.
     *
     * @param acronimos
     * @return devuelve un map con todos datos del csv.
     *
     */
    public Map<String, String> readCsv(Map acronimos) {
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
        } catch (Exception ex) {
            Logger.getLogger(SignInController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return datos;
    }

    /**
     * Comprueba si esta presionado el boton de mostrar contraseña y muestra el
     * PasswordField o el textField segun corresponda Ademas de cambiar la
     * imagen del ojito.
     *
     * @param buttonShowHide
     * @param imageViewButton
     * @param password
     * @param textFieldPassword
     */
    public void togglePasswordFieldVisibility(ToggleButton buttonShowHide, ImageView imageViewButton, PasswordField password, TextField textFieldPassword) {
        if (buttonShowHide.isSelected()) {
            imageViewButton.setImage(new Image(getClass().getResourceAsStream("/resources/iconEye2.png")));
            password.setVisible(false);
            textFieldPassword.setVisible(true);
        } else {
            imageViewButton.setImage(new Image(getClass().getResourceAsStream("/resources/iconEye.png")));
            password.setVisible(true);
            textFieldPassword.setVisible(false);
        }
    }

    /**
     * Copia el texto de la contraseña de un campo a otro como corresponda.
     *
     * @param password
     * @param textFieldPassword
     */
    public void copyPassword(PasswordField password, TextField textFieldPassword) {
        if (password.isVisible()) {
            textFieldPassword.setText(password.getText());
        } else if (textFieldPassword.isVisible()) {
            password.setText(textFieldPassword.getText());
        }
    }

    /**
     * Ejecuta las validaciones comunes asi como las especificas del campo,
     * dependiendo del id que reciba.
     *
     *
     * @param opc id del campo a validar
     * @param value valor del campo a validar
     * @param line Line para ponerlo en rojo, especifico del campo a validar.
     * @param label label del campo a validar para enseñar el mensaje de error
     * si procede.
     * @param acro acronimo para enviar al campo de validar telefono
     * @param validate Map con los campos para cambiar a 1 si ha completado
     * exitosamente todas las validacioens.
     */
    public void executeValidations(String opc, String value, Line line, Text label, String acro, Map<String, Integer> validate) {

        switch (opc) {
            case "textFieldName":
                try {
                    commomValidations(value, false, true);
                    nameValidation(value);
                    line.setStroke(Color.GREY);
                    label.setText("");
                    validate.put("textFieldName", 1);
                } catch (CommonException ex) {
                    LOGGER.info(ex.getMessage());
                    line.setStroke(Color.RED);
                    label.setText(ex.getMessage());
                }
                break;
            case "textFieldDirection":
                try {
                    commomValidations(value, true, false);

                    line.setStroke(Color.GREY);
                    label.setText("");
                    validate.put("textFieldDirection", 1);
                } catch (CommonException ex) {
                    LOGGER.info(ex.getMessage());
                    line.setStroke(Color.RED);
                    label.setText(ex.getMessage());
                }
                break;
            case "textFieldPhone":
                try {
                    commomValidations(value, false, false);
                    phoneNumberValidation(value, acro);

                    line.setStroke(Color.GRAY);
                    label.setText("");
                    validate.put("textFieldPhone", 1);
                } catch (InvalidPhoneNumberException | CommonException ex) {
                    line.setStroke(Color.RED);
                    LOGGER.info(ex.getMessage());
                    label.setText(ex.getMessage());
                } catch (NumberParseException ex) {
                    LOGGER.info(ex.getMessage());
                }
                break;
            case "textFieldEmail":
                try {
                    commomValidations(value, true, false);
                    emailValidation(value);

                    line.setStroke(Color.GREY);
                    label.setText("");
                    validate.put("textFieldEmail", 1);
                } catch (InvalidEmailValueException | CommonException ex) {
                    LOGGER.info(ex.getMessage());
                    line.setStroke(Color.RED);
                    label.setText(ex.getMessage());

                }
                break;
            case "password":
                try {
                    commomValidations(value, true, false);
                    passwordValidation(value, acro);

                    line.setStroke(Color.GREY);
                    label.setText("");

                    validate.put("password", 1);
                } catch (InvalidPasswordException | CommonException ex) {
                    LOGGER.info(ex.getMessage());
                    line.setStroke(Color.RED);
                    label.setText(ex.getMessage());

                }
                break;
            case "confirmPassword":
                try {
                    commomValidations(value, true, false);
                    passwordValidation(value, acro);

                    line.setStroke(Color.GREY);
                    label.setText("");

                    validate.put("confirmPassword", 1);
                } catch (InvalidPasswordException | CommonException ex) {
                    LOGGER.info(ex.getMessage());
                    line.setStroke(Color.RED);
                    label.setText(ex.getMessage());
                }
                break;
            case "textFieldCode":
                try {
                    commomValidations(value, false, false);
                    codeValidation(value);

                    line.setStroke(Color.GREY);
                    label.setText("");
                    validate.put("textFieldCode", 1);
                } catch (CommonException ex) {
                    LOGGER.info(ex.getMessage());
                    line.setStroke(Color.RED);
                    label.setText(ex.getMessage());
                }
                break;
            case "passwordSignIn":
                try {
                    commomValidations(value, true, false);

                    line.setStroke(Color.GREY);
                    label.setText("");
                    validate.put("passwordSignIn", 1);
                } catch (CommonException ex) {
                    LOGGER.info(ex.getMessage());
                    line.setStroke(Color.RED);
                    label.setText(ex.getMessage());
                }
                break;
            default:
                break;
        }
    }

}
