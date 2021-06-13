package iftm.thresholdmodel;

import java.io.Serializable;
import java.util.Map;

/**
 *
 * @author fschmidt
 */
public interface ThresholdModel extends Serializable{
    
    void addValue(double value);
    
    boolean isIncluded(double error);

    ThresholdModel newInstance();

    Map<String, Double> getStatistics();
}
