/* *****************************************************************************
 *  Name: CircularSuffixArray.java
 *  Date: 9/2/2020
 *  Description: Construct a circular suffix array of a given string
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class CircularSuffixArray {
    private final int[] index;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null)
            throw new IllegalArgumentException("The String to process cannot be null");
        index = new int[s.length()];
        for (int i = 0; i < index.length; ++i)
            index[i] = i;
        StdRandom.shuffle(index);
        sort(s, index, 0, index.length - 1, 0);
    }

    private void sort(String s, int[] array, int lo, int hi, int d) {
        if (lo >= hi)
            return;
        int lt = lo;
        int gt = hi;
        int cur = lo + 1;
        int pivot = s.charAt((array[lo] + d) % s.length());
        while (cur <= gt) {
            int cond = s.charAt((array[cur] + d) % s.length()) - pivot;
            if (cond > 0) {
                int t = array[cur];
                array[cur] = array[gt];
                array[gt] = t;
                --gt;
            }
            else if (cond < 0) {
                int t = array[cur];
                array[cur] = array[lt];
                array[lt] = t;
                ++lt;
                ++cur;
            }
            else
                ++cur;
        }
        sort(s, array, lo, lt - 1, d);
        if (d < s.length())
            sort(s, array, lt, gt, d + 1);
        sort(s, array, gt + 1, hi, d);
    }

    // length of s
    public int length() {
        return index.length;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i >= index.length || i < 0)
            throw new IllegalArgumentException(
                    "The index " + i + " is out of bounds: 0-" + (index.length - 1));
        return index[i];
    }

    // unit testing (required)
    public static void main(String[] args) {
        String s = args[0];
        CircularSuffixArray csa = new CircularSuffixArray(s);
        for (int i = 0; i < csa.length(); ++i)
            StdOut.println(csa.index(i));
    }
}
