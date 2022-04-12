/*
	Simple GUI-based app to illustrate direct JDBC connection using StructureDB
	David P Leader 
	last update: 11.10.2016 (Generic JComboBox version for Java 7)
*/

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.Element;
import javax.swing.text.html.ImageView;

import chemaxon.formats.MolImporter;
import chemaxon.marvin.beans.MSketchPane;
import chemaxon.marvin.plugin.PluginException;
import chemaxon.struc.Molecule;

import java.sql.Connection;

@SuppressWarnings("serial")
public class SDBAccess extends JFrame implements ActionListener {
	// GUI components that need to be instance variables
	private JTextArea feedback;

	private JTable j;
	private JButton DButton, bCalc, bUpload, bShow, bDel;
	private JButton bWithFilters, bFile, bFilter;
	private JButton bShow1;
	private JPanel pCentre;
	private JScrollPane dataPane;
	private JComboBox filterList;

	private JMenuItem openFile;

	private Calculations calc;
	private Image img;
	private String directory;

	public SDBAccess() {
		this.setLayout(new BorderLayout(10, 10));
		setTitle("Chemical Compound Database");
		setLocation(20, 20);
		setSize(500, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setup();
	}

	// main method is placed within the GUI class to avoid surplus starter class
	public static void main(String args[]) {
		JFrame jf = new SDBAccess();
		jf.pack();
		jf.setVisible(true);
	}

	// set up GUI and initialize
	void setup() {
		/*--- North panel for JCombo box ---*/
		JPanel pNorth = new JPanel();
		add("North", pNorth);

		JLabel lcat = new JLabel("Database ", JLabel.LEFT);
		lcat.setForeground(new Color(121, 123, 125));
		pNorth.add(lcat);
		Color c = new Color(197, 215, 217);
		pNorth.setBackground(c);

		/*--- Center panel for text area ---*/
		pCentre = new JPanel();
		add("Center", pCentre);
		// Feedback text area
		feedback = new JTextArea(33, 100);
		feedback.setMargin(new Insets(10, 10, 10, 10)); // Aesthetic stand-off for text
		dataPane = new JScrollPane(feedback);
		pCentre.add(dataPane);
		pCentre.setBackground(c);

		/*--- South panel for buttons ---*/
		JPanel pSth = new JPanel();
		add("South", pSth);
		// Reset button

		Color color = new Color(178, 209, 219);

		bDel = new JButton("Start");
		bDel.addActionListener(this);
		bDel.setBackground(new Color(247, 186, 186));
		pSth.add(bDel);

		bCalc = new JButton("calculate");
		bCalc.addActionListener(this);
		bCalc.setBackground(color);
		bCalc.setVisible(false);
		bCalc.setEnabled(false);
		pSth.add(bCalc);

		DButton = new JButton("create the database");
		DButton.addActionListener(this);
		DButton.setVisible(false);
		DButton.setEnabled(false);
		DButton.setBackground(color);
		pSth.add(DButton);

		bUpload = new JButton("Upload to Database");
		bUpload.addActionListener(this);
		bUpload.setBackground(color);
		bUpload.setEnabled(false);
		bUpload.setVisible(false);
		pSth.add(bUpload);

		bShow = new JButton("show table without filters");
		bShow.addActionListener(this);
		bShow.setVisible(false);
		bShow.setBackground(color);
		pSth.add(bShow);

//		color = new Color(178, 209, 219);

		bFilter = new JButton("filter");
		bFilter.addActionListener(this);
		bFilter.setVisible(false);
		bFilter.setBackground(color);
		pSth.add(bFilter);

		bWithFilters = new JButton("table with filters");
		bWithFilters.addActionListener(this);
		bWithFilters.setVisible(false);
		bWithFilters.setBackground(color);
		pSth.add(bWithFilters);

		String[] petStrings = { "Bioavailability", "Lead-likeness", "Lipinski" };
		filterList = new JComboBox(petStrings);
		filterList.setSelectedIndex(2);
		filterList.addActionListener(this);
		filterList.setVisible(false);
		pSth.add(filterList);

		bShow1 = new JButton("show table of filter selected");
		bShow1.addActionListener(this);
		bShow1.setVisible(false);
		bShow1.setBackground(color);
		pSth.add(bShow1);

		bFile = new JButton("Save selected filter to file");
		bFile.addActionListener(this);
		bFile.setVisible(false);
		bFile.setBackground(new Color(200, 219, 204));
		pSth.add(bFile);

		// ------------------- Menus ---------------------//
		JMenuBar menuBar = new JMenuBar();

		/* --- File menu --- */
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);

		openFile = new JMenuItem("Open .sd File");
		openFile.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		openFile.addActionListener(this);
		openFile.setEnabled(true);
		fileMenu.add(openFile);
		fileMenu.addSeparator();
		setJMenuBar(menuBar);
	}

	// event-handling method for button-press
	public void actionPerformed(ActionEvent event) {

		if (event.getSource() == bDel) {
			//deletes the old table 
			BondSearch bs = new BondSearch();
			bs.delete();
			bDel.setVisible(false);
			
			
			//buttons to calculate and make/populate database visible now
			bCalc.setVisible(true);
			DButton.setVisible(true);
			bUpload.setVisible(true);

		} else if (event.getSource() == bCalc) {
			//runs the calculations 
			feedback.append("\n \n Calculating...");
			try {
				calc = new Calculations();

				bCalc.setEnabled(false);
				DButton.setEnabled(true);

				feedback.append("\n Calculated!");
			} catch (IOException | PluginException e) {
				e.printStackTrace();
			}

		} else if (event.getSource() == DButton) {
			//creates the database
			feedback.append("\n \n Creating the database...");
			makeDB mdb = new makeDB();
			if (mdb.createDB()) {
				feedback.append("\n Database is created!");
			} else {
				feedback.append("\n Database not created! \n");
			}
			DButton.setEnabled(false);
			bUpload.setEnabled(true);

		} else if (event.getSource() == bUpload) {
			//uploads the data to the database 
			feedback.append("\n \n Uploading data to databse");

			BondSearch bs = new BondSearch();
			bUpload.setEnabled(false);
			bs.insert(); 
			bs.deleteRow(); //deletes the rows with logP and logD NaN
			
			feedback.append("\n Data has been uploaded! \n");


			bUpload.setVisible(false);
			DButton.setVisible(false);
			bCalc.setVisible(false);

			bFilter.setVisible(true);

		} else if (event.getSource() == bShow) {
			//shows the database in a Jtable from a result set from BondSearch using the query from DBuery 
			Object rowData[][] = { { "Row1-Column1", "Row1-Column2", "Row1-Column3", "Row1-Column4", "Row1-Column5",
					"Row1-Column6", "Row1-Column7", "Row1-Column8", "Row1-Column9", "Row1-Column10", "Row1-Column11"} };
			Object columnNames[] = { "ID", "Name", "IUPAC", "H Bond Acceptors", "H Bond Donors", "Rotatable bonds",
					"Ring Count", "Mass", "LogP", "Smile", "Image" };
			DefaultTableModel mTableModel = new DefaultTableModel(rowData, columnNames);
			JTable table = new JTable(mTableModel) {
				public Class getColumnClass(int column) {
					return (column == 10) ? Icon.class : Object.class; //to show the image 
				}
			};
			table.setAutoCreateRowSorter(true);
			table.setRowHeight(50);
			table.getColumnModel().getColumn(0).setPreferredWidth(1);
			table.getColumnModel().getColumn(7).setPreferredWidth(1);
			table.getColumnModel().getColumn(8).setPreferredWidth(1);

			dataPane.setViewportView(table);
			mTableModel.removeRow(0);
			Object[] rows;

			BondSearch bs = new BondSearch();
			ResultSet rs = bs.showChemDB();
			int i = 0;
			try {
				while (rs.next()) {
					rows = new Object[] { rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4), rs.getInt(5),
							rs.getInt(6), rs.getInt(7), rs.getDouble(8), rs.getDouble(9), rs.getString(10), calc.getImageList()[rs.getInt(1)] };
					i++;
					mTableModel.addRow(rows);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

		} else if (event.getSource() == bFilter) {
			//runs the filter code and changes to true in the database when the filter is true
		
			bFilter.setVisible(false);
			bWithFilters.setVisible(true);
			BondSearch bs = new BondSearch();
			bs.addLeadLikness();
			bs.addDBLipinski();
			bs.addBio();

			bShow.setVisible(true);
			bShow1.setVisible(true);
			filterList.setVisible(true);
			bFile.setVisible(true);

		} else if (event.getSource() == bWithFilters) {
			//shows table from database with the filters ticked if they are true
			Object rowData[][] = { { "Row1-Column1", "Row1-Column2", "Row1-Column3", "Row1-Column4", "Row1-Column5",
					"Row1-Column6", "Row1-Column7", "Row1-Column8", "Row1-Column9", "Row1-Column10", "Row1-Column11",
					"Row1-Column12", "Row1-Column13", "Row1-Column14" } };
			Object columnNames[] = { "ID", "Name", "IUPAC", "H Bond Acceptors", "H Bond Donors", "Rotatable bonds",
					"Ring Count", "Mass", "LogP", "Smile", "Lipinski", "Bioavailability", "Lead-Likeness", "Image" };
			DefaultTableModel mTableModel = new DefaultTableModel(rowData, columnNames);
			JTable table = new JTable(mTableModel) {
				public Class getColumnClass(int column) { //for ticks for booleans and so can show images in table 
					if (column == 10) {
						return Boolean.class;
					}
					if (column == 11) {
						return Boolean.class;
					}
					if (column == 12) {
						return Boolean.class;
					}
					return (column == 13) ? Icon.class : Object.class;
				}
			};
			table.setAutoCreateRowSorter(true); //to sort rows 
			table.setRowHeight(50);
			table.getColumnModel().getColumn(0).setPreferredWidth(1);
			dataPane.setViewportView(table);
			mTableModel.removeRow(0); //removes temporary row 
			Object[] rows;

			BondSearch bs = new BondSearch();
			ResultSet rs = bs.showFilters();
			int i = 0;
			try {
				while (rs.next()) {
					rows = new Object[] { rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4), rs.getInt(5),
							rs.getInt(6), rs.getInt(7), rs.getDouble(8), rs.getDouble(9), rs.getString(10),
							Boolean.valueOf(rs.getBoolean(11)), Boolean.valueOf(rs.getBoolean(12)),
							Boolean.valueOf(rs.getBoolean(13)), calc.getImageList()[rs.getInt(1)] };
					i++;
					mTableModel.addRow(rows);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else if (event.getSource() == bShow1) {
			//shows all that are true for the selected filter 
			BondSearch bs = new BondSearch();
			Object rowData[][] = {
					{ "Row1-Column1", "Row1-Column2", "Row1-Column3", "Row1-Column4", "Row1-Column5", "Row1-Column6",
							"Row1-Column7", "Row1-Column8", "Row1-Column9", "Row1-Column10", "Row1-Column11" } };
			Object columnNames[] = { "ID", "Name", "IUPAC", "H Bond Acceptors", "H Bond Donors", "Rotatable bonds",
					"Ring Count", "Mass", "LogP", "Smile", "Image" };
			DefaultTableModel mTableModel = new DefaultTableModel(rowData, columnNames);
			JTable table = new JTable(mTableModel) {
				public Class getColumnClass(int column) {
					return (column == 10) ? Icon.class : Object.class;
				}
			};
			table.setRowHeight(50);
			table.getColumnModel().getColumn(0).setPreferredWidth(1);
			table.getColumnModel().getColumn(7).setPreferredWidth(1);
			table.getColumnModel().getColumn(8).setPreferredWidth(1);
			dataPane.setViewportView(table);
			mTableModel.removeRow(0);
			table.setAutoCreateRowSorter(true);
			Object[] rows;

			String x = String.valueOf(filterList.getSelectedItem()); //gets the selected filter from the JComboBox
			if (x == "Bioavailability") {
				ResultSet rs = bs.filterBio();
				int i = 0;
				try {
					while (rs.next()) {
						rows = new Object[] { rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4),
								rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getDouble(8), rs.getDouble(9),
								rs.getString(10), calc.getImageList()[rs.getInt(1)] };
						i++;
						mTableModel.addRow(rows);
					}

				} catch (SQLException e) {
					e.printStackTrace();
				}

			} else if (x == "Lead-likeness") {
				ResultSet rs = bs.filterLead();
				;
				int i = 0;
				try {
					while (rs.next()) {
						rows = new Object[] { rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4),
								rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getDouble(8), rs.getDouble(9),
								rs.getString(10), calc.getImageList()[rs.getInt(1)] };
						i++;
						mTableModel.addRow(rows);
					}

				} catch (SQLException e) {
					e.printStackTrace();
				}

			} else if (x == "Lipinski") {
				ResultSet rs = bs.filterLipinski();
				int i = 0;
				try {
					while (rs.next()) {
						rows = new Object[] { rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4),
								rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getDouble(8), rs.getDouble(9),
								rs.getString(10), calc.getImageList()[rs.getInt(1)] };
						i++;
						mTableModel.addRow(rows);
					}

				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} else if (event.getSource() == bFile) {
			//prints for the selected filter and also shows the table on the screen 
			BondSearch bs = new BondSearch();
			Object rowData[][] = {
					{ "Row1-Column1", "Row1-Column2", "Row1-Column3", "Row1-Column4", "Row1-Column5", "Row1-Column6",
							"Row1-Column7", "Row1-Column8", "Row1-Column9", "Row1-Column10", "Row1-Column12" } };
			Object columnNames[] = { "ID", "Name", "IUPAC", "H Bond Acceptors", "H Bond Donors", "Rotatable bonds",
					"Ring Count", "Mass", "LogP", "Smile", "Image" };
			DefaultTableModel mTableModel = new DefaultTableModel(rowData, columnNames);
			JTable table = new JTable(mTableModel) {
				public Class getColumnClass(int column) {
					return (column == 10) ? Icon.class : Object.class;
				}
			};
			table.setRowHeight(50);
			table.getColumnModel().getColumn(0).setPreferredWidth(1);
			table.getColumnModel().getColumn(7).setPreferredWidth(1);
			table.getColumnModel().getColumn(8).setPreferredWidth(1);
			dataPane.setViewportView(table);
			mTableModel.removeRow(0);
			table.setAutoCreateRowSorter(true);
			Object[] rows;
			
		
			String x = String.valueOf(filterList.getSelectedItem()); //gets the selected filter from the JComboBox
			if (x == "Bioavailability") {
				ResultSet rs = bs.filterBio();
				;
				int i = 0;
				try {
					while (rs.next()) {
						rows = new Object[] { rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4),
								rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getDouble(8), rs.getDouble(9),
								rs.getString(10), calc.getImageList()[rs.getInt(1)] };
						i++;
						mTableModel.addRow(rows);
					}

				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else if (x == "Lead-likeness") {
				ResultSet rs = bs.filterLead();
				;
				int i = 0;
				try {
					while (rs.next()) {
						rows = new Object[] { rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4),
								rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getDouble(8), rs.getDouble(9),
								rs.getString(10), calc.getImageList()[rs.getInt(1)] };
						i++;
						mTableModel.addRow(rows);
					}

				} catch (SQLException e) {
					e.printStackTrace();
				}

			} else if (x == "Lipinski") {
				ResultSet rs = bs.filterLipinski();
				int i = 0;
				try {
					while (rs.next()) {
						rows = new Object[] { rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4),
								rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getDouble(8), rs.getDouble(9),
								rs.getString(10), calc.getImageList()[rs.getInt(1)] };
						i++;
						mTableModel.addRow(rows);
					}

				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			//to print to file 
			FileWriter fw;
			try {
				fw = new FileWriter(directory + x + ".txt"); //file named after the selected filter(x)
				BufferedWriter bw = new BufferedWriter(fw);
				String line = "";
				for (int row = 0; row < table.getRowCount(); row++) {
					for (int col = 0; col < table.getColumnCount(); col++) {
						line += table.getColumnName(col);
						line += ": ";
						line += table.getValueAt(row, col);
						line += "\n";
					}
				}
				bw.write(line);
				bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else if (event.getSource() == openFile) {
			//to load the selected file when click on openFile in menu
			LoadFile ldfi = new LoadFile(this);
			Scanner scanner = ldfi.getScanner();
			directory = ldfi.getDirectory(); //to save the file later in the same directory - so program can be used on different computers 
			if (scanner != null) {
				// get original directory stuff
				String fileName = ldfi.getDirectory() + ldfi.getFileName();
				feedback.append("\n Opening " + fileName);
				feedback.append("\n Done!");
				Calculations.setNameFile(fileName);
				bCalc.setEnabled(true);
			}

		}

	}

}
