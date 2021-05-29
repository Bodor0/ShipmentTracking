package com.example.shipmenttracking;

public class userDataItem {
    private String city;
    private String name;
    private String postcode;
    private String state;
    private String streetName;
    private String streetNr;
    private String streetType;
    private String uid;

    public userDataItem(String city, String name, String postcode, String state,String streetName, String streetNr,String streetType, String uid) {
        this.city = city;
        this.name = name;
        this.postcode = postcode;
        this.state = state;
        this.streetName = streetName;
        this.streetNr = streetNr;
        this.streetType = streetType;
        this.uid = uid;
    }

    public userDataItem() {}

    public String getCity() { return city; }
    public String getName() { return name; }
    public String getPostcode() { return postcode; }
    public String getState() { return state; }
    public String getStreetName() { return streetName; }
    public String getStreetNr() { return streetNr; }
    public String getStreetType() { return streetType; }
    public String getUid() { return uid; }
}
