package com.mm.plume.navigationdrawer;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.NonReusable;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mm.plume.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by MM on 12/26/2017.
 */
@NonReusable
@Layout(R.layout.drawer_header)
public class DrawerHeader {
    private String username;
    private String email;
    private String profileImage;
    private Context context;

    public DrawerHeader(Context context, String username, String email, String profileImage) {
        this.username = username;
        this.email = email;
        this.profileImage = profileImage;
        this.context = context;
    }

    @View(R.id.nameTxt)
    private TextView nameTxt;

    @View(R.id.emailTxt)
    private TextView emailTxt;

    @View(R.id.iv_profile)
    private CircleImageView profilePicture;

    @Resolve
    private void onResolved() {
        nameTxt.setText(username);
        emailTxt.setText(email);

        Glide
                .with(context)
                .load(profileImage)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.profile))
                .into(profilePicture);
    }
}