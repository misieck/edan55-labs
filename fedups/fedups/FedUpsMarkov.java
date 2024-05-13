
import java.io.*;
import java.util.*;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class FedUpsMarkov {
    public static void main(String args[]) throws IOException {


        DeliveryGraph G = new DeliveryGraph(args[0]);
        double[][]A = G.A;
        double[] b = G.b;
        int f = G.fed;
        int h = G.home;
        int p = G.post;
        int n = b.length;

        HashSet<Integer> reachable = findReachable(A, h);
        var reach = canReach(A,h);
        //System.out.println(Arrays.toString(reach));
        System.out.println(reach);
        System.out.println(reachable);
        for (int x = 0; x < n; x++) {
            if (!reachable.contains(x)) {
                for (int y = 0; y < n; y++) A[x][y] = 0.0;
            }
        }

        for (int x = 0; x < n; x++) A[x][x] -= 1;
        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) System.out.print(A[x][y] + " ");
            System.out.println();
        }
        for (int x = 0; x < n; x++) System.out.println(b[x]);
        double[] sol = Gaussian.solve(A, b);

        if (reachable.contains(f)) System.out.println("FedUps time: " + sol[f]);
        else System.out.println("FedUps time: Unreachable");
        if (reachable.contains(p)) System.out.println("PostNHL time: " + sol[p]);
        else System.out.println("PostNHL time: Unreachable");

	}

    public static double[][] transpose(double [][]matrix){
        int m = matrix.length;
        int n = matrix[0].length;
        double[][] res = new double[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = i + 1; j < n; j++) {
                res[i][j] = matrix[j][i];
                res[j][i] = matrix[i][j];;
            }
        }
        return res;
    }

    public static Collection<Integer> canReach(double [][] A, int home ) {
        HashSet<Integer> res = new HashSet<>(Arrays.asList(home));
        Stack<Integer> toAdd = new Stack<>();  toAdd.push(home);
        double [][] _A = transpose(A);
        IntPredicate resContains = res::contains;
        while(!toAdd.isEmpty()){
            double row[] = _A[toAdd.pop()];
            IntStream.range(0, row.length)
                    .filter(rowidx -> row[rowidx]!=0)
                    .filter( resContains.negate() )
                    .peek(toAdd::push)
                    .forEach(res::add);
        }
        return res;
    }

    public static HashSet<Integer> findReachable(double[][] A, int added) {
        return findReachable(A, new HashSet<>(),added);
    }

    public static HashSet<Integer> findReachable(double[][] A, HashSet<Integer> reachable, int added) {
        if (reachable.contains(added)) return reachable;
        reachable.add(added);
        for (int x = 0; x < A.length; x++) {
            if (A[x][added] != 0.0) findReachable(A, reachable, x);
        }
        return reachable;
    }
}