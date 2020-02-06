
/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.HashSet;

public class BoggleSolver {

    private class DictNode {
        private String word;
        private DictNode[] next = new DictNode[26];
    }

    private DictNode root = new DictNode();

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
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
        ArrayList<Integer>[] adj = (ArrayList<Integer>[]) new ArrayList[len];
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
            adj[i] = al;
        }
        for (int i = 0; i < len; ++i)
            dfs(bd, adj, new boolean[len], i, root, ans);
        return ans;
    }

    private void dfs(int[] board, ArrayList<Integer>[] adj, boolean[] visited, int n, DictNode node,
                     HashSet<String> ans) {
        if (node == null)
            return;
        String word = node.word;
        if (word != null && word.length() >= 3)
            ans.add(word);
        int letter = board[n];
        if (letter == 'Q' - 'A') {
            node = node.next[letter];
            letter = 'U' - 'A';
            if (node == null)
                return;
        }
        visited[n] = true;
        for (int i : adj[n]) {
            if (!visited[i]) {
                dfs(board, adj, visited, i, node.next[letter], ans);
            }
        }
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
        BoggleBoard bb = new BoggleBoard("board-q.txt");
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
