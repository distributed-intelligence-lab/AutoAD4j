package iftm.identityfunction.onlinearima.learningoptimizer;

import Jama.Matrix;
import iftm.identityfunction.onlinearima.LearningRateAdaptizer;

/**
 * @author kevinstyp
 * With inspiration from:
 * https://ruder.io/optimizing-gradient-descent/index.html#gradientdescentoptimizationalgorithms
 * https://wiseodd.github.io/techblog/2016/06/22/nn-optimization/
 */
public class NesterovLearningRateOptimizer extends AbstractLearningRateOptimizer {

    public NesterovLearningRateOptimizer(LearningRateAdaptizer learningRateAdaptizer, double... args) {
        super(learningRateAdaptizer);
    }

    @Override
    public Matrix getGradiantLoss(Matrix gl, Matrix lastGradientLoss) {
        double learnRate = learningRateAdaptizer.getBaseRate();
        return gl.times(learnRate);
    }
}
