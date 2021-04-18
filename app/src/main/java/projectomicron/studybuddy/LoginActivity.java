package projectomicron.studybuddy;

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
 * This class is an AppCompatActivity class that facilitates the login functionality for a user.
 * Created by Kevin Anderson on 4/9/2021.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * Instance variables only visible in the LoginActivity class. These are the components of the
     * view.
     */
    private EditText userNameInputField;
    private EditText passwordInputField;
    private TextView errorMessageLabel;
    private Button loginButton;
    private Button createAccountButton;
    private ProgressDialog dialog;

    /**
     * Instance variables only visible in the LoginActivity class. These are the fields that store
     * the text the user entered from the text boxes.
     */
    private String userName;
    private String passWord;

    /**
     * Overrides the onCreate method inherited from AppCompatActivity to get the references for the
     * components in the view.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Get references to the text boxes
        userNameInputField = (EditText) findViewById(R.id.userName);
        passwordInputField = (EditText) findViewById(R.id.passWord);

        //Get a reference to the error message label
        errorMessageLabel = (TextView) findViewById(R.id.errorMessageTextLabel);

        //Get references to the buttons
        loginButton = (Button) findViewById(R.id.loginButton);
        createAccountButton = (Button) findViewById(R.id.createAccountButton);

        //Set onClickListeners for the buttons
        loginButton.setOnClickListener(this);
        createAccountButton.setOnClickListener(this);
    }

    /**
     * Redirects a user to a new activity.
     * @param intent a new activity to start
     */
    public void onSwitch(Intent intent) {
        startActivity(intent);
    }

    /**
     * Implements the onClick method in the View.OnClickListener Interface to add the event
     * listeners to the buttons.
     * @param v the view of the activity class
     * @precondition userNameInputField.getText().toString().size() > 0
     * @precondition passwordInputField.getText().toString().size() > 0
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginButton:
                //Get the input that the user typed in the text boxes
                userName = userNameInputField.getText().toString();
                passWord = passwordInputField.getText().toString();

                //Check if any of the input is blank
                if (userName.equals("") || passWord.equals("")) {
                    //Set the error message label letting the user know that not all of the fields
                    //were entered
                    errorMessageLabel.setText(R.string.errorMessageTextLabelMissingField);
                    //Make the error message label visible
                    errorMessageLabel.setVisibility(View.VISIBLE);
                    break;
                }
                else {
                    //Start the background thread of authenticating the user's login
                    new LoginAuthentication().execute();
                    break;
                }
            case R.id.createAccountButton:
                //Set an intent to redirect the user to the CreateAccountActivity activity
                Intent intent = new Intent(getApplicationContext(), CreateAccountActivity.class);
                //Start the next activity
                onSwitch(intent);
            default:
                break;
        }
    }

    /**
     * This is a subclass of the AsyncTask class that manages the login authentication of an user.
     */
    class LoginAuthentication extends AsyncTask<String, String, String> {
        /**
         * Overrides the onPreExecute method inherited from AsyncTask to show a dialog.
         */
        @Override
        protected void onPreExecute() {
            //Show a dialog letting the user know that the login process has begun
            dialog = new ProgressDialog(LoginActivity.this);
            dialog.setMessage("Logging in...");
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
            //Declare a LoginAuthenticator object
            final LoginAuthenticator loginAuthenticator = new LoginAuthenticator();

            //Change the state of the LoginAuthenticator object with the input from the user
            loginAuthenticator.setUserName(userName);
            loginAuthenticator.setPassWord(passWord);

            //Do a check of the user's entered credentials to see if they exist in the database
            //Store the JSON message in a variable
            String loginStatus = loginAuthenticator.checkCredentials();
            if (loginStatus.equals("Login successful!")) {
                //Get the userId of the user
                loginAuthenticator.queryUserIDAndUserRole();

               //Check if the userId is zero
                if (loginAuthenticator.getUserID() == 0) {
                    //Let the user know that there was a connection error
                    LoginActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Set the error message label letting the user know that there was a
                            //a connection error
                            errorMessageLabel.setText(R.string.errorMessageTextLabelConnectionError);
                            //Make the error message label visible
                            errorMessageLabel.setVisibility(View.VISIBLE);
                        }
                    });
                }
                else {
                    //Set an intent to redirect the user to the TabMenuManagerActivity activity
                    Intent intent = new Intent(getApplicationContext(), TabMenuManagerActivity.class);
                    //Store the userId and user role of the user to be used throughout the application
                    intent.putExtra("userID", loginAuthenticator.getUserID());
                    intent.putExtra("userRole", loginAuthenticator.getUserRole());
                    intent.putExtra("creatorID", loginAuthenticator.getTrainerID());
                    //Start the next activity
                    onSwitch(intent);
                }
            }
            else if (loginStatus.equals("Invalid Credentials!")) {
                //Let the user know that their login credentials are invalid
                LoginActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Set the error message label letting the user know that their login
                        //credentials were invalid
                        errorMessageLabel.setText(R.string.errorMessageTextLabelInvalidLogin);
                        //Make the error message label visible
                        errorMessageLabel.setVisibility(View.VISIBLE);
                    }
                });
            }
            else {
                //Let the user know that there was a connection error
                LoginActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Set the error message label letting the user know that there was a
                        //a connection error
                        errorMessageLabel.setText(R.string.errorMessageTextLabelConnectionError);
                        //Make the error message label visible
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
