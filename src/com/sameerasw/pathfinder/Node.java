package com.sameerasw.pathfinder;

import java.util.*;

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

    @Override
    public int compareTo(Node other) {
        return Integer.compare(this.fScore, other.fScore);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;
        Node node = (Node) o;
        return x == node.x && y == node.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}