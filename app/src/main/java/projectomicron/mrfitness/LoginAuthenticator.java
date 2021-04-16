package projectomicron.mrfitness;

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
 * Created by Emanuel Guerrero on 11/12/2015.
 */
public class LoginAuthenticator {
    /**
     * Instance variables only visible in the LoginAuthenticator class.
     */
    private String userName;
    private String passWord;
    private int userID;
    private int userRole;
    private int trainerID;

    /**
     * Instance variables that are only visible in the LoginAuthenticator class. Contains the
     * specific URL that contains the PHP script of the webservice.
     */
    private final String LOGIN_URL = "http://lamp.cse.fau.edu/~eguerre4/webservice/login.php";
    private final String GETUSERID_URL = "http://lamp.cse.fau.edu/~eguerre4/webservice/getuserid.php";

    /**
     * Instance variables that are only visible in the LoginAuthenticator class. Contains the JSON
     * element ids from the response of the PHP script.
     */
    private final String TAG_SUCCESS = "success";
    private final String TAG_MESSAGE = "message";
    private final String TAG_USERID = "userid";
    private final String TAG_USERROLE = "userrole";
    private final String TAG_TRAINERID = "trainerid";

    /**
     * Constructs a LoginAuthenticator object.
     */
    public LoginAuthenticator() {
        this.userName = "";
        this.passWord = "";
        this.userID = 0;
        this.userRole = -1;
        this.trainerID = 0;
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
     * Gets the trainer id of the user if they are a client.
     * @precondition trainerID > 0 && reasonForUse == 1
     * @return the trainer id of the user
     */
    public int getTrainerID() {
        return trainerID;
    }

    /**
     * Sets the trainer id of the user if they are a client.
     * @param aTrainerID the trainer of the user's trainer
     * @precondition aTrainerID > 0
     */
    public void setTrainerID(int aTrainerID) {
        this.trainerID = aTrainerID;
    }

    /**
     * Checks the login credentials of the user against the database.
     * @precondition userName.size() > 0
     * @precondition passWord.size() > 0
     * @return a String value that determines if the user's credentials are correct are not
     */
    public String checkCredentials() {
        //Declare a variable to store a JSON message
        String jsonMessage;

        //Build the POST parameters that need to be passed in the request
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", userName));
        params.add(new BasicNameValuePair("password", passWord));

        //Make the HTTP request to check the credentials of the user
        Log.d("request!", "starting");
        JSONObject json = JSONWebservice.getInstance().makeHttpRequest(LOGIN_URL, params);

        //Check the log for the json response
        //Log.d("Login attempt", json.toString());

        //Get the json success tag
        try {
            int success = json.getInt(TAG_SUCCESS);
            if (success == 1) {
                Log.d("Login Successful!", json.toString());
                jsonMessage = json.getString(TAG_MESSAGE);
            }
            else if (success == 0 && json.getString(TAG_MESSAGE).equals("Invalid Credentials")) {
                Log.d("Login Failure!", json.getString(TAG_MESSAGE));
                jsonMessage = json.getString(TAG_MESSAGE);
            }
            else {
                Log.d("Connection Error!", json.getString(TAG_MESSAGE));
                jsonMessage = json.getString(TAG_MESSAGE);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            jsonMessage = "Connection Error!";
            return jsonMessage;
        }

        return  jsonMessage;
    }

    /**
     * Queries the database for the userID of the user.
     * @precondition userName.size() > 0
     */
    public void queryUserIDAndUserRole() {
        //Build the POST parameters that need to be passed in the request
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", userName));

        //Make the HTTP request to get the user id and user role from the database
        Log.d("request!", "starting");
        JSONObject json = JSONWebservice.getInstance().makeHttpRequest(GETUSERID_URL, params);

        //Check the log for the json response
        Log.d("Login attempt", json.toString());

        //Get the json success tag
        try {
            int success = json.getInt(TAG_SUCCESS);
            if (success == 1) {
                Log.d("UserID Obtained!", json.getString(TAG_MESSAGE).toString());
                setUserID(json.getInt(TAG_USERID));
                setUserRole(json.getInt(TAG_USERROLE));
                setTrainerID(json.getInt(TAG_TRAINERID));

            }
            else {
                Log.d("Can not obtain UserID!", json.getString(TAG_MESSAGE));
                setUserID(0);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            setUserID(0);
        }
    }
}
