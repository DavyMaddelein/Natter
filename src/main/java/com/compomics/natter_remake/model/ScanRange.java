package com.compomics.natter_remake.model;

/**
 *
 * @author Davy
 */
public class ScanRange {
    private double scanId;
    private double retentionTime;

    public void setRetentionTime(double retentionTime) {
        this.retentionTime = retentionTime;}

    public void setScan(double scanId) {
       this.scanId = scanId;}

    public double getScanId() {
        return scanId;
    }

    public double getRetentionTime() {
        return retentionTime;
    }
}
