package com.compomics.natter_remake.controllers.output;

import com.compomics.natter_remake.model.Intensity;
import com.compomics.natter_remake.model.PeptideMatch;
import com.compomics.natter_remake.model.PeptidePartner;
import com.compomics.natter_remake.model.Protein;

/**
 *
 * @author Davy
 */
public class CSVOutputFormatterForDistillerFile extends OutputFormatter {
//TODO this class has not been finished 

    public CSVOutputFormatterForDistillerFile(String separator) {
        super(separator);
    }

    @Override
    protected String formatPeptideMatchesForProtein(Protein protein, String rovFileName) {
        StringBuilder peptideMatchOutputString = new StringBuilder();
        for (PeptideMatch peptideMatch : protein.getPeptideMatches()) {
            peptideMatchOutputString.append(protein.getAccession()).append(SEPARATOR);
            peptideMatchOutputString.append(peptideMatch.getPeptideSequence());
            peptideMatchOutputString.append(formatPeptidePartnersForMatch(peptideMatch, rovFileName));
        }
        return peptideMatchOutputString.toString();
    }

    @Override
    protected String formatPeptidePartnersForMatch(PeptideMatch peptideMatch, String rovFileName) {
        StringBuilder peptidePartnerOutputString = new StringBuilder();
        for (PeptidePartner partner : peptideMatch.getPeptidePartners()) {
            //peptidePartnerOutputString.append(SEPARATOR).append(partner.getModifiedSequence());
            peptidePartnerOutputString.append(SEPARATOR).append(partner.getComponent());
            peptidePartnerOutputString.append(formatPartnerIntensitiesForPartner(partner));
        }
        peptidePartnerOutputString.append('\n');
        return peptidePartnerOutputString.toString();
    }

    @Override
    protected String formatPartnerIntensitiesForPartner(PeptidePartner partner) {
        StringBuilder intensityOutputString = new StringBuilder();
        intensityOutputString.append(SEPARATOR);
        for (Intensity measuredIntensity : partner.getIntensitiesForPartner()) {
            intensityOutputString.append(measuredIntensity.getValue()).append('@').append(measuredIntensity.getRetentionTime());
        }
        return intensityOutputString.toString();
    }

    @Override
    protected String formatPeptideMatch(PeptideMatch peptideMatch, String rovFileName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
