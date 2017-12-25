package application;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class PieChartSample4 extends Application {

    @Override
    public void start(Stage stage) {

        BorderPane root = new BorderPane();

        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(
                new PieChart.Data("Grapefruit", 13),
                new PieChart.Data("Oranges", 25),
                new PieChart.Data("Plums", 10),
                new PieChart.Data("Pears", 22),
                new PieChart.Data("Apples", 30));

        final PieChart chart = new PieChart(pieChartData);
        chart.setTitle("Imported Fruits");

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

        root.setCenter(chartWithCaption);

        // Just some stuff to change the overall layout:
        HBox controls = new HBox(5);
        controls.setPadding(new Insets(10));
        controls.setAlignment(Pos.CENTER);
        controls.getChildren().addAll(new Label("Some other stuff here"), new TextField(), new Button("OK"));
        root.setTop(controls);
        root.setPadding(new Insets(0, 0, 10, 40));
        root.setLeft(new Circle(25,  Color.SALMON));

        Scene scene = new Scene(root);
        stage.setTitle("Imported Fruits");
        stage.setWidth(600);
        stage.setHeight(500);

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}