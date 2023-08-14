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

import java.awt.Font;
import rommanager.utils.ProgressBar;
import rommanager.utils.Popup;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import org.apache.commons.io.FilenameUtils;
import rommanager.main.TableFilter.ExportFilesNumber;
import rommanager.utils.Desktop;
import rommanager.utils.ProcessAbstract;
import rommanager.utils.TriStateCheckBox;

/**
 *
 * @author phramusca ( https://github.com/phramusca/JaMuz/ )
 */
public class RomManagerGUI extends javax.swing.JFrame {

    private final ProgressBar progressBarGame;
    private final ProgressBar progressBarConsole;
    private static TableModelRom tableModel;
    
	private ProcessList processList;
	private ProcessRead processRead;
    private ProcessSend processSend;
	private ProcessExport processExport;
	private ProcessSetScore processSetScore;
	
    /**
     * Creates new form RomManagerGUI
     */
    public RomManagerGUI() {
        initComponents();
        
		jTextFieldPathExport.setText(RomManager.options.get("romset.exportPath"));
		jTextFieldPathSource.setText(RomManager.options.get("romset.sourcePath"));
        
        progressBarGame = (ProgressBar)jProgressBarGame;
        progressBarConsole = (ProgressBar)jProgressBarConsole;
        
		jTableRom.setRowHeight(BufferIcon.ICON_HEIGHT);
		
        tableModel = (TableModelRom) jTableRom.getModel();
        jTableRom.setRowSorter(null);
		//Adding columns from tableModel. Cannot be done automatically on properties
		// as done, in initComponents, before setColumnModel which removes the columns ...
		jTableRom.createDefaultColumnsFromModel();

		setColumn(0, BufferIcon.ICON_WIDTH, BufferIcon.ICON_WIDTH); //Screenshot
        setColumn(1, 100, 300); //Name
		setColumn(2, 100, 800); //Description
		setColumn(3, 100, 200); //Console
		setColumn(4, 100, 150); //Genre
        setColumn(5, 60, 60);   //Players
        setColumn(6, 80, 80);   //Release Date
        setColumn(7, 50, 50);   //Rating
		setColumn(8, 100, 200); //Export selection
		
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setVerticalAlignment(SwingConstants.TOP);
        jTableRom.getColumnModel().getColumn(2).setCellRenderer(renderer);
		
        JTableHeader header = jTableRom.getTableHeader();
        header.addMouseListener(new TableHeaderMouseListener(jTableRom));

        triStateCheckBoxHidden.setState(TriStateCheckBox.State.UNSELECTED);
        triStateCheckBoxAdult.setState(TriStateCheckBox.State.UNSELECTED);
        
        jTextFieldSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
              filter();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
              filter();
            }
            @Override
            public void insertUpdate(DocumentEvent e) {
              filter();
            }

            public void filter() {
                tableModel.tableFilter.search(jTextFieldSearch.getText());
                tableModel.filter();
            }
          });
        
		disableGUI("Reading ods file: ");
		new ReadOds(() -> {
            enableGuiAndFilter();
        }, jTextFieldPathSource.getText()).start();
    }

    class TableHeaderMouseListener extends MouseAdapter {
        private JTable table;

        public TableHeaderMouseListener(JTable table) {
            this.table = table;
        }

        public void mouseClicked(MouseEvent event) {
            Point point = event.getPoint();
            int column = table.columnAtPoint(point);
            switch(column) {
                case 3: jRadioButtonConsole.doClick(); break;
                case 4: jRadioButtonGenre.doClick(); break;
                case 5: jRadioButtonPlayers.doClick(); break;
                case 6: jRadioButtonDecade.doClick(); break;
                case 7: jRadioButtonRating.doClick(); break;
            }
        }
    }
    
	/**
	 *
	 * @param list
	 * @return
	 */
	public static DefaultListModel getModel(List<String> list) {
        return getModel(list, true);
    }
	
	/**
	 *
	 * @param list
	 * @param sort
	 * @return
	 */
	public static DefaultListModel getModel(List<String> list, boolean sort) {
        DefaultListModel model = new DefaultListModel();
        if(sort) { Collections.sort(list); }
        model.addElement("All");
        list.forEach(element -> {
            model.addElement(element);
        });
        return model;
    }
	
	private class ReadOds extends ProcessAbstract {
		private final ICallBackProcess callBack;
		private final String sourceFolder;
		public ReadOds(ICallBackProcess callBack, String sourceFolder) {
			super("Thread.RomManagerGUI.ReadOds");
			this.callBack = callBack;
			this.sourceFolder = sourceFolder;
		}
		@Override
		public void run() {
			RomManagerOds.readFile(tableModel, progressBarGame, sourceFolder);
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

        buttonGroupSorting = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jTextFieldPathExport = new javax.swing.JTextField();
        jButtonOptionSelectFolderExport = new javax.swing.JButton();
        jTextFieldPathSource = new javax.swing.JTextField();
        jButtonOptionSelectFolderSource = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jProgressBarGame = new ProgressBar();
        jButtonScanSource = new javax.swing.JButton();
        jButtonScore = new javax.swing.JButton();
        jButtonExport = new javax.swing.JButton();
        jButtonReadGameList = new javax.swing.JButton();
        jButtonAbort = new javax.swing.JButton();
        jLabelAction = new javax.swing.JLabel();
        jButtonSave = new javax.swing.JButton();
        jButtonReadJeuxVideo = new javax.swing.JButton();
        jProgressBarConsole = new ProgressBar();
        jButtonSendGamelist = new javax.swing.JButton();
        jSplitPaneFilters = new javax.swing.JSplitPane();
        jPanel3 = new javax.swing.JPanel();
        jScrollPaneSelectGenre4 = new javax.swing.JScrollPane();
        jListFilterRating = new javax.swing.JList();
        jScrollPaneSelectGenre5 = new javax.swing.JScrollPane();
        jListFilterNumberFilesExport = new javax.swing.JList();
        jScrollPaneSelectGenre6 = new javax.swing.JScrollPane();
        jListFilterPlayers = new javax.swing.JList();
        jScrollPaneSelectGenre7 = new javax.swing.JScrollPane();
        jListFilterPlayCount = new javax.swing.JList();
        jScrollPaneSelectGenre1 = new javax.swing.JScrollPane();
        jListFilterConsole = new javax.swing.JList();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPaneSelectGenre2 = new javax.swing.JScrollPane();
        jListFilterGenre = new javax.swing.JList();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        triStateCheckBoxFavorite = new rommanager.utils.TriStateCheckBox();
        triStateCheckBoxHidden = new rommanager.utils.TriStateCheckBox();
        triStateCheckBoxAdult = new rommanager.utils.TriStateCheckBox();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jRadioButtonConsole = new javax.swing.JRadioButton();
        jRadioButtonRating = new javax.swing.JRadioButton();
        jRadioButtonPlayers = new javax.swing.JRadioButton();
        jRadioButtonGenre = new javax.swing.JRadioButton();
        jRadioButtonDecade = new javax.swing.JRadioButton();
        jLabel9 = new javax.swing.JLabel();
        jScrollPaneSelectGenre8 = new javax.swing.JScrollPane();
        jListFilterDecade = new javax.swing.JList();
        jTextFieldSearch = new javax.swing.JTextField();
        jSplitPaneList = new javax.swing.JSplitPane();
        jScrollPaneCheckTags1 = new javax.swing.JScrollPane();
        jTableRom = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jButtonAuto = new javax.swing.JButton();
        jButtonVideo = new javax.swing.JButton();
        jSplitPaneDesc = new javax.swing.JSplitPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaDescription = new javax.swing.JTextArea();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListVersions = new javax.swing.JList<>();
        jTextFieldFilename = new javax.swing.JTextField();
        jButtonEdit = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Rom Manager");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

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

        jProgressBarGame.setString(""); // NOI18N
        jProgressBarGame.setStringPainted(true);

        jButtonScanSource.setText("Scan Source"); // NOI18N
        jButtonScanSource.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonScanSourceActionPerformed(evt);
            }
        });

        jButtonScore.setText("Set Score");
        jButtonScore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonScoreActionPerformed(evt);
            }
        });

        jButtonExport.setText("Sync"); // NOI18N
        jButtonExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExportActionPerformed(evt);
            }
        });

        jButtonReadGameList.setText("Read gamelist.xml");
        jButtonReadGameList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonReadGameListActionPerformed(evt);
            }
        });

        jButtonAbort.setText("Abort");
        jButtonAbort.setEnabled(false);
        jButtonAbort.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAbortActionPerformed(evt);
            }
        });

        jLabelAction.setText("Action: ");

        jButtonSave.setText("Save");
        jButtonSave.setToolTipText("");
        jButtonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveActionPerformed(evt);
            }
        });

        jButtonReadJeuxVideo.setText("Read jeuxvideo.com");
        jButtonReadJeuxVideo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonReadJeuxVideoActionPerformed(evt);
            }
        });

        jProgressBarConsole.setString(""); // NOI18N
        jProgressBarConsole.setStringPainted(true);

        jButtonSendGamelist.setText("Send gamelist.xml");
        jButtonSendGamelist.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSendGamelistActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1)
                    .addComponent(jLabelAction))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jButtonScanSource)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButtonScore)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButtonExport)
                                        .addGap(18, 18, 18)
                                        .addComponent(jButtonReadGameList)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButtonSendGamelist)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButtonReadJeuxVideo)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addComponent(jProgressBarConsole, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jProgressBarGame, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jTextFieldPathExport)
                                .addGap(6, 6, 6)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButtonAbort, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButtonOptionSelectFolderExport, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButtonSave, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jTextFieldPathSource)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonScanSource)
                    .addComponent(jButtonScore)
                    .addComponent(jButtonExport)
                    .addComponent(jButtonReadGameList)
                    .addComponent(jButtonSave)
                    .addComponent(jButtonReadJeuxVideo)
                    .addComponent(jButtonSendGamelist))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jProgressBarConsole, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jProgressBarGame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelAction)))
                    .addComponent(jButtonAbort, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jListFilterRating.setModel(new DefaultListModel());
        jListFilterRating.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jListFilterRating.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListFilterRatingValueChanged(evt);
            }
        });
        jScrollPaneSelectGenre4.setViewportView(jListFilterRating);

        jListFilterNumberFilesExport.setModel(new DefaultListModel());
        jListFilterNumberFilesExport.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jListFilterNumberFilesExport.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListFilterNumberFilesExportValueChanged(evt);
            }
        });
        jScrollPaneSelectGenre5.setViewportView(jListFilterNumberFilesExport);

        jListFilterPlayers.setModel(new DefaultListModel());
        jListFilterPlayers.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jListFilterPlayers.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListFilterPlayersValueChanged(evt);
            }
        });
        jScrollPaneSelectGenre6.setViewportView(jListFilterPlayers);

        jListFilterPlayCount.setModel(new DefaultListModel());
        jListFilterPlayCount.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jListFilterPlayCount.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListFilterPlayCountValueChanged(evt);
            }
        });
        jScrollPaneSelectGenre7.setViewportView(jListFilterPlayCount);

        jListFilterConsole.setModel(new DefaultListModel());
        jListFilterConsole.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jListFilterConsole.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListFilterConsoleValueChanged(evt);
            }
        });
        jScrollPaneSelectGenre1.setViewportView(jListFilterConsole);

        jLabel3.setText("Console");

        jLabel4.setText("Genre");

        jListFilterGenre.setModel(new DefaultListModel());
        jListFilterGenre.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jListFilterGenre.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListFilterGenreValueChanged(evt);
            }
        });
        jScrollPaneSelectGenre2.setViewportView(jListFilterGenre);

        jLabel5.setText("Rating     ");

        jLabel6.setText("Players    ");

        jLabel7.setText("Play Count");

        jLabel8.setText("Nb of exported files");

        triStateCheckBoxFavorite.setText("");
        triStateCheckBoxFavorite.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                triStateCheckBoxFavoriteStateChanged(evt);
            }
        });

        triStateCheckBoxHidden.setText("");
        triStateCheckBoxHidden.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                triStateCheckBoxHiddenStateChanged(evt);
            }
        });

        triStateCheckBoxAdult.setText("");
        triStateCheckBoxAdult.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                triStateCheckBoxAdultStateChanged(evt);
            }
        });

        jLabel10.setText("Favorites");

        jLabel11.setText("Hidden");

        jLabel12.setText("Adult");

        buttonGroupSorting.add(jRadioButtonConsole);
        jRadioButtonConsole.setSelected(true);
        jRadioButtonConsole.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButtonConsoleItemStateChanged(evt);
            }
        });

        buttonGroupSorting.add(jRadioButtonRating);
        jRadioButtonRating.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButtonRatingItemStateChanged(evt);
            }
        });

        buttonGroupSorting.add(jRadioButtonPlayers);
        jRadioButtonPlayers.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButtonPlayersItemStateChanged(evt);
            }
        });

        buttonGroupSorting.add(jRadioButtonGenre);
        jRadioButtonGenre.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButtonGenreItemStateChanged(evt);
            }
        });

        buttonGroupSorting.add(jRadioButtonDecade);
        jRadioButtonDecade.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButtonDecadeItemStateChanged(evt);
            }
        });

        jLabel9.setText("Date     ");

        jListFilterDecade.setModel(new DefaultListModel());
        jListFilterDecade.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jListFilterDecade.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListFilterDecadeValueChanged(evt);
            }
        });
        jScrollPaneSelectGenre8.setViewportView(jListFilterDecade);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPaneSelectGenre4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jRadioButtonRating)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jRadioButtonPlayers)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPaneSelectGenre6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPaneSelectGenre7, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
            .addComponent(jScrollPaneSelectGenre2, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jScrollPaneSelectGenre1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jRadioButtonConsole)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(triStateCheckBoxFavorite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10)
                        .addGap(18, 18, 18)
                        .addComponent(triStateCheckBoxHidden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel11)
                        .addGap(18, 18, 18)
                        .addComponent(triStateCheckBoxAdult, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jRadioButtonGenre)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPaneSelectGenre5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jRadioButtonDecade)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jScrollPaneSelectGenre8, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))
                .addContainerGap())
            .addComponent(jTextFieldSearch)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jTextFieldSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRadioButtonConsole)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneSelectGenre1, javax.swing.GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE)
                .addGap(8, 8, 8)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jRadioButtonGenre))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneSelectGenre2, javax.swing.GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRadioButtonRating)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6)))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jRadioButtonPlayers)
                        .addComponent(jLabel5)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPaneSelectGenre6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE)
                    .addComponent(jScrollPaneSelectGenre4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPaneSelectGenre7, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel8)
                        .addComponent(jLabel9))
                    .addComponent(jRadioButtonDecade))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPaneSelectGenre8, javax.swing.GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE)
                    .addComponent(jScrollPaneSelectGenre5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(triStateCheckBoxFavorite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(triStateCheckBoxHidden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(triStateCheckBoxAdult, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel10)
                        .addComponent(jLabel11))
                    .addComponent(jLabel12)))
        );

        jSplitPaneFilters.setLeftComponent(jPanel3);

        jSplitPaneList.setResizeWeight(1.0);

        jTableRom.setAutoCreateColumnsFromModel(false);
        jTableRom.setModel(new rommanager.main.TableModelRom());
        jTableRom.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        jTableRom.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTableRomFocusLost(evt);
            }
        });
        jTableRom.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableRomMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTableRomMousePressed(evt);
            }
        });
        jScrollPaneCheckTags1.setViewportView(jTableRom);

        jSplitPaneList.setLeftComponent(jScrollPaneCheckTags1);

        jButtonAuto.setText("Auto");
        jButtonAuto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAutoActionPerformed(evt);
            }
        });

        jButtonVideo.setText("Video");
        jButtonVideo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonVideoActionPerformed(evt);
            }
        });

        jSplitPaneDesc.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPaneDesc.setResizeWeight(0.5);

        jTextAreaDescription.setEditable(false);
        jTextAreaDescription.setColumns(20);
        jTextAreaDescription.setLineWrap(true);
        jTextAreaDescription.setRows(5);
        jTextAreaDescription.setWrapStyleWord(true);
        jTextAreaDescription.setEnabled(false);
        jScrollPane2.setViewportView(jTextAreaDescription);

        jSplitPaneDesc.setTopComponent(jScrollPane2);

        jListVersions.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jListVersionsFocusLost(evt);
            }
        });
        jScrollPane1.setViewportView(jListVersions);

        jSplitPaneDesc.setRightComponent(jScrollPane1);

        jTextFieldFilename.setEditable(false);
        jTextFieldFilename.setEnabled(false);

        jButtonEdit.setText("Edit");
        jButtonEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEditActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTextFieldFilename)
                    .addComponent(jSplitPaneDesc, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jButtonVideo)
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addComponent(jButtonEdit)
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addComponent(jButtonAuto))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonAuto)
                    .addComponent(jButtonVideo)
                    .addComponent(jButtonEdit))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldFilename, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPaneDesc, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE))
        );

        jSplitPaneList.setRightComponent(jPanel2);

        jSplitPaneFilters.setRightComponent(jSplitPaneList);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jSplitPaneFilters, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPaneFilters, javax.swing.GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonScanSourceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonScanSourceActionPerformed
		disableGUI("Listing : ");
		String sourcePath = jTextFieldPathSource.getText();
		File file = new File(sourcePath);
		if(!file.exists()) {
			Popup.warning("Source path does not exist.");
            enableGUI();
			return;
		}
		processList = new ProcessList(sourcePath, progressBarConsole, progressBarGame, tableModel, new CallBackProcess());
		processList.browseNbFiles();		
		DialogConsole.main(this, new CallBackDialogConsoleScan(), true, "Scan Source", false);
    }//GEN-LAST:event_jButtonScanSourceActionPerformed

	private class CallBackDialogConsoleScan implements ICallBackConsole {
		@Override
		public void completed(boolean refresh, boolean onlyCultes) {
			processList.start(refresh);
		}

        @Override
        public void cancelled() {
            enableGUI();
        }
	}
	
	private class CallBackProcess implements ICallBackProcess {
		@Override
		public void completed() {
			enableGuiAndFilter();
		}
	}
    
    private void fillFilters() {
        isListFilterManualChange = false;
        fillFilterConsole();
        fillFilterGenre();
        fillFilterRating();
        fillFilterPlayers();
        fillFilterPlayCount();
        fillFilterNumberFilesExport();
        fillFilterDecade();
        isListFilterManualChange = true;
    }
    
    private void fillFilterConsole() {
        List<String> consoles = tableModel.getRoms().values().stream().map(r -> r.getConsoleStr()).distinct().collect(Collectors.toList());
        jListFilterConsole.setModel(getModel(consoles));
        jListFilterConsole.setSelectedIndex(0);
    }
    
    private DefaultListModel fillFilterGenre() {
        String selectedConsole = (String) jListFilterConsole.getSelectedValue();
        List<String> genres = tableModel.getRoms().values().stream()
                .filter(r -> selectedConsole.equals("All") || r.console.getName().equals(selectedConsole))
                .map(r -> r.getGame().getGenres())
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());
        final DefaultListModel model = getModel(genres);
        jListFilterGenre.setModel(model);
        jListFilterGenre.setSelectedIndex(0);
        return model;
    }
    
    private void fillFilterRating() {
        //FIXME 1 Merge 3 ratings (one from gamelist.xml, 2 from jeuxvideo.com) (or distinct ?)
        String selectedConsole = (String) jListFilterConsole.getSelectedValue();
        String selectedGenre = (String) jListFilterGenre.getSelectedValue();
        List<String> ratings = tableModel.getRoms().values().stream()
                .filter(r -> (selectedConsole.equals("All") || r.console.getName().equals(selectedConsole))
                        && (selectedGenre.equals("All") || r.getGame().getGenres().contains(selectedGenre)))
                .map(r -> String.valueOf(r.getGame().getRating()))
                .distinct().collect(Collectors.toList());
        jListFilterRating.setModel(getModel(ratings));
        jListFilterRating.setSelectedIndex(0);
    }
    
    
    
    private void fillFilterPlayers() {
        String selectedConsole = (String) jListFilterConsole.getSelectedValue();
        String selectedGenre = (String) jListFilterGenre.getSelectedValue();
        String selectedRating = (String) jListFilterRating.getSelectedValue();
        List<String> players = tableModel.getRoms().values().stream()
                .filter(r -> (selectedConsole.equals("All") || r.console.getName().equals(selectedConsole))
                        && (selectedGenre.equals("All") || r.getGame().getGenres().contains(selectedGenre))
                        && (selectedRating.equals("All") || String.valueOf(r.getGame().getRating()).equals(selectedRating)))
                .map(r -> String.valueOf(r.getGame().getPlayers()))
                .distinct().collect(Collectors.toList());
        jListFilterPlayers.setModel(getModel(players));
        jListFilterPlayers.setSelectedIndex(0);
    }
    
    private void fillFilterPlayCount() {
        String selectedConsole = (String) jListFilterConsole.getSelectedValue();
        String selectedGenre = (String) jListFilterGenre.getSelectedValue();
        String selectedRating = (String) jListFilterRating.getSelectedValue();
        String selectedPlayers = (String) jListFilterPlayers.getSelectedValue();
        List<String> playCounts = tableModel.getRoms().values().stream()
                .filter(r -> (selectedConsole.equals("All") || r.console.getName().equals(selectedConsole))
                        && (selectedGenre.equals("All") || r.getGame().getGenres().contains(selectedGenre))
                        && (selectedRating.equals("All") || String.valueOf(r.getGame().getRating()).equals(selectedRating))
                        && (selectedPlayers.equals("All") || String.valueOf(r.getGame().getPlayers()).equals(selectedPlayers)))
                .map(r -> String.valueOf(r.getGame().getPlaycount()))
                .distinct().collect(Collectors.toList());
        jListFilterPlayCount.setModel(getModel(playCounts));
        jListFilterPlayCount.setSelectedIndex(0);
    }
    
    private void fillFilterNumberFilesExport() {
        DefaultListModel model = new DefaultListModel();
        for(ExportFilesNumber element : ExportFilesNumber.values()) {
            model.addElement(element);
        }
        jListFilterNumberFilesExport.setModel(model);
        jListFilterNumberFilesExport.setSelectedValue(ExportFilesNumber.MORE_THAN_ZERO, true);
    }

    private void fillFilterDecade() {
        List<String> decades = tableModel.getRoms().values().stream()
                .map(r -> String.valueOf(r.getGame().getReleaseDecade()))
                .distinct().collect(Collectors.toList());
        jListFilterDecade.setModel(getModel(decades));
        jListFilterDecade.setSelectedIndex(0);
    }
    
    private void jButtonExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExportActionPerformed
        disableGUI("Exporting : ");
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
		processExport = new ProcessExport(sourcePath, exportPath, progressBarConsole, progressBarGame, tableModel, new CallBackProcess());
		processList = new ProcessList(sourcePath, progressBarConsole, progressBarGame, tableModel, new CallBackProcess());
		processList.browseNbFiles();
		DialogConsole.main(this, new CallBackDialogConsoleExport(), false, "Sync", true);
    }//GEN-LAST:event_jButtonExportActionPerformed

	private class CallBackDialogConsoleExport implements ICallBackConsole {
		@Override
		public void completed(boolean refresh, boolean onlyCultes) {
            processExport.setOnlyCultes(onlyCultes);
			processExport.start();
		}

        @Override
        public void cancelled() {
            enableGUI();
        }
	}
	
	private void disableGUI(String text) {
		jLabelAction.setText(text);
		jTableRom.setAutoCreateRowSorter(false);
		jTableRom.setRowSorter(null);
		enableGUI(false);
	}
    
	public void enableGuiAndFilter() {
		fillFilters();
        tableModel.filter();
        enableGUI(true);
	}
    
    public void enableGUI() {
		enableGUI(true);
	}
	
	private void enableGUI(boolean enable) {
		jButtonScanSource.setEnabled(enable);
		jButtonScore.setEnabled(enable);
		jButtonExport.setEnabled(enable);
		jButtonReadGameList.setEnabled(enable);
        jButtonReadJeuxVideo.setEnabled(enable);
		jButtonSave.setEnabled(enable);
        jButtonSendGamelist.setEnabled(enable);
		jButtonEdit.setEnabled(enable);
        jButtonAuto.setEnabled(enable);
		jButtonOptionSelectFolderExport.setEnabled(enable);
		jButtonOptionSelectFolderSource.setEnabled(enable);
		jButtonAbort.setText("Abort");
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
		RomContainer romContainer=getRomContainer();
		if(romContainer!=null) {
            jTextFieldFilename.setText(romContainer.getFilename());
            jTextAreaDescription.setText(romContainer.getGame().getDesc());
			displayVersions(romContainer.getVersions());
		}
    }//GEN-LAST:event_jTableRomMouseClicked

	private void displayVersions(List<RomVersion> versions) {
		DefaultListModel versionsModel = new DefaultListModel();
			int i=0;
			List<Integer> indices=new ArrayList();
			Collections.sort(versions, (RomVersion o1, RomVersion o2) -> {
				//ORDER BY getScore DESC
				if (o1.getScore() == o2.getScore())
					return 0;
				else if (o1.getScore() < o2.getScore())
					return 1;
				else
					return -1;
			});
			for(RomVersion romVersion : versions) {
				versionsModel.addElement(romVersion);
				if(romVersion.isExportable()) {
					indices.add(i);
				}
				i++;
			}
			jListVersions.setModel(versionsModel);
			
			int[] indicesArray = new int[indices.size()];
			for(i = 0; i < indices.size(); i++) { indicesArray[i] = indices.get(i); }
			
			jListVersions.setSelectedIndices(indicesArray);
	}
	
	private RomContainer getRomContainer() {
		RomContainer romContainer=null;
		int selectedRow = jTableRom.getSelectedRow(); 		
		if(selectedRow>=0) { 			
			selectedRow = jTableRom.convertRowIndexToModel(selectedRow);
			romContainer = tableModel.getRom(selectedRow);
		}
		return romContainer;
	}
	
    private void jButtonReadGameListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonReadGameListActionPerformed
		readGamelist();
    }//GEN-LAST:event_jButtonReadGameListActionPerformed

    private void readGamelist() {
        disableGUI("Reading gamelist.xml : ");
		String exportPath = jTextFieldPathExport.getText();
		File file = new File(exportPath);
		if(!file.exists()) {
			enableGUI();
			Popup.warning("Export path does not exist.");
			return;
		}
        String sourcePath = jTextFieldPathSource.getText();
        File sourceFile = new File(sourcePath);
        if(!sourceFile.exists()) {
            Popup.warning("Source path does not exist.");
            enableGUI();
            return;
        }
		processRead = new ProcessRead(sourcePath, exportPath, progressBarConsole, progressBarGame, tableModel, new CallBackProcess());
		processRead.start();
    }
    
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
        jButtonAbort.setEnabled(false);
		jButtonAbort.setText("Aborting...");
		abort(processList);
		abort(processExport);
		abort(processRead);
        abort(processSend);
    }//GEN-LAST:event_jButtonAbortActionPerformed

    private void jButtonScoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonScoreActionPerformed
	    disableGUI("Setting score : ");
        String sourcePath = jTextFieldPathSource.getText();
        File file = new File(sourcePath);
        if(!file.exists()) {
            Popup.warning("Source path does not exist.");
            enableGUI();
            return;
        }
        processSetScore = new ProcessSetScore(progressBarGame, tableModel, new CallBackProcess(), sourcePath);
        processList = new ProcessList(sourcePath, progressBarConsole, progressBarGame, tableModel, new CallBackProcess());
        processList.browseNbFiles();
        DialogConsole.main(this, new CallBackDialogConsoleScore(), false, "Set Score", false);
    }//GEN-LAST:event_jButtonScoreActionPerformed

    private class CallBackDialogConsoleScore implements ICallBackConsole {
		@Override
		public void completed(boolean refresh, boolean onlyCultes) {
            //FIXME 6 option : set best as exportable OR leave exportable flag unchanged
            int n = JOptionPane.showConfirmDialog(null, "Are you sure you want to set score ? It will RESET ALL your selections !!!",  //NOI18N
            "Please Confirm",  //NOI18N
            JOptionPane.YES_NO_OPTION);
            if (n == JOptionPane.YES_OPTION) {
                processSetScore.start();
            } else {
                enableGUI();
            }
		}

        @Override
        public void cancelled() {
            enableGUI();
        }
	}
    
    private void jListVersionsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jListVersionsFocusLost
		DefaultListModel versionsModel = (DefaultListModel)jListVersions.getModel();
		int i=0;
		for (Iterator it = Collections.list(versionsModel.elements()) .iterator(); it.hasNext();) {
			RomVersion romVersion = (RomVersion) it.next();
			romVersion.setExportable(selectedIndicesContains(i));
			i++;
		}
    }//GEN-LAST:event_jListVersionsFocusLost

    private void jButtonAutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAutoActionPerformed
        RomContainer romContainer=getRomContainer();
		if(romContainer!=null) {
			romContainer.setExportableVersions();
			displayVersions(romContainer.getVersions());
//			tableModel.fireTableDataChanged(); //TODO: Uncomment when fire does not deselect line in jtable
		}
    }//GEN-LAST:event_jButtonAutoActionPerformed

    private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveActionPerformed
        disableGUI("Saving : ");
		String sourcePath = jTextFieldPathSource.getText();
		File file = new File(sourcePath);
		if(!file.exists()) {
			Popup.warning("Source path does not exist.");
			enableGUI();
			return;
		}
		new SaveOds(new ICallBackProcess() {
            @Override
            public void completed() {
                jButtonSave.setFont(new Font(jButtonSave.getFont().getName(), Font.PLAIN, 12));
                enableGuiAndFilter();
            }
        } , sourcePath).start();
    }//GEN-LAST:event_jButtonSaveActionPerformed

    private static boolean isListFilterManualChange = true;
    
    private void jListFilterConsoleValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListFilterConsoleValueChanged
        if(jListFilterConsole.getSelectedValue()!=null && !evt.getValueIsAdjusting()) {
            tableModel.tableFilter.displayByConsole((String) jListFilterConsole.getSelectedValue());
            if(isListFilterManualChange) {
                fillFilterGenre();
            }
        }
    }//GEN-LAST:event_jListFilterConsoleValueChanged

    private void jListFilterGenreValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListFilterGenreValueChanged
        if(jListFilterGenre.getSelectedValue()!=null && !evt.getValueIsAdjusting()) {
            tableModel.tableFilter.displayByGenre((String) jListFilterGenre.getSelectedValue());
            if(isListFilterManualChange) {
                fillFilterRating();
                
            }
        }
    }//GEN-LAST:event_jListFilterGenreValueChanged

    private void jListFilterRatingValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListFilterRatingValueChanged
        if(jListFilterRating.getSelectedValue()!=null && !evt.getValueIsAdjusting()) {
            tableModel.tableFilter.displayByRating((String) jListFilterRating.getSelectedValue());
            if(isListFilterManualChange) {
                fillFilterPlayers();
            }
        }
    }//GEN-LAST:event_jListFilterRatingValueChanged

    private void jTableRomFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTableRomFocusLost
        int selectedRow = jTableRom.getSelectedRow(); 		
        if(selectedRow<0) { 			
			DefaultListModel model = (DefaultListModel)jListVersions.getModel();
                model.clear();
		}
    }//GEN-LAST:event_jTableRomFocusLost

    private void jListFilterNumberFilesExportValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListFilterNumberFilesExportValueChanged
        if(jListFilterNumberFilesExport.getSelectedValue()!=null && !evt.getValueIsAdjusting()) {
            tableModel.tableFilter.displayByNumberExportFiles((ExportFilesNumber) jListFilterNumberFilesExport.getSelectedValue());
            if(isListFilterManualChange) {
                tableModel.filter();
            }
        }
    }//GEN-LAST:event_jListFilterNumberFilesExportValueChanged

    private void jButtonReadJeuxVideoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonReadJeuxVideoActionPerformed
        disableGUI("Reading jeuxvideo.com : ");
        String sourcePath = jTextFieldPathSource.getText();
		JeuxVideos jeuxVideos = new JeuxVideos(new CallBackJeuxVideo(), tableModel, progressBarGame, sourcePath);
		jeuxVideos.start();
    }//GEN-LAST:event_jButtonReadJeuxVideoActionPerformed

    private void jButtonVideoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonVideoActionPerformed
        RomContainer romContainer=getRomContainer();
		if(romContainer!=null) {
            String exportPath = jTextFieldPathExport.getText();
            File file = new File(exportPath);
            if(!file.exists()) {
                enableGUI();
                Popup.warning("Export path does not exist.");
                return;
            }
            String consolePath = FilenameUtils.concat(exportPath, romContainer.getConsole().name());           
            File cacheFile = BufferVideo.getCacheFile(romContainer.getGame().getName(), new File(FilenameUtils.concat(consolePath, romContainer.getGame().getVideo())));
            Desktop.openFile(cacheFile.getAbsolutePath());
            
            
            
//			romContainer.setExportableVersions();
//			displayVersions(romContainer.getVersions());
//			tableModel.fireTableDataChanged(); //TODO: Uncomment when fire does not deselect line in jtable
		}
    }//GEN-LAST:event_jButtonVideoActionPerformed

    private void jListFilterPlayersValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListFilterPlayersValueChanged
        if(jListFilterPlayers.getSelectedValue()!=null && !evt.getValueIsAdjusting()) {
            tableModel.tableFilter.displayByPlayers((String) jListFilterPlayers.getSelectedValue());
            if(isListFilterManualChange) {
                fillFilterPlayCount();
            }
        }
    }//GEN-LAST:event_jListFilterPlayersValueChanged

    private void jListFilterPlayCountValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListFilterPlayCountValueChanged
        if(jListFilterPlayCount.getSelectedValue()!=null && !evt.getValueIsAdjusting()) {
            tableModel.tableFilter.displayByPlayCount((String) jListFilterPlayCount.getSelectedValue());
            if(isListFilterManualChange) {
                fillFilterNumberFilesExport();
            }
        }
    }//GEN-LAST:event_jListFilterPlayCountValueChanged

    private void triStateCheckBoxFavoriteStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_triStateCheckBoxFavoriteStateChanged
        TriStateCheckBox checkbox = (TriStateCheckBox) evt.getSource();
        if(!checkbox.getModel().isArmed()) {
            tableModel.tableFilter.displayFavorite(checkbox.getState());
            tableModel.filter();
        }
    }//GEN-LAST:event_triStateCheckBoxFavoriteStateChanged

    private void triStateCheckBoxHiddenStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_triStateCheckBoxHiddenStateChanged
        TriStateCheckBox checkbox = (TriStateCheckBox) evt.getSource();
        if(!checkbox.getModel().isArmed()) {
            tableModel.tableFilter.displayHidden(checkbox.getState());
            tableModel.filter();
        }
    }//GEN-LAST:event_triStateCheckBoxHiddenStateChanged

    private void triStateCheckBoxAdultStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_triStateCheckBoxAdultStateChanged
        TriStateCheckBox checkbox = (TriStateCheckBox) evt.getSource();
        if(!checkbox.getModel().isArmed()) {
            tableModel.tableFilter.displayAdult(checkbox.getState());
            tableModel.filter();
        }
    }//GEN-LAST:event_triStateCheckBoxAdultStateChanged

    private void jButtonEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEditActionPerformed
        RomContainer romContainer = getRomContainer();
		if(romContainer!=null) {
            String sourcePath = jTextFieldPathSource.getText();
            File sourceFile = new File(sourcePath);
            if(!sourceFile.exists()) {
                Popup.warning("Source path does not exist.");
                enableGUI();
                return;
            }
            File localFile = new File(FilenameUtils.concat(FilenameUtils.concat(sourcePath, romContainer.getConsole().name()), "gamelist.xml"));
            DialogRomEdition.main(this, romContainer.getConsole(), romContainer, localFile.getAbsolutePath(), new ICallBackProcess() {
                @Override
                public void completed() {
                    tableModel.filter();
                    jButtonSendGamelist.setFont(new Font(jButtonSendGamelist.getFont().getName(), Font.BOLD, 16));
                    jButtonSave.setFont(new Font(jButtonSave.getFont().getName(), Font.BOLD, 16));
                }
            });
        }
    }//GEN-LAST:event_jButtonEditActionPerformed

    private void jButtonSendGamelistActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSendGamelistActionPerformed
        disableGUI("Sending gamelist.xml : ");
		String exportPath = jTextFieldPathExport.getText();
		File file = new File(exportPath);
		if(!file.exists()) {
			enableGUI();
			Popup.warning("Export path does not exist.");
			return;
		}
        String sourcePath = jTextFieldPathSource.getText();
        File sourceFile = new File(sourcePath);
        if(!sourceFile.exists()) {
            Popup.warning("Source path does not exist.");
            enableGUI();
            return;
        }
		processSend = new ProcessSend(sourcePath, exportPath, progressBarConsole, progressBarGame, new ICallBackProcess() {
            @Override
            public void completed() {
                jButtonSendGamelist.setFont(new Font(jButtonSendGamelist.getFont().getName(), Font.PLAIN, 12));
                enableGuiAndFilter();
            }
        });
		processSend.start();
    }//GEN-LAST:event_jButtonSendGamelistActionPerformed

    private void jRadioButtonRatingItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButtonRatingItemStateChanged
        JRadioButton source = (JRadioButton) evt.getSource();
        if(source.isSelected()) {
            tableModel.tableFilter.sortBy(TableModelColumn.Rating);
            tableModel.filter();
        }
    }//GEN-LAST:event_jRadioButtonRatingItemStateChanged

    private void jRadioButtonConsoleItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButtonConsoleItemStateChanged
        JRadioButton source = (JRadioButton) evt.getSource();
        if(source.isSelected()) {
            tableModel.tableFilter.sortBy(TableModelColumn.Console);
            tableModel.filter();
        }
    }//GEN-LAST:event_jRadioButtonConsoleItemStateChanged

    private void jRadioButtonPlayersItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButtonPlayersItemStateChanged
        JRadioButton source = (JRadioButton) evt.getSource();
        if(source.isSelected()) {
            tableModel.tableFilter.sortBy(TableModelColumn.Players);
            tableModel.filter();
        }
    }//GEN-LAST:event_jRadioButtonPlayersItemStateChanged

    private void jRadioButtonGenreItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButtonGenreItemStateChanged
        JRadioButton source = (JRadioButton) evt.getSource();
        if(source.isSelected()) {
            tableModel.tableFilter.sortBy(TableModelColumn.Genre);
            tableModel.filter();
        }
    }//GEN-LAST:event_jRadioButtonGenreItemStateChanged

    private void jRadioButtonDecadeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButtonDecadeItemStateChanged
        JRadioButton source = (JRadioButton) evt.getSource();
        if(source.isSelected()) {
            tableModel.tableFilter.sortBy(TableModelColumn.ReleaseDate);
            tableModel.filter();
        }
    }//GEN-LAST:event_jRadioButtonDecadeItemStateChanged

    private void jListFilterDecadeValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListFilterDecadeValueChanged
        if(jListFilterDecade.getSelectedValue()!=null && !evt.getValueIsAdjusting()) {
            tableModel.tableFilter.displayByDecade((String) jListFilterDecade.getSelectedValue());
            if(isListFilterManualChange) {
                tableModel.filter();
            }
        }
    }//GEN-LAST:event_jListFilterDecadeValueChanged

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        boolean isSendGameBold = jButtonSendGamelist.getFont().equals(new Font(jButtonSendGamelist.getFont().getName(), Font.BOLD, 16));
        boolean isSaveBold = jButtonSave.getFont().equals(new Font(jButtonSave.getFont().getName(), Font.BOLD, 16));
        if(isSendGameBold || isSaveBold) {
            int result = JOptionPane.showConfirmDialog(this, "Exit the application?");
            if (result==JOptionPane.OK_OPTION) {
                System.exit(0);     
            }
        } else {
            System.exit(0); 
        }
    }//GEN-LAST:event_formWindowClosing
    
    class CallBackJeuxVideo implements ICallBack {
		
		@Override
		public void completed() {
            enableGuiAndFilter();
		}

		@Override
		public void interrupted() {
			Popup.info("Canceled");
            enableGUI();
		}

		@Override
		public void error(Exception ex) {
            Popup.info("Error: " + ex.getLocalizedMessage());
			enableGUI();
		}
	}
    
	private class SaveOds extends ProcessAbstract {
		private final ICallBackProcess callBack;
		private final String sourceFolder;
		public SaveOds(ICallBackProcess callBack, String sourceFolder) {
			super("Thread.RomManagerGUI.ReadOds");
			this.callBack = callBack;
			this.sourceFolder = sourceFolder;
		}
		@Override
		public void run() {
			progressBarGame.setIndeterminate("Saving ods file");
			RomManagerOds.createFile(tableModel, progressBarGame, sourceFolder);
			progressBarGame.reset();
			callBack.completed();
		}
	}
	
	private boolean selectedIndicesContains(int value) {
		return IntStream.of(jListVersions.getSelectedIndices()).anyMatch(x -> x == value);
	}
	
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
        //</editor-fold>

        java.awt.EventQueue.invokeLater(() -> {
			RomManagerGUI panel = new RomManagerGUI();
			panel.setLocationRelativeTo(null);
			panel.setExtendedState(RomManagerGUI.MAXIMIZED_BOTH);
			panel.setVisible(true);
		});
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupSorting;
    private static javax.swing.JButton jButtonAbort;
    private javax.swing.JButton jButtonAuto;
    private javax.swing.JButton jButtonEdit;
    private javax.swing.JButton jButtonExport;
    private javax.swing.JButton jButtonOptionSelectFolderExport;
    private javax.swing.JButton jButtonOptionSelectFolderSource;
    private javax.swing.JButton jButtonReadGameList;
    private javax.swing.JButton jButtonReadJeuxVideo;
    private javax.swing.JButton jButtonSave;
    private javax.swing.JButton jButtonScanSource;
    private javax.swing.JButton jButtonScore;
    private javax.swing.JButton jButtonSendGamelist;
    private javax.swing.JButton jButtonVideo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelAction;
    private javax.swing.JList jListFilterConsole;
    private javax.swing.JList jListFilterDecade;
    private javax.swing.JList jListFilterGenre;
    private javax.swing.JList jListFilterNumberFilesExport;
    private javax.swing.JList jListFilterPlayCount;
    private javax.swing.JList jListFilterPlayers;
    private javax.swing.JList jListFilterRating;
    private javax.swing.JList<String> jListVersions;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private static javax.swing.JProgressBar jProgressBarConsole;
    private static javax.swing.JProgressBar jProgressBarGame;
    private javax.swing.JRadioButton jRadioButtonConsole;
    private javax.swing.JRadioButton jRadioButtonDecade;
    private javax.swing.JRadioButton jRadioButtonGenre;
    private javax.swing.JRadioButton jRadioButtonPlayers;
    private javax.swing.JRadioButton jRadioButtonRating;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private static javax.swing.JScrollPane jScrollPaneCheckTags1;
    private javax.swing.JScrollPane jScrollPaneSelectGenre1;
    private javax.swing.JScrollPane jScrollPaneSelectGenre2;
    private javax.swing.JScrollPane jScrollPaneSelectGenre4;
    private javax.swing.JScrollPane jScrollPaneSelectGenre5;
    private javax.swing.JScrollPane jScrollPaneSelectGenre6;
    private javax.swing.JScrollPane jScrollPaneSelectGenre7;
    private javax.swing.JScrollPane jScrollPaneSelectGenre8;
    private javax.swing.JSplitPane jSplitPaneDesc;
    private javax.swing.JSplitPane jSplitPaneFilters;
    private javax.swing.JSplitPane jSplitPaneList;
    private static javax.swing.JTable jTableRom;
    private javax.swing.JTextArea jTextAreaDescription;
    private javax.swing.JTextField jTextFieldFilename;
    private static javax.swing.JTextField jTextFieldPathExport;
    private static javax.swing.JTextField jTextFieldPathSource;
    private javax.swing.JTextField jTextFieldSearch;
    private rommanager.utils.TriStateCheckBox triStateCheckBoxAdult;
    private rommanager.utils.TriStateCheckBox triStateCheckBoxFavorite;
    private rommanager.utils.TriStateCheckBox triStateCheckBoxHidden;
    // End of variables declaration//GEN-END:variables
}
