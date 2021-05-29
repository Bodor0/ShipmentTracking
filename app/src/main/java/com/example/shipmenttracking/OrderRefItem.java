package com.example.shipmenttracking;

public class OrderRefItem {
    private Integer href;
    private String id;
    private String uid;

    public OrderRefItem(Integer href, String id, String uid) {
        this.href = href;
        this.id = id;
        this.uid = uid;
    }

    public OrderRefItem() {}

    public Integer getHref() { return href; }
    public String getID() { return id; }
    public String getUID() { return uid; }
}
