package lal.openNLP.maxEnt;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import opennlp.tools.doccat.BagOfWordsFeatureGenerator;
import opennlp.tools.doccat.DoccatFactory;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.doccat.FeatureGenerator;
import opennlp.tools.doccat.NGramFeatureGenerator;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;

public class SentimentAnalyzer {
	
	/* File IO variables */
	private static String OUTPUT_MODEL_FILE = "./output/sentimentModel.model";
	private static String OUTPUT_LABELLED_FILE = "./output/generatedTestResult.txt";
	private static String INPUT_TRAINING_FILE = "./input/tweets.txt";
	private static String INPUT_TEST_FILE = "./input/testTweet.txt";

	/* Algo options */
	private static String CUT_OFF = "2";
	private static String ITERATIONS = "100";
	private static String ALGORITHM = "MAXENT";
	
	/* Analysis options*/
	//private static String OPERATION;
	
	public static void main(String[] args) throws Exception {
		SentimentAnalyzer sa = new SentimentAnalyzer();
		//sa.trainSentimentAnalysisModel();
		sa.predictTextUsingModel("very bad movie");
	}
	
	public void trainSentimentAnalysisModel() throws Exception
	{
		trainModel();
		NLPStats nlpStats = new NLPStats();
		predictTrainingFileUsingModel(INPUT_TRAINING_FILE);
		nlpStats.StatsInitializer("./output/report1.txt",OUTPUT_LABELLED_FILE, INPUT_TRAINING_FILE);
		predictTestFileUsingModel(INPUT_TEST_FILE);
		nlpStats.StatsInitializer("./output/report2.txt",OUTPUT_LABELLED_FILE, "./output/testTweetResults.txt");
		
		
	}
	
	public String predictTextUsingModel(String text) throws Exception
	{
		DocumentCategorizerME model = loadModel();
		String result = classifyNewText(text, model);
		if(result.equals("0"))
		{
			result = "Negative :(";
		}
		else if(result.equals("1"))
		{
			result = "Positive :)";
		}
		return result;
	}
	
	public void predictTrainingFileUsingModel(String filename) throws Exception
	{

		try {
			File file = new File(OUTPUT_LABELLED_FILE);
			if (!file.exists()) {
				file.createNewFile();
				System.out.println("Created new output file "+OUTPUT_LABELLED_FILE);
			}
			
			DocumentCategorizerME categorizer = loadModel();
			System.out.println("Loading Model Completed");

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);

			BufferedReader br1 = new BufferedReader(new FileReader(filename));
			for(String line; (line = br1.readLine()) != null; ) {
				String processedText = line.substring(line.indexOf("\t")+1);
				String decisionType = classifyNewText(processedText, categorizer);
				System.out.println("TEXT:"+processedText+"==DECISION=="+decisionType);
				saveClassifiedResult(processedText, decisionType, bw);
			}
			bw.close();
			br1.close();
		}catch(Exception e)
		{
			System.out.println("Failed in NLPClassifier "+e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}
	
	public void predictTestFileUsingModel(String filename) throws Exception
	{

		try {
			File file = new File(OUTPUT_LABELLED_FILE);
			if (!file.exists()) {
				file.createNewFile();
				System.out.println("Created new output file "+OUTPUT_LABELLED_FILE);
			}
			
			DocumentCategorizerME categorizer = loadModel();
			System.out.println("Loading Model Completed");

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);

			BufferedReader br1 = new BufferedReader(new FileReader(filename));
			for(String line; (line = br1.readLine()) != null; ) {
				String processedText = line;
				String decisionType = classifyNewText(processedText, categorizer);
				System.out.println("TEXT:"+processedText+"==DECISION=="+decisionType);
				saveClassifiedResult(processedText, decisionType, bw);
			}
			bw.close();
			br1.close();
		}catch(Exception e)
		{
			System.out.println("Failed in NLPClassifier "+e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}

	private void trainModel() throws Exception 
	{
		MarkableFileInputStreamFactory factory = null;
		OutputStream onlpModelOutput = null;
		DoccatModel model = null;

		try {
			System.out.println("Opening file "+INPUT_TRAINING_FILE);
			factory = new MarkableFileInputStreamFactory(new File(INPUT_TRAINING_FILE));
			ObjectStream<String> lineStream = new PlainTextByLineStream(factory, "UTF-8");
			ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(lineStream);

			TrainingParameters trainingParams = new TrainingParameters();
			trainingParams.put(TrainingParameters.ITERATIONS_PARAM, ITERATIONS);
			trainingParams.put(TrainingParameters.CUTOFF_PARAM, CUT_OFF);
			trainingParams.put(TrainingParameters.ALGORITHM_PARAM, ALGORITHM);

			System.out.println("CUT OFF = "+CUT_OFF);
			System.out.println("Training Iteration = "+ITERATIONS);
			System.out.println("Algorithm = "+ALGORITHM);

			FeatureGenerator fg1 = new BagOfWordsFeatureGenerator();
			FeatureGenerator fg2 = new NGramFeatureGenerator();

			model = DocumentCategorizerME.train("en", sampleStream, trainingParams, new DoccatFactory());
			//model = DocumentCategorizerME.train("en", sampleStream, trainingParams, fg1, fg2);
			System.out.println("-------------------------------- MAXENT MODEL CREATED --------------------------------");
			//Saving the model
			onlpModelOutput = new BufferedOutputStream(new FileOutputStream(OUTPUT_MODEL_FILE));
			model.serialize(onlpModelOutput);
			onlpModelOutput.close();
			System.out.println("Model saved to "+OUTPUT_MODEL_FILE);

		} catch (Exception e) {
			System.out.println("Training model failed "+e.getMessage());
			e.printStackTrace();
			throw e;
		} 
	}
	
	private DocumentCategorizerME loadModel() throws Exception
	{
		InputStream is = null;
		DoccatModel classificationModel = null;
		File f = new File(OUTPUT_MODEL_FILE);
		if(!(f.exists() && !f.isDirectory())) 
		{ 
			System.out.println(OUTPUT_MODEL_FILE+" not found!!!!");
			throw new Exception(OUTPUT_MODEL_FILE+" not found!!!!");
		}
		try {
			is = new FileInputStream(OUTPUT_MODEL_FILE);
			classificationModel = new DoccatModel(is);
			System.out.println("Model loaded from "+OUTPUT_MODEL_FILE);
		} catch (IOException e) {
			System.out.println("Loading model failed from "+OUTPUT_MODEL_FILE+"==="+e.getMessage());
			e.printStackTrace();
		}
		DocumentCategorizerME myCategorizer = new DocumentCategorizerME(classificationModel);
		return(myCategorizer);
	}

	private String classifyNewText(String text, DocumentCategorizerME myCategorizer) 
	{
		//DocumentCategorizerME myCategorizer = new DocumentCategorizerME(model);
		double[] outcomes = myCategorizer.categorize(text);
		String category = myCategorizer.getBestCategory(outcomes);

		/*if (category.equalsIgnoreCase("1")) {
			System.out.println("The text is positive :) "+text);
		} else {
			System.out.println("The text is negative :( "+text);
		}*/
		System.out.println(text+"==="+category);
		return category;
	}
	
	private void saveClassifiedResult(String processedText, String decisionType, BufferedWriter bw) throws Exception 
	{
		bw.write(decisionType+"\t"+processedText+"\n");
		bw.flush();
		System.out.println("Result written to text output file");

		if (decisionType.equalsIgnoreCase("1")) {
			System.out.println("The text is positive :) "+processedText);
		} else if (decisionType.equalsIgnoreCase("0")){
			System.out.println("The text is negative :( "+processedText);
		} else {
			System.out.println("Unknown category "+decisionType+" for text "+processedText);
		}

	}

}
