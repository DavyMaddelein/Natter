package com.compomics.natter_remake.model;

/**
 *
 * @author Davy
 */
public class LcRun {

    private String lcRunTitle;
    private int lcRunDbNumber;

    public LcRun(int aRunDbNumber, String aTitle) {
        this.lcRunDbNumber = aRunDbNumber;
        this.lcRunTitle = aTitle;
    }

    public String getLcRunTitle() {
        return lcRunTitle;
    }

    public int getLcRunDbNumber() {
        return lcRunDbNumber;
    }
}
