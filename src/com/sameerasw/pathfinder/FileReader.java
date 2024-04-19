package com.sameerasw.pathfinder;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

import static com.sameerasw.pathfinder.PathPrinter.*;

public class FileReader {
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
            FileDialog dialog = new FileDialog((Frame) null, "Select File to Open");
            dialog.setMode(FileDialog.LOAD);
            dialog.setVisible(true);
            filename = dialog.getFile();
            if (filename == null) {
                System.out.println(ANSI_RED_BACKGROUND + "You cancelled the choice" + ANSI_RESET);
                System.exit(0);
            } else {
                System.out.println("You chose " + dialog.getDirectory() + filename);
            }
        }

        String[] readings = new String[0];
        try {
            Scanner scanner = new Scanner(new File(filename));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                readings = Arrays.copyOf(readings, readings.length + 1);
                readings[readings.length - 1] = line;
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while reading " + filename);
        }

        //strip spaces from the array
        for (int i = 0; i < readings.length; i++) {
            readings[i] = readings[i].strip();
        }

        return readings;
    }
}