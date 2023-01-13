package rainfallanalyser.cp2406assignment_alpha;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;

/**
 * CP2406 Assignment - Tan Beng Siang
 * Alpha Version
 * Based on of the assignment's starting code that was provided.
 * Only drawPicture() class was modified for this project.
 * Impose requirement on user to define path for the analysed rainfall data.
 * See https: https://github.com/StillNotAPotato/CP2406Assignment_alpha
 */
public class RainfallVisualiser extends Application {

    /**
     * Draw picture.
     * Drawing area parameters (width and height in pixels) denote size
     */
    public void drawPicture(GraphicsContext g, int width, int height) {

        // Create x and y-axis
        int border_width = 20;
        g.setStroke(Color.BLACK);
        g.setLineWidth(2);
        g.strokeLine(border_width, border_width, border_width, height - border_width);
        g.strokeLine(border_width, height - border_width, width - border_width, height - border_width);

        TextIO.getln(); // Remove first line

        ArrayList<Double> allMonthlyTotals = new ArrayList<>();

        double maxMonthlyTotal = Double.NEGATIVE_INFINITY;
        int firstYear = 2050;
        int lastYear = 0;
        // Read file first and create an array of the monthly totals
        while (!TextIO.eof()) {
            String[] line = TextIO.getln().trim().strip().split(",");
            double monthlyTotal = Double.parseDouble(line[2]);
            allMonthlyTotals.add(monthlyTotal);
            if (monthlyTotal > maxMonthlyTotal)
                maxMonthlyTotal = monthlyTotal;

            int year = Integer.parseInt(line[0]);
            if (year < firstYear)
                firstYear = year;
            else if (year > lastYear)
                lastYear = year;
        }

        double xAxisLength = width - 2 * border_width;
        double yAxisLength = height - 2 * border_width;
        double currentXPos = border_width;
        double barWidth = xAxisLength / allMonthlyTotals.size();

        g.setFill(Color.BLUE);
        g.setLineWidth(0.5);

        for (Double monthlyTotal : allMonthlyTotals) {
            // Get height of column relative to maximum height
            double columnHeight = (monthlyTotal / maxMonthlyTotal) * yAxisLength;

            // Draw rectangle representing rainfall and draw black outline
            g.fillRect(currentXPos, height - border_width - columnHeight, barWidth, columnHeight);
            g.strokeRect(currentXPos, height - border_width - columnHeight, barWidth, columnHeight);

            currentXPos += barWidth;
        }

        // Add title and axis names
        g.setFill(Color.BLACK);
        g.setFont(Font.font(24));
        g.fillText("Rainfall: " + firstYear + " to " + lastYear, width/2.5, border_width);

        g.setFont(Font.font(15));
        g.fillText("Months", width/2.0, height-5);

        g.rotate(-90);
        g.fillText("Rainfall (millimeters)",-height/1.6, border_width-5);
    }


    //------ Implementation details: DO NOT EDIT THIS ------
    public void start(Stage stage) {
        int width = 200 * 6 + 40;
        int height = 500;
        Canvas canvas = new Canvas(width, height);
        drawPicture(canvas.getGraphicsContext2D(), width, height);
        BorderPane root = new BorderPane(canvas);
        root.setStyle("-fx-border-width: 4px; -fx-border-color: #444");
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Rainfall Visualiser");
        stage.show();
        stage.setResizable(false);
    }
    //------ End of Implementation details ------


    public static void main(String[] args) {
        System.out.print("Enter path: ");
        var path = TextIO.getln();

        // Used for testing
//        var path = "src/main/resources/rainfalldata_analysed/MountSheridanStationCNS_analysed.csv";
//        var path = "src/main/resources/rainfalldata_analysed/IDCJAC0009_031205_1800_Data_analysed.csv";
        TextIO.readFile(path);
        launch();
    }

}
