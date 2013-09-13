package com.compomics.natter_remake.controllers.output;

import com.compomics.natter_remake.model.PeptideMatch;
import com.compomics.natter_remake.model.PeptidePartner;
import com.compomics.natter_remake.model.Protein;
import com.compomics.natter_remake.model.RovFile;
import com.compomics.natter_remake.model.RovFileData;

/**
 *
 * @author Davy
 */
public abstract class OutputFormatter {

    protected final String SEPARATOR;

    public OutputFormatter(String separator) {
        SEPARATOR = separator;
    }

    final public String formatData(RovFile rovFile) {
        StringBuilder outputString = new StringBuilder();
        outputString.append(formatHeaderLine()).append("\n");
        for (Protein protein : rovFile.getParsedData().getProteinHits()) {
            outputString.append(formatPeptideMatchesForProtein(protein,rovFile.getName()));
        }
        return outputString.toString();
    }

    final public String formatPeptideMatches(RovFile rovFile) {
        StringBuilder outputString = new StringBuilder();
        outputString.append(formatPeptideMatchHeaderLine()).append("\n");
        for (PeptideMatch peptideMatch : rovFile.getParsedData().getPeptideMatchList()) {
            outputString.append(formatPeptideMatch(peptideMatch,rovFile.getName()));
        }
        return outputString.toString();
    }

    private String formatHeaderLine() {
        String headerLine = "accession;sequence;ratio;lcrun_name;mod_seq_file;composition;identificationid;spectrumid;datfileid;datfile_query;accession;start;end;enzymatic;sequence;modified_sequence;score;homology;exmp_mass;cal_mass;light_isotope;heavy_isotope;valid;Description;identitythreshold;confidence;DB;title;precursor;charge;isoforms;db_filename;intensity@retention time;mod_seq_file;composition;identificationid;spectrumid;datfileid;datfile_query;accession;start;end;enzymatic;sequence;modified_sequence;score;homology;exmp_mass;cal_mass;light_isotope;heavy_isotope;valid;Description;identitythreshold;confidence;DB;title;precursor;charge;isoforms;db_filename;intensity@retention time";       
        return headerLine;
    }

    private String formatPeptideMatchHeaderLine() {
        String headerLine = "sequence;ratio;lcrun_name;mod_seq_file;composition;identificationid;spectrumid;datfileid;datfile_query;accession;start;end;enzymatic;sequence;modified_sequence;score;homology;exmp_mass;cal_mass;light_isotope;heavy_isotope;valid;Description;identitythreshold;confidence;DB;title;precursor;charge;isoforms;db_filename;intensity@retention time;mod_seq_file;composition;identificationid;spectrumid;datfileid;datfile_query;accession;start;end;enzymatic;sequence;modified_sequence;score;homology;exmp_mass;cal_mass;light_isotope;heavy_isotope;valid;Description;identitythreshold;confidence;DB;title;precursor;charge;isoforms;db_filename;intensity@retention time";
        return headerLine;
    }

    protected abstract String formatPeptideMatchesForProtein(Protein protein,String rovFileName);

    protected abstract String formatPeptidePartnersForMatch(PeptideMatch peptideMatch,String rovFileName);

    protected abstract String formatPartnerIntensitiesForPartner(PeptidePartner partner);

    protected abstract String formatPeptideMatch(PeptideMatch peptideMatch,String rovFileName);
}