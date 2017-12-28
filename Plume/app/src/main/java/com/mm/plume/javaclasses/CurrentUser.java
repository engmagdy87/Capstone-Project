package com.mm.plume.javaclasses;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MM on 12/27/2017.
 */

public class CurrentUser implements Parcelable {
    private String uid;
    private String displayNAME;
    private String email;
    private String profileImage;

    public CurrentUser(String uid, String displayNAME, String email, String profileImage) {
        this.uid = uid;
        this.displayNAME = displayNAME;
        this.email = email;
        this.profileImage = profileImage;
    }

    protected CurrentUser(Parcel in) {
        uid = in.readString();
        displayNAME = in.readString();
        email = in.readString();
        profileImage = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(displayNAME);
        dest.writeString(email);
        dest.writeString(profileImage);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CurrentUser> CREATOR = new Creator<CurrentUser>() {
        @Override
        public CurrentUser createFromParcel(Parcel in) {
            return new CurrentUser(in);
        }

        @Override
        public CurrentUser[] newArray(int size) {
            return new CurrentUser[size];
        }
    };

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDisplayNAME() {
        return displayNAME;
    }

    public void setDisplayNAME(String displayNAME) {
        this.displayNAME = displayNAME;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
