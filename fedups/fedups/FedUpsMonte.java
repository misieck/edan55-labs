import java.util.Scanner;
import java.util.TreeMap;
import java.util.Random;
import java.util.ArrayList;
import java.io.*;
class Node {
    static Random r = new Random();
    TreeMap<Double,Edge> edges;
    ArrayList<Edge> edgeList;
    double acumProb = 0.0;
    boolean accessible = false;
    int id;
	Node(int i)
	{
        edgeList = new ArrayList<>();
		edges = new TreeMap<Double,Edge>();
        id = i;
	}
    void addRoad(Edge e, double p) {
        edgeList.add(e);
        if (p == 0) return;
        acumProb += p;
        edges.put(acumProb, e);
    }
    Edge getRandomEdge() {
        double d = r.nextDouble();
        return edges.higherEntry(d).getValue();
    }
    void scanGraph() {
        if (accessible) return;
        accessible = true;
        for (Edge e: edgeList) {
            if (e.accessible(this)) {
                e.getOther(this).scanGraph();
            }
        }
    }
}
class Edge {
	Node	u,v;
    boolean uac,vac;
    int     t;
	Edge(Node u, Node v, int t, double p1, double p2)
	{
		this.u = u;
		this.v = v;
        this.t = t;
        u.addRoad(this, p1);
        v.addRoad(this, p2);
        uac = p1 > 0.0;
        vac = p2 > 0.0;
	}
    Node getOther(Node o) {
        if (o == u) return v;
        else if (o == v) return u;
        return null;
    }
    boolean accessible(Node o) {
        if (o == u) return vac;
        else if (o == v) return uac;
        return false;
    }
}
class Car {
    int acumTime = 0;
    Node start, current, target;
    Car(Node start, Node target) {
        this.start = start;
        this.current = start;
        this.target = target;
    }
    boolean drive() {
        Edge e = current.getRandomEdge();
        acumTime += e.t;
        current = e.getOther(current);
        return (current == target);
    }
    int deliver() {
        boolean arrived = false;
        while (!arrived) {
            arrived = drive();
        }
        return acumTime;
    }
    void reset() {
        acumTime = 0;
        current = start;
    }
}

class DeliveryGraph {
    Node[] nodes;
    Edge[] edges;
    int home;
    int fed;
    int post;
    double[][] A;
    double[] b;

    public DeliveryGraph(String name) throws IOException{
        File g = new File("data/"+name+".in");
        int n, m, i, u, v, t;
        double p1, p2;
        try (Scanner s = new Scanner(g)) {
            n = s.nextInt();
            m = s.nextInt();
            home = s.nextInt();
            fed = s.nextInt();
            post = s.nextInt();
            nodes = new Node[n];
            edges = new Edge[m];
            A = new double[n][n];
            b = new double[n];

            for (i = 0; i < n; i += 1)
                nodes[i] = new Node(i);

            for (i = 0; i < m; i += 1) {
                u = s.nextInt();
                v = s.nextInt();
                t = s.nextInt();
                p1 = Double.parseDouble(s.next());
                p2 = Double.parseDouble(s.next());
                edges[i] = new Edge(nodes[u], nodes[v], t, p1, p2);
                A[u][v] = p1;
                A[v][u] = p2;
                b[u] -= p1 * t;
                b[v] -= p2 * t;
            }
        }
    }
}

public class FedUpsMonte {
    public static void main(String args[]) throws IOException {

        DeliveryGraph G = new DeliveryGraph(args[0]);
        Node[]node = G.nodes;
        int f = G.fed;
        int h = G.home;
        int p = G.post;
        double ftot = 0,ptot = 0;
        Car cf = new Car(node[f], node[h]);
        Car cp = new Car(node[p], node[h]);
        node[h].scanGraph();
        if (node[f].accessible) {
            for (int i = 0; i < 10000; i++) {
                ftot += cf.deliver();
                cf.reset();
            }
            System.out.println("Est. time FedUps = " + ftot / 10000);
        } else {
            System.out.println("FedUps: We tried to deliver your package, but you were not at home");
        }
        if (node[p].accessible) {
            for (int i = 0; i < 10000; i++) {
                ptot += cp.deliver();
                cp.reset();
            }
            System.out.println("Est. time PostNHL = " + ptot / 10000);
        } else {
            System.out.println("PostNHL: We tried to deliver your package, but you were not at home");
        }

	}
}
