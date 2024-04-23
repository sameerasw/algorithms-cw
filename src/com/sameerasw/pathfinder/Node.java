package com.sameerasw.pathfinder;

import java.util.Objects;

public class Node implements Comparable<Node> {
    int x, y;
    int gScore = Integer.MAX_VALUE;
    int fScore = Integer.MAX_VALUE;

    Node(int x, int y) {
        this.x = x;
        this.y = y;
    }

    static char getNode(Node node, String[] readings) {
        // Return the node as a character from the readings
        try {
            return readings[node.y].charAt(node.x);
        } catch (IndexOutOfBoundsException e) {
            return '0';
        }
    }

    public static int hCost(Node start, Node finish) {
        // Calculate the heuristic cost
        return Math.abs(start.x - finish.x) + Math.abs(start.y - finish.y);
    }

    @Override
    public int compareTo(Node other) {
        // Compare the fScore of the nodes
        return Integer.compare(this.fScore, other.fScore);
    }

    @Override
    public boolean equals(Object o) {
        // Check if the nodes are equal
        if (this == o) return true;
        if (!(o instanceof Node node)) return false;
        return x == node.x && y == node.y;
    }

    @Override
    public int hashCode() {
        // Generate a hash code for the nodes
        return Objects.hash(x, y);
    }
}