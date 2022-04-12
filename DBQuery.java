/*
DBQuery
Class with static methods to provide access to SQL query strings
A seperate class with a 1-item array is obviously overkill here, 
but this structure is useful when one has many queries and simplifies code in other classes
DPL 11.09.14
 */

public class DBQuery 
{

	//-------------------------- PARAMETERIZED QUERIES -------------------------//
	final static String name0 = "BONDS_OF_ORDER_N";
	
	final static String name1 = "INSERT_TO_TABLE";
	
	final static String name2 = "ADD_LIPINSKI";
	
	final static String name3 = "DB_ADD_LIPINSKI";
	
	final static String name4 = "ADD_BIO";
	
	final static String name5 = "DELETE_ROW";
	
//	final static String name1 = "SHOW_TABLE";

	// Query for bonds of a particular order
	final static String query0 = "SELECT a1.CompoundID, b.bondNumber, a1.AtomNumber, a1.AtomType, "
			+ "a2.AtomNumber AS BondAtom2, a2.AtomType AS AtomType2 " 
			+ "FROM Atom a1, Atom a2, Bond b "
			+ "WHERE a1.CompoundID = b.CompoundID "
			+ "AND a2.CompoundID = b.CompoundID "
			+ "AND a1.AtomNumber = b.Atom1Number "
			+ "AND a2.AtomNumber = b.Atom2Number "
			+ "AND b.BondOrder = ? ";

//	final static String query1 = "SELECT * FROM BOND";
	
	final static String query1 = "INSERT INTO chemDB(ID, Name, IUPAC, Acceptor, Donor, Rotatable, Ring, Mass, LogP, Smile, LogD, PSA, FusedRing) "
			+ "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	
	final static String query2 = "UPDATE chemDB SET Lipinski = true WHERE ID = ?;";
	
	final static String query3 = "UPDATE chemDB SET Lipinski = true WHERE Mass <= ? "
			+ "AND LogP <= ? AND Donor <= ? AND Acceptor <= ?;";
	
	final static String query4 = "UPDATE chemDB SET Bioavail = true WHERE ID = ?;";
	
	final static String query5 = "DELETE FROM ChemDB WHERE ID = ?";


	// creates an array of all ParamQuerys - only one here, but would generally be several
	static ParamQuery pqList[] = 
			{ new ParamQuery(name0, query0), new ParamQuery(name1, query1), new ParamQuery(name2, query2), 
					new ParamQuery(name3, query3), new ParamQuery(name4, query4), new ParamQuery(name5, query5)
			};
			

	// find ParamQuery object by queryName and returns
	public static ParamQuery getParamQuery(String name) 
	{
		for (int i = 0; i < pqList.length; i++) 
		{
			if (pqList[i].getQueryName().equals(name)) 
			{
				return pqList[i];
			}
		}
		return null;
	}
			

	//----------------------- SIMPLE QUERIES W/O PARAMETERS ----------------------//

	// Query to find number of Compounds	
	static String compNumQuery = "SELECT COUNT(*) FROM Compound ";
	
	static String showTable = "SELECT BondNumber, Atom1number, Atom2number FROM Bond";
	
	static String showChemDB = "SELECT ID, Name, IUPAC, Acceptor, Donor, Rotatable, Ring, Mass, "
								+ "LogP, Smile FROM chemDB;";
	
	static String showFilters = "SELECT ID, Name, IUPAC, Acceptor, Donor, Rotatable, Ring, Mass, "
			+ "LogP, Smile, Lipinski, Bioavail, LeadLikeness FROM chemDB;"; 
	
	static String createDB = "CREATE TABLE IF NOT EXISTS chemDB\n"
			+ "	(ID INT NOT NULL,"
			+ "	Name VARCHAR(120), "
			+ "	IUPAC VARCHAR(400),"
			+ "	Acceptor INT, "
			+ "	Donor INT, "
			+ "	Rotatable INT, "
			+ "	Ring INT, "
			+ "	Mass DOUBLE, "
			+ "	LogP DOUBLE, "
			+ "	Smile VARCHAR(250), "
			+ "	LogD DOUBLE, "
			+ "	PSA DOUBLE, "
			+ "	FusedRing INT, "
			+ "	Lipinski BOOLEAN, "
			+ "	Bioavail BOOLEAN, "
			+ "	LeadLikeness BOOLEAN,"
			+ "PRIMARY KEY(ID))";
	
	
	static String filterLipinski = "SELECT  ID, Name, IUPAC, Acceptor, Donor, Rotatable, Ring, Mass, "
					+"LogP, Smile FROM chemDB WHERE Lipinski = true;";
	
	static String filterLead = "SELECT  ID, Name, IUPAC, Acceptor, Donor, Rotatable, Ring, Mass, "
			+"LogP, Smile FROM chemDB WHERE LeadLikeness = true;";
	
	static String filterBio = "SELECT  ID, Name, IUPAC, Acceptor, Donor, Rotatable, Ring, Mass, "
			+"LogP, Smile FROM chemDB WHERE Bioavail = true;";
	
	static String deleteTable = "DROP TABLE IF EXISTS chemDB;";
	
	
	static String addLead = "UPDATE chemDB SET LeadLikeness = true WHERE Mass <= 450 "
			+ "AND LogD <= 5 AND Ring <= 4 AND Rotatable <= 10 AND Donor <= 5 AND Acceptor <=5;";
	
	public static String getCompNumQuery()
	{
		return compNumQuery;
	}
	
	public static String getShowTable() {
		return showTable;
	}
	
	
	public static String getCreateDB() {
		return createDB;
	}
	
	public static String getChemDB() {
		return showChemDB;
	}
	
	public static String getShowFilters() {
		return showFilters;
	}
	
	public static String getFilterLipinski() {
		return filterLipinski;
	}
	
	public static String getDeleteTable() {
		return deleteTable;
	}
	
	public static String getAddLead() {
		return addLead;
	}
	
	public static String getFilterLead() {
		return filterLead;
	}
	
	public static String getFilterBio() {
		return filterBio;
	}

}
