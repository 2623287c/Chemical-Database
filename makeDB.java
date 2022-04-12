
//Class to make DB SQL and format results

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class makeDB {
	private StringBuilder resultsBuilder; // Builder for accumulating output
	private Connect cnt;
	private Connection conn;

	public makeDB() {
		resultsBuilder = new StringBuilder();
		cnt = new Connect();
		conn = cnt.getConnection();
//		showTable();
//		closeConnection();

	}
	
	
	
	boolean createDB() {
		String query = DBQuery.getCreateDB();
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(query);
			return true;
		} catch (SQLException e) {
			System.out.println("SQL Exception: " + e.toString());
		}
		return false;
	}

	private void showTable() {
		String query = DBQuery.getShowTable();
		try {
			Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet resSet = stmt.executeQuery(query);

			while (resSet.next()) {
				int tableData = resSet.getInt("BondNumber");
				int tableData2 = resSet.getInt("Atom1number");

				System.out.println(tableData);
				System.out.println("atom: " + tableData2);

				resultsBuilder.append(tableData);	
			}
		} catch (SQLException e) {
			System.out.println("SQL Exception: " + e.toString());
		}
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
