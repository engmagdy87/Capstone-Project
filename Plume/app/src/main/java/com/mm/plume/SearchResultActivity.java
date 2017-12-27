package com.mm.plume;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.mm.plume.adapters.BookItemAdapter;
import com.mm.plume.javaclasses.BookInfo;

import java.util.ArrayList;

public class SearchResultActivity extends AppCompatActivity implements BookItemAdapter.BookItemAdapterOnClickHandler {
    private RecyclerView recyclerView;
    private BookItemAdapter bookAdapter = new BookItemAdapter(this, this);
    private ArrayList<BookInfo> booksData;
    private String activityTitle;
    private Toolbar toolbar;
    private static final String ONSAVEINSTANCESTATE_BOOKS = "books";
    private static final String ONSAVEINSTANCESTATE_TITLE = "title";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(ONSAVEINSTANCESTATE_BOOKS, booksData);
        outState.putString(ONSAVEINSTANCESTATE_TITLE, activityTitle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        recyclerView = findViewById(R.id.recyclerview_book);
        toolbar= findViewById(R.id.app_bar);
        StaggeredGridLayoutManager stagGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        recyclerView.setLayoutManager(stagGridLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(bookAdapter);
        recyclerView.setVisibility(View.VISIBLE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(ONSAVEINSTANCESTATE_BOOKS)) {
                booksData = savedInstanceState
                        .getParcelableArrayList(ONSAVEINSTANCESTATE_BOOKS);
                activityTitle = savedInstanceState
                        .getString(ONSAVEINSTANCESTATE_TITLE);

                bookAdapter.setBookData(booksData);
                getSupportActionBar().setTitle(activityTitle);
            }
        } else {
            Intent myIntent = getIntent();
            Bundle extras = myIntent.getExtras();
            if (extras != null) {
                if (extras.containsKey("booksData")) {
                    booksData = myIntent.getParcelableArrayListExtra("booksData");
                    bookAdapter.setBookData(booksData);
                }
                if (extras.containsKey("searchKeyword")) {
                    activityTitle = myIntent.getStringExtra("searchKeyword");
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
        bookDetails.putExtras(extras);
        startActivity(bookDetails);
    }
}
