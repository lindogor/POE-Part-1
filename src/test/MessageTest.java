import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;

// These tests check whether the Message class behaves correctly.
public class MessageTest {

    @Before
    public void resetState() throws Exception {
        // Reset shared state before each test so tests do not affect each other.
        Field field = Message.class.getDeclaredField("messageCount");
        field.setAccessible(true);
        field.setInt(null, 0);
        Message.clearSentMessages();
        new File("messages.json").delete();
    }

    @After
    public void cleanup() {
        // Remove the JSON file after each test to keep the workspace clean.
        new File("messages.json").delete();
    }

    
    // These tests check if the message length rule works.
    // validateMessage() — message length <= 250 chars
    
    @Test
    public void testMessageLength_valid_returnsSuccess() {
        String msg = "Hi Mike, can you join us for dinner tonight?";
        assertEquals("Message ready to send.", Message.validateMessage(msg));
    }

    @Test
    public void testMessageLength_exceeds250_returnsFailure() {
        String msg = "A".repeat(251);
        String result = Message.validateMessage(msg);
        assertTrue("Should report excess characters",
                result.startsWith("Message exceeds 250 characters by"));
    }

    @Test
    public void testMessageLength_exactly250_returnsSuccess() {
        String msg = "A".repeat(250);
        assertEquals("Message ready to send.", Message.validateMessage(msg));
    }

    @Test
    public void testMessageLength_reportsCorrectExcess() {
        String msg = "A".repeat(260);
        assertTrue(Message.validateMessage(msg).contains("10"));
    }

    
    // These tests check if a phone number is entered in the correct format.
    // checkRecipientCell()
    

    @Test
    public void testRecipientCell_valid_returnsSuccess() {
        assertEquals("Cell phone number successfully captured.",
                Message.checkRecipientCell("+27718693002"));
    }

    @Test
    public void testRecipientCell_noInternationalCode_returnsFailure() {
        // Test data message 2: 08575975889 — no +27
        String result = Message.checkRecipientCell("08575975889");
        assertTrue("Should report formatting error",
                result.startsWith("Cell phone number is incorrectly formatted"));
    }

    @Test
    public void testRecipientCell_wrongCode_returnsFailure() {
        String result = Message.checkRecipientCell("+1234567890");
        assertTrue(result.startsWith("Cell phone number is incorrectly formatted"));
    }

    @Test
    public void testRecipientCell_empty_returnsFailure() {
        String result = Message.checkRecipientCell("");
        assertTrue(result.startsWith("Cell phone number is incorrectly formatted"));
    }

    @Test
    public void testRecipientCell_tooShort_returnsFailure() {
        String result = Message.checkRecipientCell("+2771869");
        assertTrue(result.startsWith("Cell phone number is incorrectly formatted"));
    }

    
    // These tests check that the hash is created in the right format.
    // createMessageHash() — format XX:N:FIRSTLAST
    

    @Test
    public void testMessageHash_formatIsCorrect() {
        // Test Case 1: "Hi Mike, can you join us for dinner tonight?"  ID auto
        String messageID = Message.generateMessageID();
        String hash = Message.createMessageHash("Hi Mike, can you join us for dinner tonight?", messageID);
        assertTrue("Hash must match pattern XX:N:WORD", hash.matches("[0-9]{2}:[0-9]+:[A-Z0-9]+"));
    }

    @Test
    public void testMessageHash_isAllUpperCase() {
        String messageID = Message.generateMessageID();
        String hash = Message.createMessageHash("hi mike tonight", messageID);
        assertEquals(hash, hash.toUpperCase());
    }

    @Test
    public void testMessageHash_firstAndLastWord() {
        String hash = Message.createMessageHash("Hi Mike, can you join us for dinner tonight?", "0012345678");
        assertTrue("Hash should end with HITONIGHT", hash.endsWith(":HITONIGHT"));
    }

    @Test
    public void testMessageHash_singleWord_repeats() {
        String hash = Message.createMessageHash("Hello", "0012345678");
        assertTrue("Single word should repeat", hash.endsWith(":HELLOHELLO"));
    }

    @Test
    public void testMessageHash_incrementsCount() {
        String id = Message.generateMessageID();
        String h1 = Message.createMessageHash("Hi there", id);
        String h2 = Message.createMessageHash("Bye world", id);
        int n1 = Integer.parseInt(h1.split(":")[1]);
        int n2 = Integer.parseInt(h2.split(":")[1]);
        assertEquals(1, n2 - n1);
    }

    @Test
    public void testMessageHash_stripsSpecialChars() {
        String hash = Message.createMessageHash("Hi Mike, can you join us for dinner tonight?", "0012345678");
        assertFalse("Hash should not contain commas", hash.contains(","));
        assertFalse("Hash should not contain question marks", hash.contains("?"));
    }

   
    // These tests check whether the generated message ID is valid.
    // checkMessageID()
    

    @Test
    public void testCheckMessageID_generatedID_isValid() {
        String id = Message.generateMessageID();
        assertTrue("Generated ID should pass check", Message.checkMessageID(id));
        System.out.println("Message ID generated: " + id);
    }

    @Test
    public void testCheckMessageID_tooLong_returnsFalse() {
        assertFalse(Message.checkMessageID("12345678901")); // 11 digits
    }

    @Test
    public void testCheckMessageID_withLetters_returnsFalse() {
        assertFalse(Message.checkMessageID("12345ABC90"));
    }

    @Test
    public void testCheckMessageID_empty_returnsFalse() {
        assertFalse(Message.checkMessageID(""));
    }

    
    // These tests check what happens when the user chooses to send, store, or discard a message.
    // sentMessage() — Send / Disregard / Store actions
    

    @Test
    public void testSentMessage_sendAction_returnsSuccess() {
        String result = Message.sentMessage(
            "Hi Mike, can you join us for dinner tonight?",
            "1234567890", "12:1:HITONIGHT", "+27718693002", 1);
        assertEquals("Message successfully sent.", result);
    }

    @Test
    public void testSentMessage_disregardAction_returnsDeletePrompt() {
        String result = Message.sentMessage(
            "Hi Keegan, did you receive the payment?",
            "1234567890", "12:1:HIPAYMENT", "08575975889", 2);
        assertEquals("Press 0 to delete the message.", result);
    }

    @Test
    public void testSentMessage_storeAction_returnsStoredMessage() {
        String result = Message.sentMessage(
            "Hi Mike, can you join us for dinner tonight?",
            "1234567890", "12:1:HITONIGHT", "+27718693002", 3);
        assertEquals("Message successfully stored.", result);
    }

    
    // These tests check how many messages are currently in memory.
    // returnTotalMessages()
    

    @Test
    public void testReturnTotalMessages_afterTwoSent_returnsTwo() {
        Message.sentMessage("Hi Mike, can you join us for dinner tonight?",
            "1111111111", "11:1:HITONIGHT", "+27718693002", 1);
        Message.sentMessage("Hi Keegan, did you receive the payment?",
            "2222222222", "22:2:HIPAYMENT", "+27831234567", 1);
        assertEquals(2, Message.returnTotalMessages());
    }

    @Test
    public void testReturnTotalMessages_noMessagesSent_returnsZero() {
        assertEquals(0, Message.returnTotalMessages());
    }

  
    // These tests check what is shown when the app prints sent messages.
    // printMessages()
   

    @Test
    public void testPrintMessages_containsSentMessage() {
        Message.sentMessage("Hi Mike, can you join us for dinner tonight?",
            "1111111111", "11:1:HITONIGHT", "+27718693002", 1);
        String output = Message.printMessages();
        assertTrue("printMessages should contain the message text",
                output.contains("Hi Mike, can you join us for dinner tonight?"));
    }

    @Test
    public void testPrintMessages_noMessages_returnsNotice() {
        assertEquals("No messages sent yet.", Message.printMessages());
    }

    
    // These tests check that messages are saved correctly into the JSON file.
    // storeMessage() / JSON storage
    

    @Test
    public void testStoreMessage_createsFile() {
        Message.storeMessage("1234567890", "12:1:HITONIGHT",
            "Hi Mike, can you join us for dinner tonight?", "+27718693002");
        assertTrue(new File("messages.json").exists());
    }

    @Test
    public void testStoreMessage_containsCorrectFields() throws IOException {
        Message.storeMessage("1234567890", "12:1:HITONIGHT",
            "Hi Mike, can you join us for dinner tonight?", "+27718693002");
        String content = new String(Files.readAllBytes(Paths.get("messages.json")));
        assertTrue(content.contains("1234567890"));
        assertTrue(content.contains("12:1:HITONIGHT"));
        assertTrue(content.contains("+27718693002"));
        assertTrue(content.contains("\"status\":\"stored\""));
    }

    @Test
    public void testStoreMessage_appendsMultipleEntries() throws IOException {
        Message.storeMessage("1111111111", "11:1:HITONIGHT",
            "Hi Mike, can you join us for dinner tonight?", "+27718693002");
        Message.storeMessage("2222222222", "22:2:HIPAYMENT",
            "Hi Keegan, did you receive the payment?", "+27831234567");
        String content = new String(Files.readAllBytes(Paths.get("messages.json")));
        assertTrue(content.contains("1111111111"));
        assertTrue(content.contains("2222222222"));
    }
}