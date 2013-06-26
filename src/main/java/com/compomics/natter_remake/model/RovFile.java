package com.compomics.natter_remake.model;

import java.io.File;

/**
 *
 * @author Davy
 */
public class RovFile extends File {
    
    private byte[] fileContent;
    private RovFileData data;
    /**
     * constructor for Distiller files in memory
     * @param fileName name of the distiller file
     * @param fileContent the content in a distiller file
     */
    public RovFile(String fileName, byte[] fileContent){
        super(fileName);
        this.fileContent = fileContent;
    }
    /**
     * constructor for Distiller files on disk
     * @param fileLocationOnDisk the location on disk
     */
    public RovFile (String fileLocationOnDisk){
        super(fileLocationOnDisk);
        //todo add file bytes to bytearray
    }
    
    public RovFile(File aFile){
        super(aFile.getAbsolutePath());
    }
    
    public byte[] getFileContent(){
     return fileContent;
    }

    public void addParsedData(RovFileData parsedRovFile) {
        this.data = parsedRovFile;
    }
    
    public RovFileData getParsedData(){
        return data;
    }
}
