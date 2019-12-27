import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class Permutation {
    public static void main(String[] args) {
        int k = Integer.parseInt(args[0]);
        RandomizedQueue<String> rq = new RandomizedQueue<String>();
        for (int i = 0; i < k; ++i)
            rq.enqueue(StdIn.readString());
        while (!StdIn.isEmpty()) {
            if (!rq.isEmpty()) {
                rq.dequeue();
                rq.enqueue(StdIn.readString());
            }
        }
        for (int i = 0; i < k; ++i)
            StdOut.println(rq.dequeue());
    }
}
