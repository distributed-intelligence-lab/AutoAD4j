package iftm.identityfunction.onlinearima.learningoptimizer;

import Jama.Matrix;
import iftm.identityfunction.onlinearima.LearningRateAdaptizer;

import java.io.Serializable;

/**
 * @author kevinstyp
 */
public abstract class AbstractLearningRateOptimizer implements Serializable {
    protected LearningRateAdaptizer learningRateAdaptizer;

    public AbstractLearningRateOptimizer(LearningRateAdaptizer learningRateAdaptizer) {
        this.learningRateAdaptizer = learningRateAdaptizer;
    }

    public abstract Matrix getGradiantLoss(Matrix gl, Matrix lastGradientLoss);
}
