package projectomicron.studybuddy;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * This class is a Fragment class that facilitates the user viewing their profile and updating their
 * profile information.
 * Created by Kevin Anderson on 4/3/2021.
 */
public class ProfileFragmentActivity extends Fragment implements View.OnClickListener {
    /**
     * Instance variables only visible in the ProfileFragmentActivity class. These are the components
     * of the view.
     */
    private EditText userFirstNameInputField;
    private EditText userLastNameInputField;
//    private EditText userAgeInputField;
    private EditText userUserNameInputField;
    private EditText userPassWordInputField;
//    private EditText userCertificationNumberInputField;
//    private EditText userInsuranceNumberInputField;
    private EditText creatorNameInputField;
//    private EditText creatorCertificationNumberInputField;
//    private EditText creatorInsuranceNumberInputField;
//    private TextView userCertificationNumberTextLabel;
//    private TextView userInsuranceNumberTextLabel;
    private TextView yourTrainerTextLabel;
    private TextView creatorNameTextLabel;
//    private TextView creatorCertificationNumberTextLabel;
//    private TextView creatorInsuranceNumberTextLabel;
    private TextView errorMessageLabel;
    private Button editButton;
    private Button cancelButton;
    private Button updateButton;
    private Button deleteAccountButton;
    private ProgressDialog dialog;

    /**
     * Instance variables only visible in the ProfileFragmentActivity class. These are the fields
     * that store the data that the user enters.
     */
    private String userName;
    private String passWord;
    private String firstName;
    private String lastName;
    private int userID;
//    private int age = 0;
    private int reasonForUse;
    private int creatorID;
//    private int certificationNumber = 0;
//    private int insuranceNumber = 0;

    /**
     * Instance variables only visible in the ProfileFragmentActivity class. These are placeholder
     * fields that hold the latest data that will be use to revert back to the original state when
     * the user hits the cancel button when updating their account.
     */
    private String tempUserName;
    private String tempPassWord;
    private String tempFirstName;
    private String tempLastName;
//    private int tempAge = 0;
//    private int tempCertificationNumber = 0;
//    private int tempInsuranceNumber = 0;

    /**
     * Instance variables only visible in the ProfileFragmentActivity class. These are
     * AccountManager objects that are used for loading the user's account info and for their
     * associated creator if the user logged in is a client.
     */
    private AccountManager userAccountManager;
    private AccountManager creatorAccountManager;

    private FragmentActivity activity;

    /**
     * Constructs a ProfileFragmentActivity object
     */
    public ProfileFragmentActivity() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (FragmentActivity) activity;
    }

    /**
     * Overrides the onCreate method inherited from Fragment
     * @param savedInstanceState an instance of the activity
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /**
     * Redirects a user to a new activity.
     * @param intent a new activity to start
     */
    public void onSwitch(Intent intent) {
        startActivity(intent);
    }

    /**
     * Overrides the onCreateView method inherited from Fragment to get references
     * to the components.
     * @param inflater
     * @param container a component that stores other components
     * @param savedInstanceState an instance of the activity
     * @return a view layout to create the activity
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the layout for the Messages fragment
        View view = inflater.inflate(R.layout.activity_profile_fragment, container, false);

        //Get references to the text boxes
        userFirstNameInputField = (EditText) view.findViewById(R.id.firstNameTextBox);
        userLastNameInputField = (EditText) view.findViewById(R.id.lastNameTextBox);
//        userAgeInputField = (EditText) view.findViewById(R.id.ageTextBox);
        userUserNameInputField = (EditText) view.findViewById(R.id.userNameTextBox);
        userPassWordInputField = (EditText) view.findViewById(R.id.passWordTextBox);
//        userCertificationNumberInputField = (EditText) view.findViewById(R.id.certificationNumberTextBox);
//        userInsuranceNumberInputField = (EditText) view.findViewById(R.id.insuranceNumberTextBox);
        creatorNameInputField = (EditText) view.findViewById(R.id.creatorFirstNameTextBox);
//        creatorCertificationNumberInputField = (EditText) view.findViewById(R.id.creatorCertificationTextBox);
//        creatorInsuranceNumberInputField = (EditText) view.findViewById(R.id.creatorInsuranceNumberTextBox);

        //Get references to the text labels
//        userCertificationNumberTextLabel = (TextView) view.findViewById(R.id.certificationNumberTextLabel);
//        userInsuranceNumberTextLabel = (TextView) view.findViewById(R.id.insuranceNumberTextLabel);
        yourTrainerTextLabel = (TextView) view.findViewById(R.id.yourCreatorTextLabelProfileFragment);
        creatorNameTextLabel = (TextView) view.findViewById(R.id.creatorNameTextLabel);
//        creatorCertificationNumberTextLabel = (TextView) view.findViewById(R.id.creatorCertificationTextLabel);
//        creatorInsuranceNumberTextLabel = (TextView) view.findViewById(R.id.creatorInsuranceNumberTextLabel);
        errorMessageLabel = (TextView) view.findViewById(R.id.errorMessageTextLabel);

        //Get references to the buttons
        editButton = (Button) view.findViewById(R.id.editButton);
        deleteAccountButton = (Button) view.findViewById(R.id.deleteAccountButton);
        cancelButton = (Button) view.findViewById(R.id.cancelButton);
        updateButton = (Button) view.findViewById(R.id.updateButton);
        //Set onClickListeners for the buttons
        editButton.setOnClickListener(this);
        deleteAccountButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        updateButton.setOnClickListener(this);

        //Get the user id and the reason for use from the intent
        Bundle extras = activity.getIntent().getExtras();
        userID = extras.getInt("userID");
        reasonForUse = extras.getInt("userRole");

        //Configure the layout the user sees based on the reason for use
        if (reasonForUse == 1) {
            //Set the user certification number and insurance number fields invisible
//            userCertificationNumberTextLabel.setVisibility(View.INVISIBLE);
//            userCertificationNumberInputField.setVisibility(View.INVISIBLE);
//            userInsuranceNumberTextLabel.setVisibility(View.INVISIBLE);
//            userInsuranceNumberInputField.setVisibility(View.INVISIBLE);
            //Initialize the user account manager object with the user id of the user that is the
            //client
            userAccountManager = new AccountManager(userID, "", "", "", "", reasonForUse, 0);
            //Start the background thread of loading the user's account information
            new LoadAccount().execute();
        }
        else {
            //Set the your creator fields invisible because the user is a creator
            yourTrainerTextLabel.setVisibility(View.INVISIBLE);
            creatorNameTextLabel.setVisibility(View.INVISIBLE);
            creatorNameInputField.setVisibility(View.INVISIBLE);
//            creatorCertificationNumberTextLabel.setVisibility(View.INVISIBLE);
//            creatorCertificationNumberInputField.setVisibility(View.INVISIBLE);
//            creatorInsuranceNumberTextLabel.setVisibility(View.INVISIBLE);
//            creatorInsuranceNumberInputField.setVisibility(View.INVISIBLE);
            //Initialize the user account manager object with the user id of the user that is the
            //creator
            userAccountManager = new AccountManager(userID, "", "", "", "",  reasonForUse, 0);
            //Start the background thread of loading the user's account information
            new LoadAccount().execute();
        }

        return view;
    }

    /**
     * Overrides onDestroyView method inherited from Fragment to clean up the references to the
     * components of the view.
     */
    @Override
    public void onDestroyView() {
        //Clean up the references to the components of the view to null
        super.onDestroyView();
        userFirstNameInputField = null;
        userLastNameInputField = null;
//        userAgeInputField = null;
        userUserNameInputField = null;
        userPassWordInputField = null;
//        userCertificationNumberInputField = null;
//        userInsuranceNumberInputField = null;
        creatorNameInputField = null;
//        creatorCertificationNumberInputField = null;
//        creatorInsuranceNumberInputField = null;
//        userCertificationNumberTextLabel = null;
//        userInsuranceNumberTextLabel = null;
        yourTrainerTextLabel = null;
        creatorNameTextLabel = null;
//        creatorCertificationNumberTextLabel = null;
//        creatorInsuranceNumberTextLabel = null;
        errorMessageLabel = null;
        editButton = null;
        cancelButton = null;
        updateButton = null;
        deleteAccountButton = null;
        dialog = null;
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
                userFirstNameInputField.setEnabled(true);
                userLastNameInputField.setEnabled(true);
//                userAgeInputField.setEnabled(true);
                userUserNameInputField.setEnabled(true);
                userPassWordInputField.setEnabled(true);
//                userCertificationNumberInputField.setEnabled(true);
//                userInsuranceNumberInputField.setEnabled(true);
                //Make the edit and delete account buttons invisible and set the cancel and update
                //buttons visible
                editButton.setVisibility(View.INVISIBLE);
                deleteAccountButton.setVisibility(View.INVISIBLE);
                cancelButton.setVisibility(View.VISIBLE);
                updateButton.setVisibility(View.VISIBLE);
                break;
            case R.id.cancelButton:
                //Make the text boxes for the user not editable
                userFirstNameInputField.setEnabled(false);
                userLastNameInputField.setEnabled(false);
//                userAgeInputField.setEnabled(false);
                userUserNameInputField.setEnabled(false);
                userPassWordInputField.setEnabled(false);
//                userCertificationNumberInputField.setEnabled(false);
//                userInsuranceNumberInputField.setEnabled(false);
                //Make the cancel and update buttons invisible and set the edit and delete account
                //buttons visible
                cancelButton.setVisibility(View.INVISIBLE);
                updateButton.setVisibility(View.INVISIBLE);
                editButton.setVisibility(View.VISIBLE);
                deleteAccountButton.setVisibility(View.VISIBLE);
                //Update the input fields with the original data from when the account was load in
                //the beginning of the activity
                userFirstNameInputField.setText(tempFirstName);
                userLastNameInputField.setText(tempLastName);
//                userAgeInputField.setText(String.valueOf(tempAge));
                userUserNameInputField.setText(tempUserName);
                userPassWordInputField.setText(tempPassWord);
//                userCertificationNumberInputField.setText(String.valueOf(tempCertificationNumber));
//                userInsuranceNumberInputField.setText(String.valueOf(tempInsuranceNumber));
                break;
            case R.id.deleteAccountButton:
                //Start the background thread to deleting the user's account
                new DeleteAccount().execute();
                break;
            case R.id.updateButton:
                //Get the input that the user typed in the text boxes
                userName = userUserNameInputField.getText().toString();
                passWord = userPassWordInputField.getText().toString();
                firstName = userFirstNameInputField.getText().toString();
                lastName = userLastNameInputField.getText().toString();
//                try {
//                    age = Integer.parseInt(userAgeInputField.getText().toString());
//                }
//                catch (NumberFormatException e) {
//                    age = 0;
//                }

                //Check if any of the input is blank
                if (userName.equals("") || passWord.equals("") || firstName.equals("") ||
                        lastName.equals("") /*|| age == 0*/) {
                    //Set the error message label letting the user know that not all of the fields
                    //were entered
                    errorMessageLabel.setText(R.string.errorMessageTextLabelMissingField);
                    //Make the error message label visible
                    errorMessageLabel.setVisibility(View.VISIBLE);
                    break;
                }
                else {
                    //Check what reason for use they are
                    if (reasonForUse == 1) {
                        //Make the certification number and insurance number zero since the user is
                        //a client
//                        certificationNumber = 0;
//                        insuranceNumber = 0;
                        //Start the background thread for updating the user's account
                        new UpdateAccount().execute();
                    }
                    else {
                        //Get the input that the user typed in for the certification number
                        //and the insurance number since the user is a creator
                        try {
//                            certificationNumber = Integer.parseInt(userCertificationNumberInputField.getText().toString());
//                            insuranceNumber = Integer.parseInt(userInsuranceNumberInputField.getText().toString());
                        }
                        catch (NumberFormatException e) {
                            e.printStackTrace();
//                            certificationNumber = 0;
//                            insuranceNumber = 0;
                        }

                        //Check if the certification number and/or insurance number is blank
//                        if (certificationNumber == 0 || insuranceNumber == 0) {
//                            //Set the error message letting the user know that they need to fill
//                            //out the certification number and/or insurance number
//                            errorMessageLabel.setText(R.string.
//                                    errorMessageTextLabelNoCertificationOrInsuranceNumber);
//                            //Make the error message label visible
//                            errorMessageLabel.setVisibility(View.VISIBLE);
//                        }
//                        else {
//                            //Start the background thread for updating the user's account
//                            new UpdateAccount().execute();
//                        }
                        //Start the background thread for updating the user's account
                        new UpdateAccount().execute();
                    }
                    break;
                }
            default:
                break;
        }
    }



    /**
     * This is a subclass of the AsyncTask class that manages the loading of an account of an user.
     */
    class LoadAccount extends AsyncTask<String, String, String> {
        /**
         * Overrides the onPreExecute method inherited from AsyncTask to show a dialog.
         */
        @Override
        protected void onPreExecute() {
            //Show a dialog letting the user know that the loading account process has begun
            dialog = new ProgressDialog(getContext());
            dialog.setMessage("Loading Account...");
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
            //Check the reason for use of the user
            if (reasonForUse == 1) {
                //Invoke the method to loading the user's account
                //Store the JSON message in a variable
                String loadAccountStatus = userAccountManager.loadAccount();
                if (loadAccountStatus.equals("Account loaded!")) {
                    //Initialize the creator account manager object with the creator id of the client
                    //as the user id
                    creatorAccountManager = new AccountManager(userAccountManager.getTrainerID(), "", "", "", "",  0, 0);
                    //Invoke the method to the loading the user's account but we are technically loading
                    //the creator's account
                    //Store the JSON message in a variable
                    String loadTrainerAccountStatus = creatorAccountManager.loadAccount();
                    if (loadTrainerAccountStatus.equals("Account loaded!")) {
                        //Populate the text boxes with the data of the user
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                userFirstNameInputField.setText(userAccountManager.getFirstName());
                                userLastNameInputField.setText(userAccountManager.getLastName());
//                                userAgeInputField.setText(String.valueOf(userAccountManager.getAge()));
                                userUserNameInputField.setText(userAccountManager.getUserName());
                                userPassWordInputField.setText(userAccountManager.getPassWord());
                                creatorNameInputField.setText(
                                        creatorAccountManager.getFirstName() +
                                                        " " + creatorAccountManager.getLastName());
//                                creatorCertificationNumberInputField.setText(String.valueOf(
//                                        creatorAccountManager.getCertificationNumber()));
//                                creatorInsuranceNumberInputField.setText(String.valueOf(
//                                        creatorAccountManager.getInsuranceNumber()));
                                //Populate the temporary instance fields
                                tempUserName = userAccountManager.getUserName();
                                tempPassWord = userAccountManager.getPassWord();
                                tempFirstName = userAccountManager.getFirstName();
                                tempLastName = userAccountManager.getLastName();
//                                tempAge = userAccountManager.getAge();
                            }
                        });
                    }
                    else if (loadTrainerAccountStatus.equals("Account does not exist!")) {
                        //Let the user know that there was an error with loading the user account and
                        //will redirect to the LoginActivity activity
                        dialog.setMessage("Error loading profile. Returning to login");
                        try {
                            Thread.sleep(15000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //Remove the Intent's extras variables before redirecting
                        activity.getIntent().removeExtra("userID");
                        activity.getIntent().removeExtra("userRole");
                        //Set an intent to redirect the user to the LoginActivity activity
                        Intent intent = new Intent(activity, LoginActivity.class);
                        //Start the next activity
                        onSwitch(intent);
                    }
                    else {
                        //Let the user know that there was an connection error when connecting to the
                        //database and will redirect the user to the LoginActivity activity
                        dialog.setMessage("Connection error! Returning to login");
                        try {
                            Thread.sleep(15000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //Remove the Intent's extras variables before redirecting
                        activity.getIntent().removeExtra("userID");
                        activity.getIntent().removeExtra("userRole");
                        //Set an intent to redirect the user to the LoginActivity activity
                        Intent intent = new Intent(activity, LoginActivity.class);
                        //Start the next activity
                        onSwitch(intent);
                    }
                }
                else if (loadAccountStatus.equals("Account does not exist!")) {
                    //Let the user know that there was an error with loading the user account and
                    //will redirect to the LoginActivity activity
                    dialog.setMessage("Error loading profile. Returning to login");
                    try {
                        Thread.sleep(15000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //Remove the Intent's extras variables before redirecting
                    activity.getIntent().removeExtra("userID");
                    activity.getIntent().removeExtra("userRole");
                    //Set an intent to redirect the user to the LoginActivity activity
                    Intent intent = new Intent(activity, LoginActivity.class);
                    //Start the next activity
                    onSwitch(intent);
                }
                else {
                    //Let the user know that there was an connection error when connecting to the
                    //database and will redirect the user to the LoginActivity activity
                    dialog.setMessage("Connection error! Returning to login");
                    try {
                        Thread.sleep(15000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //Remove the Intent's extras variables before redirecting
                    activity.getIntent().removeExtra("userID");
                    activity.getIntent().removeExtra("userRole");
                    //Set an intent to redirect the user to the LoginActivity activity
                    Intent intent = new Intent(activity, LoginActivity.class);
                    //Start the next activity
                    onSwitch(intent);
                }
            }
            else {
                //Invoke the method to loading the user's account
                //Store the JSON message in a variable
                String loadAccountStatus = userAccountManager.loadAccount();
                if (loadAccountStatus.equals("Account loaded!")) {
                    //Populate the text boxes with the data of the user
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            userFirstNameInputField.setText(userAccountManager.getFirstName());
                            userLastNameInputField.setText(userAccountManager.getLastName());
//                            userAgeInputField.setText(String.valueOf(userAccountManager.getAge()));
                            userUserNameInputField.setText(userAccountManager.getUserName());
                            userPassWordInputField.setText(userAccountManager.getPassWord());
//                            userCertificationNumberInputField.setText(String.valueOf(
//                                    userAccountManager.getCertificationNumber()));
//                            userInsuranceNumberInputField.setText(String.valueOf(
//                                    userAccountManager.getInsuranceNumber()));
                            //Populate the temporary instance fields
                            tempUserName = userAccountManager.getUserName();
                            tempPassWord = userAccountManager.getPassWord();
                            tempFirstName = userAccountManager.getFirstName();
                            tempLastName = userAccountManager.getLastName();
//                            tempAge = userAccountManager.getAge();
//                            tempCertificationNumber = userAccountManager.getCertificationNumber();
//                            tempInsuranceNumber = userAccountManager.getInsuranceNumber();
                        }
                    });
                }
                else if (loadAccountStatus.equals("Account does not exist!")) {
                    //Let the user know that there was an error with loading the user account and
                    //will redirect to the LoginActivity activity
                    dialog.setMessage("Error loading profile. Returning to login");
                    try {
                        Thread.sleep(15000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //Remove the Intent's extras variables before redirecting
                    activity.getIntent().removeExtra("userID");
                    activity.getIntent().removeExtra("userRole");
                    //Set an intent to redirect the user to the LoginActivity activity
                    Intent intent = new Intent(activity, LoginActivity.class);
                    //Start the next activity
                    onSwitch(intent);
                }
                else {
                    //Let the user know that there was an connection error when connecting to the
                    //database and will redirect the user to the LoginActivity activity
                    dialog.setMessage("Connection error! Returning to login");
                    try {
                        Thread.sleep(15000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //Remove the Intent's extras variables before redirecting
                    activity.getIntent().removeExtra("userID");
                    activity.getIntent().removeExtra("userRole");
                    //Set an intent to redirect the user to the LoginActivity activity
                    Intent intent = new Intent(activity, LoginActivity.class);
                    //Start the next activity
                    onSwitch(intent);
                }
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

    /**
     * This is a subclass of the AsyncTask class that manages the updating of an account of an user.
     */
    class UpdateAccount extends AsyncTask<String, String, String> {
        /**
         * Overrides the onPreExecute method inherited from AsyncTask to show a dialog.
         */
        @Override
        protected void onPreExecute() {
            //Show a dialog letting the user know that the updating account process has begun
            dialog = new ProgressDialog(getContext());
            dialog.setMessage("Updating Account...");
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
            //Update the user account manager object with the input that the user type in
            userAccountManager.setFirstName(firstName);
            userAccountManager.setLastName(lastName);
//            userAccountManager.setAge(age);
            userAccountManager.setUserName(userName);
            userAccountManager.setPassWord(passWord);
//            userAccountManager.setCertificationNumber(certificationNumber);
//            userAccountManager.setInsuranceNumber(insuranceNumber);

            //Invoke the method to updating the user's account
            //Store the JSON message in a variable
            String updateAccountStatus = userAccountManager.updateAccount();
            if (updateAccountStatus.equals("Account updated successfully!")) {
                //Let the user know that their account has been successfully updated
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Set the error message label letting the user know that their account has
                        //been successfully updated
                        errorMessageLabel.setText(R.string.
                                errorMessageTextLabelAccountUpdatedSuccessfully);
                        //Make the error message label visible
                        errorMessageLabel.setVisibility(View.VISIBLE);
                        //Set the cancel and update buttons invisible and the edit and delete
                        //account buttons visible
                        cancelButton.setVisibility(View.INVISIBLE);
                        updateButton.setVisibility(View.INVISIBLE);
                        editButton.setVisibility(View.VISIBLE);
                        deleteAccountButton.setVisibility(View.VISIBLE);
                        //Update the temporary variables with the new data
                        tempUserName = userName;
                        tempPassWord = passWord;
                        tempFirstName = firstName;
                        tempLastName = lastName;
//                        tempAge = age;
//                        tempCertificationNumber = certificationNumber;
//                        tempInsuranceNumber = insuranceNumber;
                        //Make the text boxes not editable
                        userFirstNameInputField.setEnabled(false);
                        userLastNameInputField.setEnabled(false);
//                        userAgeInputField.setEnabled(false);
                        userUserNameInputField.setEnabled(false);
                        userPassWordInputField.setEnabled(false);
//                        userCertificationNumberInputField.setEnabled(false);
//                        userInsuranceNumberInputField.setEnabled(false);
                    }
                });
            }
            else if (updateAccountStatus.equals("The username is already taken!")) {
                //Let the user know that the username entered is already taken
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Set the error message label letting the user know that the username entered
                        //is already taken
                        errorMessageLabel.setText(R.string.
                                errorMessageTextLabelUserNameAlreadyTaken);
                        //Make the error message label visible
                        errorMessageLabel.setVisibility(View.VISIBLE);
                    }
                });
            }
            else if (updateAccountStatus.equals("Not all fields were entered!")) {
                //Let the user know that not all of the fields were entered when sending the data
                //the database
                activity.runOnUiThread(new Runnable() {
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
                activity.runOnUiThread(new Runnable() {
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

        /**
         * Overrides the onPostExecute method inherited from AsyncTask to close the dialog box.
         * @param s auto-generated from the compiler
         */
        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
        }
    }

    /**
     * This is a subclass of the AsyncTask class that manages the deleting of an account of an user.
     */
    class DeleteAccount extends AsyncTask<String, String, String> {
        /**
         * Overrides the onPreExecute method inherited from AsyncTask to show a dialog.
         */
        @Override
        protected void onPreExecute() {
            //Show a dialog letting the user know that the delete account process has begun
            dialog = new ProgressDialog(getContext());
            dialog.setMessage("Deleting Account...");
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
            //Invoke the method to delete the user's account
            //Store the JSON message in a variable
            String deleteAccountStatus = userAccountManager.deleteAccount();
            if (deleteAccountStatus.equals("Account deleted successfully!")) {
                //Let the user know that there was an error with loading the user account and
                //will redirect to the LoginActivity activity
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        dialog.setMessage("Profile has been deleted. Returning to login");
//                        try {
//                            Thread.sleep(15000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
                //Remove the Intent's extras variables before redirecting
                activity.getIntent().removeExtra("userID");
                activity.getIntent().removeExtra("userRole");
                //Set an intent to redirect the user to the LoginActivity activity
                Intent intent = new Intent(activity, LoginActivity.class);
                //Start the next activity
                onSwitch(intent);
            }
            else if (deleteAccountStatus.equals("User's account does not exist!")) {
                //Let the user know that there was an error with deleting the user account and
                //will redirect to the LoginActivity activity
//                activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        dialog.setMessage("Error deleting profile. Returning to login");
//                        try {
//                            Thread.sleep(15000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
                //Remove the Intent's extras variables before redirecting
                activity.getIntent().removeExtra("userID");
                activity.getIntent().removeExtra("userRole");
                //Set an intent to redirect the user to the LoginActivity activity
                Intent intent = new Intent(activity, LoginActivity.class);
                //Start the next activity
                onSwitch(intent);
            }
            else {
                //Let the user know that there was a connection error when connecting to
                //the database
                activity.runOnUiThread(new Runnable() {
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
