package com.compomics.natter_remake.model;

/**
 *
 * @author Davy
 */
public class Modification {

    private int modificationNumberInFile = -1;
    private String modification= "";
    
    public Modification(String modification){
        this.modification = parseModificationName(modification);
    }

    public Modification() {
       }
    
    public int getModificationNumberInFile() {
        return modificationNumberInFile;
    }

    public void setModificationNumberInFile(int modificationNumberInFile) {
        this.modificationNumberInFile = modificationNumberInFile;
    }

    public String getModification() {
        return modification;
    }

    public void setModification(String modification) {
        this.modification = modification;
    }
    
    private String parseModificationName(String aModification){
        String parsedModification = String.format("<%s>", aModification.split(" ")[0]);
        return parsedModification;
    }
}
