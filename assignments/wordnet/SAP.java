/* *****************************************************************************
 *  Name: SAP.java
 *  Date: 1/2/2020
 *  Description: computes the length of shortest ancestor path, and the ancestor thereof
 **************************************************************************** */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashSet;

public class SAP {

    private static final int INFINITY = Integer.MAX_VALUE;
    private static final int CACHESIZE = 1 << 3;

    private final Digraph G;
    private final int[] vDistTo;
    private final int[] wDistTo;
    private int length;
    private int ancestor;

    private final int[] cachedVs = new int[CACHESIZE];
    private final int[] cachedWs = new int[CACHESIZE];
    private final int[] cachedLengths = new int[CACHESIZE];
    private final int[] cachedAncestors = new int[CACHESIZE];
    private int cachePos = 0;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null)
            throw new IllegalArgumentException("The argument cannot be null");
        this.G = new Digraph(G);
        int v = this.G.V();
        vDistTo = new int[v];
        wDistTo = new int[v];
        for (int i = 0; i < v; ++i) {
            vDistTo[i] = INFINITY;
            wDistTo[i] = INFINITY;
        }
        for (int i = 0; i < CACHESIZE; ++i)
            cachedVs[i] = -1;
    }

    private boolean checkCache(int v, int w) {
        for (int i = 0; i < CACHESIZE; ++i) {
            if ((cachedVs[i] == v && cachedWs[i] == w)
                    || (cachedVs[i] == w && cachedWs[i] == v)) {
                length = cachedLengths[i];
                ancestor = cachedAncestors[i];
                return true;
            }
        }
        return false;
    }

    private void setCache(int v, int w, int len, int anc) {
        cachePos = (cachePos + 1) % CACHESIZE;
        cachedVs[cachePos] = v;
        cachedWs[cachePos] = w;
        cachedLengths[cachePos] = len;
        cachedAncestors[cachePos] = anc;
    }

    private void validateVertex(int v) {
        if (v < 0 || v >= G.V())
            throw new IllegalArgumentException("The vertex " + v + " is out of bound " + G.V());
    }

    private void bfs(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        if (v == w) {
            length = 0;
            ancestor = v;
            return;
        }
        if (checkCache(v, w))
            return;

        Queue<Integer> qv = new Queue<>();
        Queue<Integer> qw = new Queue<>();
        HashSet<Integer> vMarked = new HashSet<>();
        HashSet<Integer> wMarked = new HashSet<>();
        length = INFINITY;
        ancestor = -1;

        qv.enqueue(v);
        vMarked.add(v);
        vDistTo[v] = 0;
        qw.enqueue(w);
        wMarked.add(w);
        wDistTo[w] = 0;

        boolean testV = true;
        boolean testW = true;

        while ((!qv.isEmpty() && testV) || (!qw.isEmpty() && testW)) {
            if (!qv.isEmpty() && testV) {
                int cv = qv.dequeue();
                for (int dad : G.adj(cv)) {
                    if (vMarked.contains(dad))
                        continue;
                    vMarked.add(dad);
                    vDistTo[dad] = vDistTo[cv] + 1;
                    if (vDistTo[dad] > length) {
                        testV = false;
                        break;
                    }
                    if (wMarked.contains(dad)) {
                        int len = wDistTo[dad] + vDistTo[dad];
                        if (len < length) {
                            length = len;
                            ancestor = dad;
                        }
                    }
                    qv.enqueue(dad);
                }
            }
            if (!qw.isEmpty() && testW) {
                int cw = qw.dequeue();
                for (int dad : G.adj(cw)) {
                    if (wMarked.contains(dad))
                        continue;
                    wMarked.add(dad);
                    wDistTo[dad] = wDistTo[cw] + 1;
                    if (wDistTo[dad] > length) {
                        testW = false;
                        break;
                    }
                    if (vMarked.contains(dad)) {
                        int len = wDistTo[dad] + vDistTo[dad];
                        if (len < length) {
                            length = len;
                            ancestor = dad;
                        }
                    }
                    qw.enqueue(dad);
                }
            }
        }
        if (length == INFINITY)
            length = -1;
        setCache(v, w, length, ancestor);
        for (int i : vMarked)
            vDistTo[i] = INFINITY;
        for (int i : wMarked)
            wDistTo[i] = INFINITY;
    }

    private void bfs(Iterable<Integer> v, Iterable<Integer> w) {
        Queue<Integer> qv = new Queue<>();
        Queue<Integer> qw = new Queue<>();
        HashSet<Integer> vMarked = new HashSet<>();
        HashSet<Integer> wMarked = new HashSet<>();
        length = INFINITY;
        ancestor = -1;

        for (Integer x : v) {
            if (x == null)
                throw new IllegalArgumentException("The Iterable cannot contain null element");
            validateVertex(x);
            qv.enqueue(x);
            vMarked.add(x);
            vDistTo[x] = 0;
        }
        for (Integer x : w) {
            if (x == null)
                throw new IllegalArgumentException("The Iterable cannot contain null element");
            validateVertex(x);
            if (vMarked.contains(x)) {
                length = 0;
                ancestor = x;
                return;
            }
            qw.enqueue(x);
            wMarked.add(x);
            wDistTo[x] = 0;
        }

        boolean testV = true;
        boolean testW = true;

        while ((!qv.isEmpty() && testV) || (!qw.isEmpty() && testW)) {
            if (!qv.isEmpty()) {
                int cv = qv.dequeue();
                for (int dad : G.adj(cv)) {
                    if (vMarked.contains(dad))
                        continue;
                    vMarked.add(dad);
                    vDistTo[dad] = vDistTo[cv] + 1;
                    if (vDistTo[dad] > length) {
                        testV = false;
                        break;
                    }
                    if (wMarked.contains(dad)) {
                        int len = wDistTo[dad] + vDistTo[dad];
                        if (len < length) {
                            length = len;
                            ancestor = dad;
                        }
                    }
                    qv.enqueue(dad);
                }
            }
            if (!qw.isEmpty()) {
                int cw = qw.dequeue();
                for (int dad : G.adj(cw)) {
                    if (wMarked.contains(dad))
                        continue;
                    wMarked.add(dad);
                    wDistTo[dad] = wDistTo[cw] + 1;
                    if (wDistTo[dad] > length) {
                        testW = false;
                        break;
                    }
                    if (vMarked.contains(dad)) {
                        int len = wDistTo[dad] + vDistTo[dad];
                        if (len < length) {
                            length = len;
                            ancestor = dad;
                        }
                    }
                    qw.enqueue(dad);
                }
            }
        }
        if (length == INFINITY)
            length = -1;
        for (int i : vMarked)
            vDistTo[i] = INFINITY;
        for (int i : wMarked)
            wDistTo[i] = INFINITY;
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        bfs(v, w);
        return length;
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        bfs(v, w);
        return ancestor;
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null)
            throw new IllegalArgumentException("The arguments cannot be null");
        bfs(v, w);
        return length;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null)
            throw new IllegalArgumentException("The arguments cannot be null");
        bfs(v, w);
        return ancestor;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
