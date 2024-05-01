import java.util.Scanner;
import java.util.Set;
import java.io.File;
import java.util.ArrayList;import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

class Nodes {
    static boolean[][] connected;
    public static void setSize(int size) {
        connected = new boolean[size][size];
    }
    public static void connect(int x, int y) {
        connected[x][y] = true;
        connected[y][x] = true;
    }
    public static boolean isCon(int x, int y) {
        return connected[x][y];
    }
    public static boolean containsAny(int i,Set<Integer> S) {
        for (int j: S) {
            if (isCon(i,j)) return true;
        }
        return false;
    }
}

abstract class NiceTree {
    Map<Set<Integer>, Integer> cache = new HashMap<Set<Integer>,Integer>();
    public int c(Set<Integer> S) {
        if (cache.containsKey(S)) return cache.get(S);
        int res = c_impl(S);
        cache.put(S, res);
        return res;
    }
    public abstract int c_impl(Set<Integer> S);
}

class NiceLeaf extends NiceTree {
    public int c_impl(Set<Integer> S) {
        if (S.size() != 0) System.out.println("ERROR: S is not empty when it reached Leaf");
        return 0;
    }
}

class NiceIntroduce extends NiceTree {
    int introduced;
    NiceTree next;
    public void setIntroduced(int introduced) {
        this.introduced = introduced;
    }
    public void setNext(NiceTree next) {
        this.next = next;
    }
    public int c_impl(Set<Integer> S) {
        if (S.contains(introduced)) {
            Set<Integer> Sr = new HashSet<Integer>(S);
            Sr.remove(introduced);
            return next.c(Sr) + 1;
        }
        else {
            return next.c(S);
        }
    }
}

class NiceForget extends NiceTree {
    int removed;
    NiceTree next;
    public void setIntroduced(int removed) {
        this.removed = removed;
    }
    public void setNext(NiceTree next) {
        this.next = next;
    }
    public int c_impl(Set<Integer> S) {
        if (Nodes.containsAny(removed, S)) {
            return next.c(S);
        }
        Set<Integer> Sr = new HashSet<>(S);
        Sr.add(removed);
        int r1 = 1 + next.c(Sr);
        int r2 = next.c(S);
        return Math.max(r1, r2);
    }
}

class NiceJoin extends NiceTree {
    NiceTree left;
    NiceTree right;
    public void setLeft(NiceTree left) {
        this.left = left;
    }
    public void setRight(NiceTree right) {
        this.right = right;
    }
    public int c_impl(Set<Integer> S) {
        return left.c(S) + right.c(S) - S.size();
    }
}

class UglyTree {
    int idx;
    ArrayList<UglyTree> childs;
    ArrayList<Integer> nodes;
    public UglyTree(int i) {
        idx = i;
        childs = new ArrayList<UglyTree>();
        nodes = new ArrayList<Integer>();
    }
    public void addChild(UglyTree n) {
        childs.add(n);
    }
    public void addNode(int n) {
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
                    Nodes.setSize(n);;
                }
                else {
                    int n1 = Integer.parseInt(line[0]);
                    int n2 = Integer.parseInt(line[1]);
                    Nodes.connect(n1, n2);
                }
            }
            int b,w,v; // Bags, Width, vertexes
            Scanner st = new Scanner(G);
            UglyTree[] tree = null;
            while (sg.hasNextLine()) {
                String[] line = sg.nextLine().split(" ");
                if (line[0].equals("c")) continue;
                if (line[0].equals("s")) {
                    if (tree != null) throw(new Exception());
                    b = Integer.parseInt(line[2]);
                    w = Integer.parseInt(line[3]); // still not sure this is needed
                    v = Integer.parseInt(line[3]); // pretty sure this will always be = n so REALLY not needed
                    tree = new UglyTree[b];
                    for (int i = 0; i < b; i++) {
                        tree[i] = new UglyTree(i);
                    }
                }
                else if (line[0].equals("b")) {
                    int size = line.length;
                    int idx = Integer.parseInt(line[1]);
                    for (int i = 2; i < size; i++) {
                        tree[idx].addNode(i);
                    }
                }
                else {
                    int n1 = Integer.parseInt(line[0]);
                    int n2 = Integer.parseInt(line[1]);
                    tree[n1].addChild(tree[n2]);
                }
            }
        }
        catch (Exception e) {
            System.out.println("Failed bad:" + e);
        }
	}
}
