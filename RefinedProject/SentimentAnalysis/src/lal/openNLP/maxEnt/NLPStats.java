package lal.openNLP.maxEnt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;

public class NLPStats {
	
	/**
	 * Here 1: is termed as Negative
	 * Here 0: is termed as Positive
	 * ========================================
	 * Considering class Appropriate as subject
	 * ---------------------------------------------------------------------------------------------------
	 * Predicted/Actual					| Actual Correct(Appropriate 0)	| Actual Incorrect(Inappropriate 1)
	 * --------------------------------------------------------------------------------------------------
	 * Selected(Appropriate 0)			|	true Positive				| false Positive	
	 * Not Selected(Inappropriate 1)	|	false Negative				| true negative
	 * ---------------------------------------------------------------------------------------------------
	 * Precision = (true Positive)/(true Positive + false Positive)
	 * Recall = (true Positive)/(true Positive + false Negative)
	 * 
	 * ========================================
	 * Considering class InAppropriate as subject
	 * ---------------------------------------------------------------------------------------------------
	 * Predicted/Actual					| Actual Correct(InAppropriate 1)| Actual Incorrect(Appropriate 0)
	 * --------------------------------------------------------------------------------------------------
	 * Selected(InAppropriate 1)		|	true Positive				| false Positive	
	 * Not Selected(Appropriate 0)		|	false Negative				| true negative
	 * ---------------------------------------------------------------------------------------------------
	 * Precision = (true Positive)/(true Positive + false Positive)
	 * Recall = (true Positive)/(true Positive + false Negative)
	 */
	
	private static String OUTPUT_REPORT_FILE = null;
	private static String OUTPUT_LABELLED_FILE = null;
	private static String OUTPUT_PRELABELLED_FILE = null;
	
	public void StatsInitializer(String outputReportFile,
								String outputLabelledFile,
								String preLabelledFile) throws Exception {
		
		OUTPUT_REPORT_FILE = outputReportFile;
		OUTPUT_LABELLED_FILE = outputLabelledFile;
		OUTPUT_PRELABELLED_FILE = preLabelledFile;
		
		File file = new File(OUTPUT_REPORT_FILE);
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		
		TestModel(OUTPUT_PRELABELLED_FILE, OUTPUT_LABELLED_FILE, bw);
		TruePositive(OUTPUT_PRELABELLED_FILE, OUTPUT_LABELLED_FILE, bw);
		TrueNegatives(OUTPUT_PRELABELLED_FILE, OUTPUT_LABELLED_FILE, bw);
		//FalsePositive(OUTPUT_PRELABELLED_FILE, OUTPUT_LABELLED_FILE, bw);
		//FalseNegatives(OUTPUT_PRELABELLED_FILE, OUTPUT_LABELLED_FILE, bw);
		
		bw.close();
		
		System.out.println("~~~~~~~~~~~~~~~~~REPORT SUMMARY CREATED~~~~~~~~~~~~~~~~~~~~");
	}
	
	private void TestModel(String preLabelledFile, String postLabelledFile, BufferedWriter bw) throws Exception
	{
		BufferedReader br1 = new BufferedReader(new FileReader(preLabelledFile));
		BufferedReader br2 = new BufferedReader(new FileReader(postLabelledFile));
		
		int i = 0,count = 0;
		String line1,line2;
		
		line1 = br1.readLine();
		line2 = br2.readLine();
		
		for(; (line1!=null) && (line2 != null);i++) 
		{
			
			if(line1.compareToIgnoreCase(line2) == 0)
	        {
	        	count++;
	        }
	        line1=br1.readLine();
	        line2=br2.readLine();
	    }
		br1.close();
		br2.close();
		bw.write("=======================================================\n");
		bw.write("Total Correct Classification = "+count+"\n");
		bw.write("Total InCorrect Classification = "+(i-count)+"\n");
		bw.write("Total No. of Comments Classified  = "+i+"\n");
		bw.write("Accuracy = "+(count*1.0/i)+"\n");
		bw.write("=======================================================\n");
		bw.flush();
	}
	
	private void TrueNegatives(String preLabelledFile, String postLabelledFile, BufferedWriter bw) throws Exception
	{
		BufferedReader br1 = new BufferedReader(new FileReader(preLabelledFile));
		BufferedReader br2 = new BufferedReader(new FileReader(postLabelledFile));
		
		int correctNonOffensive = 0, classifiedNonOffensive = 0, incorrectNonOffensive=0;
		String line1,line2;
		line1 = br1.readLine();
		line2 = br2.readLine();
		
		for(;(line1!=null) && (line2 != null);)
		{
			String label1 = line1.substring(0,line1.indexOf("\t"));
			String label2 = line2.substring(0,line2.indexOf("\t"));
			if(label2.equals("1"))
			{
				classifiedNonOffensive++;
				if(label1.equals("1"))
				{
					correctNonOffensive++;
				}
				else
				{
					incorrectNonOffensive++;
				}
			}
			line1=br1.readLine();
			line2=br2.readLine();
		}
		
		br1.close();
		br2.close();
		bw.write("=======================================================\n");
		bw.write("CORRECT Classified NonOffensive = "+ correctNonOffensive+"\n");
		bw.write("INCORRECT Classified NonOffensive = "+ incorrectNonOffensive+"\n");
		bw.write("TOTAL Classified Non Offensive ="+ classifiedNonOffensive+"\n");
		bw.write("True Negative Accuracy = "+(correctNonOffensive*1.0/classifiedNonOffensive)+"\n");
		bw.write("=======================================================\n");
		bw.flush();
	}
	
	private void FalseNegatives(String preLabelledFile, String postLabelledFile, BufferedWriter bw) throws Exception
	{
		BufferedReader br1 = new BufferedReader(new FileReader(preLabelledFile));
		BufferedReader br2 = new BufferedReader(new FileReader(postLabelledFile));
		
		int correctNonOffensive = 0, classifiedNonOffensive = 0, incorrectNonOffensive=0;
		String line1,line2;
		line1 = br1.readLine();
		line2 = br2.readLine();
		
		for(;(line1!=null) && (line2 != null);)
		{
			String label1 = line1.substring(0,line1.indexOf("\t"));
			String label2 = line2.substring(0,line2.indexOf("\t"));
			if(label2.equals("1"))
			{
				classifiedNonOffensive++;
				if(label1.equals("1"))
				{
					correctNonOffensive++;
				}
				else
				{
					incorrectNonOffensive++;
				}
			}
			line1=br1.readLine();
			line2=br2.readLine();
		}
		
		br1.close();
		br2.close();
		bw.write("=======================================================\n");
		bw.write("CORRECT Classified NonOffensive = "+ correctNonOffensive+"\n");
		bw.write("INCORRECT Classified NonOffensive = "+ incorrectNonOffensive+"\n");
		bw.write("TOTAL Classified Non Offensive ="+ classifiedNonOffensive+"\n");
		bw.write("False Negative Accuracy = "+(incorrectNonOffensive*1.0/classifiedNonOffensive)+"\n");
		bw.write("=======================================================\n");
		bw.flush();
	}
	
	private void FalsePositive(String preLabelledFile, String postLabelledFile, BufferedWriter bw) throws Exception
	{
		BufferedReader br1 = new BufferedReader(new FileReader(preLabelledFile));
		BufferedReader br2 = new BufferedReader(new FileReader(postLabelledFile));
		
		int correctOffensive = 0, classifiedOffensive = 0, incorrectOffensive=0;
		String line1,line2;
		line1 = br1.readLine();
		line2 = br2.readLine();
		
		for(;(line1!=null) && (line2 != null);)
		{
			String label1 = line1.substring(0,line1.indexOf("\t"));
			String label2 = line2.substring(0,line2.indexOf("\t"));
			if(label2.equals("0"))
			{
				classifiedOffensive++;
				if(label1.equals("0"))
				{
					correctOffensive++;
				}
				else
				{
					incorrectOffensive++;
				}
			}
			line1=br1.readLine();
			line2=br2.readLine();
		}
		
		br1.close();
		br2.close();
		bw.write("=======================================================\n");
		bw.write("CORRECT Classified Offensive = "+ correctOffensive+"\n");
		bw.write("INCORRECT Classified Offensive = "+ incorrectOffensive+"\n");
		bw.write("TOTAL Classified Offensive ="+ classifiedOffensive+"\n");
		bw.write("False Positive Accuracy = "+(incorrectOffensive*1.0/classifiedOffensive)+"\n");
		bw.write("=======================================================\n");
		bw.flush();
	}
	
	private void TruePositive(String preLabelledFile, String postLabelledFile, BufferedWriter bw) throws Exception
	{
		BufferedReader br1 = new BufferedReader(new FileReader(preLabelledFile));
		BufferedReader br2 = new BufferedReader(new FileReader(postLabelledFile));
		
		int correctOffensive = 0, classifiedOffensive = 0, incorrectOffensive=0;
		String line1,line2;
		line1 = br1.readLine();
		line2 = br2.readLine();
		
		for(;(line1!=null) && (line2 != null);)
		{
			String label1 = line1.substring(0,line1.indexOf("\t"));
			String label2 = line2.substring(0,line2.indexOf("\t"));
			if(label2.equals("0"))
			{
				classifiedOffensive++;
				if(label1.equals("0"))
				{
					correctOffensive++;
				}
				else
				{
					incorrectOffensive++;
				}
			}
			line1=br1.readLine();
			line2=br2.readLine();
		}
		
		br1.close();
		br2.close();
		bw.write("=======================================================\n");
		bw.write("CORRECT Classified Offensive = "+ correctOffensive+"\n");
		bw.write("INCORRECT Classified Offensive = "+ incorrectOffensive+"\n");
		bw.write("TOTAL Classified Offensive ="+ classifiedOffensive+"\n");
		bw.write("True Positive Accuracy = "+(correctOffensive*1.0/classifiedOffensive)+"\n");
		bw.write("=======================================================\n");
		bw.flush();
	}

}