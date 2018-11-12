package csse2002.block.world;
import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ActionTest {
    Action validAction;

    /* I know these are not descriptive names, but since I'm making a lot of
     * tiles, I figured it would be most practical do name them as below.
     */
    private Tile tile1;
    private Tile tile2;
    private Tile tile3;
    private Tile tile4;
    private Tile tile5;
    private Tile tile6;

    private final String newLine =
            System.getProperty("line.separator");


    /*Typical valid usage output*/
    private final String outputString1 = "Moved builder north" + newLine
            + "Top block on current tile removed" + newLine
            + "Moved builder east" + newLine
            + "Top block on current tile removed" + newLine
            + "Dropped a block from inventory" + newLine
            + "Moved builder west" + newLine
            + "Moved builder south" + newLine
            + "Top block on current tile removed" + newLine
            + "Moved builder south" + newLine
            + "Dropped a block from inventory" + newLine;

    /*Typical valid usage input*/
    private final String inputString1 = "MOVE_BUILDER north\n"
            + "DIG\n"
            + "MOVE_BUILDER east\n"
            + "DIG\n"
            + "DROP 0\n"
            + "MOVE_BUILDER west\n"
            + "MOVE_BUILDER south\n"
            + "DIG\n"
            + "MOVE_BUILDER south\n"
            + "DROP 0";

    /*Attempting to move to tile without an exit*/
    private final String inputString2 = "MOVE_BUILDER north\n"
            + "MOVE_BUILDER east\n"
            + "MOVE_BUILDER west\n"
            + "MOVE_BUILDER south\n";

    /*correct output when attempting to move to tile without an exit*/
    private final String outputString2 = "Moved builder north" + newLine
            + "Moved builder east" + newLine
            + "No exit this way" + newLine
            + "No exit this way" + newLine;

    /*Digging some blocks, used to test non-carryable blocks*/
    private final String inputString3 = "MOVE_BUILDER north" + newLine
            + "DIG" + newLine
            + "MOVE_BUILDER east" + newLine
            + "DIG" + newLine;

    /* Used to compare the stdout of my program to the expected stdout*/
    private final ByteArrayOutputStream outContent =
            new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    /**
     * Since I will be testing the stdout of Action, I have to create
     * and restore stdout
     */
    @Before
    public void setUpActionTest() {
        System.setOut(new PrintStream(outContent));
    }

    /**
     * Since I will be testing the stdout of Action, I have to create
     * and restore stdout
     */
    @After
    public void restoreStdOut() {
        System.setOut(originalOut);
    }

    /**
     * Constructor test
     */
    @Test
    public void newValidActionTest() {
        validAction = new Action(Action.MOVE_BUILDER, "TestingString");
        assertEquals(validAction.getPrimaryAction(), 0);
        assertEquals(validAction.getSecondaryAction(), "TestingString");

    }

    /**
     * Tests the loadAction method.
     */
    @Test
    public void loadActionTest() throws ActionFormatException {
        BufferedReader testBufferedReader1 = new BufferedReader(new
                StringReader("MOVE_BUILDER north"));
        BufferedReader testBufferedReader2 = new BufferedReader(new
                StringReader("DROP 1"));
        validAction = Action.loadAction(testBufferedReader1);
        assertEquals(validAction.getPrimaryAction(), 0);
        assertEquals(validAction.getSecondaryAction(), "north");
        validAction = Action.loadAction(testBufferedReader2);
        assertEquals(validAction.getPrimaryAction(), 3);
        assertEquals(validAction.getSecondaryAction(), "1");
    }

    /**
     * Generic setup for some test cases.
     */
    private void firstSetup() throws NoExitException, TooHighException,
            InvalidBlockException {
        tile1 = new Tile();
        tile2 = new Tile();
        tile2 = new Tile();
        tile3 = new Tile();
        tile4 = new Tile();
        tile5 = new Tile();
        tile6 = new Tile();
        tile1.addExit("north", tile2);
        tile1.addExit("east", tile3);
        tile1.addExit("south", tile4);
        tile1.addExit("west", tile5);
        tile2.addExit("east", tile6);
        tile2.addExit("south", tile1);
        tile6.addExit("west", tile2);
        tile2.placeBlock(new WoodBlock());
        tile1.placeBlock(new WoodBlock());
    }

    /**
     * Testing some typical use cases.
     */
    @Test
    public void aFewActionsTest() throws NoExitException,
            WorldMapInconsistentException, TooHighException,
            InvalidBlockException, ActionFormatException {
        firstSetup();
        Builder builderBob = new Builder("Bob", tile1);
        BufferedReader reader = new BufferedReader(new
                StringReader(inputString1));
        WorldMap map = new WorldMap(tile1, new Position(2, 2), builderBob);
        Action.processActions(reader, map);
        assertEquals(outputString1, outContent.toString());

    }

    /**
     * Generic setup for certain test cases.
     */
    private void secondSetup() throws NoExitException {
        tile1 = new Tile();
        tile2 = new Tile();
        tile2 = new Tile();
        tile3 = new Tile();
        tile4 = new Tile();
        tile5 = new Tile();
        tile6 = new Tile();
        tile1.addExit("north", tile2);
        tile2.addExit("east", tile6);
        tile2.addExit("south", tile1);
    }

    /**
     * Tests if a tile without the requested exit is not able to be moved
     * to from the given tile.
     */
    @Test
    public void testingIfNoExitIsDealtWith() throws NoExitException,
            WorldMapInconsistentException, ActionFormatException {
        secondSetup();
        Builder builderJob = new Builder("Job", tile1);
        WorldMap map = new WorldMap(tile1, new Position(0, 0), builderJob);
        BufferedReader input = new BufferedReader(new
                StringReader(inputString2));
        Action.processActions(input, map);
        assertEquals(outputString2, outContent.toString());
        assertEquals(builderJob.getCurrentTile(), tile6);
        assertEquals(builderJob.getCurrentTile(),
        map.getTile(new Position(1, -1)));
    }

    /**
     * Tests if non-carryable blocks are dug properly, but not added to
     * inventory.
     */
    @Test
    public void testingInventoryOnNotCarryable() throws NoExitException,
            WorldMapInconsistentException, TooHighException,
            InvalidBlockException, ActionFormatException {
        firstSetup();
        Builder builderPob = new Builder("Pob", tile1);
        WorldMap map = new WorldMap(tile1, new Position(0, 0), builderPob);
        Action.processActions(new BufferedReader(new
                StringReader(inputString3)), map);
        assertEquals(builderPob.getInventory().size(), 1);
        assertTrue(builderPob.getInventory().get(0) instanceof WoodBlock);
    }

    /**
     * Tests that undiggable blocks display the right text and that
     * these blocks are not added to inventory
     */
    @Test
    public void testingUnDiggableBlocks() throws WorldMapInconsistentException,
            ActionFormatException, NoExitException,
            TooHighException, InvalidBlockException {
        secondSetup();
        tile1.placeBlock(new StoneBlock());
        WorldMap map = new WorldMap(tile1, new Position(0,0),
                new Builder("john", tile1));
        Action.processActions(new
                BufferedReader(new StringReader("DIG")), map);
        assertEquals("Cannot use that block" + newLine, outContent.toString());
        assertEquals(map.getBuilder().getInventory().size(), 0);
    }


    /**
     * Tests if Action deals with digging too low appropriately.
     * Also checks if anything is added to the inventory when nothing
     * is being dug.
     */
    @Test
    public void testingTooLow() throws NoExitException,
            WorldMapInconsistentException, ActionFormatException {
        secondSetup();
        String tooMuchDigging = "DIG" + newLine
                + "DIG" + newLine
                + "DIG" + newLine
                + "DIG" + newLine
                + "DIG" + newLine;
        String dealingWithDigging = "Top block on current tile removed"
                + newLine + "Top block on current tile removed"
                + newLine + "Top block on current tile removed"
                + newLine + "Too low" + newLine
                + "Too low" + newLine;

        Builder builderJob = new Builder("Job", tile1);
        WorldMap map = new WorldMap(tile1, new Position(0, 0), builderJob);
        Action.processActions(new BufferedReader(new
                StringReader(tooMuchDigging)), map);
        assertEquals(dealingWithDigging, outContent.toString());
        System.out.println(builderJob.getInventory().size());
        assertEquals(builderJob.getInventory().size(), 2);
    }

    /**
     * Tests if "Too high" is dealt with appropriately
     * i.e. if Action deals with attempting to place blocks when they shouldn't
     * be placed.
      */
    @Test
    public void tooHighTest() throws NoExitException, TooHighException,
            InvalidBlockException, WorldMapInconsistentException,
            ActionFormatException {
        firstSetup();
        for (int i = 0; i < 3; i++) {
            tile1.placeBlock(new StoneBlock());
            tile2.placeBlock(new StoneBlock());
        }
        tile1.placeBlock(new StoneBlock());
        tile2.placeBlock(new WoodBlock());
        Builder builder = new Builder("hi", tile1);
        WorldMap map = new WorldMap(tile1, new Position(0,0), builder);
        String actions = "MOVE_BUILDER north" + newLine
                + "DIG" + newLine
                + "MOVE_BUILDER south" + newLine
                + "DROP 0" + newLine;
        String expectedOutput = "Moved builder north" + newLine
                + "Top block on current tile removed" + newLine
                + "Moved builder south" + newLine
                + "Too high" + newLine;
        Action.processActions(new BufferedReader(new StringReader(actions)),
                map);
        assertEquals(expectedOutput, outContent.toString());
    }


    /**
     * A few MOVE_BLOCK tests
     * This tests:
     *      if move block works under valid circumstances
     *      if move block prints "Too high" when trying to move to a tile
     * that is too high
     *      if move block prints "Cannot use that block" when trying to move
     *      an unmovable block
     */
    @Test
    public void testingMoveBlock() throws NoExitException,
            InvalidBlockException, TooHighException, ActionFormatException,
            WorldMapInconsistentException {
        firstSetup();
        tile1.placeBlock(new WoodBlock());
        tile1.placeBlock(new WoodBlock());
        tile2.placeBlock(new StoneBlock());
        String actions = "MOVE_BLOCK north" + newLine
                + "MOVE_BUILDER north" + newLine
                + "DIG" + newLine
                + "MOVE_BLOCK south" + newLine
                + "MOVE_BUILDER south" + newLine
                + "DIG" + newLine
                + "MOVE_BUILDER north" + newLine
                + "MOVE_BLOCK south" + newLine;

        String expectedOutput = "Moved block north" + newLine
                + "Moved builder north" + newLine
                + "Top block on current tile removed" + newLine
                + "Too high" + newLine
                + "Moved builder south" + newLine
                + "Top block on current tile removed" + newLine
                + "Moved builder north" + newLine
                + "Cannot use that block" + newLine;

        Builder builder = new Builder("hi", tile1);
        WorldMap map = new WorldMap(tile1, new Position(0,0), builder);
        Action.processActions(new BufferedReader(new StringReader(actions)),
                map);
        assertEquals(expectedOutput, outContent.toString());
    }
}
