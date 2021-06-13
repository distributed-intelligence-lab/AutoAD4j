package iftm.aggregationfunction;

import iftm.anomalydetection.DistancePredictionResult;
import java.util.List;

/**
 *
 * @author fschmidt
 */
public class AtLeastOneAnomalyAggregation implements AggregationFunction {

    @Override
    public DistancePredictionResult predict(List<DistancePredictionResult> results) {
        boolean isAnomaly = false;
        double error = 0;
        double threshold = 0;
        boolean firstR = true;
        double[] distances = new double[0];
        for (DistancePredictionResult r : results) {
            if(firstR){
                distances = new double[r.getDistance().length];
                firstR=false;
            }
            if (r.isAnomaly()) {
                isAnomaly = true;
            }
            error += r.getError();
            threshold += r.getThreshold();
            for(int i = 0; i < r.getDistance().length;i++){
                distances[i] = r.getDistance()[i];
            }
        }
        return new DistancePredictionResult(isAnomaly,error,distances,threshold);
    }

}
