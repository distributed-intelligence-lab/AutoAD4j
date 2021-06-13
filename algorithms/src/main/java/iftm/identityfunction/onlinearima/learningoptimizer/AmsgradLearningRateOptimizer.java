package iftm.identityfunction.onlinearima.learningoptimizer;

import Jama.Matrix;
import iftm.identityfunction.onlinearima.LearningRateAdaptizer;

/**
 * @author kevinstyp
 * With inspiration from:
 * https://ruder.io/optimizing-gradient-descent/index.html#gradientdescentoptimizationalgorithms
 * https://wiseodd.github.io/techblog/2016/06/22/nn-optimization/
 */
public class AmsgradLearningRateOptimizer extends AbstractLearningRateOptimizer {

    private double[] adamM;
    private double[] adamR;
    private double[] adamRprev;
    private int adamCounter = 1;

    final private double ams_beta1;// = 0.4;
    final private double ams_beta2;// = 0.4;
    final private double epsilon;// = 1e-8;

    public AmsgradLearningRateOptimizer(LearningRateAdaptizer learningRateAdaptizer, double... args) {
        super(learningRateAdaptizer);
        ams_beta1 = args[0];
        ams_beta2 = args[1];
        epsilon = args[2];
    }

    @Override
    public Matrix getGradiantLoss(Matrix gl, Matrix lastGradientLoss) {
        double learnRate = learningRateAdaptizer.getBaseRate();
        Matrix currentLoss = new Matrix(gl.getRowDimension(), gl.getColumnDimension(), 0.0);

        if (adamM == null || adamR == null) {
            adamM = new double[gl.getRowDimension()];
            adamR = new double[gl.getRowDimension()];
            adamRprev = new double[gl.getRowDimension()];
        }
        for (int i = 0; i < gl.getRowDimension(); i++) {
            adamM[i] = ams_beta1 * adamM[i] + ((1.0 - ams_beta1) * gl.get(i, 0));
            adamR[i] = ams_beta2 * adamR[i] + ((1.0 - ams_beta2) * Math.pow(gl.get(i, 0), 2));

            double m_k_hat = adamM[i] / (1.0 - Math.pow(ams_beta1, adamCounter));
            double r_k_hat = adamR[i] / (1.0 - Math.pow(ams_beta2, adamCounter));

            r_k_hat = Math.max(r_k_hat, adamRprev[i]);
            adamRprev[i] = r_k_hat;

            currentLoss.set(i, 0, learnRate * m_k_hat / (Math.sqrt(r_k_hat) + epsilon));
        }
        return currentLoss;
    }
}
