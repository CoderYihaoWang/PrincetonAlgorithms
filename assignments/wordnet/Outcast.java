/* *****************************************************************************
 *  Name: Outcast.java
 *  Date: 1/2/2020
 *  Description: computes the outcast from a group of words
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    private final WordNet wordnet;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        this.wordnet = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        int len = nouns.length;
        int[][] distances = new int[len][len];
        for (int i = 0; i < len; ++i) {
            distances[i][i] = 0;
            for (int j = i + 1; j < len; ++j) {
                distances[i][j] = wordnet.distance(nouns[i], nouns[j]);
                distances[j][i] = distances[i][j];
            }
        }
        int maxId = -1;
        int maxDistance = -1;
        for (int i = 0; i < len; ++i) {
            int distance = 0;
            for (int j = 0; j < len; ++j)
                distance += distances[i][j];
            if (distance > maxDistance) {
                maxId = i;
                maxDistance = distance;
            }
        }
        return nouns[maxId];
    }

    // The following test client takes from the command line
    // the name of a synset file, the name of a hypernym file,
    // followed by the names of outcast files,
    // and prints out an outcast in each file
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
