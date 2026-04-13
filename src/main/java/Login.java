import java.util.Scanner;

/**
 * Login handles user authentication using registered credentials.
 *
 * This class prompts the user to enter the username and password,
 * validates them against the stored values, and then welcomes the user.
 */
public class Login {
    public String username;
    public String password;
    public String Name;
    public String LastName;

    /**
     * Runs the login flow and keeps prompting until the correct credentials are entered.
     */
    public void loginDetails() {
        Scanner scanner = new Scanner(System.in);
        boolean loggedIn = false;
        System.out.println("Welcome to the Login Page.");

        do {
            System.out.println("Enter username.");
            username = scanner.next();
            System.out.println("Enter password.");
            password = scanner.next();
            if (loginUser(username, password)) {
                loggedIn = true;
                System.out.println("Login successful.");
                
            } else {
                System.out.println("Username or password is incorrect, please try again.");
            }
        } while (!loggedIn);

        System.out.println(getWelcomeMessage(Name, LastName));
    }
    public String getWelcomeMessage(String name, String lastName) {
        return "Welcome " + name + ", " + lastName + " it is great to see you again.";
    }

    /**
     * Validates that the entered username matches the stored username.
     */
    public boolean loginUser(String username, String password) {
        return username.equals(this.username) && password.equals(this.password);
    }

    /**
     * Username validation rules are duplicated here to support consistent checks.
     */
    public boolean checkUsername(String username) {
        // \\w{5} matches exactly 5 word characters (letters, digits, or underscores)
        // contains("_") ensures at least one underscore is present
        return username.matches("\\w{5}") && username.contains("_");
    }

    /**
     * Password complexity rules are duplicated here to support consistent checks.
     */
    public boolean checkPasswordComplexity(String password) {
        // Check minimum length of 8 characters
        // .*[A-Z].* matches if there's at least one uppercase letter
        // .*\d.* matches if there's at least one digit
        // .*[^\w].* matches if there's at least one special character (non-word character)
        // !password.contains("_") ensures no underscore is present
        return password.length() >= 8
                && password.matches(".*[A-Z].*")
                && password.matches(".*\\d.*")
                && password.matches(".*[^a-zA-Z0-9].*")
                && !password.contains("_");
    }
//Methods of the Registration class are duplicated here to allow for validation during login as well, ensuring that the same rules apply when checking credentials.
    /**
     * Validates a South African phone number format.
     */
    public boolean checkcellPhoneNum(String phonenumber) {
        // ^\\+27\\d{9}$ matches strings that start with +27 followed by exactly 9 digits
        String regex = "^\\+27\\d{9}$";
        return phonenumber.matches(regex);
    }
}

