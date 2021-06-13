package iftm.identityfunction;

import iftm.identityfunction.streaminghs.TreeOrchestrator;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author fschmidt
 */
public class StreamingHSTreesIdentityFunction implements IdentityFunction {

    private TreeOrchestrator model;
    private final int nrOfTrees;
    private final int maxDepth;
    private final int windowSize;
    private int nrOfDimensions;
    private double[] minArray;
    private double[] maxArray;
    private int sizeLimit;

    public StreamingHSTreesIdentityFunction(int nrOfTrees, int maxDepth, int windowSize) {
        this.nrOfTrees = nrOfTrees;
        this.maxDepth = maxDepth;
        this.windowSize = windowSize;
        this.model = null;
    }

    public StreamingHSTreesIdentityFunction(int nrOfTrees, int maxDepth, int windowSize, int nrOfDimensions, double[] minArray,
            double[] maxArray, int sizeLimit) {
        this.nrOfTrees = nrOfTrees;
        this.maxDepth = maxDepth;
        this.windowSize = windowSize;
        this.nrOfDimensions = nrOfDimensions;
        this.minArray = minArray;
        this.maxArray = maxArray;
        this.sizeLimit = sizeLimit;
        this.model = new TreeOrchestrator(nrOfTrees, maxDepth, windowSize, nrOfDimensions, minArray, maxArray, sizeLimit);
    }

    @Override
    public double[] predict(double[] values) {
        if (model == null) {
            init(values);
            //or send back a null vector
        }
        double anomalyScore = model.predictSample(values);
        double[] result = new double[values.length];
        result[0] = anomalyScore;
        return result;
    }

    @Override
    public void train(double[] values) {
        if (model == null) {
            init(values);
        }
        checkForNewBorders(values);
        model.insertSample(values);
    }

    private void init(double[] point) {
        nrOfDimensions = point.length;
        minArray = point.clone();
        maxArray = point.clone();
        model = new TreeOrchestrator(nrOfTrees, maxDepth, windowSize, nrOfDimensions, minArray, maxArray, sizeLimit);
    }

    private void checkForNewBorders(double[] point) {
        boolean changedModel = false;
        for (int i = 0; i < nrOfDimensions; i++) {
            if (minArray[i] > point[i]) {
                minArray[i] = point[i];
                changedModel = true;
            }

            if (maxArray[i] < point[i]) {
                maxArray[i] = point[i];
                changedModel = true;
            }
        }
        if (changedModel) {
            model = new TreeOrchestrator(nrOfTrees, maxDepth, windowSize, nrOfDimensions, minArray, maxArray, sizeLimit);
        }
    }

    @Override
    public IdentityFunction newInstance() {
        return new StreamingHSTreesIdentityFunction(nrOfTrees, maxDepth, windowSize, nrOfDimensions, minArray, maxArray, sizeLimit);
    }

    public int getNrOfTrees() {
        return nrOfTrees;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public int getWindowSize() {
        return windowSize;
    }

    @Override
    public Map<String, Double> getStatistics() {
        Map<String, Double> stats = new HashMap<>();
        stats.put("identityfunction.hyperparameter.nrOfTrees", (double) nrOfTrees);
        stats.put("identityfunction.hyperparameter.maxDepth", (double) maxDepth);
        stats.put("identityfunction.hyperparameter.windowSize", (double) windowSize);
        return stats;
    }
}
