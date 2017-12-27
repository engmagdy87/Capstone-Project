package com.mm.plume;

import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mm.plume.javaclasses.BookInfo;

import java.util.ArrayList;

public class BookDetailsActivity extends AppCompatActivity {
    private TextView bookTitle;
    private TextView bookCompleteTitle;
    private TextView bookAuthors;
    private TextView bookPublish;
    private TextView bookCategories;
    private TextView description;
    private FloatingActionButton shareBtn;
    private ImageView addFavBtn;
    private ImageView bookCover;

    String addFavTag;
    BookInfo bookInfo;

    private static final String ONSAVEINSTANCESTATE_BOOK = "book";
    private static final String ONSAVEINSTANCESTATE_ADDFAV = "add_fav";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ONSAVEINSTANCESTATE_BOOK, bookInfo);
        outState.putString(ONSAVEINSTANCESTATE_ADDFAV, addFavTag);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        bookAuthors = findViewById(R.id.book_authors);
        bookPublish = findViewById(R.id.book_publish);
        bookCategories = findViewById(R.id.book_cat);
        shareBtn = findViewById(R.id.share_fab);
        addFavBtn = findViewById(R.id.add_fav);
        description = findViewById(R.id.book_desc);
        bookTitle = findViewById(R.id.book_title);
        bookCompleteTitle = findViewById(R.id.book_complete_title);
        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        bookCover = findViewById(R.id.photo);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(ONSAVEINSTANCESTATE_BOOK)) {
                bookInfo = savedInstanceState
                        .getParcelable(ONSAVEINSTANCESTATE_BOOK);
                addFavTag = savedInstanceState
                        .getString(ONSAVEINSTANCESTATE_ADDFAV);
                if (addFavTag.equals("star_off")) {
                    addFavBtn.setTag("star_off");
                    addFavBtn.setImageResource(R.drawable.ic_unfavorite);
                } else {
                    addFavBtn.setTag("star_on");
                    addFavBtn.setImageResource(R.drawable.ic_favorite);
                }
            }
        } else {
            addFavTag = "star_off";
            addFavBtn.setTag(addFavTag);

            Intent myIntent = getIntent();
            Bundle extras = myIntent.getExtras();
            if (extras != null) {
                if (extras.containsKey("book")) {
                    bookInfo = myIntent.getParcelableExtra("book");
                }
            }
        }

        bookTitle.setText(bookInfo.getTitle());
        bookCompleteTitle.setText(bookInfo.getTitle());

        if (bookInfo.getThumbnail().equals("N/A")) {

            Glide
                    .with(this)
                    .load(R.drawable.book_cover)
                    .into(bookCover);
        } else {
            Glide
                    .with(this)
                    .load(bookInfo.getThumbnail())
                    .into(bookCover);
        }
        StringBuilder b = new StringBuilder();
        String[] authors = bookInfo.getAuthors();
        for (int i = 0; i < authors.length; i++) {
            b.append(authors[i]);
            if (i == authors.length - 1)
                break;
            b.append(", ");
        }

        bookAuthors.setText("By " + b.toString());
        bookPublish.setText("Published by " + bookInfo.getPublisher() + " at " + bookInfo.getPublishedDate());

        b = new StringBuilder();
        String[] categories = bookInfo.getCategories();
        for (int i = 0; i < categories.length; i++) {
            b.append(categories[i]);
            if (i == categories.length - 1)
                break;
            b.append(", ");
        }
        bookCategories.setText("Categories: " + b.toString());
        description.setText("Description:\n" + bookInfo.getDescription());

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareTextUrl();
            }
        });

        addFavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addFavBtn.getTag().toString().equals("star_off")) {
                    addFavBtn.setTag("star_on");
                    addFavTag = "star_on";
                    addFavBtn.setImageResource(R.drawable.ic_favorite);
                    Toast.makeText(getBaseContext(), "Added to favorite list", Toast.LENGTH_SHORT).show();
                } else {
                    addFavBtn.setTag("star_off");
                    addFavTag = "star_off";
                    addFavBtn.setImageResource(R.drawable.ic_unfavorite);
                    Toast.makeText(getBaseContext(), "Removed from favorite list", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void shareTextUrl() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        share.putExtra(Intent.EXTRA_SUBJECT, bookInfo.getTitle());
        share.putExtra(Intent.EXTRA_TEXT, bookInfo.getShareLink());

        startActivity(Intent.createChooser(share, "Share Plume Book"));
    }
}
