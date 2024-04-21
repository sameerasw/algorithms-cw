package com.sameerasw.pathfinder;

import java.util.*;

import static com.sameerasw.pathfinder.PathPrinter.*;

public class Main {

    //-------------debugging purposes only-------------------

    //0: no logging, 1: print the finishing nodes and the maze, 2: print the steps and the history at each step, 3: print the full history (use only for debugging small mazes)
    private static final int loggingLevel = 3;
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
    private static void shortestPath(String[] readings, Node start, Node goal) {
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        openSet.add(start);

        Map<Node, Node> cameFrom = new HashMap<>();

        start.gScore = 0;
        start.fScore = heuristicCostEstimate(start, goal);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (current.equals(goal)) {
                // Path found
                reconstructPath(cameFrom, current, start, goal);
                return;
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

    private static List<Node> reconstructPath(Map<Node, Node> cameFrom, Node current, Node start, Node goal) {
        System.out.println("\nPath found: ");
        List<Node> totalPath = new ArrayList<>();
        totalPath.add(current);
        System.out.println("Starting from: " + start.x + ", " + start.y);

        //print the rest of the path in the format of "direction to (x, y)"
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            totalPath.add(current);

            System.out.println(switchDirection(current, cameFrom.get(current)) + "to: " + current.x + ", " + current.y);

            //end the loop if the current node is the start node
            if (current.equals(start)) {
                break;
            }
        }

        System.out.println("Ending at: " + goal.x + ", " + goal.y);

        Collections.reverse(totalPath);
        return totalPath;
    }

    public static void main() {
        System.out.println("Program started.");
        FileReader fileReader = new FileReader();
        String[] readings = fileReader.readFile();

        int[] startFinish = printArray(readings);
        Node start = new Node(startFinish[0], startFinish[1]);
        Node goal = new Node(startFinish[2], startFinish[3]);

        shortestPath(readings, start, goal);

        System.out.println("Program ended.");
        System.exit(0);

    }
}