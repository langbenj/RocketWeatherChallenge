package langco.rocketweatherchallenge;

import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;



//Data is read in an AsyncTask thread to prevent freezes and other errors.
public class WeatherJSONReader extends AsyncTask<String,Integer,ArrayList<String>> {

    @Override
    public ArrayList<String> doInBackground(String... urls) {
        //Initialize the return value, this will be built as the data is read
        String return_value ="";

        //We'll only be using 1 URL but this piece of code could be modified to read multiple URLs.
        for (String url : urls) {
            URL built_url;
            try {
                built_url = new URL(url);
                try {
                    //Open the connection to the site specified by the URL passed into the class
                    HttpURLConnection http_connection = (HttpURLConnection) built_url.openConnection();
                    try {
                        //As the data comes in through the InputStream it is converted into a readable String format
                        //through use of BufferedReader as each line comes in it's appended to the return value
                        InputStream input = new BufferedInputStream(http_connection.getInputStream());
                        BufferedReader buffer_stream = new BufferedReader(new InputStreamReader(input));
                        String buffer_line;
                        while ((buffer_line = buffer_stream.readLine()) != null) {
                            return_value += buffer_line;
                        }
                    }
                    finally {
                        //When the read is complete disconnect the http connection to clean up memory and stop conflicts
                        http_connection.disconnect();
                    }
                } catch (IOException e) {
                    //Print the stack trace for debugging if there's an IO exception
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                //If the URL is not formed correctly print out the stack trace for debugging
                e.printStackTrace();
            }
        }

        //Parse the output from the url
        ArrayList<String> temperatures= new ArrayList<>();
        ArrayList<String> descriptions= new ArrayList<>();
        String start_period="Tonight";
        try {
            JSONObject full_json_object = new JSONObject(return_value);
            //Generate data arrays for temperatures and descriptions
            JSONObject data_object = full_json_object.getJSONObject("data");
            JSONArray temperature_array = data_object.getJSONArray("temperature");
            JSONArray description_array = data_object.getJSONArray("text");
            for (int i=0; i<temperature_array.length(); i++) {
                temperatures.add(temperature_array.getString(i));
            }
            for (int i=0; i<description_array.length(); i++) {
                descriptions.add(description_array.getString(i));
            }

            //Grab the first value in the Start Period Name. This is used to determine if the format
            //is for the day or night
            JSONObject time_object = full_json_object.getJSONObject("time");
            JSONArray day_array = time_object.getJSONArray("startPeriodName");
            start_period = day_array.getString(0);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // The feed changes based on the time of day. If it's between 5:00 AM and 6:00 PM the
        // first value in the returned data is today. Otherwise it's tonight. This throws off
        // all of the calculations unless I check for it. Sneaky.

        //This is where we generate the strings that will be used to fill the views in the viewpager
        ArrayList<String> result_list= new ArrayList<>();
        int offset;
        if (start_period.equals("Tonight")) {
            offset = 1;
        }
        else {
            offset = 0; 
        }
        for (int x = 0; x < 5; x++) {
            result_list.add("High: \n" + temperatures.get((x * 2) + offset) + "\n\n" +
                    "Description: \n" + descriptions.get((x * 2) + offset));
            }

        //Return the results
        return result_list;
    }


    protected void onPostExecute(ArrayList<String> result) {
        //Registers this class into the Otto Library's bus
        //Otto is used to communicate between Fragments and Activities.
        App.bus.register(this);
        App.bus.post(new BusEventHandler(result));
    }
}
