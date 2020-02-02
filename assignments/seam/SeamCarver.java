
/* *****************************************************************************
 *  Name: SeamCarver.java
 *  Date: 3/2/2020
 *  Description: Resizes a picture using the seam carving algorithm
 **************************************************************************** */

import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;

public class SeamCarver {

    private Picture picture;
    private int height;
    private int width;
    private double[][] energy;
    // is the picture stored in transposed mode?
    private boolean transposed = false;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null)
            throw new IllegalArgumentException("The picture cannot be null");

        this.picture = new Picture(picture);
        height = picture.height();
        width = picture.width();
        energy = new double[height][width];
        for (int i = 0; i < height; ++i)
            for (int j = 0; j < width; ++j)
                energy[i][j] = computeEnergy(i, j);
    }

    // inputs should not be on the edge, those conditions must be dealt with by caller
    private double computeEnergy(int x, int y) {
        if (x == 0 || x == height - 1 || y == 0 || y == width - 1)
            return 1000.0;
        int up = picture.getRGB(y, x - 1);
        int down = picture.getRGB(y, x + 1);
        int left = picture.getRGB(y - 1, x);
        int right = picture.getRGB(y + 1, x);
        return Math.sqrt(computeDelta(up, down) + computeDelta(left, right));
    }

    private double computeDelta(int left, int right) {
        int lr = (left >> 16) & 0xff;
        int lg = (left >> 8) & 0xff;
        int lb = left & 0xff;
        int rr = (right >> 16) & 0xff;
        int rg = (right >> 8) & 0xff;
        int rb = right & 0xff;
        return (rr - lr) * (rr - lr) + (rg - lg) * (rg - lg) + (rb - lb) * (rb - lb);
    }

    // current picture
    public Picture picture() {
        // makes sure that the returned picture is not transposed
        if (transposed)
            transpose();
        transposed = false;
        return new Picture(picture);
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
        if (transposed)
            transpose();
        transposed = false;
        if (x >= width || y >= height || x < 0 || y < 0)
            throw new IllegalArgumentException("Invalid indices!");
        return energy[y][x];
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        if (!transposed)
            transpose();
        transposed = false; // change transposed to false to make it acceptable for findVerticalSeam
        int[] ans = findVerticalSeam();
        transposed = true; // and then change it back
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

        if (width <= 1)
            throw new IllegalArgumentException("Cannot remove as the width is too small");
        if (seam == null)
            throw new IllegalArgumentException("The seam cannot be null!");
        if (seam.length != height)
            throw new IllegalArgumentException("Invalid seam!");
        int last = seam[0];
        if (last >= width || last < 0)
            throw new IllegalArgumentException("Invalid seam!");
        for (int i = 1; i < height; ++i) {
            if (seam[i] >= width || seam[i] < 0 || Math.abs(seam[i] - last) > 1)
                throw new IllegalArgumentException("Invalid seam!");
            last = seam[i];
        }

        Picture tp = new Picture(width - 1, height);
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < seam[i]; ++j)
                tp.setRGB(j, i, picture.getRGB(j, i));
            for (int j = seam[i]; j < width - 1; ++j)
                tp.setRGB(j, i, picture.getRGB(j + 1, i));
        }
        picture = tp;
        width = width - 1;
        double[][] te = new double[height][width];
        for (int i = 0; i < height; ++i) {
            if (seam[i] != 0) {
                System.arraycopy(energy[i], 0, te[i], 0, seam[i] - 1);
                te[i][seam[i] - 1] = computeEnergy(i, seam[i] - 1);
            }
            if (seam[i] != width) {
                te[i][seam[i]] = computeEnergy(i, seam[i]);
                System.arraycopy(energy[i], seam[i] + 2, te[i], seam[i] + 1, width - 1 - seam[i]);
            }
        }
        energy = te;
    }

    private void transpose() {
        // switching height and width
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

    //  test client copied from ResizeDemo.java
    public static void main(String[] args) {
        if (args.length != 3) {
            StdOut.println(
                    "Usage:\njava ResizeDemo [image filename] [num cols to remove] [num rows to remove]");
            return;
        }

        Picture inputImg = new Picture(args[0]);
        int removeColumns = Integer.parseInt(args[1]);
        int removeRows = Integer.parseInt(args[2]);

        StdOut.printf("image is %d columns by %d rows\n", inputImg.width(), inputImg.height());
        SeamCarver sc = new SeamCarver(inputImg);

        Stopwatch sw = new Stopwatch();

        for (int i = 0; i < removeRows; i++) {
            int[] horizontalSeam = sc.findHorizontalSeam();
            sc.removeHorizontalSeam(horizontalSeam);
        }

        for (int i = 0; i < removeColumns; i++) {
            int[] verticalSeam = sc.findVerticalSeam();
            sc.removeVerticalSeam(verticalSeam);
        }
        Picture outputImg = sc.picture();

        StdOut.printf("new image size is %d columns by %d rows\n", sc.width(), sc.height());

        StdOut.println("Resizing time: " + sw.elapsedTime() + " seconds.");
        inputImg.show();
        outputImg.show();
    }
}
