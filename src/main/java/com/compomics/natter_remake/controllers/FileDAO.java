package com.compomics.natter_remake.controllers;

import com.compomics.natter_remake.controllers.output.OutputFormatter;
import com.compomics.natter_remake.model.Header;
import com.compomics.natter_remake.model.RovFile;
import com.compomics.natter_remake.model.RovFileData;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;
import javax.swing.JOptionPane;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author Davy
 */
public class FileDAO {
    private static final Logger logger = Logger.getLogger(FileDAO.class);
    public static final File NATTERTEMPDIR = new File(System.getProperty("java.io.tmpdir") + "/natter_rov_files");
    /**
     * check if file is zipped
     */
    static final String magicZipHexString = "504b0304";
    static final String magicGzipHexString = "1f8b";
    final protected static char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * writes the given byte array to the specified location
     *
     * @param fileContent the byte array to write
     * @param filename name to give the new file
     * @param fileOutputLocation folder to write the file to
     * @param deleteOnExit should the file be deleted at program termination
     * @return the distiller file
     * @throws NullPointerException
     * @throws IOException
     */
    static File writeByteArrayToDisk(byte[] fileContent, String filename, File fileOutputLocation, boolean deleteOnExit) throws NullPointerException, IOException {
        StringBuilder outputString = new StringBuilder();
        if (!fileOutputLocation.exists()) {
            if (!fileOutputLocation.mkdir()) {
                throw new IOException("could not create output folder");
            }
        }
        if (fileOutputLocation.isDirectory()) {
            outputString.append(fileOutputLocation.getAbsolutePath()).append("\\");
        } else if (fileOutputLocation.getParent() == null) {
            //alteratively use homefolder
            throw new FileNotFoundException("file location is not a directory and there is no parent directory");
        } else {
            outputString.append(fileOutputLocation.getParent()).append("\\");
        }

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM_dd_yyyy_h_mm_ss_SS");
        outputString.append(sdf.format(date));
        RovFile outputFile = new RovFile(outputString.toString());
        OutputStream out = new FileOutputStream(outputFile);
        try {
            out.write(fileContent);
        }
        finally {
            if (deleteOnExit) {
                outputFile.deleteOnExit();
            }
            out.close();
        }
        return outputFile;
    }

    /**
     * writes the given byte array to the specified location
     *
     * @param fileContent the byte array to write
     * @param filename name to give the new file
     * @param fileOutputLocation folder to write the file to
     * @param deleteOnExit should the file be deleted at program termination
     * @return the distiller file
     * @throws NullPointerException
     * @throws IOException
     */
    static boolean writeByteArrayToDisk(List<byte[]> fileContent, String filename, File fileOutputLocation, boolean deleteOnExit) throws NullPointerException, IOException {
        for (byte[] file : fileContent) {
            writeByteArrayToDisk(file, filename, fileOutputLocation, deleteOnExit);
        }
        return true;
    }

    /**
     * writes the given byte array to the specified location
     *
     * @param zippedFileContent byte array to unzip and write
     * @param fileOutputLocation the location to write to
     * @return
     * @throws IOException
     */
    public static boolean unzipAndWriteByteArrayToDisk(byte[] zippedFileContent, File fileOutputLocation) throws IOException {
        return writeByteArrayToDisk(inflateByteArray(zippedFileContent), null, fileOutputLocation, true);
    }

    /**
     *
     * @param zippedFileContent
     * @param filename
     * @param fileOutputLocation
     * @return
     * @throws IOException
     */
    public static boolean unzipAndWriteByteArrayToDisk(byte[] zippedFileContent, String filename, File fileOutputLocation) throws IOException {
        return writeByteArrayToDisk(inflateByteArray(zippedFileContent), filename, fileOutputLocation, true);
    }

    /**
     *
     * @param zippedFileContent
     * @param filename
     * @param fileOutputLocation
     * @param deleteOnExit
     * @return
     * @throws IOException
     */
    public static boolean unzipAndWriteByteArrayToDisk(byte[] zippedFileContent, String filename, File fileOutputLocation, boolean deleteOnExit) throws IOException {
        return writeByteArrayToDisk(inflateByteArray(zippedFileContent), filename, fileOutputLocation, deleteOnExit);
    }

    /**
     * unzips a byte array that is a gzip archive
     *
     * @param fileContent
     * @return
     * @throws IOException
     */
    public static List<byte[]> unGzipByteArray(byte[] byteArray) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        IOUtils.copy(new GZIPInputStream(new ByteArrayInputStream(byteArray)), byteArrayOutputStream);
        byte[] byteArrayToReturn = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
        return Arrays.asList(byteArrayToReturn);
    }

    /**
     * unzips a byte array
     *
     * @param byteArray the byte array to unzip
     * @return a list containing the unzipped contents of the byte array,
     * otherwise a list with a single entry being the byte array given to the
     * method
     * @throws IOException
     */
    public static List<byte[]> unzipByteArray(byte[] byteArray) throws IOException {
        //TODO fix this
        List<byte[]> unzippedEntries = new ArrayList<byte[]>();
        if (checkIfByteArrayIsZipped(byteArray)) {
            ZipInputStream zin = new ZipInputStream(new ByteArrayInputStream(byteArray));
            try {
                while (zin.getNextEntry() != null) {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    int count;
                    byte data[] = new byte[50];
                    try {
                        while ((count = zin.read(data, 0, 50)) != -1) {
                            byteArrayOutputStream.write(data, 0, count);
                        }
                        unzippedEntries.add(byteArrayOutputStream.toByteArray());
                    }
                    finally {
                        byteArrayOutputStream.close();
                    }
                }
            }
            finally {
                zin.close();
            }
        }
        return unzippedEntries;
    }

    /**
     * checks if a file is a zip archive
     *
     * @param file file to check
     * @return true if a zip archive otherwise false
     * @throws IOException
     */
    public static boolean checkIfFileIsZipped(File file) throws IOException {
        boolean isZipped = false;
        FileInputStream reader = new FileInputStream(file);
        byte[] magicByteArray = new byte[4];
        reader.read(magicByteArray);
        if (magicZipHexString.equals(new String(magicByteArray))) {
            isZipped = true;
        }
        return isZipped;
    }

    /**
     * checks if a byte array is a zip archive
     *
     * @param byteArray the byte array to check
     * @return true if a zip archive, otherwise false
     */
    public static boolean checkIfByteArrayIsZipped(byte[] byteArray) {
        return magicZipHexString.equals(new String(Arrays.copyOfRange(byteArray, 0, 3)));
    }

    /**
     * writes the extracted data to the temp dir
     * @param rovFile the Distiller file to write the data for
     * @return true if succeeded
     * @throws IOException
     */
    public static boolean writeExtractedDataToDisk(RovFile rovFile) throws IOException {
        return writeExtractedDataToDisk(rovFile, NATTERTEMPDIR);
    }
    
    /**
     * writes the extracted data to the specified folder
     * @param rovFile distiller file to write the data for
     * @param outputLocation the location to write the extracted data to
     * @return true if succeeded
     */
    public static boolean writeExtractedDataToDisk(RovFile rovFile,File outputLocation) throws IOException{
        RovFileData rovFileData = rovFile.getParsedData();
        writeHeaderToDisk(rovFileData.getHeader());
        String outputString = OutputFormatter.formatForCSV(rovFileData);
        writeOutputToDisk(outputString);
        return true;
    
    }

    /**
     * writes a list of Distiller files to the temp dir
     * @param rovFiles list of Distiller files to write the data from
     * @return true if succeeded
     * @throws IOException
     */
    public static boolean writeExtractedDataToDisk(List<RovFile> rovFiles) throws IOException {
        return writeExtractedDataToDisk(rovFiles, NATTERTEMPDIR);
    }

    public static boolean writeExtractedDataToDisk(List<RovFile> rovFiles, File outputLocationFolder) throws IOException {
        boolean success = false;
        Set<String> failedFiles = new HashSet<String>(rovFiles.size());
        for (RovFile rovFile : rovFiles) {
            try {
                if (!writeExtractedDataToDisk(rovFile)) {
                    failedFiles.add(rovFile.getName());
                }
            }
            catch (IOException ioe) {
                logger.error(ioe);
                failedFiles.add(rovFile.getName());
            }
        }
        if (failedFiles.isEmpty()) {
            success = true;
        } else {
            JOptionPane.showMessageDialog(null, "these files could not be processed: ");
        }
        return success;
    }

    /**
     * writes the header from Distiller files to disk
     * @param header the header from the extracted data from a Distiller file
     * @throws IOException
     */
    private static void writeHeaderToDisk(Header header) throws IOException {


        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * gets the (unzipped) file content from a specified file
     * @param file the file to get the contents from
     * @return a list containing all the entries in the archive, or a list with
     * a single entry if the file is not zipped
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static List<byte[]> getFileContentFromFileOnDisk(File file) throws FileNotFoundException, IOException {
        return inflateByteArray(fileContentToByteArray(file));
    }

    /**
     * reads a file and returns a byte array with the contents
     *
     * @param file file to get the contents from
     * @return a byte array
     * @throws IOException
     */
    public static byte[] fileContentToByteArray(File file) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        IOUtils.copy(new FileReader(file), byteArrayOutputStream, Charset.forName("UTF-8"));
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * inflates a byte array of an archive
     *
     * @param zippedFileContent the byte array to inflate
     * @return the inflated byte array
     * @throws IOException
     */
    private static List<byte[]> inflateByteArray(byte[] zippedFileContent) throws IOException {
        List<byte[]> inflatedByteArray = new ArrayList<byte[]>();
        if (checkIfByteArrayIsZipped(zippedFileContent)) {
            inflatedByteArray = unzipByteArray(zippedFileContent);
        } else if (checkIfByteArrayIsGZipped(zippedFileContent)) {
            inflatedByteArray = unGzipByteArray(zippedFileContent);
        } else {
            inflatedByteArray.add(zippedFileContent);
        }
        return inflatedByteArray;
    }

    private static boolean checkIfByteArrayIsGZipped(byte[] zippedFileContent) {
        return magicGzipHexString.equals(new String(Arrays.copyOfRange(zippedFileContent, 0, 1)));
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private static void writeOutputToDisk(String outputString) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
