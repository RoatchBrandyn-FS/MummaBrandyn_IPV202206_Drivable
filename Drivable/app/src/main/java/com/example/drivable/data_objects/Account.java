package com.example.drivable.data_objects;

public class Account {

    //starting test variables
    private final String docID;
    private final String email;
    private final String password;
    // *** MORE VARIABLES TO BE ADDED AFTER POC ***

    public Account (String _docID, String _email, String _password){
        docID = _docID;
        email = _email;
        password = _password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
