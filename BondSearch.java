// Class to handle SQL bond searches and format results

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

import chemaxon.formats.MolFormatException;
import chemaxon.marvin.plugin.PluginException;

public class BondSearch {
	private StringBuilder resultsBuilder; // Builder for accumulating output
	private Connect cnt;
	private Connection conn;

	public BondSearch() {
		resultsBuilder = new StringBuilder();
		cnt = new Connect();
		conn = cnt.getConnection();
//		showTable();
//		closeConnection();

	}

	ResultSet showTable() {
		String query = DBQuery.getShowTable();
		try {
			Statement stmt = conn.createStatement();
			ResultSet resSet = stmt.executeQuery(query);
//			System.out.println("this");

			return resSet;
//			while (resSet.next()) {
//				int tableData = resSet.getInt("BondNumber");
//				int tableData2 = resSet.getInt("Atom1number");
//
//				System.out.println(tableData);
//				System.out.println("atom: " + tableData2);
//
//				resultsBuilder.append(tableData);	
//			}
		} catch (SQLException e) {
			System.out.println("SQL Exception: " + e.toString());
		}
		return null;
	}

	ResultSet showChemDB() {
		String query = DBQuery.getChemDB();
		try {
			Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet resSet = stmt.executeQuery(query);
		
			return resSet;

		} catch (SQLException e) {
			System.out.println("SQL Exception: " + e.toString());
		}
		return null;
	}

	void delete() {
		String query = DBQuery.getDeleteTable();
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(query);

		} catch (SQLException e) {
			System.out.println("SQL Exception: " + e.toString());
		}

	}

	void deleteRow() {
		try {
			String query = "DELETE_ROW";
			ParamQuery parQ = DBQuery.getParamQuery(query);
			parQ.setPrepStatement(conn);
			PreparedStatement stmt = parQ.getPrepStatement();
			for (int j: Calculations.getaDel()) {
				stmt.setInt(1, j);
				stmt.addBatch();
			}
			stmt.executeBatch();
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	void addLeadLikness() {
		String query = DBQuery.getAddLead();
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(query);

		} catch (SQLException e) {
			System.out.println("SQL Exception: " + e.toString());
		}
	}
	
	void addBio() {
		try {
			Calculations.bioavail();
			String query = "ADD_BIO";
			ParamQuery parQ = DBQuery.getParamQuery(query);

			parQ.setPrepStatement(conn);
			PreparedStatement stmt = parQ.getPrepStatement();
			
			for (int j : Calculations.getBioNu()) { //for each ID that passed Bioavailability 
				stmt.setInt(1, j);
				stmt.addBatch();
			}
			stmt.executeBatch();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	

	void addDBLipinski() {
		try {
			String query = "DB_ADD_LIPINSKI";
			ParamQuery parQ = DBQuery.getParamQuery(query);
			int lMass = Integer
					.parseInt(JOptionPane.showInputDialog(null, "Please enter a mass for Lipisnki filtering"));
			int lLogP = Integer
					.parseInt(JOptionPane.showInputDialog(null, "Please enter a LogP for Lipisnki filtering"));
			int lDonor = Integer
					.parseInt(JOptionPane.showInputDialog(null, "Please enter a donor for Lipisnki filtering"));
			int lAcceptor = Integer
					.parseInt(JOptionPane.showInputDialog(null, "Please enter an acceptor for Lipisnki filtering"));

			parQ.setPrepStatement(conn);
			PreparedStatement stmt = parQ.getPrepStatement();
			stmt.setInt(1, lMass);
			stmt.setInt(2, lLogP);
			stmt.setInt(3, lDonor);
			stmt.setInt(4, lAcceptor);

			stmt.executeUpdate();
			stmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	void insert() {

		String query = "INSERT_TO_TABLE";
		ParamQuery parQ = DBQuery.getParamQuery(query);

		try {
			parQ.setPrepStatement(conn);
			PreparedStatement stmt = parQ.getPrepStatement();

			for (int i = 0; i < Calculations.getSize(); i++) { //adaptable for if there are more than 100 compounds - gets the number of compounds chosen by the user previously 
				stmt.setInt(1, i);
				stmt.setString(2, Calculations.getaName()[i]);
				stmt.setString(3, Calculations.getaIUPAC()[i]);
				stmt.setInt(4, Calculations.getaAcceptor()[i]);
				stmt.setInt(5, Calculations.getaDonor()[i]);
				stmt.setInt(6, Calculations.getaRot()[i]);
				stmt.setInt(7, Calculations.getaRing()[i]);
				stmt.setDouble(8, Calculations.getaMass()[i]);
				stmt.setDouble(9, Calculations.getaLogP()[i]);
				stmt.setString(10, Calculations.getaSmile()[i]);
				stmt.setDouble(11, Calculations.getaLogD()[i]);
				stmt.setDouble(12, Calculations.getaPSA()[i]);
				stmt.setInt(13, Calculations.getaFu()[i]);
				stmt.addBatch();
			}

			stmt.executeBatch();
		} catch (SQLException e) {
			System.out.println("SQL Exception: " + e.toString());
		}
	}

	ResultSet filterLipinski() {
		String query = DBQuery.getFilterLipinski();
		try {
			Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet resSet = stmt.executeQuery(query);
			return resSet;

		} catch (SQLException e) {
			System.out.println("SQL Exception: " + e.toString());
		}
		return null;
	}

	ResultSet filterLead() {
		String query = DBQuery.getFilterLead();
		try {
			Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet resSet = stmt.executeQuery(query);
			return resSet;

		} catch (SQLException e) {
			System.out.println("SQL Exception: " + e.toString());
		}
		return null;
	}
	
	
	ResultSet filterBio() {
		String query = DBQuery.getFilterBio();
		try {
			Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet resSet = stmt.executeQuery(query);
			return resSet;

		} catch (SQLException e) {
			System.out.println("SQL Exception: " + e.toString());
		}
		return null;
	}

	ResultSet showFilters() {
		String query = DBQuery.getShowFilters();
		try {
			Statement stmt = conn.createStatement();
			ResultSet resSet = stmt.executeQuery(query);

			return resSet;

		} catch (SQLException e) {
			System.out.println("SQL Exception: " + e.toString());
		}
		return null;
	}

	// close the connection
	private void closeConnection() {
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {
				System.out.println("Can't close.");
			}
		}
	}

	public String getResult() {
		System.out.println(resultsBuilder.toString());
		return resultsBuilder.toString();
	}
}
