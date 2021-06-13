package iftm.identityfunction;

import iftm.identityfunction.onlinearima.AbstractModelArima;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractOnlineArimaIdentityFunction extends AbstractArimaIdentityFunction {

    protected List<AbstractModelArima> models;
    private int modelCount;
    private final double initialLearnRate;
    private final int numSamplesLearningRateAdaption;
    protected double[] gammaValues = null;

    public AbstractOnlineArimaIdentityFunction(int mk, int d) {
        this(mk, d, 1.0, 1);
    }

    public AbstractOnlineArimaIdentityFunction(int mk, int d, double initialLearnRate, int numSamplesLearningRateAdaption) {
        super(mk, d);
        this.initialLearnRate = initialLearnRate;
        this.numSamplesLearningRateAdaption = numSamplesLearningRateAdaption;
    }

    @Override
    public abstract IdentityFunction newInstance();

    public abstract AbstractModelArima newModel();

    /**
     * make a prediction for each metric
     *
     * @param values
     * @return
     */
    @Override
    public double[] predict(double[] values) {
        if (models == null) {
            models = new ArrayList<>();
            modelCount = values.length;
            for (int i = 0; i < modelCount; i++) {
                models.add(newModel());
            }
        }
        double[] predictions = new double[modelCount];
        for (int i = 0; i < modelCount; i++) {
            predictions[i] = (double) models.get(i).predict();
        }
        return predictions;
    }

    @Override
    public void train(double[] values) {
        train(values, true);
    }

    public void train(double[] values, boolean updateModel){
        if (models == null) {
            models = new ArrayList<>();
            modelCount = values.length;
            for (int i = 0; i < modelCount; i++) {
                models.add(newModel());
            }
        }

        for (int i = 0; i < modelCount; i++) {
            models.get(i).train(values[i], updateModel);
        }
    }

    public AbstractModelArima getModels(){
        if(models.size() > 0 ) {
            return models.get(0);
        }
        else{
            return null;
        }
    }

    public double getInitialLearnRate() {
        return initialLearnRate;
    }

    public int getNumSamplesLearningRateAdaption() {
        return numSamplesLearningRateAdaption;
    }

    public void setInitialGamma(double[] gammaValues){
        this.gammaValues = gammaValues;
    }

    @Override
    public Map<String, Double> getStatistics() {
        Map<String, Double> stats = super.getStatistics();
        stats.put("identityfunction.hyperparameter.initialLearnRate", initialLearnRate);
        stats.put("identityfunction.hyperparameter.numSamplesLearningRateAdaption", (double) numSamplesLearningRateAdaption);
        return stats;
    }
}
