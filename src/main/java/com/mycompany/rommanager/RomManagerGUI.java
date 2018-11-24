/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.rommanager;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public class RomManagerGUI extends javax.swing.JFrame {

    private final ProgressBar progressBar;
    
    TableModelRomSevenZip tableModel;
    
    /**
     * Creates new form RomManagerGUI
     */
    public RomManagerGUI() {
        initComponents();
        
        jTextFieldName.setText(RomManager.options.get("romset.name"));
        jTextFieldPath.setText(RomManager.options.get("romset.path"));
        
        progressBar = (ProgressBar)jProgressBar1;
        
        tableModel = (TableModelRomSevenZip) jTableRom.getModel();
        jTableRom.setRowSorter(null);
		//Adding columns from tableModel. Cannot be done automatically on properties
		// as done, in initComponents, before setColumnModel which removes the columns ...
		jTableRom.createDefaultColumnsFromModel();

		setColumn(0, 100, 600);
        setColumn(1, 100, 400);
    }

    private void setColumn(int index, int min, int pref) {
        TableColumn column = jTableRom.getColumnModel().getColumn(index);
		column.setMinWidth(min);
        column.setPreferredWidth(pref);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
//    @SuppressWarnings("unchecked");
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButtonOptionSelectFolder = new javax.swing.JButton();
        jTextFieldPath = new javax.swing.JTextField();
        jTextFieldName = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jButtonExtract = new javax.swing.JButton();
        jButtonList = new javax.swing.JButton();
        jProgressBar1 = new ProgressBar();
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPaneCheckTags1 = new javax.swing.JScrollPane();
        jTableRom = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListVersions = new javax.swing.JList<>();
        jLabelAction = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Rom Manager");

        jButtonOptionSelectFolder.setText("Select"); // NOI18N
        jButtonOptionSelectFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOptionSelectFolderActionPerformed(evt);
            }
        });

        jTextFieldPath.setEditable(false);
        jTextFieldPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldPathActionPerformed(evt);
            }
        });

        jLabel1.setText("Name: ");

        jButtonExtract.setText("Extract");
        jButtonExtract.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExtractActionPerformed(evt);
            }
        });

        jButtonList.setText("List");
        jButtonList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonListActionPerformed(evt);
            }
        });

        jProgressBar1.setString(""); // NOI18N
        jProgressBar1.setStringPainted(true);

        jSplitPane1.setDividerLocation(800);

        jTableRom.setAutoCreateColumnsFromModel(false);
        jTableRom.setModel(new TableModelRomSevenZip());
        jTableRom.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        jTableRom.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTableRomMousePressed(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableRomMouseClicked(evt);
            }
        });
        jScrollPaneCheckTags1.setViewportView(jTableRom);

        jSplitPane1.setLeftComponent(jScrollPaneCheckTags1);

        jScrollPane1.setViewportView(jListVersions);

        jSplitPane1.setRightComponent(jScrollPane1);

        jLabelAction.setText("Action: ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1358, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonExtract)
                        .addGap(18, 18, 18)
                        .addComponent(jLabelAction)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonList))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldName))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTextFieldPath)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonOptionSelectFolder)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonOptionSelectFolder))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextFieldName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonList)
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelAction)
                    .addComponent(jButtonExtract))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 608, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonOptionSelectFolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOptionSelectFolderActionPerformed
        String selectedFolder=selectFolder(jTextFieldPath.getText());
        if(!selectedFolder.equals("")) {  //NOI18N
            jTextFieldPath.setText(selectedFolder);
			jTextFieldName.setText(FilenameUtils.getBaseName(selectedFolder));
        }
    }//GEN-LAST:event_jButtonOptionSelectFolderActionPerformed

    private void jButtonExtractActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExtractActionPerformed
        RomDevice romDevice = getRomDevice();
        if(romDevice!=null) {
            int n = JOptionPane.showConfirmDialog(
                this, "Extract ?\n\nName: \""+romDevice.getName()+"\" ?\nPath: "+romDevice.getPath(),  //NOI18N
                "Please Confirm",  //NOI18N
                JOptionPane.YES_NO_OPTION);
            if (n == JOptionPane.YES_OPTION) {
				disableGUI("Extraction: ");
                romDevice.extract();
				
            } 
            
        }   
    }//GEN-LAST:event_jButtonExtractActionPerformed

	private void disableGUI(String text) {
		jLabelAction.setText(text);
		enableGUI(false);
	}
	
	public static void enableGUI() {
		enableGUI(true);
	}
	
	private static void enableGUI(boolean enable) {
		jButtonExtract.setEnabled(enable);
		jButtonList.setEnabled(enable);
		jButtonOptionSelectFolder.setEnabled(enable);
	}
	
    private void jButtonListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonListActionPerformed
		final RomDevice romDevice = getRomDevice();
        if(romDevice!=null) {
            Thread t = new Thread("Thread.RomDevice.List") {
                @Override
                public void run() {
					disableGUI("Listing: ");
			        romDevice.list(tableModel);
					
					//Enable filter
					if(tableModel.getRowCount()>0) {
						jTableRom.setAutoCreateRowSorter(true);
						TableRowSorter<TableModelRomSevenZip> tableSorter = new TableRowSorter<>(tableModel);
						jTableRom.setRowSorter(tableSorter);
						List <RowSorter.SortKey> sortKeys = new ArrayList<>();

						//   "FileName", "Versions"
						sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
						tableSorter.setSortKeys(sortKeys);
					}
					else {
						jTableRom.setAutoCreateRowSorter(false);
					}
					Popup.info("Listing complete.");
					enableGUI();
                }
            };
            t.start();
        }
    }//GEN-LAST:event_jButtonListActionPerformed

    private void jTableRomMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableRomMousePressed
        if ( SwingUtilities.isRightMouseButton( evt ) )
        {
            Point p = evt.getPoint();
            int rowNumber = jTableRom.rowAtPoint( p );
            ListSelectionModel model = jTableRom.getSelectionModel();
            model.setSelectionInterval( rowNumber, rowNumber );
        }
    }//GEN-LAST:event_jTableRomMousePressed

    private void jTableRomMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableRomMouseClicked
        int selectedRow = jTableRom.getSelectedRow(); 		
		if(selectedRow>=0) { 			
			selectedRow = jTableRom.convertRowIndexToModel(selectedRow);
			RomSevenZipFile romSevenZipFile = tableModel.getFiles().get(selectedRow);
			DefaultListModel versionsModel = new DefaultListModel();
			for(RomVersion romVersion : romSevenZipFile.getVersions()) {
				versionsModel.addElement(romVersion);
			}
			jListVersions.setModel(versionsModel);
		}
    }//GEN-LAST:event_jTableRomMouseClicked

    private void jTextFieldPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldPathActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldPathActionPerformed

    private RomDevice getRomDevice() {

        String name = jTextFieldName.getText();
        String path = jTextFieldPath.getText();
        
        File file = new File(path);
        if(!file.exists()) {
            Popup.warning("Path does not exist.");
            return null;
        }
        
        if(name.trim().equals("")) {
            Popup.warning("Name cannot be empty.");
            return null;
        }
        
        RomManager.options.set("romset.name", name);
        RomManager.options.set("romset.path", path);
        RomManager.options.save();
        
        return new RomDevice(name, path, progressBar, tableModel);
    }
    
    public static String selectFolder(String defaultFolder) {
		JFileChooser fc = new JFileChooser(defaultFolder);
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
            File selFile = fc.getSelectedFile();
			return selFile.getAbsolutePath();
        } else {
			return "";  //NOI18N
        }
	}
    
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
        } catch (ClassNotFoundException | InstantiationException | 
				IllegalAccessException | 
				javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RomManagerGUI.class.getName())
					.log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
				RomManagerGUI panel = new RomManagerGUI();
				panel.setLocationRelativeTo(null);
//				panel.setExtendedState(RomManagerGUI.MAXIMIZED_BOTH);
				panel.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private static javax.swing.JButton jButtonExtract;
    private static javax.swing.JButton jButtonList;
    private static javax.swing.JButton jButtonOptionSelectFolder;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelAction;
    private javax.swing.JList<String> jListVersions;
    private static javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private static javax.swing.JScrollPane jScrollPaneCheckTags1;
    private javax.swing.JSplitPane jSplitPane1;
    private static javax.swing.JTable jTableRom;
    private javax.swing.JTextField jTextFieldName;
    private static javax.swing.JTextField jTextFieldPath;
    // End of variables declaration//GEN-END:variables
}
