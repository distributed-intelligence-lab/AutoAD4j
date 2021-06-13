package iftm.identityfunction.onlinearima.learningoptimizer;

import Jama.Matrix;
import iftm.identityfunction.onlinearima.LearningRateAdaptizer;

/**
 * @author kevinstyp
 * With inspiration from:
 * https://ruder.io/optimizing-gradient-descent/index.html#gradientdescentoptimizationalgorithms
 * https://wiseodd.github.io/techblog/2016/06/22/nn-optimization/
 */
public class AdamLearningRateOptimizer extends AbstractLearningRateOptimizer {

    private double[] adamM;
    private double[] adamR;
    private int adamCounter = 1;

    final private double beta1;// = 0.4;
    final private double beta2;// = 0.4;
    final private double epsilon;// = 1e-8;

    public AdamLearningRateOptimizer(LearningRateAdaptizer learningRateAdaptizer, double... args) {
        super(learningRateAdaptizer);
        beta1 = args[0];
        beta2 = args[1];
        epsilon = args[2];
    }

    @Override
    public Matrix getGradiantLoss(Matrix gl, Matrix lastGradientLoss) {
        double learnRate = learningRateAdaptizer.getBaseRate();
        Matrix currentLoss = new Matrix(gl.getRowDimension(), gl.getColumnDimension(), 0.0);

        if(adamM == null || adamR == null){
            adamM = new double[gl.getRowDimension()];
            adamR = new double[gl.getRowDimension()];
        }
        for (int i = 0; i < gl.getRowDimension(); i++) {
            adamM[i] = beta1 * adamM[i] + ((1.0 - beta1) * gl.get(i, 0));
            adamR[i] = beta2 * adamR[i] + ((1.0 - beta2) * Math.pow(gl.get(i,0), 2));

            double m_k_hat = adamM[i] / (1.0 - Math.pow(beta1, adamCounter));
            double r_k_hat = adamR[i] / (1.0 - Math.pow(beta2, adamCounter));

            currentLoss.set(i, 0, learnRate * m_k_hat / (Math.sqrt(r_k_hat) + epsilon));
        }
        return currentLoss;
    }
}
