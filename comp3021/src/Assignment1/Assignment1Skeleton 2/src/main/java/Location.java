public class Location {
    public int xloc;
    public int yloc;

    public Location(int p_xloc, int p_yloc) {
        this.xloc = p_xloc;
        this.yloc = p_yloc;
    }

    /*
    * The metric of the distance.
    * DO NOT change the functionality of the method.
    * Otherwise, you may generate wrong results in Task 1
    */
    int getDisSquare(Location l) {
        int disSquare = 0;
        disSquare += (this.xloc - l.xloc)  * (this.xloc - l.xloc);
        disSquare += (this.yloc - l.yloc) * (this.yloc - l.yloc);
        return disSquare;
    }
}
