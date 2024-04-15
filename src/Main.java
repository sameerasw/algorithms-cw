import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
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

    public static int[] printArray(String[] arr) {
        //add a border to the array representation
        System.out.println(ANSI_RED_BACKGROUND + " ".repeat((arr[0].length()+2)*3) + ANSI_RESET);

        int[] output = new int[0];

        // Print the nested array with element by element
        for (int i = 0; i < arr.length; i++) {
            System.out.print(ANSI_RED_BACKGROUND + "   " + ANSI_RESET);
            for (int j = 0; j < arr[i].length(); j++) {
                if (getNode(arr, i, j) == 'S') {
                    output = new int[]{j, i};
                } else if (getNode(arr, i, j) == 'F') {
                    output = new int[]{j, i};
                }
                printColors(getNode(arr, i, j));
            }
            System.out.print(ANSI_RED_BACKGROUND + "   " + ANSI_RESET + "\n");
        }

        //add a border to the array representation
        System.out.println(ANSI_RED_BACKGROUND + " ".repeat((arr[0].length()+2)*3) + ANSI_RESET);

        return output;
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

    public static String shortestPath(String[] readings, int[] nodeInfo) {
        //implement the shortest path algorithm
        int direction = 0;
        ArrayList<int[]> history = new ArrayList<>();
        boolean keepLooking = true;

        while (keepLooking) {

            //add the new position to the history with the direction as a stack
            history.add(new int[]{nodeInfo[0], nodeInfo[1], direction});

            System.out.println("searching started");
            //move the player to the given direction
            ArrayList output = movePlayer(readings, nodeInfo[0], nodeInfo[1], direction);
            readings = (String[]) output.get(0);
            nodeInfo = (int[]) output.get(1);


            //if the player hits the finish node return the path
            if (getNode(readings, nodeInfo[0], nodeInfo[1]) == 'F') {
                return "Path found: " + history;
            } else if (history.size() > 0 && direction < 3) {
                direction++;
            } else if (history.size() > 0 && direction == 3) {
                //if the player hits a wall or a visited node pop the last element from the history and move the player to the previous position
                int[] last = history.get(history.size() - 1);
                history.remove(history.size() - 1);
                nodeInfo = new int[]{last[0], last[1]};
                direction = last[2]+1;
//                keepLooking = false;
            } else {
                keepLooking = false;
            }

//            keepLooking = false;
        }
        System.out.println(direction);
        return "No path found.";
    }

    public static ArrayList movePlayer(String[] readings, int x, int y, int direction) {
        //move the player from the current position to the given direction until it hits a wall (0) and return the new array and the new position of the player as a nested array
        int[] currentPosition = new int[]{x, y};
        ArrayList<Object> output = new ArrayList<>();
        output.add(readings);
        output.add(currentPosition);

        while (getNode(readings, currentPosition[0], currentPosition[1]) != '0') {

            switch (direction) {
                case 0 -> currentPosition[1] -= 1;
                case 1 -> currentPosition[0] += 1;
                case 2 -> currentPosition[1] += 1;
                case 3 -> currentPosition[0] -= 1;
            }

            System.out.println("Current position: " + Arrays.toString(currentPosition) + " Direction: " + direction);

            //if the player hits the finish node return the array
            if (getNode(readings, currentPosition[1], currentPosition[0]) == 'F') {
                //set the output array to the new readings and the new position of the player as a nested array
                output.set(1, currentPosition);
                System.out.println("Found the finish node");
                return output;
            } else if (getNode(readings, currentPosition[1], currentPosition[0]) == '0') {
                //if the player hits a wall return the array
                System.out.println("Hit a wall");
                return output;
            } else {
                //if the player hits a empty or visited node update the current visited node with @
                readings[currentPosition[1]] = readings[currentPosition[1]].substring(0, currentPosition[0]) + "@" + readings[currentPosition[1]].substring(currentPosition[0] + 1);
                output.set(0, readings);
                System.out.println("Visited node: " + Arrays.toString(currentPosition));
            }

            printArray(readings);
        }

        return output;
    }

    public static void main(String[] args) {
        System.out.println("Program started.");

        //read the file
        String[] readings = readFile("data.txt");

        //print the array
        int[] nodeInfo = printArray(readings);
        System.out.println("Start position: " + Arrays.toString(nodeInfo));

        //get the shortest path
        String path = shortestPath(readings, nodeInfo);

        System.out.println(path);

        nodeInfo = printArray(readings);

        System.out.println("Program ended.");
    }
}