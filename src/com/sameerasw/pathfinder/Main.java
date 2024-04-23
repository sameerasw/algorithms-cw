package com.sameerasw.pathfinder;

import java.util.*;

import static com.sameerasw.pathfinder.Node.hCost;
import static com.sameerasw.pathfinder.PathPrinter.*;

public class Main {

    //-------------debugging purposes only-------------------
    public static boolean logging = false;
    //-------------------------------------------------------

    private static long shortestPath(String[] maze, Node start, Node finish) {
        // Implement the A* algorithm to find the shortest path from the start node to the finish node

        // Start the timer
        long startTime = System.nanoTime();

        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Map<Node, Node> history = new HashMap<>();
        Set<Node> visitedNodes = new HashSet<>();

        openSet.add(start);

        start.gScore = 0;
        start.fScore = hCost(start, finish);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (current.equals(finish)) {
                // End the timer and reconstruct the path if the finish node is reached
                long endTime = System.nanoTime();
                generateFinalPath(history, current, start, finish);
                return endTime - startTime;
            }

            for (Node neighbor : getNeighbors(current, maze, finish)) {
                if (visitedNodes.contains(neighbor)) {
                    // Skip if the neighbor has already been visitedNodes
                    continue;
                }

                // Calculate the tentative gScore (travel distance difference is always 1)
                int newgScore = current.gScore + 1;

                if (newgScore < neighbor.gScore) {
                    // Update the neighbor node if the tentative gScore is less than the current gScore
                    if (!history.containsKey(neighbor)) {
                        history.put(neighbor, current);
                    }
                    neighbor.gScore = newgScore;
                    neighbor.fScore = neighbor.gScore + hCost(neighbor, finish);

                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }

            visitedNodes.add(current); // Mark the current node as visitedNodes
        }

        System.out.println("No path found.");
        long endTime = System.nanoTime();
        return endTime - startTime;
    }

    private static List<Node> getNeighbors(Node active, String[] maze, Node finish) {
        // Get the neighbors of the active node (in the main directions at the furthest point)
        List<Node> neighbors = new ArrayList<>();
        int x = active.x;
        int y = active.y;

        int xStart = x;
        int yStart = y;

        int direction = 0;
        while (direction < 4) {
            do {
                switch (direction) {
                    // increment x or y based on the direction
                    case 0 -> y--;
                    case 1 -> x++;
                    case 2 -> y++;
                    case 3 -> x--;
                }
            } while ((Node.getNode(new Node(x, y), maze) != '0') && (Node.getNode(new Node(x, y), maze) != 'F'));

            if (Node.getNode(new Node(x, y), maze) == 'F') {
                //if the finish is found, add it to the neighbors list
                System.out.println("Goal found at x:" + x + " y:" + y);
                neighbors.add(finish);
                break;
            }

            if (Node.getNode(new Node(x, y), maze) == '0') {
                //if the node is empty, add the previous node to the neighbors list
                switch (direction) {
                    case 0 -> y++;
                    case 1 -> x--;
                    case 2 -> y--;
                    case 3 -> x++;
                }
                neighbors.add(new Node(x, y));
            }

            if (x != xStart || y != yStart) {
                //if the x or y has changed, add the node to the neighbors list
                if (logging)
                    System.out.println("Checking direction: " + direction + " x:" + x + " y:" + y);
                neighbors.add(new Node(x, y));
            }

            //reset x and y
            x = xStart;
            y = yStart;

            direction++;
        }
        return neighbors;
    }

    private static void generateFinalPath(Map<Node, Node> history, Node active, Node start, Node finish) {
        // Reconstruct the path from the finish node to the start node
        System.out.println("\nPath found: ");
        List<Node> totalPath = new ArrayList<>();
        totalPath.add(active);

        while (history.containsKey(active)) {
            // Add the active node to the totalPath list while backtracking
            active = history.get(active);
            totalPath.add(active);

            if (active.equals(start)) {
                // End the loop if the active node is the start node
                break;
            }
        }

        // Reverse the totalPath list to print the middle moves in order
        Collections.reverse(totalPath);

        // Print the path of moves with directions
        String direction = "";
        for (int i = 0; i < totalPath.size(); i++) {
            if (i == 0) {
                System.out.println("> Starting from: (" + ANSI_GREEN + totalPath.get(i).x + ", " + totalPath.get(i).y + ANSI_RESET + ")");
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
                System.out.println(i + ". Move " + ANSI_CYAN + direction + ANSI_RESET + " to: (" + totalPath.get(i).x + ", " + totalPath.get(i).y + ")");
            }
        }
        System.out.println("> Finishing at: (" + ANSI_RED + finish.x + ", " + finish.y + ANSI_RESET + ")\n");

        // Print the total number of moves
        System.out.print("\nTotal moves: " + (totalPath.size() - 1));

        // Print the total number of blocks travelled calculated by the node's coordinate difference
        int totalBlocks = 0;
        for (int i = 0; i < totalPath.size() - 1; i++) {
            totalBlocks += Math.abs(totalPath.get(i).x - totalPath.get(i + 1).x) + Math.abs(totalPath.get(i).y - totalPath.get(i + 1).y);
        }
        System.out.print("   Total steps of blocks: " + totalBlocks + "\n");
    }

    public static void main(String[] args) {
        System.out.println("Program started.");
        FileReader fileReader = new FileReader();
        String[] maze = fileReader.readFile();

        int[] startFinish = printArray(maze);
        Node start = new Node(startFinish[0], startFinish[1]);
        Node finish = new Node(startFinish[2], startFinish[3]);

        long time = shortestPath(maze, start, finish);

        System.out.println("\nTime taken: " + ANSI_CYAN + ANSI_REVERSED + " " + time / 1000000 + " milliseconds " + ANSI_RESET);

        System.out.println("Program ended.");
        System.exit(0);

    }
}