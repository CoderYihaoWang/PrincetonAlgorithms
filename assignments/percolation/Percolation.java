/******************************************************************************
 *  Name:              Yihao Wang
 *  Last modified:     12/24/2019
 ******************************************************************************/

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private final WeightedQuickUnionUF wqu;
    private final boolean[][] opened;
    private final boolean[] bottom;
    private final int n;
    private int numberOfOpenSites = 0;
    private boolean percolates = false;

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        if (n < 1)
            throw new IllegalArgumentException("The side length" + n + " is not larger than 1");
        wqu = new WeightedQuickUnionUF((n + 1) * (n + 1));
        this.n = n;
        opened = new boolean[n + 1][n + 1];
        bottom = new boolean[(n + 1) * (n + 1)];
        for (int i = (n + 1) * n; i < bottom.length; ++i)
            bottom[i] = true;
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
        if (opened[row][col])
            return;
        opened[row][col] = true;
        int index = xyTo1D(row, col);
        int up = -1;
        int down = -1;
        int left = -1;
        int right = -1;
        if (col > 1 && opened[row][col - 1]) {
            int other = xyTo1D(row, col - 1);
            left = wqu.find(other);
            wqu.union(index, other);
        }
        if (col < n && opened[row][col + 1]) {
            int other = xyTo1D(row, col + 1);
            right = wqu.find(other);
            wqu.union(index, other);
        }
        if (row < n && opened[row + 1][col]) {
            int other = xyTo1D(row + 1, col);
            down = wqu.find(other);
            wqu.union(index, other);
        }
        if (row > 1 && opened[row - 1][col]) {
            int other = xyTo1D(row - 1, col);
            up = wqu.find(other);
            wqu.union(index, other);
        }
        if (row == 1) {
            up = wqu.find(0);
            wqu.union(index, 0);
        }
        if ((left >= 0 && bottom[left])
                || (right >= 0 && bottom[right])
                || (up >= 0 && bottom[up])
                || (down >= 0 && bottom[down])
                || row == n) {
            if (left >= 0)
                bottom[left] = false;
            if (right >= 0)
                bottom[right] = false;
            if (up >= 0)
                bottom[up] = false;
            if (down >= 0)
                bottom[down] = false;
            int root = wqu.find(index);
            if (root == wqu.find(0))
                percolates = true;
            bottom[root] = true;
        }
        ++numberOfOpenSites;
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        validate(row, col);
        return opened[row][col];
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        validate(row, col);
        return wqu.find(xyTo1D(row, col)) == wqu.find(0);
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return numberOfOpenSites;
    }

    // does the system percolate?
    public boolean percolates() {
        return percolates;
    }
}
