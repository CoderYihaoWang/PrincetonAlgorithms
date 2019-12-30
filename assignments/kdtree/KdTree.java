import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

public class KdTree {
    private Node root;
    private int size;

    private class Node {
        private final Point2D value;
        private Node left;
        private Node right;
        private final boolean isVertical;

        public Node(Point2D value, Node left, Node right, boolean isVertical) {
            this.value = value;
            this.left = left;
            this.right = right;
            this.isVertical = isVertical;
        }
    }

    // construct an empty set of points
    public KdTree() {
        root = null;
        size = 0;
    }

    // is the set empty?
    public boolean isEmpty() {
        return size == 0;
    }

    // number of points in the set
    public int size() {
        return size;
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (size == 0)
            root = new Node(p, null, null, true);

    }


    // does the set contain point p?
    public boolean contains(Point2D p) {
        return find(root, p) != null;
    }

    private Node find(Node node, Point2D p) {
        if (node == null)
            return null;
        if (node.value.equals(p))
            return node;

    }

    // draw all points to standard draw
    public void draw() {
        drawTree(root);
    }

    private void drawTree(Node node) {
        if (node == null)
            return;
        node.value.draw();
        drawTree(node.left);
        drawTree(node.right);
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        return null;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        return null;
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
        // initialize the two data structures with point from file
        String filename = args[0];
        In in = new In(filename);
        KdTree kdtree = new KdTree();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kdtree.insert(p);
        }

        // process nearest neighbor queries
        StdDraw.enableDoubleBuffering();
        while (true) {

            // the location (x, y) of the mouse
            double x = StdDraw.mouseX();
            double y = StdDraw.mouseY();
            Point2D query = new Point2D(x, y);

            // draw all of the points
            StdDraw.clear();
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setPenRadius(0.01);
            kdtree.draw();

            // draw in blue the nearest neighbor (using kd-tree algorithm)
            StdDraw.setPenRadius(0.03);
            StdDraw.setPenColor(StdDraw.BLUE);
            kdtree.nearest(query).draw();
            StdDraw.show();
            StdDraw.pause(40);
        }
    }
}
