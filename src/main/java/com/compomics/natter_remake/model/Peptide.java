package com.compomics.natter_remake.model;

/**
 *
 * @author Davy
 */
public class Peptide {

    private String sequence;
    private boolean valid = false;
    private String composition;
    private int peptideMatchId;
    private int peptideGroupHitNumber;
    private PeptideGroup peptideGroup;
    private String modifiedSequence;

    public String getSequence() {
        return sequence;
    }
    
    public String getModifiedSequence(){
        return modifiedSequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }
    
    public void setModifiedSequence(String modifiedSequence){
        this.modifiedSequence = modifiedSequence;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getComposition() {
        return composition;
    }

    public void setComposition(String composition) {
        this.composition = composition;
    }

    public int getPeptideMatchId() {
        return peptideMatchId;
    }

    public void setPeptideMatchId(int peptideMatchId) {
        this.peptideMatchId = peptideMatchId;
    }

    public void setPeptideNumber(int peptideNumber) {
        this.peptideGroupHitNumber = peptideNumber;
    }

    public int getPeptideNumber() {
        return this.peptideGroupHitNumber;
    }

    public void setPeptideGroup(PeptideGroup peptideGroup) {
        this.peptideGroup = peptideGroup;
    }

    public PeptideGroup getPeptideGroup() {
        return peptideGroup;
    }
}
