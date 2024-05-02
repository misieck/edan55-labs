
import java.util.HashSet;
import java.util.Set;


public class BitSet {
    public static long myOperation (long bitset, int idx, long max){
        if (idx >= max)
            return bitset;
        if (bitset == -1){
            bitset = 0;
        }

        long bitset2 = MyBitSet.set(bitset, idx%64);
        return 1 + myOperation(bitset, idx+1, max) + myOperation(bitset2, idx+2, max/2);
    }

    public static long jOperation (java.util.BitSet jset, int idx, long max){
        if (idx >= max) {
            long[] a = jset.toLongArray();
            return a.length == 0? 0: a[0];
        }
        if (jset.cardinality() == 64 ){
            jset.clear();
        }
        var cpy = jset.clone();
        jset.set(idx%64);
        return 1 + jOperation( (java.util.BitSet) cpy, idx+1, max ) + jOperation(jset, idx+2, max/2);
    }

    public static long setOperation (Set<Integer> set, int idx, long max){
        if (idx >= max) {
            return 10; //set.hashCode();
        }
        if (set.size() == 64 ){
            set.clear();
        }
        var cpy = new HashSet<Integer>(set);
        set.add(idx%64);
        return 1 + setOperation( cpy, idx+1, max ) + setOperation(set, idx+2, max/2);
    }



    public static void main (String[] argv){

        long start, duration;
        long iters = 10_000_040L;

        long bitset = 0x1L;//1
        long res = MyBitSet.set(bitset, 2); //+ 4
        System.out.println(String.format("0x%08X", res)); //5
        res = MyBitSet.set(res, 63);
        System.out.println(String.format("0x%08X",res));
        res = MyBitSet.set(res, 1); //+ 2
        System.out.println(String.format("0x%08X",res));
        res = MyBitSet.set(res, 3); //+ 8
        System.out.println(String.format("0x%08X",res));

        System.out.println(String.format("res: %b",MyBitSet.get(res, 3)));
        System.out.println(String.format("res: %b",MyBitSet.get(res, 10)));

        res = MyBitSet.clear(res, 3); //- 8
        System.out.println(String.format("0x%08X",res));
        res = MyBitSet.clear(res, 63); //- 8
        System.out.println(String.format("0x%08X",res));



        duration = 0;
        bitset = 0x0;
        for (long i = 0; i<iters; i++){
            long index = i % 64;
            if (bitset == - 1 ){
                bitset = 0;
            }
            start = System.nanoTime();
            if (!MyBitSet.get(bitset, index)){
                bitset = MyBitSet.set(bitset, index);
            }
            duration = duration + System.nanoTime() - start;
        }
        System.out.println("Test My: "+ duration/100.0/iters + ", res: " + bitset);


        duration = 0;
        java.util.BitSet jset = new java.util.BitSet(64);
        Set<Integer> set = new HashSet<>(64);
        for (long i = 0; i<iters; i++){
            long index = i % 64;

            if (jset.cardinality() == 64 ){
                jset.clear();
            }
            start = System.nanoTime();
            if (!jset.get((int)index)) {
                jset.set((int) index);
            }
            duration = duration + System.nanoTime() - start;
        }
        long[] r = jset.toLongArray();
        System.out.println(jset.length());
        System.out.println("Test Jset: "+ duration/100.0/iters + ", res: " + r[0]);
        bitset = 0;
        jset.clear();

        iters = 1000;
        duration = 0;
        start = System.nanoTime();
        res = myOperation(bitset, 0, iters);
        duration = duration + System.nanoTime() - start;
        System.out.println("Test myoperation: "+ duration/100.0/iters + ", res: " + res);

        duration = 0;
        start = System.nanoTime();
        res = jOperation(jset, 0, iters);
        duration = duration + System.nanoTime() - start;
        System.out.println("Test joperation: "+ duration/100.0/iters + ", res: " + res);

        duration = 0;
        start = System.nanoTime();
        res = setOperation(set, 0, iters);
        duration = duration + System.nanoTime() - start;
        System.out.println("Test setOperation: "+ duration/100.0/iters + ", res: " + res);





    }

}

class MyBitSet{
    public static long set(long bitset, long index){
        return bitset | 1L<<index;
    }

    public static long clear(long bitset, long index){
        return  bitset & ~(1L<<index);
    }

    public static boolean get(long bitset, long index){
        return ((bitset >>> index) & 0x1L) == 1L;
    }
}



