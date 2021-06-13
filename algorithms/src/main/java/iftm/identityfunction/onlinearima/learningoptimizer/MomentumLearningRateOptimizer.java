package iftm.identityfunction.onlinearima.learningoptimizer;

import Jama.Matrix;
import iftm.identityfunction.onlinearima.LearningRateAdaptizer;

/**
 * @author kevinstyp
 * With inspiration from:
 * https://ruder.io/optimizing-gradient-descent/index.html#gradientdescentoptimizationalgorithms
 * https://wiseodd.github.io/techblog/2016/06/22/nn-optimization/
 */
public class MomentumLearningRateOptimizer extends AbstractLearningRateOptimizer {

    final private double fractionGamma;// = 0.5;

    public MomentumLearningRateOptimizer(LearningRateAdaptizer learningRateAdaptizer, double... args) {
        super(learningRateAdaptizer);
        fractionGamma = args[0];
    }

    @Override
    public Matrix getGradiantLoss(Matrix gl, Matrix lastGradientLoss) {
        double learnRate = learningRateAdaptizer.getBaseRate();

        if (lastGradientLoss == null) {
            //Fallback for first empty last-gradient: Empty(values = 0) Matrix of same size
            lastGradientLoss = new Matrix(gl.getRowDimension(), gl.getColumnDimension(), 0.0);
        }

        //Momentum implementation
        return lastGradientLoss.times(fractionGamma).plus(gl.times(learnRate));
    }
}
