/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.natter_remake.View;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;

/**
 *
 * @author Davy
 */
public class FileSelectionFrame extends javax.swing.JFrame {

    private static final Logger logger = Logger.getLogger(FileSelectionFrame.class);

    /**
     * Creates new form FileSelectionFrame
     */
    public FileSelectionFrame() throws ZipException, IOException {
        initComponents();
        new FileSelectionFrame().setVisible(true);


    }

    private void buttonpress() {
        try {
            ZipFile selectedFile = new ZipFile(new File(""));
            selectedFile.getEntry("bb8");
        } catch (ZipException ex) {
            logger.error(ex);
            //fail silently?
        } catch (IOException ex) {
            logger.error(ex);
            JOptionPane.showMessageDialog(this, "");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}