package com.compomics.natter_remake.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Davy
 */
public class RovFileData {

    private String fileName;
    private List<PeptideGroup> peptideGroups = new ArrayList<PeptideGroup>();
    private List<PeptideMatch> peptideMatchList = new ArrayList<PeptideMatch>(30);
    private List<Protein> proteinHits = new ArrayList<Protein>();
    private Header header = new Header();

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    
    public void setPeptideGroups(List<PeptideGroup> parsedPeptideGroups) {
        peptideGroups.addAll(parsedPeptideGroups);
    }

    public List<PeptideGroup> getPeptideGroups() {
        return Collections.unmodifiableList(peptideGroups);
    }

    public void addPeptideMatch(PeptideMatch peptideMatch) {
        this.peptideMatchList.add(peptideMatch);
    }

    public List<PeptideMatch> getPeptideMatches() {
        return Collections.unmodifiableList(peptideMatchList);
    }

    public void addProteinHit(Protein parsedProteinHit) {
        proteinHits.add(parsedProteinHit);
    }
    
     public List<PeptideMatch> getPeptideMatchList() {
        return Collections.unmodifiableList(peptideMatchList);
    }

    public List<Protein> getProteinHits() {
        return Collections.unmodifiableList(proteinHits);
    }

    public Header getHeader() {
        return header;
    }
}
