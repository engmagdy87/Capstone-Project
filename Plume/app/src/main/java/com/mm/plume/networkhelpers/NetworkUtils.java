package com.mm.plume.networkhelpers;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by MM on 12/24/2017.
 */

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();


    private static final String query = "q";
    private static final String maxResults = "maxResults";
    //    https://www.googleapis.com/books/v1/volumes?q=flowers+inauthor:keys
    private static final String STATIC_BASE_URL = "https://www.googleapis.com/books/v1/volumes";
    


    public static URL buildUrl(String keyword,String searchBy) {
        String SEARCH_BY = null;
        if(searchBy.equals("Author")){
            SEARCH_BY = "+inauthor:keys";
        }else if(searchBy.equals("Title")){
            SEARCH_BY = "+intitle:keys";
        } else if(searchBy.equals("Subject")){
            SEARCH_BY = "+insubject:keys";
        }

        Uri builtUri = Uri.parse(STATIC_BASE_URL).buildUpon()
                .appendQueryParameter(maxResults,"40")
                .appendQueryParameter(query, keyword)
                .fragment(SEARCH_BY)
                .build();
        String stringUrl = builtUri.toString()+"&key=AIzaSyCXO-W1yBgGnbPP8DpaF3DW4wwFGLUpJlo";
        String plusReplaced = stringUrl.replace("#%2B", "+");
        String finalUrl = plusReplaced.replace("%3A", ":");

        URL url = null;
        try {
            url = new URL(finalUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();

            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

}
