import java.util.Scanner;

/**
 * Registration handles gathering and validating user sign-up data.
 *
 * This class prompts the user for first name, last name, username,
 * password, and South African phone number, then validates each field
 * against the defined registration rules.
 */
public class Registration {
    public String username;
    public String password;
    public String SaPhonenum;
    public String Name;
    public String LastName;

    /**
     * Runs the interactive registration process.
     * Prompts for user details and validates each input until correct.
     */
    public void Register() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the Registration Page.");

        System.out.println("Enter Name.");
        Name = scanner.next();
        System.out.println("Enter Last Name.");
        LastName = scanner.next();

        do {
            System.out.println("Enter Username (Exactly 5 characters and include an underscore).");
            username = scanner.next();
            if (!checkUsername(username)) {
                System.out.println("Username is not correctly formatted; please ensure that your username contains an underscore and is exactly five characters long.");
            } else {
                System.out.println("Username successfully captured.");
            }
        } while (!checkUsername(username));

        do {
            System.out.println("Enter Password (8 characters min, 1 Capital letter,1 special char, no underscore).");
            password = scanner.next();
            if (checkPasswordComplexity(password)) {
                System.out.println("Password successfully captured.");
            } else {
                System.out.println("Password is not correctly formatted; please ensure that your password contains at least eight characters, a capital letter, a number, and a special character.");
            }
        } while (!checkPasswordComplexity(password));

        do {
            System.out.println("Enter South African Phone Number (Start with +27).");
            SaPhonenum = scanner.next();
            if (checkcellPhoneNum(SaPhonenum)) {
                System.out.println("Cell phone number successfully added.");
            } else {
                System.out.println("Cell phone number is not correctly formatted or does not contain +27 code.");
            }
        } while (!checkcellPhoneNum(SaPhonenum));
    }

    /**
     * Validates that the username is exactly 5 characters and contains an underscore.
     */
    public boolean checkUsername(String username) {
        return username.matches("\\w{5}") && username.contains("_");
    }

    /**
     * Validates that the password meets the complexity rules.
     */
    public boolean checkPasswordComplexity(String password) {
        return password.length() >= 8
                && password.matches(".*[A-Z].*")
                && password.matches(".*\\d.*")
                && password.matches(".*[^a-zA-Z0-9].*")
                && !password.contains("_");
    }

    /**
     * Validates that the phone number is a South African number beginning with +27.
     */
    public boolean checkcellPhoneNum(String phonenumber) {
        String regex = "^\\+27\\d{9}$";
        boolean isValid = phonenumber.matches(regex);
        return isValid;
    }
}

