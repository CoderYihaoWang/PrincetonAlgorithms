/* *****************************************************************************
 *  Name: MoveToFront.java
 *  Date: 9/2/2020
 *  Description: Conduct the move to front encoding and decoding
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        int[] radix = new int[256];
        for (int i = 0; i < 256; ++i)
            radix[i] = i;
        while (!BinaryStdIn.isEmpty()) {
            int c = BinaryStdIn.readChar();
            int i = 0;
            while (c != radix[i])
                ++i;
            BinaryStdOut.write((char) i);
            for (; i > 0; --i)
                radix[i] = radix[i - 1];
            radix[0] = c;
        }
        BinaryStdOut.flush();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        int[] radix = new int[256];
        for (int i = 0; i < 256; ++i)
            radix[i] = i;
        while (!BinaryStdIn.isEmpty()) {
            int i = BinaryStdIn.readChar();
            BinaryStdOut.write((char) radix[i]);
            int c = radix[i];
            for (; i > 0; --i)
                radix[i] = radix[i - 1];
            radix[0] = c;
        }
        BinaryStdOut.flush();
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        if (args[0].equals("-"))
            encode();
        else if (args[0].equals("+"))
            decode();
    }

}
