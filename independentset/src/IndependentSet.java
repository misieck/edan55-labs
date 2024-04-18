import java.util.Scanner;
import java.io.File;
import java.util.ArrayList;import java.util.Arrays;

class Graph {
    ArrayList<Node> g;
    public Graph (Node[] n) {
        g = new ArrayList<Node>(Arrays.asList(n));
    }
    public int algR0() { return algR0(g); }
    public int algR0(ArrayList<Node> g) {
        int maxn = 0;
        Node bestn = null;
        ArrayList<Node> nextiter = new ArrayList<>(g);
        int score = 0;
        for (Node n: g) {
            if (n.neighbors == 0) {
                //n.remove();
                //lonersRemoved.add(n);
                nextiter.remove(n);
                score++;
                // system.out.println(n + " died from loneliness, score is now: " + score);
            }
            else if (n.neighbors > maxn) {
                maxn = n.neighbors;
                bestn = n;
            }
        }
        if (bestn == null) {
            // system.out.println("Bottom detected");
            //for (Node l: lonersRemoved) l.add();
            return score;
        }
        else {
            // system.out.println(bestn + " has been deemed the best, he will face trial in accordance to our customs");
            bestn.remove();
            nextiter.remove(bestn);
            // system.out.println("-- FIRST TRIAL OF " + bestn + " --");
            int score1 = score + algR0(nextiter);
            bestn.removeAllNeighbors();
            nextiter.removeAll(bestn.connected);
            // system.out.println("-- SECOND TRIAL OF " + bestn + " --");
            int score2 = score + 1 + algR0(nextiter);
            bestn.add();
            bestn.addAllNeighbors();
            //for (Node l: lonersRemoved) l.add();
            // system.out.println(bestn + " can either leave for " + score1 + " or stay for " + score2);
            return Math.max(score1, score2);
        }
    }
    
    public int algR1() { return algR1(g); }
    public int algR1(ArrayList<Node> g) {
        int maxn = 0;
        Node bestn = null;
        ArrayList<Node> nextiter = new ArrayList<>(g);
        ArrayList<Node> mono = new ArrayList<>();
        int score = 0;
        for (Node n: g) {
            if (n.neighbors == 1) {
                nextiter.remove(n);
                nextiter.removeAll(n.connected);
                n.remove();
                n.removeAllNeighbors();
            }
            else if (n.neighbors == 0) {
                //n.remove();
                //lonersRemoved.add(n);
                nextiter.remove(n);
                score++;
                // system.out.println(n + " died from loneliness, score is now: " + score);
            }
            if (n.neighbors > maxn) {
                maxn = n.neighbors;
                bestn = n;
            }
        }
        if (bestn == null) {
            // system.out.println("Bottom detected");
            //for (Node l: lonersRemoved) l.add();
            return score;
        }
        else if (bestn.remove_count != 0) {
            score += algR1(nextiter);
            for (Node n: mono) {
                n.add();
                n.addAllNeighbors();
            }
            return score;
        }
        else {
            // system.out.println(bestn + " has been deemed the best, he will face trial in accordance to our customs");
            bestn.remove();
            nextiter.remove(bestn);
            // system.out.println("-- FIRST TRIAL OF " + bestn + " --");
            int score1 = score + algR1(nextiter);
            bestn.removeAllNeighbors();
            nextiter.removeAll(bestn.connected);
            // system.out.println("-- SECOND TRIAL OF " + bestn + " --");
            int score2 = score + 1 + algR1(nextiter);
            bestn.add();
            bestn.addAllNeighbors();
            //for (Node l: lonersRemoved) l.add();
            // system.out.println(bestn + " can either leave for " + score1 + " or stay for " + score2);
            return Math.max(score1, score2);
        }
    }
}

class Node {
    int idx;
    int remove_count;
    int neighbors;
    ArrayList<Node> connected;
	public Node(int i) {
        idx = i;
        remove_count = 0;
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
        remove_count++;
        if (remove_count == 1) {
            for (Node n: connected) {
                n.neighbors--;
            }
            // system.out.println("REMOVED " + this);
        }
    }
    public void add() {
        remove_count--;
        if (remove_count == 0) {
            for (Node n: connected) {
                n.neighbors++;
            }
            // system.out.println("ADDED " + this);
        }
    }
    public String toString() {return "Node"+idx;}
}

public class IndependentSet {
    public static void main(String args[]) {
        //File f = new File("data/g120.in"); // change this line to change the input data
        //try (Scanner s = new Scanner(System.in)){
        Scanner input = new Scanner(System.in);
        String f = input.nextLine();
        input.close();
        try {
            File F = new File("data/g" + f + ".in");
            int	n, i, j, k;
            Scanner s = new Scanner(F);
            n = s.nextInt();
            Node[] node = new Node[n];
            for (i = 0; i < n; i += 1)
                node[i] = new Node(i);
            for (i = 0; i < n; i += 1) {
                for (j = 0; j < n; j++) {
                    k = s.nextInt();
                    if (k == 1) {
                        node[i].addNeighbor(node[j]);
                    }}}
            s.close();
            Graph g = new Graph(node);
            System.out.println("Biggest independent cut: " + g.algR1());
        }
        catch (Exception e) {
            System.out.println("Failed bad:" + e);
        }
	}
}
