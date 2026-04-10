import java.util.Scanner;

public class Registration {
    public String username;
    public String password;
    public String SaPhonenum;

    public void Register() {
        Scanner scanner = new Scanner(System.in);

        do {
            System.out.println("Enter Username (Exactly 5 characters and include an underscore)");
            username = scanner.next();
            if (!checkUsername(username)) {
                System.out.println("Username is not correctly formated; please enure that your username contains an underscore and is no more than five characters in lenght");
            }
        } while (!checkUsername(username));

        do {
            System.out.println("Enter Password (8 characters min, 1 Capital letter,1 special char, no underscore) ");
        password = scanner.next();
            if (checkPasswordComplexity(password)) {
            }
            else {
            System.out.println("Invalid password follow conditions");
            }
        }while (!checkPasswordComplexity(password));

        do {
            System.out.println("Enter South African Phone Number (Start with +27)");

            SaPhonenum = scanner.next();
            if (checkcellPhoneNum(SaPhonenum)) {
                System.out.println("Successfully Added");
            } else {
                System.out.println("Incorrectly formatted or does not contain +27 code.");
            }
        } while (!checkcellPhoneNum(SaPhonenum));

    }

//Methods
        public boolean checkUsername (String username){
            return username.matches("\\w{5}") && username.contains("_");
        }
        public boolean checkPasswordComplexity (String password){
            return password.length() >= 8
                    && password.matches(".*[A-Z].*")
                    && password.matches(".*\\d.*")
                    && password.matches(".*[^a-zA-Z0-9].*")
                    && !password.contains("_");
        }
        public boolean checkcellPhoneNum (String phonenumber){
            String regex = "^\\+27\\d{9}$";
            boolean isValid = phonenumber.matches(regex);
            if (isValid) {
            } else {
            }
            return isValid;
        }
    }

