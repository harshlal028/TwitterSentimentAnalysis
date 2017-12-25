package application;

import java.util.HashMap;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import lal.openNLP.maxEnt.CountSentiment;
import lal.openNLP.maxEnt.ProcessText;
import lal.openNLP.maxEnt.SentimentAnalyzer;

public class PieChartSample extends Application {

	private static final String CHART_LABEL="Twitter Sentiment Analysis - MS DHONI";
	private static final String POSITIVE="Positive :)";
	private static final String NEGATIVE="Negative :(";
	private static final String NEUTRAL="Neutral :|";
	ObservableList<PieChart.Data> pieChartData =
			FXCollections.observableArrayList();

	@Override
	public void start(Stage stage) {

		BorderPane root = new BorderPane();

		/*Create a Pie Chart*/
		final PieChart chart = new PieChart(pieChartData);
		chart.setTitle(CHART_LABEL);

		final Label caption = new Label("");
		caption.setTextFill(Color.DARKORANGE);
		caption.setStyle("-fx-font: 24 arial;");

		Group chartWithCaption = new Group(chart, caption);

		for (final PieChart.Data data : chart.getData()) {
			data.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED,
					new EventHandler<MouseEvent>() {
				@Override public void handle(MouseEvent e) {
					Point2D locationInScene = new Point2D(e.getSceneX(), e.getSceneY());
					Point2D locationInParent = chartWithCaption.sceneToLocal(locationInScene);

					caption.relocate(locationInParent.getX(), locationInParent.getY());

					caption.setText(String.valueOf(data.getPieValue())  + "%");
				}
			});
		}

		// Just some stuff to change the overall layout:
		HBox controls = new HBox(5);
		controls.setPadding(new Insets(10));
		controls.setAlignment(Pos.CENTER);
		
		/*Training Model Button*/
		Button trainButton = new Button();
		trainButton.setText("Train Model");
		trainButton.setOnAction((event)-> {
			Button source = (Button) event.getSource();
			if (source.isArmed()) {
				source.setDisable(true);
				System.out.println("trainButton Clicked!");
				trainModel();
				source.setDisable(false);
			} 
		});
		
		/*Analyzing Tweets Button*/
		Button analyzeButton = new Button();
		analyzeButton.setText("Analyze Tweets");
		analyzeButton.setOnAction((event)-> {
			Button source = (Button) event.getSource();
			if (source.isArmed()) {
				source.setDisable(true);
				System.out.println("analyzeButton Clicked!");
				analyzeTweets();
				source.setDisable(false);
			}
		});
		
		/*Updating Graph Button*/
		Button updateGraphButton = new Button();
		updateGraphButton.setText("Update Graph");
		updateGraphButton.setOnAction((event)-> {
			Button source = (Button) event.getSource();
			if (source.isArmed()) {
				source.setDisable(true);
				System.out.println("updateGraphButton Clicked!");
				addChartData();
				root.setCenter(chartWithCaption);
				source.setDisable(false);
			}
		});
		
		controls.getChildren().addAll(trainButton,updateGraphButton,analyzeButton);
		root.setTop(controls);
		root.setPadding(new Insets(0, 0, 10, 40));
		//root.setLeft(new Circle(25,  Color.SALMON));

		GridPane grid = new GridPane();
		grid.setHgap(10);
	    grid.setVgap(12);
	    
	    HBox hbButtons = new HBox();
	    hbButtons.setSpacing(10.0);
	    
	    final TextArea rawText = new TextArea();
		rawText.setPromptText("Enter your text here.");
		rawText.setWrapText(true);
		rawText.setPrefSize(250, 150);
		
		//Defining the Submit button
		Button submit = new Button("Submit");
		
		//Defining the Clear button
		Button clear = new Button("Clear");
		clear.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
		
		
		//Adding a Label
		final Label label = new Label();
		label.setFont(Font.font(30));
		
		//Setting an action for the Submit button
		submit.setOnAction(new EventHandler<ActionEvent>() {

		@Override
		    public void handle(ActionEvent e) {
			String decision = null;
		        if (!rawText.getText().equalsIgnoreCase("") ) {
		        	try {
		        		String RAWText = rawText.getText();
		        		decision = predictTextSentiment(RAWText);
		        		if(decision.equalsIgnoreCase(POSITIVE))
		        			label.setTextFill(Color.web("#4ad629"));
		        		if(decision.equalsIgnoreCase(NEGATIVE))
		        			label.setTextFill(Color.web("#ff0101"));
		        		if(decision.equalsIgnoreCase(NEUTRAL))
		        			label.setTextFill(Color.web("#f3ec26"));
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
		        	label.setText(decision);
		        } else {
		        	label.setText("No Text Entered");
		        	label.setTextFill(Color.web("#0076a3"));
		        }
		     }
		 });
		 
		//Setting an action for the Clear button
		clear.setOnAction(new EventHandler<ActionEvent>() {
		@Override
		    public void handle(ActionEvent e) {
		        rawText.clear();
		    }
		});
		
		hbButtons.getChildren().addAll(submit, clear);
		grid.add(rawText, 0, 0);
		grid.add(hbButtons, 3, 0);
		grid.add(label, 3, 0);
		
		root.setBottom(grid);
		
		Scene scene = new Scene(root);
		stage.setTitle("Sentiment Analysis");
		stage.setWidth(600);
		stage.setHeight(700);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		//stage.setFullScreen(true);

		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);

	}

	public String predictTextSentiment(String rawText)
	{
		ProcessText pt = new ProcessText();
		String processedText = pt.processRawText(rawText);
		
		SentimentAnalyzer sa = new SentimentAnalyzer();
		String decision = "ERROR";
		try {
			decision = sa.predictTextUsingModel(processedText);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(decision.toLowerCase().contains("positive"))
			decision = POSITIVE;
		if(decision.toLowerCase().contains("negative"))
			decision = NEGATIVE;
		if(decision.toLowerCase().contains("neutral"))
			decision = NEUTRAL;
		return decision;
	}
	
	public void analyzeTweets()
	{
		//Call analyze tweet function here
		SentimentAnalyzer sa = new SentimentAnalyzer();
		try {
			sa.predictTestFileUsingModel("./input/testTweet.txt");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void trainModel()
	{
		//Call Train Model Function here
		SentimentAnalyzer sa = new SentimentAnalyzer();
		try {
			sa.trainSentimentAnalysisModel();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addChartData()
	{
		double positive = 0;
		double negative = 0;
		double neutral = 0;

		// Calculate Percentages and add Data to the chart
		CountSentiment cs = new CountSentiment();
		HashMap<String, Double> hashmap = null;
		try {
			hashmap = cs.calculateSentimentCount("./output/generatedTestResult.txt");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(hashmap != null)
		{
			positive = hashmap.get("positive");
			negative = hashmap.get("negative");
			neutral = hashmap.get("neutral");
		}
		
		double total = positive + negative + neutral;
		positive = (positive / total)*360;
		negative = (negative / total)*360;
		neutral = (neutral / total)*360;
		
		addData("Positive Tweets", positive);
		addData("Negative Tweets", negative);
		addData("Neutral Tweets", neutral);
	}

	//adds new Data to the list
	public void naiveAddData(String name, double value)
	{
		pieChartData.add(new Data(name, value));
	}

	//updates existing Data-Object if name matches
	public void addData(String name, double value)
	{
		for(Data d : pieChartData)
		{
			if(d.getName().equals(name))
			{
				d.setPieValue(value);
				return;
			}
		}
		naiveAddData(name, value);
	}
}