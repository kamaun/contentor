package projectomicron.mrfitness;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * This class is a subclass of AppCompatActivity that facilitates the creattion of a workout.
 * Created by Emanuel Guerrero on 12/2/2015.
 */
public class CreateWorkOutActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * Instance variables only visible in the CreateWorkOutActivity class. These are the components
     * of the view
     */
    private TextView clientTextBox;
    private TextView errorMessageLabel;
    private EditText treadMillMilesTextBox;
    private EditText treadMillMinutesTextBox;
    private EditText pushUpsTextBox;
    private EditText sitUpsTextBox;
    private EditText squatsTextBox;
    private Button createWorkOutButton;
    private Button goBackButton;
    private ProgressDialog dialog;

    /**
     * Instance variables only visible in the CreateWorkOutActivity class. These are the extra
     * that are access from intents
     */
    private int userID;
    private int reasonForUse;
    private int trainerID;
    private int clientID;
    private String clientName;

    /**
     * Instance variables only visisble in the CreateWorkOutActivity class. These are the values
     * that the user will input.
     */
    private int treadMillMiles;
    private int treadMillMinutes;
    private int pushUps;
    private int sitUps;
    private int squats;

    /**
     * Overrides the onCreate method inherited from AppCompatActivity to get the references for the
     * components in the view.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get the user id, reason for use, and trainer id from the intent
        Bundle extras = getIntent().getExtras();
        userID = extras.getInt("userID");
        reasonForUse = extras.getInt("userRole");
        trainerID = extras.getInt("trainerID");
        clientID = extras.getInt("clientID");
        clientName = extras.getString("clientName");

        //Get the view of the AppCompatActivity
        setContentView(R.layout.activity_create_workout);

        //Get a reference to the client text box
        clientTextBox = (TextView) findViewById(R.id.clientTextBox);
        //Set the name of the client text box
        clientTextBox.setText(clientName);

        //Get a reference to the error message label
        errorMessageLabel = (TextView) findViewById(R.id.errorMessageLabel);

        //Get references for the text boxes
        treadMillMilesTextBox = (EditText) findViewById(R.id.treadMillMilesTextBox);
        treadMillMinutesTextBox = (EditText) findViewById(R.id.treadMillMinutesTextBox);
        pushUpsTextBox = (EditText) findViewById(R.id.pushUpsTextBox);
        sitUpsTextBox = (EditText) findViewById(R.id.sitUpsTextBox);
        squatsTextBox = (EditText) findViewById(R.id.squatsTextBox);

        //Get references for the buttons
        createWorkOutButton = (Button) findViewById(R.id.editButton);
        goBackButton = (Button) findViewById(R.id.goBackButton);
        //Set onClickListeners for the buttons
        createWorkOutButton.setOnClickListener(this);
        goBackButton.setOnClickListener(this);
    }

    /**
     *Implements the onClick method in the View.OnClickListener Interface to add the event
     * listeners to the buttons.
     * @param v the view of the activity class
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editButton:
                //Get the input from the text boxes
                try {
                    treadMillMiles = Integer.parseInt(treadMillMilesTextBox.getText().toString());
                }
                catch (NumberFormatException e) {
                    treadMillMiles = 0;
                }
                try {
                    treadMillMinutes = Integer.parseInt(treadMillMinutesTextBox.getText().toString());
                }
                catch (NumberFormatException e) {
                    treadMillMinutes = 0;
                }
                try {
                    pushUps = Integer.parseInt(pushUpsTextBox.getText().toString());
                }
                catch (NumberFormatException e) {
                    pushUps = 0;
                }
                try {
                    sitUps = Integer.parseInt(sitUpsTextBox.getText().toString());
                }
                catch (NumberFormatException e) {
                    sitUps = 0;
                }
                try {
                    squats = Integer.parseInt(squatsTextBox.getText().toString());
                }
                catch (NumberFormatException e) {
                    squats = 0;
                }
                //Check if any of the input was zero
                if (treadMillMiles < 0 || treadMillMinutes < 0 || pushUps < 0 || sitUps < 0 ||
                        squats < 0) {
                    //Let the user know that some of the fields are missing
                    errorMessageLabel.setText(R.string.errorMessageTextLabelNegativeValues);
                    errorMessageLabel.setVisibility(View.VISIBLE);
                }
                else {
                    //Start the background thread to creating the workout for the client
                    new CreateWorkOut().execute();
                }
                break;
            case R.id.goBackButton:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //Set an intent to redirect the user to the TabMenuManagerActivity activity
                        Intent intent = new Intent(getApplicationContext(), TabMenuManagerActivity.class);
                        //Store the userId and user role of the user to be used throughout the application
                        intent.putExtra("userID", userID);
                        intent.putExtra("userRole", reasonForUse);
                        intent.putExtra("trainerID", trainerID);
                        intent.putExtra("clientID", clientID);
                        intent.putExtra("clientName", clientName);
                        //Start the next activity
                        onSwitch(intent);
                    }
                }).start();
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

    /**
     * This is a subclass of AsyncTask that creates the workout for the client
     */
    class CreateWorkOut extends AsyncTask<String, String, String> {
        /**
         * Overrides the onPreExecute method inherited from AsyncTask to show a dialog.
         */
        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(CreateWorkOutActivity.this);
            dialog.setMessage("Creating WorkOut...");
            dialog.setIndeterminate(false);
            dialog.setCancelable(true);
            dialog.show();
        }

        /**
         * Implements the doInBackground method in the Interface that AsyncTask implements.
         * @param params auto-generated from the compiler
         * @return a String that indicates if the process is complete
         */
        @Override
        protected String doInBackground(String... params) {
            //Declare a FitnessManager object
            FitnessManager fitnessManager = new FitnessManager();

            //Create the workout for the client and check if it was successfully
            final int workOutID = fitnessManager.createWorkOut(clientID, treadMillMiles,
                                            treadMillMinutes, pushUps, sitUps, squats);
            if (workOutID == -1) {
                //Let the user know that not all of the fields were filled
                CreateWorkOutActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Set the error message label letting the user know that they did not fill
                        //all of the fields
                        errorMessageLabel.setText(R.string.errorMessageTextLabelMissingField);
                        errorMessageLabel.setVisibility(View.VISIBLE);
                    }
                });
            }
            else if (workOutID == -2) {
                //Let the user know that there was a connection error
                CreateWorkOutActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Set the error message label letting the user know that they did not fill
                        //all of the fields
                        errorMessageLabel.setText(R.string.errorMessageTextLabelConnectionError);
                        errorMessageLabel.setVisibility(View.VISIBLE);
                    }
                });
            }
            else {
                //Let the user know that the workout is created. GIve them the workout ID
                CreateWorkOutActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Set the error message label letting the user know that they did not fill
                        //all of the fields
                        errorMessageLabel.setText("WorkOut " + String.valueOf(workOutID) + " was created.\n" +
                                                    "Please click on the go back button");
                        errorMessageLabel.setVisibility(View.VISIBLE);
                    }
                });
            }
            return null;
        }

        /**
         * Overrides the onPostExecute method inherited from AsyncTask to close the dialog box.
         * @param s auto-generated from the compiler
         */
        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
        }
    }
}
