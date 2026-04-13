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
}

