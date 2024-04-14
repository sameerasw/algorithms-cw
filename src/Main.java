import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class Main {

    public static void printArray(String[] arr) {
        //add a border to the array representation
        System.out.println("\u001b[41m ".repeat(arr[0].length()*3+4) + "\u001B[0m");

        // Print the nested array with element by element
        for (int i = 0; i < arr.length; i++) {
            System.out.print("\u001b[41m  \u001B[0m");
            for (int j = 0; j < arr[i].length(); j++) {
                printColors(arr[i].charAt(j));
            }
            System.out.print("\u001b[41m  \u001B[0m\n");
        }

        //add a border to the array representation
        System.out.println("\u001b[41m ".repeat(arr[0].length()*3+4) + "\u001B[0m");
    }

    private static void printColors(char c) {
        //change text color depending on the element printed using ANSI escape codes
        switch (c) {
            case '.' -> System.out.print("\u001B[40m");
            case '0' -> System.out.print("\u001B[41m");
            //use background green color for 'F'
            case 'F' -> System.out.print("\u001b[7m\u001b[1m\u001B[36m");
            case 'S' -> System.out.print("\u001b[7m\u001b[1m\u001B[32m");
            case '@' -> System.out.print("\u001b[7m\u001b[1m\u001B[34m");
        }

        if (c == '0') {
            System.out.print("   ");
        } else {
            System.out.print(" " + c + " ");
        }

        //reset the color to default
        System.out.print("\u001B[0m");
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

    public static void main(String[] args) {
        System.out.println("Program started.");

        //read the file
        String[] readings = readFile("data.txt");

        //print the array
        printArray(readings);

        System.out.println("Program ended.");
    }
}