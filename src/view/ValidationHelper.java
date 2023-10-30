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
import exceptions.InvalidPhoneNumberException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

/**
 *
 * @author bayro
 */
public class ValidationHelper {

    private static final Logger LOGGER = Logger.getLogger("SignUpController.class");

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
        // Check if the number is valid
        boolean isValid = phoneUtil.isValidNumber(numberProto);

        if (!isValid) {
            throw new InvalidPhoneNumberException("Format of phone number is incorrect ");
        }
    }

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

    public void nameValidation(String code) throws CommonException {
        Pattern pattern = Pattern.compile("^[^0-9]+$");
        Matcher matcher = pattern.matcher(code);

        boolean match = false;
        if (matcher.find()) {
            match = true;
        }
        if (!match) {
            throw new CommonException("numbers");
        }
    }

    public void executeValidations(String opc, String value, Line line, Text label, String acro,  Map<String, Integer> validate) {

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
                    Logger.getLogger(SignUpController.class.getName()).log(Level.SEVERE, null, ex);
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
            case "textFieldPassword":
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
            case "confirmPassword":
                break;
            default:
                break;
        }
    }
}
