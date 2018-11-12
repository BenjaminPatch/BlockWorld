package csse2002.block.world;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Handles top-level interaction with performing actions on a WorldMap
 */
public class Main {
    /**
     * The entry point of the application.
     * Takes 3 parameters an input map file (args[0]), actions (args[1]),
     * and an output map file (args[2]).
     * The actions parameter can be either a filename, or the string
     * "System.in".
     * @param args - the input arguments to the program
     */
    public static void main(java.lang.String[] args) {
        if (args.length != 3) {
            System.err.println("Usage: program inputMap actions outputMap");
            System.exit(1);
        }

        WorldMap map = null;
        BufferedReader input = null;

        try {
            map = new WorldMap(args[0]);
        } catch (WorldMapFormatException | WorldMapInconsistentException |
                FileNotFoundException e) {
            System.out.println(args[0]);
            System.err.println(e);
            System.exit(2);
        }
        try {
            if (args[1].equals("System.in")) {
                input = new BufferedReader(new
                        InputStreamReader(System.in));
            } else {
                input = new BufferedReader(new FileReader(args[1]));
            }
        }
        catch (FileNotFoundException e) {
            System.err.println(e);
            System.exit(3);
        }

        try {
            Action.processActions(input, map);
        } catch (ActionFormatException e) {
            System.err.println(e);
            System.exit(4);
        }

        try {
            map.saveMap(args[2]);
        } catch (IOException e) {
            System.err.println(e);
            System.exit(5);
        }
    }


}
