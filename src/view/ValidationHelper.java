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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author bayro
 */
public class ValidationHelper {

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
}
