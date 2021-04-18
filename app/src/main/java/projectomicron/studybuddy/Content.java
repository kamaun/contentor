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
    private int treadMillMiles;
    private int treadMillMinutes;
    private int pushUps;
    private int sitUps;
    private int squats;

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
     * @param aTreadMillMiles the amount of miles ran on a treadmill
     * @param aTreadMillMinutes the amount of minutes the user ran on a treadmill
     * @param aPushUps the amount of push ups the user completed
     * @param aSitUps the amount of sit ups the user completed
     * @param aSquats the amount of squats the user completed
     */
    public Content(int aUserID, int aContentID, int aTreadMillMiles, int aTreadMillMinutes, int aPushUps,
                   int aSitUps, int aSquats) {
        this.userID = aUserID;
        this.contentID = aContentID;
        this.treadMillMiles = aTreadMillMiles;
        this.treadMillMinutes = aTreadMillMinutes;
        this.pushUps = aPushUps;
        this.sitUps = aSitUps;
        this.squats = aSquats;
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
     * Gets the tread mill miles run in a content.
     * @precondition treadMillMiles >= 0
     * @return the amount the miles ran on the treadmill
     */
    public int getTreadMillMiles() {
        return treadMillMiles;
    }

    /**
     * Gets the tread mill minutes in a content.
     * @precondition treadMillMinutes >= 0
     * @return the amount of minutes ran on the treadmill
     */
    public int getTreadMillMinutes() {
        return treadMillMinutes;
    }

    /**
     * Gets the push ups done in a content.
     * @precondition pushUps >= 0
     * @return the amount of push ups the user completed in the content
     */
    public int getPushUps() {
        return pushUps;
    }

    /**
     * Gets the sit ups done in a content.
     * @precondition sitUps >= 0
     * @return the amount of sit ups the user completed in the content
     */
    public int getSitUps() {
        return sitUps;
    }

    /**
     * Gets the squats done in a content.
     * @precondition squats >= 0
     * @return the amount of squats the user completed in the content
     */
    public int getSquats() {
        return squats;
    }
}
