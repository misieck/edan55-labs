
import java.io.*;
public class FedUpsMarkov {
    public static void main(String args[]) throws IOException {
        DeliveryGraph G = new DeliveryGraph(args[0]);
        double[][]A = G.A;
        double[] b = G.b;
        int f = G.fed;
        int h = G.home;
        int p = G.post;
        int n = b.length;

        for (int x = 0; x < n; x++) A[x][x] -= 1;
        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) System.out.print(A[x][y] + " ");
            System.out.println();
        }
        for (int x = 0; x < n; x++) System.out.println(b[x]);
        double[] sol = Gaussian.solve(A, b);

        System.out.println("FedUps time: " + sol[f]);
        System.out.println("PostNHL time: " + sol[p]);
        
        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) System.out.print(A[x][y] + " ");
            System.out.println();
        }
        for (int x = 0; x < n; x++) System.out.println(b[x]);

	}
}