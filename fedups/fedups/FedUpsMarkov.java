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
            Node[] node = new Node[n];
            Edge[] edge = new Edge[m];

            for (i = 0; i < n; i += 1)
                node[i] = new Node(i);

            for (i = 0; i < m; i += 1) {
                u = s.nextInt();
                v = s.nextInt();
                t = s.nextInt();
                p1 = Double.parseDouble(s.next());
                p2 = Double.parseDouble(s.next());
                edge[i] = new Edge(node[u], node[v], t, p1, p2);
            }
            double ftot = 0,ptot = 0;
            Car cf = new Car(node[f], node[h]);
            Car cp = new Car(node[p], node[h]);
            node[h].verify();
            if (node[f].accessible) {
                for (i = 0; i < 10000; i++) {
                    ftot += cf.deliver();
                    cf.reset();
                }
                System.out.println("Est. time FedUps = " + ftot / 10000);
            } else {
                System.out.println("FedUps: We tried to deliver your package, but you were not at home");
            }
            if (node[p].accessible) {
                for (i = 0; i < 10000; i++) {
                    ptot += cp.deliver();
                    cp.reset();
                }
                System.out.println("Est. time PostNHL = " + ptot / 10000);
            } else {
                System.out.println("PostNHL: We tried to deliver your package, but you were not at home");
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("Failed bad: " + e);
            return;
        }
	}
}