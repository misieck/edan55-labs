import java.util.Scanner;
import java.util.TreeMap;
import java.util.Random;
import java.util.ArrayList;
import java.io.*;
public class FedUpsMarkov {
    public static void main(String args[]) {
        File g = new File("data/"+args[0]+".in");
        int n, m, h, f, p, i, u, v, t;
        double p1, p2;
        try (Scanner s = new Scanner(g)){
            n = s.nextInt();
            m = s.nextInt();
            h = s.nextInt();
            f = s.nextInt();
            p = s.nextInt();
            double[][] A = new double[n][n];
            double[] b = new double[n];

            for (i = 0; i < m; i += 1) {
                u = s.nextInt();
                v = s.nextInt();
                t = s.nextInt();
                p1 = Double.parseDouble(s.next());
                p2 = Double.parseDouble(s.next());
                A[u][v] = p1;
                A[v][u] = p2;
                b[u] += p1 * t;
                b[v] += p2 * t;
            }
            for (int x = 0; x < n; x++) {
                for (int y = 0; y < n; y++) System.out.print(A[x][y] + " ");
                System.out.println();
            }
            for (int x = 0; x < n; x++) System.out.println(b[x]);
        }
        catch (FileNotFoundException e) {
            System.out.println("Failed bad: " + e);
            return;
        }
	}
}