package iftm.identityfunction.onlinearima.learningoptimizer;

import Jama.Matrix;
import iftm.identityfunction.onlinearima.LearningRateAdaptizer;

/**
 * @author kevinstyp
 * Vanilla SGD
 */
public class SimpleLearningRateOptimizer extends AbstractLearningRateOptimizer {

    public SimpleLearningRateOptimizer(LearningRateAdaptizer learningRateAdaptizer, double... args) {
        super(learningRateAdaptizer);
    }

    @Override
    public Matrix getGradiantLoss(Matrix gl, Matrix lastGradientLoss) {
        double learnRate = learningRateAdaptizer.getBaseRate();
        return gl.times(learnRate);
    }
}
