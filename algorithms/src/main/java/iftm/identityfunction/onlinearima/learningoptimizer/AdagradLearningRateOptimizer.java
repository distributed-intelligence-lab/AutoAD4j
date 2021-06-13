package iftm.identityfunction.onlinearima.learningoptimizer;

import Jama.Matrix;
import iftm.identityfunction.onlinearima.LearningRateAdaptizer;

/**
 * @author kevinstyp
 * With inspiration from:
 * https://ruder.io/optimizing-gradient-descent/index.html#gradientdescentoptimizationalgorithms
 * https://wiseodd.github.io/techblog/2016/06/22/nn-optimization/
 */
public class AdagradLearningRateOptimizer extends AbstractLearningRateOptimizer {

    private Matrix adaGradCacheK;
    final private double epsilon;// = 1e-8;

    public AdagradLearningRateOptimizer(LearningRateAdaptizer learningRateAdaptizer, double... args) {
        super(learningRateAdaptizer);
        epsilon = args[0];
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
            double squareSum = adaGradCacheK.get(i, 0) + Math.pow(gl.get(i, 0), 2);
            adaGradCacheK.set(i, 0, squareSum);
            currentLoss.set(i, 0, learnRate * gl.get(i, 0) / (Math.sqrt(squareSum) + epsilon));
        }
        return currentLoss;
    }
}
