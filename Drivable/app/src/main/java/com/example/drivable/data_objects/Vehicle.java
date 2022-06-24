package com.example.drivable.data_objects;

public class Vehicle {

    //starting variables
    private final String docID;
    private final String name;
    private final String vinNum;
    private final String make;
    //later variables

    public Vehicle(String _docID, String _name, String _vinNum, String _make){
        docID = _docID;
        name = _name;
        vinNum = _vinNum;
        make = _make;
    }

    public String getName() {
        return name;
    }

    public String getVinNum() {
        return vinNum;
    }

    public String getMake() {
        return make;
    }
}
