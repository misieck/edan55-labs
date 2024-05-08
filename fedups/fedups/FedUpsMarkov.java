
import java.io.*;
import java.util.HashSet;
public class FedUpsMarkov {
    public static void main(String args[]) throws IOException {
        DeliveryGraph G = new DeliveryGraph(args[0]);
        double[][]A = G.A;
        double[] b = G.b;
        int f = G.fed;
        int h = G.home;
        int p = G.post;
        int n = b.length;

        HashSet<Integer> reachable = findReachable(A, h);
        System.out.println(reachable);
        for (int x = 0; x < n; x++) {
            if (!reachable.contains(x)) {
                for (int y = 0; y < n; y++) A[x][y] = 0.0;
            }
        }

        for (int x = 0; x < n; x++) A[x][x] -= 1;
        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) System.out.print(A[x][y] + " ");
            System.out.println();
        }
        for (int x = 0; x < n; x++) System.out.println(b[x]);
        double[] sol = Gaussian.solve(A, b);

        if (reachable.contains(f)) System.out.println("FedUps time: " + sol[f]);
        else System.out.println("FedUps time: Unreachable");
        if (reachable.contains(p)) System.out.println("PostNHL time: " + sol[p]);
        else System.out.println("PostNHL time: Unreachable");

	}

    public static HashSet<Integer> findReachable(double[][] A, int added) {
        return findReachable(A, new HashSet<>(),added);
    }

    public static HashSet<Integer> findReachable(double[][] A, HashSet<Integer> reachable, int added) {
        if (reachable.contains(added)) return reachable;
        reachable.add(added);
        for (int x = 0; x < A.length; x++) {
            if (A[x][added] != 0.0) findReachable(A, reachable, x);
        }
        return reachable;
    }
}