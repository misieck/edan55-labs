/**
** Graciously borrowed from https://www.sanfoundry.com/java-program-gaussian-elimination-algorithm/
**/

import java.util.Scanner;

public class Gaussian
{
    public static double[] solve(double[][] A, double[] B) {
        int N = B.length;
        for (int k = 0; k < N; k++) {
            /** find pivot row **/
            int max = k;
            for (int i = k + 1; i < N; i++) 
                if (Math.abs(A[i][k]) > Math.abs(A[max][k])) 
                    max = i;
            /** swap row in A matrix **/    
            double[] temp = A[k]; 
            A[k] = A[max]; 
            A[max] = temp;
            /** swap corresponding values in constants matrix **/
            double t = B[k]; 
            B[k] = B[max]; 
            B[max] = t;
            /** pivot within A and B **/
            for (int i = k + 1; i < N; i++) {
                double factor = A[i][k] / A[k][k];
                B[i] -= factor * B[k];
                for (int j = k; j < N; j++) 
                    A[i][j] -= factor * A[k][j];
            }
        }
        //printRowEchelonForm(A, B);

        /** back substitution **/
        double[] solution = new double[N];
        for (int i = N - 1; i >= 0; i--) {
            double sum = 0.0;
            for (int j = i + 1; j < N; j++) 
                sum += A[i][j] * solution[j];
            solution[i] = (B[i] - sum) / A[i][i];
        }
        return solution;
    }

    public static void printRowEchelonForm(double[][] A, double[] B) {
        int N = B.length;
        System.out.println("\nRow Echelon form : ");
        for (int i = 0; i < N; i++)
        {
            for (int j = 0; j < N; j++)
                System.out.printf("%.3f ", A[i][j]);
            System.out.printf("| %.3f\n", B[i]);
        }
        System.out.println();
    }

    public static void printSolution(double[] sol) {
        int N = sol.length;
        System.out.println("\nSolution : ");
        for (int i = 0; i < N; i++) 
            System.out.printf("%.3f ", sol[i]);   
        System.out.println();
    }

    public static void main (String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.println("Gaussian Elimination Algorithm Test\n");
        int N = 3;
        double[] B = {3, 99, 5};
        double[][] A = {{4, -7,  3},
                       { 6,  5, -1},
                       { 0, -2,  0}};

        double [] res = solve(A,B);
        printSolution(res);
    }
}