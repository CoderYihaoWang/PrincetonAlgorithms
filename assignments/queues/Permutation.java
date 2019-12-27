import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class Permutation {
    public static void main(String[] args) {
        int k = Integer.parseInt(args[0]);
        RandomizedQueue<String> rq = new RandomizedQueue<String>();
        int i = 0;
        for (; i < k; ++i)
            rq.enqueue(StdIn.readString());
        // reservoir sampling
        while (!StdIn.isEmpty()) {
            ++i;
            int other = StdRandom.uniform(i);
            String str = StdIn.readString();
            if (other < k) {
                rq.dequeue();
                rq.enqueue(str);
            }
        }
        for (int j = 0; j < k; ++j)
            StdOut.println(rq.dequeue());
    }
}
