package iftm.identityfunction;

import java.io.Serializable;
import java.util.Map;

/**
 *
 * @author fschmidt
 */
public interface IdentityFunction extends Serializable {

    double[] predict(double[] values);

    void train(double[] values);

    IdentityFunction newInstance();

    Map<String, Double> getStatistics();
}
