package csse2002.block.world;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class WorldMap {

    Builder builder;
    Position startPosition;
    SparseTileArray sparseTile;
    Tile startingTile;
    String builderName;

    /**
     * Constructs a new block world map from a startingTile,
     * position and builder, such that getBuilder() == builder,
     * getStartPosition() == startPosition, and getTiles()
     * returns a list of tiles that are linked to startingTile.
     * @param startingTile - the tile which the builder starts on
     * @param startingPosition - the position of the starting tile
     * @param builder - the builder who will traverse the block world
     * @throws WorldMapInconsistentException - if there are inconsistencies in
     * the positions of tiles (such as two tiles at a single position)
     */
    public WorldMap(Tile startingTile, Position startingPosition,
            Builder builder) throws WorldMapInconsistentException {
        this.builder = builder;
        this.startPosition = startingPosition;
        this.startingTile = startingTile;
        this.sparseTile = new SparseTileArray();
        this.sparseTile.addLinkedTiles(startingTile,
                startingPosition.getX(), startingPosition.getY());

    }

    /**
     * Construct a block world map from the given filename. The block world
     * map format is as follows:
     *
     *  <startingX>
     *  <startingY>
     *  <builder's name>
     *  <inventory1>,<inventory2>, ... ,<inventoryN>
     *
     *  total:<number of tiles>
     *  <tile0 id> <block1>,<block2>, ... ,<blockN>
     *  <tile1 id> <block1>,<block2>, ... ,<blockN>
     *     ...
     *  <tileN-1 id> <block1>,<block2>, ... ,<blockN>
     *
     *  exits
     *  <tile0 id> <name1>:<id1>,<name2>:<id2>, ... ,<nameN>:<idN>
     *  <tile1 id> <name1>:<id1>,<name2>:<id2>, ... ,<nameN>:<idN>
     *     ...
     *  <tileN-1 id> <name1>:<id1>,<name2>:<id2>, ... ,<nameN>:<idN>
     *  Tile IDs are the ordering of tiles returned by getTiles()
     *  i.e. tile 0 is getTiles().get(0).
     *  The ordering does not need to be checked when loading a map
     *  (but the saveMap function below does when saving).
     *  Note: A blank line is required for an empty inventory,
     *  and lines with just an ID followed by a space are required for:
     *
     *     A tile entry below "total:N", if the tile has no blocks
     *     A tile entry below "exits", if the tile has no exits
     *
     * @param filename - the name to load the file from
     * @throws WorldMapFormatException - if the file is incorrectly formatted
     * @throws WorldMapInconsistentException - if the file is correctly
     * formatted, but has inconsistencies (such as overlapping tiles)
     * @throws java.io.FileNotFoundException - if the file does not exist
     * @require filename != null
     * @Ensure the loaded map is geometrically consistent
     */
    public WorldMap(String filename)
            throws WorldMapFormatException,
            WorldMapInconsistentException,
            java.io.FileNotFoundException{
        this.sparseTile = new SparseTileArray();
        File file;

        try {
            file = new File(filename);
            if (!file.isFile()) {
                throw new FileNotFoundException();
            }
        } catch (IOException e) {
            throw new FileNotFoundException();
        }

        try {
            BufferedReader input = new BufferedReader(new
                    FileReader(file));
            List<Block> builderInvent = readFirstSection(input);
            List<Block> blocksOnTile = new ArrayList<>();
            Map<Integer, Tile> tiles = new HashMap<>();
            Integer numOfTiles = readTileSection(input, blocksOnTile, tiles);
            this.builder = new Builder(builderName, tiles.get(0),
                    builderInvent);
            String line = input.readLine();
            if (!line.equals("exits")) {
                throw new WorldMapFormatException();
            }
            readExits(input, tiles, numOfTiles);
            this.sparseTile.addLinkedTiles(tiles.get(0),
                    this.startPosition.getX(), this.startPosition.getY());
        } catch (IOException | InvalidBlockException e) {
            throw new WorldMapFormatException();
        }

    }

    /**
     * Reads the exits from a given file
     * @param input - the BufferedReader containing information about world.
     * @param tiles - A list of tiles, whose exits are to be populated.
     * @param tileCount - The amount of tiles (used for error checking)
     */
    private void readExits(BufferedReader input, Map<Integer, Tile> tiles,
            Integer tileCount)
            throws WorldMapFormatException {
        String line;
        Integer exitCount = 0;
        List<Integer> tilesPrevious = new ArrayList<>();
        try {
            line = input.readLine();
            while (line != null) {
                if (line.isEmpty()) {
                    throw new WorldMapFormatException();
                }
                Integer tileID;
                String[] IDAndExits = line.split(" ");
                if (IDAndExits.length == 1) {
                    exitCount++;
                    line = input.readLine();
                    continue;
                }
                String[] exitsAndDests = IDAndExits[1].split(","); //Exits and
                                                                // Destinations
                tileID = Integer.parseInt(IDAndExits[0]);
                if (tilesPrevious.contains(tileID)) {
                    throw new WorldMapFormatException();
                }
                if (tileID < 0 || tileID >= tileCount) {
                    throw new WorldMapFormatException();
                }

                List<Integer> exitsPrevious = new ArrayList<>();
                for (String exit: exitsAndDests) {
                    String[] singleExit = exit.split(":");
                    if (singleExit.length != 2) {
                        throw new WorldMapFormatException();
                    }
                    Integer destinationTile = Integer.parseInt(singleExit[1]);
                    if (exitsPrevious.contains(destinationTile)) {
                        throw new WorldMapFormatException();
                    }
                    if (destinationTile < 0 || destinationTile.equals(tileID) ||
                            destinationTile >= tileCount) {
                        throw new WorldMapFormatException();
                    }
                    if (!(singleExit[0].equals("north") ||
                            singleExit[0].equals("east") ||
                            singleExit[0].equals("south") ||
                            singleExit[0].equals("west"))){
                        throw new WorldMapFormatException();
                    }
                    exitsPrevious.add(destinationTile);
                    tiles.get(tileID).addExit(singleExit[0],
                            tiles.get(destinationTile));
                }
                tilesPrevious.add(tileID);
                exitCount++;
                line = input.readLine();
            }

        } catch (IOException | NumberFormatException | NoExitException e) {
            throw new WorldMapFormatException();
        }
        if (!exitCount.equals(tileCount)) {
            throw new WorldMapFormatException();
        }
    }

    private Integer readTileSection(BufferedReader input,
            List<Block> blocksOnTile, Map<Integer, Tile> tiles)
            throws WorldMapFormatException {
        List<Integer> tilesPrevious = new ArrayList<>();
        Integer numOfTiles;
        try {
            String line  = input.readLine();
            String[] lineSections = line.split(":");
            if (lineSections.length != 2 || !lineSections[0].equals("total")) {
                throw new WorldMapFormatException();
            }
            numOfTiles = Integer.parseInt(lineSections[1]);
            line = input.readLine();
            while (!line.isEmpty()) {
                blocksOnTile = new ArrayList<>();
                Integer tileID;
                lineSections = line.split(" ");
                tileID = Integer.parseInt(lineSections[0]);
                if (tilesPrevious.contains(tileID)) {
                    throw new WorldMapFormatException();
                }
                if (tileID < 0 || tileID >= numOfTiles) {
                    throw new WorldMapFormatException();
                }
                if (lineSections.length == 1) {
                    tiles.put(tileID, new Tile(blocksOnTile));
                    line = input.readLine();
                    continue;
                }
                String[] blocks = lineSections[1].split(",");
                populateBlockList(blocksOnTile, blocks);

                tiles.put(tileID, new Tile(blocksOnTile));
                line = input.readLine();
                tilesPrevious.add(tileID);
            }
        } catch (IOException | NumberFormatException | TooHighException e) {
            throw new WorldMapFormatException();
        }
        return numOfTiles;
    }

    private List<Block> readFirstSection(BufferedReader input)
            throws WorldMapFormatException {
        int startX;
        int startY;
        List<Block> builderInvent;
        try {
            String line = input.readLine();
            startX = Integer.parseInt(line);
            line = input.readLine();
            startY = Integer.parseInt(line);
            this.startPosition = new Position(startX, startY);
            line = input.readLine();
            builderName = line;
            line = input.readLine();
            builderInvent = processBuilderInventory(line);
            line = input.readLine();
            if (!line.isEmpty()) {
                throw new WorldMapFormatException(); /*This is after*/
            }                                         /*the invent line*/
        } catch (IOException | NumberFormatException e) {
            throw new WorldMapFormatException();
        }
        return builderInvent;
    }

    private void populateBlockList (List<Block> toFill, String[] blocks)
            throws WorldMapFormatException{
        for (String item: blocks) {
            if (item.equals("soil")) {
                toFill.add(new SoilBlock());
            } else if (item.equals("grass")) {
                toFill.add(new GrassBlock());
            } else if (item.equals("wood")) {
                toFill.add(new WoodBlock());
            } else if (item.equals("stone")) {
                toFill.add(new StoneBlock());
            } else if (item.equals("")) {
                break;
            } else {
                throw new WorldMapFormatException();
            }
        }
    }

    private List<Block> processBuilderInventory(String line) throws
            WorldMapFormatException {
        String[] inventContents = line.split(",");
        List<Block> inventory = new ArrayList<>();
        populateBlockList(inventory, inventContents);
        return inventory;
    }

    /**
     * Gets the builder associated with this block world.
     * @return the builder object
     */
    public Builder getBuilder() {
        return this.builder;
    }

    /**
     * Gets the starting position.
     * @return the starting position.
     */
    public Position getStartPosition() {
        return this.startPosition;
    }

    /**
     * Get a tile by position.
     * @param position - get the Tile at this position
     * @return -  the tile at that position
     * @require - position != null
     */
    public Tile getTile(Position position) {
        return this.sparseTile.getTile(position);
    }


    /**
     * Get a list of tiles in a breadth-first-search order (see
     * SparseTileArray.getTiles() for details).
     * @return a list of ordered tiles
     */
    public List<Tile> getTiles() {
        return this.sparseTile.getTiles();
    }



    /**
     * Saves the given csse2002.block.world.WorldMap to a file specified
     * by the filename.
     * See the csse2002.block.world.WorldMap(filename) constructor for
     * the format of the map.
     * The Tile IDs need to relate to the ordering of tiles returned by
     * getTiles() i.e. tile 0 is getTiles().get(0)
     * The function should do the following:
     *
     *     1 Open the filename and write a map in the format given
     *     in the csse2002.block.world.WorldMap constructor.
     *     2 Write the current builder's (given by getBuilder())
     *     name and inventory.
     *     3 Write the starting position (given by getStartPosition())
     *     4 Write the number of tiles
     *     5 Write the index, and then each tile as given by
     *     getTiles() (in the same order).
     *     6 Write each tiles exits, as given by getTiles().get(id).getExits()
     *     7 Throw an IOException if the file cannot be opened for writing,
     *     or if writing fails.
     * @param filename -  the filename to be written to
     * @throws IOException - if the file cannot be opened or written to.
     */

    public void saveMap(String filename) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        writer.write(Integer.toString(this.startPosition.getX()));
        writer.newLine();
        writer.write(Integer.toString(this.startPosition.getY()));
        writer.newLine();
        writer.write(this.getBuilder().getName());
        writer.newLine();

        int inventorySize = this.getBuilder().getInventory().size();
        int count = 0;

        for (Block block: this.getBuilder().getInventory()) {
            writer.append(block.getBlockType());
            if (count < inventorySize - 1) {
                writer.append(",");
            }
            count++;
        }
        writer.newLine();
        writer.newLine();
        writer.write("total:");
        writer.write(Integer.toString(this.getTiles().size()));
        count = 0;
        for (Tile tile: this.getTiles()) {
            writer.newLine();
            writer.write(Integer.toString(count));
            writer.append(" ");
            int blockCount = 0;
            for (Block block: tile.getBlocks()) {
                writer.write(block.getBlockType());
                if (blockCount <
                        this.getTiles().get(count).getBlocks().size() - 1) {
                    writer.append(",");
                }
                blockCount++;
            }
            count++;
        }
        writer.newLine();
        writer.newLine();
        writer.write("exits");
        count = 0;
        for (Tile tile: this.getTiles()) {
            writer.newLine();
            writer.write(Integer.toString(this.getTiles().indexOf(tile)));
            if (tile.getExits().containsKey("north")) {

                writer.append(" ");
                writer.write("north:");
                Tile exit = tile.getExits().get("north");
                int index = this.getTiles().indexOf(exit);
                writer.write(Integer.toString(index));
            }
            if (tile.getExits().containsKey("east")) {
                writer.append(" ");
                writer.write("east:");
                Tile exit = tile.getExits().get("east");
                int index = this.getTiles().indexOf(exit);
                writer.write(Integer.toString(index));
            }
            if (tile.getExits().containsKey("south")) {
                writer.append(" ");
                writer.write("south:");
                Tile exit = tile.getExits().get("south");
                int index = this.getTiles().indexOf(exit);
                writer.write(Integer.toString(index));
            }
            if (tile.getExits().containsKey("west")) {
                writer.append(" ");
                writer.write("west:");
                Tile exit = tile.getExits().get("west");
                int index = this.getTiles().indexOf(exit);
                writer.write(Integer.toString(index));
            }
            count++;
        }
        writer.flush();
    }


}
