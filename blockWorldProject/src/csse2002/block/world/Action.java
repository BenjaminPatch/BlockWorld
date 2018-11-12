package csse2002.block.world;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;

/**
 * Represents an Action which can be performed
 * on the block world (also called world map).
 * An action is something that a builder can do
 * on a tile in the block world. The actions include,
 * moving the builder in a direction, moving a block in a direction,
 * digging on the current tile the builder is standing on and
 * dropping an item from a builder's inventory.
 */
public class Action {

    /*MOVE_BUILDER action which is represented by integer 0*/
    public static final int MOVE_BUILDER = 0;

    /*MOVE_BLOCK action which is represented by integer 1*/
    public static final int MOVE_BLOCK = 1;

    /*DIG action which is represented by integer 2*/
    public static final int DIG = 2;

    /*DROP action which is represented by integer 3*/
    public static final int DROP = 3;

    /*The primary action for an instance*/
    private int primaryAction;

    /*The secondary action for an instance*/
    private java.lang.String secondaryAction;

    /**
     * Create an Action that represents a manipulation of the blockworld.
     * @require secondaryAction != null
     * @require if primaryAction == MOVE_BUILDER or
     * MOVE_BLOCK, secondaryAction must be either
     * "north", "east", "west" or "south".
     * @param primaryAction - the action to be created
     * @param secondaryAction - the supplementary
     * information associated with the primary action
     */
    public Action(int primaryAction, java.lang.String secondaryAction) {
        this.primaryAction = primaryAction;
        this.secondaryAction = secondaryAction;
    }

    /**
     * Gets the supplementary information associated with the Action
     * @return the primary action
     */
    public int getPrimaryAction() {
        return this.primaryAction;
    }

    /**
     * Get the integer representing the Action (e.g.,
     * return 0 if Action is MOVE_BUILDER)
     * @return the secondary action, or "" (empty string)
     * if no secondary action exists
     */
    public String getSecondaryAction() {
        return this.secondaryAction;
    }

    private static int mapPrimaryAction(String action) {
        switch(action) {
            case "MOVE_BUILDER":
                return MOVE_BUILDER;
            case "MOVE_BLOCK":
                return MOVE_BLOCK;
            case "DIG":
                return DIG;
            case "DROP":
                return DROP;
            default:
                return -1;
        }
    }

    /**
     * Create a single Action if possible from the given reader.
     * Read a line from the given reader and load the Action on that line.
     * Only load one Action and return the created action.
     * Each line consists of a primary action,
     * and optionally a secondary action.
     * @param reader - the reader to read the action contents form
     * @return - the created action, or null if the reader is
     *           at the end of the file.
     * @throws ActionFormatException - if the line has invalid contents
     *                                  and the action cannot be created
     */
    public static Action loadAction(java.io.BufferedReader reader)
            throws ActionFormatException {
        int primaryAction;
        String secondaryAction;
        String actionLine;
        String[] lineContents;
        try {
            actionLine = reader.readLine();
            if (actionLine == null) {
                return null;
            }

            lineContents = actionLine.split(" ");
            if (lineContents.length != 1 && lineContents.length != 2) {
                throw new ActionFormatException();
            }
            if ((primaryAction = mapPrimaryAction(lineContents[0])) == -1) {
                throw new ActionFormatException();
            }
            if (lineContents.length == 1) {
                if (!lineContents[0].equals("DIG")) {
                    throw new ActionFormatException();
                }
                primaryAction = DIG;
                secondaryAction = "";
            } else {
                secondaryAction = lineContents[1];
            }
        } catch (IOException e) {
            throw new ActionFormatException();
        }
        switch (lineContents[0]) {
            case "MOVE_BUILDER":
                primaryAction = MOVE_BUILDER;
                break;
            case "MOVE_BLOCK":
                primaryAction = MOVE_BLOCK;
                break;
            case "DROP":
                primaryAction = DROP;
                break;
        }
        return new Action(primaryAction, secondaryAction);
    }

    /**
     * ead all the actions from the given reader and perform them on the
     * given block world.
     * All actions that can be performed should print an appropriate message
     * (as outlined in processAction()), any invalid actions that cannot be
     * created or performed on the world map, should also print an error message
     * (also described in processAction()).
     * Each message should be printed on a new line (Use System.out.println()).
     * Each action is listed on a single line, and one file can contain multiple
     * actions. Each action must be processed after it is read (i.e. do not read
     * the whole file first, read and process each action one at a time).
     * @param reader - the reader to read actions from
     * @param startingMap - the starting map that actions will be applied to
     * @throws ActionFormatException - if loadAction throws an
     * ActionFormatException
     * @require - reader != null, startingMap != null
     */
    public static void processActions(java.io.BufferedReader reader,
            WorldMap startingMap) throws ActionFormatException {
        while (true) {
            try {
                reader.mark(1);
                if (reader.read() == -1) {
                    break;
                }
                reader.reset();
            } catch (IOException e) {
                throw new ActionFormatException();
            }
            Action action = loadAction(reader);
            processAction(action, startingMap);
        }
    }

    /**
     * Perform the given action on a WorldMap, and print output to System.out.
     * After this method finishes, map should be updated. (e.g., If the action
     * is DIG, the Tile on which the builder is currently on should be updated
     * to contain 1 less block (Builder.digOnCurrentTile()). The builder to use
     * for actions is that given by map.getBuilder().
     * Do the following for these actions:
     *
     *     For DIG action: call Builder.digOnCurrentTile(), then print
     *     to console "Top block on current tile removed".
     *     For DROP action: call Builder.dropFromInventory(), then print
     *     to console "Dropped a block from inventory". The dropped item
     *     is given by action.getSecondaryAction(), that is first converted
     *     to an int. If the action.getSecondaryAction() cannot be converted
     *     to an int, print "Error: Invalid action" to the console. Valid
     *     integers (including negative integers and large positive integers)
     *     should be passed to Builder.dropFromInventory().
     *     For the MOVE_BLOCK action: call Tile.moveBlock() on the builder's
     *     current tile (Builder.getCurrentTile()), then print to console
     *     "Moved block {direction}". The direction is given by
     *     action.getSecondaryAction()
     *     For MOVE_BUILDER action: call Builder.moveTo(), then print to
     *     console "Moved builder {direction}". The direction is given by
     *     action.getSecondaryAction()
     *     If action.getPrimaryAction() < 0 or action.getPrimaryAction() > 3,
     *     or action.getSecondary() is not a direction
     *     (for MOVE_BLOCK or MOVE_BUILDER), or a valid integer (for DROP)
     *     then print to console "Error: Invalid action"
     *
     * "{direction}" is one of "north", "east", "south" or "west".
     * For handling exceptions do the following:
     *
     *     If a NoExitException is thrown, print to the console
     *     "No exit this way"
     *     If a TooHighException is thrown, print to the console "Too high"
     *     If a TooLowException is thrown, print to the console "Too low"
     *     If an InvalidBlockException is thrown, print to the console
     *     "Cannot use that block"
     * @param action - the action to be done on the map
     * @param map - the map to perform the action on
     * @require - action != null, map != null
     */
    public static void processAction(Action action, WorldMap map) {
        Builder actionBuilder = map.getBuilder();
        Tile builderTile = actionBuilder.getCurrentTile();
        if (action.getPrimaryAction() < 0 ||  action.getPrimaryAction() > 3) {
            System.out.println("Error: Invalid action");
        }
        if (action.getPrimaryAction() == MOVE_BUILDER ||
                action.getPrimaryAction() == MOVE_BLOCK) {
            Set<String> directions = new HashSet<String>(
                    Arrays.asList("north", "east", "west", "south"));
            if (!directions.contains(action.getSecondaryAction())) {
                System.out.println("Error: Invalid action");
            }
        }

        switch (action.getPrimaryAction()) {
            case MOVE_BUILDER:
                try {
                    String wahu = action.getSecondaryAction();
                    actionBuilder.moveTo(builderTile.getExits().get(wahu));
                    System.out.println("Moved builder " +
                            action.getSecondaryAction());
                } catch (NoExitException e) {
                    System.out.println("No exit this way");
                }
                break;
            case MOVE_BLOCK:
                try {
                    builderTile.moveBlock(action.getSecondaryAction());
                    System.out.println("Moved block " +
                            action.getSecondaryAction());
                } catch (TooHighException e) {
                    System.out.println("Too high");
                } catch (NoExitException e) {
                    System.out.println("No exit this way");
                } catch (InvalidBlockException e) {
                    System.out.println("Cannot use that block");
                }
                break;
            case DIG:
                try {
                    actionBuilder.digOnCurrentTile();
                    System.out.println("Top block on current tile removed");
                } catch (InvalidBlockException e) {
                    System.out.println("Cannot use that block");
                } catch (TooLowException e) {
                    System.out.println("Too low");
                }
                break;
            case DROP:
                Integer secondary = 0;
                try {
                    secondary = Integer.valueOf(action.getSecondaryAction());
                } catch (NumberFormatException e) {
                    System.out.println("Error: Invalid action");
                }
                try {
                    actionBuilder.dropFromInventory(secondary);
                    System.out.println("Dropped a block from inventory");
                } catch (InvalidBlockException e) {
                    System.out.println("Cannot use that block");
                } catch (TooHighException e) {
                    System.out.println("Too high");
                }
                break;
        }
    }
}
