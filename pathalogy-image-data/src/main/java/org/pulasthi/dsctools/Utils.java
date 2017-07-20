package org.pulasthi.dsctools;

public class Utils {
    public static void printAndThrowRuntimeException(RuntimeException e) {
        e.printStackTrace(System.out);
        throw e;
    }

    public static void printAndThrowRuntimeException(String message) {
        System.out.println(message);
        throw new RuntimeException(message);
    }

    public static void printMessage(String msg) {
        if (ParallelOps.worldProcRank != 0) {
            return;
        }
        System.out.println(msg);
    }
    public static double calculateEuclideanDistance(double x[], double y[], int dimension){
        double sum = 0;
        for (int i = 0; i < dimension; i++) {
            sum += (x[i]-y[i])*(x[i]-y[i]);
        }

        return Math.sqrt(sum);
    }
}
