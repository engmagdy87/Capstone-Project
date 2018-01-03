package com.mm.plume;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.support.v7.app.ActionBarDrawerToggle;

import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mindorks.placeholderview.PlaceHolderView;
import com.mm.plume.javaclasses.BookInfo;
import com.mm.plume.javaclasses.CurrentUser;
import com.mm.plume.navigationdrawer.DrawerHeader;
import com.mm.plume.navigationdrawer.DrawerMenuItem;
import com.mm.plume.networkhelpers.BookJsonUtils;
import com.mm.plume.networkhelpers.NetworkUtils;
import com.mm.plume.widget.PlumeWidgetService;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {
    Context context;
    SearchView searchView;
    RadioGroup searchByRadioGroup;
    Button searchBtn;
    static int favBookListCount;
    static FirebaseDatabase database;
    static DatabaseReference myRef;
    public static ArrayList<BookInfo> booksData;
    static BookInfo bookInfo;
    static ProgressBar loadingIndicator;
    String searchKeyword;
    int selectedRadioButtonID;
    int radioButtonIndex;
    CurrentUser currentUser;

    private PlaceHolderView mDrawerView;
    private DrawerLayout mDrawer;
    private android.support.v7.widget.Toolbar mToolbar;

    private static final String ONSAVEINSTANCESTATE_SEARCHKEYWORD = "search_keyword";
    private static final String ONSAVEINSTANCESTATE_SEARCHBY = "search_by";
    private static final String ONSAVEINSTANCESTATE_USERINFO = "currentUser";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ONSAVEINSTANCESTATE_SEARCHKEYWORD, searchKeyword);
        outState.putInt(ONSAVEINSTANCESTATE_SEARCHBY, radioButtonIndex);
        outState.putParcelable(ONSAVEINSTANCESTATE_USERINFO, currentUser);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (database == null) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
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
                currentUser = savedInstanceState
                        .getParcelable(ONSAVEINSTANCESTATE_USERINFO);

                searchView.setQuery(searchKeyword, false);
                ((RadioButton) searchByRadioGroup.getChildAt(radioButtonIndex)).setChecked(true);
            }
        }
        Intent myIntent = getIntent();
        Bundle extras = myIntent.getExtras();
        if (extras != null) {
            if (extras.containsKey("currentUser")) {
                currentUser = myIntent.getParcelableExtra("currentUser");
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

        mDrawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        mDrawerView = (PlaceHolderView) findViewById(R.id.drawerView);
        mToolbar = findViewById(R.id.toolbar);

        setupDrawer();
        getFavoriteList(currentUser.getUid(), this);
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
                    extras.putString("searchKeyword", searchKeyword);
                    extras.putParcelableArrayList("booksData", booksData);
                    extras.putString("currentUserId", currentUser.getUid());

                    extras.putInt("favListSize", favBookListCount);
                    SearchResult.putExtras(extras);
                    startActivity(SearchResult);
                    overridePendingTransition( R.transition.slide_in_up, R.transition.slide_out_up );
                }
            }
        }
    }

    private void setupDrawer() {
        mDrawerView
                .addView(new DrawerHeader(getBaseContext(), currentUser.getDisplayNAME(), currentUser.getEmail(), currentUser.getProfileImage()))
                .addView(new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_PROFILE, currentUser.getUid(), mDrawer))
                .addView(new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_REQUESTS, null, null));

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        mDrawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public static void getFavoriteList(String currentUserId, final Context context) {

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");
        myRef.keepSynced(true);
        myRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> itr = dataSnapshot.getChildren().iterator();
                booksData = new ArrayList<BookInfo>((int) dataSnapshot.getChildrenCount());

                String id, title, publisher, publishedDate, description, isbn, thumbnail, shareLink;
                String[] authors = new String[1], categories = new String[1];

                for (int i = 0; i < dataSnapshot.getChildrenCount(); i++) {
                    if (itr.hasNext()) {
                        bookInfo = new BookInfo();
                        DataSnapshot dIter = itr.next();

                        id = dIter.child("id").getValue().toString();
                        isbn = dIter.child("isbn").getValue().toString();
                        title = dIter.child("title").getValue().toString();
                        authors[0] = dIter.child("authors").getValue().toString();
                        publisher = dIter.child("publisher").getValue().toString();
                        publishedDate = dIter.child("publishedDate").getValue().toString();
                        description = dIter.child("description").getValue().toString();
                        categories[0] = dIter.child("categories").getValue().toString();
                        shareLink = dIter.child("shareLink").getValue().toString();
                        thumbnail = decodeString(dIter.child("thumbnail").getValue().toString());

                        bookInfo.setIsbn(isbn);
                        bookInfo.setThumbnail(thumbnail);
                        bookInfo.setId(id);
                        bookInfo.setTitle(title);
                        bookInfo.setAuthors(authors);
                        bookInfo.setCategories(categories);
                        bookInfo.setPublisher(publisher);
                        bookInfo.setPublishedDate(publishedDate);
                        bookInfo.setDescription(description);
                        bookInfo.setShareLink(shareLink);
                        booksData.add(bookInfo);
                    }
                }
                favBookListCount = booksData.size();
                PlumeWidgetService.startFavListService(context, favBookListCount);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static String decodeString(String string) {
        return string.replace(",", ".");
    }
}
