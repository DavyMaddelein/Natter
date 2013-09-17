package com.compomics.natter_remake.controllers;

import com.compomics.natter_remake.model.LcRun;
import com.compomics.natter_remake.model.Project;
import com.compomics.natter_remake.model.RovFile;
import com.compomics.natter_remake.model.RovFileData;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

/**
 *
 * @author Davy
 */
public class DataExtractor {

    private static final Logger logger = Logger.getLogger(DataExtractor.class);

    /**
     * fetches and extracts the data from distiller files in memory
     *
     * @param project the project to get the distiller data for
     * @return a List of RovFile objects with their respective data added to
     * them
     * @throws SQLException
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws XMLStreamException
     */
    public static List<RovFile> extractDataInMem(Project project) throws SQLException, ParserConfigurationException, IOException, XMLStreamException {
        InputStreamReader rovFileInputStreamReader;
        List<RovFile> rovFiles = DbDAO.downloadRovFilesInMemoryForProject(project);
        for (RovFile file : rovFiles) {
            rovFileInputStreamReader = new InputStreamReader(new ByteArrayInputStream(file.getFileContent()), "UTF-8");
            file.addParsedData(parseRovFile(rovFileInputStreamReader));
            rovFileInputStreamReader.close();
        }
        return rovFiles;
    }

    /**
     * extracts the data in distiller files connected to a given liquid chromatography run
     * @param lcrun the liquid chromatography run
     * @return a {@code List} of distiller file objects
     * @throws UnsupportedEncodingException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws XMLStreamException
     * @throws SQLException
     */
    public static List<RovFile> extractDataInMem(LcRun lcrun) throws UnsupportedEncodingException, FileNotFoundException, IOException, ParserConfigurationException, XMLStreamException, SQLException {
        InputStreamReader rovFileInputStreamReader;
        List<RovFile> rovFiles = DbDAO.downloadRovFilesInMemoryForLcrun(lcrun);
        for (RovFile file : rovFiles) {
            rovFileInputStreamReader = new InputStreamReader(new ByteArrayInputStream(file.getFileContent()), "UTF-8");
            file.addParsedData(parseRovFile(rovFileInputStreamReader));
            rovFileInputStreamReader.close();
        }
        return rovFiles;
    }

    /**
     * fetches and extracts the data in the Distiller files one by one in memory
     *
     * @param project the project in ms-lims to extract the data from
     * @return a list of Distiller files in memory with the extracted data added
     * to them
     * @throws SQLException if the
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws XMLStreamException
     */
    public static List<RovFile> extractDataLowMem(Project project) throws SQLException, ParserConfigurationException, IOException, XMLStreamException {
        List<Integer> quantitationFileIds = DbDAO.getQuantitationFileIdsForProject(project);
        List<RovFile> rovFiles = new ArrayList<RovFile>(quantitationFileIds.size());
        for (Integer quantitation_fileid : quantitationFileIds) {
            RovFile rovFile = (extractDataForQuantitationFileIdInMem(quantitation_fileid));
            rovFiles.add(rovFile);
        }
        return rovFiles;
    }

    /**
     * extracts the distiller data for a quantitation_fileid in memory
     *
     * @param quantitationFileId the quantitation fileid in the db to extract
     * the data from
     * @return RovFile object containing the extracted data
     * @throws SQLException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws XMLStreamException
     */
    public static RovFile extractDataForQuantitationFileIdInMem(int quantitationFileId) throws SQLException, IOException, ParserConfigurationException, XMLStreamException {
        InputStreamReader rovFileInputStreamReader;
        RovFile rovFile = DbDAO.getQuantitationFileForQuantitationFileId(quantitationFileId);
        rovFileInputStreamReader = new InputStreamReader(new ByteArrayInputStream(rovFile.getFileContent()), "UTF-8");
        rovFile.addParsedData(parseRovFile(rovFileInputStreamReader));
        return rovFile;
    }

    /**
     * extracts the rov files to the OS temp dir and removes them afterwards
     *
     * @param project the project to extract the data from
     * @throws SQLException
     * @throws FileNotFoundException
     */
    public static List<RovFile> extractDataToLocal(Project project) throws SQLException, FileNotFoundException, NullPointerException, IOException, ParserConfigurationException, SAXException, XMLStreamException {
        File natterSaveLocation = new File(MessageFormat.format("{0}/natter_output_files", System.getProperty("java.io.tmpdir")));
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    File natterSaveLocation = new File(MessageFormat.format("{0}/natter_output_files", System.getProperty("java.io.tmpdir")));
                    if (natterSaveLocation.exists()) {
                        FileUtils.deleteDirectory(natterSaveLocation);
                    }
                }
                catch (IOException ex) {
                    logger.error(ex);
                }
            }
        });
        return extractDataToLocal(project, natterSaveLocation);
    }

    /**
     * extracts the distiller files to the specified dir
     *
     * @param project the project to extract the data from
     * @param rovFileOutputLocationFolder
     * @return
     * @throws SQLException
     * @throws FileNotFoundException
     * @throws NullPointerException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws XMLStreamException
     */
    public static List<RovFile> extractDataToLocal(Project project, File rovFileOutputLocationFolder) throws SQLException, FileNotFoundException, NullPointerException, IOException, ParserConfigurationException, SAXException, XMLStreamException {
        List<RovFile> filesToRun = DbDAO.downloadRovFilesLocallyForProject(project, rovFileOutputLocationFolder);
        for (RovFile file : filesToRun) {
            file.addParsedData(parseRovFile(file));
        }
        return filesToRun;
    }

    /**
     * fetches and extracts the rov files one by one to the specified
     * outputfolder
     *
     * @param rovFileOutputLocationFolder the folder to write the data to
     * @param project the project to extract the data from
     * @throws SQLException
     * @throws FileNotFoundException
     * @throws NullPointerException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws XMLStreamException
     */
    public static void extractDataToLocalLowMem(File rovFileOutputLocationFolder, Project project) throws SQLException, FileNotFoundException, NullPointerException, IOException, ParserConfigurationException, SAXException, XMLStreamException {
        List<RovFile> filesToRun = DbDAO.downloadRovFilesLocallyForProject(project, rovFileOutputLocationFolder);
        for (RovFile file : filesToRun) {
            file.addParsedData(parseRovFile(file));
            FileDAO.writeExtractedDataToDisk(file);
        }
    }

    /**
     * distiller file parser for xml files
     *
     * @param rovFile the distiller file to parse
     * @return a RovFileData object containing the parsed data
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws XMLStreamException
     */
    public static RovFileData parseRovFile(File rovFile) throws ParserConfigurationException, IOException, XMLStreamException {
        InvalidXMLCharacterFilterReader rovFileStreamReader = null;
        RovFileData data;
        try {
            rovFileStreamReader = new InvalidXMLCharacterFilterReader(new InputStreamReader(new FileInputStream(rovFile.getAbsolutePath()), "UTF-8"));
            data = DataExtractor.parseRovFile(rovFileStreamReader);
        }
        finally {
            if (rovFileStreamReader != null) {
                rovFileStreamReader.close();
            }
        }
        return data;
    }

    /**
     * distiller file parser method for readers
     *
     * @param reader stream reader containing the content from a distiller file
     * @return a RovFileData object containing the parsed data
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws XMLStreamException
     */
    public static RovFileData parseRovFile(Reader reader) throws ParserConfigurationException, IOException, XMLStreamException {
        XMLInputFactory xmlParseFactory = XMLInputFactory.newInstance();
        XMLEventReader xmlReader = xmlParseFactory.createXMLEventReader(reader);
        RovFileXMLParser xmlParser = new RovFileXMLParser(xmlReader);
        return xmlParser.getRovFileData();
    }
}
