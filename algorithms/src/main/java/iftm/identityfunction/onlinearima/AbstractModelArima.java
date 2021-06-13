package iftm.identityfunction.onlinearima;

import Jama.Matrix;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ARIMA(k, d, q) domain approximated with another ARIMA(k + m, d, 0), which is the Online Arima domain
 */
public abstract class AbstractModelArima implements Serializable {
    static final Logger LOG = Logger.getLogger(AbstractModelArima.class.getName());

    private final int d;
    private final List<double[]> grads;
    private final int mk;
    private Matrix lm;
    final LearningRateAdaptizer learningRateAdaptizer;
    Matrix lastGradientLoss;

    AbstractModelArima(int d, List<double[]> grads, int mk, double[][] l, double rate) {
        this(d, grads, mk, l, rate, 1, 1);
    }

    AbstractModelArima(int d, List<double[]> grads, int mk, double[][] l, double rate, double initialLearnRate, int numSamplesLearningRateAdaption) {
        this.d = d;
        this.grads = grads;
        this.mk = mk;
        this.lm = Matrix.constructWithCopy(l);
        learningRateAdaptizer = new LearningRateAdaptizer(rate, initialLearnRate, numSamplesLearningRateAdaption);
    }

    protected static double initRate(int mk) {
        double d = Math.sqrt(2.0 * mk);
        double g = 2 * Math.sqrt(mk) * d;
        return 0.5 * Math.min(1.0 / (mk), 4 * g * d);
    }

    public void train(double newVal) {
        train(newVal, true);
    }

    public void train(double newVal, boolean updateModel) {
        if (updateModel) {
            double prediction = (double) predict();
            updateModel(prediction, newVal);
        } else {
            // Fill last gradient with empty (value = 0) vector of gradients size
            lastGradientLoss = new Matrix(getMk(), 1, 0.0);
        }
        addGrad(newVal);
    }

    abstract void updateModel(double prediction, double realData);

    void addGrad(double newVal) {
        double[] newGrad = new double[d + 1];
        newGrad[0] = newVal;
        int currGradLength = grads.size();
        if (currGradLength > 0) {
            for (int g = 1; g < d + 1; g++) {
                newGrad[g] = newGrad[g - 1] - grads.get(currGradLength - 1)[g - 1];
            }
        }
        if (currGradLength == mk) {
            grads.remove(0);
        }
        grads.add(newGrad);
    }

    public Object predict() {
        double xT = 0.0;
        for (int i = 0; i < mk; i++) {
            if (grads.size() - i - 1 < 0) {
                break;
            }
            xT += lm.get(i, 0) * grads.get(grads.size() - i - 1)[d];
        }
        for (int i = 0; i < d; i++) {
            if (grads.size() - 1 < 0) {
                break;
            } else {
                xT += grads.get(grads.size() - 1)[i];
            }
        }
        return xT;
    }

    Matrix gradiantLoss(double prediction, double realData) {
        double[][] gradLoss = new double[mk][1];
        for (int i = 0; i < mk; i++) {
            double x = 0;
            if (grads.size() - i - 1 >= 0) {
                x = grads.get(grads.size() - i - 1)[d];
            }
            gradLoss[i][0] = -1 * (realData - prediction) * x; //Was -2
        }
        return Matrix.constructWithCopy(gradLoss);
    }

    public int getD() {
        return d;
    }

    Matrix getLm() {
        return lm;
    }

    double getRate() {
        return learningRateAdaptizer.getRate();
    }

    double getBaseRate() {
        return learningRateAdaptizer.getBaseRate();
    }

    void setLm(Matrix lm) {
        this.lm = lm;
    }

    int getMk() {
        return mk;
    }

    public Matrix getGamma() {
        return getLm();
    }

    static double[][] initL(int mk) {
        double[][] l = new double[mk][1];
        //mean 1, d1,
        for (int i = 0; i < mk; i++) {
            l[i][0] = Math.random() - 0.5;
        }
        return l;
    }

    public void setGamma(double[] paramArray) {
        Matrix lm = getLm();
        for (int i = 0; i < paramArray.length; i++) {
            lm.set(i, 0, paramArray[i]);
        }
        setLm(lm);
    }

    public void printMatrix(Matrix m, String name) {
        //i - Row index; j - Column index.
        //1 column, 'mk' rows
        for (int i = 0; i < m.getRowDimension(); i++) {
            String logMessage = "";
            for (int j = 0; j < m.getColumnDimension(); j++) {
                logMessage += name + " " + BigDecimal.valueOf(m.get(i, j)).toPlainString() + ", ";
            }
            logMessage += "\n";
            LOG.log(Level.INFO, logMessage);
        }
    }

    public LearningRateAdaptizer getLearningRateAdaptizer() {
        return learningRateAdaptizer;
    }
}
