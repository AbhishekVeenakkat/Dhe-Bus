package com.abhishekveenakkat.ksrtcapp.models;

/**
 * Created by Abhishek Veenakkat on 28-02-2017.
 */


public class ScheduleModel
{
    private String routeid;
    private String stationname;
    private String arrivaltime;

    public String getRouteid() {
        return routeid;
    }

    public void setRouteid(String routeid) {
        this.routeid = routeid;
    }

    public String getStationname() {
        return stationname;
    }

    public void setStationname(String stationname) {
        this.stationname = stationname;
    }

    public String getArrivaltime() {
        return arrivaltime;
    }

    public void setArrivaltime(String arrivaltime) {
        this.arrivaltime = arrivaltime;
    }
}