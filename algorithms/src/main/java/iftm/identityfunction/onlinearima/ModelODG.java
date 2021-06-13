package iftm.identityfunction.onlinearima;

import Jama.Matrix;
import iftm.identityfunction.onlinearima.learningoptimizer.AbstractLearningRateOptimizer;
import iftm.identityfunction.onlinearima.learningoptimizer.LearningRateOptimizers;
import iftm.identityfunction.onlinearima.learningoptimizer.NesterovLearningRateOptimizer;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ModelODG extends AbstractModelArima {

    private AbstractLearningRateOptimizer learningRateOptimizer;
    private Logger logger = Logger.getLogger(ModelODG.class.getName());

    public ModelODG(int mk, int d) {
        this(mk, d, 1, 1, LearningRateOptimizers.SIMPLE);
    }

    public ModelODG(int mk, int d, double initialLearnRate, int numSamplesLearningRateAdaption, LearningRateOptimizers learnMode, double... args) {
        super(d, new ArrayList<>(), mk, initL(mk), initRate(mk), initialLearnRate, numSamplesLearningRateAdaption);
        try {

            this.learningRateOptimizer = learnMode.get(learningRateAdaptizer, args);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            logger.log(Level.SEVERE, "Failed to instantiate LearningRateOptimizer class.", e);
        }
    }

    /*Matrix miniBatch = new Matrix(getMk(), 1, 0.0);
    private int miniBatchCounter = 0;
    private int miniBatchSize = 10;*/
    @Override
    void updateModel(double prediction, double realData) {
        if(learningRateOptimizer instanceof NesterovLearningRateOptimizer){
            //Special case for nesterov, which updates parameters before gradient loss calculation
            double fractionGamma = 0.5;
            setLm(getLm().plus(lastGradientLoss.times(fractionGamma)));
        }

        double newPrediction = (double) predict();

        Matrix gl = gradiantLoss(newPrediction, realData);
        Matrix midres = learningRateOptimizer.getGradiantLoss(gl, lastGradientLoss);

        // TODO: Mini-Batch-Mode
        /*miniBatch = miniBatch.plus(midres);
        if(miniBatchCounter % miniBatchSize == 0) {
            miniBatch.times(1.0 / miniBatchSize);
            printMatrix(miniBatch.transpose(), "miniBatch");
            // Update coefficients here
            setLm(getLm().plus(miniBatch));
            miniBatch = new Matrix(getMk(), 1, 0.0);
        }
        miniBatchCounter++;*/
        lastGradientLoss = midres;
        setLm(getLm().plus(midres));
    }

    public void setMode(LearningRateOptimizers learnMode, double... args) {try {
        this.learningRateOptimizer = learnMode.get(learningRateAdaptizer, args);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            logger.log(Level.SEVERE, "Failed to instantiate LearningRateOptimizer class on change.", e);
        }
    }

    public AbstractLearningRateOptimizer getLearningRateOptimizer() {
        return learningRateOptimizer;
    }

    public void setLearningRate(double learningRate){
        this.learningRateAdaptizer.setRate(learningRate);
    }
}
