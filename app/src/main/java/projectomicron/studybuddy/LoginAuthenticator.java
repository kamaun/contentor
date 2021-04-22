package projectomicron.studybuddy;

import android.content.Context;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * This class handles the login authentication of the user and stores basic user information needed
 * throughout the lifetime of the application.
 * Created by Kevin Anderson on 4/9/2021.
 */
public class LoginAuthenticator {
    /**
     * Instance variables only visible in the LoginAuthenticator class.
     */
    private String userName;
    private String passWord;
    private int userID;
    private int userRole;
    private int creatorID;

    /**
     * Constructs a LoginAuthenticator object.
     */
    public LoginAuthenticator() {
        this.userName = "";
        this.passWord = "";
        this.userID = 0;
        this.userRole = -1;
        this.creatorID = 0;
    }

    /**
     * Sets the username of the user logged in.
     * @param aUserName a username that the user enters to log in
     * @precondition aUserName.size() > 0
     */
    public void setUserName(String aUserName) {
        this.userName = aUserName;
    }

    /**
     * Sets the password of the user logged in.
     * @param aPassWord a password that the user enters to log in
     * @precondition aPassWord.size() > 0
     */
    public void setPassWord(String aPassWord) {
        this.passWord = aPassWord;
    }

    /**
     * Gets the user ID of the user logged in.
     * @return a userID that is associated with a user
     * precondition userID > 0
     */
    public int getUserID() {
        return userID;
    }

    /**
     * Sets the user ID of the user logged in from loading the profile.
     * @param aUserID a user id that is associated with a user
     * @precondition aUserID > 0
     */
    public void setUserID(int aUserID) {
        this.userID = aUserID;
    }

    /**
     * Gets the user role of the user logged in.
     * @return the userRole of the user
     * @precondition userRole == 1 || userRole == 0
     */
    public int getUserRole() {
        return userRole;
    }

    /**
     * Sets the user role of the user logged in from loading the profile.
     * @param aUserRole a user role that is associated with a user
     * @precondition aUserRole == 1 || aUserRole == 0
     */
    public void setUserRole(int aUserRole) {
        this.userRole = aUserRole;
    }

    /**
     * Gets the creator id of the user if they are a client.
     * @precondition creatorID > 0 && reasonForUse == 1
     * @return the creator id of the user
     */
    public int getCreatorID() {
        return creatorID;
    }

    /**
     * Sets the creator id of the user if they are a client.
     * @param aTrainerID the creator of the user's creator
     * @precondition aTrainerID > 0
     */
    public void setCreatorId(int aTrainerID) {
        this.creatorID = aTrainerID;
    }
}
