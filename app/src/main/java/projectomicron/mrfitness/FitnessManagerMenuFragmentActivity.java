package projectomicron.mrfitness;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is a Fragment class that serves as a central hub to viewing the fitness status,
 * looking for a previous workout, and creating a new workout.
 * Created by Emanuel Guerrero on 12/2/2015.
 */
public class FitnessManagerMenuFragmentActivity extends Fragment implements View.OnClickListener {
    /**
     * Instance variables only visible in the FitnessManagerMenuFragmentActivity class. These are the
     * components to the view.
     */
    private TextView clientTextLabel;
    private TextView errorMessageLabel;
    private EditText workOutNumberTextBox;
    private Spinner clientSpinner;
    private Button createWorkOutButton;
    private Button viewWorkOutButton;
    private Button viewFitnessStatsButton;
    private ProgressDialog dialog;

    /**
     * Instance variables only visible in the FitnessManagerMenuFragment class. These are the intent
     * variables used to load data, and distinguish the reason for use.
     */
    private int userID;
    private int reasonForUse;
    private int trainerID;
    private int clientIDFromSpinner = 0;
    private int workOutID;
    private String clientName = "";

    /**
     * Instance variables only visible in the FitnessManagerFragmentActivity class. Contains the URL
     * to the specific URL to the PHP of the webservice to populate the client spinner.
     */
    private final String LOADALLCLIENTS_URL = "http://lamp.cse.fau.edu/~eguerre4/webservice/" +
            "loadallclients.php";

    /**
     * Instance variables that are only visible in the FitnessManagerMenuFragmentActivity class.
     * Contains the JSON element ids from the response of the PHP script.
     */
    private final String TAG_SUCCESS = "success";
    private final String TAG_MESSAGE = "message";
    private final String TAG_CLIENTS = "clients";
    private final String TAG_FIRSTNAME = "firstname";
    private final String TAG_LASTNAME = "lastname";
    private final String TAG_USERID = "userid";

    /**
     * Instance variables that are only visible in the FitnessManagerMenuFragmentActivity class.
     * Contains a reference to the activity
     */
    private FragmentActivity activity;

    /**
     * Constructs a FitnessManagerMenuFragmentActivity object
     */
    public FitnessManagerMenuFragmentActivity() {

    }

    /**
     * Overrides the onAttach method inherited from Fragment to attach a reference from the activity
     * to the fragment.
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (FragmentActivity) activity;
    }

    /**
     * Overrides the onCreate method inherited from Fragment
     *
     * @param savedInstanceState an instance of the activity
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Overrides the onCreateView method inherited from Fragment
     * @param inflater
     * @param container a component that stores other components
     * @param savedInstanceState an instance of the activity
     * @return a view layout to create the activity
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Get the user id, reason for use, and trainer id from the intent
        Bundle extras = activity.getIntent().getExtras();
        userID = extras.getInt("userID");
        reasonForUse = extras.getInt("userRole");
        trainerID = extras.getInt("trainerID");

        //Inflate the layout for the FitnessManagerMenuFragment activity
        View view = inflater.inflate(R.layout.activity_work_out_menu_fragment, container, false);

        //Get references to the buttons
        createWorkOutButton = (Button) view.findViewById(R.id.editButton);
        viewWorkOutButton = (Button) view.findViewById(R.id.viewWorkOutButton);
        viewFitnessStatsButton = (Button) view.findViewById(R.id.viewFitnessStatusButton);
        //Set on click listeners for the buttons
        createWorkOutButton.setOnClickListener(this);
        viewWorkOutButton.setOnClickListener(this);
        viewFitnessStatsButton.setOnClickListener(this);

        //Get a reference to the error message label and work out number text box
        errorMessageLabel = (TextView) view.findViewById(R.id.errorMessageLabel);
        workOutNumberTextBox = (EditText) view.findViewById(R.id.workOutNumnerTextBox);

        //Get a reference to the client text label and spinner
        clientTextLabel = (TextView) view.findViewById(R.id.clientTextLabel);
        clientSpinner = (Spinner) view.findViewById(R.id.clientSpinner);
        //Check the reason for use to see if the user is a trainer or a client
        if (reasonForUse == 2) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //Build the POST parameters that need to be passed in the request for loading the user's
                    //account
                    List<NameValuePair> postParams = new ArrayList<NameValuePair>();
                    postParams.add(new BasicNameValuePair("trainerid", Integer.toString(userID)));

                    //Make the HTTP request to load the user's account
                    Log.d("request!", "starting");
                    final JSONObject json = JSONWebservice.getInstance().makeHttpRequest(LOADALLCLIENTS_URL, postParams);

                    //Check the log for the json response
                    Log.d("Loading clients", json.toString());

                    final List<String> clientList  = new ArrayList<String>();
                    final ArrayList<Integer> clientID = new ArrayList<Integer>();

                    try {
                        int success = json.getInt(TAG_SUCCESS);
                        if (success == 1 && json.getString(TAG_MESSAGE).equals("Clients loaded!")) {
                            Log.d("Clients loaded", json.getString(TAG_MESSAGE));
                            //Get the array of clients from the JSON object
                            JSONArray clients = json.getJSONArray(TAG_CLIENTS);
                            //Traverse through the JSON array and copy each element to the clientList and client
                            //ID ArrayList
                            for (int i = 0; i < clients.length(); i++) {
                                JSONObject client = clients.getJSONObject(i);
                                clientList.add(client.getString(TAG_FIRSTNAME) + " "
                                        + client.getString(TAG_LASTNAME));
                                clientID.add(client.getInt(TAG_USERID));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (!clientList.isEmpty()) {
                        //Create ArrayAdapter using the clientList ArrayList
                        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                                android.R.layout.simple_spinner_item, clientList);
                        //Specify the layout to use when the choices appear
                        adapter.setDropDownViewResource(android.
                                R.layout.simple_spinner_dropdown_item);
                        //Apply the adapter to the spinner
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                clientSpinner.setAdapter(adapter);
                            }
                        });

                        //Set the listener for when the user selects a choice from the spinner
                        clientSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                //Get the position of the item selected
                                int selectedItemPosition = clientSpinner.getSelectedItemPosition();
                                //Set the receiver id based on the selectedItemPosition
                                clientIDFromSpinner = clientID.get(selectedItemPosition);
                                clientName = clientList.get(selectedItemPosition);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                //Auto-generated method stub
                            }
                        });
                    }
                    else {
                        //Set the client label and spinner invisible
                        clientTextLabel.setVisibility(View.INVISIBLE);
                        clientSpinner.setVisibility(View.INVISIBLE);
                        //Set the error message label visible to let the user know that they do not
                        //have any clients
                        errorMessageLabel.setVisibility(View.VISIBLE);
                        errorMessageLabel.setText(R.string.errorMessageLabelNoClients);
                    }
                }
            }).start();
        }
        else {
            //Set the client label and client spinner invisible
            clientTextLabel.setVisibility(View.INVISIBLE);
            clientSpinner.setVisibility(View.INVISIBLE);
            //Set the create workout button invisible
            createWorkOutButton.setVisibility(View.INVISIBLE);
        }

        return view;
    }

    /**
     * Implements the onClick method in the View.OnClickListener Interface to add the event
     * listeners to the buttons.
     * @param v the view of the activity class
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editButton:
                //Check if the user selected a client from the spinner first
                if (clientIDFromSpinner != 0 && !clientName.equals("")) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //Set an intent to redirect the user to the TabMenuManagerActivity activity
                            Intent intent = new Intent(activity.getApplicationContext(), CreateWorkOutActivity.class);
                            //Store the userId and user role of the user to be used throughout the application
                            intent.putExtra("userID", userID);
                            intent.putExtra("userRole", reasonForUse);
                            intent.putExtra("trainerID", trainerID);
                            intent.putExtra("clientID", clientIDFromSpinner);
                            intent.putExtra("clientName", clientName);
                            //Start the next activity
                            onSwitch(intent);
                        }
                    }).start();
                }
                else {
                    //Let the user know that because they are a trainer that they must select a
                    //client from the spinner.
                    errorMessageLabel.setVisibility(View.VISIBLE);
                    errorMessageLabel.setText(R.string.errorMessageTextLabelNoClientSelected);
                }
                break;
            case R.id.viewWorkOutButton:
                //Check if the workout number text box was filled out
                try {
                    workOutID = Integer.parseInt(workOutNumberTextBox.getText().toString());
                }
                catch (NumberFormatException e) {
                    workOutID = 0;
                }
                if (workOutID == 0) {
                    //Let the user know that they need to enter a work out ID
                    errorMessageLabel.setVisibility(View.VISIBLE);
                    errorMessageLabel.setText(R.string.errorMessageTextLabelNoWorkOutID);
                }
                else {
                    //Check the reason for use
                    if (reasonForUse == 2) {
                        //Check if the user selected a client from the spinner
                        if (clientIDFromSpinner != 0 && !clientName.equals("")) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    //Set an intent to redirect the user to the TabMenuManagerActivity activity
                                    Intent intent = new Intent(activity.getApplicationContext(), ViewWorkOutActivity.class);
                                    //Store the userId and user role of the user to be used throughout the application
                                    intent.putExtra("userID", userID);
                                    intent.putExtra("userRole", reasonForUse);
                                    intent.putExtra("trainerID", trainerID);
                                    intent.putExtra("clientID", clientIDFromSpinner);
                                    intent.putExtra("clientName", clientName);
                                    intent.putExtra("workOutID", workOutID);
                                    //Start the next activity
                                    onSwitch(intent);
                                }
                            }).start();
                        }
                        else {
                            //Let the user know that because they are a trainer that they must select a
                            //client from the spinner.
                            errorMessageLabel.setVisibility(View.VISIBLE);
                            errorMessageLabel.setText(R.string.errorMessageTextLabelNoClientSelected);
                        }
                    }
                    else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                //Set an intent to redirect the user to the TabMenuManagerActivity activity
                                Intent intent = new Intent(activity.getApplicationContext(), ViewWorkOutActivity.class);
                                //Store the userId and user role of the user to be used throughout the application
                                intent.putExtra("userID", userID);
                                intent.putExtra("userRole", reasonForUse);
                                intent.putExtra("trainerID", trainerID);
                                intent.putExtra("clientID", clientIDFromSpinner);
                                intent.putExtra("clientName", clientName);
                                intent.putExtra("workOutID", workOutID);
                                //Start the next activity
                                onSwitch(intent);
                            }
                        }).start();
                    }
                }
                break;
            case R.id.viewFitnessStatusButton:
                //Check the reason for use
                if (reasonForUse == 2) {
                    //Check if the user selected a client from the spinner
                    if (clientIDFromSpinner != 0 && !clientName.equals("")) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                //Set an intent to redirect the user to the TabMenuManagerActivity activity
                                Intent intent = new Intent(activity.getApplicationContext(), ViewFitnessStatsActivity.class);
                                //Store the userId and user role of the user to be used throughout the application
                                intent.putExtra("userID", userID);
                                intent.putExtra("userRole", reasonForUse);
                                intent.putExtra("trainerID", trainerID);
                                intent.putExtra("clientID", clientIDFromSpinner);
                                intent.putExtra("clientName", clientName);
                                //Start the next activity
                                onSwitch(intent);
                            }
                        }).start();
                    }
                    else {
                        //Let the user know that because they are a trainer that they must select a
                        //client from the spinner.
                        errorMessageLabel.setVisibility(View.VISIBLE);
                        errorMessageLabel.setText(R.string.errorMessageTextLabelNoClientSelected);
                    }
                }
                else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //Set an intent to redirect the user to the TabMenuManagerActivity activity
                            Intent intent = new Intent(activity.getApplicationContext(), ViewFitnessStatsActivity.class);
                            //Store the userId and user role of the user to be used throughout the application
                            intent.putExtra("userID", userID);
                            intent.putExtra("userRole", reasonForUse);
                            intent.putExtra("trainerID", trainerID);
                            intent.putExtra("clientID", clientIDFromSpinner);
                            intent.putExtra("clientName", clientName);
                            //Start the next activity
                            onSwitch(intent);
                        }
                    }).start();
                }
                break;
            default:
                break;
        }
    }

    /**
     * Redirects a user to a new activity.
     * @param intent a new activity to start
     */
    public void onSwitch(Intent intent) {
        startActivity(intent);
    }
}
