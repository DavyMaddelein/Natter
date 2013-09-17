package com.compomics.natter_remake.controllers;

import com.compomics.natter_remake.model.LcRun;
import com.compomics.natter_remake.model.Project;
import com.compomics.natter_remake.model.RovFile;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author Davy
 */
public class DbDAO {

    /**
     * get all the projects in memory for the established connection
     *
     * @param project the {@code Project} to get the data for
     * @return a {@code List} containing the distiller files for that project
     * @throws SQLException if there was a problem with the retrieval of the
     * distiller files
     * @throws IOException
     */
    public static List<RovFile> downloadRovFilesInMemoryForProject(Project project) throws SQLException, IOException {
        List<RovFile> files = new ArrayList<RovFile>();
        PreparedStatement stat = DbConnectionController.getConnection().prepareStatement(new StringBuilder().append("select distinct qf.filename, qf.file from (select distinct q.l_quantitation_fileid as temp from identification as i, spectrum as f , identification_to_quantitation as t, quantitation_group as q where i.l_spectrumid = f.spectrumid and f.l_projectid = ").append(project.getProjectId()).append(" and i.identificationid = t.l_identificationid and t.l_quantitation_groupid = q.quantitation_groupid) as linker, quantitation_file as qf where linker.temp = qf.quantitation_fileid").toString());
        try {
            ResultSet rs = stat.executeQuery();
            try {
                while (rs.next()) {
                    files.add(new RovFile(rs.getString("filename"), FileDAO.unGzipByteArray(rs.getBytes("file")).get(0)));
                }
            }
            finally {
                rs.close();
            }
        }
        finally {
            stat.close();
        }
        return files;
    }

    /**
     * download the distiller files to disc in the OS_temp_dir/natter_rov_files,
     * these will be removed on program termination
     *
     * @param project the {@code Project} to get the distiller files for
     * @return a {@code List} containing the Files
     * @throws SQLException
     * @throws NullPointerException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static List<RovFile> downloadRovFilesLocallyForProject(Project project) throws SQLException, NullPointerException, FileNotFoundException, IOException {
        return downloadRovFilesLocallyForProject(project, FileDAO.NATTERTEMPDIR);
    }

    /**
     * download the distiller files to disc in the specified dir, these will be removed on program termination
     *
     * @param project the project to get the distiller files for
     * @param rovFileOutputLocationFolder the folder to download the files in
     * @return a {@code List} containing the Files
     * @throws SQLException
     * @throws NullPointerException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static List<RovFile> downloadRovFilesLocallyForProject(Project project, File rovFileOutputLocationFolder) throws SQLException, NullPointerException, FileNotFoundException, IOException {
        return downloadRovFilesLocallyForProject(project, rovFileOutputLocationFolder, true);
    }

    /**
     * download the distiller files to disc in the specified dir
     *
     * @param project the project to get the distiller files for
     * @param rovFileOutputLocationFolder the folder to download the files in
     * @param deleteOnExit if the files should be deleted after program
     * termination
     * @return a {@code List} containing the Files
     * @throws SQLException
     * @throws NullPointerException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static List<RovFile> downloadRovFilesLocallyForProject(Project project, File rovFileOutputLocationFolder, boolean deleteOnExit) throws SQLException, NullPointerException, FileNotFoundException, IOException {
        List<RovFile> files = new ArrayList<RovFile>();
        PreparedStatement stat = DbConnectionController.getConnection().prepareStatement(new StringBuilder().append("select distinct qf.filename, qf.file from (select distinct q.l_quantitation_fileid as temp from identification as i, spectrum as f , identification_to_quantitation as t, quantitation_group as q where i.l_spectrumid = f.spectrumid and f.l_projectid = ").append(project.getProjectId()).append(" and i.identificationid = t.l_identificationid and t.l_quantitation_groupid = q.quantitation_groupid) as linker, quantitation_file as qf where linker.temp = qf.quantitation_fileid order by qf.quantitation_fileid").toString());
        try {
            ResultSet rs = stat.executeQuery();
            try {
                while (rs.next()) {
                    if (FileDAO.unzipAndWriteByteArrayToDisk(rs.getBytes("file"), rs.getString("filename"), rovFileOutputLocationFolder, deleteOnExit)) {
                        files.add(new RovFile(String.format("%s%s", rovFileOutputLocationFolder.getAbsolutePath(), rs.getString("filename"))));
                    }
                }
            }
            finally {
                rs.close();
            }
        }
        finally {
            stat.close();
        }
        return files;
    }

    /**
     * get an in memory representation of the distiller file referenced in the
     * db by the quantitation_fileid
     *
     * @param quantitation_fileid the id in the database to get the distiller
     * file for
     * @return the distiller file with the contents in memory
     * @throws SQLException
     * @throws IOException
     */
    public static RovFile getQuantitationFileForQuantitationFileId(Integer quantitation_fileid) throws SQLException, IOException {
        PreparedStatement stat = DbConnectionController.getConnection().prepareStatement(new StringBuilder().append("select filename,file from quantitation_file where quantitation_fileid = ").append(quantitation_fileid).toString());
        RovFile rovFile;
        try {
            ResultSet rs = stat.executeQuery();
            try {
                rs.next();
                rovFile = new RovFile(rs.getString("filename"), FileDAO.unGzipByteArray(rs.getBytes("file")).get(0));
            }
            finally {
                rs.close();
            }
        }
        finally {
            stat.close();
        }
        return rovFile;
    }

    /**
     * get all the quantitanion_fileids for a given project from the db
     *
     * @param project the project to get the quantitation_fileids for
     * @return a {@code List} with the Ids
     * @throws SQLException
     */
    public static List<Integer> getQuantitationFileIdsForProject(Project project) throws SQLException {
        List<Integer> quantitationFileIds = new ArrayList<Integer>();
        PreparedStatement stat = DbConnectionController.getConnection().prepareStatement(new StringBuilder().append("select distinct qg.l_quantitation_fileid as fileid from quantitation_group as qg, identification_to_quantitation as itq, (select identification.identificationid as result from identification,spectrum where l_spectrumid = spectrumid and l_projectid = ").append(project.getProjectId()).append(") as ident_result where ident_result.result = itq.l_identificationid and qg.quantitation_groupid = itq.l_quantitation_groupid").toString());
        try {
            ResultSet rs = stat.executeQuery();
            try {
                while (rs.next()) {
                    quantitationFileIds.add(rs.getInt("fileid"));
                }
            }
            finally {
                rs.close();
            }
        }
        finally {
            stat.close();
        }
        return quantitationFileIds;
    }

    /**
     * get all the projects from the database
     *
     * @return a {@code List} containing all the projects
     * @throws SQLException
     */
    public static List<Project> getAllProjects() throws SQLException {
        List<Project> projects = new ArrayList<Project>();
        PreparedStatement stat = DbConnectionController.getConnection().prepareStatement("select projectid,title from project");
        try {
            ResultSet rs = stat.executeQuery();
            try {
                while (rs.next()) {
                    projects.add(new Project(rs.getInt("projectid"), rs.getString("title")));
                }
            }
            finally {
                rs.close();
            }
        }
        finally {
            stat.close();
        }
        return projects;
    }
/**
 * simple check if the peptide sequence was found in the project
 * @param peptideSequence the sequence to check
 * @param projectNumber the project number to check in
 * @return true if found, false otherwise
 * @throws SQLException 
 */
    public static boolean checkIfPeptideIsIdentified(String peptideSequence, int projectNumber) throws SQLException {
        boolean identified = false;
        PreparedStatement stat = DbConnectionController.getConnection().prepareStatement("select count(identification.*) from spectrum,identification where l_projectid = ? and l_spectrumid = spectrumid and sequence = ?");
        try {
            stat.setInt(1, projectNumber);
            stat.setString(2, peptideSequence);
            ResultSet rs = stat.executeQuery();
            try {
                if (rs.isBeforeFirst() && !rs.isAfterLast()) {
                    identified = true;
                }
            }
            finally {
                rs.close();
            }
        }
        finally {
            stat.close();
        }
        return identified;
    }
/**
 * create a project object from a given projectid
 * @param projectId the project number in the database
 * @return a {@code Project} representation of the project in the db
 * @throws SQLException 
 */
    public static Project getProjectForProjectId(int projectId) throws SQLException {
        Project project;
        //TODO add search on name?
        PreparedStatement stat = DbConnectionController.getConnection().prepareStatement("select project.title from project,lcrun where projectid = ? and l_projectid = projectid");
        try {
            stat.setInt(1, projectId);
            ResultSet rs = stat.executeQuery();
            try {
                rs.next();
                project = new Project(projectId, rs.getString(1));
            }
            finally {
                rs.close();
            }
        }
        finally {
            stat.close();
        }
        return project;
    }

    /**
     * retrieves and adds the liquid chromatography runs for a project
     *
     * @param project the project to get and add the liquid chromatography runs
     * for
     * @throws SQLException
     */
    public static void addLcRunsToProject(Project project) throws SQLException {
        PreparedStatement stat = null;
        try {
            stat = DbConnectionController.getConnection().prepareStatement("select lcrun.lcrunid,lcrun.name from lcrun where l_projectid = ?");
            stat.setInt(1, project.getProjectId());
            ResultSet rs = stat.executeQuery();
            try {
                while (rs.next()) {
                    project.addLcRun(new LcRun(rs.getInt(1), rs.getString(2)));
                }
            }
            finally {
                rs.close();
            }
        }
        finally {
            if (stat != null) {
                stat.close();
            }
        }
    }

    /**
     * get the liquid chromatography run from ms-lims where the peptide was
     * found in
     *
     * @param peptideSequence the sequence of the peptide
     * @param projectId the project to look in
     * @return a concatenated, comma separated {@code String} containing all the
     * liquid chromatography run names containing the peptide sequence for a
     * given project
     * @throws SQLException
     */
    public static String getLcRunForPeptideInProject(String peptideSequence, int projectId) throws SQLException {
        StringBuilder lcrunName = new StringBuilder();
        PreparedStatement stat = DbConnectionController.getConnection().prepareStatement("select distinct lcrun.name from spectrum,identification,lcrun where lcrunid = l_lcrunid and spectrum.l_projectid = ? and l_spectrumid = spectrumid and sequence = ?");
        try {
            stat.setInt(1, projectId);
            stat.setString(2, peptideSequence);
            ResultSet rs = stat.executeQuery();
            try {
                while (rs.next()) {
                    lcrunName.append(rs.getString(1)).append(",");
                }
                if (lcrunName.length() > 0) {
                    lcrunName.deleteCharAt(lcrunName.length() - 1);
                }
            }
            finally {
                rs.close();
            }
        }
        finally {
            stat.close();
        }
        return lcrunName.toString();
    }

    /**
     * get the identification for a sequence recorded in a project
     *
     * @param peptideSequence the sequence to retrieve the identification data
     * for
     * @param projectNumber the project number to search in
     * @return a comma separated string with the identification data
     * @throws SQLException
     */
    public static String getIdentificationForSequenceInProject(String peptideSequence, String rovFileName, String component) throws SQLException {
        return getIdentificationForSequenceInProject(peptideSequence, rovFileName, component, ",");
    }

    /**
     * get the identification for a sequence recorded in a project
     *
     * @param peptideSequence the sequence to retrieve the identification data
     * for
     * @param projectNumber the project number to search in
     * @param separator the separator to use in constructing the string
     * @return a {@code String}
     * @throws SQLException
     */
    public static String getIdentificationForSequenceInProject(String peptideSequence, String rovFileName, String component, String separator) throws SQLException {
        StringBuilder result = new StringBuilder();
        result.append(separator);
        PreparedStatement stat = DbConnectionController.getConnection().prepareStatement("select identification.* from identification,identification_to_quantitation,quantitation_group, quantitation_file where quantitation_file.quantitation_fileid = l_quantitation_fileid and l_quantitation_groupid = quantitation_groupid and l_identificationid = identificationid and sequence =? and identification_to_quantitation.type = ? and quantitation_file.filename = ?");
        try {
            stat.setString(3, rovFileName);
            stat.setString(1, peptideSequence);
            stat.setString(2, component);
            ResultSet rs = stat.executeQuery();
            try {
                if (!rs.next()) {
                    //becausse for loops are for wusses
                    result.append(separator);
                    result.append(separator);
                    result.append(separator);
                    result.append(separator);
                    result.append(separator);
                    result.append(separator);
                    result.append(separator);
                    result.append(separator);
                    result.append(separator);
                    result.append(separator);
                    result.append(separator);
                    result.append(separator);
                    result.append(separator);
                    result.append(separator);
                    result.append(separator);
                    result.append(separator);
                    result.append(separator);
                    result.append(separator);
                    result.append(separator);
                    result.append(separator);
                    result.append(separator);
                    result.append(separator);
                    result.append(separator);
                    result.append(separator);
                    result.append(separator);
                } else {
                    result.append(rs.getLong(1)).append(separator);
                    result.append(rs.getLong(2)).append(separator);
                    result.append(rs.getLong(3)).append(separator);
                    result.append(rs.getLong(4)).append(separator);
                    result.append(rs.getString(5)).append(separator);
                    result.append(rs.getLong(6)).append(separator);
                    result.append(rs.getLong(7)).append(separator);
                    result.append(rs.getString(8)).append(separator);
                    result.append(rs.getString(9)).append(separator);
                    result.append(rs.getString(10)).append(separator);
                    result.append(rs.getLong(12)).append(separator);
                    result.append(rs.getDouble(13)).append(separator);
                    result.append(rs.getLong(14)).append(separator);
                    result.append(rs.getLong(15)).append(separator);
                    result.append(rs.getLong(16)).append(separator);
                    result.append(rs.getLong(17)).append(separator);
                    result.append(rs.getInt(18)).append(separator);
                    result.append(rs.getString(19)).append(separator);
                    result.append(rs.getLong(20)).append(separator);
                    result.append(rs.getString(21)).append(separator);
                    result.append(rs.getString(22)).append(separator);
                    result.append(rs.getString(23)).append(separator);
                    result.append(rs.getString(24)).append(separator);
                    result.append(rs.getString(25)).append(separator);
                    result.append(rs.getString(26)).append(separator);
                    result.append(rs.getString(27));
                }
            }
            finally {
                rs.close();
            }
        }
        finally {
            stat.close();
        }
        result.delete(0, separator.length());
        return result.toString();
    }

    /**
     * download distiller files from the database and keep them in memory for a
     * given liquid chromatography run
     *
     * @param lcrun the liquid chromatography run to retrieve for
     * @return a <@code List> containing the retrieved distiller files
     * @throws SQLException
     * @throws IOException
     */
    static List<RovFile> downloadRovFilesInMemoryForLcrun(LcRun lcrun) throws SQLException, IOException {
        List<RovFile> files = new ArrayList<RovFile>();
        PreparedStatement stat = DbConnectionController.getConnection().prepareStatement(new StringBuilder().append("select distinct qf.filename, qf.file from (select distinct q.l_quantitation_fileid as temp from identification as i, spectrum as f , identification_to_quantitation as t, quantitation_group as q where i.l_spectrumid = f.spectrumid and f.l_lcrunid = ").append(lcrun.getLcRunDbNumber()).append(" and i.identificationid = t.l_identificationid and t.l_quantitation_groupid = q.quantitation_groupid) as linker, quantitation_file as qf where linker.temp = qf.quantitation_fileid").toString());
        try {
            ResultSet rs = stat.executeQuery();
            try {
                while (rs.next()) {
                    files.add(new RovFile(rs.getString("filename"), FileDAO.unGzipByteArray(rs.getBytes("file")).get(0)));
                }
            }
            finally {
                rs.close();
            }
        }
        finally {
            stat.close();
        }
        return files;
    }
}
