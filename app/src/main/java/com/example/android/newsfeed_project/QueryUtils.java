package com.example.android.newsfeed_project;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {

    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();
    private static Context mContext;
    private static int pages;

    private static int currentPage;

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {

    }

    /**
     * Query The Guardian API and return a list of {@link News} objects.
     */
    public static List<News> fetchNewsList(String requestUrl, Context context) {
        mContext = context;
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the Http request", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link News}
        List<News> newsList = extractFeatureFromJson(jsonResponse);
        // Return the list of {@link News}
        return newsList;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies that an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);

                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link News} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<News> extractFeatureFromJson(String newsJson) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJson)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding news to
        List<News> newsList = new ArrayList<News>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Create a JSONObject from the JSON response string
            JSONObject rootJson = new JSONObject(newsJson);

            // Extract the JSONObject for the key called "response"
            // which contains all the News and relevant information(fields)
            JSONObject response = rootJson.optJSONObject("response");

            currentPage = response.getInt("currentPage");
            pages = response.optInt("pages");

            //TODO: get more field to make pagination like feature

            // Extract the JSONArray associated with the key called "results",
            // which represents a list of features (or news).
            JSONArray results = response.optJSONArray("results");
            // For each news in the results array, create a {@link News} object
            for (int i = 0; i < results.length(); i++) {

                // Get a single news at position i within the list of news
                JSONObject currentNews = results.optJSONObject(i);

                // Extract the value for the key called "webPublicationDate"
                String dateString = currentNews.optString("webPublicationDate");
                // Extract the value for the key called "sectionName"
                String sectionName = currentNews.optString("sectionName");
                // Extract the value for the key called "webUrl"
                String webUrl = currentNews.optString("thumbnail");

                // For a given news, extract the JSONObject associated with the
                // key called "fields", which represents a list of all fields
                // for that news.
                JSONObject fields = currentNews.optJSONObject("fields");

                // Extract the value for the key called "headline"
                String headline = fields.optString("headline");

                // Extract the value for the key called "thumbnail"
                String thumbnailUrl = fields.optString("thumbnail");

                // Create a new {@link News} object with the headline, sectionName, thumbnailUrl,
                // and converted Date from the JSON response.
                News news = new News(headline, sectionName, thumbnailUrl, convertDate(dateString), webUrl);

                // Add the new {@link News} to the list of news.
                newsList.add(news);
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of News
        return newsList;
    }

    /**
     * Return a String Date with our custom Date format which has been converted from
     * default format of Date string we are getting through response.
     */
    private static String convertDate(String dateString) {

        // This is the format of the date in which
        // we are getting the Date string value from parsing the response
        SimpleDateFormat formatterIn = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        // This is our required format of the date in which
        // we want our Date string value to store and display
        SimpleDateFormat formatterOut = new SimpleDateFormat("d MMM yyy\nh:mm a");

        // Try to convert the Date from one format to another
        // and if the exception occurs meanwhile, then catch it so our app doesn't crash
        String convertedDate = null;
        try {
            Date date = formatterIn.parse(dateString);
            convertedDate = formatterOut.format(date);
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Error converting the date to our required format", e);
        }

        // Return the date string in our required date format
        return convertedDate;
    }

    public static int getPages() {
        return pages;
    }

    public static int getCurrentPage() {
        return currentPage;
    }

    public static boolean isNetworkConnected() {
        ConnectivityManager connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }

}