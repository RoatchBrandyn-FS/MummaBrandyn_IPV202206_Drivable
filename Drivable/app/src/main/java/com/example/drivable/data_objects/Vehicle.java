package com.example.drivable.data_objects;

import java.io.Serializable;

public class Vehicle implements Serializable {

    //starting variables
    private final String docID;
    private final String name;
    private final String vinNum;
    private final String odometer;
    private final boolean isActive;
    private final String year;
    private final String make;
    private final String model;
    private final String driveTrain;
    //later variables

    public Vehicle(String _docID, String _name, String _vinNum, String _odometer, boolean _isActive, String _year, String _make, String _model, String _driveTrain){
        docID = _docID;
        name = _name;
        vinNum = _vinNum;
        odometer = _odometer;
        isActive = _isActive;
        year = _year;
        make = _make;
        model = _model;
        driveTrain = _driveTrain;
    }

    public String getName() {
        return name;
    }

    public String getVinNum() {
        return vinNum;
    }

    public String getOdometer() {
        return odometer;
    }

    public boolean isActive() {
        return isActive;
    }

    public String getYear() {
        return year;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public String getDriveTrain() {
        return driveTrain;
    }
}
