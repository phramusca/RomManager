/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rommanager.main;

import rommanager.utils.ProgressBar;
import rommanager.utils.Popup;
import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import org.apache.commons.io.FilenameUtils;
import rommanager.gamelist.ProcessList;

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
        
		jTextFieldPathExport.setText(RomManager.options.get("romset.exportPath"));
        
        progressBar = (ProgressBar)jProgressBar1;
        
		jTableRom.setRowHeight(IconBuffer.ICON_HEIGHT);
		
        tableModel = (TableModelRomSevenZip) jTableRom.getModel();
        jTableRom.setRowSorter(null);
		//Adding columns from tableModel. Cannot be done automatically on properties
		// as done, in initComponents, before setColumnModel which removes the columns ...
		jTableRom.createDefaultColumnsFromModel();

		setColumn(0, 220, 220);
        setColumn(1, 100, 300);
		setColumn(2, 100, 800);
		setColumn(3, 100, 200);
		setColumn(4, 100, 150);
		setColumn(5, 100, 50);
		setColumn(6, 100, 100);
		
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setVerticalAlignment(SwingConstants.TOP);
        jTableRom.getColumnModel().getColumn(2).setCellRenderer(renderer);
		
		RomManagerOds.readFile(tableModel, progressBar);
		enableFilter();
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

        jButtonOptionAddConsole = new javax.swing.JButton();
        jButtonExtract = new javax.swing.JButton();
        jProgressBar1 = new ProgressBar();
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListVersions = new javax.swing.JList<>();
        jScrollPaneCheckTags1 = new javax.swing.JScrollPane();
        jTableRom = new javax.swing.JTable();
        jLabelAction = new javax.swing.JLabel();
        jButtonRead = new javax.swing.JButton();
        jTextFieldPathExport = new javax.swing.JTextField();
        jButtonOptionSelectFolderExport = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Rom Manager");

        jButtonOptionAddConsole.setText("Add"); // NOI18N
        jButtonOptionAddConsole.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOptionAddConsoleActionPerformed(evt);
            }
        });

        jButtonExtract.setText("Extract");
        jButtonExtract.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExtractActionPerformed(evt);
            }
        });

        jProgressBar1.setString(""); // NOI18N
        jProgressBar1.setStringPainted(true);

        jSplitPane1.setDividerLocation(150);

        jScrollPane1.setViewportView(jListVersions);

        jSplitPane1.setLeftComponent(jScrollPane1);

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

        jSplitPane1.setRightComponent(jScrollPaneCheckTags1);

        jLabelAction.setText("Action: ");

        jButtonRead.setText("Read");
        jButtonRead.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonReadActionPerformed(evt);
            }
        });

        jTextFieldPathExport.setEditable(false);

        jButtonOptionSelectFolderExport.setText("Select"); // NOI18N
        jButtonOptionSelectFolderExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOptionSelectFolderExportActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSplitPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jTextFieldPathExport)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonOptionSelectFolderExport)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonRead))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabelAction)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 1196, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonOptionAddConsole)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonExtract)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelAction)
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonExtract)
                    .addComponent(jButtonOptionAddConsole))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldPathExport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonOptionSelectFolderExport)
                    .addComponent(jButtonRead))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 536, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonOptionAddConsoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOptionAddConsoleActionPerformed
        
		String selectedFolder=selectFolder(RomManager.options.get("romset.path"));
        if(!selectedFolder.equals("")) {  //NOI18N
			String consolePath="";
			try {
				consolePath=FilenameUtils.getBaseName(selectedFolder);
				Console console = Console.valueOf(consolePath);
				RomManager.options.set("romset.path", selectedFolder);
				RomManager.options.save();

				final RomDevice romDevice = getRomDevice(console);
				if(romDevice!=null) {
					Thread t = new Thread("Thread.RomDevice.List") {
						@Override
						public void run() {
							disableGUI("Listing: ");
							jTableRom.setAutoCreateRowSorter(false);
							jTableRom.setRowSorter(null);
							romDevice.list(tableModel, progressBar);
							progressBar.setIndeterminate("Saving ods file");
							RomManagerOds.createFile(tableModel, progressBar);
							progressBar.reset();
							enableFilter();
							Popup.info("Listing complete.");
							enableGUI();
						}
					};
					t.start();
				}
			} catch (IllegalArgumentException ex) {
				Popup.warning("Unknown console: "+consolePath);
			}
        }
    }//GEN-LAST:event_jButtonOptionAddConsoleActionPerformed

	private void enableFilter() {
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
	}
	
    private void jButtonExtractActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExtractActionPerformed
//        RomDevice romDevice = getRomDevice("");
//        if(romDevice!=null) {
//            int n = JOptionPane.showConfirmDialog(
//                this, "Extract ?\n\nName: \""+romDevice.getName()+"\" ?\nPath: "+romDevice.getPath(),  //NOI18N
//                "Please Confirm",  //NOI18N
//                JOptionPane.YES_NO_OPTION);
//            if (n == JOptionPane.YES_OPTION) {
//				disableGUI("Extraction: ");
				Popup.warning("Disabled for now!");enableGUI();
//                romDevice.extract();
//            } 
//        }   
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
		jButtonRead.setEnabled(enable);
		jButtonOptionAddConsole.setEnabled(enable);
		jButtonOptionSelectFolderExport.setEnabled(enable);
	}
	
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
			
			RomSevenZipFile romSevenZipFile = tableModel.getRom(selectedRow);
			DefaultListModel versionsModel = new DefaultListModel();
			int i=0;
			List<Integer> indices=new ArrayList();
			for(RomVersion romVersion : romSevenZipFile.getVersions()) {
				versionsModel.addElement(romVersion);
				if(romVersion.isBest()) {
					indices.add(i);
				}
				i++;
			}
			jListVersions.setModel(versionsModel);
			
			int[] indicesArray = new int[indices.size()];
			for(i = 0; i < indices.size(); i++) { indicesArray[i] = indices.get(i); }
			
			jListVersions.setSelectedIndices(indicesArray);
		}
    }//GEN-LAST:event_jTableRomMouseClicked

    private void jButtonReadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonReadActionPerformed
		disableGUI("Reading gamelist.xml: ");
		String exportPath = jTextFieldPathExport.getText();
		File file = new File(exportPath);
		if(!file.exists()) {
			Popup.warning("Export path does not exist.");
			return;
		}
		tableModel.setRootPath(exportPath);
		ProcessList processList = new ProcessList(exportPath, progressBar, tableModel);
		processList.start();
    }//GEN-LAST:event_jButtonReadActionPerformed

    private void jButtonOptionSelectFolderExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOptionSelectFolderExportActionPerformed
        String selectedFolder=selectFolder(jTextFieldPathExport.getText());
        if(!selectedFolder.equals("")) {  //NOI18N
			RomManager.options.set("romset.exportPath", selectedFolder);
			RomManager.options.save();
            jTextFieldPathExport.setText(selectedFolder);
        }
    }//GEN-LAST:event_jButtonOptionSelectFolderExportActionPerformed

    private RomDevice getRomDevice(Console console) {

        String path = RomManager.options.get("romset.path");
        
        File file = new File(path);
        if(!file.exists()) {
            Popup.warning("RomSet path does not exist.");
            return null;
        }
        
        if(console.toString().trim().equals("CHANGEME")) {
            Popup.warning("Unsupported console type :(");
            return null;
        }
		
        return new RomDevice(console, path);
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

        java.awt.EventQueue.invokeLater(() -> {
			RomManagerGUI panel = new RomManagerGUI();
			panel.setLocationRelativeTo(null);
			panel.setExtendedState(RomManagerGUI.MAXIMIZED_BOTH);
			panel.setVisible(true);
		});
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private static javax.swing.JButton jButtonExtract;
    private static javax.swing.JButton jButtonOptionAddConsole;
    private static javax.swing.JButton jButtonOptionSelectFolderExport;
    private static javax.swing.JButton jButtonRead;
    private javax.swing.JLabel jLabelAction;
    private javax.swing.JList<String> jListVersions;
    private static javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private static javax.swing.JScrollPane jScrollPaneCheckTags1;
    private javax.swing.JSplitPane jSplitPane1;
    private static javax.swing.JTable jTableRom;
    private static javax.swing.JTextField jTextFieldPathExport;
    // End of variables declaration//GEN-END:variables
}
