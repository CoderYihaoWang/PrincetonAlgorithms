/* *****************************************************************************
 *  Name: WordNet.java
 *  Date: 1/2/2020
 *  Description: compute distances and shortest ancestor paths in a WordNet graph
 **************************************************************************** */

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashMap;
import java.util.HashSet;

public class WordNet {

    private final ST<String, Bag<Integer>> nouns = new ST<>();
    private final ST<Integer, String> syns = new ST<>();
    private final SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null)
            throw new IllegalArgumentException("The filenames cannot be null");

        In synsetsIn = new In(synsets);
        while (synsetsIn.hasNextLine()) {
            String[] line = synsetsIn.readLine().split(",");
            int id = Integer.parseInt(line[0]);
            String synset = line[1];
            syns.put(id, synset);
            for (String noun : synset.split(" ")) {
                if (!nouns.contains(noun))
                    nouns.put(noun, new Bag<>());
                nouns.get(noun).add(id);
            }
        }

        HashMap<Integer, Bag<Integer>> out = new HashMap<>();
        HashSet<Integer> in = new HashSet<>();

        In hypernymsIn = new In(hypernyms);
        while (hypernymsIn.hasNextLine()) {
            String[] line = hypernymsIn.readLine().split(",");
            int from = Integer.parseInt(line[0]);
            out.put(from, new Bag<>());
            Bag<Integer> cur = out.get(from);
            for (int i = 1; i < line.length; ++i) {
                int to = Integer.parseInt(line[i]);
                cur.add(to);
                in.add(to);
            }
        }

        int root = 0;
        for (int i : in)
            if (!out.containsKey(i))
                ++root;
        if (root != 0)
            throw new IllegalArgumentException("The hypernyms are not rooted");

        Digraph G = new Digraph(out.size() + 1);
        for (int from : out.keySet())
            for (int to : out.get(from))
                G.addEdge(from, to);

        if (new DirectedCycle(G).hasCycle())
            throw new IllegalArgumentException("The hypernyms contain cycle(s)");

        sap = new SAP(G);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return nouns;
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null)
            throw new IllegalArgumentException("The word argument cannot be null");
        return nouns.contains(word);
    }

    // distance between nounA and nounB
    public int distance(String nounA, String nounB) {
        if (!isNoun(nounA))
            throw new IllegalArgumentException("The word " + nounA + " is not in the synset");
        if (!isNoun(nounB))
            throw new IllegalArgumentException("The word " + nounB + " is not in the synset");
        return sap.length(nouns.get(nounA), nouns.get(nounB));
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path
    public String sap(String nounA, String nounB) {
        if (!isNoun(nounA))
            throw new IllegalArgumentException("The word " + nounA + " is not in the synset");
        if (!isNoun(nounB))
            throw new IllegalArgumentException("The word " + nounB + " is not in the synset");
        return syns.get(sap.ancestor(nouns.get(nounA), nouns.get(nounB)));
    }

    // do unit testing of this class
    public static void main(String[] args) {
        WordNet wn = new WordNet(args[0], args[1]);
        while (!StdIn.isEmpty()) {
            String nounA = StdIn.readString();
            String nounB = StdIn.readString();
            int distance = wn.distance(nounA, nounB);
            String sap = wn.sap(nounA, nounB);
            StdOut.println(
                    "A: " + nounA + "\tB: " + nounB + "\tDistance: " + distance + "\tSAP: "
                            + sap);
        }
    }
}
