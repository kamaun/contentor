package group13.studybuddy;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * This class executes http requests to the webservice for loading and saving data to the database.
 * The webservice will a send a response with a JSON string that will be used to create a JSON
 * object. All classes can only have one instance of this class as a singleton.
 * Created by Jaime Andrews on 4/11/2021.
 */
public class JSONWebservice {
    /**
     * Instance variable only visible in the JSONWebservice class.
     */
    private static JSONWebservice instance = new JSONWebservice();

    /**
     * Instance fields only visible in the JSONWebservice class.
     */
    private static InputStream is;
    private static JSONObject jsonObject;
    private static String json = "";

    /**
     * Constructs an empty JSONWebservice object.
     */
    private JSONWebservice() {

    }

    /**
     * Gets an instance of the JSONWebservice class.
     * @return a instance of the JSONWebservice class to call methods to access the database
     */
    public static JSONWebservice getInstance() {
        return instance;
    }

    /**
     * Gets a JSON object from the requested URL.
     * @param url the url to run specific PHP script
     * @param  params a set of POST parameters to send in the request
     * @precondition params.size() > 0
     * @return a JSON object that the object that requested can use
     */
    public JSONObject makeHttpRequest(String url, List<NameValuePair> params) {
        //Make HTTP request
        try {
            // Set the timeout in milliseconds until a connection is established.
            HttpParams httpParameters = new BasicHttpParams();
            int timeoutConnection = 3000;
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            // Set the default socket timeout (SO_TIMEOUT) in milliseconds which is the timeout for
            // waiting for data.
            int timeoutSocket = 5000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

            //Request a POST method
            DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
            HttpPost httpPost = new HttpPost(url);

            //Set the header to define requiring a JSON String object
            httpPost.setHeader("Accept", "json");
            httpPost.setEntity(new UrlEncodedFormEntity(params));

            //Get a response from the server
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        catch (ClientProtocolException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        //Convert the result to a JSON String
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, "utf8"), 8);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
            is.close();
            json = stringBuilder.toString();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        //Try parsing the string to a JSON object
        try {
            jsonObject = new JSONObject(json);
        }
        catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        //Return the JSON String
        return jsonObject;
    }

}
