package com.rhys.logol.revdupsample.users;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * This class is a layout for all of the information retrieved from the user
 * */
@IgnoreExtraProperties
public class User {

    public String email;
    public String firsName;
    public String lastName;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String firstName, String lastName, String email) {
        this.firsName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

}