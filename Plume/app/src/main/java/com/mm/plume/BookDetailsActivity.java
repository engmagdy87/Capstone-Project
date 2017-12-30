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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mm.plume.javaclasses.BookInfo;

import java.util.ArrayList;

public class BookDetailsActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef;
    private TextView bookTitle;
    private TextView bookCompleteTitle;
    private TextView bookAuthors;
    private TextView bookIsbn;
    private TextView bookPublish;
    private TextView bookCategories;
    private TextView description;
    private FloatingActionButton shareBtn;
    private ImageView addFavBtn;
    private ImageView bookCover;

    String addFavTag;
    String userId;
    String fav;
    BookInfo bookInfo;

    private static final String ONSAVEINSTANCESTATE_BOOK = "book";
    private static final String ONSAVEINSTANCESTATE_ADDFAV = "add_fav";
    private static final String ONSAVEINSTANCESTATE_USERID = "userId";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ONSAVEINSTANCESTATE_BOOK, bookInfo);
        outState.putString(ONSAVEINSTANCESTATE_ADDFAV, addFavTag);
        outState.putString(ONSAVEINSTANCESTATE_USERID, userId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);
        // Write a message to the database
        database = FirebaseDatabase.getInstance();

        bookAuthors = findViewById(R.id.book_authors);
        bookPublish = findViewById(R.id.book_publish);
        bookIsbn = findViewById(R.id.book_isbn);
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
                userId = savedInstanceState
                        .getString(ONSAVEINSTANCESTATE_USERID);
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
                if (extras.containsKey("userId")) {
                    userId = myIntent.getStringExtra("userId");
                }
                if (extras.containsKey("fav")) {
                    fav = myIntent.getStringExtra("fav");
                } else {
                    fav = "not fav";
                }
            }
            if (!fav.equals("fav")) {
                myRef = database.getReference("users");

                myRef.child(userId).child(bookInfo.getId()).child("isbn").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String value = dataSnapshot.getValue(String.class);
                        if (value == null) {
                            addFavBtn.setTag("star_off");
                            addFavTag = "star_off";
                            addFavBtn.setImageResource(R.drawable.ic_unfavorite);
                        } else {
                            addFavBtn.setTag("star_on");
                            addFavTag = "star_on";
                            addFavBtn.setImageResource(R.drawable.ic_favorite);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } else {
                myRef = database.getReference("users");
                addFavBtn.setTag("star_on");
                addFavTag = "star_on";
                addFavBtn.setImageResource(R.drawable.ic_favorite);
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
        bookIsbn.setText("ISBN: " + bookInfo.getIsbn());

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
                    myRef.child(userId).child(bookInfo.getId()).child("isbn").setValue(bookInfo.getIsbn());
                    myRef.child(userId).child(bookInfo.getId()).child("thumbnail").setValue(encodeString(bookInfo.getThumbnail()));
                    myRef.child(userId).child(bookInfo.getId()).child("id").setValue(bookInfo.getId());
                    myRef.child(userId).child(bookInfo.getId()).child("publisher").setValue(bookInfo.getPublisher());
                    myRef.child(userId).child(bookInfo.getId()).child("publishedDate").setValue(bookInfo.getPublishedDate());
                    myRef.child(userId).child(bookInfo.getId()).child("description").setValue(bookInfo.getDescription());
                    myRef.child(userId).child(bookInfo.getId()).child("shareLink").setValue(bookInfo.getShareLink());
                    myRef.child(userId).child(bookInfo.getId()).child("title").setValue(bookInfo.getTitle());
                    StringBuilder b = new StringBuilder();
                    String[] authors = bookInfo.getAuthors();
                    for (int i = 0; i < authors.length; i++) {
                        b.append(authors[i]);
                        if (i == authors.length - 1)
                            break;
                        b.append(", ");
                    }
                    myRef.child(userId).child(bookInfo.getId()).child("authors").setValue(b.toString());
                    b = new StringBuilder();
                    String[] categories = bookInfo.getCategories();
                    for (int i = 0; i < categories.length; i++) {
                        b.append(categories[i]);
                        if (i == categories.length - 1)
                            break;
                        b.append(", ");
                    }
                    myRef.child(userId).child(bookInfo.getId()).child("categories").setValue(b.toString());
                } else {
                    addFavBtn.setTag("star_off");
                    addFavTag = "star_off";
                    addFavBtn.setImageResource(R.drawable.ic_unfavorite);

                    myRef.child(userId).child(bookInfo.getId()).removeValue();
                }
                myRef.child(userId).child(bookInfo.getId()).child("isbn").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        String value = dataSnapshot.getValue(String.class);
                        if (value == null)
                            Toast.makeText(getBaseContext(), "Removed from favorite", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getBaseContext(), "Added to favorite", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Toast.makeText(getBaseContext(), "Failed", Toast.LENGTH_SHORT).show();
                    }
                });
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
    public static String encodeString(String string) {
        return string.replace(".", ",");
    }
}
