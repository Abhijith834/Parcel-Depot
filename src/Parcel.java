package com.depot.model;


public class Parcel {
    private String parcelID;
    private double length;
    private double width;
    private double height;
    private double weight;
    private int daysInDepot;
    private boolean collected;

    public Parcel(String parcelID, double length, double width, double height,
                  double weight, int daysInDepot) {
        this.parcelID = parcelID;
        this.length = length;
        this.width = width;
        this.height = height;
        this.weight = weight;
        this.daysInDepot = daysInDepot;
        this.collected = false; // By default, not collected
    }

    // Getters & Setters
    public String getParcelID() {
        return parcelID;
    }

    public void setParcelID(String parcelID) {
        this.parcelID = parcelID;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getDaysInDepot() {
        return daysInDepot;
    }

    public void setDaysInDepot(int daysInDepot) {
        this.daysInDepot = daysInDepot;
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }


    public boolean isValidID() {
        return parcelID.matches("^[XC]\\d+$");
    }

    @Override
    public String toString() {
        return "Parcel{" +
                "ID='" + parcelID + '\'' +
                ", dim=" + length + "x" + width + "x" + height +
                ", weight=" + weight +
                ", days=" + daysInDepot +
                ", collected=" + collected +
                '}';
    }
}
