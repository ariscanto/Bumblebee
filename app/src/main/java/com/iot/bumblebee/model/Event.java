package com.iot.bumblebee.model;

public interface Event{

    public String getCreateDate();

    public void setCreateDate(String createDate);

    public String getState();

    public void setState(String type);

    public Double getDistance();

    public void setDistance(Double distance);

    public Double getLat();

    public void setLat(Double lat);

    public Double getLon();

    public void setLon(Double lon);

}
