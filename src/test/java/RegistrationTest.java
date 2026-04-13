import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class RegistrationTest{
    @Test
      public void testCheckUsername() {
    Registration registration = new Registration();
      String username = "kyl_1";
      boolean actualResponse = registration.checkUsername(username);
      assertEquals(true, actualResponse);
   }

@Test
      public void testCheckPasswordComplexity() {
    Registration registration = new Registration();
      String password = "Ch&&sec@ke99!";
      boolean actualResponse = registration.checkPasswordComplexity(password);
      assertEquals(true, actualResponse);
   }

   @Test 
   public void testCheckPasswordComplexity1() {
    Registration registration = new Registration();
      String password = "password";
      assertEquals(false, registration.checkPasswordComplexity(password));
}



}