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

abstract class NiceChain extends NiceTree{
    NiceTree next;
    public void setNext(NiceTree next) {
        this.next = next;
    }
}

class NiceLeaf extends NiceTree {
    public int c_impl(Set<Integer> S) {
        if (S.size() != 0) System.out.println("ERROR: S is not empty when it reached Leaf");
        return 0;
    }
}

class NiceIntroduce extends NiceChain {
    int introduced;
    public NiceIntroduce(int introduced) {
        this.introduced = introduced;
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

class NiceForget extends NiceChain {
    int removed;
    public NiceForget(int removed) {
        this.removed = removed;
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
    ArrayList<NiceTree> nexts = new ArrayList<>();
    public void addNext(NiceTree next) {
        nexts.add(next);
    }
    public int c_impl(Set<Integer> S) {
        int sum = 0;
        for (NiceTree n: nexts) sum += n.c(S);
        return sum - (S.size() * (nexts.size()-1));
    }
}

class UglyTree {
    int idx;
    ArrayList<UglyTree> childs;
    HashSet<Integer> nodes;
    public UglyTree(int i) {
        idx = i;
        childs = new ArrayList<UglyTree>();
        nodes = new HashSet<Integer>();
    }
    public void addChild(UglyTree n) {
        childs.add(n);
    }
    public void addNode(int n) {
        nodes.add(n);
    }
    public NiceTree niceify() {
        if (childs.size() == 0) {
            NiceChain[] chain = new NiceChain[nodes.size()];
            int i = 0;
            for (int idx: nodes) {
                chain[i] = new NiceIntroduce(idx);
                i++;
            }
            for (int idx = 0; idx < i-1; idx++) {
                chain[idx].setNext(chain[idx+1]);
            }
            chain[i-1].setNext(new NiceLeaf());
            return chain[0];
        } else if (childs.size() == 1) {
            UglyTree other = childs.get(0);
            Set<Integer> f = new HashSet<>(nodes);
            Set<Integer> a = new HashSet<>(other.nodes);
            f.removeAll(a);
            a.removeAll(nodes);
            NiceChain[] chain = new NiceChain[f.size() + a.size()];
            int i = 0;
            for (int idx: f) {
                chain[i] = new NiceForget(idx);
                i++;
            }
            for (int idx: a) {
                chain[i] = new NiceIntroduce(idx);
                i++;
            }
            for (int idx = 0; idx < i-1; idx++) {
                chain[idx].setNext(chain[idx+1]);
            }
            chain[i-1].setNext(other.niceify());
            return chain[0];
        } else {
            NiceJoin join = new NiceJoin();
            for (UglyTree c: childs) {
                join.addNext(c.niceify());
            }
            return join;
        }
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
