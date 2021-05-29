package com.example.shipmenttracking;


public class GeographicAddressItem {
    private String city;
    private Integer id;
    private String postcode;
    private String stateOrProvince;
    private String streetName;
    private String streetNr;
    private String streetType;

    public GeographicAddressItem(String city, Integer id, String postcode, String stateOrProvince, String streetName, String streetNr, String streetType) {
        this.city = city;
        this.id = id;
        this.postcode = postcode;
        this.stateOrProvince = stateOrProvince;
        this.streetName = streetName;
        this.streetNr = streetNr;
        this.streetType = streetType;
    }

    public GeographicAddressItem() {}

    public String getCity() { return city; }
    public Integer getId() { return id; }
    public String getPostcode() { return postcode; }
    public String getStateOrProvince() { return stateOrProvince; }
    public String getStreetName() { return streetName; }
    public String getStreetNr() { return streetNr; }
    public String getStreetType() { return streetType; }
}
