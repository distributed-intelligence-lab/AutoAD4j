package iftm.identityfunction.cabirch.utils;

/**
 *
 * @author fschmidt
 */
public class DistanceUtil {

    private DistanceUtil(){}

    public static double euclidean(double[] v1, double[] v2) {
        double result = 0;
        for (int i = 0; i < v1.length; i++) {
            result += ((v1[i] - v2[i])*(v1[i] - v2[i]));
        }
        return Math.sqrt(result);
    }
}
