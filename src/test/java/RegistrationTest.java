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
   public void testCheckUsernameInvalid() {
    Registration registration = new Registration();
      String username = "kyle!!!!!!!";
      boolean actualResponse = registration.checkUsername(username);
      assertEquals(false, actualResponse);
   }

@Test
      public void testCheckPasswordComplexity() {
    Registration registration = new Registration();
      String password = "Ch&&sec@ke99!";
      boolean actualResponse = registration.checkPasswordComplexity(password);
      assertEquals(true, actualResponse);
   }

   @Test 
   public void testCheckPasswordComplexityInvalid() {
    Registration registration = new Registration();
      String password = "password";
      assertEquals(false, registration.checkPasswordComplexity(password));
}

@Test 
public void testcheckCellPhoneNum() {
 Registration registration = new Registration();
   String SaPhonenum = "+27838968976";
   assertEquals(true, registration.checkcellPhoneNum(SaPhonenum));

}

@Test
public void testcheckCellPhoneNumInvalid() {
 Registration registration = new Registration();   
   String SaPhonenum = "08966553";
   assertEquals(false, registration.checkcellPhoneNum(SaPhonenum));
}
}