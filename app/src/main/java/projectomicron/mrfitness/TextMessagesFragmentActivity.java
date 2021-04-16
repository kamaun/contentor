package projectomicron.mrfitness;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
 * This class is a Fragment class that facilitates the user viewing, sending, and deleting text
 * messages
 * Created by Emanuel Guerrero on 11/12/2015.
 */
public class TextMessagesFragmentActivity extends Fragment implements View.OnClickListener {
    /**
     * Instance variables only visible in the TextMessagesFragmentActivity class. These are the
     * components of the view.
     */
    private ListView textMessagesListView;
    private TextView errorMessageLabel;
    private Button composeMessageButton;
    private Button deleteMessageButton;
    private ProgressDialog dialog;

    /**
     * Instance variables only visible in the TextMessagesFragmentActivity class.
     */
    private int userID;
    private int reasonForUse;
    private int trainerID;
    private int receiverID = 0;
    private String messageText;
    private ArrayList<TextMessage> tempTextMessagesListView;
    private ArrayList<String> textMessageContent;

    /**
     * Instance variables only visible in the TextMessagesFragmentActivity class. These are used in
     * deleting a message from the List view and from the user's text messages.
     */
    private int messageID = 0;
    private int textMessagePosition = -1;

    /**
     * Instance variables only visible in the TextMessagesFragmentActivity class. These are
     * TextMessageManager objects that are used for loading, sending, and deleting text messages of
     * a user.
     */
    private TextMessageManager userTextMessageManager;

    /**
     * Instance variables only visible in the TextMessagesFragmentActivity class. This is the custom
     * adpater used to populate the List View
     */
    private ArrayAdapter<String> arrayAdapter;

    /**
     * Instance variables only visible in the TextMessagesFragmentActivity class. Contains the URL
     * to the specific URL to the PHP of the webservice.
     */
    private final String LOADALLCLIENTS_URL = "http://lamp.cse.fau.edu/~eguerre4/webservice/" +
            "loadallclients.php";

    /**
     * Instance variables that are only visible in the textMessagesFragmentActivity class. Contains
     * the JSON element ids from the response of the PHP script.
     */
    private final String TAG_SUCCESS = "success";
    private final String TAG_MESSAGE = "message";
    private final String TAG_CLIENTS = "clients";
    private final String TAG_FIRSTNAME = "firstname";
    private final String TAG_LASTNAME = "lastname";
    private final String TAG_USERID = "userid";

    private FragmentActivity activity;

    /**
     * Constructs a TextMessagesFragment object
     */
    public TextMessagesFragmentActivity() {

    }

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

        //Initialize the userTextMessageManger object
        userTextMessageManager = new TextMessageManager();

        //Inflate the layout for the Messages fragment
        View view = inflater.inflate(R.layout.activity_text_messages_fragment, container, false);

        //Get a reference to the error message label
        errorMessageLabel = (TextView) view.findViewById(R.id.errorMessageLabel);

        //Get references to the buttons
        composeMessageButton = (Button) view.findViewById(R.id.composeMessageButton);
        deleteMessageButton = (Button) view.findViewById(R.id.deleteMessageButton);
        //Set OnClickListeners to the buttons
        composeMessageButton.setOnClickListener(this);
        deleteMessageButton.setOnClickListener(this);

        //Get a reference to the List View
        textMessagesListView = (ListView) view.findViewById(R.id.textMessagesListView);
        textMessagesListView.setClickable(true);
        //Set an onItemClickListener for the List View
        textMessagesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Make the delete button visible
                deleteMessageButton.setEnabled(true);
                //Get the message id of the particular text message in the row
                messageID = tempTextMessagesListView.get(position).getMessageID();
                textMessagePosition = position;
            }
        });

        //Start the background thread for loading the text messages
        new LoadMessages().execute();

        return view;
    }

    /**
     * Overrides onDestroyView method inherited from Fragment to clean up the references to the
     * components of the view.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //Clean up the references to the components of the view to null
        textMessagesListView = null;
        errorMessageLabel = null;
        composeMessageButton = null;
        deleteMessageButton = null;
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
            case R.id.composeMessageButton:
                //Create the dialog box to allow the user to create and send a message
                LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                View promptView = layoutInflater.inflate(R.layout.
                        activity_dialog_compose_message, null);
                AlertDialog.Builder composeMessageDialog = new AlertDialog.Builder(getContext());
                //Set the layout of the dialog box
                composeMessageDialog.setView(promptView);
                //Get the components in the view
                final Spinner clientSpinner = (Spinner) promptView.findViewById(R.id.clientDialogSpiner);
                final TextView toTextLabel = (TextView) promptView.findViewById(R.id.toDialogTextLabel);
                final TextView trainerTextLabel = (TextView) promptView.findViewById(R.id.trainerDialogTextLabel);
                final EditText messageContents = (EditText) promptView.findViewById(R.id.messageContentsDialogTextBox);
                final TextView errorMessageTextLabel = (TextView) promptView.findViewById(R.id.errorMessageTextLabel);
                //Check the reason for use
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

                            List<String> clientList  = new ArrayList<String>();
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
                                //Make the clientSpinner visible
                                toTextLabel.setVisibility(View.VISIBLE);
                                clientSpinner.setVisibility(View.VISIBLE);
                                //Create ArrayAdapter using the clientList ArrayList
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                                        android.R.layout.simple_spinner_item, clientList);
                                //Specify the layout to use when the choices appear
                                adapter.setDropDownViewResource(android.
                                        R.layout.simple_spinner_dropdown_item);
                                //Apply the adapter to the spinner
                                clientSpinner.setAdapter(adapter);

                                //Set the listener for when the user selects a choice from the spinner
                                clientSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        //Get the position of the item selected
                                        int selectedItemPosition = clientSpinner.getSelectedItemPosition();
                                        //Set the receiver id based on the selectedItemPosition
                                        receiverID = clientID.get(selectedItemPosition);
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                        //Auto-generated method stub
                                    }
                                });
                            }
                            else {
                                //Make the trainerTextLabel visible
                                toTextLabel.setVisibility(View.VISIBLE);
                                trainerTextLabel.setVisibility(View.VISIBLE);
                                //Let the user know that they do not have any clients
                                trainerTextLabel.setText(R.string.errorMessageLabelNoClients);
                            }
                        }
                    }).start();
                }
                //Set up the dialog box
                composeMessageDialog.setCancelable(false)
                        .setPositiveButton("Send",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        //Get the user input from the text box
                                        messageText = messageContents.getText().toString();
                                        //Check if the input is blank
                                        if (messageText.equals("") || messageText.isEmpty()) {
                                            //Set the error message label letting the
                                            // user know that the message is empty
                                            errorMessageTextLabel.setText(R.string.errorMessageTextLabelMissingField);
                                            //Set the error message label visible
                                            errorMessageTextLabel.setVisibility(View.VISIBLE);
                                        }
                                        else {
                                            //Check the reason for use
                                            if (reasonForUse == 2) {
                                                //Check if they selected a client
                                                if (receiverID == 0) {
                                                    //Set the error message label letting
                                                    //the user know that they need to select
                                                    //a client
                                                    errorMessageTextLabel.setText(R.string.errorMessageTextLabelNoClientSelected);
                                                    //Set the error message label visible
                                                    errorMessageTextLabel.setVisibility(View.VISIBLE);
                                                }
                                                else {
                                                    //Start the background thread to sending a message
                                                    new SendMessage().execute();

                                                }
                                            }
                                            else {
                                                //Start the background thread to sending a message
                                                receiverID = trainerID;
                                                new SendMessage().execute();

                                            }
                                        }
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                //Create a composeMessageDialog object
                AlertDialog alertDialog = composeMessageDialog.create();
                alertDialog.show();
                break;
            case R.id.deleteMessageButton:
                //Check if the message id and the text message position is not zero
                if (messageID != 0 && textMessagePosition >= 0) {
                    deleteMessageButton.setEnabled(false);
                    //Start the background thread to deleting a message
                    new DeleteMessage().execute();
                }
                break;
            default:
                break;
        }
    }

    /**
     * This is a subclass of the AsyncTask class that manages the loading of a user's messages.
     */
    class LoadMessages extends AsyncTask<String, String, String> {
        /**
         * Overrides the onPreExecute method inherited from AsyncTask to show a dialog.
         */
        @Override
        protected void onPreExecute() {
            //Show a dialog letting the user know that the loading text messages process has begun
            dialog = new ProgressDialog(getContext());
            dialog.setMessage("Loading Text Messages...");
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
            //Invoke the method to loading the text messages of the user
            //Store the JSON message in a variable
            String loadTextMessagesStatus = userTextMessageManager.loadTextMessages(userID);
            if (loadTextMessagesStatus.equals("Messages loaded!")) {
                //Request an Iterator object to copy the elements from the state of the
                //userTextMessageManager so not affecting the current state of the
                //userTextMessageManager object
                Iterator<TextMessage> iterator = userTextMessageManager.getTextMessages();
                //Copy the contents into a new ArrayList
                tempTextMessagesListView = new ArrayList<TextMessage>();
                textMessageContent = new ArrayList<String>();
                while (iterator.hasNext()) {
                    TextMessage textMessage = iterator.next();
                    tempTextMessagesListView.add(textMessage);
                    textMessageContent.add("From: " + textMessage.getSenderID() + "\n" +
                            textMessage.getMessageText());
                }
                //Create a CustomAdapter object
                //customAdapter = new CustomAdapter(TextMessagesFragmentActivity.this, tempTextMessagesListView);
                arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, textMessageContent );
                //Set the adapter to the ListView
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textMessagesListView.setAdapter(arrayAdapter);
                        //Make the buttons visible
                        composeMessageButton.setVisibility(View.VISIBLE);
                        deleteMessageButton.setVisibility(View.VISIBLE);
                    }
                });
            }
            else if (loadTextMessagesStatus.equals("No messages found!")) {
                //Let the user that they do not have any text messages
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textMessagesListView.setVisibility(View.INVISIBLE);
                        //Set the error message label letting the user know that they do not have
                        //any messages
                        errorMessageLabel.setText(R.string.errorMessageTextLabelNoMessages);
                        //Set the error message label visible
                        errorMessageLabel.setVisibility(View.VISIBLE);
                    }
                });
            }
            else {
                //Let the user know that there was a connection error with the database
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textMessagesListView.setVisibility(View.INVISIBLE);
                        //Set the error message label letting the user know that there was a
                        //connection error with the database
                        errorMessageLabel.setText(R.string.errorMessageTextLabelConnectionError);
                        //Set the error message label visible
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
     * This is a subclass of the AsyncTask class that manages the sending of a text message to
     * another user.
     */
    class SendMessage extends AsyncTask<String, String, String> {
        /**
         * Overrides the onPreExecute method inherited from AsyncTask to show a dialog.
         */
        @Override
        protected void onPreExecute() {
            //Show a dialog letting the user know that the sending text message process has begun
            dialog = new ProgressDialog(getContext());
            dialog.setMessage("Sending Text Message...");
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
            //Invoke the method to sending the text messages of the user
            //Store the JSON message in a variable
            String textMessageStatusSent = userTextMessageManager.sendMessage(receiverID, userID, messageText);
            if (textMessageStatusSent.equals("Message has been sent successfully!")) {
                //Let the user know that their message has been sent
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.setMessage("Your message has been sent");
                        try {
                            Thread.sleep(15000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            else if (textMessageStatusSent.equals("Not all fields were entered!")) {
                //Let the user know that they did not fill out all of the fields
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.setMessage("Not all of the fields were filled");
                        try {
                            Thread.sleep(15000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            else {
                //Let the user know that there was a connection error with the database
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.setMessage("There is an error with the connection");
                        try {
                            Thread.sleep(15000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
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
     * This is a subclass of the AsyncTask class that manages the deletion of a message.
     */
    class DeleteMessage extends AsyncTask<String, String, String> {
        /**
         * Overrides the onPreExecute method inherited from AsyncTask to show a dialog.
         */
        @Override
        protected void onPreExecute() {
            //Show a dialog letting the user know that the deleting text message process has begun
            dialog = new ProgressDialog(getContext());
            dialog.setMessage("Deleting Text Message...");
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
            //Invoke the method to deleting the text messages of the user
            //Store the JSON message in a variable
            String textMessageDeletedStatus = userTextMessageManager.deleteMessage(messageID);
            if (textMessageDeletedStatus.equals("Message deleted successfully!")) {

                //Delete the row that contains the deleted text message in the List View
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Delete the text message in the tempTextMessageListView ArrayList to maintain the
                        //list
                        tempTextMessagesListView.remove(textMessagePosition);
                        textMessageContent.remove(textMessagePosition);
                        arrayAdapter.notifyDataSetChanged();
                        messageID = 0;
                        textMessagePosition = -1;
                        //Check if all of the messages have been deleted
                        if (tempTextMessagesListView.isEmpty()) {
                            //Let the user know that they do not have anymore messages
                            errorMessageLabel.setText(R.string.errorMessageTextLabelNoMessages);
                            //Set the error message label visible
                            errorMessageLabel.setVisibility(View.VISIBLE);
                        }
                        //Let the user that the text message was deleted
                        dialog.setMessage("The message has been deleted");
                        try {
                            Thread.sleep(15000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            else {
                //Let the user know that there was a connection error with the database
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.setMessage("There is an error with the connection");
                        try {
                            Thread.sleep(15000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
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

