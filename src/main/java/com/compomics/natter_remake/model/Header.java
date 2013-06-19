/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.natter_remake.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Davy
 */
public class Header {

    private String proteaseUsed;
    private int cutOff;
    private String distillerVersion;
    private String quantitationMethod;
    private int matchedPeptides;
    private int matchedProteins;
    private int foundPeptides;
    private List<String> modsUsed = new ArrayList<String>();

    public String getProteaseUsed() {
        return proteaseUsed;
    }

    public void setProteaseUsed(String proteaseUsed) {
        this.proteaseUsed = proteaseUsed;
    }

    public int getCutOff() {
        return cutOff;
    }

    public void setCutOff(int cutOff) {
        this.cutOff = cutOff;
    }

    public String getDistillerVersion() {
        return distillerVersion;
    }

    public void setDistillerVersion(String distillerVersion) {
        this.distillerVersion = distillerVersion;
    }

    public List<String> getModsUsed() {
        return Collections.unmodifiableList(modsUsed);
    }

    public void addMod(String mod) {
        modsUsed.add(mod);
    }

    public void setModsUsed(List<String> modsUsed) {
        this.modsUsed = modsUsed;
    }

    public String getQuantitationMethod() {
        return quantitationMethod;
    }

    public void setQuantitationMethod(String quantitationMethod) {
        this.quantitationMethod = quantitationMethod;
    }

    public int getMatchedPeptides() {
        return matchedPeptides;
    }

    public void setMatchedPeptides(int matchedPeptides) {
        this.matchedPeptides = matchedPeptides;
    }

    public int getMatchedProteins() {
        return matchedProteins;
    }

    public void setMatchedProteins(int matchedProteins) {
        this.matchedProteins = matchedProteins;
    }

    public int getFoundPeptides() {
        return foundPeptides;
    }

    public void setFoundPeptides(int foundPeptides) {
        this.foundPeptides = foundPeptides;
    }
}
