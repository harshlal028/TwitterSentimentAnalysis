package lal.openNLP.maxEnt;

public class ProcessText {
	
	public static void main(String[] args) {
		
		//String a = "@ROBIN watching M.S. Dhoni : The Untold Story Full \n Movie http:\\\\www.facebook.com\\twitterrel=nofollow\u003eFacebook\u003c003e";
		String a1 = "RT @Koimoi: #MSDhoniTheUntoldStory Becomes The Highest Grossing Biopic Ever| 2nd Week Box Office Report \n@itsSSR @Tutejajoginder https:\\t.c2026 helloooooo";
		ProcessText pt = new ProcessText();
		pt.processRawText(a1);
	}

	public String processRawText(String rawText)
	{
		rawText = rawText.replaceAll("http\\S+", "");
		rawText = rawText.replaceAll("@\\S+", "");
		rawText = rawText.replaceAll("[^A-Za-z0-9 ]", "");
		rawText = rawText.replaceAll(" +", " ");
		rawText = rawText.trim();
		return(rawText);
	}
}
