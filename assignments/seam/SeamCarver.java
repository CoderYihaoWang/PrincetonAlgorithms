


/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

public class SeamCarver {

    private Picture picture;
    private int height;
    private int width;
    private double[][] energy;
    private boolean transposed = false;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        this.picture = new Picture(picture);
        height = picture.height();
        width = picture.width();
        energy = new double[height][width];
        for (int i = 1; i < height - 1; ++i)
            for (int j = 1; j < width - 1; ++j)
                energy[i][j] = computeEnergy(i, j);
    }

    // inputs should not be on the edge, those conditions must be dealt with by caller
    private double computeEnergy(int x, int y) {
        if (x == 0 || x == height - 1 || y == 0 || y == width - 1)
            return 1000.0;
        int c = picture.getRGB(y, x);
        int up = picture.getRGB(y, x - 1);
        int down = picture.getRGB(y, x + 1);
        int left = picture.getRGB(y - 1, x);
        int right = picture.getRGB(y + 1, x);
        return Math.sqrt(computeDelta(c, up, down) + computeDelta(c, left, right));
    }

    private double computeDelta(int c, int left, int right) {
        int cr = (c >> 16) & 0xff;
        int cg = (c >> 8) & 0xff;
        int cb = c & 0xff;
        int lr = (left >> 16) & 0xff;
        int lg = (left >> 8) & 0xff;
        int lb = left & 0xff;
        int rr = (right >> 16) & 0xff;
        int rg = (right >> 8) & 0xff;
        int rb = right & 0xff;
        return (cr - lr) * (cr - lr)
                + (cg - lg) * (cg - lg)
                + (cb - lb) * (cb - lb)
                + (cr - rr) * (cr - rr)
                + (cg - rg) * (cg - rg)
                + (cb - rb) * (cb - rb);
    }

    // current picture
    public Picture picture() {
        if (transposed)
            transpose();
        transposed = false;
        return picture;
    }

    // width of current picture
    public int width() {
        if (transposed)
            transpose();
        transposed = false;
        return width;
    }

    // height of current picture
    public int height() {
        if (transposed)
            transpose();
        transposed = false;
        return height;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        return energy[y][x];
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        if (!transposed)
            transpose();
        transposed = false;
        int[] ans = findVerticalSeam();
        transposed = true;
        return ans;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        if (transposed)
            transpose();
        transposed = false;
        double[][] distTo = new double[height][width];
        int[][] edgeTo = new int[height][width];
        for (int i = 0; i < width; ++i) {
            distTo[0][i] = energy[0][i];
            edgeTo[0][i] = -1;
        }
        for (int i = 1; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                double left = j == 0 ? Double.POSITIVE_INFINITY : distTo[i - 1][j - 1];
                double middle = distTo[i - 1][j];
                double right = j == width - 1 ? Double.POSITIVE_INFINITY : distTo[i - 1][j + 1];
                if (left <= middle && left <= right) {
                    edgeTo[i][j] = j - 1;
                    distTo[i][j] = energy[i][j] + left;
                }
                else if (middle <= right && middle <= left) {
                    edgeTo[i][j] = j;
                    distTo[i][j] = energy[i][j] + middle;
                }
                else {
                    edgeTo[i][j] = j + 1;
                    distTo[i][j] = energy[i][j] + right;
                }
            }
        }
        int minId = 0;
        double min = Double.POSITIVE_INFINITY;
        for (int i = 0; i < width; ++i) {
            if (distTo[height - 1][i] < min) {
                min = distTo[height - 1][i];
                minId = i;
            }
        }
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                StdOut.printf("%.2f ", distTo[i][j]);
            }
            StdOut.println();
        }

        int[] ans = new int[height];
        Stack<Integer> stack = new Stack<>();
        for (int i = height - 1; i >= 0; --i) {
            stack.push(minId);
            minId = edgeTo[i][minId];
        }
        for (int i = 0; i < height; ++i)
            ans[i] = stack.pop();
        return ans;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (!transposed)
            transpose();
        transposed = false;
        removeVerticalSeam(seam);
        transposed = true;
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (transposed)
            transpose();
        transposed = false;
    }

    private void transpose() {
        int t = height;
        height = width;
        width = t;
        Picture tp = new Picture(width, height);
        double[][] te = new double[height][width];
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                tp.setRGB(j, i, picture.getRGB(i, j));
                te[i][j] = energy[j][i];
            }
        }
        picture = tp;
        energy = te;
    }

    //  unit testing (optional)
    public static void main(String[] args) {

    }

}
