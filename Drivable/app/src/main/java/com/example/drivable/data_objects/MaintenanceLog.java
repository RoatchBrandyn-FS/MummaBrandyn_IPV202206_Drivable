package com.example.drivable.data_objects;

import java.io.Serializable;
import java.util.Comparator;

public class MaintenanceLog implements Serializable, Comparable<MaintenanceLog>{

    private final String docID;
    private final String name;
    private final String date;
    private final String logRefString;
    private final String shopName;
    private final String addressLine2;
    private final double lat;
    private final double lng;
    private final String report;

    public MaintenanceLog (String _docID, String _name,String _date, String _logRefString, String _shopName, String _addressLine2, double _lat, double _lng, String _report){

        docID = _docID;
        name = _name;
        date = _date;
        logRefString = _logRefString;
        shopName = _shopName;
        addressLine2 = _addressLine2;
        lat = _lat;
        lng = _lng;
        report = _report;

    }

    public String getDocID() {
        return docID;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getLogRefString() {
        return logRefString;
    }

    public String getShopName() {
        return shopName;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getReport() {
        return report;
    }

    @Override
    public int compareTo(MaintenanceLog maintenanceLog) {
        return this.name.compareTo(maintenanceLog.getName());
    }
}
