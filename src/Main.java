import java.awt.*;
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

    //set logging to true to print the array at each step
    public static final boolean logging = true;
    public static final boolean fullLogging = true;

    public static int[] printArray(String[] arr) {
        //add a border to the array representation
        System.out.println(ANSI_RED_BACKGROUND + " ".repeat((arr[0].length()+2)*3) + ANSI_RESET);

        int[] start = new int[0];
        int[] finish = new int[0];
        int[] output = new int[0];

        // Print the nested array with element by element
        for (int i = 0; i < arr.length; i++) {
            System.out.print(ANSI_RED_BACKGROUND + "   " + ANSI_RESET);
            for (int j = 0; j < arr[i].length(); j++) {
                if (getNode(arr, i, j) == 'S') {
                    start = new int[]{j, i};
                } else if (getNode(arr, i, j) == 'F') {
                    finish = new int[]{j, i};
                }
                printColors(getNode(arr, i, j));
            }
            System.out.print(ANSI_RED_BACKGROUND + "   " + ANSI_RESET + "\n");
        }

        //add a border to the array representation
        System.out.println(ANSI_RED_BACKGROUND + " ".repeat((arr[0].length()+2)*3) + ANSI_RESET);

        output = new int[]{start[0], start[1], finish[0], finish[1]};
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
        } else if (c == '.') {
            System.out.print("   ");
        } else{
            System.out.print(" " + c + " ");
        }

        //reset the color to default
        System.out.print(ANSI_RESET);
    }

    public static String[] readFile() {
    // Read the file and return the content as a string array and return as an array using scanner

    //look for data.txt and if it's not found open a file picker
    File file = new File("data.txt");
    String filename;
    if (file.exists()) {
        System.out.println("File found: " + file.getName());
        filename = file.getName();
    } else {
        System.out.println("File not found, opening file picker");

        //open file picker
        FileDialog dialog = new FileDialog((Frame)null, "Select File to Open");
        dialog.setMode(FileDialog.LOAD);
        dialog.setVisible(true);
        filename = dialog.getFile();
        if (filename == null) {
            System.out.println("You cancelled the choice");
            System.exit(0);
        } else {
            System.out.println("You chose " + dialog.getDirectory() + filename);
        }
    }

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
        System.out.println("An error occurred while reading " + filename);
    }

    //strip spaces from the array
    for (int i = 0; i < readings.length; i++) {
        readings[i] = readings[i].strip();
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

    public static void printHistory(ArrayList<int[]> history) {
        //print the history in a readable format
        System.out.println("History: ");
        for (int[] node : history) {
            System.out.println(Arrays.toString(node));
        }
    }

    public static boolean inHistory(ArrayList<int[]> history, int[] node, int direction) {
        //check if the node is in the history with the movement direction
        for (int[] n : history) {
            if (n[0] == node[0] && n[1] == node[1] && n[2] == direction) {
                return true;
            }
        }
        return false;
    }

    public static void printFinalPath(ArrayList<int[]> history, int[] start, int[] finish) {
        //print the final path in a readable format with directional arrows
        System.out.println("\nFinal path: ");
        System.out.println("Starting node: " + start[0] + ", " + start[1]);
        for (int i = 0; i < history.size(); i++) {
            int[] node = history.get(i);
            if (i < history.size() - 1) {
                int[] nextNode = history.get(i + 1);
                if (node[0] == nextNode[0] && node[1] > nextNode[1]) {
                    System.out.println("Node " + Arrays.toString(node) + " ↑");
                } else if (node[0] == nextNode[0] && node[1] < nextNode[1]) {
                    System.out.println("Node " + Arrays.toString(node) + " ↓");
                } else if (node[0] > nextNode[0] && node[1] == nextNode[1]) {
                    System.out.println("Node " + Arrays.toString(node) + " ←");
                } else if (node[0] < nextNode[0] && node[1] == nextNode[1]) {
                    System.out.println("Node " + Arrays.toString(node) + " →");
                }
            }
        }
        System.out.println("Finishing node: " + finish[0] + ", " + finish[1]);
    }

    public static ArrayList<Object> movePlayer(String[] readings, int x, int y, int direction, ArrayList<int[]> history) {
        //move the player from the current position to the given direction until it hits a wall (0) and return the new array and the new position of the player as a nested array
        int[] currentPosition = new int[]{x, y};
        ArrayList<Object> output = new ArrayList<>();

        int iterations = 0;

        output.add(readings);
        output.add(currentPosition);
        output.add(direction);
        output.add(iterations);

        int lastDirection = 0;

        lastDirection = ((int[]) history.getLast())[2];
        if ((lastDirection+2)%4 == direction  && lastDirection != -1){
            if (logging) { System.out.println("Opposite direction, skipped" + direction + " " + lastDirection); }
            direction++;
            output.set(2, direction);
            output.set(3, iterations);
            return output;
        }

        while (true) {
            switch (direction) {
                case 0 -> currentPosition[1] -= 1;
                case 1 -> currentPosition[0] += 1;
                case 2 -> currentPosition[1] += 1;
                case 3 -> currentPosition[0] -= 1;
            }

            String nodeResult = String.valueOf(getNode(readings, currentPosition[1], currentPosition[0]));

            if (logging) { System.out.println("Checking node: " + Arrays.toString(currentPosition) + " moving: " + direction + " node: " + nodeResult); }

            //if the player hits the finish node return the array
            if (nodeResult.equals("F")) {
                //set the output array to the new readings and the new position of the player as a nested array
                output.set(1, currentPosition);
                output.set(0, readings);
                output.set(2, direction);
                output.set(3, iterations);
                System.out.println("Found the finish node!");
                return output;

            } else if (nodeResult.equals("0")){
                //if the player hits a wall return the array
                if (logging) { System.out.println("Hit a wall"); }

                //go back a step
                switch (direction) {
                    case 0 -> currentPosition[1] += 1;
                    case 1 -> currentPosition[0] -= 1;
                    case 2 -> currentPosition[1] -= 1;
                    case 3 -> currentPosition[0] += 1;
                }

                //increase the direction if it's less than 3 or leave it at 3
                if (direction < 4 && iterations < 1) {
                    direction++;
                }

                output.set(0, readings);
                output.set(1, currentPosition);
                output.set(2, direction);
                output.set(3, iterations);
                return output;

            } else {
                //if the player hits an empty or visited node update the current visited node with @ if it's not the start node or the finish node
                if (!nodeResult.equals("S")){
                    readings[currentPosition[1]] = readings[currentPosition[1]].substring(0, currentPosition[0]) + "@" + readings[currentPosition[1]].substring(currentPosition[0] + 1);
                }

                output.set(0, readings);
                output.set(1, currentPosition);
                output.set(3, iterations);
                if (logging) { System.out.println("Keep moving to direction: " + direction); }
                iterations++;
            }

            if (fullLogging) { printArray(readings); }
        }
    }

    public static String shortestPath(String[] readings, int[] nodeInfo) {
        //implement the shortest path algorithm
        int direction = 0;
        ArrayList<int[]> history = new ArrayList<>();
        ArrayList<int[]> fullHistory = new ArrayList<>();
        boolean keepLooking = true;

        int[] start = new int[]{nodeInfo[0], nodeInfo[1]};

        history.add(new int[]{nodeInfo[0], nodeInfo[1], -1});
        fullHistory.add(new int[]{nodeInfo[0], nodeInfo[1], -1});
        if (logging) { printHistory(history); }

        while (keepLooking) {

            if (logging) { System.out.println("\n" + ANSI_GREEN + ANSI_REVERSED + "searching started" + ANSI_RESET); }
            int[] prevPosition = new int[]{nodeInfo[0], nodeInfo[1]};

            //move the player to the given direction
            if (inHistory(fullHistory, nodeInfo, direction)) {
                if (logging) { System.out.println("Node already visited, reverting to previous node"); }
                direction++;
            } else {
                ArrayList<Object> output = movePlayer(readings, nodeInfo[0], nodeInfo[1], direction, history);
                readings = (String[]) output.get(0);
                nodeInfo = (int[]) output.get(1);
                direction = (int) output.get(2);
            }

            if (logging) { System.out.println("previous node: " + Arrays.toString(prevPosition) + " → current node: " + Arrays.toString(nodeInfo)); }


            //if the new node is different from the previous node update the nodeInfo and reset the direction to 0
            if (prevPosition[0] != nodeInfo[0] || prevPosition[1] != nodeInfo[1]) {
                //add the new position to the history with the direction as a stack
                history.add(new int[]{prevPosition[0], prevPosition[1], direction});
                fullHistory.add(new int[]{prevPosition[0], prevPosition[1], direction});

//                printHistory(history);

                direction = 0;
                if (logging) { System.out.println("node changed, direction reset"); }

            }


            //if the player hits the finish node return the path
            if (getNode(readings, nodeInfo[1], nodeInfo[0]) == 'F') {
                //clear the console
                System.out.println("\033[H\033[2J");
                System.out.flush();

                //print the final array
                printArray(readings);
                printFinalPath(history, start, new int[]{nodeInfo[0], nodeInfo[1]});
                return "Path found";

            } else if (((nodeInfo[0] != start[0] || nodeInfo[1] != start[1]) || history.size() > 1 )  && direction == 4) {
                //if the player hits a wall or a visited node and the direction is 3 go back to the previous node
                int[] previousNode = history.getLast();

                //remove the last elements from the history where the node is the same as the previous node
                while (!history.isEmpty() && history.getLast()[0] == previousNode[0] && history.getLast()[1] == previousNode[1]) {
                    history.removeLast();
                }

                if (logging) { System.out.println("Reverted history to previous checkpoint"); }
//                if (logging) { printHistory(history); }

                //update the nodeInfo and direction to the previous node
                nodeInfo = new int[]{previousNode[0], previousNode[1]};
                direction = previousNode[2];

//                keepLooking = false; //-------------------------------------------------------------KILL SWITCH

            } else if (nodeInfo[0] == start[0] && nodeInfo[1] == start[1] && direction == 4) {
                keepLooking = false;
                System.out.println("Nowhere to go, No path found.");
            }
        }
        return "No path found.";
    }

    public static void main(String[] args) {
        System.out.println("Program started. Pick the file");

        //read the file
        String[] readings = readFile();

        //print the array
        int[] nodeInfo = printArray(readings);
        System.out.println("Start position: " + nodeInfo[0] + ", " + nodeInfo[1]);

        //get the shortest path
        String path = shortestPath(readings, nodeInfo);

        System.out.println(path);

        System.out.println("Program ended.");
    }
}