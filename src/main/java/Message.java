import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Message {

    // MessageCount keeps track of how many messages have been created
    private static int messageCount = 0;
    // JSON_FILE is the name of the file where stored messages will be saved
    private static final String JSON_FILE = "messages.json";
    // sentMessages keeps the details of messages sent during the program's runtime
    private static final List<String> sentMessages = new ArrayList<>();

    public static void main(String[] args) {
        // Main menu for the QuickChat application, allowing users to send messages, view sent messages, or quit.
        System.out.println("Welcome to the QuickChat.");
        System.out.println("Please choose an option:");
        System.out.println("Option 1) Send Message");
        System.out.println("Option 2) Show recently sent messages (Coming Soon).);");
        System.out.println("Option 3) Quit");
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your option (numbers only): ");
        int option = scanner.nextInt();

        // Switch case to handle users selected option and run the necessary conditions for each case.

        switch (option) {
            case 1:
                // Case 1 allows the user to send a message by entering the message content, recipient's cell number and choosing to send, store, or disregard the message. It also generates a message ID and hash for each message.
                
                System.out.println("How many messages would you like to send?");
                int numMessages = scanner.nextInt();
                scanner.nextLine();
                for (int i = 0; i < numMessages; i++) {
                    System.out.print("Enter your message: ");
                    String message = scanner.nextLine();
                    System.out.println(validateMessage(message));
                    if (message.length() > 250) {
                        i--;
                        continue;
                    }

                    String messageID = generateMessageID();
                    System.out.println("Message ID generated: " + messageID);

                    String hash = createMessageHash(message, messageID);
                    System.out.println("Message Hash: " + hash);

                    String recipientNumber;
                    do {
                        System.out.println("Enter recipient's cell number (Should contain +27): ");
                         recipientNumber = scanner.nextLine();
                        System.out.println(checkRecipientCell(recipientNumber));
                    } while (!checkRecipientCell(recipientNumber).startsWith("Cell phone number successfully"));

                    System.out.println("What would you like to do with this message?");
                    System.out.println("1) Send Message");
                    System.out.println("2) Disregard Message");
                    System.out.println("3) Store Message to send later");
                    System.out.print("Choose an option: ");
                    int action = scanner.nextInt();
                    scanner.nextLine();

                    String result = sentMessage(message, messageID, hash, recipientNumber, action);
                    System.out.println(result);

                    if (action == 2) {
                        System.out.print("Enter 0 to confirm delete: ");
                        String confirm = scanner.nextLine();
                        if (confirm.equals("0")) {
                            System.out.println("Message deleted.");
                            i--;
                        } else {
                            System.out.println("Deletion cancelled.");
                        }
                    }
                }
                System.out.println("Total messages sent: " + returnTotalMessages());
                System.out.println("Sent Messages");
                System.out.println(printMessages());
                break;
            case 2:
                // Case 2 will show recently sent messages, but this feature is still under development and will display a coming soon message for now.
                System.out.println("Show recently sent messages (Coming Soon).");
                break;
            case 3:
                // Case 3 allows the user to quit the application, displaying a goodbye message before exiting.
                System.out.println("Quitting QuickChat. Goodbye!");
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
        scanner.close();
    }

    // checkMessageID()
    // Ensures the message ID is not more than 10 characters
    public static boolean checkMessageID(String messageID) {
        return messageID != null && messageID.length() <= 10 && messageID.matches("\\d+");
    }

    // checkRecipientCell()
    // Ensures cell number is no more than 10 chars and starts with +27
    public static String checkRecipientCell(String phonenum) {
        String regex = "^\\+27\\d{9}$";
        if (phonenum != null && phonenum.matches(regex)) {
            return "Cell phone number successfully captured.";
        } else {
            return "Cell phone number is incorrectly formatted. Please correct the number and try again.";
        }
    }

    // createMessageHash()
    // Creates and returns the Message Hash: XX:N:FIRSTLAST
    public static String createMessageHash(String message, String messageID) {
        messageCount++;
        String idPrefix = messageID.substring(0, Math.min(2, messageID.length()));
        String[] words = message.trim().split("\\s+");
        String firstWord = words[0].replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
        String lastWord = words[words.length - 1].replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
        return idPrefix + ":" + messageCount + ":" + firstWord + lastWord;
    }

    // sentMessage()
    // Allows user to choose: Send, Store, or Disregard the message
    public static String sentMessage(String message, String messageID, String hash, String recipient, int action) {
        switch (action) {
            case 1:
                sentMessages.add("ID: " + messageID + " | Hash: " + hash +
              " / To: " + recipient + " / Msg: " + message);
                return "Message successfully sent.";
            case 2:
                return "Press 0 to delete the message.";
            case 3:
                storeMessage(messageID, hash, message, recipient);
                return "Message successfully stored.";
            default:
                return "Invalid option. Message discarded.";
        }
    }

    // printMessages()
    // Returns all messages sent while the program is running
    public static String printMessages() {
        if (sentMessages.isEmpty()) {
            return "No messages sent yet.";
        }
        // Stringbuilder is used to efficiently join together all sent messages into a single string for display.
        StringBuilder sb = new StringBuilder();
        for (String msg : sentMessages) {
            sb.append(msg).append("\n");
        }
        return sb.toString().trim();
    }

    // returnTotalMessages()
    // Returns the total number of messages sent
    public static int returnTotalMessages() {
        return sentMessages.size();
    }

    // storeMessage()
    // Stores the message in a JSON file
    public static void storeMessage(String messageID, String hash, String message, String recipient) {
        String existing = "[]";
        File jsonFile = new File(JSON_FILE);
        if (jsonFile.exists()) {
            try {
                existing = new String(Files.readAllBytes(Paths.get(JSON_FILE))).trim();
                if (existing.isEmpty()) {
                    existing = "[]";
                }
            } catch (IOException e) {
                existing = "[]";
            }
        }

        String safeMessage = message.replace("\\", "\\\\").replace("\"", "\\\"");
        String safeRecipient = recipient.replace("\\", "\\\\").replace("\"", "\\\"");

        String entry = String.format(
                "{\"messageID\":\"%s\",\"hash\":\"%s\",\"message\":\"%s\",\"recipient\":\"%s\",\"status\":\"stored\"}",
                messageID, hash, safeMessage, safeRecipient
        );

        String updated = existing.equals("[]")
                ? "[" + entry + "]"
                : existing.substring(0, existing.lastIndexOf(']')) + "," + entry + "]";

        try (FileWriter writer = new FileWriter(JSON_FILE)) {
            writer.write(updated);
        } catch (IOException e) {
            System.out.println("Error saving message: " + e.getMessage());
        }
    }

    // generateMessageID()  internal helper
    public static String generateMessageID() {
        long timestamp = System.currentTimeMillis();
        String id = String.valueOf(timestamp % 10000000000L);
        while (id.length() < 10) {
            id = "0" + id;
        }
        return id.substring(id.length() - 10);
    }

    // validateMessage()
    // Returns success/failure string based on message length
    public static String validateMessage(String message) {
        if (message.length() <= 250) {
            return "Message ready to send.";
        } else {
            int excess = message.length() - 250;
            return "Message exceeds 250 characters by " + excess + "; please reduce the size.";
        }
    }

    // clearSentMessages()  used for test resets
    public static void clearSentMessages() {
        sentMessages.clear();
    }
}
