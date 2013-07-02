package com.compomics.natter_remake.model;

/**
 *
 * @author Davy
 */
public class Ratio {

    private boolean valid;
    private String ratioType = "not found";
    private double ratioValue = -1;
    private double ratioQuality = -1;

    public void setRatio(String ratioType) {
            this.ratioType = ratioType;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public void setValue(double ratioValue) {
        this.ratioValue = ratioValue;
    }

    public void setQuality(double ratioQuality) {
        this.ratioQuality = ratioQuality;
    }

    public boolean isValid() {
        return valid;
    }

    public String getRatioType() {
        return ratioType;
    }

    public double getRatioValue() {
        return ratioValue;
    }

    public double getRatioQuality() {
        return ratioQuality;
    }
}
