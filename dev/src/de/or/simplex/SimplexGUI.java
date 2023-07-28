package de.or.simplex;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.*;

/*	---------------------------------------------------------------------------------
---------------------------------------------------------------------------------
--| Copyright (c) by Tobias Burkard, 2011	      |--
---------------------------------------------------------------------------------
-- --
-- CLASS: SimplexGUI --
-- --
---------------------------------------------------------------------------------
-- --
-- PROJECT: Simplex-Tableu --
-- --
---------------------------------------------------------------------------------
-- --
-- SYSTEM ENVIRONMENT 					    --
-- OS			Ubuntu 10.04 (Linux 2.6.32)	--
-- SOFTWARE 	JDK 1.6.0 				    --
-- --
---------------------------------------------------------------------------------
---------------------------------------------------------------------------------	*/

/**	
*	Dies ist die GUI-Klasse des Simplex-Solvers.
*	Sie zeichnet das GUI und kümmert sich um die korrekte Formatierung der Tabellen.
*	Bei bedarf übergibt sie die zu berechnende Tabelle an die SimplexC-Klasse.
*	
* 	@version 0.1 von 04.2011
*
* 	@author Tobias Burkard
* 
* 	@since JDK 1.6.0
**/
public class SimplexGUI {
	
	// --- Attribute
	private JFrame frame_ref;
	private JFrame infoFrame_ref;
	private JTable tableEin_ref;
	private JTable tableAus_ref;
	private DefaultTableModel tableModEin_ref;
	private DefaultTableModel tableModAus_ref;
	private String[] tableNames1X_ref = {"", "x", "y", "z", "rechte Seite"};
	private String[] tableNames1Y_ref = {"G", "sA", "sB", "sC", "sD"};
	private String[] tableNames2X_ref = {"", "", "", "", "rechte Seite"};
	private String[] tableNames2Y_ref = {"G", "", "", "", ""};
	private String[][] tableMatrixEin_ref;
	private String[][] tableMatrixAus_ref;
	private JScrollPane scrollerEin_ref;
	private JScrollPane scrollerAus_ref;
	JTextField xT_ref;
	JTextField yT_ref;
	JTextField zT_ref;
	JTextField gT_ref;
	private SimplexC mainC_ref;
	double takenTime;
	
	/**
	 * Erstellt leere Tabellen fuer den Start
	 */
	public SimplexGUI() {
		tableModEin_ref = (DefaultTableModel) new MyTableModelEin(null, tableNames1X_ref);
		tableModAus_ref = (DefaultTableModel) new MyTableModelAus(null, tableNames2X_ref);
		tableMatrixEin_ref = createTableMatrix(1);
		tableMatrixAus_ref = createTableMatrix(2);
		takenTime = 0.0;
	} //endconstructor SimplexGUI
	
	// --- Methoden
	/**
	 * Zeichnet das Haupt-GUI
	 * 
	 * @param mainC_ref Das SimplexC-Objekt: Eine Rueckreferenz auf die Hauptklasse
	 */
	void drawGUI(SimplexC mainC_ref) {
		this.mainC_ref = mainC_ref;
		frame_ref = new JFrame("Simplex-Tableu Solver v0.1");
		frame_ref.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ImageIcon frameIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/or/simplex/resource/calc.gif"));
		frame_ref.setIconImage(frameIcon_ref.getImage());
		JMenuBar menuBar_ref = new JMenuBar();
		frame_ref.setJMenuBar(menuBar_ref);
		JMenu dateiMenu_ref = new JMenu("Datei");
		menuBar_ref.add(dateiMenu_ref);
		JMenu tabelleMenu_ref = new JMenu("Tabelle");
		menuBar_ref.add(tabelleMenu_ref);
		JMenu hilfeMenu_ref = new JMenu("Hilfe");
		menuBar_ref.add(hilfeMenu_ref);
		JMenuItem dateiMenu_beenden_ref = new JMenuItem("Beenden");
		dateiMenu_beenden_ref.addActionListener(new MenuListener());
		dateiMenu_ref.add(dateiMenu_beenden_ref);
		JMenuItem tabelleMenu_berechnen_ref = new JMenuItem("Berechnen");
		tabelleMenu_berechnen_ref.addActionListener(new MenuListener());
		tabelleMenu_ref.add(tabelleMenu_berechnen_ref);
		JMenuItem tabelleMenu_reset_ref = new JMenuItem("Reset");
		tabelleMenu_reset_ref.addActionListener(new MenuListener());
		tabelleMenu_ref.add(tabelleMenu_reset_ref);
		JMenuItem hilfeMenu_info_ref = new JMenuItem("Info");
		hilfeMenu_info_ref.addActionListener(new MenuListener());
		hilfeMenu_ref.add(hilfeMenu_info_ref);
		
		Box hintergrund_ref = new Box(BoxLayout.Y_AXIS);
		hintergrund_ref.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		
		JPanel infoPanel_ref = new JPanel();
		GridLayout infoGrid_ref = new GridLayout(4,1);
		infoPanel_ref.setLayout(infoGrid_ref);
		infoPanel_ref.setBorder(new TitledBorder("Information"));
		JLabel info0_ref = new JLabel("Zum Start füllen Sie bitte die erste Tabelle aus und klicken dann");
		JLabel info1_ref = new JLabel("auf \"Berechnen\".");
		JLabel info2_ref = new JLabel("Zulaessig sind ganzzahlige und Kommawerte.");
		Font labelFont_ref = new Font(info0_ref.getFont().getName(),Font.PLAIN, info0_ref.getFont().getSize());
		info0_ref.setFont(labelFont_ref);
		info1_ref.setFont(labelFont_ref);
		info2_ref.setFont(labelFont_ref);
		infoPanel_ref.add(info0_ref);
		infoPanel_ref.add(info1_ref);
		infoPanel_ref.add(Box.createVerticalStrut(1));
		infoPanel_ref.add(info2_ref);
		hintergrund_ref.add(infoPanel_ref);
		hintergrund_ref.add(Box.createVerticalStrut(20));
		
		JPanel legendPanel_ref = new JPanel();
		legendPanel_ref.setBorder(new TitledBorder("Legende"));
		JLabel trenn1L_ref = new JLabel(" | ");
		JLabel trenn2L_ref = new JLabel(" | ");
		JLabel legend00_ref = new JLabel("Zielfunktion: ");
		legend00_ref.setFont(labelFont_ref);
		JLabel legend01_ref = new JLabel("G");
		JLabel legend10_ref = new JLabel("Variablen: ");
		legend10_ref.setFont(labelFont_ref);
		JLabel legend11_ref = new JLabel("x,y,z");
		JLabel legend20_ref = new JLabel("Schlupfvariablen: ");
		legend20_ref.setFont(labelFont_ref);
		JLabel legend21_ref = new JLabel("sA,sB,sC,sD");
		legendPanel_ref.add(legend00_ref);
		legendPanel_ref.add(legend01_ref);
		legendPanel_ref.add(trenn1L_ref);
		legendPanel_ref.add(legend10_ref);
		legendPanel_ref.add(legend11_ref);
		legendPanel_ref.add(trenn2L_ref);
		legendPanel_ref.add(legend20_ref);
		legendPanel_ref.add(legend21_ref);
		
		hintergrund_ref.add(legendPanel_ref);
		hintergrund_ref.add(Box.createVerticalStrut(20));
		
		JPanel panOne_ref = new JPanel();
		tableEin_ref = new JTable();
		tableEin_ref.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tableModEin_ref = (DefaultTableModel) new MyTableModelEin(tableMatrixEin_ref, tableNames1X_ref);
		tableEin_ref.setModel(tableModEin_ref);
		tableEin_ref.setDefaultRenderer(Object.class, new MyTableRenderer());
		((DefaultTableCellRenderer)tableEin_ref.getDefaultRenderer(tableEin_ref.getColumnClass(0))).setHorizontalAlignment(SwingConstants.CENTER);
		DefaultTableCellRenderer headerRendererEin_ref = ((DefaultTableCellRenderer) tableEin_ref.getTableHeader().getDefaultRenderer());
		Font originalFontEin_ref = headerRendererEin_ref.getFont();
		Font boldFontEin_ref = new Font (originalFontEin_ref.getName(), Font.BOLD, originalFontEin_ref.getSize());
		TableColumn colEin_ref = tableEin_ref.getColumnModel().getColumn(4);
		colEin_ref.setPreferredWidth(100);
		tableEin_ref.getTableHeader().setFont(boldFontEin_ref);
		tableEin_ref.setDefaultEditor(new Object().getClass(), new MyTableEditor());
		scrollerEin_ref = new JScrollPane(tableEin_ref);
		scrollerEin_ref.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollerEin_ref.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		Dimension dimEin_ref = scrollerEin_ref.getPreferredSize();
		
		scrollerEin_ref.setPreferredSize(new Dimension((int)(dimEin_ref.getWidth()),(int)(dimEin_ref.getHeight()/3)));
		scrollerEin_ref.setMaximumSize(new Dimension((int)(dimEin_ref.getWidth()),(int)(dimEin_ref.getHeight()/3)));
		scrollerEin_ref.setBorder(new TitledBorder("Ausgangstabelle"));
		
		panOne_ref.add(scrollerEin_ref);
	
		JPanel panTwo_ref = new JPanel();
		tableAus_ref = new JTable();
		tableAus_ref.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tableModAus_ref = (DefaultTableModel) new MyTableModelAus(tableMatrixAus_ref, tableNames2X_ref);
		tableAus_ref.setModel(tableModAus_ref);
		tableAus_ref.setDefaultRenderer(Object.class, new MyTableRenderer());
		((DefaultTableCellRenderer)tableAus_ref.getDefaultRenderer(tableAus_ref.getColumnClass(0))).setHorizontalAlignment(SwingConstants.CENTER);
		DefaultTableCellRenderer headerRendererAus_ref = ((DefaultTableCellRenderer) tableAus_ref.getTableHeader().getDefaultRenderer());
		Font boldFontAus_ref = new Font (headerRendererAus_ref.getFont().getName(), Font.BOLD, headerRendererAus_ref.getFont().getSize());
		TableColumn colAus_ref = tableAus_ref.getColumnModel().getColumn(4);
		colAus_ref.setPreferredWidth(100);
		tableAus_ref.getTableHeader().setFont(boldFontAus_ref);
		scrollerAus_ref = new JScrollPane(tableAus_ref);
		scrollerAus_ref.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollerAus_ref.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		Dimension dimAus_ref = scrollerAus_ref.getPreferredSize();
		scrollerAus_ref.setPreferredSize(new Dimension((int)(dimAus_ref.getWidth()),(int)(dimAus_ref.getHeight()/3)));
		scrollerAus_ref.setMaximumSize(new Dimension((int)(dimAus_ref.getWidth()),(int)(dimAus_ref.getHeight()/3)));
		scrollerAus_ref.setBorder(new TitledBorder("Ergebnistabelle"));
		panTwo_ref.add(scrollerAus_ref);
		
		JPanel buttonPanel_ref = new JPanel();
		ImageIcon calcButtonIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/or/simplex/resource/calc.gif"));
		JButton calcButton_ref = new JButton("Berechnen", calcButtonIcon_ref);
		calcButton_ref.addActionListener(new ButtonListener());
		buttonPanel_ref.add(calcButton_ref);
		ImageIcon resetButtonIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/or/simplex/resource/redo.gif"));
		JButton resetButton_ref = new JButton("Reset", resetButtonIcon_ref);
		resetButton_ref.addActionListener(new ButtonListener());
		buttonPanel_ref.add(Box.createVerticalStrut(1));
		buttonPanel_ref.add(resetButton_ref);
		
		JPanel resultPanel_ref = new JPanel();
		JLabel titleL_ref = new JLabel("Ergebnis: ");
		JLabel xL_ref = new JLabel("X: ");
		JLabel yL_ref = new JLabel("Y: ");
		JLabel zL_ref = new JLabel("Z: ");
		JLabel gL_ref = new JLabel("G: ");
		xT_ref = new JTextField(5);
		xT_ref.setEditable(false);
		yT_ref = new JTextField(5);
		yT_ref.setEditable(false);
		zT_ref = new JTextField(5);
		zT_ref.setEditable(false);
		gT_ref = new JTextField(7);
		gT_ref.setEditable(false);
		resultPanel_ref.add(titleL_ref);
		resultPanel_ref.add(Box.createVerticalStrut(1));
		resultPanel_ref.add(xL_ref);
		resultPanel_ref.add(xT_ref);
		resultPanel_ref.add(Box.createVerticalStrut(1));
		resultPanel_ref.add(yL_ref);
		resultPanel_ref.add(yT_ref);
		resultPanel_ref.add(Box.createVerticalStrut(1));
		resultPanel_ref.add(zL_ref);
		resultPanel_ref.add(zT_ref);
		resultPanel_ref.add(Box.createVerticalStrut(1));
		resultPanel_ref.add(gL_ref);
		resultPanel_ref.add(gT_ref);
		
		hintergrund_ref.add(panOne_ref);
		hintergrund_ref.add(panTwo_ref);
		hintergrund_ref.add(resultPanel_ref);
		hintergrund_ref.add(Box.createVerticalStrut(10));
		hintergrund_ref.add(buttonPanel_ref);
		
		frame_ref.getContentPane().add(hintergrund_ref);
		
		frame_ref.pack();
		frame_ref.setResizable(false);
		frame_ref.setVisible(true);
	} //endmethod drawGui
	
	/**
	 * Zeichnet das info-Fenster
	 */
	private void drawInfo() {
		infoFrame_ref = new JFrame("Info");
		Toolkit kit_ref = infoFrame_ref.getToolkit();
		GraphicsEnvironment ge_ref = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs_ref = ge_ref.getScreenDevices();
        Insets in_ref = kit_ref.getScreenInsets(gs_ref[0].getDefaultConfiguration());
        Dimension d_ref = kit_ref.getScreenSize();
        int max_width = (d_ref.width - in_ref.left - in_ref.right);
        int max_height = (d_ref.height - in_ref.top - in_ref.bottom);
		infoFrame_ref.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		infoFrame_ref.setLocation((int) (max_width - infoFrame_ref.getWidth()) / 2, (int) (max_height - infoFrame_ref.getHeight() ) / 2);
		ImageIcon infoI_ref = new ImageIcon(ClassLoader.getSystemResource("de/or/simplex/resource/info.gif"));
		Box hintergrund_ref = new Box(BoxLayout.Y_AXIS);
		JPanel infoHeaderP_ref = new JPanel();
		GridLayout headerGrid_ref = new GridLayout(2,1);
		infoHeaderP_ref.setLayout(headerGrid_ref);
		JLabel infoPicL_ref = new JLabel(infoI_ref);
		JLabel infoHeaderL_ref = new JLabel("Simplex-Tableu Solver v0.1");
		infoHeaderL_ref.setFont(new Font(infoHeaderL_ref.getFont().getFontName(), infoHeaderL_ref.getFont().getStyle(), 22));
		infoHeaderP_ref.add(infoPicL_ref);
		infoHeaderP_ref.add(infoHeaderL_ref);
		Box infoDescBox_ref = new Box(BoxLayout.Y_AXIS);
		Box infoDesc1LBox_ref = new Box(BoxLayout.X_AXIS);
		JLabel infoDesc1L_ref = new JLabel("Loesen von linearen Ungleichungssystemen");
		infoDesc1LBox_ref.add(infoDesc1L_ref);
		Box infoDesc2LBox_ref = new Box(BoxLayout.X_AXIS);
		JLabel infoDesc2L_ref = new JLabel("mit Hilfe des dualen Simplex-Alogirthmus.");
		infoDesc2LBox_ref.add(infoDesc2L_ref);
		infoDesc1L_ref.setFont(new Font(infoDesc1L_ref.getFont().getFontName(), Font.PLAIN, infoDesc1L_ref.getFont().getSize()));
		infoDesc2L_ref.setFont(new Font(infoDesc2L_ref.getFont().getFontName(), Font.PLAIN, infoDesc1L_ref.getFont().getSize()));
		infoDescBox_ref.add(infoDesc1LBox_ref);
		infoDescBox_ref.add(infoDesc2LBox_ref);
		Box copyRightBox_ref = new Box(BoxLayout.X_AXIS);
		JLabel copyRightL_ref = new JLabel("(c) Tobias Burkard (2011)");
		copyRightL_ref.setFont(new Font(copyRightL_ref.getFont().getFontName(), Font.ITALIC, copyRightL_ref.getFont().getSize()));
		copyRightBox_ref.add(copyRightL_ref);
		Box closeButtonBox_ref = new Box(BoxLayout.X_AXIS);
		ImageIcon closeButtonIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/or/simplex/resource/close.gif"));
		JButton closeButton_ref = new JButton("Schliessen", closeButtonIcon_ref);
		closeButton_ref.addActionListener(new ButtonListener());
		closeButtonBox_ref.add(closeButton_ref);
		hintergrund_ref.add(infoHeaderP_ref);
		hintergrund_ref.add(infoDescBox_ref);
		hintergrund_ref.add(Box.createVerticalStrut(10));
		hintergrund_ref.add(copyRightBox_ref);
		hintergrund_ref.add(Box.createVerticalStrut(20));
		hintergrund_ref.add(closeButtonBox_ref);
		infoFrame_ref.getContentPane().add(hintergrund_ref);
		infoFrame_ref.pack();
		infoFrame_ref.setResizable(false);
		infoFrame_ref.setVisible(true);
	} //endmethod drawInfo
	
	/**
	 * Erstellt leere Tabellen fuer das GUI
	 * 
	 * @param type 1:Eingangstabelle, 2:Ergebnistabelle
	 * @return Die leere Tabelle
	 */
	private String[][] createTableMatrix(int type) {
		String[][] tempMatrix_ref = new String[5][5];
		
		if (type == 1) {
			for (int i=0; i<tempMatrix_ref.length; i++) {
				for (int j=0; j<tempMatrix_ref[i].length; j++) {
					tempMatrix_ref[i][j] = "0";
				} //endfor
			} //endfor
			tableNames1Y_ref[0] = "G";
			tableNames1Y_ref[1] = "sA";
			tableNames1Y_ref[2] = "sB";
			tableNames1Y_ref[3] = "sC";
			tableNames1Y_ref[4] = "sD";
			
			tableNames1X_ref[0] = "";
			tableNames1X_ref[1] = "x";
			tableNames1X_ref[2] = "y";
			tableNames1X_ref[3] = "z";
			tableNames1X_ref[4] = "rechte Seite";
			
			for (int i=0; i<5; i++) {
				tempMatrix_ref[i][0] = tableNames1Y_ref[i];
			} //endfor
		} else {
			for (int i=0; i<5; i++) {
				for (int j=0; j<5; j++) {
					tempMatrix_ref[i][j] = "";
				} //endfor
			} //endfor
			
			tableNames2X_ref[0] = "";
			tableNames2X_ref[1] = "";
			tableNames2X_ref[2] = "";
			tableNames2X_ref[3] = "";
			tableNames2X_ref[4] = "rechte Seite";
			
			tableNames2Y_ref[0] = "G";
			tableNames2Y_ref[1] = "";
			tableNames2Y_ref[2] = "";
			tableNames2Y_ref[3] = "";
			tableNames2Y_ref[4] = "";
			
			for (int i=0; i<5; i++) {
				tempMatrix_ref[i][0] = tableNames2Y_ref[i];
			} //endfor
		} //endif

		return tempMatrix_ref;
	} //endmethod createTableMatrix
	
	/**
	 * Konvertiert die GUI-Tabelle in ein, fuer das SimplexC-Objekt, lesbares Format.
	 * Anschliessend wird die Tabelle an das SimplexC-Objekt zur Berechnung uebergeben.
	 * Zum Schluss wird die gewonnene Ergebnistabelle wieder in das GUI ueberfuehrt.
	 */
	private void callCalculate() {
		String[][] calcMatrix_ref = new String[7][5];
		for (int i=0; i<5; i++) {
			for (int j=0; j<4; j++) {
				calcMatrix_ref[i][j] = (String) tableEin_ref.getModel().getValueAt(i, (j+1));
			} //endfor
		} //endfor
		
		for (int i=0; i<5; i++) {
			calcMatrix_ref[5][i] = tableNames1X_ref[i];
			calcMatrix_ref[6][i] = tableNames1Y_ref[i];
		} //endfor
		
		calcMatrix_ref = mainC_ref.calculate(calcMatrix_ref);
		if (calcMatrix_ref[0][0].equals("error")) {
			if(calcMatrix_ref[0][1].equals("keine_nb")) {
				JOptionPane.showMessageDialog(frame_ref,"Sie haben keine Nebenbedingungen angegeben,\nbitte ueberpruefen Sie ihre Eingabe.", "Achtung", JOptionPane.WARNING_MESSAGE);
				
			} else if (calcMatrix_ref[0][1].equals("leere_matrix")) {
				JOptionPane.showMessageDialog(frame_ref,"Sie muessen Werte eingeben, bevor Sie auf berechnen klicken.", "Achtung", JOptionPane.WARNING_MESSAGE);
			} else if (calcMatrix_ref[0][1].equals("loesung_leer")) {
				JOptionPane.showMessageDialog(frame_ref,"Es gibt keine Loesung, da die Menge des\nUngleichungssystems leer ist.", "Ergebnis", JOptionPane.INFORMATION_MESSAGE);
			} else if (calcMatrix_ref[0][1].equals("loesung_unb")) {
				JOptionPane.showMessageDialog(frame_ref,"Die Loesungsmenge des Ungleichungssystems ist unbeschränkt", "Ergebnis", JOptionPane.INFORMATION_MESSAGE);
			} else if (calcMatrix_ref[0][1].equals("iterations")) {
				JOptionPane.showMessageDialog(frame_ref,"Auch nach 100 Iterationen konnte leider keine Loesung gefunden werden", "Fehler", JOptionPane.ERROR_MESSAGE);
			} else if (calcMatrix_ref[0][1].equals("keine_nb")) {
				JOptionPane.showMessageDialog(frame_ref,"Sie muessen Nebenbedingungen angeben.", "Achtung", JOptionPane.WARNING_MESSAGE);
			} else if (calcMatrix_ref[0][1].equals("invalid_function")) {
				JOptionPane.showMessageDialog(frame_ref,"Ihre Zielfunktion ist ungueltig.", "Achtung", JOptionPane.WARNING_MESSAGE);
			} //endif
			
			tableMatrixAus_ref = createTableMatrix(2);
			tableModAus_ref.setDataVector(tableMatrixAus_ref, tableNames2X_ref);
			tableModAus_ref.fireTableDataChanged();
			TableColumn colAus_ref = tableAus_ref.getColumnModel().getColumn(4);
			colAus_ref.setPreferredWidth(100);
			
		} else {
			for (int i=0; i<5; i++) {
				tableNames2X_ref[i] = calcMatrix_ref[5][i];
				tableNames2Y_ref[i] = calcMatrix_ref[6][i];
			} //endfor
			
			String[][] newDataVector_ref = new String[5][5];
			for (int i=0; i<5; i++) {
				newDataVector_ref[i][0] = tableNames2Y_ref[i];
				for (int j=0; j<4; j++) {
					newDataVector_ref[i][j+1] = calcMatrix_ref[i][j];
				} //endfor
			} //endfor
			
			tableModAus_ref.setDataVector(newDataVector_ref, tableNames2X_ref);
			tableModAus_ref.fireTableDataChanged();
			TableColumn colAus_ref = tableAus_ref.getColumnModel().getColumn(4);
			colAus_ref.setPreferredWidth(100);
			
			for (int i=0; i<5; i++) {
				if (tableNames2X_ref[i].equals("x")) {
					xT_ref.setText("0.000");
				} //endif
				if (tableNames2X_ref[i].equals("y")) {
					yT_ref.setText("0.000");
				} //endif
				if (tableNames2X_ref[i].equals("z")) {
					zT_ref.setText("0.000");
				} //endif
				
				if (tableNames2Y_ref[i].equals("x")) {
					xT_ref.setText(calcMatrix_ref[i][3]);
				} //endif
				if (tableNames2Y_ref[i].equals("y")) {
					yT_ref.setText(calcMatrix_ref[i][3]);
				} //endif
				if (tableNames2Y_ref[i].equals("z")) {
					zT_ref.setText(calcMatrix_ref[i][3]);
				} //endif
			} //endfor
			gT_ref.setText("-" + calcMatrix_ref[0][3]);
			JOptionPane.showMessageDialog(frame_ref, "Die Berechnung dauerte: " + mainC_ref.getTakenTime() + " Nanosekunden", "Info", JOptionPane.INFORMATION_MESSAGE);
			mainC_ref.resetTakenTime();
		} //endif
	} //endmethod callCalculate() 
	
	/**
	 * Setzt alle GUI-Tabellen wieder auf den Ursprungszustand zurueck
	 */
	private void doReset() {
		tableMatrixEin_ref = createTableMatrix(1);
		tableMatrixAus_ref = createTableMatrix(2);
		tableModEin_ref.setDataVector(tableMatrixEin_ref, tableNames1X_ref);
		tableModAus_ref.setDataVector(tableMatrixAus_ref, tableNames2X_ref);
		tableModEin_ref.fireTableDataChanged();
		tableModAus_ref.fireTableDataChanged();
		TableColumn colEin_ref = tableEin_ref.getColumnModel().getColumn(4);
		TableColumn colAus_ref = tableAus_ref.getColumnModel().getColumn(4);
		colEin_ref.setPreferredWidth(100);
		colAus_ref.setPreferredWidth(100);
	} //endmethod doReset
	
	/**
	 * Ueberprueft die Eingabewerte in der GUI-Tabelle und ruft, wenn alles ok ist, 
	 * die callCalculate()-Methode auf.
	 * 
	 * @see #callCalculate()
	 */
	private void doCalc() {
		boolean ok = true;
		for (int i=0; i<5; i++) {
			for (int j=1; j<5; j++) {
				try {
					Double.parseDouble((String)(tableEin_ref.getModel().getValueAt(i, j)));
				} catch (Exception ex_ref) {
					ok = false;
				} //endtry
			} //endfor
		} //endfor 
		if (!ok) {
			JOptionPane.showMessageDialog(frame_ref,"Unzulaessige Eingabewerte!\nFalls Sie werte mit Komma verwenden, benutzen sie \".\" als Trennzeichen.", "Fehler", JOptionPane.ERROR_MESSAGE);
		} else if (ok) {
			callCalculate();
		} //endif
	} //endmethod doCalc
	
	// --- Listener
	/**
	 * Der Listener fuer das Menu.
	 * Moegliche Aktionen: Beenden, Berechnen, Reset und Info
	 * 
	 * @author Tobias Burkard
	 */
	class MenuListener implements ActionListener {
		public void actionPerformed (ActionEvent ev_ref) {
			if (ev_ref.getActionCommand().equals("Beenden")) {
				System.exit(0);
			} else if (ev_ref.getActionCommand().equals("Berechnen")) {
				doCalc();
			} else if (ev_ref.getActionCommand().equals("Reset")) {
				doReset();
			} else if (ev_ref.getActionCommand().equals("Info")) {
				drawInfo();
			} //endif
		} //endmethod actionPerformed
	} //endclass ActionListener

	/**
	 * Der Listener fuer alle Knoepfe
	 * Moegliche Aktionen: Reset, Berechnen
	 * 
	 * @author Tobias Burkard
	 */
	class ButtonListener implements ActionListener {
		public void actionPerformed (ActionEvent ev_ref) {
			if (ev_ref.getActionCommand().equals("Reset")) {
				doReset();
			} else if (ev_ref.getActionCommand().equals("Berechnen")) {
				doCalc();
			} else if (ev_ref.getActionCommand().equals("Schliessen")) {
				infoFrame_ref.dispose();
				infoFrame_ref = null;
			} //endif
		} //endmethod actionPerformed
	} //endclass ButtonListener

	// --- Zusaetzliche innere Klassen
	/**
	 * TableModel fuer die Eingangstabelle.
	 * Ein eigenes TableModel, um die GUI-Tabellen, ueber die durch Java
	 * gegebene Funktionalitaet hinaus, zu formatieren.
	 * 
	 * @author Tobias Burkard
	 */
	class MyTableModelEin extends DefaultTableModel {

		private static final long serialVersionUID = 100330981635722317L;
		
		public MyTableModelEin(Object[][] werte_ref, Object[] bezeichner_ref) {
			super(werte_ref, bezeichner_ref);
		} //endconstructor
		
		public boolean isCellEditable(int row, int column) {
			if (column > 0) {
				return true;
			} else {
				return false;
			} //endif
		} //endmethod isCellEditable
	} //endclass MyTableModelEin

	/**
	 * TableModel fuer die Ergebnistabelle.
	 * Ein eigenes TableModel, um die GUI-Tabellen, ueber die durch Java
	 * gegebene Funktionalitaet hinaus, zu formatieren.
	 * 
	 * @author Tobias Burkard
	 */
	class MyTableModelAus extends DefaultTableModel {

		private static final long serialVersionUID = 100440981635722317L;
		
		public MyTableModelAus(Object[][] werte_ref, Object[] bezeichner_ref) {
			super(werte_ref, bezeichner_ref);
		} //endconstructor
		
		public boolean isCellEditable(int row, int column) {
				return false;
		} //endmethod isCellEditable
	} //endclass MyTableModelAus

	/**
	 * TableRenderer fuer die GUI-Tabellen.
	 * Ein eigener TableRenderer, um die GUI-Tabellen, ueber die durch Java
	 * gegebene Funktionalitaet hinaus, zu formatieren.
	 * 
	 * @author Tobias Burkard
	 */
	class MyTableRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 100221981647722317L;
		
	    public Component getTableCellRendererComponent(JTable table,
	        Object value, boolean isSelected, boolean hasFocus,
	        int row, int column) {

	        @SuppressWarnings("unused")
			Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	        
			Font boldFont_ref = new Font (this.getFont().getName(), Font.BOLD, this.getFont().getSize());
			
	        if (column < 1) {
	            this.setBackground(new Color(237,237,237));
	            this.setFont(boldFont_ref);
	        } else {
	            this.setBackground(Color.WHITE);
	        } //endif
	        return this;
	    } //endmethod getTableCellRendererComponent
	    
	} //endclass MyTableCellRenderer
	
	private class MyTableEditor extends DefaultCellEditor {
		private static final long serialVersionUID = 199880983615777317L;
		
		JTextField tf_ref;

		MyTableEditor() {
			super(new JTextField());
		} //endconstructor

		public Component getTableCellEditorComponent(JTable table_ref, Object value_ref, boolean isSelected, int row, int column) {
			Component c_ref = super.getTableCellEditorComponent(table_ref, value_ref, isSelected, row, column);
			if (c_ref instanceof JTextField) {
				tf_ref = ((JTextField)c_ref);
				tf_ref.selectAll();
				tf_ref.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
			} //endif

			return c_ref;
		} //endmethod getTableCellEditor
	} //endclass MyTableEditor
} //endclass SimplexGUI