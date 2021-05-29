package com.example.shipmenttracking;

import java.util.ArrayList;
import java.util.Date;

public class TrackedItem {
    private Integer addressTo;
    private String carrier;
    private ArrayList<Integer> checkpoint;
    private String href;
    private String id;
    private Integer order;
    private Date trackingDate;

    public TrackedItem(Integer addressTo, String carrier, ArrayList<Integer> checkpoint, String href, String id, Integer order, Date trackingDate) {
        this.addressTo = addressTo;
        this.carrier = carrier;
        this.checkpoint = checkpoint;
        this.href = href;
        this.id = id;
        this.order = order;
        this.trackingDate = trackingDate;
    }

    public TrackedItem() {}

    public Integer getAddressTo() { return addressTo; }
    public String getCarrier() { return carrier; }
    public ArrayList<Integer> getCheckpoint() { return checkpoint; }
    public String getHref() { return href; }
    public String getID() { return id; }
    public Integer getOrder() { return order; }
    public Date getTrackingDate() { return trackingDate; }
}
