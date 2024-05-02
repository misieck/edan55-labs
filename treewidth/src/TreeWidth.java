import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.stream.*;

public class TreeWidth {
    public static void checkAssert() throws RuntimeException {
        boolean assertsEnabled = false;
        assert assertsEnabled = true; // Intentional side effect!!!
        if (!assertsEnabled)
            throw new RuntimeException("Asserts must be enabled!!!");
    }
    public static Set<String> listFilesUsingJavaIO(String dir) {
        return Stream.of(new File(dir).listFiles())
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .collect(Collectors.toSet());
    }

    public static Set<String> listFilesUsingDirectoryStream(String dir) throws IOException {
        Set<String> fileSet = new HashSet<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(dir))) {
            for (Path path : stream) {
                if (!Files.isDirectory(path)) {
                    fileSet.add(path.getFileName()
                            .toString());
                }
            }
        }
        return fileSet;
    }

    public static Set<String> listFilesUsingFilesList(String dir) throws IOException {
        try (Stream<Path> stream = Files.list(Paths.get(dir))) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .filter(path -> path.endsWith(".td"))
                    .map(path-> path.substring(0, path.length() -3))
                    .collect(Collectors.toSet());
        }
    }

    public static void main(String args[]) throws IOException{
        if (args.length == 1 )  {
            String input = args[0]; //getInputName();
            System.out.print(input);
            long start = System.nanoTime();
            int res = executeOnData(input);
            long duration = System.nanoTime() - start;
            System.out.println(", "+res+", " + duration/1000000);
        }
        else {
            Set <String> names = listFilesUsingFilesList("data");

            for (String input: names) {
                System.out.print(input);
                long start = System.nanoTime();
                int res = executeOnData(input);
                long duration = System.nanoTime() - start;
                System.out.println(", "+res+", " + duration/1000000);
            }
        }

    }

    private static String getInputName() {
        Scanner input = new Scanner(System.in);
        String f = input.nextLine();
        input.close();
        return f;
    }

    private static int executeOnData(String f) throws FileNotFoundException {

        File G = new File("data/" + f + ".gr");
        File T = new File("data/" + f + ".td");

        readGraph(G);
        UglyTree[] tree = readUglyTree(T);
        //UglyTree zero = new UglyTree(-1);

        // Random random = new Random(32434);
           /* for (boolean[] bol: Nodes.connected) {
                System.out.println(Arrays.toString(bol));
            }
            */
        if (tree.length<2) return 1;
        UglyTree root = tree[0]; //random.nextInt(tree.length)];
        root.addChild(tree[1]);
        //zero.addChild(root);

        //root.printTree();
        NiceTree nice = root.niceify();
        //nice.printTree();
        return nice.c(new HashSet<>());
    }

    private static void readGraph(File G) throws FileNotFoundException {
        int n,e;
        Scanner sg = new Scanner(G);
        while (sg.hasNextLine()) {
            String[] line = sg.nextLine().split(" ");
            if (line[0].equals("c")) continue;
            if (line[0].equals("p")) {
                n = Integer.parseInt(line[2]);
                e = Integer.parseInt(line[3]); // don't think this is needed tbh
                Nodes.setSize(n);
            }
            else {
                int n1 = Integer.parseInt(line[0]);
                int n2 = Integer.parseInt(line[1]);
                Nodes.connect(n1, n2);
            }
        }
    }

    private static UglyTree[] readUglyTree(File T) throws FileNotFoundException {
        int b,w,v; // Bags, Width, vertexes
        Scanner st = new Scanner(T);
        UglyTree[] tree = null;
        while (st.hasNextLine()) {
            String[] line = st.nextLine().split(" ");
            //for (String str: line) System.out.print(str + " ");
            //System.out.println("");
            if (line[0].equals("c")) continue;
            if (line[0].equals("s")) {

                assert (tree == null) ;
                b = Integer.parseInt(line[2]);
                w = Integer.parseInt(line[3]); // still not sure this is needed
                v = Integer.parseInt(line[3]); // pretty sure this will always be = n so REALLY not needed
                tree = new UglyTree[b+1];
                for (int i = 0; i <= b; i++) {
                    tree[i] = new UglyTree(i);
                }
            }
            else if (line[0].equals("b")) {
                int size = line.length;
                int idx = Integer.parseInt(line[1]);
                for (int i = 2; i < size; i++) {
                    int toAdd = Integer.parseInt(line[i]);
                    tree[idx].addNode(toAdd);
                }
            }
            else {
                int n1 = Integer.parseInt(line[0]);
                int n2 = Integer.parseInt(line[1]);
                tree[n1].addChild(tree[n2]);
            }
        }
        return tree;
    }


}


class Nodes {
    static boolean[][] connected;
    public static void setSize(int size) {
        connected = new boolean[size+1][size+1];
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

abstract class Tree{

    abstract public Tree[] getChildren();
    boolean visited = false;

    public void printTree() {
        printTree(this, 0);
    }
    public static void printTree( Tree tree, int level){
        // level *= 3;
        String indent = new String(new char[level]).replace("\0", " ");
        System.out.println(indent + tree.toString());
        int chldCount = tree.getChildren().length;
        tree.visited = true;
        for (Tree child:tree.getChildren()){
            int newLevel = chldCount > 1 ? level +1: level;
            if (!child.visited) {
                printTree(child, newLevel);
            }
        }

    }
}

abstract class NiceTree extends Tree {
    Map<Set<Integer>, Integer> cache = new HashMap<Set<Integer>,Integer>();
    public int c(Set<Integer> S) {
        //System.out.println(S);
        if (cache.containsKey(S)) return cache.get(S);
        int res = c_impl(S);
        cache.put(S, res);
        //System.out.print(res+",");
        return res;
    }
    public abstract int c_impl(Set<Integer> S);

}

abstract class NiceChain extends NiceTree{
    NiceTree next;
    public void setNext(NiceTree next) {
        this.next = next;
    }

    public Tree[] getChildren() {
        NiceTree[] ret = {this.next};
        return ret;
    }
}

class NiceLeaf extends NiceTree {
    public int c_impl(Set<Integer> S) {
        //System.out.println("Leaf reached");
        if (S.size() != 0) System.out.println("ERROR: S is not empty when it reached Leaf");
        return 0;
    }

    public Tree[] getChildren() {
        NiceTree[] ret = {};
        return ret;
    }


    public String toString() {return "NiceLeaf" ;}
}

class NiceIntroduce extends NiceChain {
    Integer introduced;
    public NiceIntroduce(int introduced) {
        this.introduced = introduced;
    }
    public int c_impl(Set<Integer> S) {
        //System.out.print("Introduce node for " + introduced);
        if (S.contains(introduced)) {
            //System.out.println(", which was found in S");
            Set<Integer> Sr = new HashSet<Integer>(S);
            Sr.remove(introduced);
            return next.c(Sr) + 1;
        }
        else {
            //System.out.println(", which was not found in S");
            return next.c(S);
        }
    }

    public String toString() {return "NiceIntroduce: "+ introduced + ", ";}
}

class NiceForget extends NiceChain {
    Integer removed;
    public NiceForget(int removed) {
        this.removed = removed;
    }
    public int c_impl(Set<Integer> S) {
        //System.out.println("Forget node for " + removed);
        if (Nodes.containsAny(removed, S)) {
            //System.out.println("Not added to avoid the terrors");
            return next.c(S);
        }
        Set<Integer> Sr = new HashSet<>(S);
        Sr.add(removed);
        //System.out.println("--TESTING ADD FOR "+removed+"--");
        int r1 = next.c(Sr);
        //System.out.println("--TESTING NOT FOR "+removed+"--");
        int r2 = next.c(S);
           //     System.out.println(S+"+["+removed+"]="+r1+" or "+r2);
        return Math.max(r1, r2);
    }
    public String toString() {return "NiceForget: "+ removed + ", ";}
}

class NiceJoin extends NiceTree {
    ArrayList<NiceTree> nexts = new ArrayList<>();

    public void addNext(NiceTree next) {
        nexts.add(next);
    }

    public int c_impl(Set<Integer> S) {
        //System.out.println("BRANCH FOUND");
        int sum = 0;
        for (NiceTree n: nexts) {
            //System.out.println("Next branch!");
            sum += n.c(S);
        }
       // System.out.println("sum="+sum+",S="+S.toString()+"nexts="+nexts.size());
        return sum - (S.size() * (nexts.size()-1));
    }

    public Tree[] getChildren() {
        NiceTree[] ret = nexts.toArray(new NiceTree[0]);
        return ret;
    }

    public String toString(){return "NiceJoin: " + nexts;}
}

class UglyTree extends Tree{
    int idx;
    ArrayList<UglyTree> childs;
    Set<Integer> nodes;
    public UglyTree(int i) {
        idx = i;
        childs = new ArrayList<UglyTree>();
        nodes = new HashSet<Integer>();
    }

    public Tree[] getChildren() {
        UglyTree[] ret = childs.toArray(new UglyTree[0]);
        return ret;
    }

    public void addChild(UglyTree n) {
        childs.add(n);
        n.childs.add(this);
    }

    public void addNode(int n) {
        nodes.add(n);
    }
    public NiceTree niceify(UglyTree prev) {
        childs.remove(prev);
        return niceify();
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
            if (i == 0) return new NiceLeaf();
            chain[i-1].setNext(new NiceLeaf());
            return chain[0];
        } else if (childs.size() == 1) {
            UglyTree other = childs.get(0);
            return createChain(other);
        } else {
            NiceJoin join = new NiceJoin();
            for (UglyTree other: childs) {
                var chain = createChain(other);
                join.addNext(chain);
            }
            return join;
        }
    }

    private NiceTree createChain(UglyTree other) {
        Set<Integer> toForget = new HashSet<>(nodes);
        Set<Integer> toIntroduce = new HashSet<>(other.nodes);
        toForget.removeAll(other.nodes);
        toIntroduce.removeAll(nodes);
        NiceChain[] chain = new NiceChain[toIntroduce.size() + toForget.size()];
        int i = 0;
        for (int idx: toForget) {
            chain[i] = new NiceIntroduce(idx);
            i++;
        }
        for (int idx: toIntroduce) {
            chain[i] = new NiceForget(idx);
            i++;
        }
        for (int idx = 0; idx < i-1; idx++) {
            chain[idx].setNext(chain[idx+1]);
        }
        if (i == 0) return other.niceify(this);
        chain[i-1].setNext(other.niceify(this));
        return chain[0];
    }

    public String toString() {return "Ugly: "+idx + ", " + nodes;}
}

