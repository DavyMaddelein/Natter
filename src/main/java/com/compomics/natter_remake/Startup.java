package com.compomics.natter_remake;

import com.compomics.natter_remake.controllers.DataExtractor;
import com.compomics.natter_remake.controllers.DbConnectionController;
import com.compomics.natter_remake.controllers.DbDAO;
import com.compomics.natter_remake.controllers.FileDAO;
import com.compomics.natter_remake.controllers.output.CSVOutputFormatterForMsLims;
import com.compomics.natter_remake.model.LcRun;
import com.compomics.natter_remake.model.Project;
import com.compomics.software.CompomicsWrapper;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author Davy
 */
public class Startup extends CompomicsWrapper {

    private Startup(String[] args) throws URISyntaxException {
        File jarFile = new File(Startup.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        // get the splash 
        String mainClass = "com.compomics.natter_remake.StartFrame";
        launchTool("natter", jarFile, null, mainClass, args);
    }

    public static void main(String[] args) throws URISyntaxException, SQLException, IOException, ParserConfigurationException, XMLStreamException, ParseException {

        Options options = new Options();
        options.addOption("-u", true, "ms-lims username");
        options.addOption("-p", true, "ms-lims password");
        options.addOption("-projects", true, "ms-lims project numbers seperated by commas");

        String name = null;
        String password = null;

        CommandLineParser parser = new BasicParser();

        CommandLine cmd = parser.parse(options, args);

        if (cmd.hasOption("-u")) {
            name = cmd.getOptionValue("-u");
        }

        if (cmd.hasOption("-p")) {
            password = cmd.getOptionValue("-p");
        }

        List<String> projectList = new ArrayList<String>();
        //{
        //   {
        //       this.add(1910);
        //    }
        //}; 

        if (cmd.hasOption("-projects")) {

            projectList = Arrays.asList(cmd.getOptionValue("-projects").split(","));
        }
        DbConnectionController.createConnection(name, password, "muppet03.ugent.be", "projects");
        File parentPeptideMatchFolder = new File("Z:\\Davy\\1910_all_peptide_matches");
        File parentDataFolder = new File("Z:\\Davy\\1910");
        for (String i : projectList) {
            try {
                int projectNumber = Integer.valueOf(i);
                Project projectToExtract = DbDAO.getProjectForProjectId(projectNumber);
                DbDAO.addLcRunsToProject(projectToExtract);
                Iterator<LcRun> lcruns = projectToExtract.getLcRuns().iterator();
                while (lcruns.hasNext()) {
                    LcRun aRun = lcruns.next();
                    FileDAO.writeExtractedPeptideMatchesToDisk(DataExtractor.extractDataInMem(aRun), new File(parentPeptideMatchFolder, projectToExtract.getProjectName() + "_" + aRun.getLcRunTitle()), new CSVOutputFormatterForMsLims(";", aRun));
                    FileDAO.writeExtractedDataToDisk(DataExtractor.extractDataInMem(aRun), new File(parentDataFolder, projectToExtract.getProjectName() + "_" + aRun.getLcRunTitle()), new CSVOutputFormatterForMsLims(";", aRun));
                }
            }
            catch (SQLException sqle) {
                sqle.printStackTrace();
            }
            //new Startup(args);
        }
    }
}
