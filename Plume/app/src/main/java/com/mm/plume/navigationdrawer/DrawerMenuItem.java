package com.mm.plume.navigationdrawer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mindorks.placeholderview.PlaceHolderView;
import com.mindorks.placeholderview.PlaceHolderViewBuilder;
import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mm.plume.LoginActivity;
import com.mm.plume.MainActivity;
import com.mm.plume.R;
import com.mm.plume.SearchResultActivity;
import com.mm.plume.javaclasses.BookInfo;
import com.mm.plume.widget.PlumeWidgetService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.zip.Inflater;

/**
 * Created by MM on 12/26/2017.
 */

@Layout(R.layout.drawer_item)
public class DrawerMenuItem {

    public static final int DRAWER_MENU_ITEM_PROFILE = 1;
    public static final int DRAWER_MENU_ITEM_REQUESTS = 2;
    Class destinationActivity;
    ArrayList<BookInfo> bookInfos;
    BookInfo bookInfo;
    FirebaseDatabase database;
    DatabaseReference myRef;
    private int mMenuPosition;
    private Context mContext;
    private DrawerCallBack mCallBack;
    String userId;
    DrawerLayout drawerLayout;

    @View(R.id.itemNameTxt)
    private TextView itemNameTxt;

    @View(R.id.itemIcon)
    private ImageView itemIcon;

    public DrawerMenuItem(Context context, int menuPosition, String userId, DrawerLayout drawerLayout) {
        mContext = context;
        mMenuPosition = menuPosition;
        this.userId = userId;
        this.drawerLayout = drawerLayout;
    }

    @Resolve
    private void onResolved() {
        switch (mMenuPosition) {
            case DRAWER_MENU_ITEM_PROFILE:
                itemNameTxt.setText(R.string.fav_list);
                itemNameTxt.setContentDescription(mContext.getString(R.string.fav_list));
                itemIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_favorite_nav));
                break;
            case DRAWER_MENU_ITEM_REQUESTS:
                itemNameTxt.setText(R.string.sign_out);
                itemNameTxt.setContentDescription(mContext.getString(R.string.sign_out));
                itemIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_sign_out));
                break;
        }
    }

    @Click(R.id.mainView)
    private void onMenuItemClick() {
        switch (mMenuPosition) {
            case DRAWER_MENU_ITEM_PROFILE:
drawerLayout.closeDrawer(Gravity.LEFT);
                database = FirebaseDatabase.getInstance();
                myRef = database.getReference("users");
                myRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> itr = dataSnapshot.getChildren().iterator();
                        bookInfos = new ArrayList<BookInfo>((int) dataSnapshot.getChildrenCount());

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
                                bookInfos.add(bookInfo);
                            }
                        }

                        PlumeWidgetService.startFavListService(mContext,bookInfos.size());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                destinationActivity = SearchResultActivity.class;
                Intent SearchResult = new Intent(mContext, destinationActivity);
                SearchResult.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle extras = new Bundle();
                extras.putParcelableArrayList("booksData", bookInfos);
                extras.putString("searchKeyword", mContext.getString(R.string.fav_list));
                extras.putString("currentUserId", userId);
                SearchResult.putExtras(extras);
                mContext.startActivity(SearchResult);
                if (mCallBack != null) mCallBack.onProfileMenuSelected();
                break;
            case DRAWER_MENU_ITEM_REQUESTS:
                FirebaseAuth.getInstance().signOut();

                destinationActivity = LoginActivity.class;
                Intent HomeActivity = new Intent(mContext, destinationActivity);
                HomeActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(HomeActivity);

                if (mCallBack != null) mCallBack.onRequestMenuSelected();
                break;
        }
    }

    public void setDrawerCallBack(DrawerCallBack callBack) {
        mCallBack = callBack;
    }

    public interface DrawerCallBack {
        void onProfileMenuSelected();

        void onRequestMenuSelected();
    }

    public static String decodeString(String string) {
        return string.replace(",", ".");
    }
}
