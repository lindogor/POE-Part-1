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

        System.out.println("Welcome " + Name + " " + LastName + ", it is great to see you again.");
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
        return username.matches("\\w{5}") && username.contains("_");
    }

    /**
     * Password complexity rules are duplicated here to support consistent checks.
     */
    public boolean checkPasswordComplexity(String password) {
        return password.length() >= 8
                && password.matches(".*[A-Z].*")
                && password.matches(".*\\d.*")
                && password.matches(".*[^a-zA-Z0-9].*")
                && !password.contains("_");
    }

    /**
     * Validates a South African phone number format.
     */
    public boolean checkcellPhoneNum(String phonenumber) {
        String regex = "^\\+27\\d{9}$";
        return phonenumber.matches(regex);
    }
}

