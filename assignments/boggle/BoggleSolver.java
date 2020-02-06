
/* *****************************************************************************
 *  Name: BoggleSolver.java
 *  Date: 6/2/2020
 *  Description: solve a Boggle game
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class BoggleSolver {

    // the node type in a trie representation of the dictionary
    private class DictNode {
        private String word;
        // null if the node is only an intermediate node, otherwise store the word here
        private final DictNode[] next = new DictNode[26]; // next words, 0-25 = 'A'-'Z'
    }

    // the dictionary
    private final DictNode root = new DictNode();

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        // construct the dictionary trie
        for (int i = 0; i < dictionary.length; ++i) {
            DictNode cur = root;
            String s = dictionary[i];
            for (int j = 0; j < s.length(); ++j) {
                int id = (int) s.charAt(j) - 'A';
                if (cur.next[id] == null)
                    cur.next[id] = new DictNode();
                cur = cur.next[id];
            }
            cur.word = s;
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        HashSet<String> ans = new HashSet<>();
        int r = board.rows();
        int c = board.cols();
        int[] bd = new int[r * c];
        int len = 0;
        for (int j = 0; j < r; ++j)
            for (int k = 0; k < c; ++k)
                bd[len++] = board.getLetter(j, k) - 'A';
        HashMap<Integer, ArrayList<Integer>> adj
                = new HashMap<>(); // an array could be used, but the autograder forbits it

        // construct the adjacency list
        for (int i = 0; i < len; ++i) {
            ArrayList<Integer> al = new ArrayList<>();
            if (i >= c)
                al.add(i - c);
            if (i < len - c)
                al.add(i + c);
            if (i % c != 0) {
                al.add(i - 1);
                if (i >= c)
                    al.add(i - c - 1);
                if (i < len - c)
                    al.add(i + c - 1);
            }
            if (i % c != c - 1) {
                al.add(i + 1);
                if (i >= c)
                    al.add(i - c + 1);
                if (i < len - c)
                    al.add(i + c + 1);
            }
            adj.put(i, al);
        }
        boolean[] visited = new boolean[len];
        for (int i = 0; i < len; ++i)
            dfs(bd, adj, visited, i, root, ans);
        return ans;
    }

    // depth first search the board from position n
    // while moving along the searching path on the board, move along the trie at the same time
    private void dfs(int[] board, HashMap<Integer, ArrayList<Integer>> adj, boolean[] visited,
                     int n, DictNode node, HashSet<String> ans) {

        int letter = board[n];

        // deal with letter 'Q', it is seen as 'qu'
        if (letter == 'Q' - 'A') {
            node = node.next[letter];
            letter = 'U' - 'A';
            if (node == null)
                return;
        }

        // check the letter in the current trie node
        // if there is no such entry, give up the whole branch
        DictNode next = node.next[letter];
        if (next == null)
            return;

        // if the current position is a word whose length is larger than 3, then add it to the result
        String word = next.word;
        if (word != null && word.length() >= 3)
            ans.add(word);

        // mark the current position on the board as marked
        // recursively search next posible positions
        // and switch visited to false before ending, to make the current position available for other paths
        visited[n] = true;
        for (int i : adj.get(n))
            if (!visited[i])
                dfs(board, adj, visited, i, next, ans);
        visited[n] = false;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        int len = word.length();
        if (len < 3) return 0;
        DictNode cur = root;
        for (int i = 0; i < len; ++i) {
            int id = (int) word.charAt(i) - 'A';
            if (cur.next[id] == null)
                return 0;
            cur = cur.next[id];
        }
        if (cur.word == null) return 0;
        if (len < 5) return 1;
        if (len < 6) return 2;
        if (len < 7) return 3;
        if (len < 8) return 5;
        return 11;
    }

    public static void main(String[] args) {
        In in = new In("dictionary-yawl.txt");
        BoggleSolver bs = new BoggleSolver(in.readAllStrings());
        BoggleBoard bb = new BoggleBoard("board-points26539.txt");
        StdOut.println(bb.toString());
        Iterable<String> words = bs.getAllValidWords(bb);
        int score = 0;
        for (String s : words) {
            StdOut.print(s + " ");
            score += bs.scoreOf(s);
        }
        StdOut.println();
        StdOut.println(score);
    }
}
