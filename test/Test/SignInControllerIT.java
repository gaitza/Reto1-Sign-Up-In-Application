package Test;

import java.util.concurrent.TimeoutException;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import main.AppFX;
import static org.junit.Assert.assertEquals;
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
public class SignInControllerIT extends ApplicationTest {
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
        clickOn("#passwordSignIn");
        write("abcd*1234");
        clickOn("#imageViewButton");
        clickOn("#imageViewButton");
        clearTextField("#passwordSignIn");
    }
    
    /**
     * Test test that button Aceptar is disabled if user or password fields are empty.
    */
    
    @Test
    public void test2_SignInError() {
        clickOn("#passwordSignIn");
        clickOn("#textFieldEmail");
        write("erichu@gmail.com");
        clickOn("#buttonSignIn");
        verifyThat("Some data is wrong", Node::isVisible);
        clickOn("Aceptar");
        clearTextField("#textFieldEmail");
        clickOn("#textFieldEmail");
        clickOn("#passwordSignIn");
        write("abcd*1234");
        clickOn("#buttonSignIn");
        verifyThat("Some data is wrong", Node::isVisible);
        clickOn("Aceptar");
        clearTextField("#passwordSignIn");
        clickOn("#buttonSignIn");
        verifyThat("Some data is wrong", Node::isVisible);
        clickOn("Aceptar");
   
    }
    
    /**
     * Test test that button Aceptar is enabled when user and password fields are full.
    */ 
    
    @Test
    public void test3_LabelError() {
        clickOn("#passwordSignIn");
        write("abcd*1234");
        clickOn("#buttonSignIn");
        verifyThat("Some data is wrong", Node::isVisible);
        clickOn("Aceptar");
        clearTextField("#textFieldEmail");
        Platform.runLater(() -> {
            Text text = lookup("#labelInvalidUser").query();
            assertTextContent("This field can´t be blank", text);
        });
        clickOn("#textFieldEmail");
        write("erichu@gmail.com");
        clickOn("#buttonSignIn");
        verifyThat("#btnCerrarSesion", isVisible());
        clickOn("#btnCerrarSesion");
        clickOn("Aceptar");
    }
    
    /**
     * Test test that user's manager view is opened when button Aceptar is 
     * clicked
    */
    
    @Test
    public void test4_UsersViewOpenedOnButtonSignInClick() {
        clickOn("#hyperLinkSignIn");
        clickOn("Aceptar");
        clearTextField("#textFieldEmail");
        clearTextField("#passwordSignIn");
        clickOn("#textFieldEmail");
        write("erichu@gmail.com");
        clickOn("#passwordSignIn");
        write("abcd*1234");
        clickOn("#buttonSignIn");
        verifyThat("#btnCerrarSesion", isVisible());
    }
    
}

