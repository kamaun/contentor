package projectomicron.studybuddy;

import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * This class manages the loading, creation and updating of a content and the laoding and updating
 * of a user's content status.
 * Created by Whitney Andrews on 4/09/2021.
 */
public class ContentManager {
    /**
     * Instance variables that are only visible in the ContentManager class. Contains the
     * specific URL that contains the PHP script of the webservice.
     */
    private final String LOADCONTENT_URL = "http://lamp.cse.fau.edu/~eguerre4/webservice/" +
                                            "loadcontent.php";
    private final String CREATECONTENT_URL = "http://lamp.cse.fau.edu/~eguerre4/webservice/" +
                                            "createcontent.php";
    private final String UPDATECONTENT_URL = "http://lamp.cse.fau.edu/~eguerre4/webservice/" +
                                            "updatecontent.php";
    private final String LOADFITNESSSTATS_URL = "http://lamp.cse.fau.edu/~eguerre4/webservice/" +
                                            "loadcontentstats.php";
    private final String UPDATEFITNESSSTATS_URL = "http://lamp.cse.fau.edu/~eguerre4/webservice/" +
                                                "updatecontentstats.php";

    /**
     * Instance variables that are only visible in the ContentManager class. Contains the JSON
     * element ids from the response of the PHP script.
     */
    private final String TAG_SUCCESS = "success";
    private final String TAG_MESSAGE = "message";
    private final String TAG_WEIGHT = "weight";
    private final String TAG_HEIGHTFEET = "heightfeet";
    private final String TAG_HEIGHTINCHES = "heightinches";
    private final String TAG_USERID = "userid";
    private final String TAG_CONTENTID = "contentid";
    private final String TAG_TREADMILLMILES = "treadmillmiles";
    private final String TAG_TREADMILLMINUTES = "treadmillminutes";
    private final String TAG_PUSHUPS = "pushups";
    private final String TAG_SITUPS = "situps";
    private final String TAG_SQUATS = "squats";

    /**
     * Constructs a ContentManager object.
     */
    public ContentManager() {

    }

    /**
     * Calculates the BMI of the user.
     * @precondition aWeight > 0
     * @precondition aHeightFeet > 0
     * @precondition aHeightInches > 0
     * @param aWeight the weight of the user
     * @param aHeightFeet the feet component of the height of the user
     * @param aHeightInches the inch component of the height of the user
     * @return the BMI of the user
     */
    public int calculateBMI(int aWeight, int aHeightFeet, int aHeightInches) {
        //Calculate the actual height of the user
        int actualHeight = (aHeightFeet * 12) + aHeightInches;

        //Calculate the BMI
        int BMI = (aWeight * 703) / (actualHeight^2);

        return BMI;
    }

    /**
     * Loads a content of the user based on the user id and the content id.
     * @precondition aUserID > 0
     * @precondition aContentID > 0
     * @return a Content object that contains the information needed to display the content
     */
    public Content loadWorkout(int aUserID, int aContentID) {
        //Create a Content object
        Content content;

        //Build the POST parameters that need to be passed in the request for loading a content
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userid", Integer.toString(aUserID)));
        params.add(new BasicNameValuePair("contentid", Integer.toString(aContentID)));

        //Make the HTTP request to loading the specific content of the user
        Log.d("request!", "starting");
        JSONObject json = JSONWebservice.getInstance().makeHttpRequest(LOADCONTENT_URL, params);

        //Check the log for the json response
        Log.d("Loading Content", json.toString());

        //Get the json success tag
        try {
            int success = json.getInt(TAG_SUCCESS);
            if (success == 1 && json.getString(TAG_MESSAGE).equals("Workout loaded!")) {
                //Get the contents of the JSON object and create a Content object
                content = new Content(json.getInt(TAG_USERID), json.getInt(TAG_CONTENTID), json.getInt(TAG_TREADMILLMILES),
                                      json.getInt(TAG_TREADMILLMINUTES), json.getInt(TAG_PUSHUPS),
                                      json.getInt(TAG_SITUPS), json.getInt(TAG_SQUATS));
            }
            else if (success == 0 && json.getString(TAG_MESSAGE).equals("Workout does not exist!")) {
                //Make the fields of the content object negative one meaning the content does not
                //exist
                content = new Content(-1, -1, -1, -1, -1, -1, -1);
            }
            else {
                //Make the fields of the content object negative two meaning there was a connection
                //error with the database
                content = new Content(-2, -2, -2, -2, -2, -2, -2);
            }
        }
        catch (JSONException e) {
            //Make the fields of the content object negative two meaning there was a connection
            //error with the database
            e.printStackTrace();
            content = new Content(-2, -2, -2, -2, -2, -2, -2);
        }

        return content;
    }

    /**
     * Creates a content for a client to work on.
     * @precondition aUserID > 0
     * @precondition aTreadMillMiles >= 0
     * @precondition aTreadMillMinutes >= 0
     * @precondition aPushUps >= 0
     * @precondition aSitUps >= 0
     * @precondition aSquats >= 0
     * @param aUserID the id of the client that will do the content
     * @param aTreadMillMiles the amount of miles ran on a treadmill
     * @param aTreadMillMinutes the amount of minutes the user ran on a treadmill
     * @param aPushUps the amount of push ups the user completed
     * @param aSitUps the amount of sit ups the user completed
     * @param aSquats the amount of squats the user completed
     * @return the content id that was created for the content
     */
    public int createContent(int aUserID, int aTreadMillMiles, int aTreadMillMinutes, int aPushUps,
                             int aSitUps, int aSquats) {
        //Declare the variable that will hold the content id of the content created
        int contentID;

        //Build the POST parameters that need to be passed to creating a content for a client
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userid", Integer.toString(aUserID)));
        params.add(new BasicNameValuePair("treadmillmiles", Integer.toString(aTreadMillMiles)));
        params.add(new BasicNameValuePair("treadmillminutes", Integer.toString(aTreadMillMinutes)));
        params.add(new BasicNameValuePair("pushups", Integer.toString(aPushUps)));
        params.add(new BasicNameValuePair("situps", Integer.toString(aSitUps)));
        params.add(new BasicNameValuePair("squats", Integer.toString(aSquats)));

        //Make the HTTP request to create the content
        Log.d("request!", "starting");
        JSONObject json = JSONWebservice.getInstance().makeHttpRequest(CREATECONTENT_URL, params);

        //Check the log for the json response
        Log.d("Creating Content", json.toString());

        //Get the json success tag
        try {
            int success = json.getInt(TAG_SUCCESS);
            if (success == 1 && json.getString(TAG_MESSAGE).equals("Workout has been added successfully!")) {
                //Get the content ID from the JSON object to show to the user
                contentID = json.getInt(TAG_CONTENTID);
            }
            else if (success == 0 && json.getString(TAG_MESSAGE).equals("Not all fields were entered!")) {
                //Set the content ID to negative one to let the user know that not all of the fields
                //were filled
                contentID = -1;
            }
            else {
                //Set the content ID to negative two to let the user know that there was a
                //connection error
                contentID = -2;
            }
        }
        catch (JSONException e) {
            //Set the content ID to negative two to let the user know that there was a
            //connection error
            e.printStackTrace();
            contentID = -2;
        }

        return contentID;
    }

    /**
     * Updates a content that the user is working on.
     * @precondition aUserID > 0
     * @precondition aContentID > 0
     * @precondition aTreadMillMiles >= 0
     * @precondition aTreadMillMinutes >= 0
     * @precondition aPushUps >= 0
     * @precondition aSitUps >= 0
     * @precondition aSquats >= 0
     * @param aUserID the id of the client that will do the content
     * @param aContentID the id of the content
     * @param aTreadMillMiles the amount of miles ran on a treadmill
     * @param aTreadMillMinutes the amount of minutes the user ran on a treadmill
     * @param aPushUps the amount of push ups the user completed
     * @param aSitUps the amount of sit ups the user completed
     * @param aSquats the amount of squats the user completed
     * @return a String to indicate that the update of a content was successfully
     */
    public String updateContent(int aUserID, int aContentID, int aTreadMillMiles,
                                int aTreadMillMinutes, int aPushUps, int aSitUps, int aSquats) {
        //Declare a variable to store the JSON message
        String jsonMessage;

        //Build the POST parameters needed to update a content
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userid", Integer.toString(aUserID)));
        params.add(new BasicNameValuePair("contentid", Integer.toString(aContentID)));
        params.add(new BasicNameValuePair("treadmillmiles", Integer.toString(aTreadMillMiles)));
        params.add(new BasicNameValuePair("treadmillminutes", Integer.toString(aTreadMillMinutes)));
        params.add(new BasicNameValuePair("pushups", Integer.toString(aPushUps)));
        params.add(new BasicNameValuePair("situps", Integer.toString(aSitUps)));
        params.add(new BasicNameValuePair("squats", Integer.toString(aSquats)));

        //Make the HTTP request to update the content
        Log.d("request!", "starting");
        JSONObject json = JSONWebservice.getInstance().makeHttpRequest(UPDATECONTENT_URL, params);

        //Check the log for the json response
        Log.d("Updating Content", json.toString());

        //Get the json success tag
        try {
            int success = json.getInt(TAG_SUCCESS);
            if (success == 1 && json.getString(TAG_MESSAGE).equals("Workout updated successfully!")) {
                //Get the json string from the JSON object
                jsonMessage = json.getString(TAG_MESSAGE);
            }
            else if (success == 0 && json.getString(TAG_MESSAGE).equals("Not all fields were entered!")) {
                //Get the json string from the JSON object
                jsonMessage = json.getString(TAG_MESSAGE);
            }
            else {
                //Get the json string from the JSON object
                jsonMessage = json.getString(TAG_MESSAGE);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            jsonMessage = "Database query error#1!";
        }

        return jsonMessage;
    }

    /**
     * Loads the content stats of the user
     * @precondition aUserID > 0
     * @param aUserID the user id of the user
     * @return a ContentStats object needed to display the content stats of a user
     */
    public ContentStats loadContentStats(int aUserID) {
        //Create a ContentStats object
        ContentStats contentStats;

        //Build the POST parameters that need to be passed in the request for loading a user's
        //content stats
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userid", Integer.toString(aUserID)));

        //Make the HTTP request to load the content stats of a user
        Log.d("request!", "starting");
        JSONObject json = JSONWebservice.getInstance().makeHttpRequest(LOADFITNESSSTATS_URL, params);

        //Check the log for the json response
        Log.d("Loading ContentStats", json.toString());

        //Get the json success tag
        try {
            int success = json.getInt(TAG_SUCCESS);
            if (success == 1 && json.getString(TAG_MESSAGE).equals("Content Stats loaded!")) {
                //Get the weight, height feet, and height inches of the user from the JSON object
                int weight = json.getInt(TAG_WEIGHT);
                int heightFeet = json.getInt(TAG_HEIGHTFEET);
                int heightInches = json.getInt(TAG_HEIGHTINCHES);

                //Calculate the BMI of the user based on the retrieved weight, height feet and
                //height inches
                int BMI = calculateBMI(weight, heightFeet, heightInches);

                //Initialize the contentStats object
                contentStats = new ContentStats(weight, heightFeet, heightInches, BMI);
            }
            else if (success == 0 && json.getString(TAG_MESSAGE).equals("Content stats does not exist!")) {
                //Initialize all of the fields to negative one in the contentStats object to let the
                //the user know that there are not any content stats available
                contentStats = new ContentStats(-1, -1, -1, -1);
            }
            else {
                //Initialize all of the fields to negative two in the contentStats object to let the
                //user know that there was a connection error
                contentStats = new ContentStats(-1, -1, -1, -1);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            //Initialize all of the fields to negative two in the contentStats object to let the
            //user know that there was a connection error
            contentStats = new ContentStats(-1, -1, -1, -1);
        }

        return contentStats;
    }

    /**
     * Updates the content stats of an user.
     * @precondtion aUserID > 0
     * @precondition aWeigth > 0
     * @precondition aHeightFeet > 0
     * @precondition aHeightInches > 0
     * @param aUserID the user id of the user
     * @param aWeight the weight of the user
     * @param aHeightFeet the feet part of the height of the user
     * @param aHeightInches the inches part of the height of user
     */
    public String updateContentStats(int aUserID, int aWeight, int aHeightFeet, int aHeightInches) {
        //Declare a variable to store the JSON message
        String jsonMessage;

        //Build the POST parameters needed in the request to update a user's content stats
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userid", Integer.toString(aUserID)));
        params.add(new BasicNameValuePair("weight", Integer.toString(aWeight)));
        params.add(new BasicNameValuePair("heightfeet", Integer.toString(aHeightFeet)));
        params.add(new BasicNameValuePair("heightinches", Integer.toString(aHeightInches)));

        //Make the HTTP request to update the user's content stats
        Log.d("request!", "starting");
        JSONObject json = JSONWebservice.getInstance().makeHttpRequest(UPDATEFITNESSSTATS_URL, params);

        //Check the log for the json response
        Log.d("Updating ContentStats", json.toString());

        //Get the json success tag
        try {
            int success = json.getInt(TAG_SUCCESS);
            if (success == 1 && json.getString(TAG_MESSAGE).equals("Content stats updated successfully!")) {
                //Get the json string from the JSON object
                jsonMessage = json.getString(TAG_MESSAGE);
            }
            else if (success == 0 && json.getString(TAG_MESSAGE).equals("Not all fields were entered!")) {
                //Get the json string from the JSON object
                jsonMessage = json.getString(TAG_MESSAGE);
            }
            else {
                //Get the json string from the JSON object
                jsonMessage = json.getString(TAG_MESSAGE);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            jsonMessage = "Database query error#1!";
        }

        return jsonMessage;
    }
}

