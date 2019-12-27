import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class BruteCollinearPoints {
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
    public BruteCollinearPoints(Point[] points) {
        if (points == null)
            throw new IllegalArgumentException("The points array cannot be null");

        if (points.length < 4)
            return;

        if (points[0] == null || points[1] == null || points[2] == null)
            throw new IllegalArgumentException("The point in the points array cannot be null");

        int len = points.length;
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
                        if (k1 == k2 && k2 == k3) {
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
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
