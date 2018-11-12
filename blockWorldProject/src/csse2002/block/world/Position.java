package csse2002.block.world;

public class Position implements java.lang.Comparable<Position> {
    /*The x coordinate for a position*/
    int x;

    /*The y coordinate for a position*/
    int y;

    /**
     * Construct a position for (x, y)
     * @param x - the x coordinate
     * @param y - the y coordinate
     */
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Get the x coordinate
     * @return - the x coordinate
     */
    public int getX() {
        return this.x;
    }

    /**
     * Get the y coordinate
     * @return - the y coordinate
     */
    public int getY() {
        return this.y;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * (see https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html)
     * Two Positions are equal if getX() == other.getX()
     * && getY() == other.getY()
     * @param obj  the object to compare to
     * @Overrides - equals in class java.lang.Object
     * @return - true if obj is an instance of
     * Position and if obj.x == x and obj.y == y.
     */
    @Override
    public boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Position)) {
            return false;
        }
        return ((Position) obj).getX() == this.getX() &&
                ((Position) obj).getY() == this.getY();
    }

    /**
     * Compute a hashCode that meets the contract of Object.hashCode
     * (see https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html)
     * @Override - hashCode in class java.lang.Object
     * @return - a suitable hashcode for the Position
     */
    @Override
    public int hashCode() {
        int result = 1; /*Made into hash code*/
        final int prime = 29; /*Prime chosen to be used in calculations*/
        result = prime * result + this.getX();
        result = prime * result + this.getY();
        return result;
    }

    /**
     * Compare this position to another position.
     * return
     *
     *     -1 if getX() < other.getX()
     *     -1 if getX() == other.getX() and getY() < other.getY()
     *     0 if getX() == other.getX() and getY() == other.getY()
     *     1 if getX() > other.getX()
     *     1 if getX() == other.getX() and getY() > other.getY()
     * @param other - the other Position to compare to
     * @return - -1, 0, or 1 depending on conditions above
     */
    public int compareTo(Position other) {
        if (this.getX() < other.getX()) {
            return -1;
        }
        if (this.getX() == other.getX() && this.getY() < other.getY()) {
            return -1;
        }
        if (this.getX() > other.getX()) {
            return 1;
        }
        if (this.getX() == other.getX() && this.getY() > other.getY()) {
            return 1;
        }
        return 0;
    }

    /**
     * Convert this position to a string.
     * String should be "(<x>, <y>)" where <x> is the value
     * returned by getX() and <y> is the value returned by getY().
     * Note the space following the comma.
     * @Override -  toString in class java.lang.Object
     * @return - a string representation of the position "(<x>, <y>)"
     */
    @Override
    public java.lang.String toString() {
        return String.format("(%d, %d)", this.getX(), this.getY());
    }
}
