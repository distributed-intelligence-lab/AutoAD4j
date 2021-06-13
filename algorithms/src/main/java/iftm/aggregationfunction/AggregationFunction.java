package iftm.aggregationfunction;

import iftm.anomalydetection.DistancePredictionResult;
import java.io.Serializable;
import java.util.List;

/**
 * This class can be used either to
 *
 * @author fschmidt
 */
public interface AggregationFunction extends Serializable {

    DistancePredictionResult predict(List<DistancePredictionResult> results);
}
