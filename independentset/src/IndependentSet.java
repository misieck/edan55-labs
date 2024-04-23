import java.util.Scanner;
import java.io.File;
import java.util.ArrayList;import java.util.Arrays;

class Graph {
    ArrayList<Node> g;
    int calls = 0;
    public Graph (Node[] n) {
        g = new ArrayList<Node>(Arrays.asList(n));
    }
    public void analytic() {
        for (Node n: g) {
            // System.out.println("" + n + " has neighbors: ");
            //for (Node e: n.connected) System.out.println(" - " + e );
            // System.out.println("total: " + n.neighbors);
        }
    }
    public int algR0() { return algR0(g); }
    public int algR0(ArrayList<Node> g) {
        calls++;
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
                // System.out.println(n + " died from loneliness, score is now: " + score);
            }
            else if (n.neighbors > maxn) {
                maxn = n.neighbors;
                bestn = n;
            }
        }

        if (bestn == null) {
            // System.out.println("Bottom detected");
            //for (Node l: lonersRemoved) l.add();
            return score;
        }
        else {
            // System.out.println(bestn + " has been deemed the best, he will face trial in accordance to our customs");
            bestn.remove();
            nextiter.remove(bestn);
            // System.out.println("-- FIRST TRIAL OF " + bestn + " --");
            int score1 = score + algR0(nextiter);
            bestn.removeAllNeighbors();
            nextiter.removeAll(bestn.connected);
            // System.out.println("-- SECOND TRIAL OF " + bestn + " --");
            int score2 = score + 1 + algR0(nextiter);
            bestn.add();
            bestn.addAllNeighbors();
            //for (Node l: lonersRemoved) l.add();
            // System.out.println(bestn + " can either leave for " + score1 + " or stay for " + score2);
            return Math.max(score1, score2);
        }
    }
    
    public int algR1() { return algR1(g); }
    public int algR1(ArrayList<Node> g)  {
        calls++;
        int maxn = 0;
        Node bestn = null;
        ArrayList<Node> nextiter = new ArrayList<>(g);
        ArrayList<Node> mono = new ArrayList<>();
        int score = 0;
        for (Node n: g) {
            if (n.neighbors == 1) {
                if (n.remove_count == 0) {
                    nextiter.remove(n);
                    nextiter.removeAll(n.connected);
                    n.removeAllNeighbors();
                    mono.add(n);
                    score++;
                    // System.out.println(n + " has become a the single ladies, score is now: " + score);
                }
            } else if (n.neighbors == 0) {
                //n.remove();
                //lonersRemoved.add(n);
                nextiter.remove(n);
                score++;
                // System.out.println(n + " died from loneliness, score is now: " + score);
            }
        }

        for (Node n : nextiter) {
            if(n.neighbors >= maxn) {
                maxn = n.neighbors;
                bestn = n;
            }
        }


        if (bestn == null) {
            // System.out.println("Bottom detected");
            //for (Node l: lonersRemoved) l.add();
            for (Node n: mono) {
                n.addAllNeighbors();
            }
            return score;
        }
        else {
            // System.out.println(bestn + " has been deemed the best, he will face trial in accordance to our customs");
            bestn.remove();
            nextiter.remove(bestn);
            // System.out.println("-- FIRST TRIAL OF " + bestn + " --");
            int score1 = score + algR1(nextiter);
            bestn.removeAllNeighbors();
            nextiter.removeAll(bestn.connected);
            // System.out.println("-- SECOND TRIAL OF " + bestn + " --");
            int score2 = score + 1 + algR1(nextiter);
            bestn.add();
            bestn.addAllNeighbors();
            //for (Node l: lonersRemoved) l.add();
            // System.out.println(bestn + " can either leave for " + score1 + " or stay for " + score2);
            for (Node n: mono) {
                n.addAllNeighbors();
            }
            return Math.max(score1, score2);
        }
    }

    public int algR2() { return algR2(g); }
    public int algR2(ArrayList<Node> g)  {
        calls++;
        int maxn = 0;
        Node bestn = null;
        ArrayList<Node> nextiter = new ArrayList<>(g);
        ArrayList<Node> mono = new ArrayList<>();
        ArrayList<Node> merge = new ArrayList<>();
        int score = 0;
        for (Node n: g) {
            if (n.neighbors == 2) {
                if (n.remove_count == 0) {
                    Node[] nbr = n.getAliveNeighbors();
                    score++;
                    if (nbr[0].connected.contains(nbr[1])) {
                        // System.out.println(n + " just had a threesome with " + nbr[0] + " and " + nbr[1] + ", score is now: " + score);
                        nextiter.remove(n);
                        nextiter.removeAll(n.connected);
                        n.removeAllNeighbors();
                        mono.add(n);
                    } else {
                        // System.out.println(n + " is in a love triangle with " + nbr[0] + " and " + nbr[1] + ", score is now: " + score);
                        n.remove();
                        Node merged = new Node(n.idx);
                        for (Node m: nbr[0].getAliveNeighbors()) merged.addNeighbor(m);
                        for (Node m: nbr[1].getAliveNeighbors()) merged.addNeighbor(m);
                        n.removeAllNeighbors();
                        nextiter.remove(n);
                        nextiter.removeAll(n.connected);
                        nextiter.add(merged);
                        n.add();
                        mono.add(n);
                        merge.add(merged);
                        // System.out.println(n + " just finished writing a successful teen romance novel about their experience with " + nbr[0] + " and " + nbr[1] + ", score is now: " + score);
                    }
                }
            } else if (n.neighbors == 1) {
                if (n.remove_count == 0) {
                    nextiter.remove(n);
                    nextiter.removeAll(n.connected);
                    n.removeAllNeighbors();
                    mono.add(n);
                    score++;
                    // System.out.println(n + " has become a the single ladies, score is now: " + score);
                }
            } else if (n.neighbors == 0) {
                if (n.remove_count == 0) {
                    nextiter.remove(n);
                    score++;
                    // System.out.println(n + " died from loneliness, score is now: " + score);
                }
            } 
        }

        for (Node n : nextiter) {
            if(n.neighbors >= maxn) {
                maxn = n.neighbors;
                bestn = n;
            }
        }


        if (bestn == null) {
            // System.out.println("Bottom detected");
            //for (Node l: lonersRemoved) l.add();
            for (Node n: mono) n.addAllNeighbors();
            for (Node n: merge) n.turboRemove();
            return score;
        }
        else {
            // System.out.println(bestn + " has been deemed the best, he will face trial in accordance to our customs");
            // System.out.print("this node thinks they have " + bestn.neighbors + " friends, namely: ");
            // for (Node n: bestn.getAliveNeighbors()) System.out.print(n + ",");
            // System.out.println();
            bestn.remove();
            nextiter.remove(bestn);
            // System.out.println("-- FIRST TRIAL OF " + bestn +  " --");
            // for (Node n: nextiter) System.out.print(n + ":" + n.remove_count + ",");
            // System.out.println();
            int score1 = score + algR2(nextiter);
            bestn.removeAllNeighbors();
            nextiter.removeAll(bestn.connected);
            // System.out.println("-- SECOND TRIAL OF " + bestn + " --");
            // for (Node n: nextiter) System.out.print(n + ":" + n.remove_count + ",");
            // System.out.println();
            int score2 = score + 1 + algR2(nextiter);
            bestn.add();
            bestn.addAllNeighbors();
            //for (Node l: lonersRemoved) l.add();
            // System.out.println(bestn + " can either leave for " + score1 + " or stay for " + score2);
            for (Node n: mono) n.addAllNeighbors();
            for (Node n: merge) n.turboRemove();
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
    public Node[] getAliveNeighbors() {
        int i = 0;
        Node[] nbr = new Node[neighbors];
        for (Node n: connected) {
            if (n.remove_count == 0) {
                nbr[i] = n;
                i++;
                if (i == neighbors) return nbr;
            }
        }
        Node[] nb = new Node[i];
        for (int j = 0; j < i; j++) {
            nb[j] = nbr[j];
        }
        return nb;
    }
    public void addNeighbor(Node n) {
        if (connected.contains(n)) return;
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
            // System.out.println("REMOVED " + this);
        }
    }
    public void add() {
        remove_count--;
        if (remove_count == 0) {
            for (Node n: connected) {
                n.neighbors++;
            }
            // System.out.println("ADDED " + this);
        }
    }
    public void turboRemove() {
        remove();
        for (Node n: connected) {
            n.connected.remove(this);
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
        File F = new File("data/g" + f + ".in");
        try {
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
            // g.analytic();
            System.out.println("Biggest independent cut: " + g.algR2());
            System.out.println("Recursive calls: " + g.calls);
        }
        catch (Exception e) {
            System.out.println("Failed bad:" + e);
        }
	}
}
