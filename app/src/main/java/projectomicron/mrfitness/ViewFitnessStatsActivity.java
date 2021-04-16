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
 * This class is a subclass of AppCompatActivity that facilitates the viewing and updating of an
 * users fitness stats.
 * Created by Emanuel Guerrero on 12/2/2015.
 */
public class ViewFitnessStatsActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * Instance variables only visible in the ViewFitnessStatsActivity class
     */
    private TextView clientTextLabel;
    private TextView errorMessageLabel;
    private TextView clientTextBox;
    private EditText weightTextBox;
    private EditText feetTextBox;
    private EditText inchesTextBox;
    private EditText BMITextBox;
    private Button editButton;
    private Button goBackButton;
    private Button cancelButton;
    private Button updateButton;
    private ProgressDialog dialog;

    /**
     * Instance variables only visible in the ViewFitnessStatsActivity class. These are the extra
     * that are access from intents
     */
    private int userID;
    private int reasonForUse;
    private int trainerID;
    private int clientID;
    private int workOutID;
    private String clientName;

    /**
     * Instance variables only visisble in the ViewFitnessStatsActivity class. These are the values
     * that the user will input.
     */
    private int weight;
    private int heightFeet;
    private int heightInches;

    /**
     * Instance variables only visible in the ViewFitnessStatsActivity class. These are placeholder
     * fields that hold the latest data that will be use to revert back to the original state when
     * the user hits the cancel button when updating their account.
     */
    private int tempWeight;
    private int tempHeightFeet;
    private int tempHeightInches;

    /**
     * Instance variables only visible in the ViewFitnessStatsActivity class. This is the
     * FitnessManager object that will be used to load a fitness stat and update only if the
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
        setContentView(R.layout.activity_view_fitness_stats);

        //Get references to the text labels
        clientTextBox = (TextView) findViewById(R.id.clientTextBox);
        clientTextLabel = (TextView) findViewById(R.id.clientTextLabel);

        //Get a reference to the error message label
        errorMessageLabel = (TextView) findViewById(R.id.errorMessageLabel);

        //Get references to the text boxes
        weightTextBox = (EditText) findViewById(R.id.weightTextBox);
        feetTextBox = (EditText) findViewById(R.id.feetTextBox);
        inchesTextBox = (EditText) findViewById(R.id.inchesTextBox);
        BMITextBox = (EditText) findViewById(R.id.BMITextBox);

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
                    final FitnessStats fitnessStats = fitnessManager.loadFitnessStats(clientID);
                    //Check the workOutID that was returned
                    if (fitnessStats.getWeight() == -1) {
                        //Let the user know that the workout does not exist
                        ViewFitnessStatsActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                errorMessageLabel.setText(R.string.errorMessageTextLabelNoFitnessStats);
                                errorMessageLabel.setVisibility(View.VISIBLE);
                                editButton.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                    else if (fitnessStats.getWeight() == -2) {
                        //Let the user know that there was a connection error
                        ViewFitnessStatsActivity.this.runOnUiThread(new Runnable() {
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
                        ViewFitnessStatsActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                clientTextBox.setText(clientName);
                                weightTextBox.setText(String.valueOf(fitnessStats.getWeight()));
                                tempWeight = fitnessStats.getWeight();
                                feetTextBox.setText(String.valueOf(fitnessStats.getHeightFeet()));
                                tempHeightFeet = fitnessStats.getHeightFeet();
                                inchesTextBox.setText(String.valueOf(fitnessStats.getHeightInches()));
                                tempHeightInches = fitnessStats.getHeightInches();
                                BMITextBox.setText(String.valueOf(fitnessStats.getBMI()));
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
                    final FitnessStats fitnessStats = fitnessManager.loadFitnessStats(userID);
                    //Check the workOutID that was returned
                    if (fitnessStats.getWeight() == -1) {
                        //Let the user know that the workout does not exist
                        ViewFitnessStatsActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                errorMessageLabel.setText(R.string.errorMessageTextLabelWorkOutIDNotFound);
                                errorMessageLabel.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                    else if (fitnessStats.getWeight() == -2) {
                        //Let the user know that there was a connection error
                        ViewFitnessStatsActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                errorMessageLabel.setText(R.string.errorMessageTextLabelConnectionError);
                                errorMessageLabel.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                    else {
                        //Populate the view with the contents of the workOut object
                        ViewFitnessStatsActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                clientTextBox.setText(clientName);
                                weightTextBox.setText(String.valueOf(fitnessStats.getWeight()));
                                tempWeight = fitnessStats.getWeight();
                                feetTextBox.setText(String.valueOf(fitnessStats.getHeightFeet()));
                                tempHeightFeet = fitnessStats.getHeightFeet();
                                inchesTextBox.setText(String.valueOf(fitnessStats.getHeightInches()));
                                tempHeightInches = fitnessStats.getHeightInches();
                                BMITextBox.setText(String.valueOf(fitnessStats.getBMI()));
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
                weightTextBox.setEnabled(true);
                feetTextBox.setEnabled(true);
                inchesTextBox.setEnabled(true);
                editButton.setVisibility(View.INVISIBLE);
                goBackButton.setVisibility(View.INVISIBLE);
                cancelButton.setVisibility(View.VISIBLE);
                updateButton.setVisibility(View.VISIBLE);
                break;
            case R.id.cancelButton:
                //Make the text boxes not editable
                weightTextBox.setEnabled(false);
                feetTextBox.setEnabled(false);
                inchesTextBox.setEnabled(false);
                //Make the cancel and update buttons invisible and set the edit and delete account
                //buttons visible
                cancelButton.setVisibility(View.INVISIBLE);
                updateButton.setVisibility(View.INVISIBLE);
                editButton.setVisibility(View.VISIBLE);
                goBackButton.setVisibility(View.VISIBLE);
                //Update the input fields with the original data from when the account was load in
                //the beginning of the activity
                weightTextBox.setText(String.valueOf(tempWeight));
                feetTextBox.setText(String.valueOf(tempHeightFeet));
                inchesTextBox.setText(String.valueOf(tempHeightInches));
                break;
            case R.id.updateButton:
                //Get the input from the text boxes
                try {
                    weight = Integer.parseInt(weightTextBox.getText().toString());
                }
                catch (NumberFormatException e) {
                    weight = 0;
                }
                try {
                    heightFeet = Integer.parseInt(feetTextBox.getText().toString());
                }
                catch (NumberFormatException e) {
                    heightFeet = 0;
                }
                try {
                    heightInches = Integer.parseInt(inchesTextBox.getText().toString());
                }
                catch (NumberFormatException e) {
                    heightInches = 0;
                }
                //Check if any of the input was zero
                if (weight < 0 || heightFeet < 0 || heightInches < 0) {
                    //Let the user know that some of the fields are missing
                    errorMessageLabel.setText(R.string.errorMessageTextLabelNegativeValues);
                    errorMessageLabel.setVisibility(View.VISIBLE);
                }
                else {
                    //Start the background thread to updating the workout for the client
                    new UpdateFitnessStats().execute();
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
     * This is a subclass of AsyncTask that manages the updating of a fitness
     */
    class UpdateFitnessStats extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            //Show a dialog letting the user know that the updating account process has begun
            dialog = new ProgressDialog(ViewFitnessStatsActivity.this);
            dialog.setMessage("Updating Account...");
            dialog.setIndeterminate(false);
            dialog.setCancelable(true);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            //Invoke the method to updating a user's fitness status
            //Store the JSON message in a variable
            String workOutUpdatedStatus = fitnessManager.updateFitnessStats(clientID, weight, heightFeet, heightInches);
            if (workOutUpdatedStatus.equals("Fitness stats updated successfully!")) {
                //Let the user know that the workout has been updated successfully
                ViewFitnessStatsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        errorMessageLabel.setText(R.string.errorMessageTextLabelFitnessStatsUpdatedSuccessful);
                        errorMessageLabel.setVisibility(View.VISIBLE);
                        //Set the cancel and update buttons invisible and edit button visible
                        cancelButton.setVisibility(View.INVISIBLE);
                        updateButton.setVisibility(View.INVISIBLE);
                        editButton.setVisibility(View.VISIBLE);
                        goBackButton.setVisibility(View.VISIBLE);
                        //Update the temporary variables with the new data
                        tempWeight = weight;
                        tempHeightFeet = heightFeet;
                        tempHeightInches = heightInches;
                        //Get the new BMI
                        int tmpBMI = fitnessManager.calculateBMI(weight, heightFeet, heightInches);
                        BMITextBox.setText(String.valueOf(tmpBMI));
                        //Make the text boxes not editable
                        weightTextBox.setEnabled(false);
                        feetTextBox.setEnabled(false);

                    }
                });
            }
            else if (workOutUpdatedStatus.equals("Not all fields were entered!")) {
                ViewFitnessStatsActivity.this.runOnUiThread(new Runnable() {
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
                ViewFitnessStatsActivity.this.runOnUiThread(new Runnable() {
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
