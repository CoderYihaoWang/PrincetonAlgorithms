import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class FastCollinearPoints {
    private final SegmentList segmentList = new SegmentList();

    private class SegmentList {
        private class SegmentNode {
            private final LineSegment value;
            private SegmentNode next;

            public SegmentNode(LineSegment value, SegmentNode next) {
                this.value = value;
                this.next = next;
            }
        }

        private SegmentNode head = null;
        private int size = 0;

        public void add(LineSegment seg) {
            head = new SegmentNode(seg, head);
            ++size;
        }

        public int size() {
            return size;
        }

        public LineSegment[] toArray() {
            LineSegment[] res = new LineSegment[size];
            SegmentNode current = head;
            for (int i = 0; i < size; ++i) {
                res[i] = current.value;
                current = current.next;
            }
            return res;
        }
    }

    // finds all line segments containing 4 points
    public FastCollinearPoints(Point[] points) {
        if (points == null)
            throw new IllegalArgumentException("The points array cannot be null");

        if (points.length < 4)
            return;

        for (Point p : points)
            if (p == null)
                throw new IllegalArgumentException("The point in points array cannot be null");

        Arrays.sort(points);
        int len = points.length;
        Point current = points[0];
        for (int i = 1; i < len; ++i) {
            if (current.compareTo(points[i]) == 0)
                throw new IllegalArgumentException(
                        "There are repeated points in the input: " + current.toString());
            current = points[i];
        }

        for (int i = 0; i < len - 3; ++i) {
            Arrays.sort(points, i + 1, len, points[i].slopeOrder());
            for (int j = i + 1; j < len - 2; ++j) {
                double k1 = points[i].slopeTo(points[j]);
                double k2 = points[i].slopeTo(points[j + 1]);
                if (k1 != k2)
                    continue;
                double k3 = points[i].slopeTo(points[j + 2]);
                if (k2 == k3)
                    segmentList.add(new LineSegment(points[i], points[j + 2]));
            }
            Arrays.sort(points, i + 1, len);
        }
    }

    // the number of line segments
    public int numberOfSegments() {
        return segmentList.size();
    }

    // the line segments
    public LineSegment[] segments() {
        return segmentList.toArray();
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
