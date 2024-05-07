import java.util.Scanner;
import java.util.TreeMap;
import java.util.Random;
import java.util.ArrayList;
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

        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) System.out.print(A[x][y] + " ");
            System.out.println();
        }
        for (int x = 0; x < n; x++) System.out.println(b[x]);

	}
}