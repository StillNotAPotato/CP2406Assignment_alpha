package rainfallanalyser.cp2406assignment_alpha;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Objects;

import org.apache.commons.csv.*;

/**
 * CP2406 Assignment - Tan Beng Siang
 * Rainfall Analyser - Alpha Version
 * This application will retrieve rainfall information from a user-specified file
 * and provide the analysed information in a format for a visualiser.
 * The project's rainfalldata directory is the only place
 * where this version can analyse files.
 * See https: https://github.com/StillNotAPotato/CP2406Assignment_alpha for project repository
 */
public class RainfallAnalyser {

    public static void main(String[] args) {

        System.out.println("Welcome to the Rainfall Analyser");
        System.out.println("This program analyses the rainfall data given from various sources like BOM");
        System.out.println("It will then return the extracted Total Monthly Rainfall for the data set");
        System.out.println("as well as the minimum and maximum rainfall that occurred that month.");
        System.out.println("Enter zero to exit the program.");

        String filename;
        while (true) {
            try {
                filename = getFilename();
                if (filename == null) {
                    System.out.println("Goodbye!");
                    break;
                }
                ArrayList<String> analysedRainfallData = analyseRainfallData(filename);
                saveRainfallData(analysedRainfallData, filename);
                System.out.println(filename + " successfully analysed!");

            } catch (Exception e) {
                System.out.println("Error: there was an issue");
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Write analysed rainfall data to /rainfalldata_analysed/ directory
     * To prevent producing redundant files, add a separate method at the end.
     */
    private static void saveRainfallData(ArrayList<String> analysedRainfallData, String filename) {
        // Set output file based on the file being analysed
        TextIO.writeFile(getSavePath(filename));
        TextIO.putln("year,month,total,minimum,maximum");

        for (String rainfallData: analysedRainfallData) {
            TextIO.putln(rainfallData);
        }
    }

    /**
     * Load file to be analysed and create ArrayList representing rainfall data.
     * Use Apache commons csv package (https://commons.apache.org/proper/commons-csv/)
     */
    private static ArrayList<String> analyseRainfallData(String fileName) throws Exception {

        // Check if file is empty
        File f = new File("src/main/resources/rainfalldata/" + fileName);
        if (f.length() == 0)
            throw new Exception("That file is empty");

        // Create FileReader and CSV reader
        // See https://commons.apache.org/proper/commons-csv/
        Reader reader = new FileReader("src/main/resources/rainfalldata/" + fileName);
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withHeader().parse(reader);

        int year, month, day;
        double rainfall;
        int currentYear = 0;
        int currentMonth = 1;
        double monthlyTotal = 0.0;
        double minRainfall = Double.POSITIVE_INFINITY;
        double maxRainfall = 0.0;
        ArrayList<String> rainfallData = new ArrayList<>();

        for (CSVRecord record : records) {
            // Get data from specific rows of the Rainfall Data CSV file
            String yearText = record.get("Year");
            String monthText = record.get("Month");
            String dayText = record.get("Day");
            String rainfallText = record.get("Rainfall amount (millimetres)");

            // Analyse data and convert into the expected output format
            year = Integer.parseInt(yearText);
            month = Integer.parseInt(monthText);
            day = Integer.parseInt(dayText);

            // Check if recorded date is valid
            if ((month < 1 || month > 12) || (day < 1 || day > 31)) {
                System.out.println("Error: Invalid format for dates");
                throw new NumberFormatException ("Dates are out of expected range.");
            }

            // Check data for rainfall, if not assume zero
            rainfall = Objects.equals(rainfallText, "") ? 0 : Double.parseDouble(rainfallText);

            // Check if it's the next month. If it is the next month, save data and reset the totals/values
            if (month != currentMonth) {
                // Check if it is the first year before saving data
                rainfallData.add(writeCurrentData(monthlyTotal, minRainfall, maxRainfall, currentMonth, currentYear == 0? year : currentYear));
                currentYear = year;
                currentMonth = month;
                monthlyTotal = 0;
                maxRainfall = 0.0;
                minRainfall = Double.POSITIVE_INFINITY;
            }

            // Update total for the month
            monthlyTotal += rainfall;
            if (rainfall > maxRainfall) maxRainfall = rainfall;
            if (rainfall < minRainfall) minRainfall = rainfall;
        }
        // Catch incomplete month when exiting the for loop
        rainfallData.add(writeCurrentData(monthlyTotal, minRainfall, maxRainfall, currentMonth, currentYear));
        return rainfallData;
    }

    /**
     * Return a String representation of the months data
     */
    private static String writeCurrentData(double monthlyTotal, double minRainfall, double maxRainfall, int month, int year) {
        return String.format("%d,%d,%1.2f,%1.2f,%1.2f", year, month, monthlyTotal, minRainfall, maxRainfall);
    }

    /**
     * Get a list of available rainfall data sets to be analysed.
     * Allows the user to pick which data set to analyse from this list.
     */
    private static String getFilename() {
        System.out.println("\nThe files available are:");
        File f = new File("src/main/resources/rainfalldata");
        String[] pathNames = f.list();

        assert pathNames != null;
        for (int i = 0; i < pathNames.length; i++) {
            System.out.println((i+1) + ": " + pathNames[i]);
        }

        System.out.println("Please enter the corresponding number of the file you wish to analyse: ");

        int fileNumber;
        String filename;
        while (true) {
            // Check if selected file is valid. TextIO handles a non-Int input
            try {
                fileNumber = TextIO.getInt();
                if (fileNumber == 0) {
                    return null;
                }
                filename = pathNames[fileNumber - 1];
                break;
            }
            catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("That is outside of the range of available data files to analyse.");
                System.out.println("Please choose another one");
            }
        }
        return filename;
    } //

    /**
     * Return analysed rainfall data file path
     */
    private static String getSavePath(String filename) {
        String[] filenameElements = filename.trim().split("\\.");
        return "src/main/resources/rainfalldata_analysed/" + filenameElements[0] + "_analysed.csv";
    }
}

