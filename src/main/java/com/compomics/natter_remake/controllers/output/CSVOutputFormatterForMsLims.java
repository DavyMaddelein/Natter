package com.compomics.natter_remake.controllers.output;

import com.compomics.natter_remake.controllers.DbDAO;
import com.compomics.natter_remake.model.Intensity;
import com.compomics.natter_remake.model.LcRun;
import com.compomics.natter_remake.model.PeptideMatch;
import com.compomics.natter_remake.model.PeptidePartner;
import com.compomics.natter_remake.model.Protein;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Davy
 */
public class CSVOutputFormatterForMsLims extends OutputFormatter {

    LcRun lcrun;

    public CSVOutputFormatterForMsLims(String separator, LcRun project) {
        super(separator);
        this.lcrun = project;
    }

    @Override
    protected String formatPeptideMatch(PeptideMatch peptideMatch, String rovFileName) {
        StringBuilder peptideMatchOutputString = new StringBuilder();
        peptideMatchOutputString.append(peptideMatch.getPeptideSequence()).append(SEPARATOR);
        peptideMatchOutputString.append(peptideMatch.getOriginalRatio().getRatioValue()).append(SEPARATOR);
        peptideMatchOutputString.append(lcrun.getLcRunTitle()).append(SEPARATOR);
        peptideMatchOutputString.append(formatPeptidePartnersForMatch(peptideMatch, rovFileName));

        return peptideMatchOutputString.toString();
    }

    @Override
    protected String formatPeptideMatchesForProtein(Protein protein, String rovFileName) {
        StringBuilder peptideMatchOutputString = new StringBuilder();
        for (PeptideMatch peptideMatch : protein.getPeptideMatches()) {
            peptideMatchOutputString.append(protein.getAccession()).append(SEPARATOR);
            peptideMatchOutputString.append(peptideMatch.getPeptideSequence()).append(SEPARATOR);
            peptideMatchOutputString.append(peptideMatch.getOriginalRatio().getRatioValue()).append(SEPARATOR);
            peptideMatchOutputString.append(lcrun.getLcRunTitle()).append(SEPARATOR);
            peptideMatchOutputString.append(formatPeptidePartnersForMatch(peptideMatch, rovFileName));
        }
        return peptideMatchOutputString.toString();
    }

    protected String formatPeptidePartnersForMatch(PeptideMatch peptideMatch, String rovFileName) {
        StringBuilder peptidePartnerOutputString = new StringBuilder();
        for (PeptidePartner partner : peptideMatch.getPeptidePartners()) {
            //peptidePartnerOutputString.append(partner.isPartnerFound()).append(SEPARATOR);
            //if (!partner.getPeptidesLinkedToPartner().isEmpty()) {
            //peptidePartnerOutputString.append("true").append(SEPARATOR);
            //try {
            // peptidePartnerOutputString.append(partner.getPeptidesLinkedToPartner().get(0).getModifiedSequence()).append(SEPARATOR);
            // }
            // catch (Exception e) {
            //     peptidePartnerOutputString.append("error getting modified sequence from peptidegroup").append(SEPARATOR);
            // }
            // } else {
            //peptidePartnerOutputString.append("false").append(SEPARATOR).append(SEPARATOR);
            // }
            peptidePartnerOutputString.append(partner.getComponent()).append(SEPARATOR);
            try {
                peptidePartnerOutputString.append(DbDAO.getIdentificationForSequenceInProject(peptideMatch.getPeptideSequence(), rovFileName, partner.getComponent(), SEPARATOR)).append(SEPARATOR);
            }
            catch (SQLException ex) {
                ex.printStackTrace();
            }


            peptidePartnerOutputString.append(formatPartnerIntensitiesForPartner(partner)).append(SEPARATOR);
        }
        peptidePartnerOutputString.deleteCharAt(peptidePartnerOutputString.length() - 1);
        peptidePartnerOutputString.append('\n');
        return peptidePartnerOutputString.toString();
    }

    protected String formatPartnerIntensitiesForPartner(PeptidePartner partner) {
        StringBuilder intensityOutputString = new StringBuilder();
        for (Intensity measuredIntensity : partner.getIntensitiesForPartner()) {
            intensityOutputString.append(measuredIntensity.getValue()).append('@').append(measuredIntensity.getRetentionTime()).append("/");
        }
        intensityOutputString.deleteCharAt(intensityOutputString.length() - 1);
        return intensityOutputString.toString();
    }
}
