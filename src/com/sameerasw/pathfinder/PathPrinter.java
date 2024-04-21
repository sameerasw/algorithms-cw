package com.sameerasw.pathfinder;

import java.util.ArrayList;
import java.util.Arrays;

public class PathPrinter {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    public static final String ANSI_REVERSED = "\u001b[7m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_RED = "\u001B[31m";

    public static int[] printArray(String[] arr) {
        int[] start = new int[0];
        int[] finish = new int[0];
        int[] output;
        System.out.println(ANSI_RED_BACKGROUND + " ".repeat((arr[0].length() + 2) * 3) + ANSI_RESET);

        for (int i = 0; i < arr.length; i++) {
            System.out.print(ANSI_RED_BACKGROUND + "   " + ANSI_RESET);
            for (int j = 0; j < arr[i].length(); j++) {
                printColors(arr[i].charAt(j));
                if (arr[i].charAt(j) == 'S') {
                    start = new int[]{j, i};
                } else if (arr[i].charAt(j) == 'F') {
                    finish = new int[]{j, i};
                }
            }
            System.out.print(ANSI_RED_BACKGROUND + "   " + ANSI_RESET + "\n");
        }

        System.out.println(ANSI_RED_BACKGROUND + " ".repeat((arr[0].length() + 2) * 3) + ANSI_RESET);

        output = new int[]{start[0], start[1], finish[0], finish[1]};
        return output;
    }

    private static void printColors(char c) {
        switch (c) {
            case '.' -> System.out.print(ANSI_BLACK_BACKGROUND);
            case '0' -> System.out.print(ANSI_RED_BACKGROUND);
            case 'F' -> System.out.print(ANSI_REVERSED + ANSI_CYAN);
            case 'S' -> System.out.print(ANSI_REVERSED + ANSI_GREEN);
            case '@' -> System.out.print(ANSI_REVERSED + ANSI_BLUE);
        }

        if (c == '0' || c == '.') {
            System.out.print("   ");
        } else {
            System.out.print(" " + c + " ");
        }

        System.out.print(ANSI_RESET);
    }

    public static String switchDirection(int i) {
        return switch (i) {
            case 0 -> "Move up" + ANSI_CYAN + " ↑ " + ANSI_RESET;
            case 1 -> "Move right" + ANSI_CYAN + " → " + ANSI_RESET;
            case 2 -> "Move down" + ANSI_CYAN + " ↓ " + ANSI_RESET;
            case 3 -> "Move left" + ANSI_CYAN + " ← " + ANSI_RESET;
            default -> "Starting from ";
        };
    }
}