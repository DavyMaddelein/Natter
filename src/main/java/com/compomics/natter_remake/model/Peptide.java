package com.compomics.natter_remake.model;

import java.util.Map;

/**
 *
 * @author Davy
 */
public class Peptide {

    private String sequence = "not yet set";
    private boolean valid = false;
    private String composition = "not yet set";
    private int peptideGroupHitNumber = -1;
    private PeptideGroup peptideGroup = new PeptideGroup();
    private String modifiedSequence = "noet yet set";
    private String modifiedNumericalSequence;
    private PeptideMatch peptideMatch = new PeptideMatch();

    public String getSequence() {
        return sequence;
    }

    public String getModifiedSequence() {
        return modifiedSequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public void setModifiedSequence(String modifiedSequenceNumericalRepresentation, Map<Integer, Modification> listOfModificationsInDistillerFile) {
        this.modifiedSequence = parseModifications(sequence, listOfModificationsInDistillerFile, modifiedSequenceNumericalRepresentation);
    }

    private String parseModifications(String sequence, Map<Integer, Modification> varMods, String numericalVarMods) {
        String modifiedSequenceString = "peptide sequence not yet set";
        if (!sequence.contains("not yet set")) {
            StringBuilder modifiedSequenceBuilder = new StringBuilder();
            modifiedSequenceBuilder.append(varMods.get(Character.getNumericValue(numericalVarMods.charAt(0))).getModification());
            for (int i = 0; i < sequence.length(); i++) {
                int modificationNumber = Character.getNumericValue(numericalVarMods.charAt(i + 1));
                if (varMods.containsKey(modificationNumber)) {
                    modifiedSequenceBuilder.append(varMods.get(modificationNumber).getModification());
                }
                modifiedSequenceBuilder.append(sequence.charAt(i));
            }
            modifiedSequenceBuilder.append(varMods.get(Character.getNumericValue(numericalVarMods.charAt(sequence.length() + 1))).getModification());
            modifiedSequenceString = modifiedSequenceBuilder.toString();
        }
        return modifiedSequenceString;
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

    public PeptideMatch getPeptideMatch() {
        return peptideMatch;
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

    public void setModifiedNumericalSequence(String modifiedNumericalSequence) {
        this.modifiedNumericalSequence = modifiedNumericalSequence;
    }

    public String getModifiedNumericalSequence() {
        return modifiedNumericalSequence;
    }

    public void setPeptideMatch(PeptideMatch peptideMatch) {
        this.peptideMatch = peptideMatch;
    }
}
