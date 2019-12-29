import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;

import java.util.Comparator;
import java.util.LinkedList;

public class Solver {
    private final LinkedList<Board> solution = new LinkedList<>();
    private final boolean isSolvable;

    private class SearchNode {
        private final Board board;
        private final int moves;
        private final int manhattan;
        private final boolean isGoal;
        private final SearchNode daddy;

        public SearchNode(Board board, SearchNode daddy) {
            this.board = board;
            manhattan = board.manhattan();
            isGoal = board.isGoal();
            this.daddy = daddy;
            if (daddy == null)
                moves = 0;
            else
                moves = daddy.moves + 1;
        }
    }

    private class SearchNodeComparator implements Comparator<SearchNode> {

        public int compare(SearchNode s1, SearchNode s2) {
            return s1.manhattan + s1.moves - s2.manhattan - s2.moves;
        }
    }

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null)
            throw new IllegalArgumentException("The initial board cannot be null");

        SearchNodeComparator comparator = new SearchNodeComparator();

        MinPQ<SearchNode> gameTree = new MinPQ<>(comparator);
        MinPQ<SearchNode> twinTree = new MinPQ<>(comparator);

        gameTree.insert(new SearchNode(initial, null));
        twinTree.insert(new SearchNode(initial.twin(), null));

        SearchNode current;
        SearchNode currentTwin;

        while (true) {
            current = gameTree.delMin();
            currentTwin = twinTree.delMin();

            if (current.isGoal || currentTwin.isGoal)
                break;

            for (Board neighbor : current.board.neighbors())
                if (current.daddy == null || !neighbor.equals(current.daddy.board))
                    gameTree.insert(new SearchNode(neighbor, current));

            for (Board twinNeighbor : currentTwin.board.neighbors())
                if (currentTwin.daddy == null || !twinNeighbor.equals(currentTwin.daddy.board))
                    twinTree.insert(new SearchNode(twinNeighbor, currentTwin));
        }

        if (currentTwin.isGoal)
            isSolvable = false;
        else {
            isSolvable = true;
            LinkedList<Board> solutionHelper = new LinkedList<>();
            while (current != null) {
                solutionHelper.add(current.board);
                current = current.daddy;
            }
            while (!solutionHelper.isEmpty())
                solution.add(solutionHelper.removeLast());
        }
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return isSolvable;
    }

    // min number of moves to solve initial board
    public int moves() {
        return solution.size() - 1;
    }

    // sequence of boards in a shortest solution
    public Iterable<Board> solution() {
        if (solution.isEmpty())
            return null;
        return solution;
    }

    // test client (see below)
    public static void main(String[] args) {

        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }

}

