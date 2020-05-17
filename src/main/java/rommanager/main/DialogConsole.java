/*
 * Copyright (C) 2012 phramusca ( https://github.com/phramusca/JaMuz/ )
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;

/**
 * A JDialog extension to display an album cover
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public class DialogConsole extends javax.swing.JDialog {

	private final ICallBackConsole callback;

	/** Creates new CoverDisplayGUI
	 * @param parent
	 * @param modal  
	 * @param callback  
	 * @param displayRefresh  
	 */
	public DialogConsole(java.awt.Frame parent, boolean modal, ICallBackConsole callback, boolean displayRefresh) {
		super(parent, modal);
		initComponents();
		Arrays.asList(Console.values()).
                forEach(console -> console.setSelected(false));
		DefaultListModel model = new DefaultListModel();
		for(Console console : Console.values()) {
			if(console.getNbFiles()>0) {
				model.addElement(console);
			}
		}
		jListConsoles.setModel(model);
		this.callback = callback;
		if(!displayRefresh) {
			jRadioButtonOnlyNew.setVisible(false);
			jRadioButtonRefreshSelected.setVisible(false);
			jButton1.setText("Export");
		}
	}
	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupListType = new javax.swing.ButtonGroup();
        jPanelOptionsGenres = new javax.swing.JPanel();
        jScrollPaneOptionsMachines1 = new javax.swing.JScrollPane();
        jListConsoles = new javax.swing.JList();
        jButton1 = new javax.swing.JButton();
        jRadioButtonRefreshSelected = new javax.swing.JRadioButton();
        jRadioButtonOnlyNew = new javax.swing.JRadioButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanelOptionsGenres.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "Consoles"));

        jListConsoles.setModel(new DefaultListModel());
        jScrollPaneOptionsMachines1.setViewportView(jListConsoles);

        jButton1.setText("List"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        buttonGroupListType.add(jRadioButtonRefreshSelected);
        jRadioButtonRefreshSelected.setText("Refresh"); // NOI18N

        buttonGroupListType.add(jRadioButtonOnlyNew);
        jRadioButtonOnlyNew.setSelected(true);
        jRadioButtonOnlyNew.setText("Only add new"); // NOI18N

        javax.swing.GroupLayout jPanelOptionsGenresLayout = new javax.swing.GroupLayout(jPanelOptionsGenres);
        jPanelOptionsGenres.setLayout(jPanelOptionsGenresLayout);
        jPanelOptionsGenresLayout.setHorizontalGroup(
            jPanelOptionsGenresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelOptionsGenresLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelOptionsGenresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPaneOptionsMachines1, javax.swing.GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelOptionsGenresLayout.createSequentialGroup()
                        .addGroup(jPanelOptionsGenresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jRadioButtonOnlyNew)
                            .addComponent(jRadioButtonRefreshSelected))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        jPanelOptionsGenresLayout.setVerticalGroup(
            jPanelOptionsGenresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelOptionsGenresLayout.createSequentialGroup()
                .addComponent(jScrollPaneOptionsMachines1, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelOptionsGenresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanelOptionsGenresLayout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addGap(13, 13, 13))
                    .addGroup(jPanelOptionsGenresLayout.createSequentialGroup()
                        .addComponent(jRadioButtonOnlyNew)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jRadioButtonRefreshSelected))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelOptionsGenres, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelOptionsGenres, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

		boolean refresh = getSelectedButtonText(buttonGroupListType).equals("Refresh");
		
		List<Console> selectedConsoles = (List<Console>)jListConsoles.getSelectedValuesList();
		for(Console console : selectedConsoles) {
			console.setSelected(true);
		}
		dispose();
		callback.completed(refresh);
    }//GEN-LAST:event_jButton1ActionPerformed
	
	private String getSelectedButtonText(ButtonGroup buttonGroup) {
        for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();

            if (button.isSelected()) {
                return button.getText();
            }
        }
        return null;
    }
	
	/**
	 * 
	 * @param callback
	 * @param displayRefresh
	 */
	public static void main(ICallBackConsole callback, boolean displayRefresh) {
		/* Set the Nimbus look and feel */
		//<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
		 * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
		 */
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {  //NOI18N
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(DialogConsole.class.getName())
					.log(java.util.logging.Level.SEVERE, null, ex);
		}
		//</editor-fold>

		/* Create and display the form */
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				DialogConsole dialog = new DialogConsole(new javax.swing.JFrame(), true, callback, displayRefresh );
				//Center the dialog
				dialog.setLocationRelativeTo(dialog.getParent());
				dialog.setVisible(true);
			}
		});
	}
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupListType;
    private javax.swing.JButton jButton1;
    private static javax.swing.JList jListConsoles;
    private javax.swing.JPanel jPanelOptionsGenres;
    private javax.swing.JRadioButton jRadioButtonOnlyNew;
    private javax.swing.JRadioButton jRadioButtonRefreshSelected;
    private javax.swing.JScrollPane jScrollPaneOptionsMachines1;
    // End of variables declaration//GEN-END:variables
}
