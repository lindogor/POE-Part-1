import java.util.Scanner;

public class Login {
    public String username;
    public String password;

    public void loginDetails() {
        Scanner scanner = new Scanner(System.in);
        boolean loggedIn = true;

        do {
            System.out.println("Enter username");

            username = scanner.next();
            System.out.println("Enter password");
            password = scanner.next();
            if (username.equals(this.username) && password.equals(this.password)) {
                System.out.println("Login successful");

            } else {
                System.out.println("Invalid username or password");
            }
        } while (!loggedIn);

        System.out.println("Welcome " + username);
    }
}

