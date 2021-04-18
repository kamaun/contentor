package projectomicron.studybuddy;

/**
 * This class represents a text message object that contains the contents of a text message.
 * Created by Whitney Andrews on 4/4/2021.
 */
public class TextMessage {
    /**
     * Instance variables on visible in the TextMessage class.
     */
    private int messageID;
    //The userID is the user who receives a text message and who the current user logged in
    private int userID;
    //The senderID is the user who sends a message to the user who is logged in
    private int senderID;
    private String messageText;
    private String dateTime;

    /**
     * Constructs a TextMessage object.
     * @precondition aMessageID > 0
     * @precondition aUserID > 0
     * @precondition aSenderID > 0
     * @precondition aMessageText.size() > 0
     * @precondition aDateTime.size() > 0
     */
    public TextMessage(int aMessageID, int aUserID, int aSenderID, String aMessageText,
                       String aDateTime) {
        this.messageID = aMessageID;
        this.userID = aUserID;
        this.senderID = aSenderID;
        this.messageText = aMessageText;
        this.dateTime = aDateTime;
    }

    /**
     * Gets the message id of the text message.
     * @precondition messageID > 0
     */
    public int getMessageID() {
        return messageID;
    }

    /**
     * Gets the user id of the user that received the text message.
     * @precondition userID > 0
     */
    public int getUserID() {
        return userID;
    }

    /**
     * Gets the sender id of the user that sent the text message
     * @precondition senderID > 0
     */
    public int getSenderID() {
        return senderID;
    }

    /**
     * Gets the text message text of the text message.
     * @precondition messageText.size() > 0
     */
    public String getMessageText() {
        return messageText;
    }

    /**
     * Gets the date and time the text message was sent
     * @precondition dateTime.size() > 0
     */
    public String getDateTime() {
        return dateTime;
    }
}
