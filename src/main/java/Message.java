// JSON handling (from json-simple library): used to parse and build the messages.json structure

import org.json.simple.JSONArray;              // Represents a JSON array of message objects
import org.json.simple.JSONObject;             // Represents a single JSON object (a message entry)
import org.json.simple.parser.JSONParser;       // Parses JSON text into JSONArray/JSONObject

// I/O classes for reading/writing the messages.json file and checking its existence

import java.io.File;                            // File path/metadata abstraction
import java.io.FileReader;                      // Reads characters from a file (used by JSONParser)
import java.io.FileWriter;                      // Writes characters to a file (used to save JSON)
import java.io.IOException;                     // Exception thrown by I/O operations
import java.nio.file.Files;                     // Utility methods like readAllBytes
import java.nio.file.Paths;                     // Convert a String path into a Path for Files

// Collections and input utilities used throughout the app

import java.util.ArrayList;                     // Concrete resizable list implementation
import java.util.List;                          // List interface used for field declarations
import java.util.Scanner;                       // Reads user input from System.in for the menu

public class Message { 

    // Tracks the number of messages created (used in hash generation)
    private static int messageCount = 0;

    // JSON file where stored messages are saved
    private static final String JSON_FILE = "messages.json";

    // These arrays store message data across the session, populated as messages are created.
    private static final List<String> sentMessages      = new ArrayList<>();
    private static final List<String> disregardMessages = new ArrayList<>();
    private static final List<String> storedMessages    = new ArrayList<>();
    private static final List<String> messageHashes     = new ArrayList<>();
    private static final List<String> messageIDs        = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to QuickChat.");

        boolean running = true;
        while (running) {
            System.out.println("Main Menu");
            System.out.println("1) Send Messages");
            System.out.println("2) Show Recently Sent Messages");
            System.out.println("3) Stored Messages");
            System.out.println("4) Quit");
            System.out.print("Enter your option: ");

            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1:
                    handleSendMessages(scanner);
                    break;
                case 2:
                    // Displays all messages sent during this session
                    System.out.println("Sent Messages");
                    System.out.println(printMessages());
                    System.out.println("Total messages sent: " + returnTotalMessages());
                    break;
                case 3:
                    // Stored Messages sub-menu
                    handleStoredMessagesMenu(scanner);
                    break;
                case 4:
                    System.out.println("Quitting QuickChat. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
        scanner.close();
    }

    // Case 1: Send Messages 
    // Allows the user to compose and send one or more messages.
    private static void handleSendMessages(Scanner scanner) {
        System.out.print("How many messages would you like to send? ");
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
            System.out.println("Message ID: " + messageID);

            String hash = createMessageHash(message, messageID);
            System.out.println("Message Hash: " + hash);

            String recipientNumber;
            do {
                System.out.print("Enter recipient cell number (+27...): ");
                recipientNumber = scanner.nextLine();
                System.out.println(checkRecipientCell(recipientNumber));
            } while (!checkRecipientCell(recipientNumber).startsWith("Cell phone number successfully"));

            System.out.println("What would you like to do?");
            System.out.println("1) Send Message");
            System.out.println("2) Disregard Message");
            System.out.println("3) Store Message");
            System.out.print("Choose: ");
            int action = scanner.nextInt();
            scanner.nextLine();

            String result = sentMessage(message, messageID, hash, recipientNumber, action);
            System.out.println(result);

            // Populate parallel arrays based on action chosen
            messageIDs.add(messageID);
            messageHashes.add(hash);

            if (action == 1) {
                sentMessages.add(message);
            } else if (action == 2) {
                System.out.print("Enter 0 to confirm disregard: ");
                String confirm = scanner.nextLine();
                if (confirm.equals("0")) {
                    disregardMessages.add(message);
                    System.out.println("Message disregarded.");
                    i--;
                } else {
                    System.out.println("Disregard cancelled.");
                }
            } else if (action == 3) {
                storedMessages.add(message);
            }
        }
    }

    //  Case 3: Stored Messages Sub-Menu 
    // Fourth main menu item for stored messages.
    private static void handleStoredMessagesMenu(Scanner scanner) {
        // Load stored messages from JSON into arrays before showing options
        loadStoredMessagesFromJSON();

        boolean back = false;
        while (!back) {
            System.out.println("Stored Messages Menu");
            System.out.println("a) Display all stored messages");
            System.out.println("b) Display the longest stored message");
            System.out.println("c) Search by Message ID");
            System.out.println("d) Search by recipient");
            System.out.println("e) Delete a message using its hash");
            System.out.println("f) Display full report");
            System.out.println("x) Back to main menu");
            System.out.print("Choose: ");
            String choice = scanner.nextLine().trim().toLowerCase();

            switch (choice) {
                case "a":
                    displayAllStoredMessages();
                    break;
                case "b":
                    System.out.println("Longest stored message:");
                    System.out.println(getLongestMessage());
                    break;
                case "c":
                    System.out.print("Enter Message ID to search: ");
                    String searchID = scanner.nextLine().trim();
                    System.out.println(searchByMessageID(searchID));
                    break;
                case "d":
                    System.out.print("Enter recipient number to search: ");
                    String recipient = scanner.nextLine().trim();
                    System.out.println(searchByRecipient(recipient));
                    break;
                case "e":
                    System.out.print("Enter message hash to delete: ");
                    String hash = scanner.nextLine().trim();
                    System.out.println(deleteMessageByHash(hash));
                    break;
                case "f":
                    System.out.println(displayReport());
                    break;
                case "x":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

   // loadStoredMessagesFromJSON()
    // Reads messages.json and populates the storedMessages, messageHashes, and messageIDs arrays.
    // Uses org.json.simple library as specified in the assignment.
    @SuppressWarnings("unchecked")
    public static void loadStoredMessagesFromJSON() {
        storedMessages.clear();
        messageHashes.clear();
        messageIDs.clear();

        File file = new File(JSON_FILE);
        if (!file.exists()) {
            System.out.println("No stored messages found.");
            return;
        }

        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(file)) {
            Object obj = parser.parse(reader);
            JSONArray array = (JSONArray) obj;

            for (Object item : array) {
                JSONObject msg = (JSONObject) item;
                storedMessages.add((String) msg.get("message"));
                messageHashes.add((String) msg.get("hash"));
                messageIDs.add((String) msg.get("messageID"));
            }
            System.out.println(array.size() + " stored message(s) loaded.");
        } catch (Exception e) {
            System.out.println("Error reading JSON: " + e.getMessage());
        }
    }

    // displayAllStoredMessages()
    // Displays the sender and recipient of all stored messages.
    public static void displayAllStoredMessages() {
        if (storedMessages.isEmpty()) {
            System.out.println("No stored messages to display.");
            return;
        }
        System.out.println("All Stored Messages:");
        for (int i = 0; i < storedMessages.size(); i++) {
            System.out.println("Message " + (i + 1) + ": " + storedMessages.get(i));
            if (i < messageIDs.size()) System.out.println("  ID: " + messageIDs.get(i));
            if (i < messageHashes.size()) System.out.println("  Hash: " + messageHashes.get(i));
        }
    }

    // getLongestMessage()
    // Searches all parallel arrays and returns the longest stored message.
    public static String getLongestMessage() {
        if (storedMessages.isEmpty()) {
            return "No stored messages available.";
        }
        String longest = "";
        for (String msg : storedMessages) {
            if (msg.length() > longest.length()) {
                longest = msg;
            }
        }
        return longest;
    }

    // searchByMessageID()
    // Searches the messageIDs array and returns the corresponding message.
    public static String searchByMessageID(String searchID) {
        for (int i = 0; i < messageIDs.size(); i++) {
            if (messageIDs.get(i).equals(searchID)) {
                return "Message found:\n  ID: " + messageIDs.get(i)
                        + "\n  Message: " + storedMessages.get(i);
            }
        }
        return "Message ID \"" + searchID + "\" not found.";
    }

    // searchByRecipient()
    // Reads the JSON file and returns all messages stored for a particular recipient.
    public static String searchByRecipient(String recipient) {
        File file = new File(JSON_FILE);
        if (!file.exists()) return "No stored messages found.";

        StringBuilder results = new StringBuilder();
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(file)) {
            JSONArray array = (JSONArray) parser.parse(reader);
            for (Object item : array) {
                JSONObject msg = (JSONObject) item;
                if (recipient.equals(msg.get("recipient"))) {
                    results.append("  Message: ").append(msg.get("message")).append("\n");
                }
            }
        } catch (Exception e) {
            return "Error reading file: " + e.getMessage();
        }

        if (results.length() == 0) {
            return "No messages found for recipient: " + recipient;
        }
        return "Messages for " + recipient + ":\n" + results.toString().trim();
    }

    // deleteMessageByHash()
    // Searches the JSON file for a matching hash, removes that entry, and rewrites the file.
    @SuppressWarnings("unchecked")
    public static String deleteMessageByHash(String hash) {
        File file = new File(JSON_FILE);
        if (!file.exists()) return "No stored messages found.";

        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(file)) {
            JSONArray array = (JSONArray) parser.parse(reader);
            JSONObject toRemove = null;
            String deletedMessage = "";

            for (Object item : array) {
                JSONObject msg = (JSONObject) item;
                if (hash.equals(msg.get("hash"))) {
                    toRemove = msg;
                    deletedMessage = (String) msg.get("message");
                    break;
                }
            }

            if (toRemove == null) {
                return "Hash \"" + hash + "\" not found. No message deleted.";
            }

            array.remove(toRemove);

            // Rewrite the JSON file with the entry removed
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(array.toJSONString());
            }

            // Also remove from in-memory arrays if present
            int idx = messageHashes.indexOf(hash);
            if (idx >= 0) {
                storedMessages.remove(idx);
                messageHashes.remove(idx);
                messageIDs.remove(idx);
            }

            return "Message: \"" + deletedMessage + "\" successfully deleted.";

        } catch (Exception e) {
            return "Error deleting message: " + e.getMessage();
        }
    }

    // displayReport()
    // Returns a formatted report showing hash, recipient, and message for all stored messages.
    public static String displayReport() {
        File file = new File(JSON_FILE);
        if (!file.exists()) return "No stored messages to report.";

        StringBuilder report = new StringBuilder();
        report.append("\n========== Message Report ==========\n");

        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(file)) {
            JSONArray array = (JSONArray) parser.parse(reader);
            if (array.isEmpty()) return "No stored messages to report.";

            int count = 1;
            for (Object item : array) {
                JSONObject msg = (JSONObject) item;
                report.append("--- Message ").append(count++).append(" ---\n");
                report.append("  Hash:      ").append(msg.get("hash")).append("\n");
                report.append("  Recipient: ").append(msg.get("recipient")).append("\n");
                report.append("  Message:   ").append(msg.get("message")).append("\n");
            }
        } catch (Exception e) {
            return "Error generating report: " + e.getMessage();
        }

        report.append("====================================");
        return report.toString();
    }


    // checkMessageID()
    // Returns true if the message ID is numeric and no longer than 10 characters.
    public static boolean checkMessageID(String messageID) {
        return messageID != null && messageID.length() <= 10 && messageID.matches("\\d+");
    }

    // checkRecipientCell()
    // Validates that the phone number starts with +27 and has exactly 9 digits after it.
    public static String checkRecipientCell(String phonenum) {
        String regex = "^\\+27\\d{9}$";
        if (phonenum != null && phonenum.matches(regex)) {
            return "Cell phone number successfully captured.";
        } else {
            return "Cell phone number is incorrectly formatted. Please correct the number and try again.";
        }
    }

    // createMessageHash()
    // Builds the hash in the format XX:N:FIRSTLAST using the message ID prefix and message words.
    public static String createMessageHash(String message, String messageID) {
        messageCount++;
        String idPrefix = messageID.substring(0, Math.min(2, messageID.length()));
        String[] words = message.trim().split("\\s+");
        String firstWord = words[0].replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
        String lastWord = words[words.length - 1].replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
        return idPrefix + ":" + messageCount + ":" + firstWord + lastWord;
    }

    // sentMessage()
    // Handles the user's chosen action: send, disregard, or store the message.
    public static String sentMessage(String message, String messageID, String hash,
                                     String recipient, int action) {
        switch (action) {
            case 1:
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
    // Returns all sent messages as a formatted string.
    public static String printMessages() {
        if (sentMessages.isEmpty()) {
            return "No messages sent yet.";
        }
        StringBuilder sb = new StringBuilder();
        for (String msg : sentMessages) {
            sb.append(msg).append("\n");
        }
        return sb.toString().trim();
    }

    // returnTotalMessages()
    // Returns the number of sent messages in this session.
    public static int returnTotalMessages() {
        return sentMessages.size();
    }

    // storeMessage()
    // Appends a new message entry to messages.json.
    @SuppressWarnings("unchecked")
    public static void storeMessage(String messageID, String hash,
                                    String message, String recipient) {
        String existing = "[]";
        File jsonFile = new File(JSON_FILE);
        if (jsonFile.exists()) {
            try {
                existing = new String(Files.readAllBytes(Paths.get(JSON_FILE))).trim();
                if (existing.isEmpty()) existing = "[]";
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

    // generateMessageID()
    // Generates a unique 10-digit numeric ID based on the current timestamp.
    public static String generateMessageID() {
        long timestamp = System.currentTimeMillis();
        String id = String.valueOf(timestamp % 10000000000L);
        while (id.length() < 10) id = "0" + id;
        return id.substring(id.length() - 10);
    }

    // validateMessage()
    // Returns success if message is within 250 characters; otherwise reports the excess.
    public static String validateMessage(String message) {
        if (message.length() <= 250) {
            return "Message ready to send.";
        } else {
            int excess = message.length() - 250;
            return "Message exceeds 250 characters by " + excess + "; please reduce the size.";
        }
    }

    // clearSentMessages()
    // Clears all in-memory lists. Used to reset state between unit tests.
    public static void clearSentMessages() {
        sentMessages.clear();
        disregardMessages.clear();
        storedMessages.clear();
        messageHashes.clear();
        messageIDs.clear();
    }

    // Test Helpers 

    // populateTestData()
    // Loads the 5 required test messages into arrays. Used by unit tests only.
    public static void populateTestData() {
        clearSentMessages();

        // Message 1 — Sent
        sentMessages.add("Did you get the cake?");
        messageIDs.add("0000000001");
        messageHashes.add("00:1:DIDCAKE");

        // Message 2 — Stored
        storedMessages.add("Where are you? You are late! I have asked you to be on time.");
        messageIDs.add("0000000002");
        messageHashes.add("00:2:WHERETIME");

        // Message 3 — Disregarded
        disregardMessages.add("Yohoooo, I am at your gate.");
        messageIDs.add("0000000003");
        messageHashes.add("00:3:YOHOOOGATE");

        // Message 4 — Sent (developer recipient)
        sentMessages.add("It is dinner time !");
        messageIDs.add("0838884567");
        messageHashes.add("08:4:ITTIME");

        // Message 5 — Stored
        storedMessages.add("Ok, I am leaving without you.");
        messageIDs.add("0000000005");
        messageHashes.add("00:5:OKYOU");
    }

    // Getters for test access
    public static List<String> getSentMessages()      { return sentMessages; }
    public static List<String> getStoredMessages()    { return storedMessages; }
    public static List<String> getDisregardMessages() { return disregardMessages; }
    public static List<String> getMessageHashes()     { return messageHashes; }
    public static List<String> getMessageIDs()        { return messageIDs; }
}