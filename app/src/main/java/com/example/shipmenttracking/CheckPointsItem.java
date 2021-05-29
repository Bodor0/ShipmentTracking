package com.example.shipmenttracking;

import java.util.Date;

public class CheckPointsItem {
    private String checkPost;
    private String country;
    private Date date;
    private Integer id;
    private String status;

    public CheckPointsItem(String checkPost,String country, Date date, Integer id, String status) {
        this.checkPost = checkPost;
        this.country = country;
        this.date = date;
        this.id = id;
        this.status = status;
    }

    public CheckPointsItem() {}

    public String getCheckPost() {  return checkPost; }
    public String getCountry() { return country; }
    public Date getDate() { return date; }
    public Integer getId() { return id; }
    public String getStatus() { return status; }
}
