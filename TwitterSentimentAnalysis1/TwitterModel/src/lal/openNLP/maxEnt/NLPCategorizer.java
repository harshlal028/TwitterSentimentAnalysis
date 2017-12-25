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
import java.util.Properties;

import opennlp.tools.doccat.BagOfWordsFeatureGenerator;
import opennlp.tools.doccat.DoccatFactory;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerEvaluator;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.doccat.FeatureGenerator;
import opennlp.tools.doccat.NGramFeatureGenerator;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;

public class NLPCategorizer {

	/* File IO variables */
	private static String OUTPUT_MODEL_FILE;
	private static String OUTPUT_LABELLED_FILE;
	private static String INPUT_TRAINING_FILE;
	private static String INPUT_TEST_FILE;
	private static String INPUT_TEST_FILE_ID;

	/* Algo options */
	private static String CUT_OFF;
	private static String ITERATIONS;
	private static String ALGORITHM;

	/* DB Names and Tags */
	private static String NONOFFENSIVE_TAG;
	private static String OFFENSIVE_TAG;
	private static String PROCESSED_TABLE;

	/* Analysis options*/
	private static String ANALYZE;
	private static String OPERATION;
	
	public void NLPInitializer() throws Exception {

		try {
			System.out.println("Loading properties file");
			loadProperties("filenames.properties","maxent.properties","db.properties","textanalysis.properties");
			if(OPERATION.equalsIgnoreCase("MODEL"))
			{
				NLPModelGenerator();
			}
			else if(OPERATION.equalsIgnoreCase("CLASSIFY"))
			{
				NLPClassifier();
			}
			else
			{
				System.out.println("Unknown OPERATION option "+OPERATION);
				throw new Exception("Unknown OPERATION option "+OPERATION);
			}

		}catch(Exception e)
		{
			System.out.println("Failed in NLPInitializer "+e.getMessage());
			e.printStackTrace();
			throw e;
		}

	}
	
	private void NLPModelGenerator() throws Exception {
		
		System.out.println("Preparing Data");
		prepareDataForModelGeneration();
		
		System.out.println("Training Model");
		trainModel();
		try {
			File file = new File(OUTPUT_LABELLED_FILE);
			if (!file.exists()) {
				file.createNewFile();
				System.out.println("Created new output file "+OUTPUT_LABELLED_FILE);
			}

			DocumentCategorizerME categorizer = loadModel();

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);

			BufferedReader br1 = new BufferedReader(new FileReader(INPUT_TEST_FILE));
			BufferedReader br2 = new BufferedReader(new FileReader(INPUT_TEST_FILE_ID));
			for(String line; (line = br1.readLine()) != null; ) {
				String processedText = line.substring(line.indexOf("\t")+1);
				String decisionType = classifyNewText(processedText, categorizer);
				String foreignId = br2.readLine();
				System.out.println("TEXT:"+processedText+"==DECISION=="+decisionType+"==FOREIGNID=="+foreignId);
				saveClassifiedResult(processedText, decisionType, foreignId, bw);
			}
			bw.close();
			br1.close();
			br2.close();
		}catch(Exception e)
		{
			System.out.println("Failed in NLPInitializer "+e.getMessage());
			e.printStackTrace();
			throw e;
		}

	}

	private void NLPClassifier() throws Exception {
		
		System.out.println("Preparing Data");
		prepareDataForClassification();
		
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

			BufferedReader br1 = new BufferedReader(new FileReader(INPUT_TEST_FILE));
			BufferedReader br2 = new BufferedReader(new FileReader(INPUT_TEST_FILE_ID));
			for(String line; (line = br1.readLine()) != null; ) {
				String processedText = line.substring(line.indexOf("\t")+1);
				String decisionType = classifyNewText(processedText, categorizer);
				String foreignId = br2.readLine();
				System.out.println("TEXT:"+processedText+"==DECISION=="+decisionType+"==FOREIGNID=="+foreignId);
				saveClassifiedResult(processedText, decisionType, foreignId, bw);
			}
			bw.close();
			br1.close();
			br2.close();
		}catch(Exception e)
		{
			System.out.println("Failed in NLPClassifier "+e.getMessage());
			e.printStackTrace();
			throw e;
		}

	}

	private void loadProperties(String filename1, String filename2, String filename3, String filename4) throws Exception
	{
		//load a properties file from class path, inside static method
		Properties prop = new Properties();
		prop.load(NLPCategorizer.class.getClassLoader().getResourceAsStream(filename1));

		Properties prop2 = new Properties();
		prop2.load(NLPCategorizer.class.getClassLoader().getResourceAsStream(filename2));
		prop.putAll(prop2);

		Properties prop3 = new Properties();
		prop3.load(NLPCategorizer.class.getClassLoader().getResourceAsStream(filename3));
		prop.putAll(prop3);

		Properties prop4 = new Properties();
		prop4.load(NLPCategorizer.class.getClassLoader().getResourceAsStream(filename4));
		prop.putAll(prop4);

		//get the property value and print it out
		OUTPUT_MODEL_FILE = prop.getProperty("OUTPUT_MODEL_FILE");
		OUTPUT_LABELLED_FILE = prop.getProperty("OUTPUT_LABELLED_FILE");
		INPUT_TRAINING_FILE = prop.getProperty("INPUT_TRAINING_FILE");
		INPUT_TEST_FILE = prop.getProperty("INPUT_TEST_FILE");
		INPUT_TEST_FILE_ID = prop.getProperty("INPUT_TEST_FILE_ID");
		CUT_OFF = prop.getProperty("CUT_OFF");
		ITERATIONS = prop.getProperty("ITERATIONS");
		ALGORITHM = prop.getProperty("ALGORITHM");
		NONOFFENSIVE_TAG = prop.getProperty("NONOFFENSIVE_TAG");
		OFFENSIVE_TAG = prop.getProperty("OFFENSIVE_TAG");
		PROCESSED_TABLE = prop.getProperty("PROCESSED_TABLE");
		ANALYZE = prop.getProperty("ANALYZE");
		OPERATION = prop.getProperty("OPERATION");


		if(OUTPUT_MODEL_FILE == null || OUTPUT_LABELLED_FILE == null || INPUT_TRAINING_FILE == null || INPUT_TEST_FILE == null || 
				INPUT_TEST_FILE_ID == null || CUT_OFF == null || ITERATIONS == null || ALGORITHM == null || NONOFFENSIVE_TAG == null ||
				OFFENSIVE_TAG == null || PROCESSED_TABLE == null || ANALYZE == null || OPERATION == null)
		{
			System.out.println("Error loading properties file "+filename1+" & "+filename2+" & "+filename3+" & "+filename4);
			throw new Exception("Error Loading properties file "+filename1+" "+filename2+" "+filename3+" "+filename4);
		}
		System.out.println("Properties file loaded successfully");

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
			onlpModelOutput = new BufferedOutputStream(new FileOutputStream(OUTPUT_MODEL_FILE+ANALYZE));
			model.serialize(onlpModelOutput);
			onlpModelOutput.close();
			System.out.println("Model saved to "+OUTPUT_MODEL_FILE+ANALYZE);

		} catch (Exception e) {
			System.out.println("Training model failed "+e.getMessage());
			e.printStackTrace();
			throw e;
		} 
	}

	/*
	 * Now we call the saved model and test it
	 * Give it a new text document and the expected category
	 */
	private void test(String cat, String text) throws InvalidFormatException, IOException 
	{
		InputStream is = new FileInputStream(OUTPUT_MODEL_FILE);
		DoccatModel classificationModel = new DoccatModel(is);
		DocumentCategorizerME classificationME = new DocumentCategorizerME(classificationModel);
		DocumentCategorizerEvaluator modelEvaluator = new DocumentCategorizerEvaluator(classificationME);

		String expectedDocumentCategory = cat;
		String documentContent = text;

		DocumentSample sample = new DocumentSample(expectedDocumentCategory,documentContent);

		double[] classDistribution = classificationME.categorize(documentContent);
		String predictedCategory = classificationME.getBestCategory(classDistribution);

		modelEvaluator.evaluateSample(sample);
		double result = modelEvaluator.getAccuracy();

		System.out.println("Model prediction : " + predictedCategory);
		System.out.println("Accuracy : " + result);
	}

	private DocumentCategorizerME loadModel() throws Exception
	{
		InputStream is = null;
		DoccatModel classificationModel = null;
		File f = new File(OUTPUT_MODEL_FILE+ANALYZE);
		if(!(f.exists() && !f.isDirectory())) 
		{ 
			System.out.println(OUTPUT_MODEL_FILE+ANALYZE+" not found!!!!");
			throw new Exception(OUTPUT_MODEL_FILE+ANALYZE+" not found!!!!");
		}
		try {
			is = new FileInputStream(OUTPUT_MODEL_FILE+ANALYZE);
			classificationModel = new DoccatModel(is);
			System.out.println("Model loaded from "+OUTPUT_MODEL_FILE+ANALYZE);
		} catch (IOException e) {
			System.out.println("Loading model failed from "+OUTPUT_MODEL_FILE+ANALYZE+"==="+e.getMessage());
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

	private void saveClassifiedResult(String processedText,String decisionType, String foreignId, BufferedWriter bw) throws Exception 
	{
		bw.write(decisionType+"\t"+processedText+"\n");
		bw.flush();
		System.out.println("Result written to text output file");

		String decisionTag = null;
		if (decisionType.equalsIgnoreCase("1")) {
			decisionTag = NONOFFENSIVE_TAG;
			System.out.println("The text is positive :) "+processedText);
		} else if (decisionType.equalsIgnoreCase("0")){
			decisionTag = OFFENSIVE_TAG;
			System.out.println("The text is negative :( "+processedText);
		} else {
			System.out.println("Unknown category "+decisionType+" for text "+processedText);
		}

		System.out.println(processedText+"==="+decisionTag+"==="+foreignId);
		/*IDbService dbService = new DbService();
		dbService.populateDB(processedText, decisionTag, foreignId);
		System.out.println("Decision is saved in DB");*/
	}
	
	private void prepareDataForModelGeneration()
	{
		/* This method contains logic to prepare Data for Model generation */
	}
	
	private void prepareDataForClassification()
	{
		/* This method contains logic to prepare Data for classification */
	}
}