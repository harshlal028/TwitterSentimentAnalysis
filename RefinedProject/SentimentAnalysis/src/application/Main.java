package application;
	
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import lal.openNLP.maxEnt.ProcessText;
import lal.openNLP.maxEnt.SentimentAnalyzer;


public class Main extends Application {
	String decision = null;
	@Override
	public void start(Stage primaryStage) {
		try {
			
			SentimentAnalyzer sa = new SentimentAnalyzer();
			ProcessText pt = new ProcessText();
			primaryStage.setTitle("Analyze Sentiment");

			//Creating a GridPane container
			GridPane grid = new GridPane();
			grid.setPadding(new Insets(10, 10, 10, 10));
			grid.setVgap(5);
			grid.setHgap(5);
			
			final TextArea name = new TextArea();
			name.setPromptText("Enter your first name.");
			name.setWrapText(true);
			name.setPrefSize(250, 150);
			name.getText();
			GridPane.setConstraints(name, 0, 0);
			grid.getChildren().add(name);
			
			//Defining the Submit button
			Button submit = new Button("Submit");
			GridPane.setConstraints(submit, 3, 0);
			grid.getChildren().add(submit);
			
			//Defining the Clear button
			Button clear = new Button("Clear");
			GridPane.setConstraints(clear, 0, 3);
			grid.getChildren().add(clear);
			
			//Adding a Label
			final Label label = new Label();
			GridPane.setConstraints(label, 1, 5);
			GridPane.setColumnSpan(label, 2);
			grid.getChildren().add(label);

			
			//Setting an action for the Submit button
			submit.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			    public void handle(ActionEvent e) {
			        if (!name.getText().equalsIgnoreCase("") ) {
			        	try {
			        		String rawText = name.getText();
			        		
			        		String processedText = pt.processRawText(rawText);
			        		decision = sa.predictTextUsingModel(processedText);
							//decision = sa.predictTextUsingModel(name.getText());
							
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
			        	label.setText(decision);
			        } else {
			        	label.setText("No Text Entered");
			        }
			     }
			 });
			 
			//Setting an action for the Clear button
			clear.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			    public void handle(ActionEvent e) {
			        name.clear();
			        name.clear();
			    }
			});
			
			Scene scene = new Scene(grid,400,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
