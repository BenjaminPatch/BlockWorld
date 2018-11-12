package csse2002.block.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


/**
 * A sparse representation of tiles in an Array.
 * Contains Tiless stored with an associated Position (x, y) in a map.
 */
public class SparseTileArray {
    /*The array of tiles. Breadth-first search order*/
    private List<Tile> tileArray;

    /*A map mapping position to tile*/
    private Map<Position, Tile> positionToTile;

    /*A map mapping tile to position*/
    private Map<Tile, Position> tileToPosition;

    /**
     * Constructor for a SparseTileArray. Initializes an empty array,
     * such that getTile(x, y) returns null for any x and y.
     */
    public SparseTileArray() {
        this.tileArray = new ArrayList<>();
        this.positionToTile = new HashMap<>();
        this.tileToPosition = new HashMap<>();
    }

    /**
     * Get the tile at position at (x, y), given by position.getX()
     * and position.getY(). Return null if there is no tile at (x, y).
     * @param position  - the tile position
     * @return - the tile at (x, y) or null if no such tile exists.
     * @require - position != null
     */
    public Tile getTile(Position position) {
        if (this.tileArray.size() == 0) {
            return null;
        }
        return this.positionToTile.get(position);
    }

    /**
     * Get a set of ordered tiles from SparseTileArray in breadth-first-search
     * order. The startingTile (passed to addLinkTiles) should be the first
     * tile in the list. The following tiles should be the tiles at the "north",
     * "east", "south" and "west" exits from the starting tile, if they exist.
     * Then for each of those tiles, the next tiles will be their "north",
     * "east", "south" and "west" exits, if they exist. The order should
     * continue in the same way through all the tiles that are linked to
     * startingTile. The list returned by getTiles may be immutable, and if not,
     * changing the list (i.e., adding or removing elements) should not change
     * that returned by subsequent calls to getTiles().
     * @return - a list of tiles in breadth-first-search order.
     */
    public java.util.List<Tile> getTiles() {
        List<Tile> unModifiableTiles =
                Collections.unmodifiableList(this.tileArray);
        return unModifiableTiles;
    }

    /**
     * Processes the "north" exit of a tile.
     * @param current - The tile whose north exit will be processed.
     * @param toSearch - The Linked List of all current (and future) tiles
     *                   That need to be processed. The north exit will be
     *                   Added to this.
     * @param currentX - Point on X axis of current tile.
     * @param currentY - Point on Y axis of current tile.
     * @throws WorldMapInconsistentException - If an error is found.
     *                   Errors include: A tile existing in multiple locations,
     *                   One location having multiple tiles.
     */
    private void processNorth(Tile current, LinkedList<Tile> toSearch,
            int currentX, int currentY) throws WorldMapInconsistentException{
        int flag = 0;
        if (current.getExits().get("north") != null) {
            Object tempForChecking =
                    this.positionToTile.put(new Position(currentX,
                                    currentY - 1),
                    current.getExits().get("north"));
            if (tempForChecking != null) {
                flag = 1;
                if (tempForChecking != current.getExits().get("north")) {
                    throw new WorldMapInconsistentException();
                }
            }
            Object otherTempForChecking =
                    this.tileToPosition.put(current.getExits().get("north"),
                    new Position(currentX, currentY - 1));
            if (otherTempForChecking != null) {
                flag = 1;
                if (((Position) otherTempForChecking).compareTo(new
                        Position(currentX,
                        currentY - 1)) != 0) {
                    throw new WorldMapInconsistentException();
                }
            }

        }
        if (flag != 1 && current.getExits().get("north") != null) {
            toSearch.add(current.getExits().get("north"));
        }
    }


    /**
     * Processes the "east" exit of a tile.
     * @param current - The tile whose east exit will be processed.
     * @param toSearch - The Linked List of all current (and future) tiles
     *                   That need to be processed. The east exit will be
     *                   Added to this.
     * @param currentX - Point on X axis of current tile.
     * @param currentY - Point on Y axis of current tile.
     * @throws WorldMapInconsistentException - If an error is found.
     *                   Errors include: A tile existing in multiple locations,
     *                   One location having multiple tiles.
     */
    private void processEast(Tile current, LinkedList<Tile> toSearch,
            int currentX, int currentY) throws WorldMapInconsistentException {
        int flag = 0;
        if (current.getExits().get("east") != null) {
            Object tempForChecking =
                    this.positionToTile.put(new Position(currentX + 1,
                                    currentY),
                            current.getExits().get("east"));
            if (tempForChecking != null) {
                flag = 1;
                if (tempForChecking != current.getExits().get("east")) {
                    throw new WorldMapInconsistentException();
                }
            }
            Object otherTempForChecking =
                    this.tileToPosition.put(current.getExits().get("east"),
                    new Position(currentX + 1, currentY));
            if (otherTempForChecking != null) {
                flag = 1;
                if (((Position) otherTempForChecking).compareTo(new
                        Position(currentX + 1,
                        currentY)) != 0) {
                    throw new WorldMapInconsistentException();
                }
            }

            if (flag != 1 && current.getExits().get("east") != null) {
                toSearch.add(current.getExits().get("east"));
            }
        }
    }

    /**
     * Processes the "south" exit of a tile.
     * @param current - The tile whose south exit will be processed.
     * @param toSearch - The Linked List of all current (and future) tiles
     *                   That need to be processed. The south exit will be
     *                   Added to this.
     * @param currentX - Point on X axis of current tile.
     * @param currentY - Point on Y axis of current tile.
     * @throws WorldMapInconsistentException - If an error is found.
     *                   Errors include: A tile existing in multiple locations,
     *                   One location having multiple tiles.
     */
    private void processSouth(Tile current, LinkedList<Tile> toSearch,
    int currentX, int currentY) throws WorldMapInconsistentException {
        int flag = 0;
        if (current.getExits().get("south") != null) {
            Object tempForChecking =
                    this.positionToTile.put(new Position(currentX,
                                    currentY + 1),
                            current.getExits().get("south"));
            if (tempForChecking != null) {
                flag = 1;
                if (tempForChecking != current.getExits().get("south")) {
                    throw new WorldMapInconsistentException();
                }
            }
            Object otherTempForChecking =
                    this.tileToPosition.put(current.getExits().get("south"),
                    new Position(currentX, currentY + 1));
            if (otherTempForChecking != null) {
                flag = 1;
                if (((Position) otherTempForChecking).compareTo(new
                        Position(currentX,
                        currentY + 1)) != 0) {
                    throw new WorldMapInconsistentException();
                }
            }

            if (flag != 1 && current.getExits().get("south") != null) {
                toSearch.add(current.getExits().get("south"));
            }
        }
    }

    /**
     * Processes the "west" exit of a tile.
     * @param current - The tile whose west exit will be processed.
     * @param toSearch - The Linked List of all current (and future) tiles
     *                   That need to be processed. The north exit will be
     *                   Added to this.
     * @param currentX - Point on X axis of current tile.
     * @param currentY - Point on Y axis of current tile.
     * @throws WorldMapInconsistentException - If an error is found.
     *                   Errors include: A tile existing in multiple locations,
     *                   One location having multiple tiles.
     */
    private void processWest(Tile current, LinkedList<Tile> toSearch,
            int currentX, int currentY) throws WorldMapInconsistentException {
        int flag = 0;
        if (current.getExits().get("west") != null) {
            Object tempForChecking =
                    this.positionToTile.put(new Position(currentX - 1,
                                    currentY),
                            current.getExits().get("west"));
            if (tempForChecking != null) {
                flag = 1;
                if (tempForChecking != current.getExits().get("west")) {
                    throw new WorldMapInconsistentException();
                }
            }

            Object otherTempForChecking
                    = this.tileToPosition.put(current.getExits().get("west"),
                    new Position(currentX - 1, currentY));
            if (otherTempForChecking != null) {
                flag = 1;
                if (((Position) otherTempForChecking).compareTo(new
                        Position(currentX - 1, currentY)) != 0) {
                    throw new WorldMapInconsistentException();
                }
            }

        }
        if (flag != 1 && current.getExits().get("west") != null) {
            toSearch.add(current.getExits().get("west"));
        }

    }

    /**
     * Add a set of tiles to the sparse tilemap.
     * This function does the following:
     *
     *     Remove any tiles that are already existing in the sparse map.
     *     Add startingTile at position (startingX, startingY), such that
     *     getTile(new Position(startingX, startingY)) == startingTile.
     *     For each pair of linked tiles (tile1 at (x1, y1) and tile2 at
     *     (x2, y2) that are accessible from startingTile (i.e. there is a
     *     path through a series of exits
     *     startingTile.getExits().get("north").getExits().get("east") ...
     *     between the two tiles), tile2 will get a new position based on
     *     tile1's position, and tile1's exit name.
     *     If there are tiles that are not geometrically consistent,
     *     throw a WorldMapInconsistentException. getTiles() should return a
     *     list of each accessible tile in a breadth-first search order
     *     (see getTiles()) If an exception is thrown, reset the state of the
     *     SparseTileArray such that getTile(new Position(x, y)) returns null
     *     for any x and y.
     * @param startingTile  - the starting point in adding the linked tiles.
     *                        All added tiles must have a path (via multiple
     *                        exits) to this tile.
     * @param startingX - the x coordinate of startingTile in the array
     * @param startingY - the y coordinate of startingTile in the array
     * @throws WorldMapInconsistentException - if the tiles in the set are not
     *                                         Geometrically consistent
     * @require - startingTile != null
     * @ensure - tiles accessed through getTile() are geometrically consistent
     */
    public void addLinkedTiles(csse2002.block.world.Tile startingTile,
            int startingX, int startingY) throws WorldMapInconsistentException {
        //this.tileArray.clear();
        this.positionToTile.clear();
        this.tileToPosition.clear();
        this.tileArray.clear();
        Position startingPosition = new Position(startingX, startingY);
        this.positionToTile.put(startingPosition, startingTile);
        this.tileToPosition.put(startingTile, startingPosition);
        LinkedList<Tile> toSearch = new LinkedList<>();
        toSearch.add(startingTile);
        int currentX, currentY;
        while(toSearch.peekFirst() != null) {
            Tile current = toSearch.getFirst();
            currentX = this.tileToPosition.get(current).getX();
            currentY = this.tileToPosition.get(current).getY();

            processNorth(current, toSearch, currentX, currentY);
            processEast(current, toSearch, currentX, currentY);
            processSouth(current, toSearch, currentX, currentY);
            processWest(current, toSearch, currentX, currentY);
            int flag = 0;
            for (int i = 0; i < this.tileArray.size(); i++) {
                if (this.tileArray.get(i) == current) {
                    flag = 1;
                }
            }
            if (flag == 0 ) {
                this.tileArray.add(toSearch.remove());
            } else {
                Tile tempTile = toSearch.remove();
            }

        }
    }

}
