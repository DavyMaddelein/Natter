package com.compomics.natter_remake.model;

/**
 *
 * @author Davy
 */
public class RawFile {

    private String rawFileName = "not found";
    
    public void setRawFileName(String fileName) {
        this.rawFileName = fileName;
    }
    
    public String getRawFilename(){
        return rawFileName;
    }
    
}
