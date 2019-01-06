/* 
 * Copyright (C) 2018 phramusca ( https://github.com/phramusca/ )
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import rommanager.utils.ProcessAbstract;

/**
 *
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public class RomManagerGUI extends javax.swing.JFrame {

    private final ProgressBar progressBar;
    private static TableModelRomSevenZip tableModel;
    
	private ProcessList processList;
	private ProcessRead processRead;
	private ProcessExport processExport;
	
    /**
     * Creates new form RomManagerGUI
     */
    public RomManagerGUI() {
        initComponents();
        
		jTextFieldPathExport.setText(RomManager.options.get("romset.exportPath"));
		jTextFieldPathSource.setText(RomManager.options.get("romset.sourcePath"));
        
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
		
		disableGUI("Reading ods file: ");
		new ReadOds(new CallBackProcess()).start();
    }

	private class ReadOds extends ProcessAbstract {
		private final ICallBackProcess callBack;
		public ReadOds(ICallBackProcess callBack) {
			super("Thread.RomManagerGUI.ReadOds");
			this.callBack = callBack;
		}
		@Override
		public void run() {
			RomManagerOds.readFile(tableModel, progressBar);
			callBack.completed();
		}
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

        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListVersions = new javax.swing.JList<>();
        jScrollPaneCheckTags1 = new javax.swing.JScrollPane();
        jTableRom = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jTextFieldPathExport = new javax.swing.JTextField();
        jButtonOptionSelectFolderExport = new javax.swing.JButton();
        jTextFieldPathSource = new javax.swing.JTextField();
        jButtonOptionSelectFolderSource = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jProgressBar1 = new ProgressBar();
        jButtonReadGameList = new javax.swing.JButton();
        jButtonExport = new javax.swing.JButton();
        jButtonScanSource = new javax.swing.JButton();
        jLabelAction = new javax.swing.JLabel();
        jButtonAbort = new javax.swing.JButton();
        jButtonScore = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Rom Manager");

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

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Configuration"));

        jTextFieldPathExport.setEditable(false);

        jButtonOptionSelectFolderExport.setText("Select"); // NOI18N
        jButtonOptionSelectFolderExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOptionSelectFolderExportActionPerformed(evt);
            }
        });

        jTextFieldPathSource.setEditable(false);

        jButtonOptionSelectFolderSource.setText("Select"); // NOI18N
        jButtonOptionSelectFolderSource.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOptionSelectFolderSourceActionPerformed(evt);
            }
        });

        jLabel1.setText("Roms Source folder:");

        jLabel2.setText("Destination folder:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jTextFieldPathExport)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonOptionSelectFolderExport))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jTextFieldPathSource, javax.swing.GroupLayout.DEFAULT_SIZE, 705, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonOptionSelectFolderSource)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldPathSource, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonOptionSelectFolderSource)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldPathExport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonOptionSelectFolderExport)
                    .addComponent(jLabel2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Action"));

        jProgressBar1.setString(""); // NOI18N
        jProgressBar1.setStringPainted(true);

        jButtonReadGameList.setText("Read gamelist.xml");
        jButtonReadGameList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonReadGameListActionPerformed(evt);
            }
        });

        jButtonExport.setText("Export");
        jButtonExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExportActionPerformed(evt);
            }
        });

        jButtonScanSource.setText("Scan Source"); // NOI18N
        jButtonScanSource.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonScanSourceActionPerformed(evt);
            }
        });

        jLabelAction.setText("Action: ");

        jButtonAbort.setText("Abort");
        jButtonAbort.setEnabled(false);
        jButtonAbort.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAbortActionPerformed(evt);
            }
        });

        jButtonScore.setText("Set Score");
        jButtonScore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonScoreActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelAction)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jButtonScanSource)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonScore)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonExport)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonReadGameList)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonAbort))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonReadGameList)
                    .addComponent(jButtonExport)
                    .addComponent(jButtonScanSource)
                    .addComponent(jButtonScore))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelAction)
                    .addComponent(jButtonAbort))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonScanSourceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonScanSourceActionPerformed
		disableGUI("Listing: ");
		String sourcePath = jTextFieldPathSource.getText();
		File file = new File(sourcePath);
		if(!file.exists()) {
			Popup.warning("Source path does not exist.");
			enableGUI();
			return;
		}
		processList = new ProcessList(sourcePath, progressBar, tableModel, new CallBackProcess());
		processList.start();
    }//GEN-LAST:event_jButtonScanSourceActionPerformed

	private class CallBackProcess implements ICallBackProcess {

		@Override
		public void completed() {
			enableGUI();
		}
		
	}
	
	private static void enableFilter() {
		//Enable filter
		if(tableModel.getRowCount()>0) {
			jTableRom.setAutoCreateRowSorter(true);
//			TableRowSorter<TableModelRomSevenZip> tableSorter = new TableRowSorter<>(tableModel);
//			jTableRom.setRowSorter(tableSorter);
			
			//FIXME 2 Debug this enableFilter !
//			List <RowSorter.SortKey> sortKeys = new ArrayList<>();
//			//Order by console, name
//			sortKeys.add(new RowSorter.SortKey(3, SortOrder.ASCENDING));
//			sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
//			tableSorter.setSortKeys(sortKeys);
		}
		else {
			jTableRom.setAutoCreateRowSorter(false);
		}
	}
	
    private void jButtonExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExportActionPerformed
        int n = JOptionPane.showConfirmDialog(
			this, "Are you sure you want to export roms ?",  //NOI18N
			"Please Confirm",  //NOI18N
			JOptionPane.YES_NO_OPTION);
		if (n == JOptionPane.YES_OPTION) {
			disableGUI("Export: ");
			String exportPath = jTextFieldPathExport.getText();
			File folder = new File(exportPath);
			if(!folder.exists()) {
				enableGUI();
				Popup.warning("Export path does not exist.");
				return;
			}
			String sourcePath = jTextFieldPathSource.getText();
			folder = new File(sourcePath);
			if(!folder.exists()) {
				Popup.warning("Source path does not exist.");
				enableGUI();
				return;
			}
			processExport = new ProcessExport(
					sourcePath, 
					exportPath, 
					progressBar, 
					tableModel, 
					new CallBackProcess());
			processExport.start();
		}
    }//GEN-LAST:event_jButtonExportActionPerformed

	private void disableGUI(String text) {
		jLabelAction.setText(text);
		jTableRom.setAutoCreateRowSorter(false);
		jTableRom.setRowSorter(null);
		enableGUI(false);
	}
	
	public void enableGUI() {
		enableGUI(true);
		enableFilter();
	}
	
	private static void enableGUI(boolean enable) {
		jButtonScanSource.setEnabled(enable);
		jButtonScore.setEnabled(enable);
		jButtonExport.setEnabled(enable);
		jButtonReadGameList.setEnabled(enable);
		
		jButtonOptionSelectFolderExport.setEnabled(enable);
		jButtonOptionSelectFolderSource.setEnabled(enable);
		
		jButtonAbort.setEnabled(!enable);
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

    private void jButtonReadGameListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonReadGameListActionPerformed
		disableGUI("Reading gamelist.xml: ");
		String exportPath = jTextFieldPathExport.getText();
		File file = new File(exportPath);
		if(!file.exists()) {
			enableGUI();
			Popup.warning("Export path does not exist.");
			return;
		}
		processRead = new ProcessRead(exportPath, progressBar, tableModel, new CallBackProcess());
		processRead.start();
    }//GEN-LAST:event_jButtonReadGameListActionPerformed

    private void jButtonOptionSelectFolderExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOptionSelectFolderExportActionPerformed
        String selectedFolder=selectFolder(jTextFieldPathExport.getText());
        if(!selectedFolder.equals("")) {  //NOI18N
			RomManager.options.set("romset.exportPath", selectedFolder);
			RomManager.options.save();
            jTextFieldPathExport.setText(selectedFolder);
        }
    }//GEN-LAST:event_jButtonOptionSelectFolderExportActionPerformed

    private void jButtonOptionSelectFolderSourceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOptionSelectFolderSourceActionPerformed
		String selectedFolder=selectFolder(jTextFieldPathSource.getText());
        if(!selectedFolder.equals("")) {  //NOI18N
			RomManager.options.set("romset.sourcePath", selectedFolder);
			RomManager.options.save();
            jTextFieldPathSource.setText(selectedFolder);
        }
    }//GEN-LAST:event_jButtonOptionSelectFolderSourceActionPerformed

    private void jButtonAbortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAbortActionPerformed
        abort(processList);
		abort(processExport);
		abort(processRead);
    }//GEN-LAST:event_jButtonAbortActionPerformed

    private void jButtonScoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonScoreActionPerformed
		List<GoodCode> codes = GoodToolsConfigOds.getCodes();
    }//GEN-LAST:event_jButtonScoreActionPerformed
    
	private void abort(ProcessAbstract process) {
		if(process!=null && process.isAlive()) {
			process.abort();
		}
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
    private static javax.swing.JButton jButtonAbort;
    private static javax.swing.JButton jButtonExport;
    private static javax.swing.JButton jButtonOptionSelectFolderExport;
    private static javax.swing.JButton jButtonOptionSelectFolderSource;
    private static javax.swing.JButton jButtonReadGameList;
    private static javax.swing.JButton jButtonScanSource;
    private static javax.swing.JButton jButtonScore;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabelAction;
    private javax.swing.JList<String> jListVersions;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private static javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private static javax.swing.JScrollPane jScrollPaneCheckTags1;
    private javax.swing.JSplitPane jSplitPane1;
    private static javax.swing.JTable jTableRom;
    private static javax.swing.JTextField jTextFieldPathExport;
    private static javax.swing.JTextField jTextFieldPathSource;
    // End of variables declaration//GEN-END:variables
}
