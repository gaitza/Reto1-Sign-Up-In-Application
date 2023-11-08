/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

import java.util.concurrent.TimeoutException;
import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import main.AppFX;
import static org.hamcrest.CoreMatchers.not;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
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
    
    private void verifyLabelIsFilled(String labelId) {
        verifyThat(labelId, hasText(""));
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

    

    /**
     * Test of initial state of login view.
     */
    /*
    @Test
    public void test1_InitialState() {
        clickOn("#hyperLinkSignUp");
        verifyThat("#textFieldEmail", hasText(""));
        verifyThat("#textFieldName", hasText(""));
        verifyThat("#textFieldPhone", hasText(""));
        verifyThat("#textFieldDirection", hasText(""));
        verifyThat("#textFieldCode", hasText(""));
        verifyThat("#password", hasText(""));
        verifyThat("#confirmPassword",hasText(""));
        verifyThat("#buttonSignUp", isDisabled());
    }
    */
    /*
    @Test
    public void test2_ButtonSignInIsDisabled() {
        //clickOn("#hyperLinkSignUp");
        clickOn("#textFieldEmail");
        write("administrator@gmail.com");
        verifyThat("#buttonSignIn", isDisabled());
        clearTextField("#textFieldEmail");
        clickOn("#textFieldName");
        write("administrator");
        verifyThat("#textFieldName", isDisabled());
        clearTextField("#textFieldName");
        clickOn("#textFieldPhone");
        write("625314895");
        verifyThat("#textFieldName", isDisabled());
        clearTextField("#textFieldPhone");
        clickOn("#textFieldDirection");
        write("erandio");
        verifyThat("#textFieldName", isDisabled());
        clearTextField("#textFieldDirection");
        clickOn("#textFieldCode");
        write("42153");
        verifyThat("#textFieldName", isDisabled());
        clearTextField("#textFieldCode");
        clickOn("#password");
        write("abcd*1234");
        verifyThat("#textFieldName", isDisabled());
        clearTextField("#password");
        clickOn("#confirmPassword");
        write("abcd*1234");
        verifyThat("#buttonSignIn", isDisabled());
        clearTextField("#confirmPassword");
        verifyThat("#buttonSignIn", isDisabled());
        
        
    }
    */
    @Test
    public void test3_ButtonSignInIsEnabled() {
        clickOn("#hyperLinkSignUp");
        clickOn("#textFieldEmail");
        write("administrator@gmail.com");
        Platform.runLater(() -> {
        // Coloca aquí el código que interactúa con la interfaz de usuario
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
        clickOn("#Aceptar");
        verifyLabelIsFilled("#labelInvalidUser");
        clickOn("#textFieldName");
        write("administrator");
        verifyThat("#buttonSignUp", isEnabled());
    }
    /*
    @Test
    public void test4_UsersViewOpenedOnButtonSignInClick() {
        clickOn("#textFieldEmail");
        write("administrator@gmail.com");
        clickOn("#textFieldName");
        write("administrator");
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
        verifyThat("#Welcome.fxml", isVisible());
    }
*/
}
