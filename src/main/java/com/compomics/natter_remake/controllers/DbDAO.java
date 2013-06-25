package com.compomics.natter_remake.controllers;

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

    public static List<RovFile> downloadRovFilesInMemoryForProject(Project project) throws SQLException, IOException {
        List<RovFile> files = new ArrayList<RovFile>();
        PreparedStatement stat = DbConnectionController.getConnection().prepareStatement(new StringBuilder().append("select qf.filename, qf.file from (select distinct q.l_quantitation_fileid as temp from identification as i, spectrum as f , identification_to_quantitation as t, quantitation_group as q where i.l_spectrumid = f.spectrumid and f.l_projectid = ").append(project.getProjectId()).append(" and i.identificationid = t.l_identificationid and t.l_quantitation_groupid = q.quantitation_groupid) as linker, quantitation_file as qf where linker.temp = qf.quantitation_fileid").toString());
        ResultSet rs = stat.executeQuery();
        while (rs.next()) {
            files.add(new RovFile(rs.getString("filename"), FileDAO.unzipByteArray(rs.getBytes("file"))));
        }
        rs.close();
        stat.close();
        return files;
    }

    public static List<RovFile> downloadRovFilesLocallyForProject(Project project) throws SQLException, NullPointerException, FileNotFoundException, IOException {
        return downloadRovFilesLocallyForProject(project, FileDAO.NATTERTEMPDIR);
    }

    public static List<RovFile> downloadRovFilesLocallyForProject(Project project, File rovFileOutputLocationFolder) throws SQLException, NullPointerException, FileNotFoundException, IOException {
        return downloadRovFilesLocallyForProject(project, rovFileOutputLocationFolder, true);
    }

    public static List<RovFile> downloadRovFilesLocallyForProject(Project project, File rovFileOutputLocationFolder, boolean deleteOnExit) throws SQLException, NullPointerException, FileNotFoundException, IOException {
        List<RovFile> files = new ArrayList<RovFile>();
        PreparedStatement stat = DbConnectionController.getConnection().prepareStatement(new StringBuilder().append("select qf.filename, qf.file from (select distinct q.l_quantitation_fileid as temp from identification as i, spectrum as f , identification_to_quantitation as t, quantitation_group as q where i.l_spectrumid = f.spectrumid and f.l_projectid = ").append(project.getProjectId()).append(" and i.identificationid = t.l_identificationid and t.l_quantitation_groupid = q.quantitation_groupid) as linker, quantitation_file as qf where linker.temp = qf.quantitation_fileid order by qf.quantitation_fileid").toString());
        ResultSet rs = stat.executeQuery();
        while (rs.next()) {
            files.add(FileDAO.unzipAndWriteByteArrayToDisk(rs.getBytes("file"), rs.getString("filename"), rovFileOutputLocationFolder, deleteOnExit));
        }
        rs.close();
        stat.close();
        return files;
    }

    public static RovFile getQuantitationFileForQuantitationFileId(Integer quantitation_fileid) throws SQLException, IOException {
        PreparedStatement stat = DbConnectionController.getConnection().prepareStatement(new StringBuilder().append("select filename,file from quantitation_file where quantitation_fileid = ").append(quantitation_fileid).toString());
        ResultSet rs = stat.executeQuery();
        rs.next();
        RovFile rovFile = new RovFile(rs.getString("filename"), FileDAO.unzipByteArray(rs.getBytes("file")));
        rs.close();
        stat.close();
        return rovFile;
    }

    public static List<Integer> getQuantitationFileIdsForProject(Project project) throws SQLException {
        List<Integer> quantitationFileIds = new ArrayList<Integer>();
        PreparedStatement stat = DbConnectionController.getConnection().prepareStatement(new StringBuilder().append("select distinct qg.l_quantitation_fileid as fileid from quantitation_group as qg, identification_to_quantitation as itq, (select identification.identificationid as result from identification,spectrum where l_spectrumid = spectrumid and l_projectid = ").append(project.getProjectId()).append(") as ident_result where ident_result.result = itq.l_identificationid and qg.quantitation_groupid = itq.l_quantitation_groupid").toString());
        ResultSet rs = stat.executeQuery();
        while (rs.next()) {
            quantitationFileIds.add(rs.getInt("fileid"));
        }
        rs.close();
        stat.close();
        return quantitationFileIds;
    }

    public static List<Project> getAllProjects() throws SQLException {
        List<Project> projects = new ArrayList<Project>();
        PreparedStatement stat = DbConnectionController.getConnection().prepareStatement("select projectid,title from project");
        ResultSet rs = stat.executeQuery();
        while (rs.next()) {
            projects.add(new Project(rs.getInt("projectid"), rs.getString("title")));
        }
        rs.close();
        stat.close();
        return projects;
    }
}
