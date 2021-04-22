package group13.studybuddy;

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
 * This class is a subclass of AppCompatActivity that facilitates the creattion of a content.
 * Created by Jaime Andrews on 4/2/2021.
 */
public class CreateContentActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * Instance variables only visible in the CreateContentActivity class. These are the components
     * of the view
     */
    private TextView viewerTextBox;
    private TextView errorMessageLabel;
    private EditText contentEditText;
    private EditText treadMillMilesTextBox;
    private EditText treadMillMinutesTextBox;
    private EditText pushUpsTextBox;
    private EditText sitUpsTextBox;
    private EditText squatsTextBox;
    private Button createContentButton;
    private Button goBackButton;
    private ProgressDialog dialog;

    /**
     * Instance variables only visible in the CreateContentActivity class. These are the extra
     * that are access from intents
     */
    private int userID;
    private int reasonForUse;
    private int creatorID;
    private int viewerID;
    private String viewerName;
    private  final SQLDatabaseConnection sqlDatabaseConnection = new SQLDatabaseConnection(this);


    /**
     * Instance variables only visisble in the CreateContentActivity class. These are the values
     * that the user will input.
     */
    private String textContent;
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

        //Get the user id, reason for use, and creator id from the intent
        Bundle extras = getIntent().getExtras();
        userID = extras.getInt("userID");
        reasonForUse = extras.getInt("userRole");
        creatorID = extras.getInt("creatorID");
        viewerID = extras.getInt("viewerID");
        viewerName = extras.getString("viewerName");

        //Get the view of the AppCompatActivity
        setContentView(R.layout.activity_create_content);

        //Get a reference to the viewer text box
        viewerTextBox = (TextView) findViewById(R.id.viewerTextBox);

        contentEditText = (EditText) findViewById(R.id.contentEditText);
        //Set the name of the viewer text box
        viewerTextBox.setText(viewerName);

        //Get a reference to the error message label
        errorMessageLabel = (TextView) findViewById(R.id.errorMessageLabel);

        //Get references for the buttons
        createContentButton = (Button) findViewById(R.id.editButton);
        goBackButton = (Button) findViewById(R.id.goBackButton);
        //Set onClickListeners for the buttons
        createContentButton.setOnClickListener(this);
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
                textContent = contentEditText.getText().toString();
                //Start the background thread to creating the content for the viewer
                new CreateContent().execute();

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
                        intent.putExtra("creatorID", creatorID);
                        intent.putExtra("viewerID", viewerID);
                        intent.putExtra("viewerName", viewerName);
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
     * This is a subclass of AsyncTask that creates the content for the viewer
     */
    class CreateContent extends AsyncTask<String, String, String> {
        /**
         * Overrides the onPreExecute method inherited from AsyncTask to show a dialog.
         */
        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(CreateContentActivity.this);
            dialog.setMessage("Creating Content...");
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
            //Declare a ContentManager object
            ContentManager contentManager = new ContentManager();

            //Create the content for the viewer and check if it was successfully
            final long contentID = sqlDatabaseConnection.createContent(viewerID, textContent);
            if (contentID == -1) {
                //Let the user know that not all of the fields were filled
                CreateContentActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Set the error message label letting the user know that they did not fill
                        //all of the fields
                        errorMessageLabel.setText(R.string.errorMessageTextLabelMissingField);
                        errorMessageLabel.setVisibility(View.VISIBLE);
                    }
                });
            }
            else if (contentID == -2) {
                //Let the user know that there was a connection error
                CreateContentActivity.this.runOnUiThread(new Runnable() {
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
                //Let the user know that the content is created. GIve them the content ID
                CreateContentActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Set the error message label letting the user know that they did not fill
                        //all of the fields
                        errorMessageLabel.setText("Content " + String.valueOf(contentID) + " was created.\n" +
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
