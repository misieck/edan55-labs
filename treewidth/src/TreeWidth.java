import java.util.Scanner;
import java.io.File;
import java.util.ArrayList;import java.util.Arrays;

class Bag {
    int idx;
    ArrayList<Bag> neighbors;
    ArrayList<Node> nodes;
    public Bag(int i) {
        idx = i;
        neighbors = new ArrayList<Bag>();
        nodes = new ArrayList<Node>();
    }
    public void addNeighbor(Bag n) {
        neighbors.add(n);
        n.neighbors.add(n);
    }
    public void addNode(Node n) {
        nodes.add(n);
    }
    public String toString() {return "Bag"+idx;}
}

class Node {
    int idx;
    ArrayList<Node> connected;
	public Node(int i) {
        idx = i;
        connected = new ArrayList<Node>();
    }
    public void addNeighbor(Node n) {
        if (connected.contains(n)) return;
        connected.add(n);
        n.connected.add(this);
    }
    public String toString() {return "Node"+idx;}
}

public class TreeWidth {
    public static void main(String args[]) {
        Scanner input = new Scanner(System.in);
        String f = input.nextLine();
        input.close();
        File G = new File("data/" + f + ".gr");
        File T = new File("data/" + f + ".gr");
        try {
            int n,e;
            Scanner sg = new Scanner(G);
            Node[] node = null;
            while (sg.hasNextLine()) {
                String[] line = sg.nextLine().split(" ");
                if (line[0].equals("c")) continue;
                if (line[0].equals("p")) {
                    if (node != null) throw(new Exception());
                    n = Integer.parseInt(line[2]);
                    e = Integer.parseInt(line[3]); // don't think this is needed tbh
                    node = new Node[n];
                    for (int i = 0; i < n; i++) {
                        node[i] = new Node(i);
                    }
                }
                else {
                    int n1 = Integer.parseInt(line[0]);
                    int n2 = Integer.parseInt(line[1]);
                    node[n1].addNeighbor(node[n2]);
                }
            }
            int b,w,v; // Bags, Width, vertexes
            Scanner st = new Scanner(G);
            Bag[] bag = null;
            while (sg.hasNextLine()) {
                String[] line = sg.nextLine().split(" ");
                if (line[0].equals("c")) continue;
                if (line[0].equals("s")) {
                    if (bag != null) throw(new Exception());
                    b = Integer.parseInt(line[2]);
                    w = Integer.parseInt(line[3]); // still not sure this is needed
                    v = Integer.parseInt(line[3]); // pretty sure this will always be = n so REALLY not needed
                    bag = new Bag[b];
                    for (int i = 0; i < b; i++) {
                        bag[i] = new Bag(i);
                    }
                }
                else if (line[0].equals("b")) {
                    int size = line.length;
                    int idx = Integer.parseInt(line[1]);
                    for (int i = 2; i < size; i++) {
                        bag[idx].addNode(node[i]);
                    }
                }
                else {
                    int n1 = Integer.parseInt(line[0]);
                    int n2 = Integer.parseInt(line[1]);
                    node[n1].addNeighbor(node[n2]);
                }
            }
        }
        catch (Exception e) {
            System.out.println("Failed bad:" + e);
        }
	}
}
