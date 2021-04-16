package projectomicron.mrfitness;

import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * This class manages the loading, creation and updating of a workout and the laoding and updating
 * of a user's fitness status.
 * Created by Emanuel Guerrero on 12/1/2015.
 */
public class FitnessManager {
    /**
     * Instance variables that are only visible in the FitnessManager class. Contains the
     * specific URL that contains the PHP script of the webservice.
     */
    private final String LOADWORKOUT_URL = "http://lamp.cse.fau.edu/~eguerre4/webservice/" +
                                            "loadworkout.php";
    private final String CREATEWORKOUT_URL = "http://lamp.cse.fau.edu/~eguerre4/webservice/" +
                                            "createworkout.php";
    private final String UPDATEWORKOUT_URL = "http://lamp.cse.fau.edu/~eguerre4/webservice/" +
                                            "updateworkout.php";
    private final String LOADFITNESSSTATS_URL = "http://lamp.cse.fau.edu/~eguerre4/webservice/" +
                                            "loadfitnessstats.php";
    private final String UPDATEFITNESSSTATS_URL = "http://lamp.cse.fau.edu/~eguerre4/webservice/" +
                                                "updatefitnessstats.php";

    /**
     * Instance variables that are only visible in the FitnessManager class. Contains the JSON
     * element ids from the response of the PHP script.
     */
    private final String TAG_SUCCESS = "success";
    private final String TAG_MESSAGE = "message";
    private final String TAG_WEIGHT = "weight";
    private final String TAG_HEIGHTFEET = "heightfeet";
    private final String TAG_HEIGHTINCHES = "heightinches";
    private final String TAG_USERID = "userid";
    private final String TAG_WORKOUTID = "workoutid";
    private final String TAG_TREADMILLMILES = "treadmillmiles";
    private final String TAG_TREADMILLMINUTES = "treadmillminutes";
    private final String TAG_PUSHUPS = "pushups";
    private final String TAG_SITUPS = "situps";
    private final String TAG_SQUATS = "squats";

    /**
     * Constructs a FitnessManager object.
     */
    public FitnessManager() {

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
     * Loads a workout of the user based on the user id and the workout id.
     * @precondition aUserID > 0
     * @precondition aWorkOutID > 0
     * @return a WorkOut object that contains the information needed to display the workout
     */
    public WorkOut loadWorkout(int aUserID, int aWorkOutID) {
        //Create a WorkOut object
        WorkOut workOut;

        //Build the POST parameters that need to be passed in the request for loading a workout
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userid", Integer.toString(aUserID)));
        params.add(new BasicNameValuePair("workoutid", Integer.toString(aWorkOutID)));

        //Make the HTTP request to loading the specific workout of the user
        Log.d("request!", "starting");
        JSONObject json = JSONWebservice.getInstance().makeHttpRequest(LOADWORKOUT_URL, params);

        //Check the log for the json response
        Log.d("Loading WorkOut", json.toString());

        //Get the json success tag
        try {
            int success = json.getInt(TAG_SUCCESS);
            if (success == 1 && json.getString(TAG_MESSAGE).equals("Workout loaded!")) {
                //Get the contents of the JSON object and create a WorkOut object
                workOut = new WorkOut(json.getInt(TAG_USERID), json.getInt(TAG_WORKOUTID), json.getInt(TAG_TREADMILLMILES),
                                      json.getInt(TAG_TREADMILLMINUTES), json.getInt(TAG_PUSHUPS),
                                      json.getInt(TAG_SITUPS), json.getInt(TAG_SQUATS));
            }
            else if (success == 0 && json.getString(TAG_MESSAGE).equals("Workout does not exist!")) {
                //Make the fields of the workOut object negative one meaning the workout does not
                //exist
                workOut = new WorkOut(-1, -1, -1, -1, -1, -1, -1);
            }
            else {
                //Make the fields of the workOut object negative two meaning there was a connection
                //error with the database
                workOut = new WorkOut(-2, -2, -2, -2, -2, -2, -2);
            }
        }
        catch (JSONException e) {
            //Make the fields of the workOut object negative two meaning there was a connection
            //error with the database
            e.printStackTrace();
            workOut = new WorkOut(-2, -2, -2, -2, -2, -2, -2);
        }

        return workOut;
    }

    /**
     * Creates a workout for a client to work on.
     * @precondition aUserID > 0
     * @precondition aTreadMillMiles >= 0
     * @precondition aTreadMillMinutes >= 0
     * @precondition aPushUps >= 0
     * @precondition aSitUps >= 0
     * @precondition aSquats >= 0
     * @param aUserID the id of the client that will do the workout
     * @param aTreadMillMiles the amount of miles ran on a treadmill
     * @param aTreadMillMinutes the amount of minutes the user ran on a treadmill
     * @param aPushUps the amount of push ups the user completed
     * @param aSitUps the amount of sit ups the user completed
     * @param aSquats the amount of squats the user completed
     * @return the workout id that was created for the workout
     */
    public int createWorkOut(int aUserID, int aTreadMillMiles, int aTreadMillMinutes, int aPushUps,
                             int aSitUps, int aSquats) {
        //Declare the variable that will hold the workout id of the workout created
        int workOutID;

        //Build the POST parameters that need to be passed to creating a workout for a client
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userid", Integer.toString(aUserID)));
        params.add(new BasicNameValuePair("treadmillmiles", Integer.toString(aTreadMillMiles)));
        params.add(new BasicNameValuePair("treadmillminutes", Integer.toString(aTreadMillMinutes)));
        params.add(new BasicNameValuePair("pushups", Integer.toString(aPushUps)));
        params.add(new BasicNameValuePair("situps", Integer.toString(aSitUps)));
        params.add(new BasicNameValuePair("squats", Integer.toString(aSquats)));

        //Make the HTTP request to create the workout
        Log.d("request!", "starting");
        JSONObject json = JSONWebservice.getInstance().makeHttpRequest(CREATEWORKOUT_URL, params);

        //Check the log for the json response
        Log.d("Creating WorkOut", json.toString());

        //Get the json success tag
        try {
            int success = json.getInt(TAG_SUCCESS);
            if (success == 1 && json.getString(TAG_MESSAGE).equals("Workout has been added successfully!")) {
                //Get the workout ID from the JSON object to show to the user
                workOutID = json.getInt(TAG_WORKOUTID);
            }
            else if (success == 0 && json.getString(TAG_MESSAGE).equals("Not all fields were entered!")) {
                //Set the workout ID to negative one to let the user know that not all of the fields
                //were filled
                workOutID = -1;
            }
            else {
                //Set the workout ID to negative two to let the user know that there was a
                //connection error
                workOutID = -2;
            }
        }
        catch (JSONException e) {
            //Set the workout ID to negative two to let the user know that there was a
            //connection error
            e.printStackTrace();
            workOutID = -2;
        }

        return workOutID;
    }

    /**
     * Updates a workout that the user is working on.
     * @precondition aUserID > 0
     * @precondition aWorkOutID > 0
     * @precondition aTreadMillMiles >= 0
     * @precondition aTreadMillMinutes >= 0
     * @precondition aPushUps >= 0
     * @precondition aSitUps >= 0
     * @precondition aSquats >= 0
     * @param aUserID the id of the client that will do the workout
     * @param aWorkOutID the id of the workout
     * @param aTreadMillMiles the amount of miles ran on a treadmill
     * @param aTreadMillMinutes the amount of minutes the user ran on a treadmill
     * @param aPushUps the amount of push ups the user completed
     * @param aSitUps the amount of sit ups the user completed
     * @param aSquats the amount of squats the user completed
     * @return a String to indicate that the update of a workout was successfully
     */
    public String updateWorkOut(int aUserID, int aWorkOutID, int aTreadMillMiles,
                                int aTreadMillMinutes, int aPushUps, int aSitUps, int aSquats) {
        //Declare a variable to store the JSON message
        String jsonMessage;

        //Build the POST parameters needed to update a workout
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userid", Integer.toString(aUserID)));
        params.add(new BasicNameValuePair("workoutid", Integer.toString(aWorkOutID)));
        params.add(new BasicNameValuePair("treadmillmiles", Integer.toString(aTreadMillMiles)));
        params.add(new BasicNameValuePair("treadmillminutes", Integer.toString(aTreadMillMinutes)));
        params.add(new BasicNameValuePair("pushups", Integer.toString(aPushUps)));
        params.add(new BasicNameValuePair("situps", Integer.toString(aSitUps)));
        params.add(new BasicNameValuePair("squats", Integer.toString(aSquats)));

        //Make the HTTP request to update the workout
        Log.d("request!", "starting");
        JSONObject json = JSONWebservice.getInstance().makeHttpRequest(UPDATEWORKOUT_URL, params);

        //Check the log for the json response
        Log.d("Updating WorkOut", json.toString());

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
     * Loads the fitness stats of the user
     * @precondition aUserID > 0
     * @param aUserID the user id of the user
     * @return a FitnessStats object needed to display the fitness stats of a user
     */
    public FitnessStats loadFitnessStats(int aUserID) {
        //Create a FitnessStats object
        FitnessStats fitnessStats;

        //Build the POST parameters that need to be passed in the request for loading a user's
        //fitness stats
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userid", Integer.toString(aUserID)));

        //Make the HTTP request to load the fitness stats of a user
        Log.d("request!", "starting");
        JSONObject json = JSONWebservice.getInstance().makeHttpRequest(LOADFITNESSSTATS_URL, params);

        //Check the log for the json response
        Log.d("Loading FitnessStats", json.toString());

        //Get the json success tag
        try {
            int success = json.getInt(TAG_SUCCESS);
            if (success == 1 && json.getString(TAG_MESSAGE).equals("Fitness Stats loaded!")) {
                //Get the weight, height feet, and height inches of the user from the JSON object
                int weight = json.getInt(TAG_WEIGHT);
                int heightFeet = json.getInt(TAG_HEIGHTFEET);
                int heightInches = json.getInt(TAG_HEIGHTINCHES);

                //Calculate the BMI of the user based on the retrieved weight, height feet and
                //height inches
                int BMI = calculateBMI(weight, heightFeet, heightInches);

                //Initialize the fitnessStats object
                fitnessStats = new FitnessStats(weight, heightFeet, heightInches, BMI);
            }
            else if (success == 0 && json.getString(TAG_MESSAGE).equals("Fitness stats does not exist!")) {
                //Initialize all of the fields to negative one in the fitnessStats object to let the
                //the user know that there are not any fitness stats available
                fitnessStats = new FitnessStats(-1, -1, -1, -1);
            }
            else {
                //Initialize all of the fields to negative two in the fitnessStats object to let the
                //user know that there was a connection error
                fitnessStats = new FitnessStats(-1, -1, -1, -1);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            //Initialize all of the fields to negative two in the fitnessStats object to let the
            //user know that there was a connection error
            fitnessStats = new FitnessStats(-1, -1, -1, -1);
        }

        return fitnessStats;
    }

    /**
     * Updates the fitness stats of an user.
     * @precondtion aUserID > 0
     * @precondition aWeigth > 0
     * @precondition aHeightFeet > 0
     * @precondition aHeightInches > 0
     * @param aUserID the user id of the user
     * @param aWeight the weight of the user
     * @param aHeightFeet the feet part of the height of the user
     * @param aHeightInches the inches part of the height of user
     */
    public String updateFitnessStats(int aUserID, int aWeight, int aHeightFeet, int aHeightInches) {
        //Declare a variable to store the JSON message
        String jsonMessage;

        //Build the POST parameters needed in the request to update a user's fitness stats
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userid", Integer.toString(aUserID)));
        params.add(new BasicNameValuePair("weight", Integer.toString(aWeight)));
        params.add(new BasicNameValuePair("heightfeet", Integer.toString(aHeightFeet)));
        params.add(new BasicNameValuePair("heightinches", Integer.toString(aHeightInches)));

        //Make the HTTP request to update the user's fitness stats
        Log.d("request!", "starting");
        JSONObject json = JSONWebservice.getInstance().makeHttpRequest(UPDATEFITNESSSTATS_URL, params);

        //Check the log for the json response
        Log.d("Updating FitnessStats", json.toString());

        //Get the json success tag
        try {
            int success = json.getInt(TAG_SUCCESS);
            if (success == 1 && json.getString(TAG_MESSAGE).equals("Fitness stats updated successfully!")) {
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

