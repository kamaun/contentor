package projectomicron.studybuddy;

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
 * This class is a Fragment class that serves as a central hub to viewing the content status,
 * looking for a previous content, and creating a new content.
 * Created by Whitney Andrews on 4/2/2021.
 */
public class ContentManagerMenuFragmentActivity extends Fragment implements View.OnClickListener {
    /**
     * Instance variables only visible in the ContentManagerMenuFragmentActivity class. These are the
     * components to the view.
     */
    private TextView viewerTextLabel;
    private TextView errorMessageLabel;
    private EditText contentNumberTextBox;
    private Spinner viewerSpinner;
    private Button createContentButton;
    private Button viewContentButton;
    private Button viewContentStatsButton;
    private ProgressDialog dialog;

    /**
     * Instance variables only visible in the ContentManagerMenuFragment class. These are the intent
     * variables used to load data, and distinguish the reason for use.
     */
    private int userID;
    private int reasonForUse;
    private int creatorID;
    private int viewerIDFromSpinner = 0;
    private int contentID;
    private String viewerName = "";

    /**
     * Instance variables only visible in the ContentManagerFragmentActivity class. Contains the URL
     * to the specific URL to the PHP of the webservice to populate the viewer spinner.
     */
    private final String LOADALLVIEWERS_URL = "http://lamp.cse.fau.edu/~eguerre4/webservice/" +
            "loadallclientss.php";

    /**
     * Instance variables that are only visible in the ContentManagerMenuFragmentActivity class.
     * Contains the JSON element ids from the response of the PHP script.
     */
    private final String TAG_SUCCESS = "success";
    private final String TAG_MESSAGE = "message";
    private final String TAG_VIEWERS = "viewers";
    private final String TAG_FIRSTNAME = "firstname";
    private final String TAG_LASTNAME = "lastname";
    private final String TAG_USERID = "userid";

    /**
     * Instance variables that are only visible in the ContentManagerMenuFragmentActivity class.
     * Contains a reference to the activity
     */
    private FragmentActivity activity;

    /**
     * Constructs a ContentManagerMenuFragmentActivity object
     */
    public ContentManagerMenuFragmentActivity() {

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
        //Get the user id, reason for use, and creator id from the intent
        Bundle extras = activity.getIntent().getExtras();
        userID = extras.getInt("userID");
        reasonForUse = extras.getInt("userRole");
        creatorID = extras.getInt("creatorID");

        //Inflate the layout for the ContentManagerMenuFragment activity
        View view = inflater.inflate(R.layout.activity_content_menu_fragment, container, false);

        //Get references to the buttons
        createContentButton = (Button) view.findViewById(R.id.editButton);
        viewContentButton = (Button) view.findViewById(R.id.viewContentButton);
        viewContentStatsButton = (Button) view.findViewById(R.id.viewContentStatusButton);
        //Set on click listeners for the buttons
        createContentButton.setOnClickListener(this);
        viewContentButton.setOnClickListener(this);
        viewContentStatsButton.setOnClickListener(this);

        //Get a reference to the error message label and work out number text box
        errorMessageLabel = (TextView) view.findViewById(R.id.errorMessageLabel);
        contentNumberTextBox = (EditText) view.findViewById(R.id.contentNumnerTextBox);

        //Get a reference to the viewer text label and spinner
        viewerTextLabel = (TextView) view.findViewById(R.id.viewerTextLabel);
        viewerSpinner = (Spinner) view.findViewById(R.id.viewerSpinner);
        //Check the reason for use to see if the user is a creator or a viewer
        if (reasonForUse == 2) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //Build the POST parameters that need to be passed in the request for loading the user's
                    //account
                    List<NameValuePair> postParams = new ArrayList<NameValuePair>();
                    postParams.add(new BasicNameValuePair("creatorid", Integer.toString(userID)));

                    //Make the HTTP request to load the user's account
                    Log.d("request!", "starting");
                    final JSONObject json = JSONWebservice.getInstance().makeHttpRequest(LOADALLVIEWERS_URL, postParams);

                    //Check the log for the json response
                    Log.d("Loading viewers", json.toString());

                    final List<String> viewerList  = new ArrayList<String>();
                    final ArrayList<Integer> viewerID = new ArrayList<Integer>();

                    try {
                        int success = json.getInt(TAG_SUCCESS);
                        if (success == 1 && json.getString(TAG_MESSAGE).equals("Viewers loaded!")) {
                            Log.d("Viewers loaded", json.getString(TAG_MESSAGE));
                            //Get the array of viewers from the JSON object
                            JSONArray viewers = json.getJSONArray(TAG_VIEWERS);
                            //Traverse through the JSON array and copy each element to the viewerList and viewer
                            //ID ArrayList
                            for (int i = 0; i < viewers.length(); i++) {
                                JSONObject viewer = viewers.getJSONObject(i);
                                viewerList.add(viewer.getString(TAG_FIRSTNAME) + " "
                                        + viewer.getString(TAG_LASTNAME));
                                viewerID.add(viewer.getInt(TAG_USERID));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (!viewerList.isEmpty()) {
                        //Create ArrayAdapter using the viewerList ArrayList
                        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                                android.R.layout.simple_spinner_item, viewerList);
                        //Specify the layout to use when the choices appear
                        adapter.setDropDownViewResource(android.
                                R.layout.simple_spinner_dropdown_item);
                        //Apply the adapter to the spinner
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                viewerSpinner.setAdapter(adapter);
                            }
                        });

                        //Set the listener for when the user selects a choice from the spinner
                        viewerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                //Get the position of the item selected
                                int selectedItemPosition = viewerSpinner.getSelectedItemPosition();
                                //Set the receiver id based on the selectedItemPosition
                                viewerIDFromSpinner = viewerID.get(selectedItemPosition);
                                viewerName = viewerList.get(selectedItemPosition);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                //Auto-generated method stub
                            }
                        });
                    }
                    else {
                        //Set the viewer label and spinner invisible
                        viewerTextLabel.setVisibility(View.INVISIBLE);
                        viewerSpinner.setVisibility(View.INVISIBLE);
                        //Set the error message label visible to let the user know that they do not
                        //have any viewers
                        errorMessageLabel.setVisibility(View.VISIBLE);
                        errorMessageLabel.setText(R.string.errorMessageLabelNoViewers);
                    }
                }
            }).start();
        }
        else {
            //Set the viewer label and viewer spinner invisible
            viewerTextLabel.setVisibility(View.INVISIBLE);
            viewerSpinner.setVisibility(View.INVISIBLE);
            //Set the create content button invisible
            createContentButton.setVisibility(View.INVISIBLE);
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
                //Check if the user selected a viewer from the spinner first
                if (viewerIDFromSpinner != 0 && !viewerName.equals("")) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //Set an intent to redirect the user to the TabMenuManagerActivity activity
                            Intent intent = new Intent(activity.getApplicationContext(), CreateContentActivity.class);
                            //Store the userId and user role of the user to be used throughout the application
                            intent.putExtra("userID", userID);
                            intent.putExtra("userRole", reasonForUse);
                            intent.putExtra("creatorID", creatorID);
                            intent.putExtra("viewerID", viewerIDFromSpinner);
                            intent.putExtra("viewerName", viewerName);
                            //Start the next activity
                            onSwitch(intent);
                        }
                    }).start();
                }
                else {
                    //Let the user know that because they are a creator that they must select a
                    //viewer from the spinner.
                    errorMessageLabel.setVisibility(View.VISIBLE);
                    errorMessageLabel.setText(R.string.errorMessageTextLabelNoViewerSelected);
                }
                break;
            case R.id.viewContentButton:
                //Check if the content number text box was filled out
                try {
                    contentID = Integer.parseInt(contentNumberTextBox.getText().toString());
                }
                catch (NumberFormatException e) {
                    contentID = 0;
                }
                if (contentID == 0) {
                    //Let the user know that they need to enter a work out ID
                    errorMessageLabel.setVisibility(View.VISIBLE);
                    errorMessageLabel.setText(R.string.errorMessageTextLabelNoContentID);
                }
                else {
                    //Check the reason for use
                    if (reasonForUse == 2) {
                        //Check if the user selected a viewer from the spinner
                        if (viewerIDFromSpinner != 0 && !viewerName.equals("")) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    //Set an intent to redirect the user to the TabMenuManagerActivity activity
                                    Intent intent = new Intent(activity.getApplicationContext(), ViewContentActivity.class);
                                    //Store the userId and user role of the user to be used throughout the application
                                    intent.putExtra("userID", userID);
                                    intent.putExtra("userRole", reasonForUse);
                                    intent.putExtra("creatorID", creatorID);
                                    intent.putExtra("viewerID", viewerIDFromSpinner);
                                    intent.putExtra("viewerName", viewerName);
                                    intent.putExtra("contentID", contentID);
                                    //Start the next activity
                                    onSwitch(intent);
                                }
                            }).start();
                        }
                        else {
                            //Let the user know that because they are a creator that they must select a
                            //viewer from the spinner.
                            errorMessageLabel.setVisibility(View.VISIBLE);
                            errorMessageLabel.setText(R.string.errorMessageTextLabelNoViewerSelected);
                        }
                    }
                    else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                //Set an intent to redirect the user to the TabMenuManagerActivity activity
                                Intent intent = new Intent(activity.getApplicationContext(), ViewContentActivity.class);
                                //Store the userId and user role of the user to be used throughout the application
                                intent.putExtra("userID", userID);
                                intent.putExtra("userRole", reasonForUse);
                                intent.putExtra("creatorID", creatorID);
                                intent.putExtra("viewerID", viewerIDFromSpinner);
                                intent.putExtra("viewerName", viewerName);
                                intent.putExtra("contentID", contentID);
                                //Start the next activity
                                onSwitch(intent);
                            }
                        }).start();
                    }
                }
                break;
            case R.id.viewContentStatusButton:
                //Check the reason for use
                if (reasonForUse == 2) {
                    //Check if the user selected a viewer from the spinner
                    if (viewerIDFromSpinner != 0 && !viewerName.equals("")) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                //Set an intent to redirect the user to the TabMenuManagerActivity activity
                                Intent intent = new Intent(activity.getApplicationContext(), ViewContentStatsActivity.class);
                                //Store the userId and user role of the user to be used throughout the application
                                intent.putExtra("userID", userID);
                                intent.putExtra("userRole", reasonForUse);
                                intent.putExtra("creatorID", creatorID);
                                intent.putExtra("viewerID", viewerIDFromSpinner);
                                intent.putExtra("viewerName", viewerName);
                                //Start the next activity
                                onSwitch(intent);
                            }
                        }).start();
                    }
                    else {
                        //Let the user know that because they are a creator that they must select a
                        //viewer from the spinner.
                        errorMessageLabel.setVisibility(View.VISIBLE);
                        errorMessageLabel.setText(R.string.errorMessageTextLabelNoViewerSelected);
                    }
                }
                else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //Set an intent to redirect the user to the TabMenuManagerActivity activity
                            Intent intent = new Intent(activity.getApplicationContext(), ViewContentStatsActivity.class);
                            //Store the userId and user role of the user to be used throughout the application
                            intent.putExtra("userID", userID);
                            intent.putExtra("userRole", reasonForUse);
                            intent.putExtra("creatorID", creatorID);
                            intent.putExtra("viewerID", viewerIDFromSpinner);
                            intent.putExtra("viewerName", viewerName);
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
