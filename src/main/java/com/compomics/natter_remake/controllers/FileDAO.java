package com.compomics.natter_remake.controllers;

import com.compomics.natter_remake.model.RovFile;
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
    
    static RovFile writeByteArrayToDisk(byte[] fileContent, String filename, File fileOutputLocation, boolean deleteOnExit) throws NullPointerException, IOException {
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
        SimpleDateFormat sdf = new SimpleDateFormat("MM_dd_yyyy_h_mm_ss");
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

    static RovFile unzipAndWriteByteArrayToDisk(byte[] zippedFileContent, File fileOutputLocation) throws IOException {
        return writeByteArrayToDisk(unzipByteArray(zippedFileContent), null, fileOutputLocation, true);
    }

    static RovFile unzipAndWriteByteArrayToDisk(byte[] zippedFileContent, String filename, File fileOutputLocation) throws IOException {
        return writeByteArrayToDisk(unzipByteArray(zippedFileContent), filename, fileOutputLocation, true);
    }

    static RovFile unzipAndWriteByteArrayToDisk(byte[] zippedFileContent, String filename, File fileOutputLocation, boolean deleteOnExit) throws IOException {
        return writeByteArrayToDisk(unzipByteArray(zippedFileContent), filename, fileOutputLocation, deleteOnExit);
    }

    static byte[] unzipByteArray(byte[] fileContent) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        
        //this method uses a buffer internally
        IOUtils.copy(new GZIPInputStream(new ByteArrayInputStream(fileContent)), byteArrayOutputStream);
        
        return byteArrayOutputStream.toByteArray();
   
    }

    public static void writeExtractedDataToDisk(RovFile data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static void writeExtractedDataToDisk(List<RovFile> rovFiles) {
        for (RovFile rovFile : rovFiles) {
            writeExtractedDataToDisk(rovFile);
        }
    }
}
