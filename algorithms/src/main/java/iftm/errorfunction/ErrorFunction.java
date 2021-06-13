package iftm.errorfunction;

import java.io.Serializable;

/**
 * @author fschmidt
 */
public interface ErrorFunction extends Serializable {

    double calc(double[] actual, double[] prediction);
}
