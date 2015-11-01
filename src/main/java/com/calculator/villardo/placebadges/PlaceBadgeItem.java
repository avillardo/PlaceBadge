package com.calculator.villardo.placebadges;

import java.io.IOException;

/**
 * Created by mercium on 10/27/15.
 */
public class PlaceBadgeItem {
    private String code = null;
    private String place = null;
    private String country = null;

    PlaceBadgeItem(String country, String place, String code){
        this.code = code;

        this.place = place;
        this.country = country;

    }

    public String getUrl() {
        try {
            String lowerCase = code.toLowerCase();
            String url = "http://www.geonames.org/flags/x/"+lowerCase+".gif";
            return url;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getCode() {

        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public String getPlace() {

        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {

        this.country = country;
    }


}
