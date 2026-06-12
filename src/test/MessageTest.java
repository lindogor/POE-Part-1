import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

// Unit tests for the Message class covering Part 1, 2, and 3 requirements.
public class MessageTest {

    @Before
    public void resetState() throws Exception {
        // Reset messageCount and clear all arrays before each test.
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

    //  Helper: write a known messages.json for tests that need it 
    private void writeTestJSON() throws IOException {
        String json = "["
            + "{\"messageID\":\"0000000001\",\"hash\":\"00:1:DIDCAKE\",\"message\":\"Did you get the cake?\",\"recipient\":\"+27834557896\",\"status\":\"stored\"},"
            + "{\"messageID\":\"0000000002\",\"hash\":\"00:2:WHERETIME\",\"message\":\"Where are you? You are late! I have asked you to be on time.\",\"recipient\":\"+27838884567\",\"status\":\"stored\"},"
            + "{\"messageID\":\"0000000003\",\"hash\":\"00:3:YOHOOOGATE\",\"message\":\"Yohoooo, I am at your gate.\",\"recipient\":\"+27834484567\",\"status\":\"stored\"},"
            + "{\"messageID\":\"0838884567\",\"hash\":\"08:4:ITTIME\",\"message\":\"It is dinner time !\",\"recipient\":\"0838884567\",\"status\":\"stored\"},"
            + "{\"messageID\":\"0000000005\",\"hash\":\"00:5:OKYOU\",\"message\":\"Ok, I am leaving without you.\",\"recipient\":\"+27838884567\",\"status\":\"stored\"}"
            + "]";
        try (FileWriter fw = new FileWriter("messages.json")) {
            fw.write(json);
        }
    }

    
    // PART 3 — ARRAYS CORRECTLY POPULATED
    

    @Test
    public void testSentMessagesArray_correctlyPopulated() {
        // Test Data: Developer entry for messages 1 and 4 (both Sent)
        // The system returns: "Did you get the cake?", "It is dinner time !"
        Message.populateTestData();
        List<String> sent = Message.getSentMessages();
        assertTrue("Sent array should contain message 1", sent.contains("Did you get the cake?"));
        assertTrue("Sent array should contain message 4", sent.contains("It is dinner time !"));
    }

    @Test
    public void testDisregardedMessagesArray_correctlyPopulated() {
        // Message 3 has flag Disregard — should appear in disregard array
        Message.populateTestData();
        List<String> disregarded = Message.getDisregardMessages();
        assertTrue("Disregard array should contain message 3",
                disregarded.contains("Yohoooo, I am at your gate."));
    }

    @Test
    public void testStoredMessagesArray_correctlyPopulated() {
        // Messages 2 and 5 have flag Stored — both should appear
        Message.populateTestData();
        List<String> stored = Message.getStoredMessages();
        assertTrue("Stored array should contain message 2",
                stored.contains("Where are you? You are late! I have asked you to be on time."));
        assertTrue("Stored array should contain message 5",
                stored.contains("Ok, I am leaving without you."));
    }

    @Test
    public void testMessageHashArray_correctlyPopulated() {
        Message.populateTestData();
        List<String> hashes = Message.getMessageHashes();
        assertFalse("Hash array should not be empty", hashes.isEmpty());
        assertTrue("Hash array should contain message 1 hash", hashes.contains("00:1:DIDCAKE"));
    }

    @Test
    public void testMessageIDArray_correctlyPopulated() {
        Message.populateTestData();
        List<String> ids = Message.getMessageIDs();
        assertFalse("ID array should not be empty", ids.isEmpty());
        // Message 4 uses developer number as ID
        assertTrue("ID array should contain message 4 ID", ids.contains("0838884567"));
    }

    
    // PART 3 — DISPLAY LONGEST MESSAGE
    

    @Test
    public void testDisplayLongestMessage_returnsCorrectMessage() {
        // Test Data: messages 1-4
        // Expected: "Where are you? You are late! I have asked you to be on time."
        Message.populateTestData();
        String longest = Message.getLongestMessage();
        assertEquals(
            "Where are you? You are late! I have asked you to be on time.",
            longest
        );
    }

    @Test
    public void testDisplayLongestMessage_noMessages_returnsNotice() {
        String result = Message.getLongestMessage();
        assertEquals("No stored messages available.", result);
    }

    
    // PART 3 — SEARCH BY MESSAGE ID
    

    @Test
    public void testSearchByMessageID_found_returnsRecipientAndMessage() {
        // Test Data: message 4 — ID "0838884567"
        // Expected: "It is dinner time !"
        Message.populateTestData();
        String result = Message.searchByMessageID("0838884567");
        assertTrue("Should contain the message text", result.contains("It is dinner time !"));
        assertTrue("Should contain the message ID", result.contains("0838884567"));
    }

    @Test
    public void testSearchByMessageID_notFound_returnsNotFoundMessage() {
        Message.populateTestData();
        String result = Message.searchByMessageID("9999999999");
        assertTrue("Should report not found", result.contains("not found"));
    }

    
    // PART 3 — SEARCH BY RECIPIENT


    @Test
    public void testSearchByRecipient_returnsAllMatchingMessages() throws IOException {
        // Test Data: +27838884567
        // Expected: messages 2 and 5
        writeTestJSON();
        String result = Message.searchByRecipient("+27838884567");
        assertTrue("Should return message 2", result.contains("Where are you? You are late!"));
        assertTrue("Should return message 5", result.contains("Ok, I am leaving without you."));
    }

    @Test
    public void testSearchByRecipient_noMatch_returnsNotFoundMessage() throws IOException {
        writeTestJSON();
        String result = Message.searchByRecipient("+27000000000");
        assertTrue("Should report no messages found", result.contains("No messages found"));
    }

    // PART 3 — DELETE MESSAGE BY HASH
    

    @Test
    public void testDeleteMessageByHash_success_returnsConfirmation() throws IOException {
        // Test Data: Test Message 2 — hash "00:2:WHERETIME"
        // Expected: "Where are you? You are late! I have asked you to be on time." successfully deleted
        writeTestJSON();
        String result = Message.deleteMessageByHash("00:2:WHERETIME");
        assertTrue("Should confirm deletion", result.contains("successfully deleted"));
        assertTrue("Should mention the correct message",
                result.contains("Where are you? You are late!"));
    }

    @Test
    public void testDeleteMessageByHash_removesFromJSONFile() throws IOException {
        writeTestJSON();
        Message.deleteMessageByHash("00:2:WHERETIME");
        String fileContent = new String(Files.readAllBytes(Paths.get("messages.json")));
        assertFalse("Deleted message should not be in the file",
                fileContent.contains("Where are you? You are late!"));
    }

    @Test
    public void testDeleteMessageByHash_hashNotFound_returnsFailureMessage() throws IOException {
        writeTestJSON();
        String result = Message.deleteMessageByHash("XX:99:FAKEHASH");
        assertTrue("Should report hash not found", result.contains("not found"));
    }

    
    // PART 3 — READ JSON FILE INTO ARRAY


    @Test
    public void testLoadStoredMessagesFromJSON_populatesArrays() throws IOException {
        writeTestJSON();
        Message.loadStoredMessagesFromJSON();
        List<String> stored = Message.getStoredMessages();
        assertFalse("Stored array should not be empty after loading JSON", stored.isEmpty());
        assertTrue("Should contain message 2 text",
                stored.contains("Where are you? You are late! I have asked you to be on time."));
    }

    @Test
    public void testLoadStoredMessagesFromJSON_populatesHashArray() throws IOException {
        writeTestJSON();
        Message.loadStoredMessagesFromJSON();
        List<String> hashes = Message.getMessageHashes();
        assertTrue("Hash array should contain loaded hashes", hashes.contains("00:2:WHERETIME"));
    }

    @Test
    public void testLoadStoredMessagesFromJSON_noFile_doesNotCrash() {
        // No file present — should handle gracefully
        new File("messages.json").delete();
        Message.loadStoredMessagesFromJSON(); // should not throw
        assertTrue("Stored array should be empty", Message.getStoredMessages().isEmpty());
    }

    
    // PART 3 — DISPLAY REPORT
    

    @Test
    public void testDisplayReport_containsHashRecipientAndMessage() throws IOException {
        writeTestJSON();
        String report = Message.displayReport();
        assertTrue("Report should contain hash",      report.contains("Hash:"));
        assertTrue("Report should contain recipient", report.contains("Recipient:"));
        assertTrue("Report should contain message",   report.contains("Message:"));
    }

    @Test
    public void testDisplayReport_containsAllStoredMessages() throws IOException {
        writeTestJSON();
        String report = Message.displayReport();
        assertTrue("Report should include message 1", report.contains("Did you get the cake?"));
        assertTrue("Report should include message 2", report.contains("Where are you?"));
    }

    @Test
    public void testDisplayReport_noStoredMessages_returnsNotice() {
        String report = Message.displayReport();
        assertTrue("Should report no messages", report.contains("No stored messages"));
    }

    // PART 1 & 2 — EXISTING TESTS 

    @Test
    public void testMessageLength_valid_returnsSuccess() {
        assertEquals("Message ready to send.",
                Message.validateMessage("Hi Mike, can you join us for dinner tonight?"));
    }

    @Test
    public void testMessageLength_exceeds250_returnsFailure() {
        assertTrue(Message.validateMessage("A".repeat(251)).startsWith("Message exceeds 250 characters by"));
    }

    @Test
    public void testMessageLength_exactly250_returnsSuccess() {
        assertEquals("Message ready to send.", Message.validateMessage("A".repeat(250)));
    }

    @Test
    public void testMessageLength_reportsCorrectExcess() {
        assertTrue(Message.validateMessage("A".repeat(260)).contains("10"));
    }

    @Test
    public void testRecipientCell_valid_returnsSuccess() {
        assertEquals("Cell phone number successfully captured.",
                Message.checkRecipientCell("+27718693002"));
    }

    @Test
    public void testRecipientCell_noInternationalCode_returnsFailure() {
        assertTrue(Message.checkRecipientCell("08575975889")
                .startsWith("Cell phone number is incorrectly formatted"));
    }

    @Test
    public void testRecipientCell_wrongCode_returnsFailure() {
        assertTrue(Message.checkRecipientCell("+1234567890")
                .startsWith("Cell phone number is incorrectly formatted"));
    }

    @Test
    public void testRecipientCell_empty_returnsFailure() {
        assertTrue(Message.checkRecipientCell("")
                .startsWith("Cell phone number is incorrectly formatted"));
    }

    @Test
    public void testMessageHash_formatIsCorrect() {
        String id = Message.generateMessageID();
        String hash = Message.createMessageHash("Hi Mike, can you join us for dinner tonight?", id);
        assertTrue(hash.matches("[0-9]{2}:[0-9]+:[A-Z0-9]+"));
    }

    @Test
    public void testMessageHash_firstAndLastWord() {
        String hash = Message.createMessageHash("Hi Mike, can you join us for dinner tonight?", "0012345678");
        assertTrue(hash.endsWith(":HITONIGHT"));
    }

    @Test
    public void testMessageHash_singleWord_repeats() {
        String hash = Message.createMessageHash("Hello", "0012345678");
        assertTrue(hash.endsWith(":HELLOHELLO"));
    }

    @Test
    public void testCheckMessageID_generatedID_isValid() {
        assertTrue(Message.checkMessageID(Message.generateMessageID()));
    }

    @Test
    public void testCheckMessageID_tooLong_returnsFalse() {
        assertFalse(Message.checkMessageID("12345678901"));
    }

    @Test
    public void testSentMessage_sendAction_returnsSuccess() {
        assertEquals("Message successfully sent.",
                Message.sentMessage("Hi Mike!", "1234567890", "12:1:HIHI", "+27718693002", 1));
    }

    @Test
    public void testSentMessage_storeAction_returnsStoredMessage() {
        assertEquals("Message successfully stored.",
                Message.sentMessage("Hi Mike!", "1234567890", "12:1:HIHI", "+27718693002", 3));
    }

    @Test
    public void testReturnTotalMessages_afterTwoSent_returnsTwo() {
        Message.sentMessage("Msg 1", "1111111111", "11:1:MSGMSG", "+27718693002", 1);
        Message.sentMessage("Msg 2", "2222222222", "22:2:MSGMSG", "+27831234567", 1);
        assertEquals(2, Message.returnTotalMessages());
    }

    @Test
    public void testReturnTotalMessages_noMessages_returnsZero() {
        assertEquals(0, Message.returnTotalMessages());
    }

    @Test
    public void testPrintMessages_containsSentMessage() {
        Message.sentMessage("Hi Mike!", "1111111111", "11:1:HIHI", "+27718693002", 1);
        assertTrue(Message.printMessages().contains("Hi Mike!"));
    }

    @Test
    public void testPrintMessages_noMessages_returnsNotice() {
        assertEquals("No messages sent yet.", Message.printMessages());
    }

    @Test
    public void testStoreMessage_createsFile() {
        Message.storeMessage("1234567890", "12:1:HITONIGHT",
                "Hi Mike!", "+27718693002");
        assertTrue(new File("messages.json").exists());
    }

    @Test
    public void testStoreMessage_containsCorrectFields() throws IOException {
        Message.storeMessage("1234567890", "12:1:HITONIGHT",
                "Hi Mike!", "+27718693002");
        String content = new String(Files.readAllBytes(Paths.get("messages.json")));
        assertTrue(content.contains("1234567890"));
        assertTrue(content.contains("12:1:HITONIGHT"));
        assertTrue(content.contains("+27718693002"));
        assertTrue(content.contains("\"status\":\"stored\""));
    }
}