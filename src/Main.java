import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    private static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    private static final String ANSI_REVERSED = "\u001b[7m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_BLUE = "\u001B[34m";

    public static void printArray(String[] arr) {
        //add a border to the array representation
        System.out.println(ANSI_RED_BACKGROUND + " ".repeat((arr[0].length()+2)*3) + ANSI_RESET);

        // Print the nested array with element by element
        for (int i = 0; i < arr.length; i++) {
            System.out.print(ANSI_RED_BACKGROUND + "   " + ANSI_RESET);
            for (int j = 0; j < arr[i].length(); j++) {
                printColors(getNode(arr, i, j));
            }
            System.out.print(ANSI_RED_BACKGROUND + "   " + ANSI_RESET + "\n");
        }

        //add a border to the array representation
        System.out.println(ANSI_RED_BACKGROUND + " ".repeat((arr[0].length()+2)*3) + ANSI_RESET);
    }

    private static void printColors(char c) {
        //change text color depending on the element printed using ANSI escape codes
        switch (c) {
            case '.' -> System.out.print(ANSI_BLACK_BACKGROUND);
            case '0' -> System.out.print(ANSI_RED_BACKGROUND);
            //use background green color for 'F'
            case 'F' -> System.out.print(ANSI_REVERSED+ ANSI_CYAN);
            case 'S' -> System.out.print(ANSI_REVERSED+ ANSI_GREEN);
            case '@' -> System.out.print(ANSI_REVERSED+ ANSI_BLUE);
        }

        if (c == '0') {
            System.out.print("   ");
        } else {
            System.out.print(" " + c + " ");
        }

        //reset the color to default
        System.out.print(ANSI_RESET);
    }

    public static String[] readFile(String filename) {
        // Read the file and return the content as a string array and return as an array using scanner
        String[] readings = new String[0];
        try {
            Scanner scanner = new Scanner(new File(filename));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                readings = Arrays.copyOf(readings, readings.length + 1);
                readings[readings.length - 1] = line;
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred. File not found. at " + filename);
            e.printStackTrace();
        }

        return readings;
    }


    public static char getNode(String[] readings, int x, int y) {
        //return the node at the given x and y coordinates
        if (x < 0 || y < 0 || x >= readings.length || y >= readings[0].length()) {
            //if it's out of range return 0
            return '0';
        }
        return readings[x].charAt(y);
    }

    public static void main(String[] args) {
        System.out.println("Program started.");

        //read the file
        String[] readings = readFile("data.txt");

        //print the array
        printArray(readings);

        System.out.println("Program ended.");
    }
}