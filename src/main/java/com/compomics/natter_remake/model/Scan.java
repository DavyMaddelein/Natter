package com.compomics.natter_remake.model;

/**
 *
 * @author Davy
 */
public class Scan {

    private int scanNumber;
    private double area;

    public void setScanNumber(int scanNumber) {
        this.scanNumber = scanNumber;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public int getScanNumber() {
        return scanNumber;
    }

    public double getArea() {
        return area;
    }
}
