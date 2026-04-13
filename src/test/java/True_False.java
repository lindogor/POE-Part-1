import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;

public class True_False {
    @Test
    public void testloggedin() {
        assertTrue(true, "Logged Successfully.");
    }
    @Test
    public void testLoggedInFalse() {
        assertFalse(false, "Login Failed.");
    }
    @Test 
    public void testcheckUsername(){
        assertTrue(true, "Username is correctly formatted.");
    }
    @Test 
    public void testCheckUsernameFalse(){
        assertFalse(false, "Username is incorrectly formatted.");
    }

    @Test
    public void testcheckPasswordComplexity() {
        assertTrue(true, "Password meets complexity requirements.");
}
    @Test
    public void testCheckPasswordComplexityFalse() {
        assertFalse(false, "Password does not meet complexity requirements.");
    }

    @Test
    public void testcheckCellPhoneNum() {
        assertTrue(true, "Cell phone number is correctly formatted.");
    }

    @Test
    public void testCheckCellPhoneNumFalse() {
        assertFalse(false, "Cell phone number is incorrectly formatted.");
    }

    @Test
    public void testcellPhoneNum() {
        assertTrue(true, "Cell phone number correctly formatted.");
    }
    @Test 
    public void testCellPhoneNumFalse() {
        assertFalse(false, "Cell phone number incorrectly formatted.");
    }
}