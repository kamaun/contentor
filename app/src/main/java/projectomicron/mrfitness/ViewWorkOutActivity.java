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
 * This class is a subclass of AppCompatActivity
 * Created by Emanuel Guerrero on 12/2/2015.
 */
public class ViewWorkOutActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * Instance variables only visible in the ViewWorkOutActivity class. These are the components to
     * the view.
     */
    private TextView clientTextLabel;
    private TextView workOutIDTextBox;
    private TextView clientTextBox;
    private TextView errorMessageLabel;
    private EditText treadMillMilesTextBox;
    private EditText treadMillMinutesTextBox;
    private EditText pushUpsTextBox;
    private EditText sitUpsTextBox;
    private EditText squatsTextBox;
    private Button cancelButton;
    private Button editButton;
    private Button updateButton;
    private Button goBackButton;
    private ProgressDialog dialog;

    /**
     * Instance variables only visible in the ViewWorkOutActivity class. These are the extra
     * that are access from intents
     */
    private int userID;
    private int reasonForUse;
    private int trainerID;
    private int clientID;
    private int workOutID;
    private String clientName;

    /**
     * Instance variables only visisble in the ViewWorkOutActivity class. These are the values
     * that the user will input.
     */
    private int treadMillMiles;
    private int treadMillMinutes;
    private int pushUps;
    private int sitUps;
    private int squats;

    /**
     * Instance variables only visible in the ViewWorkOutActivity class. These are placeholder
     * fields that hold the latest data that will be use to revert back to the original state when
     * the user hits the cancel button when updating their account.
     */
    private int tempTreadMillMiles;
    private int tempTreadMillMinutes;
    private int tempPushUps;
    private int tempSitUps;
    private int tempSquats;

    /**
     * Instance variables only visible in the ViewWorkOutActivity class. This is the
     * FitnessManager object that will be used to load a workout and update the workout only if the
     * user is a trainer.
     */
    private FitnessManager fitnessManager;

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
        workOutID = extras.getInt("workOutID");

        //Initialize the FitnessManager
        fitnessManager = new FitnessManager();

        //Get the view of the AppCompatActivity
        setContentView(R.layout.activity_view_workout);

        //Get references to the text views
        clientTextLabel = (TextView) findViewById(R.id.clientTextLabel);
        clientTextBox = (TextView) findViewById(R.id.clientTextBox);
        workOutIDTextBox = (TextView) findViewById(R.id.workOutIDTextBox);

        //Get a reference to the error message label
        errorMessageLabel = (TextView) findViewById(R.id.errorMessageLabel);

        //Get references to the text boxes
        treadMillMilesTextBox = (EditText) findViewById(R.id.treadMillMilesTextBox);
        treadMillMinutesTextBox = (EditText) findViewById(R.id.treadMillMinutesTextBox);
        pushUpsTextBox = (EditText) findViewById(R.id.pushUpsTextBox);
        sitUpsTextBox = (EditText) findViewById(R.id.sitUpsTextBox);
        squatsTextBox = (EditText) findViewById(R.id.squatsTextBox);

        //Get references to the buttons
        cancelButton = (Button) findViewById(R.id.cancelButton);
        editButton = (Button) findViewById(R.id.editButton);
        updateButton = (Button) findViewById(R.id.updateButton);
        goBackButton = (Button) findViewById(R.id.goBackButton);
        //Set onClickerListeners for the buttons
        cancelButton.setOnClickListener(this);
        editButton.setOnClickListener(this);
        updateButton.setOnClickListener(this);
        goBackButton.setOnClickListener(this);

        //Check the reason for use for the user to determine how to populate the view
        if (reasonForUse == 2) {
            //Make the client text label and text box appear
            clientTextLabel.setVisibility(View.VISIBLE);
            clientTextBox.setVisibility(View.VISIBLE);
            //Make the edit box visible
            editButton.setVisibility(View.VISIBLE);
            //Start a new thread to load the client's workout
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //Invoke the method to load the clients workout information based on their
                    //userID and workoutID
                    final WorkOut workOut = fitnessManager.loadWorkout(clientID, workOutID);
                    //Check the workOutID that was returned
                    if (workOut.getWorkOutID() == -1) {
                        //Let the user know that the workout does not exist
                        ViewWorkOutActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                errorMessageLabel.setText(R.string.errorMessageTextLabelWorkOutIDNotFound);
                                errorMessageLabel.setVisibility(View.VISIBLE);
                                editButton.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                    else if (workOut.getWorkOutID() == -2) {
                        //Let the user know that there was a connection error
                        ViewWorkOutActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                errorMessageLabel.setText(R.string.errorMessageTextLabelConnectionError);
                                errorMessageLabel.setVisibility(View.VISIBLE);
                                editButton.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                    else {
                        //Populate the view with the contents of the workOut object
                        ViewWorkOutActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                clientTextBox.setText(clientName);
                                workOutIDTextBox.setText(String.valueOf(workOut.getWorkOutID()));
                                treadMillMilesTextBox.setText(String.valueOf(workOut.getTreadMillMiles()));
                                tempTreadMillMiles = workOut.getTreadMillMiles();
                                treadMillMinutesTextBox.setText(String.valueOf(workOut.getTreadMillMinutes()));
                                tempTreadMillMinutes = workOut.getTreadMillMinutes();
                                pushUpsTextBox.setText(String.valueOf(workOut.getPushUps()));
                                tempPushUps = workOut.getPushUps();
                                sitUpsTextBox.setText(String.valueOf(workOut.getSitUps()));
                                tempSitUps = workOut.getSitUps();
                                squatsTextBox.setText(String.valueOf(workOut.getSquats()));
                                tempSquats = workOut.getSquats();
                                workOutID = workOut.getWorkOutID();
                            }
                        });
                    }
                }
            }).start();
        }
        else {
            //Start a new thread to load the client's workout
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //Invoke the method to load the clients workout information based on their
                    //userID and workoutID
                    final WorkOut workOut = fitnessManager.loadWorkout(userID, workOutID);
                    //Check the workOutID that was returned
                    if (workOut.getWorkOutID() == -1) {
                        //Let the user know that the workout does not exist
                        ViewWorkOutActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                errorMessageLabel.setText(R.string.errorMessageTextLabelWorkOutIDNotFound);
                                errorMessageLabel.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                    else if (workOut.getWorkOutID() == -2) {
                        //Let the user know that there was a connection error
                        ViewWorkOutActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                errorMessageLabel.setText(R.string.errorMessageTextLabelConnectionError);
                                errorMessageLabel.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                    else {
                        //Populate the view with the contents of the workOut object
                        ViewWorkOutActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                workOutIDTextBox.setText(String.valueOf(workOut.getWorkOutID()));
                                treadMillMilesTextBox.setText(String.valueOf(workOut.getTreadMillMiles()));
                                tempTreadMillMiles = workOut.getTreadMillMiles();
                                treadMillMinutesTextBox.setText(String.valueOf(workOut.getTreadMillMinutes()));
                                tempTreadMillMinutes = workOut.getTreadMillMinutes();
                                pushUpsTextBox.setText(String.valueOf(workOut.getPushUps()));
                                tempPushUps = workOut.getPushUps();
                                sitUpsTextBox.setText(String.valueOf(workOut.getSitUps()));
                                tempSitUps = workOut.getSitUps();
                                squatsTextBox.setText(String.valueOf(workOut.getSquats()));
                                tempSquats = workOut.getSquats();
                                workOutID = workOut.getWorkOutID();
                            }
                        });
                    }
                }
            }).start();
        }
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
                //Make the text boxes for the user editable
                treadMillMilesTextBox.setEnabled(true);
                treadMillMinutesTextBox.setEnabled(true);
                pushUpsTextBox.setEnabled(true);
                sitUpsTextBox.setEnabled(true);
                squatsTextBox.setEnabled(true);
                editButton.setVisibility(View.INVISIBLE);
                goBackButton.setVisibility(View.INVISIBLE);
                cancelButton.setVisibility(View.VISIBLE);
                updateButton.setVisibility(View.VISIBLE);
                break;
            case R.id.cancelButton:
                //Make the text boxes not editable
                treadMillMilesTextBox.setEnabled(false);
                treadMillMinutesTextBox.setEnabled(false);
                pushUpsTextBox.setEnabled(false);
                sitUpsTextBox.setEnabled(false);
                squatsTextBox.setEnabled(false);
                //Make the cancel and update buttons invisible and set the edit and delete account
                //buttons visible
                cancelButton.setVisibility(View.INVISIBLE);
                updateButton.setVisibility(View.INVISIBLE);
                editButton.setVisibility(View.VISIBLE);
                goBackButton.setVisibility(View.VISIBLE);
                //Update the input fields with the original data from when the account was load in
                //the beginning of the activity
                treadMillMilesTextBox.setText(String.valueOf(tempTreadMillMiles));
                treadMillMinutesTextBox.setText(String.valueOf(tempTreadMillMinutes));
                pushUpsTextBox.setText(String.valueOf(tempPushUps));
                sitUpsTextBox.setText(String.valueOf(tempSitUps));
                squatsTextBox.setText(String.valueOf(tempSquats));
                break;
            case R.id.updateButton:
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
                    //Start the background thread to updating the workout for the client
                    new UpdateWorkOut().execute();
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
     * This is a subclass of AsyncTask that manages the updating of a work out
     */
    class UpdateWorkOut extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            //Show a dialog letting the user know that the updating account process has begun
            dialog = new ProgressDialog(ViewWorkOutActivity.this);
            dialog.setMessage("Updating Account...");
            dialog.setIndeterminate(false);
            dialog.setCancelable(true);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            //Invoke the method to updating a user's workout
            //Store the JSON message in a variable
            String workOutUpdatedStauts = fitnessManager.updateWorkOut(clientID, workOutID, treadMillMiles,
                                                                        treadMillMinutes, pushUps, sitUps, squats);
            if (workOutUpdatedStauts.equals("Workout updated successfully!")) {
                //Let the user know that the workout has been updated successfully
                ViewWorkOutActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        errorMessageLabel.setText(R.string.errorMessageTextLabelWorkOutUpdated);
                        errorMessageLabel.setVisibility(View.VISIBLE);
                        //Set the cancel and update buttons invisible and edit button visible
                        cancelButton.setVisibility(View.INVISIBLE);
                        updateButton.setVisibility(View.INVISIBLE);
                        editButton.setVisibility(View.VISIBLE);
                        goBackButton.setVisibility(View.VISIBLE);
                        //Update the temporary variables with the new data
                        tempTreadMillMiles = treadMillMiles;
                        tempTreadMillMinutes = treadMillMinutes;
                        tempPushUps = pushUps;
                        tempSitUps = sitUps;
                        tempSquats = squats;
                        //Make the text boxes not editable
                        treadMillMilesTextBox.setEnabled(false);
                        treadMillMinutesTextBox.setEnabled(false);
                        pushUpsTextBox.setEnabled(false);
                        sitUpsTextBox.setEnabled(false);
                        squatsTextBox.setEnabled(false);

                    }
                });
            }
            else if (workOutUpdatedStauts.equals("Not all fields were entered!")) {
                ViewWorkOutActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Set the error message label letting the user know that not all of the
                        //fields were entered
                        errorMessageLabel.setText(R.string.errorMessageTextLabelMissingField);
                        //Make the error message label visible
                        errorMessageLabel.setVisibility(View.VISIBLE);
                    }
                });
            }
            else {
                //Let the user know that there was a connection error when connecting to
                //the database
                ViewWorkOutActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Set the error message letting the user know that there was a connection
                        //error
                        errorMessageLabel.setText(R.string.errorMessageTextLabelConnectionError);
                        //Make the error message label visible
                        errorMessageLabel.setVisibility(View.VISIBLE);
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
        }
    }
}
