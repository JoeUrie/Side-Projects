/*
 * This project was designed for private use only.
 * Written by: Joseph Urie
 */

//imports
import java.io.*;

public class Dose {
	
	//Globals
	@SuppressWarnings("unused")
	private static String patientName;
	private static double rate; //The Patient's current Agratroban/Bivalirudin rate
	private static int aPTT; //The Patient's current 
	private static double targetDosage; //The adjusted A/B rate after aPTT draw
	
	private static BufferedReader readIn = 
            new BufferedReader(new InputStreamReader(System.in));
	
	//Methods
	private static double adjustment (int aPTT, double rate) {
		double newDosage=0.0;
		if (aPTT<35) {
			newDosage=(rate*1.5); }
		if (aPTT>=35&&aPTT<45) {
			newDosage=(rate*1.25); }
		if (aPTT>=45&&aPTT<50) {
			newDosage=(rate*1.1); }
		if (aPTT>=50&&aPTT<81) {
			newDosage=rate;	} //Goal aPTT levels
		if (aPTT>=81&&aPTT<91) {
			newDosage=(rate*0.9); }
		if (aPTT>=91&&aPTT<101) {
			newDosage=(rate*0.75); }
		if (aPTT>=101&&aPTT<=168) {
			newDosage=(rate*0.5); }
		if (aPTT>169) {
			newDosage=-1.0;	}
		
		return newDosage;
	}
	
	//Argatroban and Bivalirudin Dose Adjustment
	private static void dosageAdjustment() throws NumberFormatException, IOException {
		System.out.println("What is the patient's current argatroban/bivalirudin rate? (mcg/kg/min)");
		rate = Double.parseDouble(readIn.readLine());
		if (rate>10) System.out.println("WARNING: Patient is exceeding a 10 (mcg/kg/min) dosage! Please try again!");
		else {
			System.out.println("What is the patient's current aPTT?");
			aPTT = Integer.parseInt(readIn.readLine());
			targetDosage = adjustment(aPTT, rate);
			double change = targetDosage-rate;
			System.out.println("...Calculating...");
			if (targetDosage>10) System.out.println("WARNING: Patient will be exceeding 10 (mcg/kg/min) dosage! Please try again!");
			else {
				if (change>0) { System.out.println("Increase current patient's argatroban/bivalirudin rate by "
													+change+" (mcg/kg/min), making the total dosage "
													+targetDosage+" (mcg/kg/min)."); }
				if (change==0) { System.out.println("The patient is currently in the aPTT goal range."); }
				if (change<0) {
					if (aPTT>=81&&aPTT<91) {
						System.out.println("Decrease current patient's argatroban/bivalirudin rate by "
								+(-change)+" (mcg/kg/min), reducing the total dosage to "
								+targetDosage+" (mcg/kg/min)."); }
					if (aPTT>=91&&aPTT<101) {
						System.out.println("Hold the infusion for 1 hour.\n Decrease current patient's argatroban/bivalirudin rate by "
								+(-change)+" (mcg/kg/min), reducing the total dosage to "
								+targetDosage+" (mcg/kg/min)."); }
					if (aPTT>=101&&aPTT<=168) {
						System.out.println("Hold the infusion for 2 hours.\n Decrease current patient's argatroban/bivalirudin rate by "
								+(-change)+" (mcg/kg/min), reducing the total dosage to "
								+targetDosage+" (mcg/kg/min)."); }
					if (aPTT>169) { System.out.println("Re-draw aPTT (once) to confirm absence of contaminants AND hold infusion."); }
				}
			}
		}	
	}
	
	//Laboratory Changes
	private static void labChanges(int x) throws IOException {
		String response; 
		switch (x) {
		case 12: System.out.println("Are the current aPTT draws every 2 hours?");
				 response = readIn.readLine();
				 if (response.compareToIgnoreCase("yes")==0) System.out.println("Change aPTT draws to q4h.");
				 if (response.compareToIgnoreCase("no")==0) labChanges(13);
				 break;
		case 13: System.out.println("Is the aPTT in the goal range?");
				 response = readIn.readLine();
				 if (response.compareToIgnoreCase("yes")==0) labChanges(14);
				 if (response.compareToIgnoreCase("no")==0) System.out.println("Change/continue Q4 draws.");
				 break;
		case 14: System.out.println("Are the current aPTT draws every 4 hours?");
				 response = readIn.readLine();
				 if (response.compareToIgnoreCase("yes")==0) labChanges(15);
				 if (response.compareToIgnoreCase("no")==0) labChanges(17);
				 break;
		case 15: System.out.println("Is this the first consecutive aPTT within goal range?");
				 response = readIn.readLine();
				 if (response.compareToIgnoreCase("yes")==0) System.out.println("Re-draw aPTT in 4 hours.");
				 if (response.compareToIgnoreCase("no")==0) labChanges(16);
				 break;
		case 16: System.out.println("Is the the second consecutive aPTT within goal range?");
				 response = readIn.readLine();
				 if (response.compareToIgnoreCase("yes")==0) System.out.println("Change aPTT draws to qAM");
				 if (response.compareToIgnoreCase("no")==0) labChanges(13);
				 break;
		case 17: System.out.println("Are the current aPTT draws qAM?");
				 response = readIn.readLine();
				 if (response.compareToIgnoreCase("yes")==0) labChanges(18);
				 if (response.compareToIgnoreCase("no")==0) labChanges(15);
				 break;
		case 18: System.out.println("Is the current aPTT in goal range?");
				 response = readIn.readLine();
				 if (response.compareToIgnoreCase("yes")==0) labChanges(19);
				 if (response.compareToIgnoreCase("no")==0) {System.out.println("Change to Q4 draws."); labChanges(13);} 
				 break;
		case 19: System.out.println("Continue aPTT draws qAM.");
				 break;
		default: break;
		}
	}
	
	//Main
	public static void main (String[] argv) throws IOException {
		//PrintWriter writer = new PrintWriter("Report.txt", "UTF-8");
		System.out.println("Please enter Patient's name: ");
		patientName = readIn.readLine();
		dosageAdjustment();
		if (rate>10||targetDosage>10) System.out.println("WARNING: Patient is exceeding a 10 (mcg/kg/min) dosage! Please try again!");
		else labChanges(12);
	}
}