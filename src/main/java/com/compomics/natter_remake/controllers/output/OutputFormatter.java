package com.compomics.natter_remake.controllers.output;

import com.compomics.natter_remake.model.Intensity;
import com.compomics.natter_remake.model.PeptideMatch;
import com.compomics.natter_remake.model.PeptidePartner;
import com.compomics.natter_remake.model.Protein;
import com.compomics.natter_remake.model.RovFileData;

/**
 *
 * @author Davy
 */
public class OutputFormatter {

    public static String formatForCSV(RovFileData data) {
        StringBuilder outputString = new StringBuilder();
        for (Protein protein : data.getProteinHits()) {
            outputString.append(formatPeptideMatchesForProtein(protein));
        }
        return outputString.toString();

    }

    private static String formatPeptideMatchesForProtein(Protein protein) {
        StringBuilder peptideMatchOutputString = new StringBuilder();
        for (PeptideMatch peptideMatch : protein.getPeptideMatches()) {
            peptideMatchOutputString.append(protein.getAccession()).append(';');
            peptideMatchOutputString.append(peptideMatch.getPeptideSequence());
            peptideMatchOutputString.append(formatPeptidePartnersForMatch(peptideMatch));
        }
        return peptideMatchOutputString.toString();
    }

    private static String formatPeptidePartnersForMatch(PeptideMatch peptideMatch) {
        StringBuilder peptidePartnerOutputString = new StringBuilder();
        for (PeptidePartner partner : peptideMatch.getPeptidePartners()) {
            peptidePartnerOutputString.append(';').append(partner.getComponent()).append(';');
            peptidePartnerOutputString.append(formatPartnerIntensitiesForPartner(partner));
        }
        peptidePartnerOutputString.append('\n');
        return peptidePartnerOutputString.toString();
    }

    private static String formatPartnerIntensitiesForPartner(PeptidePartner partner) {
        StringBuilder intensityOutputString = new StringBuilder();
        for (Intensity measuredIntensity : partner.getIntensitiesForPartner()) {
            intensityOutputString.append(measuredIntensity.getValue()).append('@').append(measuredIntensity.getRetentionTime());
        }
        return intensityOutputString.toString();
    }
}