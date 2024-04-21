package com.sameerasw.pathfinder;

import java.util.*;

import static com.sameerasw.pathfinder.PathPrinter.*;

public class Main {

    //-------------debugging purposes only-------------------

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

    //-------------------------------------------------------
    private static long shortestPath(String[] readings, Node start, Node goal) {
        //start timer
        long startTime = System.nanoTime();

        PriorityQueue<Node> openSet = new PriorityQueue<>();
        openSet.add(start);

        Map<Node, Node> cameFrom = new HashMap<>();

        start.gScore = 0;
        start.fScore = heuristicCostEstimate(start, goal);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (current.equals(goal)) {
                // Path found
                //stop timer
                long endTime = System.nanoTime();
                reconstructPath(cameFrom, current, start, goal);
                return endTime - startTime;
            }

            for (Node neighbor : getNeighbors(current, readings, goal)) {
                int tentativeGScore = current.gScore + distBetween(current, neighbor);

                if (tentativeGScore < neighbor.gScore) {
                    // Only put the neighbor in the cameFrom map if it's not already present
                    if (!cameFrom.containsKey(neighbor)) {
                        cameFrom.put(neighbor, current);
                    }
                    neighbor.gScore = tentativeGScore;
                    neighbor.fScore = neighbor.gScore + heuristicCostEstimate(neighbor, goal);

                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }
        // Path not found
        long endTime = System.nanoTime();
        System.out.println("No path found.");
        return endTime - startTime;
    }

    private static int distBetween(Node current, Node neighbor) {
        return 1;
    }

    private static List<Node> getNeighbors(Node current, String[] readings, Node goal) {
        List<Node> neighbors = new ArrayList<>();
        int x = current.x;
        int y = current.y;

        int xStart = x;
        int yStart = y;

        int direction = 0;
        while (direction < 4) {

            do {
                switch (direction) {
                    case 0 -> {
                        y--;
                    }
                    case 1 -> {
                        x++;
                    }
                    case 2 -> {
                        y++;
                    }
                    case 3 -> {
                        x--;
                    }
                }
            } while ((Node.getNode(new Node(x, y), readings) != '0') && (Node.getNode(new Node(x, y), readings) != 'F'));

            if (Node.getNode(new Node(x, y), readings) == 'F') {
                System.out.println("Goal found at x:" + x + " y:" + y);
                neighbors.add(goal);
                break;
            }

            if (Node.getNode(new Node(x, y), readings) == '0') {
                switch (direction) {
                    case 0 -> {
                        y++;
                    }
                    case 1 -> {
                        x--;
                    }
                    case 2 -> {
                        y--;
                    }
                    case 3 -> {
                        x++;
                    }
                }
                neighbors.add(new Node(x, y));
            }

            if (x != xStart || y != yStart) {
                if (fullLogging)
                    System.out.println("direction: " + direction + " x:" + x + " y:" + y);
                neighbors.add(new Node(x, y));
            }

            //reset x and y
            x = xStart;
            y = yStart;

            direction++;
        }

        return neighbors;
    }

    private static int heuristicCostEstimate(Node start, Node goal) {
        return Math.abs(start.x - goal.x) + Math.abs(start.y - goal.y);
    }

    private static void reconstructPath(Map<Node, Node> cameFrom, Node current, Node start, Node goal) {
        System.out.println("\nPath found: ");
        List<Node> totalPath = new ArrayList<>();
        totalPath.add(current);

        // Add the rest of the path to the totalPath list
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            totalPath.add(current);

            // End the loop if the current node is the start node
            if (current.equals(start)) {
                break;
            }
        }

        // Reverse the totalPath list to print the middle moves in reverse order
        Collections.reverse(totalPath);

        //get the direction travelled
        String direction = "";
        for (int i = 0; i < totalPath.size(); i++) {
            if (i == 0) {
                System.out.println("Starting from: " + totalPath.get(i).x + ", " + totalPath.get(i).y);
            } else {
                if (totalPath.get(i).x > totalPath.get(i - 1).x) {
                    direction = "right";
                } else if (totalPath.get(i).x < totalPath.get(i - 1).x) {
                    direction = "left";
                } else if (totalPath.get(i).y > totalPath.get(i - 1).y) {
                    direction = "down";
                } else if (totalPath.get(i).y < totalPath.get(i - 1).y) {
                    direction = "up";
                }
                System.out.println(direction + " to: " + totalPath.get(i).x + ", " + totalPath.get(i).y);
            }
        }

        // Print the goal node
        System.out.println("Ending at: " + goal.x + ", " + goal.y);

        // Print the total number of moves
        System.out.print("\nTotal moves: " + (totalPath.size() - 1));

        // Print the total number of blocks travelled calculated by the each mode's coordinate difference
        int totalBlocks = 0;
        for (int i = 0; i < totalPath.size() - 1; i++) {
            totalBlocks += Math.abs(totalPath.get(i).x - totalPath.get(i + 1).x) + Math.abs(totalPath.get(i).y - totalPath.get(i + 1).y);
        }
        System.out.print("   Total steps of blocks: " + totalBlocks + "\n");

    }

    public static void main() {
        System.out.println("Program started.");
        FileReader fileReader = new FileReader();
        String[] readings = fileReader.readFile();

        int[] startFinish = printArray(readings);
        Node start = new Node(startFinish[0], startFinish[1]);
        Node goal = new Node(startFinish[2], startFinish[3]);

        long time = shortestPath(readings, start, goal);

        System.out.println("\nTime taken: " + time/1000000 + " milliseconds");

        System.out.println("Program ended.");
        System.exit(0);

    }
}