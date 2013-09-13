package com.compomics.natter_remake.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Davy
 */
public class PeptidePartner {

    private boolean partnerFound = false;
    private Ratio absoluteRatio;
    private List<Modification> modificationsOnPeptide = new ArrayList<Modification>();
    private String component = "not yet set";
    private String peptideSequence = "not yet set";
    private List<Peptide> peptidesLinkedToPartner = new ArrayList<Peptide>();
    private double massOverCharge = -1.0;
    private IntensityList intensitiesForPartner = new IntensityList();
    private ScanRange scanRange;
    private String modifiedSequence = "not yet set";

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

    public void setModificationsOnPeptide(Map<Integer,Modification> modifications, String numericalValueOfModifications) {
        parseModifications(peptideSequence,modifications,numericalValueOfModifications);
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
        return intensitiesForPartner;
    }

    public ScanRange getScanRange() {
        return scanRange;
    }

    public String getModifiedSequence() {
        return modifiedSequence;
    }

    public void setModifiedSequence(String sequence, Map<Integer,Modification> modifications, String numericalRepresentationOfMods) {
        modifiedSequence = parseModifications(sequence,modifications,numericalRepresentationOfMods);
    }

    private String parseModifications(String sequence, Map<Integer,Modification> varMods,String numericalVarMods) {
        String modifiedSequenceString = "peptide sequence not yet set";
        if (!sequence.contains("not yet set")) {
            StringBuilder modifiedSequenceBuilder = new StringBuilder();
            for(int i = 0; i > numericalVarMods.length(); i++){
                modifiedSequenceBuilder.append(sequence.charAt(sequence.charAt(i)));
            }
            modifiedSequenceString = modifiedSequenceBuilder.toString();
        }
        return modifiedSequenceString;
    }
}
