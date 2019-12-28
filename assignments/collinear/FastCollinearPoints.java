import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;

public class FastCollinearPoints {
    private final ArrayList<LineSegment> segmentList = new ArrayList<>();

    // finds all line segments containing 4 points
    public FastCollinearPoints(Point[] points) {
        if (points == null)
            throw new IllegalArgumentException("The points array cannot be null");

        int len = points.length;
        Point[] pointsCopy = new Point[len];

        for (int i = 0; i < len; ++i) {
            if (points[i] == null)
                throw new IllegalArgumentException("The point in points array cannot be null");
            pointsCopy[i] = points[i];
        }

        Arrays.sort(pointsCopy);

        Point current = pointsCopy[0];
        for (int i = 1; i < len; ++i) {
            if (current.compareTo(pointsCopy[i]) == 0)
                throw new IllegalArgumentException(
                        "There are repeated points in the input: " + current.toString());
            current = pointsCopy[i];
        }

        if (len < 4)
            return;

        for (int i = 0; i < len; ++i) {
            current = points[i];
            Arrays.sort(pointsCopy, current.slopeOrder());
            int end;
            for (int start = 0; start < len - 2; start = end) {
                int count = 2;
                double currentSlope = current.slopeTo(pointsCopy[start]);
                for (end = start + 1;
                     end < len
                             && Double.compare(current.slopeTo(pointsCopy[end]), currentSlope)
                             == 0;
                     ++end)
                    ++count;
                if (count >= 4) {
                    Point[] line = new Point[count];
                    line[0] = current;
                    for (int j = start; j < end; ++j)
                        line[j - start + 1] = pointsCopy[j];
                    Point min = line[0];
                    Point max = line[0];
                    for (int j = 1; j < count; ++j) {
                        if (min.compareTo(line[j]) > 0)
                            min = line[j];
                        if (max.compareTo(line[j]) < 0)
                            max = line[j];
                    }
                    if (line[0].equals(min))
                        segmentList.add(new LineSegment(min, max));
                }
            }
        }
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
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
