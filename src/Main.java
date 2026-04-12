/**
 * Main entry point for the application.
 *
 * The Main class coordinates the registration and login flow by
 * invoking the Registration class first and then passing the
 * registered user information to the Login class for authentication.
 */
public class Main {
    public static void main(String[] args) {
        Registration registration = new Registration();
        registration.Register();
        Login login = new Login();
        login.username = registration.username;
        login.password = registration.password;
        login.Name = registration.Name;
        login.LastName = registration.LastName;
        login.loginDetails();
    }
}