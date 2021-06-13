package iftm.aggregationfunction;

import iftm.anomalydetection.DistancePredictionResult;
import java.util.List;
import java.util.Set;

/**
 *
 * @author fschmidt
 */
public class AnomalyMetricGroupAggregation extends MetricGroupAggregationFunction {

    private final int atLeastNumPerGroup;

    public AnomalyMetricGroupAggregation(Set<Set<Integer>> metricGroups, int atLeastNumPerGroup) {
        super(metricGroups);
        this.atLeastNumPerGroup = atLeastNumPerGroup;
    }

    @Override
    protected DistancePredictionResult metricGroupPrediction(List<DistancePredictionResult> results) {
        boolean isAnomaly = false;
        double error = 0;
        double threshold = 0;
        double[] distance = new double[0];
        boolean firstResult = true;
        for (Set<Integer> group : getMetricGroups()) {
            int countAnomalies = 0;
            for (int i : group) {
                if (results.get(i).isAnomaly()) {
                    countAnomalies++;
                }
            }
            if (countAnomalies >= atLeastNumPerGroup) {
                isAnomaly = true;
            }
        }
        for(DistancePredictionResult r : results){
            if(firstResult){
                firstResult=false;
                distance = new double[r.getDistance().length];
            }
            error += r.getError();
            threshold += r.getThreshold();
            for(int i = 0; i < r.getDistance().length;i++){
                distance[i] += r.getDistance()[i];
            }
        }
        return new DistancePredictionResult(isAnomaly, error, distance, threshold);
    }

}
