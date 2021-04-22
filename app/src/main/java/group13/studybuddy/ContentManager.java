package group13.studybuddy;

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

    private final String CREATECONTENT_URL = "http://lamp.cse.fau.edu/~eguerre4/webservice/" +
                                            "createcontent.php";
    private final String UPDATECONTENT_URL = "http://lamp.cse.fau.edu/~eguerre4/webservice/" +
                                            "updatecontent.php";


    /**
     * Instance variables that are only visible in the ContentManager class. Contains the JSON
     * element ids from the response of the PHP script.
     */
    private final String TAG_SUCCESS = "success";
    private final String TAG_MESSAGE = "message";
    private final String TAG_CONTENTID = "contentid";

    /**
     * Constructs a ContentManager object.
     */
    public ContentManager() {

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

}

