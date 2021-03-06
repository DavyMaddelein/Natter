package com.compomics.natter_remake.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Davy
 */
public class RovFileData {

    private String fileName = "";
    private List<PeptideGroup> peptideGroups = new ArrayList<PeptideGroup>();
    private List<PeptideMatch> peptideMatchList = new ArrayList<PeptideMatch>(500);
    private List<Protein> proteinHits = new ArrayList<Protein>();
    List<Peptide> peptideHits = new ArrayList<Peptide>();
    private Header header = new Header();
    private Map<Integer,Peptide> peptideQueryMap = new HashMap<Integer,Peptide>();
    private List<RawFile> rawFiles = new ArrayList<RawFile>();
    private String lcRunName;

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

    public void addProteinHit(Protein parsedProteinHit) {
        proteinHits.add(parsedProteinHit);
    }
    
     public List<PeptideMatch> getPeptideMatchList() {
        return Collections.unmodifiableList(peptideMatchList);
    }

    public List<Protein> getProteinHits() {
        return Collections.unmodifiableList(proteinHits);
    }

    public List<Peptide> getPeptideHits() {
        return Collections.unmodifiableList(peptideHits);
    }

    public void addPeptideHit(Peptide peptide) {
        this.peptideHits.add(peptide);
    }

    public Header getHeader() {
        return header;
    }

    public void addRawFile(RawFile parsedRawFile) {
        rawFiles.add(parsedRawFile);
    }
    
    public List<RawFile> getRawFiles(){
        return Collections.unmodifiableList(rawFiles);
    }

    public void addPeptideQuery(Peptide parsedPeptide) {
        peptideQueryMap.put(parsedPeptide.getPeptideNumber(),parsedPeptide);
    }

    public void setLcRunName(String lcRunForFileName) {
        this.lcRunName = lcRunForFileName;
    }
    
    public String getLcRunName(){
        return lcRunName;
    }
}
