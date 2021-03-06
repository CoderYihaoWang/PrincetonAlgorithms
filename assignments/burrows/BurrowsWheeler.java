/* *****************************************************************************
 *  Name: BurrowsWheeler.java
 *  Date: 9/2/2020
 *  Description: Conduct the Burrows-Wheeler algorithms of data compressing
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {
    private static final int ASCII_LEN = 256;

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {
        String s = BinaryStdIn.readString();
        CircularSuffixArray csa = new CircularSuffixArray(s);
        int len = csa.length();
        for (int i = 0; i < len; ++i) {
            if (csa.index(i) == 0) {
                BinaryStdOut.write(i);
                break;
            }
        }
        for (int i = 0; i < len; ++i)
            BinaryStdOut.write(s.charAt((csa.index(i) + len - 1) % len));
        BinaryStdOut.flush();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        int first = BinaryStdIn.readInt();
        String t = BinaryStdIn.readString();
        int len = t.length();
        int[] next = new int[len];
        int[] s = new int[len];
        int[] radix = new int[ASCII_LEN + 1];
        for (int i = 0; i < len; ++i)
            radix[t.charAt(i) + 1] += 1;
        for (int i = 1; i < radix.length; ++i)
            radix[i] += radix[i - 1];
        for (int i = 0; i < len; ++i) {
            int c = radix[t.charAt(i)];
            next[c] = i;
            s[c] = t.charAt(i);
            ++radix[t.charAt(i)];
        }
        int n = 0;
        for (int i = first; n < len; i = next[i], ++n)
            BinaryStdOut.write((char) s[i]);
        BinaryStdOut.flush();
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args[0].equals("-"))
            transform();
        else if (args[0].equals("+"))
            inverseTransform();
    }
}
