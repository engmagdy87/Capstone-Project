package com.mm.plume;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.mm.plume.javaclasses.BookInfo;
import com.mm.plume.networkhelpers.BookJsonUtils;
import com.mm.plume.networkhelpers.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    SearchView searchView;
    RadioGroup searchByRadioGroup;
    Button searchBtn;
    private ProgressBar loadingIndicator;
    String searchKeyword;
    int selectedRadioButtonID;
    int radioButtonIndex;

    private static final String ONSAVEINSTANCESTATE_SEARCHKEYWORD = "search_keyword";
    private static final String ONSAVEINSTANCESTATE_SEARCHBY = "search_by";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ONSAVEINSTANCESTATE_SEARCHKEYWORD,searchKeyword);
        outState.putInt(ONSAVEINSTANCESTATE_SEARCHBY,radioButtonIndex);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchView = findViewById(R.id.sv_search);
        searchByRadioGroup = findViewById(R.id.rg_search);
        searchBtn = findViewById(R.id.btn_search);
        loadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(ONSAVEINSTANCESTATE_SEARCHKEYWORD)) {
                searchKeyword = savedInstanceState
                        .getString(ONSAVEINSTANCESTATE_SEARCHKEYWORD);
                radioButtonIndex = savedInstanceState
                        .getInt(ONSAVEINSTANCESTATE_SEARCHBY);

                searchView.setQuery(searchKeyword,false);
                ((RadioButton)searchByRadioGroup.getChildAt(radioButtonIndex)).setChecked(true);
            }
        }

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectedRadioButtonID = searchByRadioGroup.getCheckedRadioButtonId();
                String searchKeyword = searchView.getQuery().toString();

                if (searchKeyword.isEmpty()) {
                    Toast.makeText(getBaseContext(), "Please insert search keyword ", Toast.LENGTH_LONG).show();
                    return;
                }

                if (selectedRadioButtonID != -1) {

                    RadioButton selectedRadioButton = findViewById(selectedRadioButtonID);
                    radioButtonIndex = searchByRadioGroup.indexOfChild(selectedRadioButton);

                    String selectedRadioButtonText = selectedRadioButton.getText().toString();

                    new FetchBookTask().execute(searchKeyword, selectedRadioButtonText);
                } else {
                    Toast.makeText(getBaseContext(), "Please select search option ", Toast.LENGTH_LONG).show();
                }


            }
        });
    }

    public class FetchBookTask extends AsyncTask<String, Void, ArrayList<BookInfo>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<BookInfo> doInBackground(String... params) {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            boolean internetState = (netInfo != null && netInfo.isConnected());

            if (params.length == 0 || !internetState) {
                return null;
            }

            searchKeyword = params[0];
            String selectedRadioButtonText = params[1];

            URL searchAPILink = NetworkUtils.buildUrl(searchKeyword, selectedRadioButtonText);

            try {
                String jsonBookResponse = NetworkUtils
                        .getResponseFromHttpUrl(searchAPILink);

                ArrayList<BookInfo> simpleJsonBookData = BookJsonUtils
                        .getSimpleBookStringsFromJson(jsonBookResponse);

                return simpleJsonBookData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<BookInfo> booksData) {
            loadingIndicator.setVisibility(View.INVISIBLE);
            if (booksData == null) {
                Toast.makeText(getBaseContext(), "Internet connection error", Toast.LENGTH_LONG).show();
            } else {
                if (booksData.isEmpty()) {
                    Toast.makeText(getBaseContext(), "0 books found", Toast.LENGTH_LONG).show();
                } else {
                    Class destinationActivity = SearchResultActivity.class;
                    Intent SearchResult = new Intent(getBaseContext(), destinationActivity);
                    Bundle extras = new Bundle();
                    extras.putString("searchKeyword",searchKeyword);
                    extras.putParcelableArrayList("booksData",booksData);
                    SearchResult.putExtras(extras);
                    startActivity(SearchResult);
                }
            }
        }
    }

}
