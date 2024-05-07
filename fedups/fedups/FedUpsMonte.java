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
public class FedUpsMonte {
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
            node[h].scanGraph();
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
