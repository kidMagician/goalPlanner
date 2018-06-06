package com.example.nss.goalplanner.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by NSS on 2018-05-04.
 */

public class Authinfo implements Parcelable {

    private String username;
    private String email;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public Authinfo(){
    }
    public Authinfo(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
    public Authinfo( String email, String password) {
        this.username = null;
        this.email = email;
        this.password = password;
    }

    public Authinfo(Parcel in){

        username = in.readString();
        email = in.readString();
        password =in.readString();

    }

    public static final Creator<Authinfo> CREATOR =new Creator<Authinfo>() {

        @Override
        public Authinfo createFromParcel(Parcel parcel) {
            return new Authinfo(parcel);
        }

        @Override
        public Authinfo[] newArray(int i) {
            return new Authinfo[i];
        }

    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }

    @Override
    public int describeContents() {
        return 0;
    }
}
