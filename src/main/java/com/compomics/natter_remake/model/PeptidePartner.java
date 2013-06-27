package com.compomics.natter_remake.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Davy
 */
public class PeptidePartner {

    private IntensityGroup intensitiesRecoredForPeptide;
    private boolean partnerFound;
    private int ratio;
    private List<Modification> modificationsOnPeptide = new ArrayList();
    private String component;
    private String peptideSequence;
    private List<Peptide> peptidesLinkedToPartner = new ArrayList<Peptide>();
    private double massOverCharge;
    private List<Intensity> IntensitiesForPartner = new ArrayList<Intensity>(50);
    private ScanRange scanRange;

    public IntensityGroup getIntensitiesRecordedForPeptide() {
        return intensitiesRecoredForPeptide;
    }

    public void setIntensitiesRecoredForPeptide(IntensityGroup intensitiesRecoredForPeptide) {
        this.intensitiesRecoredForPeptide = intensitiesRecoredForPeptide;
    }

    public boolean isPartnerFound() {
        return partnerFound;
    }

    public void setPartnerFound(boolean partnerFound) {
        this.partnerFound = partnerFound;
    }

    public int getRatio() {
        return ratio;
    }

    public void setRatio(int ratio) {
        this.ratio = ratio;
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

    public void addIntensities(List<Intensity> parseIntensityForPartner) {
        IntensitiesForPartner.addAll(parseIntensityForPartner);
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

    public List<Intensity> getIntensitiesForPartner() {
        return IntensitiesForPartner;
    }

    public ScanRange getScanRange() {
        return scanRange;
    }
    
    
}
