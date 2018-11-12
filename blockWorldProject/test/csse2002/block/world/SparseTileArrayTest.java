package csse2002.block.world;

import static org.junit.Assert.*;

import java.util.ArrayList;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import java.util.List;

public class SparseTileArrayTest {

    /* I know it was advised to not use un-descriptive variable names like
     * the ones below, but, since I am using a fair number of different tiles,
     * I figured it would be impractical to find a unique and descriptive name
     * for all of them.
     */
    private Tile startingTile;
    private Tile tile1;
    private Tile tile2;
    private Tile tile3;
    private Tile tile4;
    private Tile tile5;
    private SparseTileArray testTileArray;

    @Rule
    public final ExpectedException inconsistentException =
            ExpectedException.none();


    @Before
    public void setUpTest() throws TooHighException, InvalidBlockException,
            NoExitException {
        setup();
        tile1.addExit("north", tile5);
        this.testTileArray = new SparseTileArray();
    }

    /**
     * Sets up a scenario to test
     */
    private void setup() throws TooHighException, InvalidBlockException,
            NoExitException {
        startingTile = new Tile();
        startingTile.placeBlock(new StoneBlock());
        tile1 = new Tile();
        startingTile.addExit("north", tile1);
        tile2 = new Tile();
        startingTile.addExit("east", tile2);
        tile3 = new Tile();
        startingTile.addExit("south", tile3);
        tile4 = new Tile();
        startingTile.addExit("west", tile4);
        tile5 = new Tile();
    }

    /**
     * Probably trivial
     */
    @Test
    public void testGetTileNull() {
            assertNull(this.testTileArray.getTile(new Position(3, 2)));
        }

    /**
     * Tests a valid scenario
     * This test also tests the getTile() method, but more importantly
     * it checks that tiles are in their right positions.
     */
    @Test
    public void testAddLinkedTiles() throws WorldMapInconsistentException,
            NoExitException {
        tile1.addExit("north", tile5);
        this.testTileArray.addLinkedTiles(this.startingTile, 2, 2);
        assertEquals(testTileArray.getTile(new Position(2, 2)), startingTile);
        assertEquals(testTileArray.getTile(new Position(3, 2)), tile2);
        assertEquals(testTileArray.getTile(new Position(1, 2)), tile4);
        System.out.println(testTileArray.getTile(new Position(2, 0)));
        assertEquals(testTileArray.getTile(new Position(2, 0)), tile5);
        assertEquals(testTileArray.getTile(new Position(2, 1)), tile1);
        assertNull(this.testTileArray.getTile(new Position(33, 32)));
    }


    /*Puts tile2 at two different locations*/
    @Test
    public void testWorldMapInconsistentException() throws NoExitException,
            WorldMapInconsistentException{
        startingTile.removeExit("north");
        startingTile.addExit("north", tile2);
        this.testTileArray = new SparseTileArray();

        inconsistentException.expect(WorldMapInconsistentException.class);
        this.testTileArray.addLinkedTiles(this.startingTile, 2, 2);

    }

    /* Goes north, then south and finds a different tile there*/
    @Test
    public void anotherWorldMapInconsistentExceptionTest() throws
            TooHighException, InvalidBlockException, NoExitException,
            WorldMapInconsistentException{
        setup();
        tile1.addExit("south", tile2);
        this.testTileArray = new SparseTileArray();


        inconsistentException.expect(WorldMapInconsistentException.class);
        this.testTileArray.addLinkedTiles(this.startingTile, 2, 2);

    }


        /**
         * When two tiles happen to be on the same place
         */
    @Test
    public void wrongTileCoordinatesTest() throws NoExitException,
            WorldMapInconsistentException {
        Tile testTile = new Tile();
        tile1 = new Tile();
        tile2 = new Tile();
        tile3 = new Tile();
        tile4 = new Tile();

        testTile.addExit("north", tile1);
        tile1.addExit("east", tile4);
        testTile.addExit("east", tile2);
        tile2.addExit("north", tile3);
        this.testTileArray = new SparseTileArray();
        inconsistentException.expect(WorldMapInconsistentException.class);
        this.testTileArray.addLinkedTiles(testTile, 2, 2);
    }
    /**
     * Another test to see if invalid placement is caught.
     */
        @Test
        public void straightLineGoesBackToSameTile() throws TooHighException,
                InvalidBlockException, NoExitException,
                WorldMapInconsistentException {
            setup();
            tile2.addExit("east", startingTile);

            this.testTileArray = new SparseTileArray();
            inconsistentException.expect(WorldMapInconsistentException.class);
            this.testTileArray.addLinkedTiles(startingTile, 2, 2);
        }

     /**
      * tests the getTiles() method
      * This test tests the order of the list returned by getTiles()
      * This test confirms that the list returned is indeed one of
      * breadth-first search.
     */
    @Test
    public void getTilesTest() throws WorldMapInconsistentException,
            TooHighException, NoExitException,
            InvalidBlockException {
        setup();
        tile1.addExit("north", tile5);
        tile1.addExit("south", startingTile);
        this.testTileArray = new SparseTileArray();
        this.testTileArray.addLinkedTiles(startingTile, 2, 2);
        List<Tile> testArrayList = new ArrayList<>();
        setUpList(testArrayList);
        List<Tile> resultList = this.testTileArray.getTiles();
        assertEquals(testArrayList.size(), resultList.size());
        for (int i = 0; i < 6; i++) {
            assertEquals(testArrayList.get(i), resultList.get(i));
        }
    }

    /**
     * Used to set up an arrayList for testing
     */
    private void setUpList(List<Tile> testArrayList) {
        testArrayList.add(startingTile);
        testArrayList.add(tile1);
        testArrayList.add(tile2);
        testArrayList.add(tile3);
        testArrayList.add(tile4);
        testArrayList.add(tile5);
    }

}
