package iftm.identityfunction.cabirch.utils;

import static java.lang.Math.*;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author fschmidt
 */
public class VectorUtil {

    static final Logger LOG = Logger.getLogger(VectorUtil.class.getName());

    private VectorUtil(){}

    public static double[] sumVec(double[] v1, double[] v2) {
        double[] result = new double[v1.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = v1[i] + v2[i];
        }
        return result;
    }

    public static double[] subVec(double[] v1, double[] v2) {
        double[] result = new double[v1.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = v1[i] - v2[i];
        }
        return result;
    }

    public static double[] multVec(double[] v1, double[] v2) {
        double[] result = new double[v1.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = v1[i] * v2[i];
        }
        return result;
    }

    public static double[] multVec(double[] v1, double d) {
        double[] result = new double[v1.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = v1[i] * d;
        }
        return result;
    }

    public static double[] divVec(double[] v1, double[] v2) {
        double[] result = new double[v1.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = v1[i] / v2[i];
        }
        return result;
    }

    public static double[] divVec(double[] v1, double d) {
        double[] result = new double[v1.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = v1[i] / d;
        }
        return result;
    }

    public static double[] sqrtVec(double[] v) {
        double[] result = new double[v.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = sqrt(v[i]);
        }
        return result;
    }

    public static double lengthVec(double[] v) {
        double result = 0.0;
        for (double aV : v) {
            if (Double.isNaN((aV))) {
                LOG.log(Level.FINE, "Input vector has NaN value. {0}", Arrays.toString(v));
            }
            result += pow(aV, 2);
        }
        if (Double.isNaN((result))) {
            LOG.log(Level.FINE, "Result value is NaN. {0}", result);
        }
        return sqrt(result);
    }

    public static double[] absVec(double[] v) {
        double[] result = new double[v.length];
        for (int i = 0; i < v.length; i++) {
            result[i] = abs(v[i]);
        }
        return result;
    }

}
