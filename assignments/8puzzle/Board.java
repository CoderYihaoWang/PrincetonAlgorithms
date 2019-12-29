import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.LinkedList;

public class Board {
    private final char[][] board;
    private final int dimension;
    private final int hamming;
    private final int manhattan;
    private final boolean isGoal;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        dimension = tiles.length;
        board = new char[dimension][dimension];
        int h = dimension * dimension - 1;
        int m = 0;
        for (int i = 0; i < dimension; ++i) {
            for (int j = 0; j < dimension; ++j) {
                int value = tiles[i][j];
                board[i][j] = (char) value;
                if (value != 0) {
                    if (value == i * dimension + j + 1)
                        --h;
                    int iOriginal = (value - 1) / dimension;
                    int jOriginal = (value - 1) % dimension;
                    m += Math.abs(i - iOriginal);
                    m += Math.abs(j - jOriginal);
                }
            }
        }
        hamming = h;
        manhattan = m;
        isGoal = h == 0;
    }

    private void swapTiles(int[][] tiles, int iFrom, int jFrom, int iTo, int jTo) {
        int temp = tiles[iTo][jTo];
        tiles[iTo][jTo] = tiles[iFrom][jFrom];
        tiles[iFrom][jFrom] = temp;
    }

    // string representation of this board
    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        int width = Integer.toString((dimension * dimension - 1)).length() + 1;
        String format = "%" + width + "d";
        res.append(dimension);
        res.append('\n');
        for (int i = 0; i < dimension; ++i) {
            for (int j = 0; j < dimension; ++j)
                res.append(String.format(format, (int) board[i][j]));
            res.append('\n');
        }
        return res.toString();
    }

    // The version supplied by the course
    // @Override
    // public String toString() {
    //     StringBuilder s = new StringBuilder();
    //     s.append(dimension + "\n");
    //     for (int i = 0; i < dimension; i++) {
    //         for (int j = 0; j < dimension; j++) {
    //             s.append(String.format("%2d ", board[i][j]));
    //         }
    //         s.append("\n");
    //     }
    //     return s.toString();
    // }

    // board dimension n
    public int dimension() {
        return dimension;
    }

    // number of tiles out of place
    public int hamming() {
        return hamming;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        return manhattan;
    }

    // is this board the goal board?
    public boolean isGoal() {
        return isGoal;
    }

    // does this board equal y?
    @Override
    public boolean equals(Object y) {
        if (this == y)
            return true;
        if (y == null || y.getClass() != this.getClass())
            return false;
        final Board that = (Board) y;
        if (this.dimension != that.dimension)
            return false;
        for (int i = 0; i < dimension; ++i)
            for (int j = 0; j < dimension; ++j)
                if (this.board[i][j] != that.board[i][j])
                    return false;
        return true;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        LinkedList<Board> neighbors = new LinkedList<>();
        int iEmpty = 0;
        int jEmpty = 0;
        int[][] neighbor = new int[dimension][dimension];
        for (int i = 0; i < dimension; ++i) {
            for (int j = 0; j < dimension; ++j) {
                neighbor[i][j] = board[i][j];
                if (neighbor[i][j] == 0) {
                    iEmpty = i;
                    jEmpty = j;
                }
            }
        }
        if (iEmpty != 0) {
            int iOther = iEmpty - 1;
            swapTiles(neighbor, iEmpty, jEmpty, iOther, jEmpty);
            neighbors.add(new Board(neighbor));
            swapTiles(neighbor, iEmpty, jEmpty, iOther, jEmpty);
        }
        if (iEmpty != dimension - 1) {
            int iOther = iEmpty + 1;
            swapTiles(neighbor, iEmpty, jEmpty, iOther, jEmpty);
            neighbors.add(new Board(neighbor));
            swapTiles(neighbor, iEmpty, jEmpty, iOther, jEmpty);
        }
        if (jEmpty != 0) {
            int jOther = jEmpty - 1;
            swapTiles(neighbor, iEmpty, jEmpty, iEmpty, jOther);
            neighbors.add(new Board(neighbor));
            swapTiles(neighbor, iEmpty, jEmpty, iEmpty, jOther);
        }
        if (jEmpty != dimension - 1) {
            int jOther = jEmpty + 1;
            swapTiles(neighbor, iEmpty, jEmpty, iEmpty, jOther);
            neighbors.add(new Board(neighbor));
            swapTiles(neighbor, iEmpty, jEmpty, iEmpty, jOther);
        }
        return neighbors;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        int[][] twinTiles = new int[dimension][dimension];
        for (int i = 0; i < dimension; ++i)
            for (int j = 0; j < dimension; ++j)
                twinTiles[i][j] = board[i][j];

        int iFrom = 0;
        int jFrom = 0;
        int iTo = 0;
        int jTo = 1;
        int from = twinTiles[iFrom][jFrom];
        int to = twinTiles[iTo][jTo];
        if (from == 0) {
            if (dimension < 3) {
                iFrom = 1;
                jFrom = 0;
            }
            else
                jFrom = 2;
        }
        if (to == 0) {
            if (dimension < 3) {
                iTo = 1;
                jTo = 0;
            }
            else
                jTo = 2;
        }

        swapTiles(twinTiles, iFrom, jFrom, iTo, jTo);
        return new Board(twinTiles);
    }

    // unit testing (not graded)
    public static void main(String[] args) {
        for (String filename : args) {

            // read in the board specified in the filename
            In in = new In(filename);
            int n = in.readInt();
            int[][] tiles = new int[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    tiles[i][j] = in.readInt();
                }
            }

            Board initial = new Board(tiles);
            StdOut.println();
            StdOut.println("Initial board:");
            StdOut.print(initial.toString());
            StdOut.printf("Dimension: %d, Hamming: %d, Manhattan: %d, isGoal?: %b\n",
                          initial.dimension(),
                          initial.hamming(), initial.manhattan(), initial.isGoal());

            Board twin = initial.twin();
            StdOut.println();
            StdOut.println("Twin board:");
            StdOut.print(twin.toString());
            StdOut.printf("Dimension: %d, Hamming: %d, Manhattan: %d, isGoal?: %b\n",
                          twin.dimension(),
                          twin.hamming(), twin.manhattan(), twin.isGoal());

            StdOut.println();
            StdOut.println("Neighbor boards:");
            for (Board neighbor : initial.neighbors()) {
                StdOut.print(neighbor.toString());
                StdOut.printf("Dimension: %d, Hamming: %d, Manhattan: %d, isGoal?: %b\n",
                              neighbor.dimension(),
                              neighbor.hamming(), neighbor.manhattan(), neighbor.isGoal());
            }

            StdOut.println();
            StdOut.println("Neighbor boards of twin:");
            for (Board neighbor : twin.neighbors()) {
                StdOut.print(neighbor.toString());
                StdOut.printf("Dimension: %d, Hamming: %d, Manhattan: %d, isGoal?: %b\n",
                              neighbor.dimension(),
                              neighbor.hamming(), neighbor.manhattan(), neighbor.isGoal());
            }

            StdOut.println();
            StdOut.println("Test equality:");
            StdOut.println();
            for (Board neighbor : initial.neighbors()) {
                for (Board nn : neighbor.neighbors()) {
                    StdOut.println("A:");
                    StdOut.print(initial.toString());
                    StdOut.println("B:");
                    StdOut.print(nn.toString());
                    StdOut.print("A and B are equal? ");
                    if (initial.equals(nn))
                        StdOut.println("Yes");
                    else
                        StdOut.println("No");
                }
            }
        }
    }
}
