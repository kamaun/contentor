package projectomicron.studybuddy;

/**
 * This class represents the contents of a content.
 * Created by Jaime Andrews on 4/09/2021.
 */
public class Content {
    /**
     * Instance variables only visible in the Content. These represent the contents of what a
     * content would contain.
     */
    private int userID;
    private int contentID;
    private String contentText;


    /**
     * Constructs a Content object.
     * @precondition aContentID > 0
     * @precondition aTreadMillMiles >= 0
     * @precondition aTreadMillMinutes >= 0
     * @precondition aPushUps >= 0
     * @precondition aSitUps >= 0
     * @precondition aSquats >= 0
     * @param aUserID the user id of the user ho is associated with the content
     * @param aContentID the content id of the content
     */
    public Content(int aUserID, int aContentID, String aContentText) {
        this.userID = aUserID;
        this.contentID = aContentID;
        this.contentText = aContentText;
    }

    /**
     * Gets the user id of the user
     * @precondition userID > 0
     * @return the user id of the user
     */
    public int getUserID() {
        return userID;
    }

    /**
     * Gets the content id of the content.
     * @precondition contentID >= 0
     * @return the content ID of the content
     */
    public int getContentID() {
        return contentID;
    }

    /**
     * Gets created content
     * @return the content in string format
     */
    public String getContentText() {
        return contentText;
    }
}
