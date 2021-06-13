package iftm.identityfunction.onlinearima.learningoptimizer;

import Jama.Matrix;
import iftm.identityfunction.onlinearima.LearningRateAdaptizer;

/**
 * @author kevinstyp
 * With inspiration from:
 * https://ruder.io/optimizing-gradient-descent/index.html#gradientdescentoptimizationalgorithms
 * https://wiseodd.github.io/techblog/2016/06/22/nn-optimization/
 */
public class RmspropLearningRateOptimizer extends AbstractLearningRateOptimizer {

    private Matrix adaGradCacheK;

    final private double gamma;// = 0.3;
    final private double epsilon;// = 1e-8;

    public RmspropLearningRateOptimizer(LearningRateAdaptizer learningRateAdaptizer, double... args) {
        super(learningRateAdaptizer);
        gamma = args[0];
        epsilon = args[1];
    }

    @Override
    public Matrix getGradiantLoss(Matrix gl, Matrix lastGradientLoss) {
        double learnRate = learningRateAdaptizer.getBaseRate();
        Matrix currentLoss = new Matrix(gl.getRowDimension(), gl.getColumnDimension(), 0.0);

        if(adaGradCacheK == null){
            //Fill if empty
            adaGradCacheK = new Matrix(gl.getRowDimension(), gl.getColumnDimension(), 0.0);
        }

        for (int i = 0; i < gl.getRowDimension(); i++) {
            double squareSum = ( (gamma) * adaGradCacheK.get(i, 0) ) + ( (1 - gamma) * Math.pow(gl.get(i, 0), 2) );
            adaGradCacheK.set(i, 0, squareSum);
            currentLoss.set(i, 0, learnRate * gl.get(i, 0) / (Math.sqrt(squareSum) + epsilon));
        }
        return currentLoss;
    }
}
