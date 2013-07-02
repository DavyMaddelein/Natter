package com.compomics.natter_remake.model;

import java.util.ArrayList;

/**
 *
 * @author Davy
 */
public class IntensityList extends ArrayList<Intensity>{
    private boolean valid = false;
    private int peakStart = -1;
    private int peakEnd= -1;
    private int peakRegionEnd = -1;
    private int peakRegionStart = -1;
    
    public IntensityList(int predeterminedSize){
        super(predeterminedSize);
    }

    public IntensityList() {
        super();
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public void setPeakStart(int peakStart) {
        this.peakStart = peakStart;
    }

    public void setPeakEnd(int peakEnd) {
        this.peakEnd = peakEnd;
    }

    public void setPeakRegionEnd(int peakRegionEnd) {
        this.peakRegionEnd = peakRegionEnd;
    }

    public void setPeakRegionStart(int peakRegionStart) {
        this.peakRegionStart = peakRegionStart;
    }

    public boolean isValidPeak() {
        return valid;
    }

    public int getPeakStart() {
        return peakStart;
    }

    public int getPeakEnd() {
        return peakEnd;
    }

    public int getPeakRegionEnd() {
        return peakRegionEnd;
    }

    public int getPeakRegionStart() {
        return peakRegionStart;
    }
}