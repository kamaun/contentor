package projectomicron.mrfitness;

import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * This class manages the creation, loading, updating, and deleting of a user's account.
 * Created by Emanuel Guerrero on 11/23/2015.
 */
public class AccountManager {
    /**
     * Instance variables only visible in the AccountManager class.
     */
    private String userName;
    private String passWord;
    private String firstName;
    private String lastName;
    private int userID;
    private int age;
    private int reasonForUse;
    private int trainerID;
    private int certificationNumber;
    private int insuranceNumber;

    /**
     * Instance variables that are only visible in the AccountManager class. Contains the specific
     * URL that contains the PHP script of the webservice.
     */
    private final String CREATEACCOUNT_URL = "http://lamp.cse.fau.edu/~eguerre4/webservice/" +
                                                "createaccount.php";
    private final String LOADACCOUNT_URL = "http://lamp.cse.fau.edu/~eguerre4/webservice/" +
                                                "loadaccount.php";
    private final String UPDATEACCOUNT_URL = "http://lamp.cse.fau.edu/~eguerre4/webservice/" +
                                                "updateaccount.php";
    private final String DELETEACCOUNT_URL = "http://lamp.cse.fau.edu/~eguerre4/webservice/" +
                                                "deleteaccount.php";
    private final String CHECKUSERNAME_URL = "http://lamp.cse.fau.edu/~eguerre4/webservice/" +
                                                "checkusername.php";

    /**
     * Instance variables that are only visible in the AccountManager class. Contains the JSON
     * element ids from the response of the PHP script.
     */
    private final String TAG_SUCCESS = "success";
    private final String TAG_MESSAGE = "message";
    private final String TAG_USERNAME = "username";
    private final String TAG_PASSWORD = "password";
    private final String TAG_FIRSTNAME = "firstname";
    private final String TAG_LASTNAME = "lastname";
    private final String TAG_AGE = "age";
    private final String TAG_TRAINERID = "trainerid";
    private final String TAG_CERTIFICATIONNUMBER = "certificationnumber";
    private final String TAG_INSURANCENUMBER = "insurancenumber";

    /**
     * Constructs an empty AccountManager object.
     */
    public AccountManager() {
    }

    /**
     * Constructs an AccountManager object with the information of the user when creating an account
     * and loading a user's account when logged in.
     * @param aUserID the user id of the user who is logged in. This field is not required when
     *                the user creates a account
     * @param aUserName the user name of the user who is logged in or creating an account
     * @param aPassWord the password that user enters when logging in or when creating an account
     * @param aFirstName the first name of the user that is logged in or when creating an account
     * @param aLastName the last name of the user that is logged in or when creating an account
     * @param aReasonForUse the role of a user that can be integer zero for client or integer one
     *                      for trainer
     * @param aTrainerID the id of the trainer that the client is associated with. This is an
     *                   optional field if the user is an trainer
     * @param aCertificationNumber the certification number of the user that has the reasonForUse
     *                             field with an integer of one. This field is required for trainers
     * @param aInsuranceNumber the insurance number of the user that has the reasonForUSe field with
     *                         an integer of one. This field is required for trainers
     * @precondition aUserID > 0 || aUsername == 0
     * @precondition aUserName.size() > 0
     * @precondition aPassWord.size() > 0
     * @precondition aFirstName.size() > 0
     * @precondition aLastName.size() > 0
     * @precondition aAge > 0
     * @precondition aReasonForUse >= 0
     * @precondition aTrainerID > 0 || aTrainerID == 0
     * @precondition aCertificationNumber > 0 || aCertification == 0
     * @precondition aInsuranceNumber > 0 || aInsuranceNumber ==0
     */
    public AccountManager(int aUserID, String aUserName, String aPassWord, String aFirstName,
                          String aLastName,int aAge, int aReasonForUse, int aTrainerID,
                          int aCertificationNumber, int aInsuranceNumber) {
        this.userID = aUserID;
        this.userName = aUserName;
        this.passWord = aPassWord;
        this.firstName = aFirstName;
        this.lastName = aLastName;
        this.age = aAge;
        this.reasonForUse = aReasonForUse;
        this.trainerID = aTrainerID;
        this.certificationNumber = aCertificationNumber;
        this.insuranceNumber = aInsuranceNumber;
    }

    /**
     * Gets the user id of the user.
     * @precondition userID > 0
     * @return the user id of the user
     */
    public int getUserID() {
        return userID;
    }

    /**
     * Gets the username of the user.
     * @precondition userName.size() > 0
     * @return the username of the user
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the username of the user.
     * @param aUserName the username the user entered to create a account or change
     * @precondition aUserName.size() > 0
     */
    public void setUserName(String aUserName) {
        this.userName = aUserName;
    }

    /**
     * Gets the password of the user.
     * @precondition passWord.size() > 0
     * @return the password of the user
     */
    public String getPassWord() {
        return passWord;
    }

    /**
     * Sets the password of the user.
     * @param aPassWord the password the user entered to create a account or change
     * @precondition aPassWord.size() > 0
     */
    public void setPassWord(String aPassWord) {
        this.passWord = aPassWord;
    }

    /**
     * Gets the first name of the user.
     * @precondition firstName.size() > 0
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name of the user.
     * @precondition aFirstName.size() > 0
     * @return the first name of the user
     */
    public void setFirstName(String aFirstName) {
        this.firstName = aFirstName;
    }

    /**
     * Gets the last name of the user.
     * @precondition lastName.size() > 0
     * @return the last name of the user
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name of the user.
     * @param aLastName the last name the user entered to create a account or change
     * @precondition aLastName.size() > 0
     */
    public void setLastName(String aLastName) {
        this.lastName = aLastName;
    }

    /**
     * Gets the age of the user.
     * @precondition age > 0
     * @return the age of the user
     */
    public int getAge() {
        return age;
    }

    /**
     * Sets the age of the user.
     * @precondition aAge > 0
     */
    public void setAge(int aAge) {
        this.age = aAge;
    }

    /**
     * Gets the reason for use of the user.
     * @precondition reasonForUse == 1 || reasonForUse == 2
     * @return the reason for use of the user
     */
    public int getReasonForUse() {
        return reasonForUse;
    }

    /**
     * Gets the trainer id of the user.
     * @precondition trainerID > 0
     * @return the trainer id of the user who is a client of the trainer.
     */
    public int getTrainerID() {
        return trainerID;
    }

    /**
     * Gets the certification number of the user who is a trainer.
     * @precondition certificationNumber > 0
     * @return the certification number of the user
     */
    public int getCertificationNumber() {
        return certificationNumber;
    }

    /**
     * Sets the certification number of the user who is a trainer.
     * @param aCertificationNumber the certification number of the user entered to create a account
     *                             or change
     * @precondition aCertificationNumber > 0
     */
    public void setCertificationNumber(int aCertificationNumber) {
        this.certificationNumber = aCertificationNumber;
    }

    /**
     * Gets the insurance number of the user who is a trainer.
     * @precondition insuranceNumber > 0
     * @return the insurance number of the user
     */
    public int getInsuranceNumber() {
        return insuranceNumber;
    }

    /**
     * Sets the insurance number of the user who is a trainer.
     * @param aInsuranceNumber the insurance number of the user entered to create a account or change
     * @precondition aInsuranceNumber > 0
     */
    public void setInsuranceNumber(int aInsuranceNumber) {
        this.insuranceNumber = aInsuranceNumber;
    }

    /**
     * Creates a new account for the user. Note that we do not need to pass a user id because the
     * database will take care of creating one for the user.
     * @precondition userName.size() > 0
     * @precondition passWord.size() > 0
     * @precondition firstName.size() > 0
     * @precondition lastName.size() > 0
     * @precondition reasonForUse == 1 || reasonForUse == 2
     * @precondition trainerID > 0 || aTrainerID == 0
     * @precondition certificationNumber > 0 || aCertification == 0
     * @precondition insuranceNumber > 0 || aInsuranceNumber ==0
     * @return a String value to indicate if the account has been created successfully
     */
    public String createAccount() {
        //Declare a variable to store the JSON message
        String jsonMessage;

        //Build the POST parameters that need to be passed in the request for checking if the
        //username that was entered already exists
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", userName));

        //Make the HTTP request to check if the username entered already exists in the database
        Log.d("request!", "starting");
        JSONObject json1 = JSONWebservice.getInstance().makeHttpRequest(CHECKUSERNAME_URL, params);

        //Check the log for the json response
        Log.d("Checking username", json1.toString());

        //Get the json success tag
        try {
            int success = json1.getInt(TAG_SUCCESS);
            if (success == 0 && json1.getString(TAG_MESSAGE).equals
                                                            ("The username is already taken!")) {
                Log.d("Creating Account failed", json1.getString(TAG_MESSAGE));
                jsonMessage = json1.getString(TAG_MESSAGE);
                return jsonMessage;
            }
            else if (success == 0 && json1.getString(TAG_MESSAGE).equals
                                                                    ("Database query error#1!")) {
                Log.d("Creating Account failed", json1.getString(TAG_MESSAGE));
                jsonMessage = json1.getString(TAG_MESSAGE);
                return jsonMessage;
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            jsonMessage = "Database query error#1!";
            return jsonMessage;
        }

        //Add additionally POST parameters that need to be passed in the request for creating the
        //user an account
        params.add(new BasicNameValuePair("password", passWord));
        params.add(new BasicNameValuePair("firstname", firstName));
        params.add(new BasicNameValuePair("lastname", lastName));
        params.add(new BasicNameValuePair("age", Integer.toString(age)));
        params.add(new BasicNameValuePair("reasonforuse", Integer.toString(reasonForUse)));
        params.add(new BasicNameValuePair("trainerid", Integer.toString(trainerID)));
        params.add(new BasicNameValuePair("certificationnumber", Integer.
                                            toString(certificationNumber)));
        params.add(new BasicNameValuePair("insurancenumber", Integer.toString(insuranceNumber)));

        //Make the HTTP request to create the account for the user
        JSONObject json2 = JSONWebservice.getInstance().makeHttpRequest(CREATEACCOUNT_URL, params);

        //Check the log for the json response
        Log.d("Checking username", json1.toString());

        //Get the json success tag
        try {
            int success = json2.getInt(TAG_SUCCESS);
            if (success == 1) {
                Log.d("Account Created!", json2.getString(TAG_MESSAGE));
                jsonMessage = json2.getString(TAG_MESSAGE);
            }
            else if (success == 0 && json2.getString(TAG_MESSAGE).equals
                                                            ("The username is already taken!")) {
                Log.d("Creating Account failed", json2.getString(TAG_MESSAGE));
                jsonMessage = json2.getString(TAG_MESSAGE);
            }
            else if (success == 0 && json2.getString(TAG_MESSAGE).equals
                                                                ("Not all fields were entered!")) {
                Log.d("Creating Account failed", json2.getString(TAG_MESSAGE));
                jsonMessage = json2.getString(TAG_MESSAGE);
            }
            else {
                Log.d("Creating Account failed", json2.getString(TAG_MESSAGE));
                jsonMessage = json2.getString(TAG_MESSAGE);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            jsonMessage = "Database query error#1!";
            return jsonMessage;
        }

        return jsonMessage;
    }

    /**
     * Loads an account of the user that is logged in.
     * @precondition userid > 0
     * @return a String value to indicate that the user's account has been successfully loaded
     */
    public String loadAccount() {
        //Declare a variable to store the JSON message
        String jsonMessage;

        //Build the POST parameters that need to be passed in the request for loading the user's
        //account
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userid", Integer.toString(userID)));

        //Make the HTTP request to load the user's account
        Log.d("request!", "starting");
        JSONObject json = JSONWebservice.getInstance().makeHttpRequest(LOADACCOUNT_URL, params);

        //Check the log for the json response
        Log.d("Loading user", json.toString());

        //Get the json success tag
        try {
            int success = json.getInt(TAG_SUCCESS);
            if (success == 1 && json.getString(TAG_MESSAGE).equals("Account loaded!")) {
                Log.d("Account loaded", json.getString(TAG_MESSAGE));
                jsonMessage = json.getString(TAG_MESSAGE);
                //Set the fields of the AccountManager object with the loaded data
                this.userName = json.getString(TAG_USERNAME);
                this.passWord = json.getString(TAG_PASSWORD);
                this.firstName = json.getString(TAG_FIRSTNAME);
                this.lastName = json.getString(TAG_LASTNAME);
                this.age = json.getInt(TAG_AGE);
                this.trainerID = json.getInt(TAG_TRAINERID);
                this.certificationNumber = json.getInt(TAG_CERTIFICATIONNUMBER);
                this.insuranceNumber = json.getInt(TAG_INSURANCENUMBER);
            }
            else if (success == 0 && json.getString(TAG_MESSAGE).equals
                                                                    ("Account does not exist!")) {
                Log.d("Loading account failed", json.getString(TAG_MESSAGE));
                jsonMessage = json.getString(TAG_MESSAGE);
            }
            else {
                Log.d("Connection Error!", json.getString(TAG_MESSAGE));
                jsonMessage = json.getString(TAG_MESSAGE);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            jsonMessage = "Database query error#1!";
            return jsonMessage;
        }

        return jsonMessage;
    }

    /**
     * Updates the account info of the user that is logged in.
     * @precondition userID > 0
     * @precondition userName.size() > 0
     * @precondition passWord.size() > 0
     * @precondition firstName.size() > 0
     * @precondition lastName.size() > 0
     * @precondition age > 0
     * @precondition reasonForUse == 1 || reasonForUse == 2
     * @precondition trainerID > 0 || aTrainerID == 0
     * @precondition certificationNumber > 0 || aCertification == 0
     * @precondition insuranceNumber > 0 || aInsuranceNumber ==0
     * @return a String value to indicate if the account has been updated successfully
     */
    public String updateAccount() {
        //Declare a variable to store the JSON message
        String jsonMessage;

        //Build the POST parameters that need to be passed in the request for checking if the
        //username that was entered already exists
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", userName));

        //Make the HTTP request to check if the username entered already exists in the database
        Log.d("request!", "starting");
        JSONObject json1 = JSONWebservice.getInstance().makeHttpRequest(CHECKUSERNAME_URL, params);

        //Check the log for the json response
        Log.d("Checking username", json1.toString());

        //Get the json success tag
        try {
            int success = json1.getInt(TAG_SUCCESS);
            if (success == 0 && json1.getString(TAG_MESSAGE).equals("The username is already taken!")) {
                Log.d("Updating Account failed", json1.getString(TAG_MESSAGE));
                jsonMessage = json1.getString(TAG_MESSAGE);
                return jsonMessage;
            }
            else if (success == 0 && json1.getString(TAG_MESSAGE).equals("Database query error#1!")) {
                Log.d("Updating Account failed", json1.getString(TAG_MESSAGE));
                jsonMessage = json1.getString(TAG_MESSAGE);
                return jsonMessage;
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            jsonMessage = "Database query error#1!";
            return jsonMessage;
        }

        //Add additionally POST parameters that need to be passed in the request for updating the
        //user an account
        params.add(new BasicNameValuePair("password", passWord));
        params.add(new BasicNameValuePair("firstname", firstName));
        params.add(new BasicNameValuePair("lastname", lastName));
        params.add(new BasicNameValuePair("age", Integer.toString(age)));
        params.add(new BasicNameValuePair("reasonforuse", Integer.toString(reasonForUse)));
        params.add(new BasicNameValuePair("trainerid", Integer.toString(trainerID)));
        params.add(new BasicNameValuePair("certificationnumber", Integer.toString(certificationNumber)));
        params.add(new BasicNameValuePair("insurancenumber", Integer.toString(insuranceNumber)));
        params.add(new BasicNameValuePair("userid", Integer.toString(userID)));

        //Make the HTTP request to create the account for the user
        JSONObject json2 = JSONWebservice.getInstance().makeHttpRequest(UPDATEACCOUNT_URL, params);

        //Check the log for the json response
        Log.d("Updating account", json1.toString());

        //Get the json success tag
        try {
            int success = json2.getInt(TAG_SUCCESS);
            if (success == 1 && json2.getString(TAG_MESSAGE).equals("Account updated successfully!")) {
                Log.d("Account updated!", json2.getString(TAG_MESSAGE));
                jsonMessage = json2.getString(TAG_MESSAGE);
            }
            else if (success == 0 && json2.getString(TAG_MESSAGE).equals("Not all fields were entered!")) {
                Log.d("Updating Account failed", json2.getString(TAG_MESSAGE));
                jsonMessage = json2.getString(TAG_MESSAGE);
            }
            else {
                Log.d("Updating Account failed", json2.getString(TAG_MESSAGE));
                jsonMessage = json2.getString(TAG_MESSAGE);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            jsonMessage = "Database query error#1!";
            return jsonMessage;
        }

        return jsonMessage;
    }

    /**
     * Deletes the account of the user.
     * @precondition userid > 0
     * @return a String value to indicate if the user's account has been removed
     */
    public String deleteAccount() {
        //Declare a variable to store the JSON message
        String jsonMessage;

        //Build the POST parameters that need to be passed in the request for deleting the user's
        //account
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userid", Integer.toString(userID)));

        //Make the HTTP request to delete the user's account
        JSONObject json = JSONWebservice.getInstance().makeHttpRequest(DELETEACCOUNT_URL, params);

        //Check the log for the json response
        Log.d("Deleting account", json.toString());

        //Get the json success tag
        try {
            int success = json.getInt(TAG_SUCCESS);
            if (success == 1 && json.getString(TAG_MESSAGE).equals("Account deleted successfully!")) {
                Log.d("Account deleted!", json.getString(TAG_MESSAGE));
                jsonMessage = json.getString(TAG_MESSAGE);
            }
            else if (success == 0 && json.getString(TAG_MESSAGE).equals("User's account does not exist!")) {
                Log.d("Deleting account failed", json.getString(TAG_MESSAGE));
                jsonMessage = json.getString(TAG_MESSAGE);
            }
            else {
                Log.d("Deleting account failed", json.getString(TAG_MESSAGE));
                jsonMessage = json.getString(TAG_MESSAGE);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            jsonMessage = "Database query error#1!";
            return jsonMessage;
        }

        return jsonMessage;
    }
}
