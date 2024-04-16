import java.util.Scanner;import java.util.ArrayList;import java.util.Arrays;import java.io.*;
class Graph {
    ArrayList<Node> g;
    public Graph (Node[] n) {
        g = new ArrayList<Node>(Arrays.asList(n));
    }
    public int algR0() {
        int maxn = 0;
        Node bestn = null;
        for (Node n: g) {
            if (n.removed <= 0){
                if (n.neighbors == 0) {
                    n.remove();
                    int score = 1 + algR0();
                    n.add();
                    return score;
                }
                else if (n.neighbors > maxn) {
                    maxn = n.neighbors;
                    bestn = n;
                }
            }
        }
        if (bestn == null) return 0;
        else {
            bestn.remove();
            int score1 = algR0();
            bestn.removeAllNeighbors();
            int score2 = 1 + algR0();
            bestn.add();
            bestn.addAllNeighbors();
            return Math.max(score1, score2);
        }} 
}
class Node {
    int removed;
    int neighbors;
    ArrayList<Node> connected;
	public Node() {
        removed = 0;
        connected = new ArrayList<Node>();
        neighbors = 0;
    }
    public void addNeighbor(Node n) {
        connected.add(n);
        n.connected.add(this);
        n.neighbors++;
        neighbors++;
    }
    public void removeAllNeighbors() {
        for (Node n: connected) {
            n.remove();
        }}
    public void addAllNeighbors() {
        for (Node n: connected) {
            n.add();
        }}
    public void remove() {
        if (removed == 0) {
            for (Node n: connected) {
                n.neighbors--;
            }
        }
        removed++;
    }
    public void add() {
        removed--;
        if (removed == 0) {
            for (Node n: connected) {
                n.neighbors++;
            }}}}
public class IndependentSet {
    public static void main(String args[]) {
        File f = new File("data/g120.in"); // change this line to change the input data
        int	n, i, j, k;
        try (Scanner s = new Scanner(f)){
            n = s.nextInt();
            Node[] node = new Node[n];
            for (i = 0; i < n; i += 1)
                node[i] = new Node();
            for (i = 0; i < n; i += 1) {
                for (j = 0; j < n; j++) {
                    k = s.nextInt();
                    if (k == 1) {
                        node[i].addNeighbor(node[j]);
                    }}}
            Graph g = new Graph(node);
            System.out.println("Biggest independent cut: " + g.algR0());
        }
        catch (FileNotFoundException e) {
            System.out.println("Failed bad");
            return;
        }
	}
}
