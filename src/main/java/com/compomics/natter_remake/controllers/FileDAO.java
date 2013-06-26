package com.compomics.natter_remake.controllers;

import com.compomics.natter_remake.model.Header;
import com.compomics.natter_remake.model.PeptideGroup;
import com.compomics.natter_remake.model.RovFile;
import com.compomics.natter_remake.model.RovFileData;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPInputStream;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Davy
 */
public class FileDAO {

    public static final File NATTERTEMPDIR =  new File(System.getProperty("java.io.tmpdir")+"/natter_rov_files");
    /**
     * writes the given byte array to the specified location
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
        } finally {
            if (deleteOnExit) {
                outputFile.deleteOnExit();
            }
            out.close();
        }
        return outputFile;
    }

    static File unzipAndWriteByteArrayToDisk(byte[] zippedFileContent, File fileOutputLocation) throws IOException {
        return writeByteArrayToDisk(unzipByteArray(zippedFileContent), null, fileOutputLocation, true);
    }

    static File unzipAndWriteByteArrayToDisk(byte[] zippedFileContent, String filename, File fileOutputLocation) throws IOException {
        return writeByteArrayToDisk(unzipByteArray(zippedFileContent), filename, fileOutputLocation, true);
    }

    static File unzipAndWriteByteArrayToDisk(byte[] zippedFileContent, String filename, File fileOutputLocation, boolean deleteOnExit) throws IOException {
        return writeByteArrayToDisk(unzipByteArray(zippedFileContent), filename, fileOutputLocation, deleteOnExit);
    }

    static byte[] unzipByteArray(byte[] fileContent) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        
        //this method uses a buffer internally
        IOUtils.copy(new GZIPInputStream(new ByteArrayInputStream(fileContent)), byteArrayOutputStream);
        
        return byteArrayOutputStream.toByteArray();
   
    }

    public static boolean writeExtractedDataToDisk(RovFile rovFile) throws IOException {
        RovFileData rovFileData = rovFile.getParsedData();
        writeHeaderToDisk(rovFileData.getHeader());
        for (PeptideGroup aPeptideGroup : rovFileData.getPeptideGroups()){
        }
        return true;
    }

    public static boolean writeExtractedDataToDisk(List<RovFile> rovFiles) throws IOException {
        for (RovFile rovFile : rovFiles) {
            if (!writeExtractedDataToDisk(rovFile)){
                //TODO perhaps offer new write to disk in separate frame for failed writes, even later perhaps through viewer
                throw new IOException("could not write data extracted from "+rovFile.getName()+" to disk");
            }
        }
        return true;
    }

    private static void writeHeaderToDisk(Header header) throws IOException {

        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
