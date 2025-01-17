package com.sameerasw.pathfinder;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

import static com.sameerasw.pathfinder.PathPrinter.ANSI_GREEN;
import static com.sameerasw.pathfinder.PathPrinter.ANSI_RESET;

public class FileReader {
    // Read the file and return the content as a string array
    public String[] readFile() {
        // Read the file and return the content as a string array
        //look for data.txt and if it's not found open a file picker
        File file = new File("data.txt");
        String filename;
        if (file.exists()) {
            System.out.println(ANSI_GREEN + "File found: " + file.getName() + ANSI_RESET);
            filename = file.getName();
        } else {
            System.out.println("Default data.txt file not found, opening the file picker");

            //open file picker
            FileDialog fileDialog = new FileDialog((Frame) null, "Select File to Open");
            fileDialog.setMode(FileDialog.LOAD);
            fileDialog.setVisible(true);
            filename = fileDialog.getFile();

            if (filename == null) {
                System.out.println("No file selected, exiting the program");
                System.exit(0);
            } else {
                file = new File(fileDialog.getDirectory() + filename);
                System.out.println("File selected: " + file + "\n");

                //check if the file is a text file
                if (!filename.endsWith(".txt")) {
                    System.out.println("Invalid file type, please select a .txt file");
                    System.exit(0);
                }

                //re-check if the file exists
                if (!file.exists()) {
                    System.out.println("File not found, exiting the program");
                    System.exit(0);
                }
            }

        }

        String[] readings = new String[0];
        try {
            Scanner scanner = new Scanner(new File(String.valueOf(file)));
            while (scanner.hasNextLine()) {
                //read the file line by line
                String line = scanner.nextLine();
                readings = Arrays.copyOf(readings, readings.length + 1);
                readings[readings.length - 1] = line;
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while reading " + filename);
        }

        //strip spaces from the array if any
        for (int i = 0; i < readings.length; i++) {
            readings[i] = readings[i].strip();
        }

        //strip any lines that are not part of the maze or full at the end
        for (int i = readings.length - 1; i >= 0; i--) {
            if (readings[i].isEmpty()) {
                readings = Arrays.copyOf(readings, readings.length - 1);
            } else {
                break;
            }
        }

        return readings;
    }
}