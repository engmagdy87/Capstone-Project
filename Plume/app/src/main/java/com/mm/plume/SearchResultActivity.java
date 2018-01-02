package com.mm.plume;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mm.plume.adapters.BookItemAdapter;
import com.mm.plume.javaclasses.BookInfo;
import com.mm.plume.widget.PlumeWidgetService;

import java.util.ArrayList;
import java.util.Iterator;

public class SearchResultActivity extends AppCompatActivity implements BookItemAdapter.BookItemAdapterOnClickHandler {

    static FirebaseDatabase database;
    static DatabaseReference myRef;

    static BookInfo bookInfo;
    private RecyclerView recyclerView;
    private BookItemAdapter bookAdapter = new BookItemAdapter(this, this);
    static ArrayList<BookInfo> booksData;
    private String activityTitle;
    static String userId;
    int favBookListCount;
    ProgressBar progressBar;
    private Toolbar toolbar;
    private static final String ONSAVEINSTANCESTATE_BOOKS = "books";
    private static final String ONSAVEINSTANCESTATE_TITLE = "title";
    private static final String ONSAVEINSTANCESTATE_USERID = "currentUserId";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(ONSAVEINSTANCESTATE_BOOKS, booksData);
        outState.putString(ONSAVEINSTANCESTATE_TITLE, activityTitle);
        outState.putString(ONSAVEINSTANCESTATE_USERID, userId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        recyclerView = findViewById(R.id.recyclerview_book);
        toolbar = findViewById(R.id.app_bar);
        StaggeredGridLayoutManager stagGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        recyclerView.setLayoutManager(stagGridLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(bookAdapter);
        recyclerView.setVisibility(View.VISIBLE);
        progressBar = findViewById(R.id.pb_loading_indicator);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(ONSAVEINSTANCESTATE_BOOKS)) {
                booksData = savedInstanceState
                        .getParcelableArrayList(ONSAVEINSTANCESTATE_BOOKS);
                activityTitle = savedInstanceState
                        .getString(ONSAVEINSTANCESTATE_TITLE);
                userId = savedInstanceState
                        .getString(ONSAVEINSTANCESTATE_USERID);

                bookAdapter.setBookData(booksData);
                getSupportActionBar().setTitle(activityTitle);
            }
        } else {

            Intent myIntent = getIntent();
            Bundle extras = myIntent.getExtras();
            if (extras != null) {
                if (extras.containsKey("booksData")) {
                    booksData = myIntent.getParcelableArrayListExtra("booksData");

                }
                if (extras.containsKey("searchKeyword")) {
                    activityTitle = myIntent.getStringExtra("searchKeyword");

                }
                if (extras.containsKey("currentUserId")) {
                    userId = myIntent.getStringExtra("currentUserId");
                }
                if (extras.containsKey("favListSize")) {
                    favBookListCount = myIntent.getIntExtra("favListSize", 0);
                    PlumeWidgetService.startFavListService(getBaseContext(), favBookListCount);
                }
                if (!activityTitle.equals("Favorite List")) {
                    bookAdapter.setBookData(booksData);
                    getSupportActionBar().setTitle(activityTitle);
                }
            }

        }

    }

    @Override
    public void onClick(BookInfo book) {
        Context context = this;
        Class destinationActivity = BookDetailsActivity.class;
        Intent bookDetails = new Intent(context, destinationActivity);
        Bundle extras = new Bundle();
        extras.putParcelable("book", book);
        extras.putString("userId", userId);
        extras.putInt("booksInFavList", favBookListCount);
        bookDetails.putExtras(extras);
        startActivity(bookDetails);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (activityTitle.equals("Favorite List")) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
            database = FirebaseDatabase.getInstance();
            myRef = database.getReference("users");
            myRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
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
                    progressBar.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    bookAdapter.setBookData(booksData);
                    getSupportActionBar().setTitle(activityTitle);
                    favBookListCount = booksData.size();
                    PlumeWidgetService.startFavListService(getBaseContext(), favBookListCount);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getBaseContext(), "Internet connection error", Toast.LENGTH_LONG).show();
                }
            });
        }
    }


    public static String decodeString(String string) {
        return string.replace(",", ".");
    }
}
