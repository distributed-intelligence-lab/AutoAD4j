package iftm.aggregationfunction;

import iftm.anomalydetection.DistancePredictionResult;
import java.util.List;
import java.util.Set;

/**
 *
 * @author fschmidt
 */
public abstract class MetricGroupAggregationFunction implements AggregationFunction {

    private final Set<Set<Integer>> metricGroups;

    public MetricGroupAggregationFunction(Set<Set<Integer>> metricGroups) {
        this.metricGroups = metricGroups;
    }

    public Set<Set<Integer>> getMetricGroups() {
        return metricGroups;
    }

    @Override
    public DistancePredictionResult predict(List<DistancePredictionResult> results) {
        return metricGroupPrediction(results);
    }

    protected abstract DistancePredictionResult metricGroupPrediction(List<DistancePredictionResult> results);

}
