package com.abhishekveenakkat.ksrtcapp.models;

/**
 * Created by Abhishek Veenakkat on 08-02-2017.
 */

public class BusModel {
    private String routeid;
    private String tripcode;
    private String routename;
    private String desc;
    private String type;
    private int unitfare;
    private int rating;
    private int prating;
    private int crating;
    private int srating;
    private int ratingcount;
    private String stime;
    private String dtime;
    private String status;

    public String getRouteid() {
        return routeid;
    }

    public void setRouteid(String routeid) {
        this.routeid = routeid;
    }

    public String getTripcode() {
        return tripcode;
    }

    public void setTripcode(String tripcode) {
        this.tripcode = tripcode;
    }

    public String getRoutename() {
        return routename;
    }

    public void setRoutename(String routename) {
        this.routename = routename;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getUnitfare() {
        return unitfare;
    }

    public void setUnitfare(int unitfare) {
        this.unitfare = unitfare;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getRatingcount() {
        return ratingcount;
    }

    public void setRatingcount(int ratingcount) {
        this.ratingcount = ratingcount;
    }

    public String getStime() {
        return stime;
    }

    public void setStime(String stime) {
        this.stime = stime;
    }

    public String getDtime() {
        return dtime;
    }

    public void setDtime(String dtime) {
        this.dtime = dtime;
    }

    public int getPrating() {
        return prating;
    }

    public void setPrating(int prating) {
        this.prating = prating;
    }

    public int getCrating() {
        return crating;
    }

    public void setCrating(int crating) {
        this.crating = crating;
    }

    public int getSrating() {
        return srating;
    }

    public void setSrating(int srating) {
        this.srating = srating;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
