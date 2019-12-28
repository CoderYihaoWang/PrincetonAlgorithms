import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;

public class BruteCollinearPoints {
    private final ArrayList<LineSegment> segmentList = new ArrayList<>();

    // finds all line segments containing 4 points
    public BruteCollinearPoints(Point[] points) {
        if (points == null)
            throw new IllegalArgumentException("The points array cannot be null");

        int len = points.length;

        if ((len > 0 && points[0] == null)
                || (len > 1 && points[1] == null)
                || (len > 2 && points[2] == null))
            throw new IllegalArgumentException("The point in the points array cannot be null");

        if (len == 2 && points[0].compareTo(points[1]) == 0)
            throw new IllegalArgumentException(
                    "There are repeated points in the input: " + points[0]
                            .toString());

        if (len == 3) {
            if (points[0].compareTo(points[1]) == 0 || points[0].compareTo(points[2]) == 0)
                throw new IllegalArgumentException(
                        "There are repeated points in the input: " + points[0]
                                .toString());
            if (points[1].compareTo(points[2]) == 0)
                throw new IllegalArgumentException(
                        "There are repeated points in the input: " + points[1]
                                .toString());
        }

        if (len < 4)
            return;

        for (int i = 0; i < len; ++i) {
            for (int j = i + 1; j < len; ++j) {
                for (int k = j + 1; k < len; ++k) {
                    for (int h = k + 1; h < len; ++h) {
                        if (points[h] == null)
                            throw new IllegalArgumentException(
                                    "The point in the points array cannot be null");
                        double k1 = points[i].slopeTo(points[j]);
                        double k2 = points[i].slopeTo(points[k]);
                        double k3 = points[i].slopeTo(points[h]);
                        if (k1 == Double.NEGATIVE_INFINITY || k2 == Double.NEGATIVE_INFINITY
                                || k3 == Double.NEGATIVE_INFINITY)
                            throw new IllegalArgumentException(
                                    "There are repeated points in the input: " + points[i]
                                            .toString());
                        if (Double.compare(k1, k2) == 0 && Double.compare(k2, k3) == 0) {
                            Point min = points[i];
                            Point max = points[i];
                            if (min.compareTo(points[j]) > 0)
                                min = points[j];
                            if (min.compareTo(points[k]) > 0)
                                min = points[k];
                            if (min.compareTo(points[h]) > 0)
                                min = points[h];
                            if (max.compareTo(points[j]) < 0)
                                max = points[j];
                            if (max.compareTo(points[k]) < 0)
                                max = points[k];
                            if (max.compareTo(points[h]) < 0)
                                max = points[h];
                            segmentList.add(new LineSegment(min, max));
                        }
                    }
                }
            }
        }
        if (points[len - 1].compareTo(points[len - 2]) == 0
                || points[len - 1].compareTo(points[len - 3]) == 0)
            throw new IllegalArgumentException(
                    "There are repeated points in the input: " + points[len - 1]
                            .toString());
        if (points[len - 2].compareTo(points[len - 3]) == 0)
            throw new IllegalArgumentException(
                    "There are repeated points in the input: " + points[len - 2]
                            .toString());

    }

    // the number of line segments
    public int numberOfSegments() {
        return segmentList.size();
    }

    // the line segments
    public LineSegment[] segments() {
        LineSegment[] res = new LineSegment[segmentList.size()];
        return segmentList.toArray(res);
    }

    public static void main(String[] args) {
        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
