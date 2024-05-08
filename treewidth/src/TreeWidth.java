import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.stream.*;
//import BitSet.MyBitSet;

public class TreeWidth {
    public static void checkAssert() throws RuntimeException {
        boolean assertsEnabled = false;
        assert assertsEnabled = true; // Intentional side effect!!!
        if (!assertsEnabled)
            throw new RuntimeException("Asserts must be enabled!!!");
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

    public static void run(String input) throws FileNotFoundException {
        System.out.print(input);
        long start = System.nanoTime();
        long[] res = executeOnData(input);
        long duration = System.nanoTime() - start;
        System.out.println(", n: " + res[3] + ", w: " + res[2] + ", a: " + res[0] + ", t: " + duration/1000000 + ", count: " + res[1]);

    }
        
    public static void main(String args[]) throws IOException{
        if (args.length == 1 )  {
            String input = args[0]; //getInputName();
            run(input);
        }
        else {
            Set <String> names = listFilesUsingFilesList("data");

            for (String input: names) {
                run(input);
            }
        }

    }

    private static String getInputName() {
        Scanner input = new Scanner(System.in);
        String f = input.nextLine();
        input.close();
        return f;
    }

    private static long[] executeOnData(String f) throws FileNotFoundException {

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
        if (tree.length<2) {
            long [] ret = {1, 0, -1, -1};
            return ret;
        }
        UglyTree root = tree[0]; //random.nextInt(tree.length)];
        root.addChild(tree[1]);
        //zero.addChild(root);

        root.printTree();
        int [] legend = new int[tree[0].width];
        Arrays.fill(legend, NiceTree.REMOVED_VAL);
        NiceTree.count = 0;
        NiceTree nice = root.niceify(legend);
        nice.printTree();
        //int res = nice.c(new HashSet<>());
        int res = nice.c_bit(0);
        long [] ret = {res, nice.count, tree[0].width, Nodes.getSize()};
        //int[] res = nice.c_all(new HashSet<>(), 0);
        //long [] ret = {res[1], nice.count, tree[0].width, Nodes.getSize()};
        return ret;
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
                v = Integer.parseInt(line[4]); // pretty sure this will always be = n so REALLY not needed
                tree = new UglyTree[b+1];
                for (int i = 0; i <= b; i++) {
                    tree[i] = new UglyTree(i,w);
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
    public static int getSize(){
        return connected.length;
    }

    public static void setSize(int size) {
        //nodes are 1-indexed
        connected = new boolean[size+1][size+1];
    }
    public static void connect(int x, int y) {
        connected[x][y] = true;
        connected[y][x] = true;
    }
    public static boolean isCon(int x, int y) {
        return connected[x][y];
    }

    public static boolean connectsAny(int from, Collection<Integer> S) {
        for (int to: S) {
            if (isCon(from,to)) return true;
        }
        return false;
    }

    public static boolean connectsAnyBit(int from, long S, int[] legend) {
        if (S == 0) return false;
        for (int i = 0; i<legend.length; i++) {
            if (MyBitSet.get(S, i)){
                if (isCon(from,legend[i])) return true;
            }
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
    public final static int REMOVED_VAL = 0;
    public static int findFirstOf(int[] numbers, int element){
        int index = -1;
        for(int i = 0; i < numbers.length; i++) {
            if(numbers[i] == element) {
                index = i;
                break;
            }
        }
        return index;
    }

    Map<Set<Integer>, Integer> cache = new HashMap<Set<Integer>,Integer>();
    Map<Long, Integer> cache_bit = new HashMap<Long,Integer>();
    int[] legend;
    public static long count = 0;
    //NiceTree(){ legend = new int[width];  }
    abstract public void setLegend(int[]l);

    public int c_bit(long S) {
        //System.out.println(S);
        if (cache_bit.containsKey(S)) return cache_bit.get(S);
        int res = c_bitimpl(S);
        cache_bit.put(S, res);
        //System.out.print(res+",");
        count++;
        return res;
    }

    public int[] c_all(Set<Integer> Ss, long Sb) {
        //System.out.println(S);

        int [] res = {-1,-1};
        if (cache_bit.containsKey(Sb)) res[0] = cache_bit.get(Sb);
        if (cache.containsKey(Ss)) res[1] = cache.get(Ss);
        if (res[0] != -1 || res[1] != -1) return res;

        res = c_allimpl(Ss, Sb);
        cache_bit.put(Sb, res[0]);
        cache.put(Ss, res[1]);

        //System.out.print(res+",");
        if (res[0] != res[1]){
            System.out.println("Res: " + this.toString() + ",bit: " + res[0] + ", " + res[1]);
        }
        count++;
        return res;
    }
    public abstract int[] c_allimpl(Set<Integer> Ss, long Sb);
    public abstract int c_bitimpl(long S);

}

abstract class NiceChain extends NiceTree{
    NiceTree next;

    public void setNext(NiceTree next) {
        this.next = next;
        next.setLegend(this.legend);
    }

    //NiceChain(int width){ super(width); }
    public Tree[] getChildren() {
        NiceTree[] ret = {this.next};
        return ret;
    }
}

class NiceLeaf extends NiceTree {
  
    public int c_bitimpl(long S) {
        //System.out.println("Leaf reached");
        assert (S == 0);
        return 0;
    }
    public int[] c_allimpl(Set<Integer> Ss, long Sb) {
        //System.out.println("Leaf reached");
        assert (Sb == 0);
        return new int[2];
    }

    public void setLegend(int[]l){
        int[] cmp = new int[l.length];
        Arrays.fill(cmp,REMOVED_VAL);
        assert (Arrays.equals(l, cmp));
    }

    public Tree[] getChildren() {
        NiceTree[] ret = {};
        return ret;
    }
    public String toString() {return " NiceLeaf";}
}

class NiceIntroduce extends NiceChain {
    Integer introduced;
    int introIdx;

    public NiceIntroduce(int introduced) {
        this.introduced = introduced;
    }

    public void setLegend(int[]l){
        int toRemove = findFirstOf(l, introduced);
        assert(toRemove >= 0);
        introIdx = toRemove;

        int [] lcpy = Arrays.copyOf(l,l.length);
        lcpy[toRemove] = REMOVED_VAL;
        legend = lcpy;
    };
    

    public int c_bitimpl(long S) {
        if (MyBitSet.get(S, introIdx)) {
            long Sr = MyBitSet.clear(S, introIdx);
            return next.c_bit(Sr) + 1;
        }
        else {
            return next.c_bit(S);
        }
    }

    public int[] c_allimpl(Set<Integer> Ss, long Sb) {
        int[] ret = new int[2];
        boolean bit_cond = MyBitSet.get(Sb, introIdx);
        boolean cond = Ss.contains(introduced);
        if (cond != bit_cond){
            System.out.println("Ss: "+ Ss + ", Sb: " + Sb + ", " + this.toString());
            assert(false);
        }
        if (bit_cond) {
            long Sbr = MyBitSet.clear(Sb, introIdx);
            Set<Integer> Ssr = new HashSet<Integer>(Ss);
            Ssr.remove(introduced);
            ret = next.c_all(Ssr, Sbr); ret[0] += 1; ret[1] += 1;
        }
        else {
            ret = next.c_all(Ss, Sb);
        }
        return ret;
    }

    public String toString() {return "NiceIntroduce_"+ introduced + "_legend_" + Arrays.toString(legend);}
}

class NiceForget extends NiceChain {
    Integer removed;
    int removedIdx;
    boolean connectsPossibly = true;

    public NiceForget(int removed, int width) {
        this.removed = removed;
        //only used for the very first node in the nice tree
        this.legend = new int[width];
        Arrays.fill(legend, REMOVED_VAL);
    }

    public void setLegend(int[]l){
        int empty = findFirstOf(l, REMOVED_VAL);
        removedIdx = empty;
        var ll = Arrays.stream( l ).boxed().toArray( Integer[]::new );
        int [] lcpy = Arrays.copyOf(l,l.length);
        lcpy[empty] = removed;
        legend = lcpy;
        try {
            Set<Integer> l_set = new HashSet<Integer>(Set.of(ll));
            connectsPossibly = Nodes.connectsAny(removed, l_set);
            connectsPossibly = true; //doesnt seem to matter - premature opt
        } catch (IllegalArgumentException e){}
    };

    public int c_bitimpl(long S) {
        if (connectsPossibly&& Nodes.connectsAnyBit(removed, S, legend)) {
            return next.c_bit(S);
        }

        long Sr = MyBitSet.set(S, removedIdx);
        int r1 = next.c_bit(Sr);
        int r2 = next.c_bit(S);
        next = null;
        return Math.max(r1, r2);
    }

    public int[] c_allimpl(Set<Integer> Ss, long Sb) {
        int[] ret = new int[2];
        boolean bit_cond = connectsPossibly && Nodes.connectsAnyBit(removed, Sb, legend);
        boolean cond = Nodes.connectsAny(removed, Ss);
        if (cond != bit_cond){
            System.out.println("Ss: "+ Ss + ", Sb: " + Sb + ", " + this.toString());
            assert(false);
        }
        if (cond) {
            return next.c_all(Ss, Sb);
        }
        Set<Integer> Ssr =  new HashSet<>(Ss);
        Ssr.add(removed);
        long Sbr = MyBitSet.set(Sb, removedIdx);

        var r1 = next.c_all(Ssr, Sbr);
        var r2 = next.c_all(Ss, Sb);
        if ( r1[0] != r1[1] ){
            System.out.println("Ss: "+ Ss + ", Sb: " + Sb + ", " + this.toString() + ", ("+Arrays.toString(r1)+")");
            assert(false);
        }
        if ( r2[0] != r2[1]){
            System.out.println("Ss: "+ Ss + ", Sb: " + Sb + ", " + this.toString() + ", ("+Arrays.toString(r2)+")");
            assert(false);
        }
        ret[1] = Math.max(r1[1], r2[1]);
        ret[0] = Math.max(r1[0], r2[0]);
        return ret;
    }


    public String toString() {return "NiceForget_"+ removed + "_legend_" + Arrays.toString(legend);}
}

class NiceJoin extends NiceTree {
    ArrayList<NiceTree> nexts = new ArrayList<>();

    public void addNext(NiceTree next) {
        nexts.add(next);
        next.setLegend(this.legend);
    }
    public void setLegend(int[]l){
        int [] lcpy = Arrays.copyOf(l,l.length);
        legend = lcpy;
    };

    @Override
    public int c_bitimpl(long S) {
        int sum = 0;
        for (NiceTree n: nexts) {
            sum += n.c_bit(S);
        }
        return sum - (Long.bitCount(S)* (nexts.size()-1));
    }
    
    public int[] c_allimpl(Set<Integer> Ss, long Sb) {
        //System.out.println("Forget node for " + removed);
        int[] ret = new int[2];
        //System.out.print("Introduce node for " + introduced);
        int[] sum = new int[2];
        for (NiceTree n: nexts) {
            //System.out.println("Next branch!");
            sum[0] += n.c_all(Ss, Sb)[0];
            sum[1] += n.c_all(Ss, Sb)[1];
        }
        ret [0] = sum[0] - (Long.bitCount(Sb)* (nexts.size()-1));
        ret [1] = sum[1] - (Ss.size() * (nexts.size()-1));
        if (Long.bitCount(Sb) != Ss.size()){
            System.out.println("sum="+sum+",Ss.size="+Ss.size()+", nexts="+nexts.size());
            System.out.println("sum="+sum+",S.bits="+Long.bitCount(Sb)+", nexts="+nexts.size());
            assert(false);
        }
        return ret;
    }

    public Tree[] getChildren() {
        NiceTree[] ret = nexts.toArray(new NiceTree[0]);
        return ret;
    }

    public String toString(){return "NiceJoin_" + "legend_" + Arrays.toString(legend) + "_nexts: "+ nexts;}
}

class UglyTree extends Tree{
    int idx;
    int width;
    ArrayList<UglyTree> childs;
    Set<Integer> nodes;
    public UglyTree(int i, int w) {
        idx = i;
        childs = new ArrayList<UglyTree>();
        nodes = new HashSet<Integer>();
        this.width = w;
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
    public NiceTree niceify(UglyTree prev, int[] legend) {
        childs.remove(prev);
        return niceify(legend);
    }

    public NiceTree niceify(int[]legend) {
        if (childs.size() == 0) {
            if (nodes.size() == 0) {
                NiceTree ret = new NiceLeaf();
                ret.setLegend(legend);
                return ret;
            }
            //this executes on the end - leaf branches
            NiceChain[] chain = new NiceChain[nodes.size()];
            int i = 0;
            for (int idx: nodes) {
                chain[i] = new NiceIntroduce(idx);
                i++;
            }
            chain[0].setLegend(legend);
            for (int idx = 0; idx < i-1; idx++) {
                chain[idx].setNext(chain[idx+1]);
            }

            chain[i-1].setNext(new NiceLeaf());
            return chain[0];

        } else if (childs.size() == 1) {
            UglyTree other = childs.get(0);
            return createChain(other, legend);
        } else {
            NiceJoin join = new NiceJoin();
            join.setLegend(legend);
            for (UglyTree other: childs) {
                NiceTree chain = createChain(other, legend);
                join.addNext(chain);
            }
            return join;
        }
    }

    private NiceTree createChain(UglyTree other, int [] _legend) {
        Set<Integer> toIntroduce = new HashSet<>(nodes);
        Set<Integer> toForget = new HashSet<>(other.nodes);
        toIntroduce.removeAll(other.nodes);
        toForget.removeAll(nodes);
        NiceChain[] chain = new NiceChain[toForget.size() + toIntroduce.size()];
        int [] legend = Arrays.copyOf(_legend, _legend.length);
        int i = 0;
        for (int idx: toIntroduce) {
            chain[i] = new NiceIntroduce(idx);
            i++;
        }

        for (int idx: toForget) {
            chain[i] = new NiceForget(idx, legend.length);
            i++;
        }

        chain[0].setLegend(legend);
        for (int idx = 0; idx < i-1; idx++) {
            chain[idx].setNext(chain[idx+1]);
        }

        if (i == 0) return other.niceify(this, chain[i-1].legend);
        var next = other.niceify(this, chain[i-1].legend);
        chain[i-1].setNext(next);

        return chain[0];
    }

    public String toString() {return "Ugly: "+idx + ", " + nodes;}
}

