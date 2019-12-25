/******************************************************************************
 *  Name:              Yihao Wang
 *  Last modified:     12/24/2019
 ******************************************************************************/

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private final WeightedQuickUnionUF wqu;
    private final boolean[][] visited;
    private final int n;
    private final int head;
    private final int tail;
    private int numberOfOpenSites;

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        if (n < 1)
            throw new IllegalArgumentException("The side length" + n + " is not larger than 1");
        wqu = new WeightedQuickUnionUF((n + 1) * (n + 1));
        this.n = n;
        head = 0;
        tail = n * (n + 1);
        numberOfOpenSites = 0;
        visited = new boolean[n + 1][n + 1];
    }

    private void validate(int row, int col) {
        if (row < 1 || row > n)
            throw new IllegalArgumentException(
                    "The row index " + row + " is not between 1 and " + n);
        if (col < 1 || col > n)
            throw new IllegalArgumentException(
                    "The column index " + col + " is not between 1 and " + n);
    }

    private int xyTo1D(int row, int col) {
        return row * (n + 1) + col;
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        validate(row, col);
        if (visited[row][col])
            return;
        int index = xyTo1D(row, col);
        if (col > 1 && isOpen(row, col - 1))
            wqu.union(index, index - 1);
        if (col < n && isOpen(row, col + 1))
            wqu.union(index, index + 1);
        if (row > 1 && isOpen(row - 1, col))
            wqu.union(index, index - n - 1);
        if (row < n && isOpen(row + 1, col))
            wqu.union(index, index + n + 1);
        if (row == 1)
            wqu.union(index, head);
        if (row == n)
            wqu.union(index, tail);
        ++numberOfOpenSites;
        visited[row][col] = true;
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        validate(row, col);
        return visited[row][col];
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        validate(row, col);
        return wqu.find(xyTo1D(row, col)) == wqu.find(head);
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return numberOfOpenSites;
    }

    // does the system percolate?
    public boolean percolates() {
        return wqu.find(tail) == wqu.find(head);
    }
}
