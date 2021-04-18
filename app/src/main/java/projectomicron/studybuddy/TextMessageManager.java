package projectomicron.studybuddy;

import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * This class manages the text messages that a user creates and uses in the application.
 * Created by Jaime Andrews on 4/7/2021.
 */
public class TextMessageManager {
    /**
     * Instance variables that are only visible in the TextMessageManager class.
     */
    private ArrayList<TextMessage> userTextMessages;
    private JSONArray textMessages = null;

    /**
     * Instance variables that are only visible in the TextMessageManager class. Contains the
     * specific URL that contains the PHP script of the webservice.
     */
    private final String LOADTEXTMESSAGES_URL = "http://lamp.cse.fau.edu/~eguerre4/webservice/" +
                                                    "loadmessages.php";
    private final String SENDTEXTMESSAGE_URL = "http://lamp.cse.fau.edu/~eguerre4/webservice/" +
                                                    "sendmessage.php";
    private final String DELETETEXTMESSAGE_URL = "http://lamp.cse.fau.edu/~eguerre4/webservice/" +
                                                    "deletemessage.php";

    /**
     * Instance variables that are only visible in the TextMessageManager class. Contains the JSON
     * element ids from the response of the PHP script.
     */
    private final String TAG_SUCCESS = "success";
    private final String TAG_MESSAGE = "message";
    private final String TAG_MESSAGEARRAY = "messages";
    private final String TAG_MESSAGEID = "messageid";
    private final String TAG_USERID = "userid";
    private final String TAG_SENDERID = "senderid";
    private final String TAG_DATESENT = "datesent";
    private final String TAG_MESSAGETEXT = "messagetext";

    /**
     * Constructs a TextMessageManager object.
     */
    public TextMessageManager() {
        userTextMessages = new ArrayList<TextMessage>();
    }

    /**
     * Returns an Iterator object that allows clients to traverse the container of the
     * TextMessageManager class and not change the state of the container.
     */
    public Iterator<TextMessage> getTextMessages() {
        return new
                Iterator<TextMessage>() {
                    private int current = 0;

                    @Override
                    public boolean hasNext() {
                        return current < userTextMessages.size();
                    }

                    @Override
                    public TextMessage next() {
                        TextMessage tempTextMessage = userTextMessages.get(current);
                        current++;
                        return tempTextMessage;
                    }
                };
    }

    /**
     * Loads all of the text messages of the user that is logged in.
     * @precondition aUserID > 0
     * @param aUserID the user id of the user that is logged in
     * @return a String value to indicate if the text messages of the user have been successfully
     *          loaded
     */
    public String loadTextMessages(int aUserID) {
        //Declare a variable to store the JSON message
        String jsonMessage;

        //Build the POST parameters that need to be passed in the request for loading all of the
        //text messages of the user
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userid", Integer.toString(aUserID)));

        //Make the HTTP request to loading all of the text messages of the user
        Log.d("request!", "starting");
        JSONObject json = JSONWebservice.getInstance().
                                                    makeHttpRequest(LOADTEXTMESSAGES_URL, params);

        //Check the log for the json response
        Log.d("Loading Text Messages", json.toString());

        //Get the json success tag
        try {
            int success = json.getInt(TAG_SUCCESS);
            if (success == 1 && json.getString(TAG_MESSAGE).equals("Messages loaded!")) {
                Log.d("Text Messages Loaded!", json.getString(TAG_MESSAGE));
                jsonMessage = json.getString(TAG_MESSAGE);
                //Get the array of text messages from the JSON object
                textMessages = json.getJSONArray(TAG_MESSAGEARRAY);
                //Traverse through the JSON array and copy each element to the userTextMessages
                //ArrayList
                for (int i = 0; i < textMessages.length(); i++) {
                    JSONObject textMessage = textMessages.getJSONObject(i);
                    userTextMessages.add(new TextMessage(textMessage.getInt(TAG_MESSAGEID),
                                                         textMessage.getInt(TAG_USERID),
                                                         textMessage.getInt(TAG_SENDERID),
                                                         textMessage.getString(TAG_MESSAGETEXT),
                                                         textMessage.getString(TAG_DATESENT)));
                }
            }
            else if (success == 0 && json.getString(TAG_MESSAGE).equals("No messages found!")) {
                Log.d("No new text messages!", json.getString(TAG_MESSAGE));
                jsonMessage = json.getString(TAG_MESSAGE);
            }
            else {
                Log.d("Connection Error!", json.getString(TAG_MESSAGE));
                jsonMessage = json.getString(TAG_MESSAGE);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            jsonMessage = "Database query error#1!";
            return jsonMessage;
        }

        return jsonMessage;
    }

    /**
     * Sends a text message to the sender's usertextmessage table in the database. Note that we do
     * not need to pass a message id or date sent because the database will take care of creating
     * them for user.
     * @precondition aUserID > 0
     * @precondition aSenderID > 0
     * @precondition aMessageText.size() > 0
     * @param aUserID the user id of the user that will receive the message
     * @param aSenderID the user that is logged in that sent the message
     * @param aMessageText the text that the text message contains
     * @return a String value to indicate if the message was sent successfully
     */
    public String sendMessage(int aUserID, int aSenderID, String aMessageText) {
        //Declare a variable to store the JSON message
        String jsonMessage;

        //Build the POST parameters that need to be passed in the request for sending a text message
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userid", Integer.toString(aUserID)));
        params.add(new BasicNameValuePair("senderid", Integer.toString(aSenderID)));
        params.add(new BasicNameValuePair("messagetext", aMessageText));

        //Make the HTTP request to sending a text message
        Log.d("request!", "starting");
        JSONObject json = JSONWebservice.getInstance().makeHttpRequest(SENDTEXTMESSAGE_URL, params);

        //Check the log for the json response
        Log.d("Sending text message!", json.toString());

        //Get the json success tag
        try {
            int success = json.getInt(TAG_SUCCESS);
            if (success == 1 && json.getString(TAG_MESSAGE).
                                                    equals("Message has been sent successfully!")) {
                Log.d("Message sent!", json.getString(TAG_MESSAGE));
                jsonMessage = json.getString(TAG_MESSAGE);
            }
            else if (success == 0 && json.getString(TAG_MESSAGE).
                                                        equals("Not all fields were entered!")) {
                Log.d("Not all fields filled!", json.getString(TAG_MESSAGE));
                jsonMessage = json.getString(TAG_MESSAGE);
            }
            else {
                Log.d("Sending message failed!", json.getString(TAG_MESSAGE));
                jsonMessage = json.getString(TAG_MESSAGE);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            jsonMessage = "Database query error#1!";
            return jsonMessage;
        }

        return jsonMessage;
    }

    /**
     * Deletes a text message of the user on both the client side and in the usertextmessage table
     * in the database.
     * @precondition aMessageID > 0
     * @param aMessageID the message id of the text message that the user wishes to remove
     * @return a String to indicate if the message was successfully removed from the database and on
     *          the client side
     */
    public String deleteMessage(int aMessageID) {
        //Declare a variable to store the JSON message
        String jsonMessage;

        //Build the POST parameters that need to be passed in the request for deleting a text
        // message
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("messageid", Integer.toString(aMessageID)));

        //Make the HTTP request to deleting a text message
        Log.d("request!", "starting");
        JSONObject json = JSONWebservice.getInstance().makeHttpRequest(DELETETEXTMESSAGE_URL,
                                                                       params);

        //Check the log for the json response
        Log.d("Deleting text message!", json.toString());

        //Get the json success tag
        try {
            int success = json.getInt(TAG_SUCCESS);
            if (success == 1 && json.getString(TAG_MESSAGE).
                                                        equals("Message deleted successfully!")) {
                Log.d("Text Message deleted!", json.getString(TAG_MESSAGE));
                jsonMessage = json.getString(TAG_MESSAGE);
                //Delete the text message from the userTextMessages ArrayList
                for (int i = 0; i < userTextMessages.size();) {
                    if (userTextMessages.get(i).getMessageID() == aMessageID) {
                        userTextMessages.remove(i);
                        break;
                    }
                    else {
                        i++;
                    }
                }
            }
            else {
                Log.d("Deleting failed!", json.getString(TAG_MESSAGE));
                jsonMessage = json.getString(TAG_MESSAGE);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            jsonMessage = "Database query error#1!";
            return jsonMessage;
        }

        return jsonMessage;
    }
}
