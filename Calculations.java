import java.awt.Image;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import chemaxon.formats.MolFormatException;
import chemaxon.formats.MolImporter;
import chemaxon.marvin.calculations.ElementalAnalyserPlugin;
import chemaxon.marvin.calculations.HBDAPlugin;
import chemaxon.marvin.calculations.IUPACNamingPlugin;
import chemaxon.marvin.calculations.MarkushEnumerationPlugin;
import chemaxon.marvin.calculations.TPSAPlugin;
import chemaxon.marvin.calculations.TopologyAnalyserPlugin;
import chemaxon.marvin.calculations.logDPlugin;
import chemaxon.marvin.calculations.logPPlugin;
import chemaxon.marvin.plugin.PluginException;
import chemaxon.struc.Molecule;

public class Calculations {

	static String nameFile;

	// creates arrays for each calculation. these are then uploaded to the database
	// later

	// to be able to use this for more compounds would enter the number of compounds
	// when prompted
	static int size = Integer.parseInt(JOptionPane.showInputDialog(null, "Please enter the number of compounds")); // allows
																													// the
																													// user
																													// to
																													// have
																													// more
																													// than
																													// 100
																													// compounds

	static String[] aIUPAC = new String[size];
	static String[] aName = new String[size];
	static int[] aAcceptor = new int[size];
	static int[] aDonor = new int[size];
	static int[] aRot = new int[size];
	static int[] aRing = new int[size];
	static double[] aMass = new double[size];
	static double[] aLogP = new double[size];
	static String[] aSmile = new String[size];
	static ImageIcon[] imageList = new ImageIcon[size];

	static double[] aLogD = new double[size];
	static double[] aPSA = new double[size];
	static int[] aFu = new int[size];

	static ArrayList<Integer> lipinskiNu = new ArrayList<Integer>();
	static ArrayList<Integer> bioNu = new ArrayList<Integer>();
	static ArrayList<Integer> leadNu = new ArrayList<Integer>();

	static ArrayList<Integer> aDel = new ArrayList<Integer>();

	public static void main(String args[]) throws MolFormatException, IOException, PluginException {

	}

	public Calculations() throws MolFormatException, IOException, PluginException {
		MolImporter mi;

		name(nameFile);
		IUPAC(mi = new MolImporter(nameFile));
		HBond(mi = new MolImporter(nameFile));
		RotRing(mi = new MolImporter(nameFile));
		mass(mi = new MolImporter(nameFile));
		calcLogP(mi = new MolImporter(nameFile));
		smile(mi = new MolImporter(nameFile));
		images(mi = new MolImporter(nameFile));
		calcLogD(mi = new MolImporter(nameFile));
		fuRing(mi = new MolImporter(nameFile));
		psa(mi = new MolImporter(nameFile));

	}

	public static String[] name(String nameFile) {
		int i = 0;
		try {
			String fN = nameFile;
			FileReader fr = new FileReader(fN);
			Scanner s = new Scanner(fr);
			String str;
			while (s.hasNextLine()) {
				if (s.nextLine().equals(">  <Name>")) {
					str = s.nextLine();
//					System.out.println(str);
					aName[i] = str;
					i++;
				}
			}
			return aName;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
		}

		return aName;
	}

	public static void IUPAC(MolImporter mi) throws IOException, PluginException {
		IUPACNamingPlugin plugin = new IUPACNamingPlugin();

		for (int i = 0; i < size; i++) {
			Molecule mol = mi.read();
//			System.out.println(i);

			// set the input molecule
			plugin.setMolecule(mol);
			// run the calculation
			plugin.run();
			// get results
			String preferredIUPACName = plugin.getPreferredIUPACName();
			// String traditionalName = plugin.getTraditionalName();

//			System.out.println(preferredIUPACName);
			aIUPAC[i] = preferredIUPACName;
			// System.out.println(traditionalName);

		}
	}

	public static void HBond(MolImporter mi) throws IOException, PluginException {
		HBDAPlugin plugin = new HBDAPlugin();
		for (int i = 0; i < size; i++) {
			Molecule mol = mi.read();
//			System.out.print(i);

			// set target molecule
			plugin.setMolecule(mol);

			// run the calculation
			plugin.run();
			// molecular data
			// with multiplicity
			int molecularAcceptorCount = plugin.getAcceptorCount();
			int molecularDonorCount = plugin.getDonorCount();
			// without multiplicity
			int molecularAcceptorAtomCount = plugin.getAcceptorAtomCount();
			int molecularDonorAtomCount = plugin.getDonorAtomCount();
//			System.out.println();
//			System.out.println("Acceptor count with multiplicity: " + molecularAcceptorCount);
//			System.out.println("Donor count with multiplicity: " + molecularDonorCount);
//			System.out.println("Acceptor count without multiplicity: " + molecularAcceptorAtomCount);
//			System.out.println("Donor count without multiplicity: " + molecularDonorAtomCount);

			aDonor[i] = molecularAcceptorCount;
			aAcceptor[i] = molecularDonorCount;
		}
	}

	public static void RotRing(MolImporter mi) throws IOException, PluginException {
		TopologyAnalyserPlugin plugin = new TopologyAnalyserPlugin();
		for (int i = 0; i < size; i++) {
			Molecule mol = mi.read();

			// set target molecule
			plugin.setMolecule(mol);

			// run the calculation
			plugin.run();

			// get molecular results
			int ringCount = plugin.getRingCount();
//			System.out.println("Ring count: " + ringCount);
			aRing[i] = ringCount;

			int rotatableBondCount = plugin.getRotatableBondCount();
//			System.out.println("Rotatable bond count: " + rotatableBondCount);
			aRot[i] = rotatableBondCount;

		}
	}

	public static void mass(MolImporter mi) throws PluginException, IOException {
		ElementalAnalyserPlugin plugin = new ElementalAnalyserPlugin();
		// set plugin parameters
		Properties params = new Properties();
		params.put("precision", "3");
		plugin.setParameters(params);

		for (int i = 0; i < size; i++) {
			Molecule mol = mi.read();

//		    // set plugin parameters
//		    Properties params = new Properties();
//		    params.put("precision", "3");
//		    plugin.setParameters(params);

			// set target molecule
			plugin.setMolecule(mol);

			// run the calculation
			plugin.run();

			// get results
			double exactMass = plugin.getExactMass();
			double mass = plugin.getMass();
//			System.out.println(mass);
			aMass[i] = mass;
		}
	}

	public static void calcLogP(MolImporter mi) throws PluginException, IOException {
		logPPlugin plugin = new logPPlugin();

		// fill parameters
		Properties params = new Properties();
		params.put("type", "logP");

		// set logP calculation method
		plugin.setlogPMethod(logPPlugin.METHOD_WEIGHTED);

		// set method weights
		plugin.setWeightOfMethods(1, 1, 1, 0);

		// set parameters
		plugin.setCloridIonConcentration(0.1);
		plugin.setNaKIonConcentration(0.1);

		// set result types
		plugin.setUserTypes("logPTrue,logPMicro");

		for (int i = 0; i < size; i++) {
			Molecule mol = mi.read();
			// set the input molecule
			plugin.setMolecule(mol);

			// run the calculation
			plugin.run();

			// get the overall logP value
			Double logP = plugin.getlogPTrue();

			if (logP.isNaN()) {
				aDel.add(i);
				logP = 0.0;
			}

			BigDecimal dc = new BigDecimal(logP);
			dc = dc.round(new MathContext(3)); // desired significant digits
			double rounded = dc.doubleValue();
			aLogP[i] = rounded;
		}
	}

	public static void calcLogD(MolImporter mi) throws PluginException, IOException {
		// instantiate plugin
		logDPlugin plugin = new logDPlugin();

		// set pH
		plugin.setpH(7.4);

		for (int i = 0; i < size; i++) {
			Molecule mol = mi.read();
			// set the input molecule
			plugin.setMolecule(mol);

			// run the calculation
			plugin.run();

			// get the overall logP value
			Double logD = plugin.getlogD();

			if (logD.isNaN()) {
				logD = 0.0;
			}
			aLogD[i] = logD;

			// print result
//			System.out.println("logD at pH 3.0: " + logD);
		}
	}

	public static void fuRing(MolImporter mi) throws PluginException, IOException {
		TopologyAnalyserPlugin plugin = new TopologyAnalyserPlugin();
		for (int i = 0; i < size; i++) {
			Molecule mol = mi.read();

			// set target molecule
			plugin.setMolecule(mol);

			// run the calculation
			plugin.run();

			// get molecular results
			int fu = plugin.getFusedAromaticRingCount();
//			System.out.println(fu);
			aFu[i] = fu;

		}
	}

	public static void psa(MolImporter mi) throws IOException, PluginException {

		// create plugin
		TPSAPlugin plugin = new TPSAPlugin();

		// optional: take major microspecies at pH=7.4
		// skip this if you want to calculate PSA for the input molecule as it is
		plugin.setpH(7.4);

		for (int i = 0; i < size; i++) {
			Molecule mol = mi.read();
			// set the input molecule
			// set target molecule
			plugin.setMolecule(mol);

			// run the calculation
			plugin.run();

			// get result
			double psa = plugin.getTPSA();

			aPSA[i] = psa;

			// print result
//			System.out.println("topological polar surface area (pH=7.4): " + psa);
		}
	}

	public static void smile(MolImporter mi) throws IOException, PluginException {
		MarkushEnumerationPlugin plugin = new MarkushEnumerationPlugin();
		for (int i = 0; i < size; i++) {
			Molecule mol = mi.read();

			// Set target molecule
			plugin.setMolecule(mol);

			// Run the calculation
			plugin.run();

			Molecule m = plugin.getNextStructure();
			while (m != null) {
				String s = m.toFormat("smiles");
//				System.out.println(s);
				aSmile[i] = s;
				// ...
				// Getting next enumerated molecule
				m = plugin.getNextStructure();
			}
		}
	}

	public static void images(MolImporter mi) throws IOException {
		for (int i = 0; i < size; i++) {
			Molecule mol = mi.read();
			Image img = (Image) mol.toObject("image:w70,h50");
			ImageIcon icon = new ImageIcon(img);
			imageList[i] = icon;
		}
	}

	// lipinski can also be calculated using the arrays - this program does it from
	// the database
	public static ArrayList<Integer> lipinski(int lMass, int lLogP, int lDonor, int lAcceptor) {
		for (int i = 0; i < size; i++) {
			if ((aMass[i] <= lMass) && (aLogP[i] <= lLogP) && (aDonor[i] <= lDonor) && (aAcceptor[i] <= lAcceptor)) {
//				System.out.println(i + "passes Lipinski");
				lipinskiNu.add(i);
			} else {
//				System.out.println(i + "doesn't pass Lipinski");
			}
		}
		return lipinskiNu;
	}

	// bioavailability is done from the array to show how this would be done
	public static ArrayList<Integer> bioavail() {
		for (int i = 0; i < size; i++) {
			int j = 0;
			if (aMass[i] <= 500) {
				j++;
			}
			if (aLogP[i] <= 5) {
				j++;
			}
			if (aDonor[i] <= 5) {
				j++;
			}
			if (aAcceptor[i] <= 10) {
				j++;
			}
			if (aRot[i] <= 10) {
				j++;
			}
			if (aPSA[i] <= 200) {
				j++;
			}
			if (aFu[i] <= 5) {
				j++;
			}
			if (j >= 6) {
//				System.out.println(i + " passes Bioavailability");
				bioNu.add(i);
			} else {
//				System.out.println(i + " doesn't pass Bioavailability");
			}
		}
		return bioNu;
	}

	// lead-likeness can also be calculated using the arrays - this program does it
	// from the database
	public static ArrayList<Integer> lead() {
		for (int i = 0; i < size; i++) {
			if ((aMass[i] <= 450) && (aLogD[i] >= -4) && (aLogD[i] <= 4) && (aRing[i] <= 4) && (aRot[i] <= 10)
					&& (aDonor[i] <= 5) && (aAcceptor[i] <= 8)) {
//				System.out.println(i + " passes lead-likeness");
				leadNu.add(i);
			} else {
//				System.out.println(i + " doesn't pass lead-likeness");
			}
		}
		return leadNu;

	}

	public static String[] getaIUPAC() {
		return aIUPAC;
	}

	public static void setaIUPAC(String[] aIUPAC) {
		Calculations.aIUPAC = aIUPAC;
	}

	public static String[] getaName() {
		return aName;
	}

	public static void setaName(String[] aName) {
		Calculations.aName = aName;
	}

	public static int[] getaAcceptor() {
		return aAcceptor;
	}

	public static void setaAcceptor(int[] aAcceptor) {
		Calculations.aAcceptor = aAcceptor;
	}

	public static int[] getaDonor() {
		return aDonor;
	}

	public static void setaDonor(int[] aDonor) {
		Calculations.aDonor = aDonor;
	}

	public static int[] getaRot() {
		return aRot;
	}

	public static void setaRot(int[] aRot) {
		Calculations.aRot = aRot;
	}

	public static int[] getaRing() {
		return aRing;
	}

	public static void setaRing(int[] aRing) {
		Calculations.aRing = aRing;
	}

	public static double[] getaMass() {
		return aMass;
	}

	public static void setaMass(double[] aMass) {
		Calculations.aMass = aMass;
	}

	public static double[] getaLogP() {
		return aLogP;
	}

	public static void setaLogP(double[] aLogP) {
		Calculations.aLogP = aLogP;
	}

	public static String[] getaSmile() {
		return aSmile;
	}

	public static void setaSmile(String[] aSmile) {
		Calculations.aSmile = aSmile;
	}

	public static ArrayList<Integer> getLipinskiNu() {
		return lipinskiNu;
	}

	public static void setLipinskiNu(ArrayList<Integer> lipinskiNu) {
		Calculations.lipinskiNu = lipinskiNu;
	}

	public static ImageIcon[] getImageList() {
		return imageList;
	}

	public static void setImageList(ImageIcon[] imageList) {
		Calculations.imageList = imageList;
	}

	public static String getNameFile() {
		return nameFile;
	}

	public static void setNameFile(String nameFile) {
		Calculations.nameFile = nameFile;
	}

	public static double[] getaLogD() {
		return aLogD;
	}

	public static void setaLogD(double[] aLogD) {
		Calculations.aLogD = aLogD;
	}

	public static double[] getaPSA() {
		return aPSA;
	}

	public static void setaPSA(double[] aPSA) {
		Calculations.aPSA = aPSA;
	}

	public static int[] getaFu() {
		return aFu;
	}

	public static void setaFu(int[] aFu) {
		Calculations.aFu = aFu;
	}

	public static ArrayList<Integer> getBioNu() {
		return bioNu;
	}

	public static void setBioNu(ArrayList<Integer> bioNu) {
		Calculations.bioNu = bioNu;
	}

	public static ArrayList<Integer> getLeadNu() {
		return leadNu;
	}

	public static void setLeadNu(ArrayList<Integer> leadNu) {
		Calculations.leadNu = leadNu;
	}

	public static ArrayList<Integer> getaDel() {
		return aDel;
	}

	public static void setaDel(ArrayList<Integer> aDel) {
		Calculations.aDel = aDel;
	}

	public static int getSize() {
		return size;
	}

	public static void setSize(int size) {
		Calculations.size = size;
	}

}
