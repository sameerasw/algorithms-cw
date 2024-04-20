package com.sameerasw.pathfinder;

import java.util.ArrayList;
import java.util.Arrays;

import static com.sameerasw.pathfinder.PathPrinter.*;

public class Main {

    //set logging level to print the movement steps at each step
    //0: no logging, 1: print the finishing nodes and the maze, 2: print the steps and the history at each step, 3: print the full history (use only for debugging small mazes)
    private static final int loggingLevel = 0;
    public static boolean logging;
    public static boolean moveLogging;
    public static boolean fullLogging;

    static {
        switch (loggingLevel) {
            case 0 -> {
                logging = false;
                moveLogging = false;
                fullLogging = false;
            }
            case 1 -> {
                logging = true;
                moveLogging = false;
                fullLogging = false;
            }
            case 2 -> {
                logging = true;
                moveLogging = true;
                fullLogging = false;
            }
            case 3 -> {
                logging = true;
                moveLogging = true;
                fullLogging = true;
            }
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

    public static ArrayList<Object> movePlayer(String[] readings, int x, int y, int direction, ArrayList<int[]> history) {
        //move the player from the current position to the given direction until it hits a wall (0) and return the new array and the new position
        int[] currentPosition = new int[]{x, y};
        ArrayList<Object> output = new ArrayList<>();
        int moves = 0;
        output.add(readings);
        output.add(currentPosition);
        output.add(direction);
        output.add(moves);
        int lastDirection;

        if (history.size() > 1) {
            lastDirection = ((int[]) history.getLast())[2];
            if (((lastDirection + 2) % 4 == direction && lastDirection != -1)) {
                //prevents the player from going back to the previous node
                if (moveLogging) { System.out.println("Opposite direction, skipped : tried: " + direction + ". Last: " + lastDirection); }
                direction++;
                output.set(2, direction);
                output.set(3, moves);
                return output;
            }
        }

        while (true) {

            switch (direction) {
                //update the coordinates based on the direction
                case 0 -> currentPosition[1] -= 1;
                case 1 -> currentPosition[0] += 1;
                case 2 -> currentPosition[1] += 1;
                case 3 -> currentPosition[0] -= 1;
            }
            String nodeResult = String.valueOf(Node.getNode(readings, currentPosition[1], currentPosition[0]));
            if (moveLogging) { System.out.println("Checking node: " + Arrays.toString(currentPosition) + " moving: " + direction + " node: " + nodeResult); }

            if (nodeResult.equals("F")) {
                //Player hits the finish node
                moves++;
                output.set(1, currentPosition);
                output.set(0, readings);
                output.set(2, direction);
                output.set(3, moves);
                if (logging) { System.out.println(ANSI_GREEN + "A path found. In " + (history.getLast()[3] + moves) + " moves." + ANSI_RESET + " Continuing search."); }
                return output;

            } else if (nodeResult.equals("0")) {
                //Player hits a wall
                if (moveLogging) { System.out.println("Hit a wall"); }

                //go back to the previous node and update the direction
                switch (direction) {
                    case 0 -> currentPosition[1] += 1;
                    case 1 -> currentPosition[0] -= 1;
                    case 2 -> currentPosition[1] -= 1;
                    case 3 -> currentPosition[0] += 1;
                }
                if (direction < 4 && moves < 1) {
                    direction++;
                }

                output.set(0, readings);
                output.set(1, currentPosition);
                output.set(2, direction);
                output.set(3, moves);
                return output;

            } else {
                //if the player hits an empty or visited node update the current visited node with @ if it's not the start node or the finish node
                if (!nodeResult.equals("S")) {
                    readings[currentPosition[1]] = readings[currentPosition[1]].substring(0, currentPosition[0]) + "@" + readings[currentPosition[1]].substring(currentPosition[0] + 1);
                }

                output.set(0, readings);
                output.set(1, currentPosition);
                output.set(3, moves);
                if (moveLogging) {
                    System.out.println("Keep moving to direction: " + direction);
                }
                moves++;
            }

            if (fullLogging) {
                printArray(readings);
            }
        }
    }

    public static long shortestPath(String[] readings, int[] nodeInfo) {
        //implement the shortest path algorithm
        int direction = 0;
        ArrayList<int[]> history = new ArrayList<>();
        ArrayList<int[]> results = new ArrayList<>();
        boolean keepLooking = true;
        int moves = 0;

        int[] start = new int[]{nodeInfo[0], nodeInfo[1]};

        history.add(new int[]{nodeInfo[0], nodeInfo[1], -1, 0});
        if (moveLogging) {
            PathPrinter.printHistory(history);
        }

        System.out.println("\nSearching...");

        long endTime = 0;
        while (keepLooking) {

            if (moveLogging) {
                System.out.println("\n" + ANSI_GREEN + ANSI_REVERSED + "searching started" + ANSI_RESET);
            }
            int[] prevPosition = new int[]{nodeInfo[0], nodeInfo[1]};

            //move the player to the given direction
            if (inHistory(history, nodeInfo, direction)) {
                if (moveLogging) {
                    System.out.println("Node already visited, reverting to previous node");
                }
                direction++;
            } else if (direction < 4) {
                ArrayList<Object> output = movePlayer(readings, nodeInfo[0], nodeInfo[1], direction, history);
                readings = (String[]) output.get(0);
                nodeInfo = (int[]) output.get(1);
                direction = (int) output.get(2);
                moves = (int) output.get(3);
            }

            if (moveLogging) {
                System.out.println("previous node: " + Arrays.toString(prevPosition) + " → current node: " + Arrays.toString(nodeInfo));
            }


            //if the new node is different from the previous node update the nodeInfo and reset the direction to 0
            if ((prevPosition[0] != nodeInfo[0] || prevPosition[1] != nodeInfo[1]) && (Node.getNode(readings, nodeInfo[1], nodeInfo[0]) != 'F')) {
                //add the new position to the history with the direction as a stack
                history.add(new int[]{prevPosition[0], prevPosition[1], direction, (history.getLast()[3] + moves)});

                if (fullLogging) {
                    printHistory(history);
                }

                direction = 0;
                if (moveLogging) {
                    System.out.println("node changed, direction reset");
                }
            }

            //if the player hits the finish node return the path
            if (Node.getNode(readings, nodeInfo[1], nodeInfo[0]) == 'F') {
                //add the previous node and the finish node to the history
                history.add(new int[]{prevPosition[0], prevPosition[1], direction, (history.getLast()[3] + moves)});
                history.add(new int[]{nodeInfo[0], nodeInfo[1], direction, (history.getLast()[3] + moves)});

                //add current history to the results if the moves count is smaller than the previous results
                if (results.isEmpty()) {
                    results = new ArrayList<>(history);
                } else if (history.size() < results.size()) {
                    results.clear();
                    results = new ArrayList<>(history);
                }

                history.removeLast();

                //revert the current node to the previous node
                nodeInfo = new int[]{history.getLast()[0], history.getLast()[1]};
                direction = history.getLast()[2] + 1;
                history.removeLast();
            }

            if (((nodeInfo[0] != start[0] || nodeInfo[1] != start[1]) || history.size() > 1) && direction == 4) {
                //if the player hits a wall or a visited node and the direction is 3 go back to the previous node

                int[] previousNode = history.getLast();

                //remove the last elements from the history where the node is the same as the previous node
                if (!history.isEmpty() && history.getLast()[0] == previousNode[0] && history.getLast()[1] == previousNode[1]) {
                    history.removeLast();
                }

                if (moveLogging) {
                    System.out.println("Reverted history to previous checkpoint");
                }
//                if (fullLogging) { printHistory(history); }

                //update the nodeInfo and direction to the previous node
                nodeInfo = new int[]{previousNode[0], previousNode[1]};
                direction = previousNode[2] + 1;

//                keepLooking = false; //-------------------------------------------------------------KILL SWITCH

            } else if (nodeInfo[0] == start[0] && nodeInfo[1] == start[1] && direction == 4) {
                //stop the timer
                endTime = System.nanoTime();

                keepLooking = false;
                if (logging) { System.out.println("Nowhere to go.\n"); }
                if (logging) { printArray(readings); }

                finalResult(results);
            }
        }
        return endTime;
    }

    public static void main() {
        System.out.println("Program started.");

        //read the file
        String[] readings = new FileReader().readFile();

        //print the array

        int[] nodeInfo = PathPrinter.printArray(readings);

        //start the time calculation
        long startTime = System.nanoTime();

        //get the shortest path
        long endTime = shortestPath(readings, nodeInfo);
        long duration = (endTime - startTime) / 1000000;
        System.out.println("\nShortest path calculation time: " + ANSI_CYAN + duration + "ms." + ANSI_RESET + " Program ended.");
        System.exit(0);
    }
}