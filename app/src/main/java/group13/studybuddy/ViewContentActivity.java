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
 * This class is a subclass of AppCompatActivity
 * Created by Kevin Anderson on 4/2/2021.
 */
public class ViewContentActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * Instance variables only visible in the ViewContentActivity class. These are the components to
     * the view.
     */
    private TextView viewerTextLabel;
    private TextView contentIDTextBox;
    private EditText viewerTextBox;
    private TextView errorMessageLabel;
    private Button cancelButton;
    private Button editButton;
    private Button updateButton;
    private Button goBackButton;
    private ProgressDialog dialog;
    private  final SQLDatabaseConnection sqlDatabaseConnection = new SQLDatabaseConnection(this);

    /**
     * Instance variables only visible in the ViewContentActivity class. These are the extra
     * that are access from intents
     */
    private int userID;
    private int reasonForUse;
    private int creatorID;
    private int clientID;
    private int contentID;
    private String viewerName;
    private String textContent;


    /**
     * Instance variables only visible in the ViewContentActivity class. This is the
     * ContentManager object that will be used to load a content and update the content only if the
     * user is a creator.
     */
    private ContentManager contentManager;

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
        clientID = extras.getInt("viewerID");
        viewerName = extras.getString("viewerName");
        contentID = extras.getInt("contentID");

        //Initialize the ContentManager
        contentManager = new ContentManager();

        //Get the view of the AppCompatActivity
        setContentView(R.layout.activity_view_content);

        //Get references to the text views
        viewerTextLabel = (TextView) findViewById(R.id.viewerTextLabel);
        viewerTextBox = (EditText) findViewById(R.id.viewerTextBox);
        contentIDTextBox = (TextView) findViewById(R.id.contentIDTextBox);

        //Get a reference to the error message label
        errorMessageLabel = (TextView) findViewById(R.id.errorMessageLabel);

        //Get references to the text boxes


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
            viewerTextLabel.setVisibility(View.VISIBLE);
            viewerTextBox.setVisibility(View.VISIBLE);
            //Make the edit box visible
            editButton.setVisibility(View.VISIBLE);
            //Start a new thread to load the client's content
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //Invoke the method to load the clients content information based on their
                    //userID and contentID
                    final Content content = sqlDatabaseConnection.loadContent(clientID, contentID);
                    //Check the contentID that was returned
                    if (content.getContentID() == -1) {
                        //Let the user know that the content does not exist
                        ViewContentActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                errorMessageLabel.setText(R.string.errorMessageTextLabelContentIDNotFound);
                                errorMessageLabel.setVisibility(View.VISIBLE);
                                editButton.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                    else if (content.getContentID() == -2) {
                        //Let the user know that there was a connection error
                        ViewContentActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                errorMessageLabel.setText(R.string.errorMessageTextLabelConnectionError);
                                errorMessageLabel.setVisibility(View.VISIBLE);
                                editButton.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                    else {
                        //Populate the view with the contents of the content object
                        ViewContentActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                viewerTextLabel.setText(viewerName);
                                contentIDTextBox.setText(String.valueOf(content.getContentID()));
                                viewerTextBox.setText(content.getContentText());
                                contentID = content.getContentID();
                            }
                        });
                    }
                }
            }).start();
        }
        else {
            //Start a new thread to load the client's content
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //Invoke the method to load the clients content information based on their
                    //userID and contentID
                    final Content content = sqlDatabaseConnection.loadContent(userID, contentID);
                    //Check the contentID that was returned
                    if (content.getContentID() == -1) {
                        //Let the user know that the content does not exist
                        ViewContentActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                errorMessageLabel.setText(R.string.errorMessageTextLabelContentIDNotFound);
                                errorMessageLabel.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                    else if (content.getContentID() == -2) {
                        //Let the user know that there was a connection error
                        ViewContentActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                errorMessageLabel.setText(R.string.errorMessageTextLabelConnectionError);
                                errorMessageLabel.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                    else {
                        //Populate the view with the contents of the content object
                        ViewContentActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                contentIDTextBox.setText(String.valueOf(content.getContentID()));
                                viewerTextBox.setText(content.getContentText());
                                contentID = content.getContentID();
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

                viewerTextBox.setEnabled(true);
                editButton.setVisibility(View.INVISIBLE);
                goBackButton.setVisibility(View.INVISIBLE);
                cancelButton.setVisibility(View.VISIBLE);
                updateButton.setVisibility(View.VISIBLE);
                break;
            case R.id.cancelButton:
                //Make the text boxes not editable
                viewerTextBox.setEnabled(false);

                //Make the cancel and update buttons invisible and set the edit and delete account
                //buttons visible
                cancelButton.setVisibility(View.INVISIBLE);
                updateButton.setVisibility(View.INVISIBLE);
                editButton.setVisibility(View.VISIBLE);
                goBackButton.setVisibility(View.VISIBLE);

                break;
            case R.id.updateButton:
                //Get the input from the text boxes
                textContent = viewerTextBox.getText().toString();

                //Start the background thread to updating the content for the client
                new UpdateContent().execute();

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
                        intent.putExtra("clientID", clientID);
                        intent.putExtra("clientName", viewerName);
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
    class UpdateContent extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            //Show a dialog letting the user know that the updating account process has begun
            dialog = new ProgressDialog(ViewContentActivity.this);
            dialog.setMessage("Updating Account...");
            dialog.setIndeterminate(false);
            dialog.setCancelable(true);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            //Invoke the method to updating a user's content
            //Store the JSON message in a variable
            String contentUpdatedStauts = sqlDatabaseConnection.updateContent(clientID, contentID, textContent);
            if (contentUpdatedStauts.equals("Content updated successfully!")) {
                //Let the user know that the content has been updated successfully
                ViewContentActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        errorMessageLabel.setText(R.string.errorMessageTextLabelContentUpdated);
                        errorMessageLabel.setVisibility(View.VISIBLE);
                        //Set the cancel and update buttons invisible and edit button visible
                        cancelButton.setVisibility(View.INVISIBLE);
                        updateButton.setVisibility(View.INVISIBLE);
                        editButton.setVisibility(View.VISIBLE);
                        goBackButton.setVisibility(View.VISIBLE);
                        //Update the temporary variables with the new data
//                        *********
                        //Make the text boxes not editable
                        viewerTextBox.setEnabled(false);

                    }
                });
            }
            else if (contentUpdatedStauts.equals("Not all fields were entered!")) {
                ViewContentActivity.this.runOnUiThread(new Runnable() {
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
                ViewContentActivity.this.runOnUiThread(new Runnable() {
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
