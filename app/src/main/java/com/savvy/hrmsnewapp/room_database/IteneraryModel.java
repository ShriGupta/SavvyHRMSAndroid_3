package com.savvy.hrmsnewapp.room_database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class IteneraryModel implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "source")
    String source;
    @ColumnInfo(name = "destination")
    String destination;
    @ColumnInfo(name = "departuredate")
    String departuredate;
    @ColumnInfo(name = "returndate")
    String returndate;
    @ColumnInfo(name = "mode")
    String mode;
    @ColumnInfo(name = "classcode")
    String classcode;
    @ColumnInfo(name = "starttime")
    String starttime;
    @ColumnInfo(name = "endtime")
    String endtime;
    @ColumnInfo(name = "flightdetail")
    String flightdetail;
    @ColumnInfo(name = "travelwaytype")
    String travelwaytype;

    @ColumnInfo(name = "sourceid")
    String sourceid;

    @ColumnInfo(name = "destinationid")
    String destinationid;

    @ColumnInfo(name = "modeid")
    String modeid;

    @ColumnInfo(name = "classid")
    String classid;


    @ColumnInfo(name = "seatprefid")
    String seatprefid;

    @ColumnInfo(name = "seatprefvalue")
    String seatpreValue;

    @ColumnInfo(name = "insurancevalue")
    String insuranceValue;

    @ColumnInfo(name = "frequentlyfillerno")
    String frequentlyFillerno;

    public void setSeatprefid(String seatprefid) {
        this.seatprefid = seatprefid;
    }

    public void setSeatpreValue(String seatpreValue) {
        this.seatpreValue = seatpreValue;
    }

    public void setInsuranceValue(String insuranceValue) {
        this.insuranceValue = insuranceValue;
    }

    public void setFrequentlyFillerno(String frequentlyFillerno) {
        this.frequentlyFillerno = frequentlyFillerno;
    }

    public void setSpecialRequest(String specialRequest) {
        this.specialRequest = specialRequest;
    }

    public String getSeatprefid() {
        return seatprefid;
    }

    public String getSeatpreValue() {
        return seatpreValue;
    }

    public String isInsuranceValue() {
        return insuranceValue;
    }

    public String getFrequentlyFillerno() {
        return frequentlyFillerno;
    }

    public String getSpecialRequest() {
        return specialRequest;
    }

    @ColumnInfo(name = "specialrequest")
    String specialRequest;




    public void setSourceid(String sourceid) {
        this.sourceid = sourceid;
    }

    public void setDestinationid(String destinationid) {
        this.destinationid = destinationid;
    }

    public void setModeid(String modeid) {
        this.modeid = modeid;
    }

    public void setClassid(String classid) {
        this.classid = classid;
    }



    public String getSourceid() {
        return sourceid;
    }

    public String getDestinationid() {
        return destinationid;
    }

    public String getModeid() {
        return modeid;
    }

    public String getClassid() {
        return classid;
    }




    public void setTravelwaytype(String travelwaytype) {
        this.travelwaytype = travelwaytype;
    }

    public String getTravelwaytype() {
        return travelwaytype;
    }
    public int getId() {
        return id;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public String getDeparturedate() {
        return departuredate;
    }

    public String getReturndate() {
        return returndate;
    }

    public String getMode() {
        return mode;
    }

    public String getClasscode() {
        return classcode;
    }

    public String getStarttime() {
        return starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public String getFlightdetail() {
        return flightdetail;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setDeparturedate(String departuredate) {
        this.departuredate = departuredate;
    }

    public void setReturndate(String returndate) {
        this.returndate = returndate;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setClasscode(String classcode) {
        this.classcode = classcode;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public void setFlightdetail(String flightdetail) {
        this.flightdetail = flightdetail;
    }


}
