package com.sameerasw.pathfinder;

import java.util.*;

import static com.sameerasw.pathfinder.PathPrinter.*;

public class Main {

    //-------------debugging purposes only-------------------
    public static boolean logging = false;
    //-------------------------------------------------------

    private static long shortestPath(String[] readings, Node start, Node goal) {
        // Implement the A* algorithm to find the shortest path from the start node to the goal node

        // Start the timer
        long startTime = System.nanoTime();

        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Map<Node, Node> cameFrom = new HashMap<>();
        Set<Node> visited = new HashSet<>();

        openSet.add(start);

        start.gScore = 0;
        start.fScore = heuristicCostEstimate(start, goal);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (current.equals(goal)) {
                // End the timer and reconstruct the path if the goal node is reached
                long endTime = System.nanoTime();
                reconstructPath(cameFrom, current, start, goal);
                return endTime - startTime;
            }

            for (Node neighbor : getNeighbors(current, readings, goal)) {
                if (visited.contains(neighbor)) {
                    // Skip if the neighbor has already been visited
                    continue;
                }

                // Calculate the tentative gScore (travel distance difference is always 1)
                int tentativeGScore = current.gScore + 1;

                if (tentativeGScore < neighbor.gScore) {
                    // Update the neighbor node if the tentative gScore is less than the current gScore
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

            visited.add(current); // Mark the current node as visited
        }

        System.out.println("No path found.");
        long endTime = System.nanoTime();
        return endTime - startTime;
    }

    private static List<Node> getNeighbors(Node current, String[] readings, Node goal) {
        // Get the neighbors of the current node (in the main directions at the furthest point)
        List<Node> neighbors = new ArrayList<>();
        int x = current.x;
        int y = current.y;

        int xStart = x;
        int yStart = y;

        int direction = 0;
        while (direction < 4) {
            do {
                switch (direction) {
                    // increment x or y based on the direction
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
                //if the goal is found, add it to the neighbors list
                System.out.println("Goal found at x:" + x + " y:" + y);
                neighbors.add(goal);
                break;
            }

            if (Node.getNode(new Node(x, y), readings) == '0') {
                //if the node is empty, add the previous node to the neighbors list
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
                //if the x or y has changed, add the node to the neighbors list
                if (logging)
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
        // Calculate the heuristic cost estimate using the Manhattan distance (x and y coordinate difference)
        return Math.abs(start.x - goal.x) + Math.abs(start.y - goal.y);
    }

    private static void reconstructPath(Map<Node, Node> cameFrom, Node current, Node start, Node goal) {
        // Reconstruct the path from the goal node to the start node
        System.out.println("\nPath found: ");
        List<Node> totalPath = new ArrayList<>();
        totalPath.add(current);

        while (cameFrom.containsKey(current)) {
            // Add the current node to the totalPath list while backtracking
            current = cameFrom.get(current);
            totalPath.add(current);

            if (current.equals(start)) {
                // End the loop if the current node is the start node
                break;
            }
        }

        // Reverse the totalPath list to print the middle moves in order
        Collections.reverse(totalPath);

        // Print the path of moves with directions
        String direction = "";
        for (int i = 0; i < totalPath.size(); i++) {
            if (i == 0) {
                System.out.println("Starting from: " + ANSI_GREEN + totalPath.get(i).x + ", " + totalPath.get(i).y + ANSI_RESET);
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
                System.out.println(ANSI_CYAN + direction + ANSI_RESET + " to: " + totalPath.get(i).x + ", " + totalPath.get(i).y);
            }
        }
        System.out.println("Ending at: " + ANSI_RED + goal.x + ", " + goal.y + ANSI_RESET);

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
        String[] readings = fileReader.readFile();

        int[] startFinish = printArray(readings);
        Node start = new Node(startFinish[0], startFinish[1]);
        Node goal = new Node(startFinish[2], startFinish[3]);

        long time = shortestPath(readings, start, goal);

        System.out.println("\nTime taken: " + ANSI_CYAN + ANSI_REVERSED + " " + time / 1000000 + " milliseconds " + ANSI_RESET);

        System.out.println("Program ended.");
        System.exit(0);

    }
}