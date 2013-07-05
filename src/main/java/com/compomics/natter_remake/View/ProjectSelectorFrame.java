package com.compomics.natter_remake.View;

import com.compomics.natter_remake.controllers.DataExtractor;
import com.compomics.natter_remake.controllers.DbDAO;
import com.compomics.natter_remake.controllers.FileDAO;
import com.compomics.natter_remake.model.Project;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import javax.swing.JFileChooser;
import javax.swing.ListSelectionModel;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.apache.log4j.Logger;

/**
 *
 * @author Davy
 */
public class ProjectSelectorFrame extends javax.swing.JFrame {

    private final static Logger logger = Logger.getLogger(ProjectSelectorFrame.class);
    private File saveLocation;

    /**
     * Creates new form ProjectSelectorFrame
     */
    public ProjectSelectorFrame() throws SQLException {
        initComponents();
        projectList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        this.setVisible(true);
        fillProjectList();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        runButton = new javax.swing.JButton();
        jProgressBar1 = new javax.swing.JProgressBar();
        jScrollPane1 = new javax.swing.JScrollPane();
        projectList = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        saveFolderLocationTextField = new javax.swing.JTextField();
        saveLocationSelectionButton = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        jRadioButtonMenuItem1 = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItem2 = new javax.swing.JRadioButtonMenuItem();
        outputMenu = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        runButton.setText("Extract");
        runButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runButtonActionPerformed(evt);
            }
        });

        jScrollPane1.setViewportView(projectList);

        jLabel1.setText("folder to save output");

        saveFolderLocationTextField.setEditable(false);

        saveLocationSelectionButton.setText("set save location ...");
        saveLocationSelectionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveLocationSelectionButtonActionPerformed(evt);
            }
        });

        jMenu1.setText("Advanced");

        jMenu3.setText("extraction method ...");

        jRadioButtonMenuItem1.setSelected(true);
        jRadioButtonMenuItem1.setText("all Distiller files in mem");
        jMenu3.add(jRadioButtonMenuItem1);

        jRadioButtonMenuItem2.setSelected(true);
        jRadioButtonMenuItem2.setText("one Distiller file by one");
        jMenu3.add(jRadioButtonMenuItem2);

        jMenu1.add(jMenu3);

        jMenuBar1.add(jMenu1);

        outputMenu.setText("select output...");
        outputMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outputMenuActionPerformed(evt);
            }
        });
        jMenuBar1.add(outputMenu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(runButton))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(saveFolderLocationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 86, Short.MAX_VALUE)
                                .addComponent(saveLocationSelectionButton)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(57, 57, 57)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(saveFolderLocationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(saveLocationSelectionButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(runButton)
                .addGap(18, 18, 18)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void saveLocationSelectionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveLocationSelectionButtonActionPerformed
        JFileChooser saveLocationChooser = new JFileChooser();
        saveLocationChooser.setMultiSelectionEnabled(false);
        saveLocationChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        saveLocationChooser.showOpenDialog(this);
        saveLocation = saveLocationChooser.getSelectedFile();
        saveFolderLocationTextField.setText(saveLocation.getAbsolutePath());
    }//GEN-LAST:event_saveLocationSelectionButtonActionPerformed

    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runButtonActionPerformed
        try {
            for (Object project : projectList.getSelectedValues()) {
                FileDAO.writeExtractedDataToDisk(DataExtractor.extractDataInMem((Project) project));
            }
        }
        catch (SQLException ex) {
            logger.error(ex);
        }
        catch (ParserConfigurationException ex) {
            logger.error(ex);
        }
        catch (IOException ex) {
            logger.error(ex);
        }
        catch (XMLStreamException ex) {
            logger.error(ex);
        }
    }//GEN-LAST:event_runButtonActionPerformed

    private void outputMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_outputMenuActionPerformed
        new OutputSelectionFrame();
        
        
        
        
    }//GEN-LAST:event_outputMenuActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }
        catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ProjectSelectorFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ProjectSelectorFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ProjectSelectorFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ProjectSelectorFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new ProjectSelectorFrame().setVisible(true);
                }
                catch (SQLException ex) {
                    logger.error(ex);
                }
            }
        });
    }

    private void fillProjectList() throws SQLException {
        projectList.setListData(DbDAO.getAllProjects().toArray());
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem1;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JMenu outputMenu;
    private javax.swing.JList projectList;
    private javax.swing.JButton runButton;
    private javax.swing.JTextField saveFolderLocationTextField;
    private javax.swing.JButton saveLocationSelectionButton;
    // End of variables declaration//GEN-END:variables
}
