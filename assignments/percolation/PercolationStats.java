/******************************************************************************
 *  Name:              Yihao Wang
 *  Last modified:     12/24/2019
 ******************************************************************************/

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    private final double mean;
    private final double stddev;
    private final double confidenceLo;
    private final double confidenceHi;

    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {
        if (n <= 0)
            throw new IllegalArgumentException("n must be larger than 1");
        if (trials <= 0)
            throw new IllegalArgumentException("trials must be larger than 1");
        double[] percent = new double[trials];
        int[] order = new int[n * n];
        for (int i = 0; i < n * n; ++i)
            order[i] = i;
        for (int i = 0; i < trials; ++i) {
            Percolation perc = new Percolation(n);
            StdRandom.shuffle(order);
            int j = 0;
            while (!perc.percolates()) {
                perc.open(order[j] / n + 1, order[j] % n + 1);
                ++j;
            }
            percent[i] = (double) perc.numberOfOpenSites() / n / n;
        }
        mean = StdStats.mean(percent);
        stddev = StdStats.stddev(percent);
        double halfWidth = 1.96 * stddev / Math.sqrt(trials);
        confidenceLo = mean - halfWidth;
        confidenceHi = mean + halfWidth;
    }

    // sample mean of percolation threshold
    public double mean() {
        return mean;
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return stddev;
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        return confidenceLo;
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return confidenceHi;
    }

    // test client (see below)
    public static void main(String[] args) {
        PercolationStats ps = new PercolationStats(Integer.parseInt(args[0]),
                                                   Integer.parseInt(args[1]));
        StdOut.printf("mean                    = %.10f\n", ps.mean());
        StdOut.printf("stddev                  = %.10f\n", ps.stddev());
        StdOut.printf("95%% confidence interval = [%.10f, %.10f]\n", ps.confidenceLo(),
                      ps.confidenceHi());
    }
}
