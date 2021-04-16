package projectomicron.mrfitness;

/**
 * This class represents the contents of a workout.
 * Created by Emanuel Guerrero on 12/1/2015.
 */
public class WorkOut {
    /**
     * Instance variables only visible in the WorkOut. These represent the contents of what a
     * workout would contain.
     */
    private int userID;
    private int workOutID;
    private int treadMillMiles;
    private int treadMillMinutes;
    private int pushUps;
    private int sitUps;
    private int squats;

    /**
     * Constructs a WorkOut object.
     * @precondition aWorkOutID > 0
     * @precondition aTreadMillMiles >= 0
     * @precondition aTreadMillMinutes >= 0
     * @precondition aPushUps >= 0
     * @precondition aSitUps >= 0
     * @precondition aSquats >= 0
     * @param aUserID the user id of the user ho is associated with the workout
     * @param aWorkOutID the workout id of the workout
     * @param aTreadMillMiles the amount of miles ran on a treadmill
     * @param aTreadMillMinutes the amount of minutes the user ran on a treadmill
     * @param aPushUps the amount of push ups the user completed
     * @param aSitUps the amount of sit ups the user completed
     * @param aSquats the amount of squats the user completed
     */
    public WorkOut(int aUserID, int aWorkOutID, int aTreadMillMiles, int aTreadMillMinutes, int aPushUps,
                   int aSitUps, int aSquats) {
        this.userID = aUserID;
        this.workOutID = aWorkOutID;
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
     * Gets the workout id of the workout.
     * @precondition workOutID >= 0
     * @return the workout ID of the workout
     */
    public int getWorkOutID() {
        return workOutID;
    }

    /**
     * Gets the tread mill miles run in a workout.
     * @precondition treadMillMiles >= 0
     * @return the amount the miles ran on the treadmill
     */
    public int getTreadMillMiles() {
        return treadMillMiles;
    }

    /**
     * Gets the tread mill minutes in a workout.
     * @precondition treadMillMinutes >= 0
     * @return the amount of minutes ran on the treadmill
     */
    public int getTreadMillMinutes() {
        return treadMillMinutes;
    }

    /**
     * Gets the push ups done in a workout.
     * @precondition pushUps >= 0
     * @return the amount of push ups the user completed in the workout
     */
    public int getPushUps() {
        return pushUps;
    }

    /**
     * Gets the sit ups done in a workout.
     * @precondition sitUps >= 0
     * @return the amount of sit ups the user completed in the workout
     */
    public int getSitUps() {
        return sitUps;
    }

    /**
     * Gets the squats done in a workout.
     * @precondition squats >= 0
     * @return the amount of squats the user completed in the workout
     */
    public int getSquats() {
        return squats;
    }
}
