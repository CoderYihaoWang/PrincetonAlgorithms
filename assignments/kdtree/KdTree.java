import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.util.ArrayList;

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
        if (p == null)
            throw new IllegalArgumentException("The inserted point cannot be null");
        if (size == 0) {
            root = new Node(p, null, null, true);
            ++size;
        }
        else
            nodeInsert(root, p);
    }

    private void nodeInsert(Node node, Point2D p) {
        if (node.value.equals(p))
            return;
        boolean newNodeIsVertical;
        boolean condition;
        if (node.isVertical) {
            condition = p.x() < node.value.x();
            newNodeIsVertical = false;
        }
        else {
            condition = p.y() < node.value.y();
            newNodeIsVertical = true;
        }
        if (condition) {
            if (node.left == null) {
                node.left = new Node(p, null, null, newNodeIsVertical);
                ++size;
            }
            else
                nodeInsert(node.left, p);
        }
        else {
            if (node.right == null) {
                node.right = new Node(p, null, null, newNodeIsVertical);
                ++size;
            }
            else
                nodeInsert(node.right, p);
        }
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException("The point to find cannot be null");
        return nodeContains(root, p);
    }

    private boolean nodeContains(Node node, Point2D p) {
        if (node == null)
            return false;
        if (node.value.equals(p))
            return true;
        if (node.isVertical) {
            if (p.x() < node.value.x())
                return nodeContains(node.left, p);
            return nodeContains(node.right, p);
        }
        if (p.y() < node.value.y())
            return nodeContains(node.left, p);
        return nodeContains(node.right, p);
    }

    // draw all points to standard draw
    public void draw() {
        nodeDraw(root);
    }

    private void nodeDraw(Node node) {
        if (node == null)
            return;
        node.value.draw();
        nodeDraw(node.left);
        nodeDraw(node.right);
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null)
            throw new IllegalArgumentException("The rectangle cannot be null");
        ArrayList<Point2D> res = new ArrayList<>();
        nodeRange(root, rect, res);
        return res;
    }

    private void nodeRange(Node node, RectHV rect, ArrayList<Point2D> res) {
        if (node == null)
            return;
        if (rect.contains(node.value))
            res.add(node.value);
        if (node.isVertical) {
            if (rect.xmin() < node.value.x())
                nodeRange(node.left, rect, res);
            if (rect.xmax() >= node.value.x())
                nodeRange(node.right, rect, res);
        }
        else {
            if (rect.ymin() < node.value.y())
                nodeRange(node.left, rect, res);
            if (rect.ymax() >= node.value.y())
                nodeRange(node.right, rect, res);
        }
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException("The point to find cannot be null");
        if (size == 0)
            return null;
        return nodeNearest(root, p, new RectHV(0.0, 0.0, 1.0, 1.0), root.value);
    }

    private Point2D nodeNearest(Node node, Point2D p, RectHV rect,
                                Point2D currentNearest) {
        if (node.value.distanceSquaredTo(p) < currentNearest.distanceSquaredTo(p))
            currentNearest = node.value;

        boolean condition;
        RectHV leftRect;
        RectHV rightRect;
        if (node.isVertical) {
            condition = p.x() < node.value.x();
            leftRect = new RectHV(rect.xmin(), rect.ymin(), node.value.x(), rect.ymax());
            rightRect = new RectHV(node.value.x(), rect.ymin(), rect.xmax(), rect.ymax());
        }
        else {
            condition = p.y() < node.value.y();
            leftRect = new RectHV(rect.xmin(), rect.ymin(), rect.xmax(), node.value.y());
            rightRect = new RectHV(rect.xmin(), node.value.y(), rect.xmax(), rect.ymax());
        }

        Node next;
        Node other;
        RectHV nextRect;
        RectHV otherRect;
        if (condition) {
            next = node.left;
            other = node.right;
            nextRect = leftRect;
            otherRect = rightRect;
        }
        else {
            next = node.right;
            other = node.left;
            nextRect = rightRect;
            otherRect = leftRect;
        }

        if (next != null)
            if (nextRect.distanceSquaredTo(p) < currentNearest.distanceSquaredTo(p))
                currentNearest = nodeNearest(next, p, nextRect, currentNearest);

        if (other != null)
            if (otherRect.distanceSquaredTo(p) < currentNearest.distanceSquaredTo(p))
                return nodeNearest(other, p, otherRect, currentNearest);

        return currentNearest;
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
