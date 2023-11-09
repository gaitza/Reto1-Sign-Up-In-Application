/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

import java.util.concurrent.TimeoutException;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.text.Text;
import main.AppFX;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import org.hamcrest.Matcher;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.testfx.api.FxAssert;
import static org.testfx.api.FxAssert.verifyThat;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import static org.testfx.matcher.base.NodeMatchers.isDisabled;
import static org.testfx.matcher.base.NodeMatchers.isEnabled;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.TextInputControlMatchers.hasText;

/**
 *
 * @author gaizka
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SignUpControllerIT extends ApplicationTest {
    /**
     * Starts application to be tested.
     * @param stage Primary Stage object
     * @throws Exception If there is any error
     */
    /*@Override public void start(Stage stage) throws Exception {
       new ApplicationUD3Example().start(stage);
    }*/
    /**
     * Stops application to be tested: it does nothing.
     */
    @Override public void stop() {}
    /**
     * Set up Java FX fixture for tests. This is a general approach for using a 
     * unique instance of the application in the test.
     * @throws java.util.concurrent.TimeoutException
     */
    @BeforeClass
    public static void setUpClass() throws TimeoutException {
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(AppFX.class);
        
   }
    


    // Método personalizado para borrar un TextField por su identificador
    private void clearTextField(String textFieldId) {
        // Usando lookup, busca el TextField por su identificador
        TextField textField = lookup(textFieldId).query();

        // Borra el contenido del TextField
        interact(() -> {
            textField.clear();
        });

        // Verifica que el TextField esté vacío
        verifyThat(textField, hasText(""));
    }

    
     private void selectComboBoxOption(String comboBoxId, String optionToSelect) {
        // Busca el ComboBox por su identificador
        ComboBox<String> comboBox = lookup(comboBoxId).query();

        // Abre la lista desplegable
        clickOn(comboBox);

        // Selecciona la opción deseada
        comboBox.getSelectionModel().select(optionToSelect);

        // Opcional: Cierra la lista desplegable (puede ser necesario en algunos casos)
        //clickOn(comboBox);
    }

    // Método personalizado para la aserción
    private void assertTextContent(String expectedText, Text text) {
        // Verifica que el objeto text no sea null antes de acceder a sus propiedades
        if (text != null) {
            assertEquals(expectedText, text.getText());
        } else {
            throw new AssertionError("El objeto Text es nulo");
        }
    }

    /**
     * Test of initial state of login view.
     */
     
    
    @Test
    public void test1_EyeShowPassword() {
        clickOn("#hyperLinkSignUp");
        clickOn("#password");
        write("abcd*1234");
        clickOn("#imageViewButton");
        clickOn("#confirmPassword");
        write("abcd*1234");
        clickOn("#imageViewButtonConfirm");
        clickOn("#imageViewButton");
        clickOn("#imageViewButtonConfirm");
        clearTextField("#password");
        clearTextField("#confirmPassword");
    }
    
    
    @Test
    public void test2_SignUpError() {
        clickOn("#textFieldEmail");
        write("administrator@gmail.com");
        clickOn("#buttonSignUp");
        verifyThat("Some data is wrong", Node::isVisible);
        clickOn("Aceptar");
        clearTextField("#textFieldEmail");
        clickOn("#textFieldName");
        write("administrator");
        clickOn("#buttonSignUp");
        verifyThat("Some data is wrong", Node::isVisible);
        clickOn("Aceptar");
        clearTextField("#textFieldName");
        clickOn("#textFieldPhone");
        write("625314895");
        clickOn("#buttonSignUp");
        verifyThat("Some data is wrong", Node::isVisible);
        clickOn("Aceptar");
        clearTextField("#textFieldPhone");
        clickOn("#textFieldDirection");
        write("erandio");
        clickOn("#buttonSignUp");
        verifyThat("Some data is wrong", Node::isVisible);
        clickOn("Aceptar");
        clearTextField("#textFieldDirection");
        clickOn("#textFieldCode");
        write("42153");
        clickOn("#buttonSignUp");
        verifyThat("Some data is wrong", Node::isVisible);
        clickOn("Aceptar");
        clearTextField("#textFieldCode");
        clickOn("#password");
        write("abcd*1234");
        clickOn("#buttonSignUp");
        verifyThat("Some data is wrong", Node::isVisible);
        clickOn("Aceptar");
        clearTextField("#password");
        clickOn("#confirmPassword");
        write("abcd*1234");
        clickOn("#buttonSignUp");
        verifyThat("Some data is wrong", Node::isVisible);
        clickOn("Aceptar");
        clearTextField("#confirmPassword");
        clickOn("#buttonSignUp");
        verifyThat("Some data is wrong", Node::isVisible);
        clickOn("Aceptar");
        
        
    }
     
    
    
    @Test
    public void test3_LabelError() {
        clickOn("#textFieldEmail");
        write("Markel@gmail.com");
        Platform.runLater(() -> {
        selectComboBoxOption("#comboPhone", "Spain");
        });
        clickOn("#textFieldPhone");
        write("625314895");
        clickOn("#textFieldDirection");
        write("erandio");
        clickOn("#textFieldCode");
        write("42153");
        clickOn("#password");
        write("abcd*1234");
        clickOn("#confirmPassword");
        write("abcd*1234");
        clickOn("#buttonSignUp");
        verifyThat("Some data is wrong", Node::isVisible);
        clickOn("Aceptar");
        Platform.runLater(() -> {
            Text text = lookup("#labelInvalidName").query();
            assertTextContent("This field can´t be blank", text);
        });
        clickOn("#textFieldName");
        write("administrator");
        clickOn("#buttonSignUp");
        
    }
    
    
    
    @Test
    public void test4_UsersViewOpenedOnButtonSignUpClick() {
        clickOn("#textFieldEmail");
        write("administrator@gmail.com");
        clickOn("#textFieldName");
        write("administrator");
        Platform.runLater(() -> {
        selectComboBoxOption("#comboPhone", "Spain");
        });
        clickOn("#textFieldPhone");
        write("625314895");
        clickOn("#textFieldDirection");
        write("erandio");
        clickOn("#textFieldCode");
        write("42153");
        clickOn("#password");
        write("abcd*1234");
        clickOn("#confirmPassword");
        write("abcd*1234");
        clickOn("#buttonSignUp");
        verifyThat("#btnContinuar", isVisible());
    }
    

   
}
