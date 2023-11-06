package Test;

import java.util.concurrent.TimeoutException;
import javafx.stage.Stage;
import main.AppFX;
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
    /**
     * Test of initial state of login view.
     */
    
    @Test
    public void test1_InitialState() {
        verifyThat("#textFieldUser", hasText(""));
        verifyThat("#password",hasText(""));
        verifyThat("#buttonSignIn", isDisabled());
    }
    
    /**
     * Test test that button Aceptar is disabled if user or password fields are empty.
    */
    
    @Test
    public void test2_AceptarIsDisabled() {
        clickOn("#textFieldUser");
        write("administrator@gmail.com");
        //verifyThat("#buttonSignIn", isDisabled());
        eraseText(23);
        clickOn("#password");
        write("abcd*1234");
        //verifyThat("#buttonSignIn", isDisabled());
        eraseText(9);
        //verifyThat("#buttonSignIn", isDisabled());
        
        
    }
    
    /**
     * Test test that button Aceptar is enabled when user and password fields are full.
    */ 
    
    @Test
    public void test3_AceptarIsEnabled() {
        clickOn("#textFieldUser");
        write("administrator@gmail.com");
        clickOn("#password");
        write("abcd*1234");
        verifyThat("#buttonSignIn", isEnabled());
        clickOn("#textFieldUser");
        eraseText(23);
        clickOn("#password");
        eraseText(9);
    }
    
    /**
     * Test test that user's manager view is opened when button Aceptar is 
     * clicked
    */
    
    @Test
    public void test4_UsersViewOpenedOnAceptarClick() {
        clickOn("#textFieldUser");
        write("administrator@gmail.com");
        clickOn("#password");
        write("abcd*1234");
        clickOn("#buttonSignIn");
        verifyThat("#usersViewPane", isVisible());
        clickOn("#textFieldUser");
        eraseText(23);
        clickOn("#password");
        eraseText(9);
    }

}

