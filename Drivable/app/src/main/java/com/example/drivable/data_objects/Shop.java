package com.example.drivable.data_objects;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class Shop implements Serializable {

    private final String docID;
    private final String name;
    private final String addressLine1;
    private final String addressLine2;
    private final String description;
    private final boolean isMaintenance;
    private final boolean isOilChange;
    private final boolean isTiresWheels;
    private final boolean isGlass;
    private final boolean isBody;
    private double lat;
    private double lng;
    private final String nickname;

    public Shop(String _dcoID, String _name, String _addressLine1, String _addressLine2, String _description, boolean _isMaintenance, boolean _isOilChange, boolean _isTiresWheels,
                boolean _isGlass, boolean _isBody, LatLng _latLng, String _nickname){
        docID = _dcoID;
        name = _name;
        addressLine1 = _addressLine1;
        addressLine2 = _addressLine2;
        description = _description;
        isMaintenance = _isMaintenance;
        isOilChange = _isOilChange;
        isTiresWheels = _isTiresWheels;
        isGlass = _isGlass;
        isBody = _isBody;
        lat = _latLng.latitude;
        lng = _latLng.longitude;
        nickname = _nickname;
    }

    public String getDocID() {
        return docID;
    }

    public String getName() {
        return name;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public String getDescription() {
        return description;
    }

    public boolean isMaintenance() {
        return isMaintenance;
    }

    public boolean isOilChange() {
        return isOilChange;
    }

    public boolean isTiresWheels() {
        return isTiresWheels;
    }

    public boolean isGlass() {
        return isGlass;
    }

    public boolean isBody() {
        return isBody;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getNickname() {
        return nickname;
    }
}
