package com.example.android.newsfeed_project;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * A {@link NewsAdapter} knows how to create a list item layout for each news
 * in the data source (a list of {@link News} objects).
 * <p>
 * These list item layouts will be provided to an adapter view like ListView
 * to be displayed to the user.
 */
public class NewsAdapter extends ArrayAdapter<News> {

    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = NewsAdapter.class.getSimpleName();

    private List<News> newses;

    /**
     * Constructs a new {@link NewsAdapter}.
     *
     * @param context  of the app
     * @param newsList is the list of News, which is the data source of the adapter
     */
    public NewsAdapter(Context context, List<News> newsList) {
        super(context, 0, newsList);
        newses = newsList;
    }

    /**
     * Returns a list item view that displays information about the news at the given position
     * in the list of News.
     *
     * @param position    is the position of the view that need to be displayed.
     * @param convertView is the view (We can reuse if available already)
     * @param parent      is the parent ViewGroup of the convertView
     * @return the required view which needs to be displayed to the screen.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // Otherwise, if convertView is null then inflate a new list item layout.
        View itemList = convertView;
        if (itemList == null) {
            itemList = LayoutInflater.from(getContext()).inflate(R.layout.item_list, parent, false);
        }

        // Find the news at the given position in the list of News
        News currentNews = getItem(position);

        // Find the ImageView with the view ID thumbnail
        ImageView thumbnail = itemList.findViewById(R.id.thumbnail);
        // Execute {@link ImageLoaderTask} to load the image
        // by passing the thumbnail in the Constructor(initializing) and thumbnailUrl in the execute method as
        // the argument to Generic parameters.
        if (QueryUtils.isNetworkConnected()) {
            new ImageLoaderTask(thumbnail).execute(currentNews.getThumbnailUrl());
        }
        else {
            thumbnail.setImageResource(R.drawable.image);
        }
        // Find the TextView with the view ID headline
        TextView headline = itemList.findViewById(R.id.headline);
        // Set the headline text of the currentNews in that TextView
        headline.setText(currentNews.getHeadline());

        // Find the ImageView with the view ID date
        TextView date = itemList.findViewById(R.id.date);
        // Set the date value of the currentNews in that TextView
        date.setText(currentNews.getDate());

        // Find the ImageView with the view ID section_name
        TextView sectionName = itemList.findViewById(R.id.section_name);
        // Set the section name of the currentNews in that TextView
        sectionName.setText(currentNews.getSectionName());

        // Return the list item view that is now showing the appropriate data
        return itemList;
    }

    /**
     * A {@link ImageLoaderTask} to display the image(bitmap) from the image link of the (current) book
     * <p>
     * In Generics Parameters,
     * String means we are going to pass String url as an argument while calling execute() method on this {@link ImageLoaderTask} object
     * Void because we are currently not interested tracking the progress of loading the bitmaps
     * Bitmap means we are going to return Bitmap object in the end
     */
    private class ImageLoaderTask extends AsyncTask<String, Void, Bitmap> {

        /**
         * Image in which we need to show book image
         */
        ImageView imageView;

        /**
         * @param thumbnail is the image in which we are going to set the bitmap object
         */
        private ImageLoaderTask(ImageView thumbnail) {
            imageView = thumbnail;
        }

        /**
         * Load the image/bitmap on the background Thread
         *
         * @param urls (at 0th index) of the image link(from which we want to load image)
         * @return the {@link Bitmap} after loading the image from the Url
         */
        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap bitmap = null;
            // If urls array length is less than 1 or its first value(url) is null, then return early
            if (urls.length < 1 || urls[0] == null) {
                return bitmap;
            }

            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                // Url of the image of the book
                URL url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // If the request was successful (response code 200),
                // then read and decode the input stream to create Bitmap object.
                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    inputStream = urlConnection.getInputStream();

                    bitmap = BitmapFactory.decodeStream(inputStream);
                } else {
                    Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
                }
            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "Unable to create URL", e);
//                imageView.setImageResource(R.drawable.ic_launcher_background);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error loading the Bitmap", e);
//                imageView.setImageResource(R.drawable.ic_launcher_background);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    try {
                        // Closing the input stream could throw an IOException, which is why
                        // we need to catch it
                        inputStream.close();
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "Unable to close the InputStream", e);
                    }
                }
            }

            // Return the {@link Bitmap} once loading is done
            return bitmap;
        }

        /**
         * @param result is the bitmap we got from the doInBackground() method
         */
        @Override
        protected void onPostExecute(Bitmap result) {
            // If the result(bitmap) is not null means its available
            if (result != null) {
                // Set this bitmap result on the currentNews ImageView as a bitmap
                imageView.setImageBitmap(result);
            }
/*            // Set the default image as the image resource to the ImageView
            else {
                imageView.setImageResource(R.drawable.ic_launcher_background);
            }*/
        }
    }

}
