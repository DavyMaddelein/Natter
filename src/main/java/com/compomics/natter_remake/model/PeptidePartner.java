package com.compomics.natter_remake.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Davy
 */
public class PeptidePartner {

    private boolean partnerFound;
    private Ratio absoluteRatio;
    private List<Modification> modificationsOnPeptide = new ArrayList<Modification>();
    private String component;
    private String peptideSequence;
    private List<Peptide> peptidesLinkedToPartner = new ArrayList<Peptide>();
    private double massOverCharge;
    private IntensityList IntensitiesForPartner = new IntensityList();
    private ScanRange scanRange;

    public boolean isPartnerFound() {
        return partnerFound;
    }

    public void setPartnerFound(boolean partnerFound) {
        this.partnerFound = partnerFound;
    }

    public Ratio getAbsoluteRatio() {
        return absoluteRatio;
    }

    public void setAbsoluteRatio(Ratio ratio) {
        this.absoluteRatio = ratio;
    }

    public List<Modification> getModificationsOnPeptide() {
        return modificationsOnPeptide;
    }

    public void setModificationsOnPeptide(List<Modification> modificationsOnPeptide) {
        this.modificationsOnPeptide = modificationsOnPeptide;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getComponent() {
        return component;
    }

    public void setPeptideSequence(String peptideSequence) {
        this.peptideSequence = peptideSequence;
    }

    public void setMassOverCharge(double massOverCharge) {
        this.massOverCharge = massOverCharge;
    }

    public void addPeptidelinkToPartner(Peptide peptide) {
        peptidesLinkedToPartner.add(peptide);
    }

    public void addRange(ScanRange scanRange) {
        this.scanRange = scanRange;
    }

    public String getPeptideSequence() {
        return peptideSequence;
    }

    public List<Peptide> getPeptidesLinkedToPartner() {
        return peptidesLinkedToPartner;
    }

    public double getMassOverCharge() {
        return massOverCharge;
    }

    public IntensityList getIntensitiesForPartner() {
        return IntensitiesForPartner;
    }

    public ScanRange getScanRange() {
        return scanRange;
    }  
}
