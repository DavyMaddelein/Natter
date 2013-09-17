package com.compomics.natter_remake.controllers;

import com.compomics.natter_remake.controllers.output.CSVOutputFormatterForDistillerFile;
import com.compomics.natter_remake.controllers.output.OutputFormatter;
import com.compomics.natter_remake.model.Header;
import com.compomics.natter_remake.model.RovFile;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
    public static final File NATTERTEMPDIR = new File(String.format("%s/natter_rov_files", System.getProperty("java.io.tmpdir")));
    /**
     * stuff to check if file is zipped
     */
    static final String magicZipHexString = "504b0304";
    static final String magicGzipHexString = "1f8b";
    final private static char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    final private static String ENCODING = "UTF-8";

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
     * @param fileContent the byte array to unzip
     * @return a {@code List} containing the files in the gzip archive
     * @throws IOException
     */
    public static List<byte[]> unGzipByteArray(byte[] byteArray) throws IOException {
        byte[] byteArrayToReturn;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        GZIPInputStream GZIPInputStream = new GZIPInputStream(new ByteArrayInputStream(byteArray));
        try {
            IOUtils.copy(GZIPInputStream, byteArrayOutputStream);
        }
        finally {
            GZIPInputStream.close();
        }
        try {
            byteArrayToReturn = byteArrayOutputStream.toByteArray();
        }
        finally {
            byteArrayOutputStream.close();
        }
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
        try {
            if (reader.read(magicByteArray) != 4) {
                throw new IOException("misread in magic bytes");
            }
        }
        finally {
            reader.close();
        }
        if (magicZipHexString.equals(new String(magicByteArray, ENCODING))) {
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
    public static boolean checkIfByteArrayIsZipped(byte[] byteArray) throws IOException {
        return magicZipHexString.equals(new String(Arrays.copyOfRange(byteArray, 0, 3), ENCODING));
    }

    /**
     * writes the extracted data to the temp dir in CVS form
     *
     * @param rovFile the Distiller file to write the data for
     * @return true if succeeded
     * @throws IOException
     */
    public static boolean writeExtractedDataToDisk(RovFile rovFile) throws IOException {
        return writeExtractedDataToDisk(rovFile, NATTERTEMPDIR, new CSVOutputFormatterForDistillerFile(";"));
    }
/**
 * writes the extracted data to the specified folder in CVS form
 * @param rovFile the distiller file to extract and write the data for
 * @param outputLocationFolder the folder to output to
 * @return true if succeeded otherwise false
 * @throws IOException 
 */
    public static boolean writeExtractedDataToDisk(RovFile rovFile, File outputLocationFolder) throws IOException {
        return writeExtractedDataToDisk(rovFile, outputLocationFolder, new CSVOutputFormatterForDistillerFile(";"));
    }

    /**
     * writes the extracted data to the specified folder
     *
     * @param rovFile distiller file to write the data for
     * @param outputLocation the location to write the extracted data to
     * @return true if succeeded
     */
    public static boolean writeExtractedDataToDisk(RovFile rovFile, File outputLocationFolder, OutputFormatter outputFormatter) throws IOException {
        //writeHeaderToDisk(rovFileData.getHeader(),outputLocationFolder);
        String outputString = outputFormatter.formatData(rovFile);
        File outputFile = new File(outputLocationFolder, String.format("%s.natter_output", rovFile.getName()));
        if (outputFile.exists()) {
            outputFile = new File(String.format("%s_%s.natter_output", outputFile.getAbsolutePath(), Calendar.getInstance().getTimeInMillis()));
        }
        OutputStreamWriter outputWriter = new OutputStreamWriter(new FileOutputStream(outputFile), Charset.forName(ENCODING).newEncoder());
        try {
            outputWriter.write(outputString);
        }
        finally {
            outputWriter.close();
        }
        return true;
    }

    /**
     * convenience method to get all {@code PeptideMatch}es in distiller file
     *
     * @param rovFile distiller file to process
     * @param outputLocationFolder folder to write in
     * @param outputFormatter the {@code OutputFormatter} to use when processing
     * the data
     * @return boolean if write to disk succeeded
     * @throws IOException
     */
    public static boolean writeExtractedPeptideMatchesToDisk(RovFile rovFile, File outputLocationFolder, OutputFormatter outputFormatter) throws IOException {
        //writeHeaderToDisk(rovFile.getParsedData().getHeader(),outputLocationFolder);
        String outputString = outputFormatter.formatPeptideMatches(rovFile);
        File outputFile = new File(outputLocationFolder, MessageFormat.format("{0}.natter_output", rovFile.getName()));
        if (outputFile.exists()) {
            outputFile = new File(String.format("%s_%s.natter_output", outputFile.getAbsolutePath(), Calendar.getInstance().getTimeInMillis()));
        }
        OutputStreamWriter outputWriter = new OutputStreamWriter(new FileOutputStream(outputFile), Charset.forName(ENCODING).newEncoder());
        try {
            outputWriter.write(outputString);
        }
        finally {
            outputWriter.close();
        }
        return true;
    }

    /**
     * writes a {@code List} of Distiller files to the temp dir in CVS form
     *
     * @param rovFiles list of Distiller files to write the data from
     * @return true if succeeded
     * @throws IOException
     */
    public static boolean writeExtractedDataToDisk(List<RovFile> rovFiles) throws IOException {
        return writeExtractedDataToDisk(rovFiles, NATTERTEMPDIR);
    }

    /**
     * writes the extracted data of a list of distiller files to disk using the
     * {@code CSVOutputFormatterForDistillerFile} as formatter
     *
     * @param rovFiles list of distiller files to process
     * @param outputLocationFolder the folder to write to
     * @return true if the write to disk succeeded, false if even a portion were
     * not able to write correctly
     * @throws IOException contains a list of the names of the distiller files
     * that failed
     */
    public static boolean writeExtractedDataToDisk(List<RovFile> rovFiles, File outputLocationFolder) throws IOException {
        return writeExtractedDataToDisk(rovFiles, outputLocationFolder, new CSVOutputFormatterForDistillerFile(";"));
    }

    /**
     *
     * @param rovFiles
     * @param outputLocationFolder
     * @param outputFormatter
     * @return
     * @throws IOException
     */
    public static boolean writeExtractedDataToDisk(List<RovFile> rovFiles, File outputLocationFolder, OutputFormatter outputFormatter) throws IOException {
        boolean success = false;
        //sometimes it happens that there are identical named files in the same project, clean this up
        Set<String> failedFiles = new HashSet<String>(rovFiles.size());
        if (!outputLocationFolder.exists()) {
            if (!outputLocationFolder.mkdirs()) {
                throw new IOException("could not make parentfolders");
            }
        }
        for (RovFile rovFile : rovFiles) {
            try {
                if (!writeExtractedDataToDisk(rovFile, outputLocationFolder, outputFormatter)) {
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
            throw new IOException(MessageFormat.format("these files could not be written to disk {0}", failedFiles.toString()));
        }
        return success;
    }

    /**
     * writes all the peptide matches in a list of distiller files to disk
     *
     * @param rovFiles a list of distiller files to write to disk
     * @param outputLocationFolder the location to write the files to
     * @param outputFormatter
     * @return true if succeeded false otherwise
     * @throws IOException
     */
    public static boolean writeExtractedPeptideMatchesToDisk(List<RovFile> rovFiles, File outputLocationFolder, OutputFormatter outputFormatter) throws IOException {
        boolean success = false;
        //sometimes it happens that there are identical named files in the same project, clean this up
        Set<String> failedFiles = new HashSet<String>(rovFiles.size());
        if (!outputLocationFolder.exists()) {
            if (!outputLocationFolder.mkdirs()) {
                throw new IOException("could not make parent folders");
            }
        }
        for (RovFile rovFile : rovFiles) {
            try {
                if (!writeExtractedPeptideMatchesToDisk(rovFile, outputLocationFolder, outputFormatter)) {
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
     *
     * @param header the header from the extracted data from a Distiller file
     * @throws IOException
     */
    private static void writeHeaderToDisk(Header header, File outputLocationFolder) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * gets the (unzipped) file content from a specified file
     *
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
     * @return a {@code byte Array}
     * @throws IOException
     */
    public static byte[] fileContentToByteArray(File file) throws IOException {
        byte[] toReturn;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        InputStreamReader fileReader = new InputStreamReader(new FileInputStream(file), Charset.forName(ENCODING).newDecoder());
        try {
            try {
                IOUtils.copy(fileReader, byteArrayOutputStream, Charset.forName(ENCODING));
            }
            finally {
                fileReader.close();
            }
        }
        finally {
            toReturn = byteArrayOutputStream.toByteArray();
        }
        byteArrayOutputStream.close();
        return toReturn;
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
        return magicGzipHexString.equals(new String(Arrays.copyOfRange(zippedFileContent, 0, 1), Charset.forName(ENCODING)));
    }

    /**
     * shift bytes to hex values
     *
     * @param bytes the byte array to change
     * @return the reprsentation of the byte array in hex characters
     */
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
}
