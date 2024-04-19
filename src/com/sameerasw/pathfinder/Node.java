package com.sameerasw.pathfinder;

public class Node {

    public Node(int x, int y, int direction) {
    }

    // Getters and setters
    public static char getNode(String[] readings, int x, int y) {
        //return the node at the given x and y coordinates
        if (x < 0 || y < 0 || x >= readings.length || y >= readings[0].length()) {
            //if it's out of range return 0
            return '0';
        }
        return readings[x].charAt(y);
    }
}