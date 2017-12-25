package lal.openNLP.maxEnt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

public class CountSentiment {
	
	public static void main(String[] args) throws Exception  {
		
		CountSentiment cs = new CountSentiment();
		HashMap<String, Double> temp = cs.calculateSentimentCount("./output/generatedTestResult.txt");
	    System.out.println(temp.get("positive"));
	    System.out.println(temp.get("negative"));
		
	}
	
	public HashMap<String, Double> calculateSentimentCount(String filename) throws Exception
	{
		HashMap<String, Double>valueCount  = new HashMap<>();
		double positive = 0;
		double negative = 0;
		double neutral = 0;
		
		BufferedReader br1 = new BufferedReader(new FileReader(filename));
		String line = br1.readLine();
		String temp= null;
		int tempNum = -1;
		for(;(line!=null);) 
		{
			temp = null;
			tempNum = -1;
			temp = line.substring(0, line.indexOf("\t"));
			tempNum = Integer.parseInt(temp);
			if(tempNum == 0)
			{
				negative++;
			}
			else if(tempNum == 1)
			{
				positive++;
			}
			else if(tempNum == 2)
			{
				neutral++;
			}
			line = br1.readLine();
	    }
		br1.close();
		valueCount.put("positive", positive);
		valueCount.put("negative", negative);
		valueCount.put("neutral", neutral);
		return(valueCount);
	}

}
